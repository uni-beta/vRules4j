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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.Main;
import com.unibeta.vrules.cache.CacheManagerFactory;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.servlets.URLConfiguration;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * DynamicCompiler is used to compile the java source codes to java classes.
 * 
 * @author jordan
 */
public class DynamicCompiler {

	private static final String COMPILED_JAVA = "compiled_java";

	private static final int COMPILING_SUGGESS = 0;

	private static Logger log = LoggerFactory.getLogger(DynamicCompiler.class);

	private static final String DIRECTORY = "-d";
	private static final String CLASSPATH = "-classpath";

	/**
	 * Compile given java file and return compiling error if it is failed.
	 * 
	 * @param fileName
	 * @return null if compiled pass without no error.
	 * @throws Exception
	 */
	public static String compile(String fileName) throws Exception {

		try {
			Class.forName("com.sun.tools.javac.Main");
		} catch (ClassNotFoundException e) {

			String errrorMessage = "classpath configuration fatal error! caused by 'tools.jar' of JDK is needed, which is under JDK folder."
					+ " please copy tools.jar file from '$JAVA_HOME$/lib/tools.jar' directory manually and set it into classpath.";
			log.error(errrorMessage, e);

			throw new Exception(errrorMessage);
		}

		File javaFile = new File(fileName);
		Object catchedFileName = CacheManagerFactory.getGlobalCacheInstance().get(COMPILED_JAVA, javaFile.getName());
		if (catchedFileName == null) {
			CacheManagerFactory.getGlobalCacheInstance().put(COMPILED_JAVA, javaFile.getName(), fileName);
		} else {
			if (!catchedFileName.equals(fileName)) {
				throw new Exception(
						"[Error]duplicated class name with different file path. class name is " + javaFile.getName()
								+ ", old file path is:" + catchedFileName + "; new file path is:" + fileName);
			}
		}

		String destFolder = CommonUtils.getFilePathName(fileName) + VRulesConstants.DYNAMIC_CLASSES_FOLDER_NAME
				+ File.separator;

		File file = new File(destFolder);
		if (!file.exists()) {
			file.mkdirs();
		}

		String[] opinions = null;
		int flag = 1;

		URLConfiguration.initClasspathURLs();

		if (URLConfiguration.isInContainer() && !CommonUtils.isNullOrEmpty(URLConfiguration.getClasspath())) {
			log.debug("vRules4j gets the classpath is: " + URLConfiguration.getClasspath());

			opinions = new String[] { CLASSPATH, URLConfiguration.getClasspath(), DIRECTORY, destFolder, fileName };
		} else {
			opinions = new String[] { DIRECTORY, destFolder, fileName };
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(out);

		flag = Main.compile(opinions, printWriter);

		if (flag != COMPILING_SUGGESS) {
			log.error(out.toString());
			return out.toString();
		} else {
			return null;
		}

	}

}
