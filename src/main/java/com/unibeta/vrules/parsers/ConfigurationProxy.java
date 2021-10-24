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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.unibeta.vrules.base.GlobalConfig;
import com.unibeta.vrules.base.ObjectEntity;
import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.Ruleset;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.servlets.URLConfiguration;
import com.unibeta.vrules.tools.CommonSyntaxs;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>ConfigurationProxy</code> is a factory for <code>ObjectEntiry</code>.
 * User can get the specified <code>ObjectEntiry</code> instance by given file
 * name.
 * 
 * @author jordan.xue
 */
public class ConfigurationProxy {

	private ConfigurationProxy() {

	}

	private static Map<String, VRuleSuite> ruleSuiteMap = Collections
			.synchronizedMap(new Hashtable<String, VRuleSuite>());
	private static Map<String, Long> modifiedTimeTable = Collections.synchronizedMap(new Hashtable<String, Long>());
	private static Map<String, Long> beatsCheckTable = Collections.synchronizedMap(new Hashtable<String, Long>());

	private static Map<String, GlobalConfig> globalConfigMap = Collections
			.synchronizedMap(new Hashtable<String, GlobalConfig>());
	private static Map<String, Map<String, List<String>>> rulesetMap = Collections
			.synchronizedMap(new Hashtable<String, Map<String, List<String>>>());
	private static boolean enableFullClassNameMode = false;

	public static boolean isEnableFullClassNameMode() {
		return enableFullClassNameMode;
	}

	public static void setEnableFullClassNameMode(boolean enableFullClassNameMode) {
		ConfigurationProxy.enableFullClassNameMode = enableFullClassNameMode;
	}

	/**
	 * Gets the instance by given fileName. fileName is the only identify for the
	 * instance. This instance is protected by thread-safe.
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static VRuleSuite getVRuleSuiteInstance(String fileName, Class decisionClass) throws Exception {

		VRuleSuite ruleSuite = getRulesConfigMap(fileName, decisionClass);
		// Map rulesConfigMap = ruleSuite.getObjectEntities();
		// ObjectEntity entity = (ObjectEntity) rulesConfigMap.get(entityId);

		return ruleSuite;
	}

	private static VRuleSuite getRulesConfigMap(String fileName, Class decisionClass) throws Exception {

		String key = buildKeyValue(fileName, decisionClass);

		VRuleSuite ruleSuite = ruleSuiteMap.get(key);
		if (null == ruleSuite || isModified(fileName, decisionClass)) {

			RulesParser rulesParser = new RulesParser(decisionClass);

			ruleSuite = rulesParser.parserRuleConfig(fileName);
			ruleSuiteMap.put(key, ruleSuite);

			modifiedTimeTable.put(key, new Long(new File(fileName).lastModified()));

			rulesetMap.put(key, getRulesetMap0(fileName, decisionClass));
		}

		return ruleSuite;
	}

	public static boolean isModified(String fileName, Class decisionClass) {

		if (CommonUtils.isNullOrEmpty(fileName)) {
			return false;
		}

		String key = buildKeyValue(fileName, decisionClass);

		Long modified = modifiedTimeTable.get(key);

		if (null == modified) {
			return true;
		}

		Long beatsCheck = beatsCheckTable.get(key);
		if (null != beatsCheck
				&& (System.currentTimeMillis() - beatsCheck) < CommonSyntaxs.getRuleFileModifiedBeatsCheckInterval()
						* 1000) {
			return false;
		} else {
			beatsCheckTable.put(key, System.currentTimeMillis());
		}

		File file = getValidFile(fileName);

		if (file.lastModified() != modified.longValue()) {
			return true;
		}
		return isIncludedModified(fileName, decisionClass);
	}
	
	private static File getValidFile(String fileName) {

		File file = new File(fileName);

		if (!file.isAbsolute()) {
			URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
			if (null != resource) {
				file = new File(resource.getPath());
			}
		}

		return file;
	}

	private static boolean isIncludedModified(String fileName, Class decisionClass) {

		GlobalConfig globalConfig = null;
		InputStream stream = null;

		if (CommonUtils.isNullOrEmpty(fileName)) {
			return false;
		}
		fileName = fileName.replace("\\", "/").replaceAll("/+", "/");

		try {
			if (globalConfigMap.get(fileName) == null) {

				if (!new File(fileName).isAbsolute()) {
					stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
				} else {
					stream = new FileInputStream(fileName);
				}

				globalConfig = getGlobalConfig(stream);
				globalConfigMap.put(fileName, globalConfig);
			} else {
				globalConfig = globalConfigMap.get(fileName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (null != globalConfig && !CommonUtils.isNullOrEmpty(globalConfig.getIncludes())) {

			String[] includesFiles = CommonUtils.fetchIncludesFileNames(globalConfig.getIncludes(), fileName);

			for (String f : includesFiles) {
				boolean modified = isModified(f, decisionClass);
				if (modified) {
					return true;
				}
			}
		}

		return false;
	}

	public static String buildKeyValue(String fileName, Class decisionClass) {

		String key = fileName.replace("\\", "/").replaceAll("/+", "/");

		if (null != decisionClass) {
			key += "_" + decisionClass.getName();
		}
		return key;
	}

	public static Map<String, Long> getModifiedTimeTable() {

		return modifiedTimeTable;
	}

	/**
	 * Sets the last valid modified time, which is compiled pass.
	 * 
	 * @param fileName
	 * @param decisionClass
	 */
	public static void updateLastModifiedTime(String fileName, Class decisionClass) {

		String key = buildKeyValue(fileName, decisionClass);

		File file = getValidFile(fileName);
		modifiedTimeTable.put(key, new Long(file.lastModified()));
	}

