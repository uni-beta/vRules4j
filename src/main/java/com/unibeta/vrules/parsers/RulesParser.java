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

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.unibeta.vrules.base.DecisionConstantDefinition;
import com.unibeta.vrules.base.GlobalConfig;
import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.Rule.Binding;
import com.unibeta.vrules.base.Ruleset;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.base.VRules4jContext;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.utils.CommonUtils;
import com.unibeta.vrules.utils.VRulesCommonUtils;
import com.unibeta.vrules.utils.XmlUtils;

/**
 * Validation rule parser. The file name should be initialed before trying to
 * use it.
 * 
 * @author Jordan.Xue
 */
public class RulesParser {

	private static final String MERGE_EXTENSIONS = "mergeExtensions";
	private static final String RULESET = "ruleset";
	private static final String IS_COLLECTION = "isCollection";
	private static final String SRC = "src";
	private static final String VAR = "var";
	private static final String FOREACH = "foreach";
	private static final String IF_TRUE = "ifTrue";
	private static final String IF_FALSE = "ifFalse";
	private static final String WIRINGS2 = "wirings";
	private static final String ENABLE_BINDING = "enableBinding";
	private static final String ASSERTION = "assertion";
	private static final String DECISION_MODE = "decisionMode";
	private static final String JAVA = "java";
	private static final String GLOBAL = "global";
	private static final String BOOL_PARAM = "boolParam";
	private static final String DECISION = "decision";
	private static final String IMPORTS = "imports";
	private static final String METHODS = "methods";
	private static final String JAVA_BODY = "javaBody";
	private static final String VALUE = "value";
	private static final String DEFINITION = "definition";
	private static final String DECISION_CONSTANT_DEFINITIONS = "decisionConstantDefinitions";
	private static final String DECISION_CONSTANTS = "decisionConstants";
	private static final String DECISION_OBJECT = "decisionObject";
	private static final String TOGGLE_BREAKPOINT = "toggleBreakpoint";

	private static final String DEPENDS = "depends";

	private static final String BRAKPOINT = "breakpoint";

	private static final String SEQUENCE = "sequence";

	private static final String CONTEXTS = "contexts";

	private static final String CONTEXT = "context";

	private static final String DISPLAY_ERROR_VALUE = "displayErrorValue";

	private static final String NAME = "name";
	private static final String NILLABLE = "nillable";

	private static final String ERROR_OBJECT = "errorObject";

	private static final String INCLUDES = "includes";

	private static final String EXTENSION = "extension";
	private static final String EXTENSIONS = "extensions";

	private static final String RETURN_ERROR_ID = "returnErrorId";
	private static final String RETURN_ID = "returnId";

	private static final String GLOBAL_CONFIG = "globalConfig";

	private static final String IS_MAP_OR_LIST = "isMapOrList";

	private static final String BOOL_TRUE = "true";
	private static final String BOOL_FALSE = "false";
	private static final String BOOL_ON = "on";
	private static final String BOOL_OFF = "off";

	private static final String REF_ID = "refId";

	private static Logger logger = LoggerFactory.getLogger(RulesParser.class);

	// private static String fileName = null;
	// private static Map rulesConfigMap = null;
	// private static long lastModified = 0;

	private static final String MESSAGE = "message";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String PREDICATE = "predicate";
	private static final String ASSERT = "assert";
	private static final String DATA_TYPE = "dataType";
	private static final String RULE = "rule";
	private static final String RULES = "rules";
	private static final String CLASS_NAME = "className";
	private static final String NILL = "nill";
	private static final String DESC = "desc";
	private static final String ID = "id";

	private static final String IS_COMPLEX_TYPE = "isComplexType";
	private static final String RECURSIVE = "recursive";
	private static final String OUTPUT_BOOL = "outputBool";
	private static final String INPUT_OBJECTS = "inputObjects";
	private static final String ATTRIBUTES = "attributes";
	private static final String OBJECT = "object";
	private static final Pattern ifRegexPattern = Pattern
			.compile("if\\s*\\n*\\t*\\r*\\v*\\f*\\e*\\a*\\b*\\({1}.+\\){1}", Pattern.DOTALL);

	private GlobalConfig globalConfig = null;
	private Class serializeClass;
	private Map<String, String> decisionConstantDefinitionMap = null;

	protected RulesParser(Class serializeClass) {

		this.serializeClass = serializeClass;
	}

	private RulesParser() {

		this.serializeClass = null;
	}

	/**
	 * Parsers specified file by given fileName.
	 * 
	 * @param fileName
	 * @return a rule configuration <code>Map</code> of (fileName,ObjectEntity).
	 * @throws Exception
	 */
	public VRuleSuite parserRuleConfig(String fileName) throws Exception {

		List<String> circulatoryList = new ArrayList<String>();
		validateCirculatoryIncludes(fileName, circulatoryList);

		VRuleSuite ruleSuite = null;
		// List entityList;

		ruleSuite = parseDocument(fileName);

		// import the super rules configures
		if (ruleSuite.getGlobalConfig() != null && ruleSuite.getGlobalConfig().getIncludes().trim().length() > 0) {
			ruleSuite = mergeSpuerRulesByCondition(ruleSuite, fileName, null);
		}

		if (globalConfig.isMergeExtensions()) {
			mergeExtensions(ruleSuite);
		}
		sortRuleSuiteRules(ruleSuite);

		validateRuleDecisionConfig(ruleSuite.getObjectEntities());
		validateReferenceObjectExist(ruleSuite);

		return ruleSuite;
	}

