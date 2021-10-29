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
package com.unibeta.vrules.tools;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationGenericType;
import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationRules;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.vRules4j;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.parsers.ObjectSerializer;
import com.unibeta.vrules.reflect.ObjectReflector;
import com.unibeta.vrules.utils.CommonUtils;
import com.unibeta.vrules.utils.VRulesCommonUtils;
import com.unibeta.vrules.utils.XmlUtils;

/**
 * An util vRules4j-Digester tool which is used for digesting the java object to
 * vRules configurations. The original codes is contributed by Terry.Jiang.
 * 
 * @author Jordan.Xue
 */
public class Java2vRules {

    private static final String NEW_DATE_TO_STRING = "new Date().toString()";
    private static final String OUT_BOOL_PAREMATER_NAME = "$";
    private static final String RULES_XML = "Rules.xml";
    private static final String XML = ".xml";
    private static final int TYPE_NONE_COLLECTION = 0;
    private static final int TYPE_MAP = 3;
    private static final int TYPE_LIST = 2;
    private static final int TYPE_ARRAY = 1;

    private static String BR = "\r";
    private static final String COMMENTS = "<!-- "
            + BR
            + "@gt > "
            + BR
            + "@lt < "
            + BR
            + "@lteq <= "
            + BR
            + "@gteq >= "
            + BR
            + "@or || "
            + BR
            + "@and && "
            + BR
            + "@bitwise_and & "
            + BR
            + "@bitwise_or | "
            + BR
            + "@left_shift << "
            + BR
            + "@right_shift >> "
            + BR
            + "@right_unsigned_shift >>> "
            + BR
            + "@and_assign &= "
            + BR
            + "@or_assign |= "
            + BR
            + "@left_shift_assign <<= "
            + BR
            + "@right_shift_assign >>= "
            + BR
            + "@right_unsigned_shift_assign >>>= "
            + BR
            + BR
            + "$Powered By vRules4j-Digester Automatically On "
            + NEW_DATE_TO_STRING
            + "$"
            + BR
            + "Visit http://sourceforge.net/projects/vrules/ to download the latest version"
            + BR + "" + BR + "Author: "+System.getProperty("user.name") + BR + ""
            // + "msn/email: jordan.xue@hotmail.com" + BR + ""

            + "-->";

    private static Map<String, String> digestedMap = new Hashtable<String, String>();
    private static Map<String, Class> digestedGenericTypeMap = new HashMap<String, Class>();

    /**
     * The main method to be used in the command line model.
     * 
     * @param args
     *            of new String[]{className,filePath} format
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String className = null;
        String filePath = null;

        if (args == null || args.length <= 1) {
            System.out.println("please entry class name and file path name");
            System.out.println("Useage: Java2vRuels className filePath");
            return;
        } else {
            className = args[0];
            filePath = args[1];
        }

        digest(new Class[] { Class.forName(className) }, filePath);
    }

    /**
     * Serialize input VRules4j to xml and save to specified fileName if give
     * fileName is valid.
     * 
     * @param vRules4j
     * @param fileName
     * @return xml
     * @throws Exception
     */
    public static String toXml(vRules4j vRules4j, String fileName)
            throws Exception {

        String comm = COMMENTS.replace(NEW_DATE_TO_STRING,
                new Date().toString());

        String xml = comm + "\n" + ObjectSerializer.marshalToXml(vRules4j);

        if (null != fileName && !CommonUtils.isNullOrEmpty(fileName)) {
            XmlUtils.prettyPrint(xml, fileName);
        }

        return XmlUtils.formatXML(xml);
    }

