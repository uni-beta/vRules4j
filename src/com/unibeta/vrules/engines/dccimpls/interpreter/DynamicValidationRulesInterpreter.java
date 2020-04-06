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

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.Rule.Binding;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.base.VRules4jContext;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.engines.dccimpls.RulesInterpreter;
import com.unibeta.vrules.engines.dccimpls.rules.RulesValidation;
import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.reflect.ObjectReflector;
import com.unibeta.vrules.tools.CommonSyntaxs;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>DynamicValidationRulesInterpreter</code> takes responsibility for
 * generating the validation java codes according to the vRules configuration.
 * 
 * @author jordan.xue
 * @version 1.0.1-Added getValueFromCollectionByContext() for context value
 *          initialization.
 * @modified 2008-06-03 22:20
 */
public class DynamicValidationRulesInterpreter implements RulesInterpreter {

	private static final String CORE_ENGINE_VERSION = "1.0.0.b201705221255";
	private static final String CURRENT_RULE_ID_LIST = "$ruleIdList$";
	// private static final String CURRENT_RULE_SET= "$ruleset$";
	VRuleSuite ruleSuite = null;
	Map<String, ObjectEntity> entities = null;

	String errorMsg = "$errorMsg$" + System.currentTimeMillis();

	/**
	 * Generates the java codes via the vRuels configuration. this function will
	 * generates the codes and write the codes to file.
	 * 
	 * @param fileName
	 *            the vRules configuration file full path name.
	 * @return the java files full name.
	 * @exception Exception
	 */
	public String interprete(String fileName, Class serializedClass) throws Exception {

		ruleSuite = ConfigurationProxy.getVRuleSuiteInstance(fileName, serializedClass);
		entities = ruleSuite.getObjectEntities();

		String javaCodes = generateCodes(entities, fileName);

		return writeToFile(javaCodes, fileName);
	}

	String generateCodes(Map<String, ObjectEntity> entities, String fileName) throws Exception {

		StringBuffer codes = new StringBuffer();
		appendPackage(codes);
		appendImports(codes);

		appendComments(codes);
		appendPublicClassHead(codes, fileName);
		// Class start
		codes.append("{\n");
		codes.append(generateAllDeclaredVars(entities));
		codes.append(generateDeclaredContexts(ruleSuite.getContexts()));
		codes.append(" Object " + THIS_ROOT + " = null; \n");
		// codes.append(" Map<String, List<String>> " + this.CURRENT_RULE_SET +
		// " = null; \n");
		codes.append(" List<String> " + this.CURRENT_RULE_ID_LIST + " = null; \n");
		appendDeclaredMethods(codes);
		appendInterfaceMethod(codes, entities);
		appendInnerClasses(codes, entities);
		// class end
		codes.append("\n}\n");

		return codes.toString();
	}

	Object generateAllDeclaredVars(Map<String, ObjectEntity> map) throws Exception {

		StringBuffer sb = new StringBuffer();
		List<String> vars = new ArrayList<String>();

		for (ObjectEntity o : map.values()) {
			for (Rule r : o.getRules()) {
				String var = r.getBinding().getVar();
				if (null != r.getBinding() && !CommonUtils.isNullOrEmpty(var)) {
					if (!vars.contains(var)) {
						vars.add(var);
						sb.append(" Object " + var + "= null;\n");
					} else {
						// throw new Exception("var name [" + var
						// + "] is duplicated in Rule[id=" + r.getId()
						// + "] of Object[id=" + o.getId() + "]");
					}
				}
			}
		}

		return sb.toString();
	}

	String generateDeclaredContexts(List<VRules4jContext> contexts) {

		StringBuffer sb = new StringBuffer();
		for (VRules4jContext context : contexts) {

			// sb.append(context.getClassName() + " " + context.getName()
			// + "= null; \n");
			sb.append(context.getClassName() + " " + context.getName() + "; \n");
		}

		if (InterpreterUtils.hasBsh()) {
			sb.append("bsh.Interpreter $bsh = getBsh();\n");
		}

		return sb.toString();
	}

	void appendDeclaredMethods(StringBuffer codes) {

		String methods = ruleSuite.getDeclaredMethods().trim();
		codes.append(methods);
		codes.append("\n");

		// // void copyArrayToList(Object[] objs, List list)
		// codes.append("@SuppressWarnings(\"unchecked\") ");
		// codes.append(" private void copyArrayToList(Object[] objs, List list) {\n");
		// codes.append("if (null == objs) {return;}");
		// codes.append("if (null == list) { list = new ArrayList();}");
		// codes.append("for (int i = 0; i < objs.length; i++) ");
		// codes.append("{ list.add(objs[i]); }");
		// codes.append("}\n");
		//
		// // private boolean isInterfaceOf(Object obj, Class clazz)
		// codes.append("@SuppressWarnings(\"unchecked\") ");
		// codes.append("private boolean isInterfaceOf(Object obj, Class clazz) {\n");
		// codes.append("if (null == obj) {return false;}");
		// codes.append(" List list = new ArrayList();\n");
		// codes.append("copyArrayToList(obj.getClass().getInterfaces(), list);\n");
		// codes.append("if (list.contains(clazz)) {return true;}\n");
		// codes.append(" return false;}\n");
		//
		// // private List getRepeatedObjectList(Object obj)
		// codes.append("@SuppressWarnings(\"unchecked\") ");
		// codes.append("private List getRepeatedObjectList(Object obj) {\n");
		// codes.append(" List list = new ArrayList();\n");
		// codes.append("if (obj instanceof Object[]) {\n Object[] objs = (Object[])
		// obj;copyArrayToList(objs, list);");
		// codes.append("} else if (isInterfaceOf(obj, Map.class)) {Map map = (Map) obj;
		// Set keys = map.keySet();");
		// codes.append(" for (Iterator i = keys.iterator(); i.hasNext();) {
		// list.add(map.get(i.next()));}");
		// codes.append("} else if (isInterfaceOf(obj, List.class)) {\n");
		// codes.append(" List ls = (List) obj;");
		// codes.append(" for (Iterator i = ls.iterator(); i.hasNext();) {
		// list.add(i.next());}}\n");
		// codes.append("return list;}");

	}

