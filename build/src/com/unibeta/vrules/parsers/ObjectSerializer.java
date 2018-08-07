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
package com.unibeta.vrules.parsers;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import com.thoughtworks.xstream.XStream;

/**
 * </code>ObjectSerializer</code> is a utility tool for serializing the java
 * object to xml or unmarshal xml to java object.f
 * 
 * @author jordan
 */
public class ObjectSerializer {

    private static Map<Class, JAXBContext> jaxbCache = Collections
            .synchronizedMap(new HashMap<Class, JAXBContext>());
    
    private static JAXBContext getJAXBContext(Class clazz) throws Exception{
        
        JAXBContext jaxbContext = jaxbCache.get(clazz);
        
        if(null != jaxbContext){
            return jaxbContext;
        }else{
            jaxbContext = JAXBContext.newInstance(clazz);
            jaxbCache.put(clazz,jaxbContext );
            
            return jaxbContext;
        }
    }

    /**
     * Serializes the object to xml string format via XStream tool.
     * 
     * @param obj
     * @return xml string, otherwise, return the obj.toString() result.
     * @throws Exception
     */
    public static String xStreamToXml(Object obj) {

        if (null == obj) {
            return null;
        }

        if (obj.getClass().isPrimitive() || obj instanceof Integer
                || obj instanceof String || obj instanceof Character
                || obj instanceof Long || obj instanceof Double
                || obj instanceof BigDecimal || obj instanceof Float
                || obj instanceof Boolean) {
            return String.valueOf(obj);
        } else {
            try {
                Class.forName("com.thoughtworks.xstream.XStream");
                XStream stream = new XStream();
                return stream.toXML(obj);
            } catch (Exception e) {
                return obj.toString();
            }
        }
    }

    /**
     * Marshal the java object to xml.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static String marshalToXml(Object clazz) throws Exception {

        JAXBContext context1 = getJAXBContext(clazz.getClass());//JAXBContext.newInstance(clazz.getClass());
        Marshaller m = context1.createMarshaller();
        Writer writer = new StringWriter();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(clazz, writer);
        return writer.toString();

    }

    // private static boolean isUseJAXB() throws Exception {
    //
    // try {
    // Class.forName("javax.xml.bind.JAXBContext");
    // Class.forName("javax.xml.bind.Marshaller");
    // Class.forName("javax.xml.bind.Unmarshaller");
    //
    // return true;
    // } catch (Exception e) {
    // return false;
    // }
    // }

    /**
     * Unmarshal the xml string to specified java object instance.
     * 
     * @param str
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object unmarshalToObject(String str, Class clazz)
            throws Exception {

        JAXBContext jc = getJAXBContext(clazz);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(new StreamSource(new StringReader(str)));
        return obj;

    }

    /**
     * Unmarshal the xml string to specified java object instance.
     * 
     * @param node
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object unmarshalToObject(Node node, Class clazz)
            throws Exception {

        JAXBContext jc = getJAXBContext(clazz);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(node);
        return obj;
    }
}
