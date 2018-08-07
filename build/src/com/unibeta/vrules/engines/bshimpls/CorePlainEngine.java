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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.reflect.ObjectReflector;
import com.unibeta.vrules.tools.Java2vRules;
import com.unibeta.vrules.utils.CommonUtils;
import com.unibeta.vrules.utils.VRulesCommonUtils;

/**
 * <code>CoreEngine</code> is the core engine for validation.It provides the
 * plain service for atomic validation.
 * 
 * @author jordan.xue
 */
public class CorePlainEngine {

    /**
     * Validates the specified object by <code>entityId</code>.
     * 
     * @param object
     * @param fileName
     *            the full path name of rule configuration located.
     * @param entityId
     *            the unique id the every engity or element.
     * @return null if validate passed, otherwise return error messages.
     */
    public static String[] validate(Object object, String fileName,
            String entityId) throws Exception {

        List errorsList = Collections.synchronizedList(new ArrayList());

        File file = new File(fileName);
        if (!file.exists()) {
            Class targetClass = VRulesCommonUtils.getTargetClass(object);
            
            if (targetClass == null) {
                targetClass = object.getClass();
            }
            
            Java2vRules.digest(targetClass, fileName);
        }

        VRuleSuite rulesSuite = ConfigurationProxy.getVRuleSuiteInstance(
                fileName, null);// RulesParser.getEntityById(entityId);

        ObjectEntity objectEntity = (ObjectEntity) rulesSuite
                .getObjectEntities().get(entityId);

        if (null == objectEntity) {
            throw new Exception(
                    "The specified ["
                            + entityId
                            + "] element can not be found in vRules configuration file!");
        }

        if (null == object && !objectEntity.isNill()) {
            return new String[] { "The specified object is null for "
                    + entityId + "!" };
        }

        Map reflectValueMap = ObjectReflector.reflectValues(object,
                objectEntity);
        List rules = objectEntity.getRules();

        // extension fucntion logic
        if (null != objectEntity.getExtension()
                && objectEntity.getExtension().length() > 0) {

            String[] strs = validate(object, fileName,
                    objectEntity.getExtension());
            CommonUtils.copyArrayToList(strs, errorsList);
        }

        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();
            rule.setValues(generateValeMapById(rule, reflectValueMap));

            boolean valid = VRulesBshInterpreter.execute(rule,
                    rulesSuite.getDeclaredMethods());
            if (!valid) {
                if (rule.getErrorMessage() != null
                        && rule.getErrorMessage().length() > 0) {
                    errorsList.add(rule.getErrorMessage());
                } else {
                    errorsList.add(rule.getId() + " is invalid for " + entityId
                            + "!");
                }
            }

            // whether need recurse into the sub fields.
            if (needRecursionInto(rule)) {
                String[] strs = null;
                strs = validateRecursivelyInto(rule, fileName);

                CommonUtils.copyArrayToList(strs, errorsList);
            }
        }

        return (String[]) errorsList.toArray(new String[] {});
    }

    private static String[] validateRecursivelyInto(Rule rule, String fileName)
            throws Exception {

        String[] strs;
        if (rule.isMapOrList()) {
            strs = validateMapOrListElementsRecursively(rule, fileName);
        } else {
            /*
             * Go through the sub elements recursively.
             */
            strs = validate(rule.getValues().get(rule.getId()), fileName,
                    rule.getRefId());
        }
        return strs;
    }

    private static String[] validateMapOrListElementsRecursively(Rule rule,
            String fileName) throws Exception {

        Object obj = rule.getValues().get(rule.getId());

        if (null == obj) {
            return null;
        }

        List errList = new ArrayList();

        List objList = getRepeatedObjectList(obj);
        for (int i = 0; (objList != null && i < objList.size()); i++) {
            String[] errors = validate(objList.get(i), fileName,
                    rule.getRefId());
            addCommentsForMapOrListElementsError(rule, errors);
            CommonUtils.copyArrayToList(errors, errList);
        }

        return (String[]) errList.toArray(new String[] {});
    }

    private static void addCommentsForMapOrListElementsError(Rule rule,
            String[] errors) {

        for (int j = 0; errors != null && j < errors.length; j++) {

            errors[j] = errors[j] + "{In Map-or-List object of [id= "
                    + rule.getId() + ", refId = " + rule.getRefId() + "]}";
        }
    }

    private static List getRepeatedObjectList(Object obj) {

        List list = new ArrayList();

        if (obj instanceof Object[]) {

            Object[] objs = (Object[]) obj;
            CommonUtils.copyArrayToList(objs, list);

        } else if (CommonUtils.isInterfaceOf(obj, Map.class)) {

            Map map = (Map) obj;
            Set keys = map.keySet();
            for (Iterator i = keys.iterator(); i.hasNext();) {
                list.add(map.get(i.next()));
            }
        } else if (CommonUtils.isInterfaceOf(obj, List.class)) {
            List ls = (List) obj;

            for (Iterator i = ls.iterator(); i.hasNext();) {
                list.add(i.next());
            }
        }

        return list;
    }

    private static Map<String, Object> generateValeMapById(Rule rule,
            Map valueMap) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String paramaters[] = rule.getInputObjects().split(",");

        for (int i = 0; i < paramaters.length; i++) {

            String inputTrimVule = paramaters[i].trim();
            Object obj = valueMap.get(inputTrimVule);

            map.put(inputTrimVule, obj);
        }

        return map;
    }

    private static boolean needRecursionInto(Rule rule) {

        return rule.getRefId() != null && rule.getRefId().length() > 0
                && rule.isComplexType() && rule.isRecursive();
    }

}
