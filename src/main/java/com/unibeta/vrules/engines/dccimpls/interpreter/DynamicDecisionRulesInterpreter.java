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
package com.unibeta.vrules.engines.dccimpls.interpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.engines.dccimpls.RulesInterpreter;
import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>DynamicDecisionRulesInterpreter</code> is used for decision model to
 * interpreter the configurations. It is based on validation model's
 * version-2.2.4.
 * 
 * @author jordan.xue
 */
public class DynamicDecisionRulesInterpreter extends
        DynamicValidationRulesInterpreter implements RulesInterpreter {

    VRuleSuite ruleSuite = null;
    Map<String, ObjectEntity> entities = null;

    /**
     * Generates the java codes via the vRuels configuration. this function will
     * generates the codes and write the codes to file.
     * 
     * @param fileName
     *            the vRules configuration file full path name.
     * @return the java files full name.
     * @exception Exception
     */
    public String interprete(String fileName,Class decisionClass) throws Exception {

        ruleSuite = ConfigurationProxy.getVRuleSuiteInstance(fileName, decisionClass);
        entities = ruleSuite.getObjectEntities();

        super.entities = entities;
        super.ruleSuite = ruleSuite;

        String javaCodes = generateCodes(entities, fileName);

        return writeToFile(javaCodes, fileName);
    }

    void appendInterfaceMethod(StringBuffer codes, Map entities) {

        /*
         * public String[] validate(Map<String,Object> obj, String entityId)
         * throws Exception
         */
        codes.append("\n");
        codes.append("@SuppressWarnings(\"unchecked\")\n");
        codes
                .append("public String[] validate(Map<String,Object> obj) throws Exception ");
        codes.append("{\n");
        // codes.append(THIS_ROOT + " = obj; \n");
        codes.append(initDeclaredContextsValues(ruleSuite.getContexts(),
                VALIDATION_METHOD_TYPE_MAP));
        codes.append("List<String> errorList = new ArrayList<String>();\n");

        codes.append("Set set = obj.keySet();\n"
                + "for (Iterator i = set.iterator(); i.hasNext();) {\n"
                + "String id = (String)i.next();\n"
                + "String[] subErrors = validate((Object)obj.get(id),id);\n"
                + "\n if(null != subErrors){return subErrors;}" +
                // "copyArrayToList(subErrors,errorList);\n" +
                "" + "}");

        codes.append("");
        codes.append("return null;\n");

        codes.append("}\n");

        /*
         * public String[] validate(Object obj, String entityId) throws
         * Exception
         */
        codes.append("\n");
        codes.append("@SuppressWarnings(\"unchecked\")\n");
        codes
                .append("public String[] validate(Object obj, String entityId) throws Exception ");
        codes.append("{\n");
        // It never works for below line.No need this. never do that again.
        // codes.append(initDeclaredContextsValues(ruleSuite.getContexts(),VALIDATION_METHOD_TYPE_OBJECT));
        codes.append(THIS_ROOT + " = obj; \n");
        codes.append("String[] $errors = null;\n");
        appenChooseObjectLogic(codes, entities);
        codes.append("");
        codes.append("return null;\n");

        codes.append("}\n");

    }

    void appenChooseObjectLogic(StringBuffer codes, Map entities) {

        Set set = entities.keySet();

        Map<String, ObjectEntity> entityMap = new HashMap<String, ObjectEntity>();

        Collection<ObjectEntity> entityValues = entities.values();

        List<ObjectEntity> entityList = new ArrayList<ObjectEntity>();
        for (ObjectEntity o : entityValues) {
            entityList.add(o);
        }

        // Collections.sort(entityList); // no need sorting

        for (ObjectEntity entity : entityList) {

            codes.append("if(\"" + entity.getId() + "\".equals(entityId))");
            codes.append("{\n");
            codes.append("String $path$ = " + "\"" + entity.getName() + "\""
                    + ";\n");
            // codes.append("String $path$ = " +"\""+"\"" +";\n");
            codes.append(entity.getClassName() + " obj1 = ("
                    + entity.getClassName() + ") obj;\n");
            codes.append("$errors = new " + entity.getId() + "Validation"
                    + "().validate(obj1,$path$);\n");
            codes.append(" if(null != $errors){return $errors;}");
            codes.append("\n}\n");
        }
        codes.append("");

    }

    void appendInnerClassesBody(StringBuffer codes, ObjectEntity entity)
            throws Exception {

        codes.append("@SuppressWarnings(\"unchecked\")\n");
        codes.append("class " + entity.getId() + "Validation");
        appendExtensionsClass(codes, entity);
        codes.append("{\n\n");

        codes.append(entity.getClassName() + " " + THIS_ROOT + " = null; ");
        Map<String,String> types =  buildEntityRulesDataTypeToMap(entity);
        List rules = entity.getRules();

        // handleValueDateForRulesPredicate(entity, types);

        for (int i = 0; i < rules.size(); i++) {
            codes.append("\n");
            Rule rule = (Rule) rules.get(i);

            appendRuleMethod(codes, rule, types, entity);
        }

        appendObjectValidateMethod(codes, entity, types);
        codes.append("}\n\n");

    }

    void appendObjectValidateMethod(StringBuffer codes, ObjectEntity entity,
            Map types) {

        String parameterObj = "obj";

        codes.append("\n");
        codes.append("@SuppressWarnings(\"unchecked\") ");
        codes.append("public String[] validate(" + entity.getClassName() + " "
                + parameterObj + ",String $xPath$)");
        codes.append("{\n");
        codes.append(THIS_ROOT + " = " + parameterObj + ";\n");
       
        // codes.append("String $path$ = $xPath$+" + "\"/" + entity.getId() + "\""
        // + ";\n");
        codes.append("String $path$ = $xPath$" + ";\n");
        // codes.append("String $path$ = $xPath$+\"/\""+entity.getId()+";\n");
        codes.append("List errorList = new ArrayList();\n\n");


        if (!entity.isNill()) {
            codes.append("if(null == obj) ");
            codes.append("{\n");
            // codes.append("errorMsg = errorMsg +\"${xPath}:=\"+ $xPath$\"/;\n");

            String valueCode = "+\"\" + \" = null\"";
            if (!ruleSuite.getGlobalConfig().isReturnErrorValue()) {
                valueCode = "";
            }
            codes.append("errorList.add(\" " + entity.getId() + " is null! "
                    + " " + "${xPath}:=\"+ $xPath$" + valueCode + ");\n");
            codes
                    .append("return (String[])errorList.toArray(new String[]{});\n");
            codes.append("\n}\n");
        } else {
            codes.append("if(null == obj) ");
            codes.append("{\n");
            // codes.append("errorMsg = errorMsg +\"${xPath}:=\"+ $xPath$\"/;\n");
            // codes.append("errorList.add(\" " + entity.getId() + " is null! "
            // + " " + "${xPath}:=\"+ $xPath$+\"\");\n");
            codes.append("return null;\n");
            codes.append("\n}\n");
        }
        
        initSuperRootValue(codes, entity);
        
        List rules = entity.getRules();
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = (Rule) rules.get(i);

            String validationResult = "error" + rule.getId();
            codes.append("\n\n");
            codes.append("String[] " + validationResult + " = null;" + "\n");
            codes.append("boolean " + rule.getId() + "= true; \n");
            codes.append("try{\n");
            if (rule.getDepends().length() > 0) {
                codes.append("if(" + rule.getDepends() + " ){\n");
                appendValidationProcess(entity.getClassName(),codes, rule, types);
                codes.append("}\n");
            } else {
                appendValidationProcess(entity.getClassName(),codes, rule, types);

            }
            codes.append("}catch(Exception e){\n");
//            codes.append("errorList.add(\"" + entity.getId()
//                    + " validated failed caused by \" +e.toString() +  \", "
//                    + " " + "${xPath}:=\"+ $xPath$" + ");\n");
            codes.append("e.printStackTrace();\n");
            
            codes.append(" return new String[]{\""+ entity.getId()
                    + " decision process failed caused by \" +e.toString() +  \", "
                    + " " + "${xPath}:=\"+ $xPath$" + "};");
            
            codes.append("}\n");
            codes.append(rule.getId() + " = (null == " + validationResult
                    + ")? true:false;\n");
        }

        codes.append("return (String[])errorList.toArray(new String[]{});\n");

        codes.append("\n}\n");
    }
    private void initSuperRootValue(StringBuffer codes, ObjectEntity entity) {

        // if it is a subclass, super object and interface will not involve the decision process directly.
        if (null != entity.getExtension()
                && entity.getExtension().trim().length() > 0) {

            String[] extensions = entity.getExtension().split(",");
            for (String s : extensions) {
                String ext = s.trim();
                if (ext.length() > 0) {
                    
                    codes.append("if(null == super.root && "
                            + entities.get(ext).getClassName()
                            + ".class.isAssignableFrom(root.getClass()) ){\n"
                            + "super.root = ("
                            + entities.get(ext).getClassName() + ")this.root;\n"
                            + "}\n");
                    break;

                }
            }
        }
    }

    void validateObject(StringBuffer codes, Rule rule, String parameterValues) {

        String objectId = rule.getId();

        String errors = "error" + objectId;
        // codes.append("String[] " + errors + " = null;" + "\n");

        codes.append(errors + " = " + "validate"
                + CommonUtils.generateClassNameByFileFullName(objectId) + "("
                + parameterValues
                + (parameterValues.trim().length() > 0 ? ",$path$" : "$path$")
                + ");\n");
        codes.append("if( null != " + errors + ")");
        codes.append("{\n");
        codes.append(" return " + errors + ";\n");
        codes.append("}\n");
    }

    void appendRuleMethod(StringBuffer codes, Rule rule, Map types,
            ObjectEntity entity) throws Exception {

        codes.append("@SuppressWarnings(\"unchecked\")\n");
        String generateParametes = generateParametes(rule, types);
        codes.append("  String[] validate"
                + CommonUtils.generateClassNameByFileFullName(rule.getId())
                + "("
                + generateParametes
                + ((null != generateParametes && generateParametes.trim()
                        .length() > 0) ? ",String $xPath$" : "String $xPath$")
                + ")");
        codes.append("{\n\n");
        
        initSuperRootValue(codes, entity);
        
        codes.append("String $path$ = $xPath$;\n");
        codes.append("boolean " + rule.getOutputBool() + "= true;\n\n");
        codes.append("List $errors = new ArrayList();\n");
        codes
                .append("String " +errorMsg +"= \""
                        + handleErrorMessage(rule.getErrorMessage().trim())
                        + "\";\n\n");

        String predicate = rule.getPredicate();

        codes.append("try{" + "\n");
        codes.append(predicate + "\n");
        codes.append("}catch(Exception e){" + "\n");
        codes.append(rule.getOutputBool() + "= false;\n\n");
        codes
                .append("" +errorMsg +"= " +errorMsg +" +\", Exception is caused by \" +e.toString();\n");
        codes.append("e.printStackTrace();\n");
        codes.append("}");

        // codes.append("if(" + rule.getOutputBool() + "== false" + ")");
        // codes.append("{\n");

        String valueCode = "";
        if (ruleSuite.getGlobalConfig().isReturnErrorValue()) {
            valueCode = generateInputValuesOutputCode(rule.getInputObjects());
        }

        appendBshEval(codes, rule);
        
        codes.append("" +errorMsg +" = " +errorMsg +" " + "+\" ${xPath}:=\"+ $xPath$+\"/"
                + rule.getName() + "\" "

                + valueCode

                + ";\n");

        // codes.append("errors.add(errorMsg);\n");

//         if (rule.isMapOrList()) {
//             codes.append(" if("+rule.getOutputBool()+"==false){");
//         codes.append("return (String[])errors.toArray(new String[]{});");
//         }
        // codes.append("\n}\n");

        appendDecisionLogicProcess(codes, rule, types, entity);
        
        String[] pamarates = rule.getInputObjects().split(",");
        // recursive into
        if ((CommonUtils.isNullOrEmpty(rule.getIfTrue()) && CommonUtils
                .isNullOrEmpty(rule.getIfTrue()))
                && pamarates.length <= 1
                && rule.isComplexType()
                && rule.isRecursive() && rule.getRefId().length() > 0) {

            if (!rule.isMapOrList()) {
                codes.append("$path$ = $xPath$" + "+\"" + "/" + rule.getName()
                        + "\"" + ";\n");
                
                String inputs = null;
                if(null ==rule.getInputObjects() ||rule.getInputObjects().trim().length()==0){
                    inputs = "null,";
                }else{
                    inputs = rule.getInputObjects() + ",";
                }
                
                codes.append("String[] errMsgs = new " + rule.getRefId()
                        + "Validation().validate(" + inputs
                        + " $path$);\n");
                codes.append("if(null != errMsgs){" + "return errMsgs;" + "}");
                // codes.append("copyArrayToList(errMsgs,errors);\n");
            } else {

                String type = getReferIdClassName(rule.getRefId());
                codes.append("List objList = getRepeatedObjectList("
                        + rule.getInputObjects() + ");\n");
                codes.append("for(int i = 0;"+rule.getOutputBool()+"&&i< objList.size();i++)");
                codes.append("{\n");
                codes.append("String myPath = $xPath$" + "+\"" + "/");
                if (!rule.judgeNameContainsNeedDisplayCollectionNameFlag()) {
                    codes.append(rule.getName() + "/\"+" + "\"");
                }
                codes.append(getObjectEntityNameById(rule.getRefId())
                        + "\"+\"[\"+(i+1)+\"]\"" + ";\n");
                codes.append(type + " element = (" + type + ")"
                        + "objList.get(i);\n");
                codes.append("String[] errMsgs = new " + rule.getRefId()
                        + "Validation().validate(element,myPath);\n");
                codes.append("if(null != errMsgs){" + "return errMsgs;" + "}");
                // codes.append("copyArrayToList(errMsgs,errors);\n");
                codes.append("\n}\n");
            }
        }

        // codes.append("if(errors.size()==0){return null;}\n");
        // codes.append("return (String[])errors.toArray(new String[]{});");

        // codes.append("return new String[]{errorMsg}; ");
        // codes.append(" return null;\n");
//        codes.append("System.out.println(\" the rule " + rule.getId()
//                + " result is: \" + " + rule.getOutputBool() + ");");
       

        // codes.append(" return null;\n");
        codes.append(" return new String[]{" +errorMsg +"};");
        codes.append("\n}\n");

    }

    private void appendDecisionLogicProcess(StringBuffer codes, Rule rule,
            Map types, ObjectEntity entity) {

        
        Rule ifTrue = getRuleByid(entity, rule
                .getIfTrue());
        Rule ifFalse = getRuleByid(entity, rule.getIfFalse());

        if (!CommonUtils.isNullOrEmpty(rule.getIfTrue())
                || !CommonUtils.isNullOrEmpty(rule.getIfFalse())) {
            codes.append("try{" + "\n");

            // String errors = "error";
            if ( rule.getIfTrue().trim().length() > 0) {
                String generateParameterValue = generateParameterValue(ifTrue,
                        types);
                codes.append(" if(" + rule.getOutputBool() + "==true){");
                
                gotoNextRule(codes, generateParameterValue, rule.getIfTrue());
                
                codes.append("}" +
                		"}");
            }
            if (rule.getIfFalse().trim().length() > 0) {
                String generateParameterValue = generateParameterValue(ifFalse,
                        types);
                codes.append(" if(" + rule.getOutputBool() + "==false){");
                gotoNextRule(codes, generateParameterValue, rule.getIfFalse());
                codes.append("}" +
                		"}");

            }
            codes.append("}catch(Exception e){" + "\n");
            codes.append(rule.getOutputBool() + "= false;\n\n");
            codes
                    .append("" +errorMsg +"= " +errorMsg +" +\", Exception is caused by \" +e.toString();\n");
            codes.append("e.printStackTrace();\n");
            codes.append("return new String[]{" +errorMsg +"};");
            codes.append("}");
            
//            codes.append(" return new String[]{errorMsg};");
        } else {

//            codes.append(" return new String[]{errorMsg};");
        }
        // {
        // codes.append(" if(" + rule.getOutputBool() + "==false){" +
        // " return new String[]{errorMsg};" +
        // "}");
        // // codes.append(" return new String[]{errorMsg};");
        // }
    }

    private void gotoNextRule(StringBuffer codes,
            String generateParameterValue, String nextRule) {

        codes.append(" String[] tempErr =  "
                + "  validate"
                + CommonUtils.generateClassNameByFileFullName(nextRule)
                + "("
                + generateParameterValue
                + (generateParameterValue.trim().length() > 0 ? ",$path$"
                        : "$path$") + ");\n");
        codes
                .append(" if(null != tempErr){" + " return tempErr;"
                        + "");
    }

    private Rule getRuleByid(ObjectEntity entity, String id) {

        for (Rule r : entity.getRules()) {
            if (id.equals(r.getId())) {
                return r;
            }
        }
        
        if(!CommonUtils.isNullOrEmpty(entity.getExtension())){
            String[] exts = entity.getExtension().split(",");
            for(String s: exts){
                ObjectEntity o= entities.get(s.trim());
                
                if(null != o){
                 Rule r= getRuleByid(o, id);
                 if(null != r){
                     return r;
                 }
                }
            }
        }
        
        return null;
    }

}
