/*
 * vRules, copyright (C) 2007-2010 www.uni-beta.com vRules is free software; you
 * can redistribute it and/or modify it under the terms of Version 2.0 Apache
 * License as published by the Free Software Foundation. vRules is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Apache License for more details below or at
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
package com.unibeta.vrules.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;

import com.unibeta.vrules.reflect.ObjectReflector;

/**
 * Common vrules utils methods.
 * 
 * @author jordan.xue
 */
public class VRulesCommonUtils {

    private static final String XML_SCHEMA_DEFAULT_NAME = "##default";
    private static Logger log = Logger.getLogger(VRulesCommonUtils.class);

    /**
     * Builds the java been field's wrapped xml element name map, which is of
     * <filedName,xmlElementName> style.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String, String> buildXmlElementWrappedFieldNameMap(
            Class clazz) throws Exception {

        Field[] fields = ObjectReflector.getAllJavaBeanFields(clazz);

        Map<String, String> xmlElementMap = new HashMap<String, String>();

        for (Field field : fields) {

            Annotation[] annotations = field.getAnnotations();
            String name = field.getName();
            xmlElementMap.put(name,
                    distillXmlElementWrapperName(annotations, name));

            try {
                String generateMethodNmae = ObjectReflector.generateMethodName(name, clazz);
                
                
				if (null != generateMethodNmae) {
					Method method = clazz.getDeclaredMethod(generateMethodNmae,
							null);
					annotations = method.getAnnotations();
					String distillXmlElementWrapperName = distillXmlElementWrapperName(
							annotations, null);
					if (null != distillXmlElementWrapperName) {
						xmlElementMap.put(name, distillXmlElementWrapperName);
					}
				}
            } catch (Exception e) {
                continue;
            }
        }

        return xmlElementMap;
    }

    /**
     * Distill the wrapped name from the annotation.
     * 
     * @param annotations
     * @param defaultName
     *            the default name.
     * @return null if do not find the wrapped name, otherwise, return the
     *         accurate xml element name.
     */
    public static String distillXmlElementWrapperName(Annotation[] annotations,
            String defaultName) {

        String value = null;

        for (Annotation a : annotations) {
            if (a instanceof XmlElementWrapper) {
                XmlElementWrapper wrapper = (XmlElementWrapper) a;
                if (null != wrapper.name()) {
                    value = wrapper.name();
                } else if (null != defaultName && defaultName.length() > 0) {
                    value = defaultName;
                }

                break;
            } else if (a instanceof XmlRootElement) {
                XmlRootElement wrapper = (XmlRootElement) a;
                if (null != wrapper.name()
                        && !XML_SCHEMA_DEFAULT_NAME.equalsIgnoreCase(wrapper
                                .name())) {
                    value = wrapper.name();
                } else if (null != defaultName && defaultName.length() > 0) {
                    value = defaultName;
                }

                break;
            }
        }

        return value;
    }

    /**
     * @param string
     * @return
     */
    public static String generateObjectEntityXmlName(String string) {

        if (null != string && string.length() > 0) {
            String str = string.substring(1);
            String c = string.substring(0, 1).toLowerCase();

            return c + str;
        } else {
            return string;
        }
    }

    public static Class getTargetClass(Object obj) {

        Class clazz = null;

        if (obj != null) {

            try {
                Class.forName("org.springframework.aop.framework.Advised");
                if (Advised.class.isAssignableFrom(obj.getClass())) {
                    Advised advised = (Advised) obj;
                    clazz = advised.getTargetSource().getTargetClass();
                } else {
                    clazz = obj.getClass();

                }
            } catch (ClassNotFoundException e) {
                clazz = obj.getClass();
            }

        }

        return clazz;

    }

}