	void appendInnerClasses(StringBuffer codes, Map entities) throws Exception {

		Set sets = entities.keySet();
		for (Iterator i = sets.iterator(); i.hasNext();) {

			ObjectEntity entity = (ObjectEntity) entities.get(i.next());

			appendInnerClassesBody(codes, entity);
		}

	}

	void appendInnerClassesBody(StringBuffer codes, ObjectEntity entity) throws Exception {

		codes.append("@SuppressWarnings(\"unchecked\")\n");
		codes.append("class " + entity.getId() + "Validation");

		appendExtensionsClass(codes, entity);

		codes.append("{\n\n");
		codes.append(getValidClassName(entity.getClassName()) + " " + THIS_ROOT + " = null; ");

		Map<String, String> types = buildEntityRulesDataTypeToMap(entity);

		List rules = entity.getRules();

		// handleValueDateForRulesPredicate(entity, types);

		Class clz = Class.forName(entity.getClassName());
		for (int i = 0; i < rules.size(); i++) {
			codes.append("\n");
			Rule rule = (Rule) rules.get(i);

			if (Map.class.isAssignableFrom(clz)) {
				appendRuleMethodForMap(codes, rule, types, entity.getClassName());
			} else {
				appendRuleMethod(codes, rule, types, entity.getClassName());
			}

		}

		appendObjectValidateMethod(codes, entity, types);
		codes.append("}\n\n");

	}

	private void appendRuleMethodForMap(StringBuffer codes, Rule rule, Map<String, String> types, String className)
			throws Exception, Exception {

		codes.append("@SuppressWarnings(\"unchecked\")\n");
		String generateParametes = generateParametesForMap(rule);

		codes.append(" String[] validate" + CommonUtils.generateClassNameByFileFullName(rule.getId()) + "("
				+ generateParametes
				+ ((null != generateParametes && generateParametes.trim().length() > 0) ? ",String $xPath$"
						: "String $xPath$")
				+ ")");
		codes.append("{\n\n");
		codes.append("String $path$ = $xPath$;\n");
		codes.append("boolean " + rule.getOutputBool() + "= true;\n\n");
		codes.append("List $errors = new ArrayList();\n");
		codes.append("String " + errorMsg + "= \"" + handleErrorMessage(rule.getErrorMessage().trim()) + "\";\n\n");

		appendNewInstanceIfNullForRuleMethod(codes, rule, types, className);
		String predicate = rule.getPredicate();

		codes.append("try{" + "\n");

		if (!CommonUtils.isNullOrEmpty(rule.getInputObjects())) {
			String[] fields = rule.getInputObjects().split(",");
			Class clz = Class.forName(className);

			for (int i = 0; i < fields.length && !CommonUtils.isNullOrEmpty(fields[i]); i++) {

				String fName = fields[i];
				codes.append(" " + fName + " = root.get(\"" + fName + "\");\n");

			}

			codes.append(predicate + "\n");

			for (int i = 0; i < fields.length && !CommonUtils.isNullOrEmpty(fields[i]); i++) {
				String fName = fields[i];

				codes.append("root.put(\"" + fName + "\"," + fName + ");\n");
			}
		}

		codes.append("}catch(Exception e){" + "\n");
		codes.append(rule.getOutputBool() + "= false;\n\n");
		codes.append("" + errorMsg + "= " + errorMsg + " +\", Exception is caused by \" +e.toString();\n");
		codes.append("e.printStackTrace();\n");
		codes.append("}");

		codes.append("if(" + rule.getOutputBool() + "== false" + ")");
		codes.append("{\n");

		String valueCode = "";
		if (ruleSuite.getGlobalConfig().isReturnErrorValue()) {
			valueCode = generateInputValuesOutputCode(rule.getInputObjects());
		}

		if (InterpreterUtils.hasBsh()) {
			appendBshEval(codes, rule);
		} else {
			// appendPlainEval(codes, rule);
		}

		codes.append("" + errorMsg + " = " + errorMsg + " " + "+\" ${xPath}:=\"+ $xPath$+\"/" + rule.getName() + "\" "

				+ valueCode

				+ ";\n");

		codes.append("$errors.add(" + errorMsg + ");\n");

		if (rule.isMapOrList()) {
			codes.append("return (String[])$errors.toArray(new String[]{});");
		}
		codes.append("\n}\n");

		String[] pamarates = rule.getInputObjects().split(",");
		// recursive into
		if (pamarates.length <= 1 && rule.isComplexType() && rule.isRecursive() && rule.getRefId().length() > 0) {

			if (!rule.isMapOrList()) {
				codes.append("$path$ = $xPath$" + "+\"" + "/" + rule.getName() + "\"" + ";\n");

				String inputs = null;
				if (null == rule.getInputObjects() || rule.getInputObjects().trim().length() == 0) {
					inputs = "null,";
				} else {
					inputs = rule.getInputObjects() + ",";
				}

				codes.append("String[] errMsgs = new " + rule.getRefId() + "Validation().validate(" + inputs
						+ " $path$);\n");
				codes.append("copyArrayToList(errMsgs,$errors);\n");
			} else {

				String type = getReferIdClassName(rule.getRefId());

				codes.append("List objList = getRepeatedObjectList(" + rule.getInputObjects() + ");\n");

				Class clz = Class.forName(type);
				boolean isMap = Map.class.isAssignableFrom(clz);

				if (!isMap) {
					codes.append("for(int i = 0;i< objList.size();i++)");
				}

				codes.append("{\n");

				if (!isMap) {
					codes.append("String myPath = $xPath$" + "+\"" + "/");
					if (!rule.judgeNameContainsNeedDisplayCollectionNameFlag()) {
						codes.append(rule.getName() + "/\"+" + "\"");
					}
					codes.append(getObjectEntityNameById(rule.getRefId()) + "\"+\"[\"+(i+1)+\"]\"" + ";\n");
					codes.append(type + " element = (" + type + ")" + "objList.get(i);\n");
				} else {
					codes.append("String myPath = $xPath$" + "+\"" + "/");
					if (!rule.judgeNameContainsNeedDisplayCollectionNameFlag()) {
						codes.append(rule.getName() + "\";\n");
					}
					// codes.append(getObjectEntityNameById(rule.getRefId())
					// + "\"+\"[\"+(i+1)+\"]\"" + ";\n");
					codes.append(type + " element = (" + type + ")" + rule.getInputObjects() + ";\n");
				}

				codes.append("String[] errMsgs = new " + rule.getRefId() + "Validation().validate(element,myPath);\n");
				codes.append("copyArrayToList(errMsgs,$errors);\n");
				codes.append("\n}\n");
			}
		}

		codes.append("if($errors.size()==0){return null;}\n");
		codes.append("return (String[])$errors.toArray(new String[]{});");

		codes.append("\n}\n");

	}