	private void mergeExtensions(VRuleSuite ruleSuite) {

		for (ObjectEntity obj : ruleSuite.getObjectEntities().values()) {
			mergeOneEntityExtensionRules(obj, ruleSuite);
		}
	}

	private void mergeOneEntityExtensionRules(ObjectEntity obj, VRuleSuite ruleSuite) {

		if (!CommonUtils.isNullOrEmpty(obj.getExtension())) {
			String[] extIds = obj.getExtension().split(",");

			for (String id : extIds) {
				ObjectEntity extObj = ruleSuite.getObjectEntities().get(id.trim());

				if (null != extObj) {
					mergeOneEntityExtensionRules(extObj, ruleSuite);

					for (Rule r : extObj.getRules()) {
						if (!obj.getRules().contains(r)) {
							obj.getRules().add(r);
						}
					}
				}
			}

			obj.setExtension("");
		}
	}

	private void validateReferenceObjectExist(VRuleSuite ruleSuite) throws Exception {

		Map<String, ObjectEntity> objectEntities = ruleSuite.getObjectEntities();

		Set objIdSet = objectEntities.keySet();
		Collection<ObjectEntity> objList = objectEntities.values();

		for (ObjectEntity oe : objList) {
			for (Rule r : oe.getRules()) {

				if (!CommonUtils.isNullOrEmpty(r.getRefId())) {
					if (!objIdSet.contains(r.getRefId())) {
						throw new Exception("The refId[" + r.getRefId() + "] can not be found in rule[id = " + r.getId()
								+ "] of Object[id = " + oe.getId()
								+ "] from current input configuration definition file.");
					}

					if (r.getRefId().equals(oe.getId())) {
						r.setRefId("");
						logger.warn("Rule [id = " + r.getId()
								+ "] reference validation was ignored, endless loop validation was detected, rule refId can not refer to parent entity's id.");
					}
				}

				Binding binding = r.getBinding();

				if (null != binding && !CommonUtils.isNullOrEmpty(binding.getWirings())) {
					String[] wirings = binding.getWirings().split(",");

					for (String w : wirings) {
						if (!objIdSet.contains(w)) {
							throw new Exception("The binding wiring[" + w + "] can not be found in rule[id = "
									+ r.getId() + "] of Object[id = " + oe.getId()
									+ "] from current input configuration definition file.");
						}

						if (w.equals(oe.getId())) {
							binding.setWirings("");
							logger.warn("Rule [id = " + r.getId()
									+ "] binding wiring reference validation was ignored, endless loop binding was detected, rule's binding wiring can not refer to parent entity's id.");
						}
					}
				}

				String[] attributes = r.getInputObjects().split(",");
				for (String s : attributes) {
					if (null != s && (s.trim().contains(" ") || s.trim().contains("-"))) {
						throw new Exception("The attributes [" + r.getInputObjects()
								+ "] contains invalid variable in rule[id = " + r.getId() + "] of Object[id = "
								+ oe.getId() + "] from current input configuration definition file.");
					}
				}
			}
		}

	}

	private void validateRuleDecisionConfig(Map<String, ObjectEntity> objectEntities) throws Exception {

		Iterator<String> i = objectEntities.keySet().iterator();

		while (i.hasNext()) {
			ObjectEntity obj = objectEntities.get(i.next());
			List<Rule> tempRules = new ArrayList<Rule>();

			if (!CommonUtils.isNullOrEmpty(obj.getExtension())) {
				String[] exts = obj.getExtension().split(",");

				ObjectEntity extObj = objectEntities.get(exts[0]);
				if (null != extObj) {
					tempRules.addAll(extObj.getRules());
				}
			}

			tempRules.addAll(obj.getRules());

			String decisionRules = " object.\nPlease check:"
					+ "\n1. Super object's decision rule can not be sent back to child object any more, once it is triggered. "
					+ "\n2. Super object always has high authority for decision making. "
					+ "\n3. Child object can only apply super rule of super object that is directly under by, but super object can not apply any child's rules directly. "
					+ "\n4. There is none restriction in the same object scope.";

			for (Rule r : obj.getRules()) {
				if (!CommonUtils.isNullOrEmpty(r.getIfTrue())) {

					if (!containsRule(r.getIfTrue(), tempRules)) {
						throw new Exception("Decision configuration rules validation failed, referred [ifTrue="
								+ r.getIfTrue() + "] is not valid in [" + r.getId() + "] rule of " + obj.getId()
								+ decisionRules);
					}
				}

				if (!CommonUtils.isNullOrEmpty(r.getIfFalse())) {
					if (!containsRule(r.getIfFalse(), tempRules)) {
						throw new Exception("Decision configuration rules validation failed, referred [ifFalse="
								+ r.getIfFalse() + "] is not valid in [" + r.getId() + "] rule of " + obj.getId()
								+ decisionRules);
					}
				}
			}
		}
	}

	private boolean containsRule(String ruleId, List<Rule> list) {

		if (null == list) {
			return false;
		}

		for (Rule r : list) {
			if (ruleId.equals(r.getId())) {
				return true;
			}
		}

		return false;
	}

	private void sortRuleSuiteRules(VRuleSuite ruleSuite) throws Exception {

		Collection<ObjectEntity> entityValues = ruleSuite.getObjectEntities().values();

		for (ObjectEntity o : entityValues) {
			Collections.sort(o.getRules());
			validateRulesDepends(o);
		}

	}