	/**
	 * Gets valid real path of current classpath folder.
	 * 
	 * @return
	 */
	public static String getLocalClassPath() {

		String localClassPaths = URLConfiguration.getLocalClassPaths();
		if (!CommonUtils.isNullOrEmpty(localClassPaths)) {
			return localClassPaths.split(File.pathSeparator)[0];
		} else {
			String classes = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			return classes;
		}
	}

	/**
	 * Gets GlobalConfig by rule file name
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static GlobalConfig getGlobalConfig(InputStream inputStream) throws Exception {

		return new RulesParser(null).getGlobalConfig(inputStream);
	}

	/**
	 * Gets the rule id list from ruleset by beanId.
	 * 
	 * @param fileName
	 * @param decisionClass
	 * @param beanId
	 * @return
	 * @throws Exception
	 */
	public static Map<String, List<String>> getRulesetMap(String fileName, Class decisionClass) throws Exception {

		String key = buildKeyValue(fileName, decisionClass);
		Map<String, List<String>> map = rulesetMap.get(key);

		if (null == map || isModified(fileName, decisionClass)) {
			map = getRulesetMap0(fileName, decisionClass);
			rulesetMap.put(key, map);
		}

		return map;
	}

	private static Map<String, List<String>> getRulesetMap0(String fileName, Class decisionClass) throws Exception {

		Map<String, List<String>> map = new HashMap<String, List<String>>();

		String key = buildKeyValue(fileName, decisionClass);
		VRuleSuite ruleSuite = ruleSuiteMap.get(key);// getVRuleSuiteInstance(fileName, decisionClass);

		if (null != ruleSuite && ruleSuite.getRulesets() != null && ruleSuite.getRulesets().size() > 0) {

			for (Ruleset rs : ruleSuite.getRulesets().values()) {
				List<String> list = new ArrayList<String>();

				ObjectEntity oe = ruleSuite.getObjectEntities().get(rs.getId());

				if (null != oe && oe.getRules() != null && oe.getRules().size() > 0) {
					for (Rule r : oe.getRules()) {
						if (r.getDepends() != null) {
							for (Rule r1 : oe.getRules()) {
								if (r.getDepends().contains(r1.getId())) {
									list.add(r1.getId());
								}
							}
						}
					}
				}
				for (Ruleset ruleset : ruleSuite.getRulesets().values()) {
					if (rs.getId().equals(ruleset.getId())) {
						for (Rule r : ruleset.getRules()) {
							list.add(r.getId());
						}
					}
				}

				map.put(rs.getId(), list);
			}
		}
		return map;
	}
}