	Map<String, String> buildEntityRulesDataTypeToMap(ObjectEntity entity) throws Exception {

		Map<String, String> types = new HashMap<String, String>();
		if (!CommonUtils.isNullOrEmpty(entity.getExtension())) {

			String[] exts = entity.getExtension().split(",");
			for (String ext : exts) {
				if (!CommonUtils.isNullOrEmpty(ext)) {
					ObjectEntity o = entities.get(ext.trim());
					if (null == o) {
						throw new Exception(" object[" + entity.getId() + "] extensions[" + ext.trim()
								+ "] reference id configuration can not be found in the vRules config file!");
					}
					types.putAll(ObjectReflector.reflectTypes(o));
				}
			}
		}

		types.putAll(ObjectReflector.reflectTypes(entity));

		return types;
	}

	void appendExtensionsClass(StringBuffer codes, ObjectEntity entity) {

		if (!CommonUtils.isNullOrEmpty(entity.getExtension())) {
			String[] exts = entity.getExtension().split(",");
			if (!CommonUtils.isNullOrEmpty(exts[0])) {
				ObjectEntity o = entities.get(exts[0]);

				if (null != o) {
					codes.append(" extends " + o.getId() + "Validation ");
				}
			}
		}
	}

	void appendObjectValidateMethod(StringBuffer codes, ObjectEntity entity, Map types) {

		String parameterObj = "obj";

		codes.append("\n");
		codes.append("@SuppressWarnings(\"unchecked\") ");
		String className = getValidClassName(entity.getClassName());
		codes.append("public String[] validate(" + className + " " + parameterObj + ",String $xPath$)");
		codes.append("{\n");

		// appendNewInstanceIfNullForObject(codes, className);

		codes.append(THIS_ROOT + " = " + parameterObj + ";\n");

		if (InterpreterUtils.hasBsh()) {
			codes.append(" bshSet($bsh,\"" + THIS_ROOT + "\"," + parameterObj + ");\n");
		}
		// codes.append("String $path$ = $xPath$+" + "\"/" + entity.getId() +
		// "\""
		// + ";\n");
		codes.append("String $path$ = $xPath$" + ";\n");
		// codes.append("String $path$ = $xPath$+\"/\""+entity.getId()+";\n");
		codes.append("List errorList = new ArrayList();\n\n");

		// if it is a subclass, validate the super rules first.
		if (null != entity.getExtension() && entity.getExtension().trim().length() > 0) {

			String[] extensions = entity.getExtension().split(",");
			for (String s : extensions) {
				String ext = s.trim();
				if (ext.length() > 0) {
					codes.append("String[] errMsgs" + ext + " = new " + ext + "Validation().validate(" + THIS_ROOT
							+ ",$path$);\n");
					codes.append("copyArrayToList(errMsgs" + ext + ",errorList);\n");
				}
			}
		}

		if (!entity.isNill() && !ruleSuite.getGlobalConfig().isEnableBinding()) {
			codes.append("if(null == obj) ");
			codes.append("{\n");
			// codes.append("errorMsg = errorMsg +\"${xPath}:=\"+ $xPath$\"/;\n");

			String valueCode = "+\"\" + \" = null\"";
			if (!ruleSuite.getGlobalConfig().isReturnErrorValue()) {
				valueCode = "";
			}
			codes.append("errorList.add(\"" + entity.getId() + " is null! " + " " + "${xPath}:=\"+ $xPath$" + valueCode
					+ ");\n");
			codes.append("return (String[])errorList.toArray(new String[]{});\n");
			codes.append("\n}\n");
		} else {
			codes.append("if(null == obj) ");
			codes.append("{\n");
			// codes.append("errorMsg = errorMsg +\"${xPath}:=\"+ xPath\"/;\n");
			// codes.append("errorList.add(\" " + entity.getId() + " is null! "
			// + " " + "${xPath}:=\"+ $xPath$+\"\");\n");
			codes.append("return null;\n");
			codes.append("\n}\n");
		}

		List rules = entity.getRules();
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) rules.get(i);

			codes.append("boolean " + rule.getId() + "= true; \n");

			codes.append("if(" + CURRENT_RULE_ID_LIST + "==null" + "||" + this.CURRENT_RULE_ID_LIST + ".contains(\""
					+ rule.getId() + "\"))");
			codes.append("{\n");

			String validationResult = "error" + rule.getId();
			codes.append("\n\n");
			codes.append("String[] " + validationResult + " = null;" + "\n");

			codes.append("try{\n");
			if (rule.getDepends().length() > 0) {
				codes.append("if(" + rule.getDepends() + " ){\n");
				appendValidationProcess(entity.getClassName(), codes, rule, types);
				codes.append("}\n");
			} else {
				appendValidationProcess(entity.getClassName(), codes, rule, types);

			}
			codes.append("}catch(Exception e){\n");
			codes.append("errorList.add(\"" + entity.getId() + " validated failed caused by \" +e.toString() +  \", "
					+ " " + "${xPath}:=\"+ $xPath$" + ");\n");
			codes.append("e.printStackTrace();\n");
			codes.append("}\n");
			codes.append(rule.getId() + " = (null == " + validationResult + ")? true:false;\n");

