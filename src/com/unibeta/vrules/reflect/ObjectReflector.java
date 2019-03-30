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
package com.unibeta.vrules.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>ObjectReflector</code> is used for getting the specified attribute's
 * value via java reflect.
 * 
 * @author Jordan.xue
 */
public class ObjectReflector {

    private static Logger log = Logger.getLogger(ObjectReflector.class);

    private static final String JAVA_LANG_BOOLEAN = VRulesConstants.JAVA_LANG_BOOLEAN;
    private static final String BOOLEAN = VRulesConstants.JAVA_BOOLEAN;
    private static Map<String, Field[]>javaBeanFieldsMap = new HashMap<String, Field[]>();

    /**
     * Reflects the value for every specified attributes.
     * 
     * @param obj
     *            the object to be reflected.
     * @param entity
     *            <code>ObjectEntity</code>
     * @return map<id,value> returns all attributes' value in one map.
     * @throws Exception
     *             1.the validation object is null. </br> 2.the class type is
     *             not mathced. </br> 3.The specified class path can not be
     *             found.
     */
    public static Map reflectValues(Object obj, ObjectEntity entity)
            throws Exception {

        Class cls = init(obj, entity);

        Map map = new HashMap();

        List rules = entity.getRules();
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();

            String parameters[] = rule.getInputObjects().split(",");
            for (int j = 0; j < parameters.length; j++) {
                Object object = null;
                String generateMethodNmae = generateMethodName(parameters[j], cls);
                
				if (generateMethodNmae != null) {
					Method method = cls.getMethod(generateMethodNmae, null);
					object = method.invoke(obj, new Object[] {});
					// if (null != object) {
					map.put(parameters[j], object);
					// }
				}
            }
        }

