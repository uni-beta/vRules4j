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
package com.unibeta.vrules.engines.bshimpls;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.tools.Java2vRules;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>ValidationEngineImpl</code> is the first implement for
 * <code>ValidationEngine</code>. Using <code>ValidationEngineFactory</code>
 * get the instance is recommended.
 * 
 * @author Jordan.xue
 */
public class ValidationEngineBshImpl implements ValidationEngine {

    /**
     * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
     */
    public String[] validate(Object object, String fileName) throws Exception {

        String id = CommonUtils.getClassSimpleName(object.getClass());
        return validate(object, fileName, id);
        // return LocalThreadEngineExecutor.execute(object);
    }

    /**
     * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
     */
    public String[] validate(Object object, String fileName, String entityId)
            throws Exception {

        return CorePlainEngine.validate(object, fileName, entityId);
    }

    /**
     * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
     */
    public String[] validate(Object[] objects, String fileName)
            throws Exception {

        if (null == objects) {
            throw new RuntimeException();
        }
        
        File file = new File(fileName);
        if (!file.exists()) {
            Java2vRules.digest(objects, fileName);
        }

        List list = Collections.synchronizedList(new ArrayList());
        for (int i = 0; i < objects.length; i++) {
            CommonUtils.copyArrayToList(validate(objects[i], fileName), list);
        }

        return (String[]) list.toArray(new String[] {});
    }

    public List validate(Object[] objects, String fileName, Object errorObj) throws Exception {

        if(true){
            throw new Exception("Plain engine can not supprot this function at present! please use Dynamic engine to do what you want to.");
        }
        
        return null;
    }

    public List validate(Object object, String fileName, Object errorObj) throws Exception {

        if(true){
            throw new Exception("Plain engine can not supprot this function at present! please use Dynamic engine to do what you want to.");
        }
        
        return null;
    }

    public List validate(Object object, String fileName, String entityId, Object errorObj) throws Exception {

        if(true){
            throw new Exception("Plain engine can not supprot this function at present! please use Dynamic engine to do what you want to.");
        }
        
        return null;
    }

    public String[] validate(Map<String, Object> objMap, String fileName) throws Exception {

        if(true){
            throw new Exception("Plain engine can not supprot this function at present! please use Dynamic engine to do what you want to.");
        }
        return null;
    }

    public List validate(Map<String, Object> objMap, String fileName, Object errorObj) throws Exception {

        if(true){
            throw new Exception("Plain engine can not supprot this function at present! please use Dynamic engine to do what you want to.");
        }
        return null;
    }
}