			codes.append("}\n");
		}

		codes.append("return (String[])errorList.toArray(new String[]{});\n");

		codes.append("\n}\n");
	}

	void appendNewInstanceIfNullForObject(StringBuffer codes, String className) {

		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ruleSuite.getGlobalConfig().isEnableBinding() && c != null) {
			codes.append("if(null == obj) ");
			codes.append("{\n");

			if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
				try {
					if (hasNoneArgConstructor(c)) {
						// codes.append("errorMsg = errorMsg +\"${xPath}:=\"+ $xPath$\"/;\n");
						codes.append("obj = new " + className + "();\n");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			} else if (List.class.isAssignableFrom(c) || Collection.class.isAssignableFrom(c)) {
				codes.append("obj = new " + ArrayList.class.getCanonicalName() + "();\n");
			} else if (Map.class.isAssignableFrom(c)) {
				codes.append("obj = new " + LinkedHashMap.class.getCanonicalName() + "();\n");
			}

			codes.append("\n}\n");
		}
	}

	void appendValidationProcess(String clazz, StringBuffer codes, Rule rule, Map types) {

		Class cc = null;
		String generateParameterValue = "";
		try {
			cc = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != cc) {
			if (Map.class.isAssignableFrom(cc)) {
				generateParameterValue = generateParameterValueForMap(rule, types);
			} else {
				generateParameterValue = generateParameterValue(rule, types);
			}

			validateObject(codes, rule, generateParameterValue);
		}
	}

	void validateObject(StringBuffer codes, Rule rule, String parameterValues) {

		String objectId = rule.getId();

		String errors = "error" + objectId;
		// codes.append("String[] " + errors + " = null;" + "\n");

		codes.append(errors + " = " + "validate" + CommonUtils.generateClassNameByFileFullName(objectId) + "("
				+ parameterValues + (parameterValues.trim().length() > 0 ? ",$path$" : "$path$") + ");\n");
		codes.append("if( null != " + errors + ")");
		codes.append("{\n");
		codes.append("copyArrayToList(" + errors + ",errorList);\n");
		codes.append("\n}\n");
		codes.append("\n if(" + (rule.isBreakpoint() || ruleSuite.getGlobalConfig().isToggleBreakpoint())
				+ " && null != " + errors + "){\n");
		codes.append("return (String[])errorList.toArray(new String[]{});\n");
		codes.append("}\n");
	}

	void appendRuleMethod(StringBuffer codes, Rule rule, Map<String, String> types, String className) throws Exception {

		codes.append("@SuppressWarnings(\"unchecked\")\n");
		String generateParametes = generateParametes(rule, types);
		codes.append(" String[] validate" + CommonUtils.generateClassNameByFileFullName(rule.getId()) + "("
				+ generateParametes
				+ ((null != generateParametes && generateParametes.trim().length() > 0) ? ",String $xPath$"
						: "String $xPath$")
				+ ")");
		codes.append("{\n\n");
		codes.append("String $path$ = $xPath$;\n");
		codes.append("boolean " + rule.getOutputBool() + "= true;\n\n");
		codes.append("List $errors = new ArrayList();\n");
		codes.append("String " + errorMsg + "= \"" + handleErrorMessage(rule.getErrorMessage().trim()) + "\";\n\n");

		appendNewInstanceIfNullForRuleMethod(codes, rule, types, className);

		String predicate = rule.getPredicate();

		codes.append("try{" + "\n");
		codes.append(predicate + "\n");

		if (!CommonUtils.isNullOrEmpty(rule.getInputObjects())) {
			String[] fields = rule.getInputObjects().split(",");
			Class clz = Class.forName(className);

			for (int i = 0; i < fields.length && !CommonUtils.isNullOrEmpty(fields[i]); i++) {
				String fName = fields[i].substring(0, 1).toUpperCase() + fields[i].substring(1);

				if (null != getSetterMethod(clz, fName)) {
					codes.append("root.set" + fName + "(" + fields[i] + ");\n");
				}
			}
		}

		codes.append("}catch(Exception e){" + "\n");
		codes.append(rule.getOutputBool() + "= false;\n\n");
		codes.append("" + errorMsg + "= " + errorMsg + " +\", Exception is caused by \" +e.toString();\n");
		codes.append("e.printStackTrace();\n");
		codes.append("}");

		codes.append("if(" + rule.getOutputBool() + "== false" + ")");
		codes.append("{\n");

		String valueCode = "";
		if (ruleSuite.getGlobalConfig().isReturnErrorValue()) {
			valueCode = generateInputValuesOutputCode(rule.getInputObjects());
		}

		if (InterpreterUtils.hasBsh()) {
			appendBshEval(codes, rule);
		} else {
			// appendPlainEval(codes, rule);
		}

		codes.append("" + errorMsg + " = " + errorMsg + " " + "+\" ${xPath}:=\"+ $xPath$+\"/" + rule.getName() + "\" "

				+ valueCode

				+ ";\n");

		codes.append("$errors.add(" + errorMsg + ");\n");

		if (rule.isMapOrList()) {
			codes.append("return (String[])$errors.toArray(new String[]{});");
		}
		codes.append("\n}\n");

		String[] pamarates = rule.getInputObjects().split(",");
		// recursive into
		if (pamarates.length <= 1 && rule.isComplexType() && rule.isRecursive() && rule.getRefId().length() > 0) {

			if (!rule.isMapOrList()) {
				codes.append("$path$ = $xPath$" + "+\"" + "/" + rule.getName() + "\"" + ";\n");

				String inputs = null;
				if (null == rule.getInputObjects() || rule.getInputObjects().trim().length() == 0) {
					inputs = "null,";
				} else {
					inputs = rule.getInputObjects() + ",";
				}

				codes.append("String[] errMsgs = new " + rule.getRefId() + "Validation().validate(" + inputs
						+ " $path$);\n");
				codes.append("copyArrayToList(errMsgs,$errors);\n");
			} else {

				String type = getReferIdClassName(rule.getRefId());
				codes.append("List objList = getRepeatedObjectList(" + rule.getInputObjects() + ");\n");
				codes.append("for(int i = 0;i< objList.size();i++)");
				codes.append("{\n");
				codes.append("String myPath = $xPath$" + "+\"" + "/");
				if (!rule.judgeNameContainsNeedDisplayCollectionNameFlag()) {
					codes.append(rule.getName() + "/\"+" + "\"");
				}
				codes.append(getObjectEntityNameById(rule.getRefId()) + "\"+\"[\"+(i+1)+\"]\"" + ";\n");
				codes.append(type + " element = (" + type + ")" + "objList.get(i);\n");
				codes.append("String[] errMsgs = new " + rule.getRefId() + "Validation().validate(element,myPath);\n");
				codes.append("copyArrayToList(errMsgs,$errors);\n");
				codes.append("\n}\n");
			}
		}

		codes.append("if($errors.size()==0){return null;}\n");
		codes.append("return (String[])$errors.toArray(new String[]{});");

		codes.append("\n}\n");

	}

	Method getSetterMethod(Class clz, String fName) {

		Field[] fs = clz.getDeclaredFields();
		for (Field f : fs) {
			Method m = null;
			try {
				m = clz.getMethod("set" + fName, f.getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}

			if (null != m) {
				return m;
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	void appendNewInstanceIfNullForRuleMethod(StringBuffer codes, Rule rule, Map<String, String> types,
			String className) throws NoSuchMethodException, Exception {

		Class rootClassName = getClazz(className);

		if (ruleSuite.getGlobalConfig().isEnableBinding() && !CommonUtils.isNullOrEmpty(rule.getInputObjects())
				&& rule.getInputObjects().split(",").length > 0) {

			String[] prams = rule.getInputObjects().split(",");
			for (String fieldName : prams) {
				String t = types.get(fieldName);

				Class fieldClassType = getClazz(t);
				if (fieldClassType == null) {
					fieldClassType = getBindingClass(rule);
				}

				if (fieldClassType != null) {
					if (!fieldClassType.isInterface() && !Modifier.isAbstract(fieldClassType.getModifiers())
							&& null != rule.getBinding().getWirings() && rule.isComplexType()) {

						appendInstanceForCommonClass(codes, rule, rootClassName, fieldName, fieldClassType);

					} else if (List.class.isAssignableFrom(fieldClassType)
							|| Collection.class.isAssignableFrom(fieldClassType)) {

						codes.append("if (null ==" + fieldName + "||" + fieldName + ".size()==0){\n");

						Class class1 = null;

						if (fieldClassType.isInterface() || Modifier.isAbstract(fieldClassType.getModifiers())
								|| !hasNoneArgConstructor(fieldClassType)) {
							class1 = ArrayList.class;
						} else {
							class1 = fieldClassType;
						}

						codes.append(fieldName + "  = new " + class1.getCanonicalName() + "();\n");

						if (null != rootClassName && !CommonUtils.isNullOrEmpty(rule.getBinding().getWirings())) {
							Map<String, Type[]> genericMap = ObjectReflector.getGenericTypeOfField(rootClassName,
									fieldName);

							if (null != genericMap && genericMap.size() > 0) {
								Type type = genericMap.get(className + "." + fieldName)[0];

								Class clazz = ObjectReflector.eraseAsClass(type);
								if (clazz != null) {
									appendGenericInstance(codes, rule, fieldName, clazz);
								}
							}
						}
						if (null != getSetterMethod(rootClassName, fieldName)) {
							codes.append(" root.set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
									+ "(" + fieldName + ");\n");
						}
						codes.append("}\n");
					} else if (Map.class.isAssignableFrom(fieldClassType)) {
						appendInstanceForMap(codes, rule, rootClassName, fieldName, fieldClassType);
					} else {
						// empty
					}
				} else if (t != null && t.endsWith("[]") && t.split("\\[\\]").length == 1 && !t.startsWith("java.")) {

					appendInstanceForArrayObject(codes, rule, rootClassName, fieldName, t);
				}
			}
		}
	}

	private void appendInstanceForArrayObject(StringBuffer codes, Rule rule, Class rootClassName, String fieldName,
			String t) {

		String clazz = t.substring(0, t.indexOf("[]"));
		Class clz = getClazz(clazz);

		Binding binding = rule.getBinding();
		String[] wirings = binding.getWirings().split(",");

		if (clz != null && !clz.isInterface() && !Modifier.isAbstract(clz.getModifiers())) {

			codes.append("if (null ==" + fieldName + "|| " + fieldName + ".length==0){\n");

			String nodes = "";

			boolean needAssignVar = !CommonUtils.isNullOrEmpty(binding.getForeach())
					&& !CommonUtils.isNullOrEmpty(binding.getVar());
			if (!hasNoneArgConstructor(clz)) {
				nodes = "";
			} else {

				if (needAssignVar) {
					codes.append(" List $list = getRepeatedObjectList(" + binding.getForeach() + ");\n");
					codes.append("for(Object i" + ": $list" + "){\n");
					codes.append(" " + binding.getVar() + " = i" + ";\n");

					codes.append(" if(" + binding.getVar() + "!=null){\n");
				}

				for (int i = 0; i < wirings.length; i++) {
					if (!CommonUtils.isNullOrEmpty(wirings[i])) {
						String paraName = " $newInstance$" + i;
						codes.append(clazz + paraName + " = new " + clazz + "();\n");
						codes.append(" new " + wirings[i] + "Validation().validate(" + paraName + ",\"\");\n\n");
						if (i == 0) {
							nodes = paraName;
						} else {
							nodes = nodes + ", " + paraName;
						}
					}

					if (needAssignVar) {
						codes.append(" }\n");
						codes.append(" " + binding.getVar() + " = null;\n");
					}
				}

			}

			codes.append(fieldName + "  = new " + t + "{" + nodes + " };\n");

			if (needAssignVar) {
				codes.append("}\n");
			}

			if (getSetterMethod(rootClassName, fieldName) != null) {
				codes.append(" root.set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "("
						+ fieldName + ");\n");
			}

			codes.append("}\n");
		}
	}

	private Class getBindingClass(Rule rule) throws ClassNotFoundException {

		Class clz = null;
		String[] wirings = rule.getBinding().getWirings().split(",");

		if (null != wirings && wirings.length == 1 && wirings[0].length() > 0) {

			clz = Class.forName(ruleSuite.getObjectEntities().get(wirings[0]).getClassName());
			if (clz.isAssignableFrom(Map.class)) {
				clz = LinkedHashMap.class;
			} else if (clz.isAssignableFrom(List.class)) {
				clz = ArrayList.class;
			}
			// appendGenericInstance(codes, rule, fieldName, clz);
		}
		// continue;
		return clz;
	}

	private void appendInstanceForMap(StringBuffer codes, Rule rule, Class rootClassName, String fieldName,
			Class fieldClassType) throws Exception {

		codes.append("if (null ==" + fieldName + "){\n");
		Class class1 = null;

		if (fieldClassType.isInterface() || Modifier.isAbstract(fieldClassType.getModifiers())
				|| !hasNoneArgConstructor(fieldClassType)) {
			class1 = LinkedHashMap.class;
		} else {
			class1 = fieldClassType;
		}

		codes.append(fieldName + "  = new " + class1.getCanonicalName() + "();\n");

		{// //Start

			Binding binding = rule.getBinding();
			String[] wirings = binding.getWirings().split(",");

			boolean needAssignVar = !CommonUtils.isNullOrEmpty(binding.getForeach())
					&& !CommonUtils.isNullOrEmpty(binding.getVar());
			if (needAssignVar) {
				codes.append(" List $list = getRepeatedObjectList(" + binding.getForeach() + ");\n");
				codes.append("for(Object i" + ": $list" + "){\n");
				codes.append(" " + binding.getVar() + " = i" + ";\n");
				codes.append(" if(" + binding.getVar() + "!=null){\n");
			}

			for (int i = 0; i < wirings.length; i++) {

				if (!CommonUtils.isNullOrEmpty(wirings[i])) {
					String paraName = " $newInstance$" + i;
					codes.append(
							class1.getCanonicalName() + paraName + " = new " + class1.getCanonicalName() + "();\n");

					Class wc = getBindingClass(rule);

					if (Map.class.isAssignableFrom(wc)) {
						codes.append(" new " + wirings[i] + "Validation().validate(" + paraName + ",\"\");\n");
					}
					codes.append(fieldName + ".putAll(" + paraName + ");\n\n");

					if (needAssignVar) {
						codes.append(" }\n");
						codes.append(" " + binding.getVar() + " = null;\n");
					}
				}
			}

			if (needAssignVar) {
				codes.append("}\n");
			}

		} // /////////end

		if (getSetterMethod(rootClassName, fieldName) != null) {
			codes.append(" root.set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "("
					+ fieldName + ");\n");
		} else if (Map.class.isAssignableFrom(rootClassName)) {
			codes.append("root.put(\"" + fieldName + "\"," + fieldName + ");\n");
		}

		codes.append("}\n");
	}

	private void appendInstanceForCommonClass(StringBuffer codes, Rule rule, Class rootClassName, String fieldName,
			Class fieldClassType) {

		codes.append("if (null ==" + fieldName + "){\n");

		Binding binding = rule.getBinding();
		String[] wirings = binding.getWirings().split(",");

		boolean needAssignVar = !CommonUtils.isNullOrEmpty(binding.getSrc())
				&& !CommonUtils.isNullOrEmpty(binding.getVar());
		if (needAssignVar) {
			codes.append(" " + binding.getVar() + " = " + binding.getSrc() + ";\n");
			codes.append(" if(" + binding.getVar() + "!=null){\n");
		}

		if (hasNoneArgConstructor(fieldClassType) && null != wirings && wirings.length > 0
				&& !CommonUtils.isNullOrEmpty(wirings[0])) {
			codes.append(fieldName + "  = new " + fieldClassType.getCanonicalName() + "();\n");

			codes.append(" new " + wirings[0] + "Validation().validate((" + fieldClassType.getCanonicalName() + ")"
					+ fieldName + ",\"\");\n\n");
		}

		if (needAssignVar) {
			codes.append(" }\n");
			codes.append(" " + binding.getVar() + " = null;\n");
		}

		if (null != getSetterMethod(rootClassName, fieldName)) {
			codes.append(" root.set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "("
					+ fieldName + ");\n");
		} else if (Map.class.isAssignableFrom(rootClassName)) {
			codes.append("root.put(\"" + fieldName + "\"," + fieldName + ");\n");
		}

		codes.append("}\n");
	}

	private void appendGenericInstance(StringBuffer codes, Rule rule, String fieldName, Type type) {

		Class tp = (Class) type;

		if (hasNoneArgConstructor(tp)) {

			Binding binding = rule.getBinding();
			String[] wirings = binding.getWirings().split(",");

			boolean needAssignVar = !CommonUtils.isNullOrEmpty(binding.getForeach())
					&& !CommonUtils.isNullOrEmpty(binding.getVar());
			if (needAssignVar) {
				codes.append(" List $list = getRepeatedObjectList(" + binding.getForeach() + ");\n");
				codes.append("for(Object i" + ": $list" + "){\n");
				codes.append(" " + binding.getVar() + " = i" + ";\n");
				codes.append(" if(" + binding.getVar() + "!=null){\n");
			}

			for (int i = 0; i < wirings.length; i++) {

				if (!CommonUtils.isNullOrEmpty(wirings[i])) {
					String paraName = " $newInstance$" + i;
					codes.append(tp.getCanonicalName() + paraName + " = new " + tp.getCanonicalName() + "();\n");
					codes.append(" new " + wirings[i] + "Validation().validate(" + paraName + ",\"\");\n");
					codes.append(fieldName + ".add(" + paraName + ");\n\n");

					if (needAssignVar) {
						codes.append(" }\n");
						codes.append(" " + binding.getVar() + " = null;\n");
					}
				}
			}

			if (needAssignVar) {
				codes.append("}\n");
			}
		}
	}

	private boolean hasNoneArgConstructor(Class c) {

		boolean result = true;

		try {
			if (c != null && null != c.getConstructor(null)) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

	/**
	 * Get class instance from input class name.
	 * 
	 * @param t
	 * @return null if type is array expression as "XX[]", otherwise return Class.
	 */
	private Class getClazz(String t) {

		if (t == null) {
			return null;
		}

		Class c = null;

		try {
			c = Class.forName(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return c;
	}

	/**
	 * can not work well.
	 * 
	 * @param codes
	 * @param rule
	 */
	@Deprecated
	void appendPlainEval(StringBuffer codes, Rule rule) {

		codes.append("Object $o = null;\n");
		codes.append("findExpression(" + errorMsg + ");\n");
		codes.append(errorMsg + "= plainEval($o," + errorMsg + ");\n");

	}

	void appendBshEval(StringBuffer codes, Rule rule) {

		for (VRules4jContext context : ruleSuite.getContexts()) {
			String name = context.getName();
			if (!CommonUtils.isNullOrEmpty(context.getName())) {
				codes.append(" bshSet($bsh,\"" + name + "\"," + name + ");\n");
			}
		}

		String[] params = rule.getInputObjects().split(",");

		for (String str : params) {
			if (!CommonUtils.isNullOrEmpty(str)) {
				codes.append(" bshSet($bsh,\"" + str + "\"," + str + ");\n");
			}
		}

		codes.append(errorMsg + "= bshEval($bsh," + errorMsg + ");\n");

		for (String str : params) {
			if (!CommonUtils.isNullOrEmpty(str)) {
				codes.append(" bshUnset($bsh,\"" + str + "\");\n");
			}
		}

	}

	String generateInputValuesOutputCode(String inputObjects) {

		StringBuffer outValuesCode = new StringBuffer(" + \" = \" + ");

		String[] strs = inputObjects.split(",");

		if (strs.length == 1 && strs[0].trim().equals("")) {
			return "";
		}

		for (int i = 0; i < strs.length; i++) {
			if (null != strs[i] && strs[i].trim().length() > 0) {
				if (i == 0) {
					outValuesCode.append("com.unibeta.vrules.parsers.ObjectSerializer.xStreamToXml(" + strs[i] + ")");
				} else {
					outValuesCode.append(
							"+ \",\" + " + "com.unibeta.vrules.parsers.ObjectSerializer.xStreamToXml(" + strs[i] + ")");
				}
			}

		}

		return outValuesCode.toString();
	}

	String getObjectEntityNameById(String refId) {

		ObjectEntity entity = (ObjectEntity) entities.get(refId);

		return entity.getName();
	}

	String getReferIdClassName(String referId) throws Exception {

		ObjectEntity entity = (ObjectEntity) entities.get(referId);

		if (null == entity) {
			throw new Exception(
					"The specified objectId " + referId + " has not defined! Please check your configuration files.");
		}

		return entity.getClassName();
	}

	String handleErrorMessage(String errorMsg) {

		// return errorMsg;
		return errorMsg.replaceAll("\\s+", " ").replace("\"", "\\\"");
	}

	String generateParametes(Rule rule, Map<String, String> types) {

		StringBuffer str = new StringBuffer();
		String[] fields = rule.getInputObjects().split(",");

		String parameters = null;
		for (int i = 0; i < fields.length && null != fields[i]; i++) {
			str.append(",");
			String type = types.get(fields[i]);
			if (null == type) {
				continue;
			}
			str.append(getValidClassName(type) + " " + fields[i].trim());

		}

		parameters = str.substring(1);

		return parameters;
	}

	String generateParametesForMap(Rule rule) {

		StringBuffer str = new StringBuffer();
		String[] fields = rule.getInputObjects().split(",");

		String parameters = null;
		for (int i = 0; i < fields.length && null != fields[i]; i++) {
			str.append(",");
			String type = Object.class.getCanonicalName();// always be string
															// type
			if (null == type) {
				continue;
			}
			str.append(getValidClassName(type) + " " + fields[i].trim());

		}

		parameters = str.substring(1);

		return parameters;
	}

	String generateParameterValue(Rule rule, Map types) {

		StringBuffer str = new StringBuffer();
		String[] fields = rule.getInputObjects().split(",");

		String parameters = "";
		for (int i = 0; i < fields.length; i++) {

			str.append(",");

			String name = "";
			Object typeName = types.get(fields[i]);
			String id = fields[i];
			if (null == typeName) {
				continue;
			}
			name = generateMethodName(typeName, id);

			str.append(THIS_ROOT + "." + name + "()");

		}

		if (fields.length == 0) {
			parameters = "";
		} else {
			parameters = str.substring(1);
		}

		return parameters;
	}

	String generateParameterValueForMap(Rule rule, Map types) {

		StringBuffer str = new StringBuffer();
		String[] fields = rule.getInputObjects().split(",");

		String parameters = "";
		for (int i = 0; i < fields.length; i++) {

			str.append(",");

			Object typeName = Object.class.getCanonicalName();
			String id = fields[i];
			if (null == typeName) {
				continue;
			}

			str.append(THIS_ROOT + ".get(\"" + id + "\")");

		}

		if (fields.length == 0) {
			parameters = "";
		} else {
			parameters = str.substring(1);
		}

		return parameters;
	}

	String generateMethodName(Object typeName, String id) {

		String name;
		if (VRulesConstants.JAVA_BOOLEAN.equals(typeName)) {
			if (id.startsWith("is")) {
				name = "is" + id.substring(2, 3).toUpperCase() + id.substring(3);
			} else {
				name = "is" + id.substring(0, 1).toUpperCase() + id.substring(1);
			}
		} else {
			name = "get" + id.substring(0, 1).toUpperCase() + id.substring(1);
		}
		return name;
	}

	void appendInterfaceMethod(StringBuffer codes, Map entities) {

		/*
		 * public String[] validate(Map<String,Object> obj, String entityId) throws
		 * Exception
		 */
		codes.append("\n");
		codes.append("@SuppressWarnings(\"unchecked\")\n");
		codes.append(
				"public String[] validate(Map<String,Object> obj, Map<String, List<String>> ruleset) throws Exception ");
		codes.append("{\n");
		// codes.append(THIS_ROOT + " = obj; \n");
		codes.append(initDeclaredContextsValues(ruleSuite.getContexts(), VALIDATION_METHOD_TYPE_MAP));
		codes.append("List<String> errorList = new ArrayList<String>();\n");

		codes.append("Set set = obj.keySet();\n" + "for (Iterator i = set.iterator(); i.hasNext();) {\n"
				+ "String id = (String)i.next();\n" + "String[] subErrors = validate((Object)obj.get(id),id,ruleset);\n"
				+ "copyArrayToList(subErrors,errorList);\n" + "" + "}");

		codes.append("");
		codes.append("return errorList.toArray(new String[0]);\n");

		codes.append("}\n");

		/*
		 * public String[] validate(Object obj, String entityId) throws Exception
		 */
		codes.append("\n");
		codes.append("@SuppressWarnings(\"unchecked\")\n");
		codes.append(
				"public String[] validate(Object obj, String entityId,Map<String, List<String>> ruleset) throws Exception ");
		codes.append("{\n");
		// It never works for below line.No need this. never do that again.
		// codes.append(initDeclaredContextsValues(ruleSuite.getContexts(),VALIDATION_METHOD_TYPE_OBJECT));
		codes.append(THIS_ROOT + " = obj; \n");
		codes.append(CURRENT_RULE_ID_LIST + " = ruleset.get(entityId); \n");
		codes.append("String[] $errors = null;\n");
		appenChooseObjectLogic(codes, entities);
		codes.append("");
		codes.append("return $errors;\n");

		codes.append("}\n");

	}

	String initDeclaredContextsValues(List<VRules4jContext> contexts, int flag) {

		StringBuffer s = new StringBuffer();
		for (VRules4jContext context : contexts) {
			s.append("Object " + context.getName() + "Value =null;");

			if (flag == VALIDATION_METHOD_TYPE_MAP) {
				s.append("if(obj instanceof Map){");
				s.append(context.getName() + "Value = getValueFromCollectionByContext(obj,\"" + context.getClassName()
						+ "\",\"" + context.getName() + "\");\n");
				s.append("}");
			} else {
				s.append("if(obj instanceof Object[]){\n" + "List obj__List= new ArrayList();\n"
						+ "Object[] obj__array = (Object[])obj;" + "copyArrayToList(obj__array,obj__List);");
				s.append(context.getName() + "Value = getValueFromCollectionByClassName(obj__List,\""
						+ context.getClassName() + "\");\n");
				s.append("}");
			}

			s.append("if(null != " + context.getName() + "Value){\n");

			if (InterpreterUtils.isBooleanType(context.getClassName())) {
				s.append(context.getName() + " = (" + context.getClassName() + ")new Boolean(" + context.getName()
						+ "Value.toString());");
			} else if (InterpreterUtils.isNumericType(context.getClassName())) {
				s.append(context.getName() + " = (" + context.getClassName() + ")(double)new Double("
						+ context.getName() + "Value.toString());");
			} else {
				s.append(context.getName() + " = (" + context.getClassName() + ")" + context.getName() + "Value;");
			}

			if (InterpreterUtils.hasBsh()) {
				s.append(" bshSet($bsh,\"" + context.getName() + "\"," + context.getName() + "Value);\n");
			}
			// "System.out.println(\""+context.getClassName()+"\"+"+"\" inited
			// success!!\");"+
			s.append("}");
		}
		return s.toString();
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

			String canonicalName = getValidClassName(entity.getClassName());

			codes.append("if(\"" + entity.getId() + "\".equals(entityId))");
			codes.append("{\n");
			codes.append("String $path$ = " + "\"" + entity.getName() + "\"" + ";\n");
			// codes.append("String $path$ = " +"\""+"\"" +";\n");

			codes.append(canonicalName + " obj1 = (" + canonicalName + ") obj;\n");
			codes.append("$errors = new " + entity.getId() + "Validation" + "().validate(obj1,$path$);\n");

			codes.append("\n}\n");
		}
		codes.append("");

	}

	private String getValidClassName(String name) {

		Class c;
		String canonicalName;
		try {
			c = Class.forName(name);
			canonicalName = c.getCanonicalName();
		} catch (ClassNotFoundException e) {
			canonicalName = name;
		}
		return canonicalName;
	}

	void appendPublicClassHead(StringBuffer codes, String fileName) {

		codes.append("@SuppressWarnings(\"unchecked\")\n");
		codes.append("public class " + CommonUtils.generateClassNameByFileFullName(fileName)
				+ " implements RulesValidation ");
	}

	void appendComments(StringBuffer codes) {

		codes.append("/**\n" + "* Genetated by vRules4j engine.\n" + "* copyright (C) 2007-" + new Date().getYear()
				+ " www.uni-beta.com \n" + "* @Author Jordan.xue (Junxing.xue)\n" + "* @createdate "
				+ new Date().toString() + "\n" + "* @Core Engine Version: " + CORE_ENGINE_VERSION + "\n*/\n");
		codes.append("\n");

	}

	void appendImports(StringBuffer codes) {

		codes.append("\n");
		codes.append("import java.util.*;\n");
		codes.append("import java.math.*;\n");
		codes.append("import java.math.*;\n");
		codes.append("import " + RulesValidation.class.getCanonicalName() + ";\n");
		codes.append("import static " + InterpreterUtils.class.getCanonicalName() + ".*;\n");
		codes.append("import static " + CommonSyntaxs.class.getCanonicalName() + ".*;\n");
		codes.append("\n");

		// codes.append("import
		// com.unibeta.vrules.engines.dccimpls.rules.RulesValidation;\n");
		// codes.append("import com.unibeta.vrules.utils.*;\n");
		codes.append("\n");

		if (ruleSuite.getImports() != null && ruleSuite.getImports().length() > 0) {

			String imports = ruleSuite.getImports().trim();
			String[] packages = null;
			if (imports.endsWith(";") || imports.endsWith(";")) {
				imports = imports.substring(0, imports.length() - 1);
			}

			if (imports.indexOf(",") >= 0) {
				packages = imports.split(",");
			} else if (imports.indexOf(";") >= 0) {
				packages = imports.split(";");
			} else {
				packages = imports.split(";");
			}

			for (int i = 0; packages != null && i < packages.length; i++) {
				if (packages[i].trim().length() > 0) {
					codes.append("import " + packages[i] + ";\n");
				}
			}
		}

		codes.append("\n");
	}

	void appendPackage(StringBuffer codes) {

		codes.append("/*\n" + "* vRules4j, copyright (C) 2007-" + Calendar.getInstance().get(Calendar.YEAR)
				+ " www.uni-beta.com. vRules is free software;\n"
				+ "* you can redistribute it and/or modify it under the terms of Version 2.0\n"
				+ "* Apache License as published by the Free Software Foundation. vRules is\n"
				+ "* distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n"
				+ "* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A\n"
				+ "* PARTICULAR PURPOSE.\n" + "*/\n");
		codes.append("package com.unibeta.vrules.engines.dccimpls.rules;\n\n");

	}

	String writeToFile(String javaCodes, String fileName) throws Exception {

		String javaClassName = CommonUtils.generateClassNameByFileFullName(fileName) + JAVA_FILE_SUFFIX;
		String javaFileName = CommonUtils.getFilePathName(fileName) + javaClassName;

		FileWriter writer = null;
		try {

			writer = new FileWriter(javaFileName);

			writer.write(javaCodes);
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != writer) {
				writer.close();
			}
		}

		return javaFileName;
	}

}
