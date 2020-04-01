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
package com.unibeta.vrules.engines.dccimpls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.unibeta.vrules.base.GlobalConfig;
import com.unibeta.vrules.engines.dccimpls.rules.RulesValidation;
import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.tools.CommonSyntaxs;
import com.unibeta.vrules.tools.Java2vRules;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>CoreDccEngine</code> is the core execute engine for dynamic compiling
 * implement. Thread-Safety in high concurrence situation.
 * 
 * @author jordan.xue
 */
public class CoreDccEngine {

	static Logger log = Logger.getLogger(CoreDccEngine.class);
	static Map<String, byte[]> lockMap = new HashMap<String, byte[]>();
	String lockedFileName = "";

	/**
	 * Validates the specified object by <code>entityId</code>.
	 * 
	 * @param object
	 * @param fileName
	 *            the full path name of rule configuration located.
	 * @param entityId
	 *            the unique id the every entity or element.
	 * @return null if validate passed, otherwise return error messages.
	 */
	public String[] validate(Object object, String fileName, String entityId, Object decisionObject, String vrulesMode)
			throws Exception {

		String[] errors = null;
		Class decisionClass = null;

		if (null != decisionObject) {
			decisionClass = decisionObject.getClass();
		}

		fileName = fileName.replace("\\", "/").replaceAll("/+", "/");
		File file = new File(fileName);
		this.lockedFileName = fileName;

		if (!file.isAbsolute()) {
			String newFileName = ConfigurationProxy.getLocalClassPath() + File.separator + "vRules4j" + File.separator
					+ fileName;
			newFileName = newFileName.replace("\\", "/").replaceAll("/+", "/");

			File newFile = duplicateRuleFilesFromClassPath(fileName, decisionClass, newFileName);

			fileName = newFileName;
			file = newFile;
		}

		if (!file.exists()) {
			synchronized (this.lockedFileName) {
				if (!file.exists()) {
					digestObjectsToRules(object, fileName, decisionObject);
				}
			}
		}

		try {
			if (ConfigurationProxy.isModified(fileName, decisionClass)) {

				synchronized (this.lockedFileName) {
					if (ConfigurationProxy.isModified(fileName, decisionClass)) {
						interprete(fileName, vrulesMode, decisionClass);
						errors = invokeValidate(object, fileName, entityId, true, decisionClass);
					}
				}

			} else {
				errors = invokeValidate(object, fileName, entityId, false, decisionClass);
			}
		} catch (ClassNotFoundException e) {
			ConfigurationProxy.getModifiedTimeTable().put(ConfigurationProxy.buildKeyValue(fileName, decisionClass),
					new Long(System.currentTimeMillis()));

			log.warn(e.getMessage());
			log.info("begin re-try interprete and invoke " + fileName + " ...");

			long interval = CommonSyntaxs.getRuleFileModifiedBeatsCheckInterval();
			CommonSyntaxs.setRuleFileModifiedBeatsCheckInterval(0L);
			errors = validate(object, fileName, entityId, decisionObject, vrulesMode);
			CommonSyntaxs.setRuleFileModifiedBeatsCheckInterval(interval);

			log.info(fileName + " is interpreted and invoked successfully in second time.");
		}

		if (errors != null && errors.length > 0) {
			return errors;
		} else {
			return null;
		}
	}

	private byte[] getLock(String fileName, Class decisionClass) {

		String key = fileName + "@" + decisionClass != null ? decisionClass.getCanonicalName() : "";

		if (lockMap.get(key) == null) {
			lockMap.put(key, key.getBytes());
		}

		return lockMap.get(key);
	}

	private File duplicateRuleFilesFromClassPath(String fileName, Class decisionClass, String newFileName)
			throws Exception {

		File newFile = new File(newFileName);
		if (!newFile.getParentFile().exists()) {
			newFile.getParentFile().mkdirs();
		}

		URL srcUrl = Thread.currentThread().getContextClassLoader().getResource(fileName);

		if (null != srcUrl && (!newFile.exists() || (ConfigurationProxy.isModified(fileName, decisionClass)))) {

			synchronized (getLock(fileName, decisionClass)) {
				copyFilesToLocal(fileName, newFileName);
				ConfigurationProxy.updateLastModifiedTime(fileName, decisionClass);
			}
		}
		return newFile;
	}