	private void validateRulesDepends(ObjectEntity oe) throws Exception {

		for (Object o : oe.getRules()) {
			Rule rule = (Rule) o;
			if (rule.getDepends().length() > 0) {
				boolean finallFlag = true;
				String parseredStr = rule.getDepends().replace("&&", ",").replace("||", ",").replace("==", ",")
						.replace("!", ",");

				if (parseredStr.contains("<") || parseredStr.contains("<=") || parseredStr.contains(">")
						|| parseredStr.contains(">=") || parseredStr.contains("+") || parseredStr.contains("-")
						|| parseredStr.contains("*") || parseredStr.contains("/") || parseredStr.contains("<<=")
						|| parseredStr.contains(">>=") || parseredStr.contains(">>>") || parseredStr.contains("<<<")
						|| parseredStr.contains("=")) {
					throw new Exception(
							"Rule 'depends' expression statement is invalid! Only logic expression statements are available, "
									+ "such as '&&' '||' '!' and '=='. please  check the depends expression of "
									+ rule.getId() + ". " + "The invalid expression is '" + rule.getDepends() + "'.");
				}
				String[] depends = parseredStr.split(",");

				for (String str : depends) {
					if (null == str || str.trim().length() == 0) {
						continue;
					}

					boolean flag = false;
					for (Object r : oe.getRules()) {
						Rule i = (Rule) r;
						String trim = str.trim();
						if (trim.length() > 0 && trim.equals(i.getId()) && rule.getSequence() > i.getSequence()
								|| trim.equals("true") || trim.equals("false")) {
							flag = true;
							break;
						}
					}
					finallFlag = finallFlag && flag;
				}

				if (!finallFlag) {
					throw new Exception(
							"Rule's dependence configuration is invalid! The rule's dependence can only be inside the object scope and "
									+ " only high sequence can depend on the low sequence rule. Please check the rule's configuration."
									+ " The invalid rule id is " + rule.getId() + " of " + oe.getId() + " object."
									+ "The invalid configuration is '" + rule.getDepends() + "'.");
				}

			}
		}
	}

	/**
	 * Merges the super rules by specified conditions.
	 * 
	 * @param ruleSuite
	 * @param currentfileName
	 * @param nodeFlag
	 *            should be below options:
	 *            IMPORTS,JAVA_BODY,CONTEXTS,DECISION_CONSTANT_DEFINITIONS ,OBJECT,
	 *            if it is null, merge all element that needed.
	 * @return
	 * @throws Exception
	 */
	private VRuleSuite mergeSpuerRulesByCondition(VRuleSuite ruleSuite, String currentfileName, String nodeFlag)
			throws Exception {

		String[] includesFiles = CommonUtils.fetchIncludesFileNames(ruleSuite.getGlobalConfig().getIncludes(),
				currentfileName);

		for (int i = 0; i < includesFiles.length; i++) {

			VRuleSuite ruleSuite2 = ConfigurationProxy.getVRuleSuiteInstance(includesFiles[i], this.serializeClass);

			if (IMPORTS.equals(nodeFlag)) {
				mergeRulesImports(ruleSuite, ruleSuite2);
			} else if (JAVA_BODY.equals(nodeFlag) || JAVA.equals(nodeFlag) || METHODS.equals(nodeFlag)) {
				mergeRulesMethods(ruleSuite, ruleSuite2);
			} else if (CONTEXTS.equals(nodeFlag)) {
				mergeRulesContexts(ruleSuite, ruleSuite2);
			} else if (DECISION_CONSTANT_DEFINITIONS.equals(nodeFlag)) {
				mergeDecisionConstantDefinition(ruleSuite, ruleSuite2);
			} else if (OBJECT.equals(nodeFlag)) {
				mergeObjects(ruleSuite, ruleSuite2);
			} else if (RULESET.equals(nodeFlag)) {
				mergeRulesets(ruleSuite, ruleSuite2);
			} else {
				mergeRulesImports(ruleSuite, ruleSuite2);
				mergeRulesMethods(ruleSuite, ruleSuite2);
				mergeRulesContexts(ruleSuite, ruleSuite2);
				mergeDecisionConstantDefinition(ruleSuite, ruleSuite2);
				mergeObjects(ruleSuite, ruleSuite2);
				mergeRulesets(ruleSuite, ruleSuite2);
			}
		}

		return ruleSuite;
	}

	private void mergeDecisionConstantDefinition(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		ruleSuite.getDecisionConstantDefinitions().addAll(ruleSuite2.getDecisionConstantDefinitions());

	}

	private void mergeRulesImports(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		String split = ";";

		String imports = ruleSuite.getImports();
		if (imports.indexOf(",") >= 0) {
			split = ",";
		} else if (imports.indexOf(";") >= 0) {
			split = ";";
		}

		ruleSuite.setImports(imports + split + ruleSuite2.getImports());

	}

	private void mergeRulesMethods(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		String methods = ruleSuite.getDeclaredMethods();
		ruleSuite.setDeclaredMethods(ruleSuite2.getDeclaredMethods() + methods);

	}

	private void mergeRulesContexts(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		Map<String, String> map1 = convertToContextMap(ruleSuite.getContexts());
		Map<String, String> map2 = convertToContextMap(ruleSuite2.getContexts());

		map2.putAll(map1);

		ruleSuite.setContexts(convertToContextList(map2));
	}

	private List<VRules4jContext> convertToContextList(Map<String, String> map) {

		List<VRules4jContext> list = new ArrayList<VRules4jContext>();

		Set<String> set = map.keySet();
		Iterator<String> i = set.iterator();

		for (; i.hasNext();) {
			String nameKey = i.next();
			String className = map.get(nameKey);

			VRules4jContext context = new VRules4jContext();
			context.setClassName(className);
			context.setName(nameKey);

			list.add(context);
		}
		return list;
	}