    /**
     * Powers the strictest rules' configuration file via the given Class array
     * types. Digest the java class to vRuels.
     * 
     * @param clazz
     *            the Class array to be degisted.
     * @param filePath
     *            file patch to be located
     * @throws Exception
     */
    public synchronized static void digest(Class clazz[], String filePath)
            throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (null == clazz || clazz.length == 0) {
                throw new Exception("The input objects is null!");
            }

            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());

            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                map.putAll(createEntityObjects(clazz[i], map, null));
            }

            createRuleFile(map, fileName, null);
            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Class array
     * types and registering the error object instance. Digest the java class to
     * vRuels.
     * 
     * @param clazz
     *            the Class array to be degisted.
     * @param filePath
     *            file patch to be located *
     * @param errorObj
     *            the returned error object instance
     * @throws Exception
     */
    public synchronized static void digest(Class clazz[], String filePath,
            Object errorObj) throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (null == clazz || clazz.length == 0) {
                throw new Exception("The input objects is null!");
            }

            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());

            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                map.putAll(createEntityObjects(clazz[i], map, errorObj));
            }

            createRuleFile(map, fileName, null);
        }
        digestedMap.clear();
    }

    /**
     * Powers the strictest rules' configuration file via the given Object array
     * types. Digest the java class to vRuels.
     * 
     * @param obj
     *            the array object to be degisted.
     * @param filePath
     *            file path to be located
     * @throws Exception
     */
    public synchronized static void digest(Object[] obj, String filePath)
            throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (obj == null || obj.length == 0) {
                throw new Exception("The input objects is null!");
            }

            Class[] clazz = new Class[obj.length];
            for (int i = 0; i < obj.length && null != obj[i]; i++) {
                clazz[i] = VRulesCommonUtils.getTargetClass(obj[i]);
            }

            if (null == clazz || clazz.length == 0) {
                return;
            }

            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> entireMap = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());

            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                Map<String, String> map = Collections
                        .synchronizedSortedMap(new TreeMap<String, String>());
                entireMap.putAll(createEntityObjects(clazz[i], map, null));
            }

            createRuleFile(entireMap, fileName, null);
            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Class type.
     * digest the java class to vRuels.
     * 
     * @param clazz
     *            the Class object to be degisted.
     * @param filePath
     *            file path to be located
     * @throws Exception
     */
    public synchronized static void digest(Class clazz, String filePath)
            throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (clazz == null) {
                throw new Exception("The input objects is null!");
            }
            String fileName = generateFileFullName(clazz, filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());
            createRuleFile(createEntityObjects(clazz, map, null), fileName,
                    null);

            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Class type
     * and registering the error object instance. digest the java class to
     * vRuels.
     * 
     * @param clazz
     *            the Class object to be degisted.
     * @param filePath
     *            file path to be located
     * @param errorObj
     *            the returned error object instance
     * @throws Exception
     */
    public synchronized static void digest(Class clazz, String filePath,
            Object errorObj) throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (clazz == null) {
                throw new Exception("The input objects is null!");
            }

            String fileName = generateFileFullName(clazz, filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());
            createRuleFile(createEntityObjects(clazz, map, errorObj), fileName,
                    null);

            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Object type.
     * digest the java class to vRuels.
     * 
     * @param obj
     *            the object to be degisted.
     * @param filePath
     *            file path to be located
     * @throws Exception
     */
    public synchronized static void digest(Object obj, String filePath)
            throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (obj == null) {
                throw new Exception("The input objects is null!");
            }

            Class clazz = obj.getClass();
            if (null == clazz) {
                return;
            }

            String fileName = generateFileFullName(clazz, filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());
            createRuleFile(createEntityObjects(clazz, map, null), fileName,
                    null);

            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Object array
     * types, meanwhile, registering the returned error object. Digest the java
     * class to vRuels.
     * 
     * @param obj
     *            the array object to be digested.
     * @param filePath
     *            file path to be located
     * @throws Exception
     * @param errorObj
     *            the returned error object instance.
     * @throws Exception
     */
    public synchronized static void digest(Object[] obj, String filePath,
            Object errorObj) throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (obj == null || obj.length == 0) {
                throw new Exception("The input objects is null!");
            }

            Class[] clazz = new Class[obj.length];
            for (int i = 0; i < obj.length && null != obj[i]; i++) {
                clazz[i] = VRulesCommonUtils.getTargetClass(obj[i]);
            }

            if (null == clazz || clazz.length == 0) {
                return;
            }
            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> entireMap = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());

            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                Map<String, String> map = Collections
                        .synchronizedSortedMap(new TreeMap<String, String>());
                entireMap.putAll(createEntityObjects(clazz[i], map, errorObj));
            }

            createRuleFile(entireMap, fileName, null);

            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Map types
     * objects, meanwhile, registering the returned error object. Digest the
     * java class to vRuels.<br>
     * The default contexts would be the map key and object's class type.<br>
     * The default object id would be the simple class name as well.
     * 
     * @param mapObj
     * @param filePath
     * @param errorObj
     * @throws Exception
     */
    public synchronized static void digest(Map<String, Object> mapObj,
            String filePath, Object errorObj) throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (null == mapObj || mapObj.size() == 0) {
                throw new Exception("The input objects is null!");
            }

            Class clazz[] = new Class[mapObj.size()];

            Collection c = mapObj.values();
            int j = 0;
            for (Object o : c) {
                if (null != o) {
                    clazz[j++] = VRulesCommonUtils.getTargetClass(o);
                }
            }

            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());
            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                map.putAll(createEntityObjects(clazz[i], map, errorObj));
            }
            createRuleFile(map, fileName, mapObj);
            digestedMap.clear();
        }
    }

    /**
     * Powers the strictest rules' configuration file via the given Map types
     * objects, digest the java class to vRuels.<br>
     * The default contexts would be the map key and object's class type.<br>
     * The default object id would be the simple class name as well.
     * 
     * @param mapObj
     * @param filePath
     * @throws Exception
     */
    public synchronized static void digest(Map<String, Object> mapObj,
            String filePath) throws Exception {

        synchronized (digestedMap) {
            digestedMap.clear();
            if (null == mapObj || mapObj.size() == 0) {
                throw new Exception("The input objects is null!");
            }

            Class clazz[] = new Class[mapObj.size()];

            Collection c = mapObj.values();
            int j = 0;
            for (Object o : c) {
                if (null != o) {
                    clazz[j++] = VRulesCommonUtils.getTargetClass(o);
                }
            }

            String fileName = generateFileFullName(clazz[0], filePath);
            Map<String, String> map = Collections
                    .synchronizedSortedMap(new TreeMap<String, String>());
            for (int i = 0; i < clazz.length && null != clazz[i]; i++) {
                map.putAll(createEntityObjects(clazz[i], map, null));
            }
            createRuleFile(map, fileName, mapObj);
            digestedMap.clear();
        }
    }

    private static String generateFileFullName(Class clazz, String filePath) {

        String fileName = null;

        String pathName = CommonUtils.getFilePathName(filePath);
        File file = new File(pathName);

        if (!file.exists()) {
            file.mkdirs();
        }

        if (!filePath.endsWith(XML)) {
            fileName = pathName + File.separator
                    + CommonUtils.getClassSimpleName(clazz) + RULES_XML;
        } else {
            fileName = filePath;
        }

        return fileName;
    }

    private static void createRuleFile(Map objectList, String fileName,
            Map<String, Object> objMap) throws Exception {

        StringBuffer strBuffer = new StringBuffer();

        try {
            Iterator ite = objectList.values().iterator();

            strBuffer = initValidationRules(objMap);
            while (ite.hasNext()) {
                StringBuffer rule = (StringBuffer) ite.next();
                // System.out.println(rule.toString());
                strBuffer.append(rule.toString());
            }

            endValidationRules(strBuffer);

            XmlUtils.prettyPrint(strBuffer.toString(), fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }

        /*
         * BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
         * try { Iterator ite = objectList.values().iterator();
         * initValidationRules(out); while (ite.hasNext()) { StringBuffer rule =
         * (StringBuffer) ite.next(); // System.out.println(rule.toString());
         * out.write(rule.toString()); } endValidationRules(out); } catch
         * (RuntimeException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); } finally { out.close(); }
         */

    }

    /**
     * <Object refId=> </Oject>
     * 
     * @param className
     * @return
     * @throws Exception
     */
    private static Map createEntityObjects(Class clazz, Map map, Object errorObj)
            throws Exception {

        // Map map = Collections.synchronizedSortedMap(new
        // TreeMap<String,String>());
        if (digestedMap.containsKey(clazz.getName())
                || clazz.getName().startsWith(VRulesConstants.JAVA_OBJECTS)) {
            return map;
        } else {
            digestedMap.put(clazz.getName(),
                    generateRandomObjectId(digestedMap, clazz.getSimpleName()));
        }

        generateGenericObjects(clazz, map, errorObj);

        Class[] superclass = hasSuperClass(clazz);
        boolean hasSuperClass = superclass.length > 0;
        String extensions = "";
        if (hasSuperClass) {
            for (Class c : superclass) {
                if (digestedMap.get(c.getName()) == null) {
                    createEntityObjects(c, map, errorObj);
                }
            }

            extensions = buildExtensions(superclass);
        }

        String objectId = digestedMap.get(clazz.getName());

        String errorObjectElements = appendErrorObjectElements(errorObj);

        StringBuffer str = new StringBuffer();
        str.append(BR);
        str.append("<object id=\"").append(objectId + "\"");
        str.append(" name=\"").append(
                VRulesCommonUtils.generateObjectEntityXmlName(objectId) + "\"");

        if (hasSuperClass) {
            str.append(" extensions=\"" + extensions + "\"");
        }

        // Object no need the sequence, logic sequence is depends on the rule
        // sequence
        // str.append(" sequence = \""+ digestedList.size()+"\" ");

        str.append(" desc=\"\">").append(BR);
        str.append(" <className>").append(clazz.getName())
                .append("</className>").append(BR);
        str.append(" <nillable>false</nillable>").append(BR);
        str.append(" <rules>").append(BR);
        // if (clazz.isInterface()) {
        str.append(generateInterfaceRules(clazz, map, errorObj, objectId,
                errorObjectElements));
        // } else {
        // str.append(generateClassRules(clazz, map, errorObj, objectId,
        // errorObjectElements));
        // }
        str.append(" </rules>").append(BR);
        str.append("</object>").append(BR);

        map.put(objectId, str);

        return map;
    }

    private static void generateGenericObjects(Class clazz, Map map,
            Object errorObj) throws Exception {

        // append all related generic types rules template
        Map<String, Class> genericTypes = ObjectReflector
                .getAllClientGenericTypeMap(clazz);
        for (Class t : genericTypes.values()) {
            if (!t.getCanonicalName().startsWith("java.")) {
                createEntityObjects(t, map, errorObj);
            }
        }
        digestedGenericTypeMap.putAll(genericTypes);
    }

    private static String buildExtensions(Class[] superclass) {

        String s = "";

        for (Class c : superclass) {
            s += digestedMap.get(c.getName()) + ",";
        }
        return s.substring(0, s.length() - 1);
    }

    private static String generateRandomObjectId(Map map, String objectId) {

        String id = objectId;
        for (int i = 2; map.containsValue(id); i++) {
            id += "" + i;
        }
        return id.trim();
    }

    private static String generateInterfaceRules(Class clazz, Map map,
            Object errorObj, String objectId, String errorObjectElements)
            throws Exception {

        StringBuffer str = new StringBuffer();

        Method[] fds = filterLocalDeclaredMethods(clazz);
        Map<String, String> xmlNameMap = VRulesCommonUtils
                .buildXmlElementWrappedFieldNameMap(clazz);
        Map<String, Integer> ruleIdCountMap = new HashMap<String, Integer>();

        if (fds != null & fds.length > 0) {
            double sequenceIndex = 1;
            for (int i = 0; i < fds.length; i++) {
                Method method = fds[i];

                // if it is not the javabean attribute
                if (!((method.getName().startsWith("is") || method.getName()
                        .startsWith("get")) && (null == method
                        .getParameterTypes() || method.getParameterTypes().length == 0))) {
                    continue;
                }

                Rule rule = digestRuleObject(method, objectId);

                String xmlName = null;
                String attributeName = ObjectReflector
                        .reflectAttributeNameByMethodName(method
                                .getName());
                
                if (null != xmlNameMap.get(method.getName())) {
                    xmlName = xmlNameMap.get(method.getName());
                } else {
                    xmlName = attributeName;
                }

                Integer count = ruleIdCountMap.get(attributeName);
                if(count==null){
                    ruleIdCountMap.put(attributeName, 0);
                }else{
                    ruleIdCountMap.put(attributeName, ++count);
                }
                
                String ruleId = attributeName;
                if(count!= null){
                    ruleId = attributeName+count;
                }
                str.append(getIntention(1))
                        .append("<rule id=\"")
                        .append(ruleId).append("\" ");

                buildRulesAttributes(str, sequenceIndex, rule, xmlName);
                sequenceIndex =  rule.getSequence() + 1;

                str.append(getIntention(2))
                        .append(" ifTrue=\"\" ifFalse=\"\" ");
                int collectionType = TYPE_NONE_COLLECTION;
                if (!method.getReturnType().isPrimitive()) {// complex

                    collectionType = isCollectionType(method.getReturnType());

                    String refIdName = CommonUtils.getClassSimpleName(method
                            .getReturnType());

                    if (!method.getReturnType().getName()
                            .startsWith(VRulesConstants.JAVA_OBJECTS)) {

                        handleArrayInstance(map, str, method.getReturnType(),
                                rule, collectionType, refIdName, errorObj);
                    } else {
                        handleListandMapInstance(clazz, map, str, method, rule,
                                collectionType, errorObj);
                    }
                } else {
                    str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);
                }

                str.append(getIntention(2)).append("<dataType>");

                if (collectionType == TYPE_ARRAY) {
                    str.append(getArrayTypeName(method.getReturnType()) + "[]");
                } else {
//                    Map<String, Type[]> genericTypes = ObjectReflector
//                            .getGenericTypeOfField(clazz,"");
                    
                    str.append(method.getGenericReturnType().toString().replace("<", "[").replace(">", "]"));
                }
                str.append("</dataType>").append(BR);
                str.append(getIntention(2))
                        .append("<attributes>")
                        .append(attributeName).append("</attributes>")
                        .append(BR);
                str.append(getIntention(2))
                        .append("<boolParam>" + rule.getOutputBool()
                                + "</boolParam>").append(BR);
                str.append(getIntention(2)).append("<assert>");

                str.append(rule.getPredicate());
                str.append("</assert>").append(BR);
                str.append(getIntention(2))
                        .append("<message id=\""
                                + attributeName + "ErrorMsg"
                                + "\" returnErrorId=\"false\">");

                str.append(rule.getErrorMessage());

                str.append("</message>").append(BR);

                str.append(getIntention(2)).append("<decision>").append(BR);

                str.append(getIntention(0)).append(errorObjectElements)
                        .append(BR);
                str.append(getIntention(2)).append("</decision>").append(BR);

                str.append(getIntention(1)).append("</rule>").append(BR);
            }// fd
        }// fds
        return str.toString();
    }

    private static Method[] filterLocalDeclaredMethods(Class clazz) {

        if (null == clazz) {
            return new Method[0];
        }

        List<Method> methodList = Collections
                .synchronizedList(new ArrayList<Method>());

        Method[] methods = clazz.getDeclaredMethods();
        Method[] allMethods = getAllInterfacesMethods(clazz);

        for (Method method : methods) {
            boolean match = false;
            for (Method m : allMethods) {
                if (method.getName().equals(m.getName())) {
                    match = true;
                    break;
                }
            }
            if (!match && !method.getName().startsWith("set")) {
                methodList.add(method);
            }
        }

        Collections.sort(methodList, new Java2vRules.MethodComparator());

        return methodList.toArray(new Method[0]);
    }

    private static class MethodComparator implements Comparator<Method> {

        public int compare(Method o1, Method o2) {

            if (null == o1 || null == o2) {
                return 0;
            }

            String n1 = ObjectReflector.reflectAttributeNameByMethodName(o1
                    .getName());
            String n2 = ObjectReflector.reflectAttributeNameByMethodName(o2
                    .getName());

            if (null == n1 || null == n2) {
                return 0;
            }

            return n1.compareTo(n2);
        }

    }

    private static Method[] getAllInterfacesMethods(Class clazz) {

        List<Method> list = Collections
                .synchronizedList(new ArrayList<Method>());

        if (null == clazz) {
            return new Method[0];
        }

        Class[] interfaces = clazz.getInterfaces();

        for (Class c : interfaces) {
            CommonUtils.copyArrayToList(c.getMethods(), list);
        }

        return list.toArray(new Method[0]);
    }

    private static void handleListandMapInstance(Class clazz, Map map,
            StringBuffer str, Method method, Rule rule, int collectionType,
            Object errorObj) throws Exception {

        String rulesRefIdClassName = null;
        // String refIdName;
        if (collectionType == TYPE_LIST) {

            rulesRefIdClassName = getVRulesRefIdTag(clazz, method);
            if (null != rulesRefIdClassName) {
                // refIdName = CommonUtils.getClassSimpleName(Class
                // .forName(rulesRefIdClassName));

                handleReferranceInfo(map, str, rule,
                        Class.forName(rulesRefIdClassName), errorObj,
                        collectionType);
            } else {
                str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);
            }

        } else if (collectionType == TYPE_MAP) {
            rulesRefIdClassName = Map.class.getCanonicalName();
            handleReferranceInfo(map, str, rule,
                    Class.forName(rulesRefIdClassName), errorObj,
                    collectionType);
        } else {
            str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);
        }

        // return rulesRefIdClassName;

    }

    private static String getVRulesRefIdTag(Class clazz, Method method) {

        if (null == method) {
            return null;
        }

        ValidationGenericType ruleTag = null;
        Annotation[] annotations = method.getAnnotations();
        if (annotations != null) {

            for (Annotation tag : annotations) {
                if (tag instanceof ValidationGenericType) {
                    ruleTag = (ValidationGenericType) tag;
                }
            }
        }

        if (null != ruleTag) {
            return ruleTag.className();
        } else {
            String name = ObjectReflector
                    .reflectAttributeNameByMethodName(method.getName());
            String key = clazz.getCanonicalName() + "." + name;

            if (digestedGenericTypeMap.containsKey(key)) {
                return digestedGenericTypeMap.get(key).getCanonicalName();
            } else {
                return null;
            }

        }
    }

    private static void handleArrayInstance(Map map, StringBuffer str,
            Class returnType, Rule rule, int collectionType, String refIdName,
            Object errorObj) throws Exception {

        String rulesRefIdClassName = null;
        Class cls;

        if (collectionType != TYPE_NONE_COLLECTION) {

            if (collectionType == TYPE_ARRAY) {
                String arrayTypeName = getArrayTypeName(returnType);
                refIdName = refIdName.trim().substring(0,
                        refIdName.length() - 1);
                rulesRefIdClassName = arrayTypeName;
            }

        } else {
            rulesRefIdClassName = returnType.getName();
        }

        if (null != rulesRefIdClassName) {
            cls = Class.forName(rulesRefIdClassName);
            handleReferranceInfo(map, str, rule, cls, errorObj, collectionType);
        } else {
            str.append(">");
        }
        // return rulesRefIdClassName;

    }

    private static void buildRulesAttributes(StringBuffer str, double i,
            Rule rule, String xmlName) {

        str.append(getIntention(1))
                .append(" name=\"")
                .append(CommonUtils.isNullOrEmpty(rule.getName()) ? xmlName
                        : rule.getName()).append("\" ");
        str.append(getIntention(1))
                .append(" sequence=\"")
                .append(""
                        + (rule.getSequence() <= 0 ? (i) : rule.getSequence())
                        + "").append("\" ");
        rule.setSequence(i);
        str.append(getIntention(1)).append(" breakpoint=\"")
                .append("" + (rule.isBreakpoint() ? "on" : "off") + "\"");
        str.append(getIntention(1))
                .append(" depends=\"")
                .append(""
                        + (!CommonUtils.isNullOrEmpty(rule.getDepends()) ? rule
                                .getDepends() : "") + "\"");
    }

    private static String appendErrorObjectElements(Object errorObj)
            throws Exception {

        StringBuffer buffer = new StringBuffer();

        if (null != errorObj) {

            buffer.append(getIntention(0)).append(
                    ObjectSerializer.marshalToXml(errorObj));

        }
        return buffer.toString();
    }

    // private static String handleListandMapInstance(Map map, StringBuffer str,
    // Field field, Rule rule, int collectionType, Object errorObj)
    // throws Exception, ClassNotFoundException {
    //
    // String rulesRefIdClassName = null;
    // String refIdName;
    // if (collectionType > TYPE_ARRAY) {
    //
    // rulesRefIdClassName = getVRulesRefIdTag(field);
    // if (null != rulesRefIdClassName) {
    // refIdName = CommonUtils.getClassSimpleName(Class
    // .forName(rulesRefIdClassName));
    //
    // handleReferranceInfo(map, str, rule, Class
    // .forName(rulesRefIdClassName), errorObj, collectionType);
    // } else {
    // str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);
    // }
    //
    // } else {
    // str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);
    // }
    //
    // return rulesRefIdClassName;
    // }

    private static void handleReferranceInfo(Map map, StringBuffer str,
            Rule rule, Class cls, Object errorObj, int collectionType)
            throws Exception {

        // recursion on complex objects
        createEntityObjects(cls, map, errorObj);

        String refIdName = digestedMap.get(cls.getName());
        
        if(null == refIdName){
            refIdName = "";
        }
        str.append(" refId=\"").append(refIdName).append("\"");
        
        if (collectionType == TYPE_NONE_COLLECTION) {
            str.append(" isCollection=\"false\" ");
        } else {
            str.append(" isCollection=\"true\" ");
        }

        str.append(" recursive=\"true\" ");
        str.append(" isComplexType=\"true\"");
        str.append(" desc=\"" + rule.getDesc() + "\">").append(BR);

        str.append(" <binding");
        str.append(" wirings=\"" + refIdName + "\" ");
        if (collectionType == TYPE_NONE_COLLECTION) {
            str.append(" src=\"" + "" + "\" ");
        } else {
            str.append(" foreach=\"" + "" + "\" ");
        }
        str.append(" var=\"" + "" + "\" ");
        str.append("/>").append(BR);

    }

    // private static String handleArrayInstance(Map map, StringBuffer str,
    // Field field, Rule rule, int collectionType, String refIdName,
    // Object errorObj) throws Exception {
    //
    // String rulesRefIdClassName = null;
    // Class cls;
    //
    // if (collectionType != TYPE_NONE_COLLECTION) {
    //
    // if (collectionType == TYPE_ARRAY) {
    // String arrayTypeName = getArrayTypeName(field.getType());
    // refIdName = refIdName.trim().substring(0,
    // refIdName.length() - 1);
    // rulesRefIdClassName = arrayTypeName;
    // }
    //
    // } else {
    // rulesRefIdClassName = field.getType().getName();
    // }
    //
    // cls = Class.forName(rulesRefIdClassName);
    // handleReferranceInfo(map, str, rule, cls, errorObj, collectionType);
    //
    // return rulesRefIdClassName;
    // }

    // private static String getVRulesRefIdTag(Field field) throws Exception {
    //
    // if (null == field) {
    // return null;
    // }
    // ValidationGenericType ruleTag = null;
    // Annotation[] annotations = field.getAnnotations();
    // if (annotations != null) {
    //
    // for (Annotation tag : annotations) {
    // if (tag instanceof ValidationGenericType) {
    // ruleTag = (ValidationGenericType) tag;
    // }
    // }
    // }
    //
    // if (null != ruleTag) {
    // return ruleTag.className();
    // } else {
    // return null;
    // }
    // }

    private static Class[] hasSuperClass(Class clazz) {

        List<Class> list = new ArrayList<Class>();

        Class superclass = clazz.getSuperclass();
        if (superclass != null
                && !superclass.getName().startsWith(
                        VRulesConstants.JAVA_OBJECTS)) {
            list.add(superclass);
        }

        if (clazz.getInterfaces().length > 0) {
            for (Class c : clazz.getInterfaces()) {
                if (!c.getName().startsWith(VRulesConstants.JAVA_OBJECTS)) {
                    list.add(c);
                }
            }

        }

        return list.toArray(new Class[] {});
    }

    private static String getArrayTypeName(Class type) {

        String name = type.getName();

        if (name.length() > 3) {
            name = name.substring(0, name.length() - 1);
            name = name.substring(2);

            return name;
        } else {
            return null;
        }
    }

    private static int isCollectionType(Class type) throws Exception {

        if (type.isArray()) {
            return TYPE_ARRAY;
        } else if (CommonUtils.isInterfaceOf(type, List.class)
                || type.equals(List.class)) {
            return TYPE_LIST;
        } else if (CommonUtils.isInterfaceOf(type, Map.class)
                || type.equals(Map.class)) {
            return TYPE_MAP;
        }

        // if (type.isArray() || CommonUtils.isInterfaceOf(type, List.class)
        // || CommonUtils.isInterfaceOf(type, Map.class)) {
        // return TYPE_ARRAY;
        // }
        return TYPE_NONE_COLLECTION;
    }

    /**
     * INIT head of validation Rules file
     * 
     * @param objMap
     * @param root
     * @throws IOException
     */
    private static StringBuffer initValidationRules(Map<String, Object> objMap)
            throws IOException {

        StringBuffer root = new StringBuffer();
        String comm = COMMENTS.replace(NEW_DATE_TO_STRING,
                new Date().toString());

        String head = "<vRules4j> " + BR + " <global> " + BR + getIntention(2)
                + " <decisionMode>false</decisionMode> " + getIntention(2)
                + " <assertion>false</assertion> " + getIntention(2)
                + " <returnErrorId>false</returnErrorId> " + BR
                + getIntention(2)
                + " <displayErrorValue>true</displayErrorValue> " + BR
                + " <toggleBreakpoint>off</toggleBreakpoint> " + BR
                + " <enableBinding>false</enableBinding> " + BR
                + " <mergeExtensions>false</mergeExtensions> " + BR
                + getIntention(2) + " <includes></includes> " + BR
                + " </global>" + BR + " <imports></imports>" + BR
                + " <java></java>";

        root.append(comm).append(BR);
        root.append(head).append(BR);
        if (null == objMap) {
            root.append(" <contexts>" + BR
                    + " <context name = \"\" className=\"\"/>" + BR
                    + " </contexts>" + BR);
        } else {
            root.append(" <contexts>" + BR);
            Set set = objMap.keySet();
            Iterator<String> i = set.iterator();

            for (; i.hasNext();) {
                String next = i.next();
                if (null != objMap.get(next)) {

                    Class targetClass = VRulesCommonUtils.getTargetClass(objMap
                            .get(next));

                    if (null != targetClass) {

                        String contextClassName = targetClass
                                .getCanonicalName();
                        try {
                            Class.forName(contextClassName);
                            root.append("<context name = \"" + next
                                    + "\" className=\"" + contextClassName
                                    + "\"/>" + BR);
                        } catch (ClassNotFoundException e) {
                            root.append("<!--TODO: below invalid class name to be corrected -->");
                            root.append("<!--");
                            root.append("<context name = \"" + next
                                    + "\" className=\"" + contextClassName
                                    + "\"/>" + BR);
                            root.append("-->");
                        }
                    }
                }
            }
            root.append(" </contexts>" + BR);
        }

        root.append(" <decisionConstants>" + BR
                + " <definition name = \"\" value=\"\"/>" + BR
                + " </decisionConstants>" + BR);
        
        root.append(" <ruleset id = \"\" desc = \"\">" + BR
                + " <rule id = \"\" desc = \"\"/>" + BR
                + " </ruleset>" + BR);
        
        // out.write(root.toString());
        return root;
    }

    /**
     * @param out
     * @throws IOException
     */
    private static void endValidationRules(StringBuffer end) throws IOException {

        end.append("</vRules4j>");
    }

    private static String getIntention(int level) {

        String sIntention = "";
        while (level > 0) {
            sIntention += "  ";
            level--;
        }
        return sIntention;
    }

    private static Rule digestRuleObject(Object obj, String objectId) {

        Rule rule = new Rule();

        if (null == obj) {
            return rule;
        }

        Annotation[] annotations = null;
        Class filedType = null;
        String fieldName = null;

        if (obj instanceof Method) {
            Method field = (Method) obj;

            filedType = field.getReturnType();
            fieldName = ObjectReflector.reflectAttributeNameByMethodName(field
                    .getName());
            rule.setName(fieldName);
            annotations = field.getAnnotations();

        } else if (obj instanceof Field) {
            Field field = (Field) obj;
            rule.setName(field.getName());

            filedType = field.getType();
            fieldName = field.getName();

            annotations = field.getAnnotations();
        }

        if (annotations != null) {
            ValidationRules ruleTag = null;

            for (Annotation tag : annotations) {
                if (tag instanceof ValidationRules) {
                    ruleTag = (ValidationRules) tag;
                }
            }

            rule.setDesc(ruleTag == null || isEmpty(ruleTag.desc()) ? ""
                    : ruleTag.desc().trim());

            if (null != filedType && filedType.isPrimitive()) {
                if (ruleTag == null || isEmpty(ruleTag.predicate())) {
                    rule.setPredicate(" true ");
                } else {
                    rule.setPredicate(isEmpty(ruleTag.predicate()) ? ""
                            : CommonUtils.reverseSpecialExpression(ruleTag
                                    .predicate().trim()));
                }
            } else {
                if (ruleTag == null || isEmpty(ruleTag.predicate())) {
                    rule.setPredicate(fieldName + " !=null ");
                } else {
                    rule.setPredicate(ruleTag == null
                            || isEmpty(ruleTag.predicate()) ? "" : CommonUtils
                            .reverseSpecialExpression(ruleTag.predicate()
                                    .trim()));
                }
            }

            rule.setOutputBool(generateOutBoolParameter(obj,
                    null != ruleTag ? ruleTag.outputBool() : null));

            rule.setErrorMessage(ruleTag == null
                    || isEmpty(ruleTag.errorMessage()) ? fieldName
                    + " information of " + objectId + " is invalid." : ruleTag
                    .errorMessage());

            if (null != ruleTag) {
                rule.setBreakpoint((!CommonUtils.isNullOrEmpty(ruleTag
                        .breakpoint()) && (ruleTag.breakpoint()
                        .equalsIgnoreCase("on") || ruleTag.breakpoint()
                        .equalsIgnoreCase("true"))) ? true : false);
                rule.setDepends(CommonUtils.isNullOrEmpty(ruleTag.depends()) ? ruleTag
                        .depends() : "");
                rule.setSequence((!CommonUtils.isNullOrEmpty(ruleTag.sequence()) ? Long
                        .valueOf(ruleTag.sequence()) : 0l));
                rule.setName((!CommonUtils.isNullOrEmpty(ruleTag.name()) ? ruleTag
                        .name() : fieldName));
            }
        }

        return rule;
    }

    private static boolean isEmpty(String str) {

        return CommonUtils.isNullOrEmpty(str);
    }

    /**
     * Generates the outbool paremater name.
     * 
     * @param field
     * @param specOutbool
     * @return
     */
    private static String generateOutBoolParameter(Object obj,
            String specOutbool) {

        String outbool = OUT_BOOL_PAREMATER_NAME;
        if (obj instanceof Field) {
            Field field = (Field) obj;
            if (field.getName().indexOf(OUT_BOOL_PAREMATER_NAME) >= 0) {
                outbool = OUT_BOOL_PAREMATER_NAME + new Random().nextInt(10);
            } else {
                if (null != specOutbool && specOutbool.length() > 0) {
                    outbool = specOutbool;
                }
            }

            if (field.getName().indexOf(outbool) >= 0) {
                outbool = generateOutBoolParameter(field, outbool);
            }
        } else if (obj instanceof Method) {
            Method field = (Method) obj;
            if (ObjectReflector.reflectAttributeNameByMethodName(
                    field.getName()).indexOf(OUT_BOOL_PAREMATER_NAME) >= 0) {
                outbool = OUT_BOOL_PAREMATER_NAME + new Random().nextInt(10);
            } else {
                if (null != specOutbool && specOutbool.length() > 0) {
                    outbool = specOutbool;
                }
            }

            if (ObjectReflector.reflectAttributeNameByMethodName(
                    field.getName()).indexOf(outbool) >= 0) {
                outbool = generateOutBoolParameter(field, outbool);
            }
        }

        return outbool.trim();
    }

    /**
     * Copies the source file to destination file. If srcFileName name equals to
     * destFileName, the file will not be copied.
     * 
     * @param srcFileName
     * @param destFileName
     * @throws Exception
     */
    public static void copyFile(String srcFileName, String destFileName)
            throws Exception {

        CommonUtils.copyFile(srcFileName, destFileName);
    }
}