	private void interprete(String fileName, String vrulesMode, Class decisionClass) throws Exception {

		if (!ConfigurationProxy.isModified(fileName, decisionClass)) {
			return;
		}

		if (CommonUtils.isNullOrEmpty(vrulesMode)) {
			vrulesMode = ConfigurationProxy.getVRuleSuiteInstance(fileName, decisionClass).getGlobalConfig()
					.isDecisionMode() ? RulesInterpreter.VRULES_ENGINE_MODE_DECISION
							: RulesInterpreter.VRULES_ENGINE_MODE_VALIDATION;
		}

		RulesInterpreter dynamicRulesInterpreter = RulesInterpreterFactory.getInstance(vrulesMode);

		String javaFile = dynamicRulesInterpreter.interprete(fileName, decisionClass);
		String result = DynamicCompiler.compile(javaFile);

		if (result != null) {

			ConfigurationProxy.getModifiedTimeTable().put(ConfigurationProxy.buildKeyValue(fileName, decisionClass),
					new Long(System.currentTimeMillis()));

			throw new Exception(
					"vRules4j dynamic compiling failed caused by the incorrect configuration, please check the rules configuration file.\nThe invalid file is located in ["
							+ fileName + "], compiling fatal errors were found below:\n" + result);
		} else {
			File srcFile = new File(javaFile);
			srcFile.delete();

			ConfigurationProxy.updateLastModifiedTime(fileName, decisionClass);
		}
	}

	private void digestObjectsToRules(Object object, String fileName, Object decisionObject) throws Exception {

		if (object instanceof Map) {
			Map map = (Map) object;

			Java2vRules.digest(map, fileName, decisionObject);
		} else if (object.getClass().isArray()) {
			Object[] objs = (Object[]) object;
			Java2vRules.digest(objs, fileName, decisionObject);
		} else {
			Java2vRules.digest(object.getClass(), fileName, decisionObject);
		}

		log.info("vRules4j-Digester auto generated rule-template file path is: " + fileName);
	}

	private boolean copyFilesToLocal(String fileName, String newFileName) throws Exception {

		boolean result = false;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

		if (is != null) {
			FileOutputStream fos = new FileOutputStream(newFileName);
			byte[] bytes = new byte[is.available()];

			is.read(bytes);
			fos.write(bytes);

			is.close();
			fos.close();
			result = true;

			copyIncludedFilesToLocal(fileName, new FileInputStream(newFileName));
		} else {
			log.warn("resouce[" + fileName + "] can not be loaded from current classpath context.");
			result = false;
		}

		return result;
	}

	private void copyIncludedFilesToLocal(String currnetFilePath, InputStream inputStream) throws Exception {

		GlobalConfig gc = ConfigurationProxy.getGlobalConfig(inputStream);
		String[] includes = CommonUtils.fetchIncludesFileNames(gc.getIncludes(), currnetFilePath);

		for (String s : includes) {

			String nf = ConfigurationProxy.getLocalClassPath() + File.separator + "vRules4j" + File.separator + s;

			File newFile = new File(nf);
			if (!newFile.getParentFile().exists()) {
				newFile.getParentFile().mkdirs();
			}

			String canonicalPath = new File(s).getPath().replace("\\", "/").replaceAll("/+", "/");
			copyFilesToLocal(canonicalPath, nf);
		}
	}

	private static String[] invokeValidate(Object object, String fileName, String entityId, boolean isModified,
			Class decisionClass) throws Exception {

		List<String> ruleIdList = null;
		RulesValidation rulesValidation = null;

		ValidationClassLoader validationClassLoader = new ValidationClassLoader(fileName, decisionClass);
		Object validator = null;

		if (!isModified) {
			validator = validationClassLoader.getValidationInstance();
		} else {
			validator = validationClassLoader.newValidationInstance();
		}

		String[] errors = null;

		if (validator instanceof RulesValidation) {
			rulesValidation = (RulesValidation) validator;
		}

		if (object instanceof Map) {

			Map<String, List<String>> map = ConfigurationProxy.getRulesetMap(fileName, decisionClass);
			errors = rulesValidation.validate((Map) object, map);
		} else {
			errors = rulesValidation.validate(object, entityId,
					ConfigurationProxy.getRulesetMap(fileName, decisionClass));
		}

		return errors;
	}

}