	private Map<String, String> convertToContextMap(List<VRules4jContext> contexts2) {

		if (null == contexts2) {
			return new HashMap<String, String>();
		}

		Map<String, String> map1 = new HashMap<String, String>();

		for (VRules4jContext c : contexts2) {
			map1.put(c.getName(), c.getClassName());
		}
		return map1;
	}

	private Map<String, String> convertToDecisionConstantDefinitionMap(List<DecisionConstantDefinition> contexts2) {

		if (null == contexts2) {
			return new HashMap<String, String>();
		}

		Map<String, String> map1 = new HashMap<String, String>();

		for (DecisionConstantDefinition c : contexts2) {
			map1.put(c.getName(), c.getValue());
		}
		return map1;
	}

	/**
	 * Merges the super rules ruleSuite2 to sub rules ruleSuite.
	 * 
	 * @param ruleSuite
	 *            Sub rules.
	 * @param ruleSuite2
	 *            Super rules.
	 */
	private void mergeObjects(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		ruleSuite2.getObjectEntities().putAll(ruleSuite.getObjectEntities());
		ruleSuite.setObjectEntities(ruleSuite2.getObjectEntities());

	}

	/**
	 * Merges the super rules ruleSuite2 to sub rules ruleSuite.
	 * 
	 * @param ruleSuite
	 *            Sub rules.
	 * @param ruleSuite2
	 *            Super rules.
	 */
	private void mergeRulesets(VRuleSuite ruleSuite, VRuleSuite ruleSuite2) {

		ruleSuite2.getRulesets().putAll(ruleSuite.getRulesets());
		ruleSuite.setRulesets(ruleSuite2.getRulesets());

	}

	/**
	 * Set the rule configuration file name.
	 * 
	 * @param file
	 *            it shoule be full path name.
	 */
	private VRuleSuite parseDocument(String fileName) throws Exception {

		if (null == fileName) {
			String error = "The validation rule configuration file has not been set!";
			logger.error(error);

			throw new Exception(error);
		}

		VRuleSuite ruleSuite = new VRuleSuite();
		Document doc = XmlUtils.getDocumentByFileName(fileName);
		// lastModified = new File(fileName).lastModified();

		GlobalConfig globalConfig = initGlobalConfig(doc);

		Element docRoot = doc.getDocumentElement();
		String methods = XmlUtils.getNodeValueByTargetName(docRoot, JAVA_BODY);
		if (null == methods || methods.length() == 0) {
			methods = XmlUtils.getNodeValueByTargetName(docRoot, METHODS);
		}
		if (null == methods || methods.length() == 0) {
			methods = XmlUtils.getNodeValueByTargetName(docRoot, JAVA);
		}

		String imports = XmlUtils.getNodeValueByTargetName(docRoot, IMPORTS);
		ruleSuite.setGlobalConfig(globalConfig);
		ruleSuite.setDeclaredMethods(CommonUtils.handleSpecialExpression(methods));
		ruleSuite.setImports(imports);

		List<VRules4jContext> contexts = initContexts(doc);
		ruleSuite.setContexts(contexts);

		List<DecisionConstantDefinition> decisionConstantDefinitions = initDecisionConstantDefinitions(doc);
		ruleSuite.setDecisionConstantDefinitions(decisionConstantDefinitions);
		// Merge the decision constant definition for current validation usage.
		this.mergeSpuerRulesByCondition(ruleSuite, fileName, DECISION_CONSTANT_DEFINITIONS);
		decisionConstantDefinitionMap = this
				.convertToDecisionConstantDefinitionMap(ruleSuite.getDecisionConstantDefinitions());

		Map<String, Ruleset> rulesetMap = buildRulesetMap(doc);
		ruleSuite.setRulesets(rulesetMap);

		Map<String, ObjectEntity> rulesConfigMap = buildObjectEntityMap(doc);
		ruleSuite.setObjectEntities(rulesConfigMap);

		return ruleSuite;
	}

	private Map<String, Ruleset> buildRulesetMap(Document doc) throws Exception {

		NodeList rulesetNodeList = doc.getElementsByTagName(RULESET);
		Map<String, Ruleset> rulesetMap = Collections.synchronizedMap(new HashMap<String, Ruleset>());

		for (int i = 0; i < rulesetNodeList.getLength(); i++) {
			Element element = (Element) rulesetNodeList.item(i);
			Ruleset ruleset = generateRuleset(element);

			rulesetMap.put(ruleset.getId(), ruleset);
		}
		return rulesetMap;
	}

	private Ruleset generateRuleset(Element element) throws Exception {

		Ruleset entity = new Ruleset();

		entity.setId(element.getAttribute(ID));

		NodeList ruleNodeList = element.getElementsByTagName(RULE);
		List<Rule> ruleList = new ArrayList<Rule>();

		for (int i = 0; i < ruleNodeList.getLength(); i++) {

			Element ruleElement = (Element) ruleNodeList.item(i);
			Rule rule = generateRule(ruleElement);
			if (null == rule) {
				continue;
			}

			ruleList.add(rule);
		}

		entity.setRules(ruleList);

		return entity;
	}

