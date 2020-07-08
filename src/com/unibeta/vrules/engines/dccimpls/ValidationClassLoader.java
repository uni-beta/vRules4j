/*
 * vRules, copyright (C) 2007-2010 www.uni-beta.com. vRules is free software;
 * you can redistribute it and/or modify it under the terms of Version 2.0
 * Apache License as published by the Free Software Foundation. vRules is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the Apache License for more details below or at
 * http://www.apache.org/licenses/ Licensed to the Apache Software Foundation
 * (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. </pre>
 */
package com.unibeta.vrules.engines.dccimpls;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>ValidationClassLoader</code> is a subclass of <code>ClassLoader</code>.
 * It is responsible for get the validation instance for specifaied rules
 * configuration file.
 * 
 * @author jordan
 */
public class ValidationClassLoader extends URLClassLoader {

    private static Logger log = LoggerFactory.getLogger(ValidationClassLoader.class);

    private static final String COM_UNIBETA_VRULES_ENGINES_DCCIMPLS_RULES = "com.unibeta.vrules.engines.dccimpls.rules.";
    private String fileName = null;
    private Class decisionClass = null;

    private static Map<String,Queue<Object>> instancesPool = Collections
            .synchronizedMap(new HashMap<String,Queue<Object>>());
    
    public ValidationClassLoader(String fileName, Class decisionClass) {

        super(ValidationClassLoader.generateURLs(fileName), Thread
                .currentThread().getContextClassLoader());
        this.fileName = fileName;
        this.decisionClass = decisionClass;
    }

    /**
     * Creates a new validation instance by given rules configuration file name.
     * 
     * @param fileName
     *            the rules configuration fiame name.
     * @return the instance of class
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public Object newValidationInstance() throws ClassNotFoundException {

        Object instance = null;
        String classRUIName = buildClassPath();

        try {
            Class clazzDcc = loadClass(classRUIName, true);

            Object obj = clazzDcc.newInstance();
            instance = obj;

            synchronized (classRUIName) {
            	if(instancesPool.get(getKey(classRUIName)) == null) {
            		instancesPool.put(getKey(classRUIName), new LinkedBlockingQueue());
            	}
            	
            	instancesPool.get(getKey(classRUIName)).clear();
            	instancesPool.get(getKey(classRUIName)).add(instance);
            }
        } catch (ClassNotFoundException e) {
//            log.warn(e.getMessage() + ", try to re-compile target source '"
//                    + this.fileName + "' again in soon.");
            throw e;
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }

        return instancesPool.get(getKey(classRUIName)).poll();
    }

	private String getKey(String classRUIName) {
		return "@"+ConfigurationProxy.buildKeyValue(classRUIName, decisionClass);
	}

    /**
     * Generates the URLs by given vRuels configuration file name.
     * 
     * @param fileName
     *            vRuls configuration file name.
     * @return local APP-Classpath's URLs.
     * @throws MalformedURLException
     */
    private static URL[] generateURLs(String fileName) {

        String classesPath = CommonUtils.getFilePathName(fileName)
                + File.separator + VRulesConstants.DYNAMIC_CLASSES_FOLDER_NAME
                + File.separator;

        File file = new File(classesPath);
        List list = new ArrayList();
        // CommonUtils.copyArrayToList(URLConfiguration.getClasspathURLs(),
        // list);
        try {
            list.add(file.toURL());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        URL[] urls = (URL[]) list.toArray(new URL[] {});
        return urls;
    }

    /**
     * Gets current availabe validation instance directly.
     * 
     * @return
     * @throws Exception
     */
    public Object getValidationInstance() throws ClassNotFoundException {

    	Object instance = null;
    	Queue queue = null;
    	
        queue = instancesPool.get(getKey(buildClassPath()));

        if (null == queue) {
        	instance = newValidationInstance();
        }else {
        	instance = queue.poll();
        	if(instance == null) {
        		instance = newValidationInstance();
        	}
        }
        
        return instance;
    }

    private String buildClassPath() {

        String className = COM_UNIBETA_VRULES_ENGINES_DCCIMPLS_RULES
                + CommonUtils.generateClassNameByFileFullName(fileName);
        return className;
    }
    
    public boolean offerValidationInstance(Object obj) {
    	Queue queue = instancesPool.get(getKey( buildClassPath()));
           
         if (null != queue) {
        	return queue.offer(obj);
         }
         
         return true;
    }
    
    public static Map<String,Queue<Object>> getInstancePool(){
    	return instancesPool;
    }
}