        return map;
    }

    /**
     * Reflects the object types.
     * 
     * @param obj
     * @param entity
     * @return a map contains all attributes' data type.
     * @throws Exception
     * @throws Exception
     */
    public static Map<String, String> reflectTypes(ObjectEntity entity)
            throws Exception {

        if (null == entity) {
            throw new Exception(
                    " object entity configuration can not be found in the vRules config file!");
        }

        Map<String, String> map = null;
        Class cls = Class.forName(entity.getClassName());

        if (cls.isInterface()) {
            map = reflectInterfaceTypes(entity, cls);
        } else {
            map = reflectClassTypes(entity, cls);
        }

        return map;
    }

    private static Map<String, String> reflectInterfaceTypes(
            ObjectEntity entity, Class cls) throws NoSuchFieldException {

        Map<String, String> map = new HashMap<String, String>();

        Method[] methods = cls.getMethods();

        for (Method m : methods) {
            String name = m.getName();
            if ((name.startsWith("is") || name.startsWith("get"))
                    && (null == m.getParameterTypes() || m.getParameterTypes().length == 0)) {
                Class type = m.getReturnType();
                String attributeName = reflectAttributeNameByMethodName(name);

                map.put(attributeName, getObjectTypeForMethod(type));
            }
        }

        return map;
    }

    /**
     * @param name
     * @return
     */
    public static String reflectAttributeNameByMethodName(String name) {

        String attributeName = generateAttributeNameByMethodName(name);
        if (name.startsWith("is")) {
            attributeName = name.substring(2).substring(0, 1).toLowerCase()
                    + name.substring(3);
        } else if (name.startsWith("get")) {
            attributeName = name.substring(3).substring(0, 1).toLowerCase()
                    + name.substring(4);
        }
        return attributeName;
    }

    private static String generateAttributeNameByMethodName(String name) {

        String attName = "";

        return null;
    }

    private static Map<String, String> reflectClassTypes(ObjectEntity entity,
            Class cls) throws Exception {

        Map<String, String> map = new HashMap<String, String>();

        List rules = entity.getRules();
        List removedRules = new ArrayList();

        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();

            if (rule.getInputObjects().trim().length() <= 0) {
                continue;
            }

            String parameters[] = rule.getInputObjects().split(",");
            for (int j = 0; j < parameters.length; j++) {

                Method method = null;
                String generateMethodNmae = null;
                try {
                    generateMethodNmae = generateMethodName(parameters[j], cls);
                    if(null!= generateMethodNmae){
                    	method = cls.getMethod(generateMethodNmae, null);
                    }else{
                    	continue;
                    }
                    
                } catch (Exception e) {
                    log.error("[vRuels4j error] "
                            + e.getMessage()
                            + ", the method ["
                            + generateMethodNmae
                            + "] should be protected in "
                            + cls.getName()
                            + ". vRules4j engine will ignore this field, only public method can be accessible. Please check the configuration of field["
                            + parameters[j] + "] in " + cls.getName() + ".");
                    removedRules.add(rule);
                }

                if (null != method) {
					Class returnType = method.getReturnType();
					String type = getObjectTypeForMethod(returnType);
					map.put(parameters[j], type);
					// }
				}
            }
        }

        rules.removeAll(removedRules);

        return map;
    }

    private static Class init(Object obj, ObjectEntity entity)
            throws Exception, ClassNotFoundException {

        if (null == entity) {
            throw new Exception(
                    " object entity configuration can not be found in the vRules config file!");
        }

        if (null == obj) {
            throw new Exception("validation object is null for "
                    + entity.getClassName() + " !");
        }

        Class cls = Class.forName(entity.getClassName());

        // Removed because the extension function conplicted.
        // Class objCls = obj.getClass();
        // if (objCls != cls && objCls.getSuperclass() != cls) {
        // throw new Exception("Specified object is not of "
        // + entity.getClassName() + " type, whose id is "
        // + entity.getId() + " ! Please check it!");
        // }

        return cls;
    }

    /**
     * Gets the method name by the field name in javabean naming formula.
     * 
     * @param parameterName
     * @param clazz
     * @return
     * @throws Exception
     */
    public static String generateMethodName(String parameterName, Class clazz)
            throws Exception {

        String id = parameterName.trim();
        String typeName = getObjectTypeForField(clazz, id);
        
        if(typeName == null){
        	return null;
        }

        String name = "";
        if (BOOLEAN.equalsIgnoreCase(typeName)) {
            if (id.startsWith("is")) {
                name = "is" + id.substring(2, 3).toUpperCase()
                        + id.substring(3);
            } else {
                name = "is" + id.substring(0, 1).toUpperCase()
                        + id.substring(1);
            }
        } else {
            name = "get" + id.substring(0, 1).toUpperCase() + id.substring(1);
        }

        return name;
    }

    /**
     * Gets the object type by the given field name in the specified Class.
     * 
     * @param clazz
     * @param fieldName
     * @return null if filed name is invalid
     * @throws Exception
     */
    public static String getObjectTypeForField(Class clazz, String fieldName)
            throws Exception {

    	String field = resolveDeclaredField(clazz, fieldName);
    	if(null == field){
    		field= resolveField(clazz, fieldName);
    	}
    	
        return field;
    }

	private static String resolveDeclaredField(Class clazz, String fieldName) {
		Field field = null;
        String typeName = null;
        Class c = null;

        String fieldName1 = fieldName.substring(0, 1).toLowerCase()
                + fieldName.substring(1);
        String fieldName2 = fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        try {
            field = clazz.getDeclaredField(fieldName1);
            c = field.getType();
            typeName = c.getName();
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getDeclaredField(fieldName2);
                c = field.getType();
                typeName = c.getName();
            } catch (NoSuchFieldException e1) {
                try {
                    Method m = clazz.getDeclaredMethod("get" + fieldName2,
                            new Class[] {});
                    c = m.getReturnType();
                    typeName = c.getName();
                } catch (NoSuchMethodException e2) {
                    try {
                        Method m = clazz.getDeclaredMethod("is" + fieldName2,
                                new Class[] {});
                        c = m.getReturnType();
                        typeName = c.getName();
                    } catch (Exception e3) {
                    	
                    	log.warn(e.getMessage());
                    	log.warn(e1.getMessage());
                    	log.warn(e2.getMessage());
                    	
                        log.warn(
                                "NoSuchFieldException: "
                                        + fieldName1
                                        + " or "
                                        + fieldName2
                                        + " can not be found in "
                                        + clazz
                                        + ", which might be due to the incorrect configuration in vRules4j config file.");

                        typeName = null;
                    }

                }
            }
        }

        if (null != typeName && c.isArray() && (typeName.length() - 1) > 2) {
            String str = typeName;
            str = str.substring(2, str.length() - 1);
            typeName = str + "[]";
        } else {
            // Empty
        }

        return typeName;
	}
	
	private static String resolveField(Class clazz, String fieldName) {
		Field field = null;
        String typeName = null;
        Class c = null;

        String fieldName1 = fieldName.substring(0, 1).toLowerCase()
                + fieldName.substring(1);
        String fieldName2 = fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        try {
            field = clazz.getField(fieldName1);
            c = field.getType();
            typeName = c.getName();
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(fieldName2);
                c = field.getType();
                typeName = c.getName();
            } catch (NoSuchFieldException e1) {
                try {
                    Method m = clazz.getMethod("get" + fieldName2,
                            new Class[] {});
                    c = m.getReturnType();
                    typeName = c.getName();
                } catch (NoSuchMethodException e2) {
                    try {
                        Method m = clazz.getMethod("is" + fieldName2,
                                new Class[] {});
                        c = m.getReturnType();
                        typeName = c.getName();
                    } catch (Exception e3) {
                    	
                    	log.warn(e.getMessage());
                    	log.warn(e1.getMessage());
                    	log.warn(e2.getMessage());
                    	
                        log.warn(
                                "NoSuchFieldException: "
                                        + fieldName1
                                        + " or "
                                        + fieldName2
                                        + " can not be found in "
                                        + clazz
                                        + ", which might be due to the incorrect configuration in vRules4j config file.");

                        typeName = null;
                    }

                }
            }
        }

        if (null != typeName && c.isArray() && (typeName.length() - 1) > 2) {
            String str = typeName;
            str = str.substring(2, str.length() - 1);
            typeName = str + "[]";
        } else {
            // Empty
        }

        return typeName;
	}

    /**
     * Gets all fields that have the javabean set/get public methods.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Field[] getAllJavaBeanFields(Class clazz) throws Exception {
    	
    	if(null != javaBeanFieldsMap.get(clazz.getCanonicalName())){
    		return javaBeanFieldsMap.get(clazz.getCanonicalName());
    	}

        List fieldList = new ArrayList();

        Field[] fields = clazz.getDeclaredFields();
        CommonUtils.copyArrayToList(fields, fieldList);

        List methodNameList = generatePublicMethodNames(clazz);

        for (int i = 0; i < fieldList.size(); i++) {
            Field field = (Field) fieldList.get(i);

            String generateMethodNmae = ObjectReflector.generateMethodName(
                    field.getName(), clazz);
			if (null== generateMethodNmae || !methodNameList.contains(generateMethodNmae)) {
                fieldList.remove(i);
            }
        }

        Field[] array = (Field[]) fieldList.toArray(new Field[] {});
        javaBeanFieldsMap.put(clazz.getCanonicalName(), array);
        
		return array;
    }

    private static List generatePublicMethodNames(Class clazz) {

        Method[] methods = clazz.getMethods();
        List list = new ArrayList();

        for (int i = 0; i < methods.length; i++) {
            list.add(methods[i].getName());
        }

        return list;
    }

    private static String getObjectTypeForMethod(Class clazz)
            throws NoSuchFieldException {

        // Field field = clazz.getDeclaredField(fieldName);
        String typeName = null;

        if (clazz.isArray()) {
            String str = clazz.getName();
            str = str.substring(2, str.length() - 1);
            typeName = str + "[]";
        } else {
            typeName = clazz.getName();
        }

        return typeName;
    }

    /**
     * Get generic type from List or Map field.
     * 
     * @param clazz
     *            class
     * @param fieldName
     *            if field name is null or empty, it will return all related
     *            generic type from current class.
     * @return
     * @throws Exception
     */
    public static Map<String, Type[]> getGenericTypeOfField(Class clazz,
            String fieldName) throws Exception {

        if (null == clazz) {
            return null;
        }

        List<Field> fieldList = new ArrayList<Field>();
        // List<Class> typeList = new ArrayList<Class>();
        Map<String, Type[]> typeMap = new HashMap<String, Type[]>();

        if (CommonUtils.isNullOrEmpty(fieldName)) {
            CommonUtils.copyArrayToList(getAllJavaBeanFields(clazz), fieldList);
        } else {
            fieldList.add(clazz.getDeclaredField(fieldName));
        }

        for (Field f : fieldList) {
            Type t = f.getGenericType();

            if (null != t && (t instanceof ParameterizedType)) {
                ParameterizedType type = (ParameterizedType) t;

                Type[] actualTypeArguments = type.getActualTypeArguments();
                // CommonUtils.copyArrayToList(actualTypeArguments,
                // typeList);
                typeMap.put(clazz.getCanonicalName() + "." + f.getName(),
                        actualTypeArguments);
            }
        }

        return typeMap;
    }

    /**
     * Get all client generic class type in this class.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String, Class> getAllClientGenericTypeMap(Class clazz)
            throws Exception {

        if (null == clazz) {
            return null;
        }

        Map<String, Class> typeMap = new HashMap<String, Class>();
        Map<String, Type[]> map = getGenericTypeOfField(clazz, null);

        for (String key : map.keySet()) {
            Type[] actualTypeArguments = map.get(key);

            for (Type c : actualTypeArguments) {
                Class cc = eraseAsClass(c);
                if (cc != null && !cc.getCanonicalName().startsWith("java.")) {
                    typeMap.put(key, cc);
                }
            }
        }

        return typeMap;
    }

    /**
     * Erase Type to Class
     * 
     * @param type
     *            of
     *            ParameterizedType,TypeVariable.GenericArrayType,WildcardType
     * @return null if not valid, otherwise return Class.
     */
    public static Class eraseAsClass(Type type) {
        
        if(type == null){
            return null;
        }

        if (type instanceof Class) {
            return (Class) type;

        } else if (type instanceof ParameterizedType) {

            return (Class) ((ParameterizedType) type).getRawType();
        } else if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) type;

            if (tv.getBounds().length > 0) {
                return eraseAsClass(tv.getBounds()[0]);
            }

        } else if (type instanceof GenericArrayType) {

            GenericArrayType aType = (GenericArrayType) type;
            return Array.newInstance(
                    eraseAsClass(aType.getGenericComponentType()), 0)
                    .getClass();

        } else if (type instanceof WildcardType) {
            WildcardType tv = (WildcardType) type;
            if (tv.getUpperBounds().length > 0) {
                return eraseAsClass(tv.getUpperBounds()[0]);
            }

        } else {
            // TODO at least support CaptureType here
            throw new RuntimeException("not supported: " + type.getClass());
        }

        return null;
    }

}