	private Map<String, ObjectEntity> buildObjectEntityMap(Document doc) throws Exception {

		NodeList nodeList = doc.getElementsByTagName(OBJECT);
		Map<String, ObjectEntity> rulesConfigMap = Collections.synchronizedMap(new HashMap<String, ObjectEntity>());

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			ObjectEntity entity = generateObjectEntity(element);

			rulesConfigMap.put(entity.getId(), entity);
		}
		return rulesConfigMap;
	}

	private void validateCirculatoryIncludes(String fileName, List<String> circulatoryList) throws Exception {

		if (CommonUtils.isNullOrEmpty(fileName)) {
			return;
		}

		Document doc = XmlUtils.getDocumentByFileName(fileName);
		File file = new File(fileName);
		GlobalConfig globalConfig = initGlobalConfig(doc);

		String includes = globalConfig.getIncludes();
		String[] includesFiles = CommonUtils.fetchIncludesFileNames(includes, fileName);

		circulatoryList.add(file.getAbsolutePath());

		for (String s : includesFiles) {
			File f = new File(s);

			if (!f.exists()) {
				throw new Exception(
						"File not found! The included rule configuration [" + f.getAbsolutePath() + "] is not found.");
			}

			if (!circulatoryList.contains(f.getAbsolutePath()) && !f.getAbsolutePath().equals(file.getAbsolutePath())) {
				this.validateCirculatoryIncludes(s, circulatoryList);
			} else {
				throw new Exception("Dead-lock including is detected! The file [" + f.getAbsolutePath()
						+ "] is recursively included in file [" + file.getAbsolutePath()
						+ "], Please double check the rule configuration files.");
			}
		}

	}

	private List<DecisionConstantDefinition> initDecisionConstantDefinitions(Document doc) {

		List<DecisionConstantDefinition> definitions = Collections
				.synchronizedList(new ArrayList<DecisionConstantDefinition>());

		Element element = null;

		NodeList nodes = doc.getElementsByTagName(DECISION_CONSTANT_DEFINITIONS);

		if (null == nodes || nodes.getLength() == 0) {
			nodes = doc.getElementsByTagName(DECISION_CONSTANTS);
		}

		if (null != nodes && nodes.getLength() == 1) {
			element = (Element) nodes.item(0);
		} else {
			return definitions;
		}

		NodeList contextList = element.getElementsByTagName(DEFINITION);

		for (int i = 0; i < contextList.getLength(); i++) {
			DecisionConstantDefinition definition = new DecisionConstantDefinition();

			Element contextElement = (Element) contextList.item(i);
			String name = contextElement.getAttribute(NAME);
			String value = contextElement.getAttribute(VALUE);

			if (null != name && null != value && name.trim().length() > 0 && value.trim().length() > 0) {
				definition.setName(name.trim());
				definition.setValue(value.trim());

				if (null != definition.getName() && definition.getName().length() > 0) {
					definitions.add(definition);
				}
			}
		}

		return definitions;
	}

	private List<VRules4jContext> initContexts(Document doc) {

		List<VRules4jContext> contexts = Collections.synchronizedList(new ArrayList<VRules4jContext>());
		Element element = null;

		NodeList nodes = doc.getElementsByTagName(CONTEXTS);
		if (null != nodes && nodes.getLength() == 1) {
			element = (Element) nodes.item(0);
		} else {
			return contexts;
		}

		NodeList contextList = element.getElementsByTagName(CONTEXT);

		for (int i = 0; i < contextList.getLength(); i++) {
			VRules4jContext context = new VRules4jContext();

			Element contextElement = (Element) contextList.item(i);
			String name = contextElement.getAttribute(NAME);
			String className = contextElement.getAttribute(CLASS_NAME);

			if (null != name && null != className && name.trim().length() > 0 && className.trim().length() > 0) {
				context.setName(name.trim());
				context.setClassName(className.trim());

				contexts.add(context);
			}
		}

		return contexts;
	}

	private ObjectEntity generateObjectEntity(Element element) throws Exception {

		ObjectEntity entity = new ObjectEntity();

		entity.setId(element.getAttribute(ID));

		String extensions = element.getAttribute(EXTENSIONS);
		if (extensions.trim().length() == 0) {
			extensions = element.getAttribute(EXTENSION);
		}
		extensions = validateExtensions(extensions);
		entity.setExtension(extensions);
		entity.setDescription(element.getAttribute(DESC));
		entity.setName(element.getAttribute(NAME));

		String seqStr = element.getAttribute(SEQUENCE);
		long sequence = Long.valueOf(null == seqStr || "".equals(seqStr) ? 0 : Long.valueOf(seqStr)).longValue();
		// String brakpointStr = element.getAttribute(BRAKPOINT).toUpperCase();
		// String dependsStr = element.getAttribute(DEPENDS);

		entity.setSequence(sequence);

		// entity.setBreakpoint((BOOL_TRUE.toUpperCase().equals(
		// brakpointStr.toUpperCase()) || BOOL_ON.toUpperCase().equals(
		// brakpointStr)) ? true : false);
		// entity.setDepends(dependsStr);

		entity.setClassName(XmlUtils.getNodeValueByTargetName(element, CLASS_NAME).trim());
		String nill = XmlUtils.getNodeValueByTargetName(element, NILL);
		if (nill == null || "".endsWith(nill)) {
			nill = XmlUtils.getNodeValueByTargetName(element, NILLABLE);
		}

		entity.setNill((nill == null) || nill.equalsIgnoreCase(BOOL_FALSE) ? false : true);

		entity.setRules(generateRules(element));
		handleObjectEntityXmlElementNames(entity);

		return entity;
	}

	private String validateExtensions(String extensions) {

		StringBuffer sb = new StringBuffer();
		List<String> list = new ArrayList<String>();
		String[] strs = extensions.split(",");

		for (String s : strs) {
			if (s != null && s.length() > 0 && !list.contains(s)) {
				list.add(s);
				sb.append(s + ",");
			}
		}

		if (strs.length > 0 && sb.length() > 0) {
			String str = sb.toString().substring(0, sb.length() - 1);
			return str;
		} else {
			return "";
		}

	}

	private void handleObjectEntityXmlElementNames(ObjectEntity entity) throws Exception {

		Class clazz = Class.forName(entity.getClassName().trim());

		if (null == entity.getName() || entity.getName().length() <= 0) {
			Annotation[] annotations = clazz.getAnnotations();
			String entityXmlName = VRulesCommonUtils.distillXmlElementWrapperName(annotations, entity.getName());
			if (null == entityXmlName) {
				entityXmlName = VRulesCommonUtils.generateObjectEntityXmlName(entity.getId());
			}
			entity.setName(entityXmlName);
		}

		Map<String, String> map = VRulesCommonUtils.buildXmlElementWrappedFieldNameMap(clazz);

		List<Rule> ruleList = entity.getRules();

		for (Rule rule : ruleList) {
			generateElementNameOfXPath(rule, map);
		}
	}

	/**
	 * Generates the specified Rule's xml element name, according to the given
	 * Map<filedName,xmlElementName>. Added on 2008-10-12:22:23
	 * 
	 * @return xml element name
	 */
	private static String generateElementNameOfXPath(Rule rule, Map types) {

		if (rule.getName() == null || rule.getName().trim().length() <= 0) {
			StringBuffer str = new StringBuffer();
			String[] fields = rule.getInputObjects().split(",");

			String elementName = null;
			for (int i = 0; i < fields.length && null != fields[i]; i++) {
				str.append(",");
				String xmlName = (String) types.get(fields[i]);
				if (null == xmlName) {
					str.append(fields[i].trim());
				} else {
					str.append(xmlName);
				}

			}

			elementName = str.substring(1);
			rule.setName(elementName);

			return elementName;
		} else {
			return rule.getName();
		}

	}

	private List generateRules(Element element) throws Exception {

		List list = new ArrayList();

		Element rulesNode = XmlUtils.getFirstElementByTargetName(element, RULES);
		if (null == rulesNode) {
			return list;
		}

		NodeList ruleNodeList = rulesNode.getElementsByTagName(RULE);

		for (int i = 0; i < ruleNodeList.getLength(); i++) {

			Element ruleElement = (Element) ruleNodeList.item(i);
			Rule rule = generateRule(ruleElement);
			if (null == rule) {
				continue;
			}

			if (!list.contains(rule)) {
				list.add(rule);
			} else {
				throw new Exception(
						"vRules configuratuion error: The id [" + rule.getId() + "] is duplicated in the same object.");
			}
		}

		return list;
	}

	private Rule generateRule(Element ruleElement) throws Exception {

		Rule rule = new Rule();

		rule.setId(ruleElement.getAttribute(ID).trim());
		rule.setDesc(ruleElement.getAttribute(DESC).trim());
		rule.setName(ruleElement.getAttribute(NAME).trim());

		String recursive = ruleElement.getAttribute(RECURSIVE).trim();
		String isComplextype = ruleElement.getAttribute(IS_COMPLEX_TYPE).trim();
		String isMapOrList = ruleElement.getAttribute(IS_MAP_OR_LIST).trim();

		if (CommonUtils.isNullOrEmpty(isMapOrList)) {
			isMapOrList = ruleElement.getAttribute(IS_COLLECTION).trim();
		}

		String seqStr = ruleElement.getAttribute(SEQUENCE).trim();
		long sequence = Long.valueOf(null == seqStr || "".equals(seqStr) ? 0 : Long.valueOf(seqStr)).longValue();

		String brakpointStr = ruleElement.getAttribute(BRAKPOINT).toUpperCase().trim();
		String dependsStr = ruleElement.getAttribute(DEPENDS).trim();

		String ifTrue = ruleElement.getAttribute(IF_TRUE).trim();
		String ifFalse = ruleElement.getAttribute(IF_FALSE).trim();

		rule.setBreakpoint(isTrue(brakpointStr) ? true : false);
		rule.setDepends(CommonUtils.handleSpecialExpression(dependsStr));

		rule.setComplexType(isTrue(isComplextype) ? true : false);
		rule.setRecursive(isTrue(recursive) ? true : false);
		rule.setMapOrList(isTrue(isMapOrList) ? true : false);
		rule.setRefId(ruleElement.getAttribute(REF_ID));
		rule.setSequence(sequence);
		rule.setIfTrue(ifTrue);
		rule.setIfFalse(ifFalse);

		rule.setDataType(XmlUtils.getNodeValueByTargetName(ruleElement, DATA_TYPE));
		String attributes = XmlUtils.getNodeValueByTargetName(ruleElement, ATTRIBUTES).trim();

		if (attributes.equals("")) {
			attributes = XmlUtils.getNodeValueByTargetName(ruleElement, INPUT_OBJECTS).trim();
		}
		rule.setInputObjects(attributes);

		String boolParam = XmlUtils.getNodeValueByTargetName(ruleElement, OUTPUT_BOOL);
		if (CommonUtils.isNullOrEmpty(boolParam)) {
			boolParam = XmlUtils.getNodeValueByTargetName(ruleElement, BOOL_PARAM);
		}
		rule.setOutputBool(boolParam.trim());

		/*
		 * Element e = XmlUtils .getFirstElementByTargetName(ruleElement, PREDICATE);
		 * String value = e.getFirstChild().getTextContent(); rule.setPredicate(value);
		 */

		String assertion = XmlUtils.getNodeValueByTargetName(ruleElement, ASSERT).trim();
		if (CommonUtils.isNullOrEmpty(assertion)) {
			assertion = XmlUtils.getNodeValueByTargetName(ruleElement, PREDICATE).trim();
		}

		rule.setPredicate(assertion);

		Element decisionObjectTarget = getDecisionElement(ruleElement);
		if (null != decisionObjectTarget) {
			setErrorObject(ruleElement, rule);
		}

		if (rule.getId().length() == 0) {
			logger.debug("a rule without specified 'id' will be ignored.");
			return null;
		}

		// the rule's input objects can be empty in v2.1.0 version
		// if (null == rule.getOutputBool()
		// || rule.getInputObjects().length() == 0) {
		// throw new Exception(
		// "Parsing the rules configuration file failed, the inputObjects
		// element' value is mandatory! "
		// + "It can not be empty, which rule id is ["
		// + rule.getId() + "]");
		// }

		Element bindNode = XmlUtils.getFirstElementByTargetName(ruleElement, "binding");
		rule.setBinding(generateBinding(bindNode));

		handlePredicateExpression(rule);
		return rule;
	}

	private Binding generateBinding(Element bindElement) {

		Binding binding = new Binding();

		if (null != bindElement) {
			binding.setWirings(bindElement.getAttribute(WIRINGS2).trim());
			binding.setForeach(bindElement.getAttribute(FOREACH).trim());
			binding.setVar(bindElement.getAttribute(VAR).trim());
			binding.setSrc(bindElement.getAttribute(SRC).trim());
		}

		return binding;
	}

	private void setErrorObject(Element ruleElement, Rule rule) throws Exception {

		String errorMessage = generateErrorMessage(ruleElement, rule);
		String sequenceId = ErrorObjectPoolManager.generateSequenceId().toString();

		errorMessage = sequenceId + VRulesConstants.$_ERROR_MESSAGE_FLAG + errorMessage;

		rule.setErrorMessage(errorMessage);
		Object unmarshalToObject;
		if (null != serializeClass) {

			Element decisionObjectTarget = getDecisionElement(ruleElement);

			Node registeredNode = null;
			if (null != decisionObjectTarget) {

				if (RULE.equals(decisionObjectTarget.getParentNode().getNodeName())) {
					if (decisionObjectTarget.getFirstChild() != null) {
						registeredNode = decisionObjectTarget.getFirstChild().getNextSibling();
					}
				} else {
					registeredNode = decisionObjectTarget;
				}

			}
			if (registeredNode != null) {
				unmarshalToObject = ObjectSerializer.unmarshalToObject(registeredNode, serializeClass);
				processPredefinedDecisionConstant(unmarshalToObject, decisionConstantDefinitionMap);
				ErrorObjectPoolManager.addError(sequenceId, unmarshalToObject);
			} else {
				logger.debug("Decision object was not defined for rule[id=" + rule.getId()
						+ "], the validation result might be ignored.");
			}
		}
	}

	private Element getDecisionElement(Element ruleElement) {

		Element decisionObjectTarget = XmlUtils.getFirstElementByTargetName(ruleElement, ERROR_OBJECT);
		if (null == decisionObjectTarget) {
			decisionObjectTarget = XmlUtils.getFirstElementByTargetName(ruleElement, DECISION_OBJECT);
		}
		if (null == decisionObjectTarget) {
			decisionObjectTarget = XmlUtils.getFirstElementByTargetName(ruleElement, DECISION);
		}
		return decisionObjectTarget;
	}

	private void processPredefinedDecisionConstant(Object unmarshalToObject,
			Map<String, String> decisionConstantDefinitionMap2) throws Exception {

		if (null == unmarshalToObject) {
			return;
		}

		Class<?> class1 = unmarshalToObject.getClass();

		Method[] methods = class1.getDeclaredMethods();

		for (Method m : methods) {
			if (m.getReturnType().equals(String.class)) {
				String methodName = m.getName();
				String value = (String) m.invoke(unmarshalToObject, null);
				String definedConstantValue = null;

				if (null != value && value.trim().length() > 0) {
					definedConstantValue = decisionConstantDefinitionMap2.get(value.trim());
				}

				if (null != definedConstantValue) {
					String setMethodName = "s" + methodName.substring(1);
					Method method = class1.getMethod(setMethodName, String.class);
					try {
						method.invoke(unmarshalToObject, definedConstantValue);
					} catch (Exception e) {
						throw new Exception("only 'String' field type is supported in "
								+ unmarshalToObject.getClass().getCanonicalName(), e);
					}
				}
			} else {
				String returnType = m.getReturnType().getName();
				if (!returnType.startsWith("java.")
						&& !m.getGenericReturnType().getClass().getName().startsWith("javax.")
						&& !m.getGenericReturnType().getClass().getName().startsWith("sun.")
						&& m.getName().startsWith("get")) {

					Object v = m.invoke(unmarshalToObject, null);
					processPredefinedDecisionConstant(v, decisionConstantDefinitionMap2);

				}
			}
		}

	}

	private void handlePredicateExpression(Rule rule) throws Exception {

		boolean hasIfExpression = ifRegexPattern.matcher(rule.getPredicate()).find();

		if ((null == rule.getOutputBool() || rule.getOutputBool().length() == 0)) {

			if (hasIfExpression) {
				throw new Exception("Syntax parsing fatal error in rule[id=" + rule.getId()
						+ "], if the boolParam parameter missed, 'if' expression is invalid for current engine. Please check it.");
			} else {
				constructPlainAssertation(rule);
			}

		} else {

			/*
			 * if not using the outBool parameter in the predicate, consider it is a bool
			 * expression but not a statement.
			 */

			if (!hasIfExpression) {
				constructPlainAssertation(rule);
			} else {
				// Using user's expression
			}
		}

		String pred = rule.getPredicate();
		pred = CommonUtils.handleSpecialExpression(pred);

		if (globalConfig.isAssertion()) {
			pred = pred + rule.getOutputBool() + "=!" + rule.getOutputBool() + ";";
		}

		rule.setPredicate(pred);

	}

	private void constructPlainAssertation(Rule rule) {

		if (!rule.getPredicate().trim().endsWith(";")) {
			// String str = rule.getPredicate();
			// str = str.substring(0, str.length() - 1);
			// rule.setPredicate(str);

			rule.setOutputBool("$");
			String predicate = "if(" + rule.getPredicate() + "){$ = true;}else{$ = false;}";
			rule.setPredicate(predicate);
		}
	}

	private String generateErrorMessage(Element ruleElement, Rule rule) throws Exception {

		Element errorElement = XmlUtils.getFirstElementByTargetName(ruleElement, ERROR_MESSAGE);

		if (null == errorElement) {
			errorElement = XmlUtils.getFirstElementByTargetName(ruleElement, MESSAGE);
		}

		if (null == errorElement) {
			return "No message was defined for rule[id=" + rule.getId() + "]";
		}

		if ((globalConfig.isReturnErrorId() || this.isTrue(errorElement.getAttribute(RETURN_ERROR_ID))
				|| this.isTrue(errorElement.getAttribute(RETURN_ID)))) {

			String errorId = errorElement.getAttribute(ID);
			if (null == errorId || errorId.length() == 0) {

				throw new Exception(
						"Configuration rules fatal error, the errorId can not be null or empty for [rule id ="
								+ rule.getId() + "], if the 'returnErrorId' element has been set 'true'!");
			}
			return errorId.trim();
			// rule.setErrorMessage(errorId);

		} else {
			String errorMsg = XmlUtils.getNodeValueByTargetName(ruleElement, ERROR_MESSAGE).trim();

			if (CommonUtils.isNullOrEmpty(errorMsg)) {
				errorMsg = XmlUtils.getNodeValueByTargetName(ruleElement, MESSAGE).trim();
			}
			// errorMsg = errorMsg.replaceAll("\\s+", " ");
			return errorMsg; // rule.setErrorMessage();
		}
	}

	public GlobalConfig getGlobalConfig(InputStream inputStream) throws Exception {

		return initGlobalConfig(XmlUtils.getDocumentFromStream(inputStream));
	}

	private GlobalConfig initGlobalConfig(Document doc) {

		globalConfig = new GlobalConfig();
		Element element = null;

		NodeList nodes = doc.getElementsByTagName(GLOBAL_CONFIG);
		if (null == nodes || nodes.getLength() == 0) {
			nodes = doc.getElementsByTagName(GLOBAL);
		}

		if (null != nodes && nodes.getLength() == 1) {
			element = (Element) nodes.item(0);
		} else {
			return globalConfig;
		}

		String returnId = XmlUtils.getNodeValueByTargetName(element, RETURN_ERROR_ID).trim();
		if (CommonUtils.isNullOrEmpty(returnId)) {
			returnId = XmlUtils.getNodeValueByTargetName(element, RETURN_ID).trim();
		}
		String returnErrorValue = XmlUtils.getNodeValueByTargetName(element, DISPLAY_ERROR_VALUE).trim();
		String includes = XmlUtils.getNodeValueByTargetName(element, INCLUDES).trim();
		String toggleBreakpointStr = XmlUtils.getNodeValueByTargetName(element, TOGGLE_BREAKPOINT).trim();
		String decisionMode = XmlUtils.getNodeValueByTargetName(element, DECISION_MODE).trim();
		String assertionLogic = XmlUtils.getNodeValueByTargetName(element, ASSERTION).trim();
		String newInstanceIfNull = XmlUtils.getNodeValueByTargetName(element, ENABLE_BINDING).trim();
		String mergeExtensions = XmlUtils.getNodeValueByTargetName(element, MERGE_EXTENSIONS).trim();

		globalConfig.setReturnErrorId(isTrue(returnId) ? true : false);
		globalConfig.setReturnErrorValue(isTrue(returnErrorValue) ? true : false);
		globalConfig.setIncludes(includes);
		globalConfig.setToggleBreakpoint(isTrue(toggleBreakpointStr) ? true : false);
		globalConfig.setDecisionMode(isTrue(decisionMode) ? true : false);
		globalConfig.setAssertion(isTrue(assertionLogic) ? true : false);
		globalConfig.setEnableBinding(isTrue(newInstanceIfNull) ? true : false);
		globalConfig.setMergeExtensions(isTrue(mergeExtensions) ? true : false);

		return globalConfig;
	}

	private boolean isTrue(String decision) {

		if (null == decision) {
			return false;
		}

		return BOOL_TRUE.equalsIgnoreCase(decision.trim()) || BOOL_ON.equalsIgnoreCase(decision.trim());
	}

	// public void registerSerializedClass(Class serializeClass) {
	//
	// this.serializeClass = serializeClass;
	// }
}
