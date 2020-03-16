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
package com.unibeta.vrules.servlets;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.unibeta.vrules.utils.CommonUtils;
import com.unibeta.vrules.utils.ZipUitls;

/**
 * <code>URLConfiguration</code> is a runtime classpath configuration service,
 * which contains the common configuration value for current servlet usage.
 * 
 * @author Jordan.Xue
 */
public class URLConfiguration {

	private static final int SEARCH_BY_CLASSLOADER = 1;
	private static final int SEARCH_BY_WEB_CONTEXT_PATH = 0;
	// private static final String ENV_WINDOWS = "0";
	// private static final String ENV_UNIX_AND_LINUX = "1";
	// public static final String LIB_SEPARATOR_WINDOWS = ";";
	// public static final String LIB_SEPARATOR_UNIX_AND_LINUX = ":";

	private static boolean isInContainer = false;
	private static String classpath;
	private static String realPath = null;
	private static URL[] classpathURLs = null;
	private static String xclasspath = null;

	private static final String ZIP_SUFFIX = ".zip";
	private static final String JAR_SUFFIX = ".jar";
	private static final String LIB = "lib";
	private static final String CLASSES = "classes";
	private static final String WEB_INF_REGEX = "^WEB-INF$";

	private static Logger log = Logger.getLogger(URLConfiguration.class);
	final private static String CLASSPATH_SEPARATOR = File.pathSeparator;

	static {

	}

	public static String getXclasspath() {
		return xclasspath;
	}

	public static void setXclasspath(String xclasspath) {
		URLConfiguration.xclasspath = xclasspath;
	}

	private static URL[] generateUrlsByClasspath(String classpath) {

		List list = new ArrayList();

		if (null == classpath) {
			return (URL[]) list.toArray(new URL[] {});
		}
		String[] filePathNames = classpath.split(CLASSPATH_SEPARATOR);

		for (int i = 0; filePathNames != null && i < filePathNames.length; i++) {

			if (filePathNames[i].length() > 0) {
				File file = new File(filePathNames[i]);
				try {
					list.add(file.toURL());
				} catch (MalformedURLException e) {
					log.error("generateUrlsByClasspath from " + classpath + "failed, caused by ", e);
				}
			}
		}

		URL[] urls = (URL[]) list.toArray(new URL[] {});
		return urls;
	}

	private static String generateClassPath(String realPath, int searchType) {

		String classes = null;
		String libraries = null;

		List<String> webinfFloderList = null;

		switch (searchType) {
		case SEARCH_BY_WEB_CONTEXT_PATH: {

			webinfFloderList = filterWebInfWithJars(
					CommonUtils.searchFilesUnder(realPath, WEB_INF_REGEX, CLASSES, true));
			// URLConfiguration.setInContainer(true);
		}
		case SEARCH_BY_CLASSLOADER: {

			for (int i = 0; i <= 5; i++) {

				if (i == 0) {
					String root = CommonUtils.findSuperParentFileName(realPath, i + 1);
					webinfFloderList = filterWebInfWithJars(
							CommonUtils.searchFilesUnder(root, WEB_INF_REGEX, CLASSES, true));
				} else {
					String root = CommonUtils.findSuperParentFileName(realPath, i - 1);
					webinfFloderList = filterWebInfWithJars(
							CommonUtils.searchFilesUnder(root, WEB_INF_REGEX, CLASSES, false));
				}

				// if (webinfFloderList.size() > 0) {
				// URLConfiguration.setInContainer(true);
				// } else {
				// URLConfiguration.setInContainer(false);
				// }

				if (webinfFloderList.size() > 0) {
					break;
				}
			}
		}
		}

		StringBuffer sb = new StringBuffer();
		for (String webinf : webinfFloderList) {

			classes = webinf + File.separator + CLASSES;
			libraries = findAllLibraries(webinf + File.separator + LIB + File.separator);
			sb.append(classes + CLASSPATH_SEPARATOR + libraries + CLASSPATH_SEPARATOR);
		}

		return sb.toString();

	}

	private static List<String> filterWebInfWithJars(List<String> findFilesUnder) {

		List<String> list = new ArrayList<String>();

		if (null == findFilesUnder) {
			return list;
		}

		for (String path : findFilesUnder) {

			if (libContainsJars(findAllLibraries(path + File.separator + LIB + File.separator))) {
				list.add(path);
			}
		}

		return list;
	}

	private static boolean libContainsJars(String jarsLib) {

		return null != jarsLib && jarsLib.split(File.pathSeparator).length > 1;
	}

	private static String findAllLibraries(String fileName) {

		StringBuffer libs = new StringBuffer();

		if (CommonUtils.isNullOrEmpty(fileName)) {
			return "";
		}

		File file = new File(fileName.trim());
		if (null == file) {
			return "";
		}

		File[] files = file.listFiles();

		if (null == files) {
			return "";
		}

		for (int i = 0; files != null && i < files.length; i++) {

			File f = files[i];

			if (null == f) {
				continue;
			}

			if (f.isFile()) {
				if (f != null && null != f.getName() && (f.getName().toLowerCase().endsWith(JAR_SUFFIX))) {
					libs.append(f.getPath() + CLASSPATH_SEPARATOR);
				}
			} else if (f.isDirectory()) {
				libs.append(findAllLibraries(f.getPath()));
			}

		}

		return libs.toString();
	}

	private static void initEnvParameter() {

		// String env = conf.getInitParameter(PARAMATER_NAME_ENVIRONMENT);
		// String envName = System.getProperty("os.name");
		// String env = ENV_UNIX_AND_LINUX; // by default is linux
		//
		// if (null != envName
		// && envName.toUpperCase().contains("Windows".toUpperCase())) {
		// env = ENV_WINDOWS;// for windows
		// } else {
		// env = ENV_UNIX_AND_LINUX;// for linux
		// }
		//
		// if (null != env) {
		// env = env.trim();
		// if (ENV_UNIX_AND_LINUX.equals(env)) {
		// classpathSeparator = LIB_SEPARATOR_UNIX_AND_LINUX;
		// } else if (ENV_WINDOWS.equals(env)) {
		// classpathSeparator = LIB_SEPARATOR_WINDOWS;
		// } else {
		// classpathSeparator = LIB_SEPARATOR_UNIX_AND_LINUX;
		// }
		// }

	}

	public static URL[] getClasspathURLs() {

		return classpathURLs;
	}

	public static void setClasspathURLs(URL[] classpathURLs) {

		URLConfiguration.classpathURLs = classpathURLs;
	}

	public static String getClasspath() {

		return classpath;
	}

	public static void setClasspath(String classpath) {

		URLConfiguration.classpath = classpath;
	}

	public static boolean isInContainer() {

		return isInContainer;
	}

	public static void setInContainer(boolean isInContainer) {

		URLConfiguration.isInContainer = isInContainer;
	}

	public static String getRealPath() {

		return realPath;
	}

	public static void setRealPath(String realPath) {

		URLConfiguration.realPath = realPath;
	}

	/**
	 * Configure the extra classpath by below parameter:<br>
	 * vRules4j.classpath<br>
	 * vrules4j.classpath<br>
	 * vRules.classpath<br>
	 * <br>
	 * for example: -DvRules4j.classpath ="D:\myclasspath"
	 */
	public static void initClasspathURLs() {

		if (CommonUtils.isNullOrEmpty(URLConfiguration.getClasspath())) {

			URLConfiguration.initEnvParameter();
			String classpath = getLocalClassPaths();

			URL resource = URLConfiguration.class.getClassLoader().getResource(".");

			String parentClassLoaderLibs = "";
			if (null != resource) {
				String parentClassLoaderPath = resource.getPath();
				parentClassLoaderLibs = findAllLibraries(parentClassLoaderPath);
			}

			URLConfiguration.setClasspath(classpath + File.pathSeparator + parentClassLoaderLibs + File.pathSeparator
					+ findAllLibraries(System.getenv("CLASSPATH")));

			setExtraClasspath();
			resolveClasspath();

			URLConfiguration.setClasspathURLs(generateUrlsByClasspath(URLConfiguration.getClasspath()));
			log.debug("vRules4j classpath:" + URLConfiguration.getClasspath());
		}
	}

	private static void resolveClasspath() {

		String[] paths = URLConfiguration.getClasspath().split(File.pathSeparator);
		StringBuffer sb = new StringBuffer();

		for (String p : paths) {
			if (!CommonUtils.isNullOrEmpty(p)) {
				if (p.endsWith(".jar")) {
					sb.append(resolveSpringBootJarClasspath(p));
				} else {
					sb.append(p + File.pathSeparator);
				}
			}
		}

		URLConfiguration.setClasspath(sb.toString());

	}

	private static String resolveSpringBootJarClasspath(String p) {
		StringBuffer sb = new StringBuffer();
		try {
			JarFile jar = new JarFile(p);
			Manifest manifest = jar.getManifest();

			File srcFile = new File(p);

			if (manifest != null && manifest.getMainAttributes() != null) {
				Attributes mfs = manifest.getMainAttributes();

				String bootClassses = mfs.getValue("Spring-Boot-Classes");
				String bootLib = mfs.getValue("Spring-Boot-Lib");

				if (bootClassses == null || bootLib == null) {
					sb.append(p + File.pathSeparator);
				} else {
					log.info("springboot jar was detected from " + p);
					String dest = URLConfiguration.getRealPath();

					dest = p.substring(0, p.length() - ".jar".length());

					// if (CommonUtils.isNullOrEmpty(dest)) {
					// dest = p.substring(0, p.length() - ".jar".length());
					// } else {
					// dest = dest + "/"
					// + srcFile.getName().substring(0, srcFile.getName().length() -
					// ".jar".length());
					// }

					ZipUitls.deleteFiles(dest);
					ZipUitls.unzip(srcFile, new File(dest));
					log.info("springboot jar was resolved success from " + p);

					sb.append(dest + File.separator + bootClassses + File.pathSeparator);

					String libs = dest + File.separator + bootLib;
					sb.append(findAllLibraries(libs));

					log.info("revolved springboot classpath is:" + sb.toString());
				}
			} else {
				sb.append(p + File.pathSeparator);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	private static void setExtraClasspath() {

		String paths = System.getProperty("vRules4j.classpath");
		if (CommonUtils.isNullOrEmpty(paths)) {
			paths = System.getProperty("vrules4j.classpath");
		}
		if (CommonUtils.isNullOrEmpty(paths)) {
			paths = System.getProperty("vrules.classpath");
		}

		if (CommonUtils.isNullOrEmpty(paths)) {
			paths = System.getenv("VRULES_CLASSPATH");
		}

		if (!CommonUtils.isNullOrEmpty(paths)) {
			String[] ps = paths.split(",");
			for (String s : ps) {
				URLConfiguration.setClasspath(URLConfiguration.getClasspath() + File.pathSeparator + findAllLibraries(s)
						+ File.pathSeparator + s + File.pathSeparator);
			}
		}
		xclasspath = paths;
	}

	public static String getLocalClassPaths() {
		String classpath = null;

		if (!CommonUtils.isNullOrEmpty(URLConfiguration.getRealPath())) {
			classpath = generateClassPath(URLConfiguration.getRealPath(), SEARCH_BY_WEB_CONTEXT_PATH);
		} else {
			String classes = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			classpath = generateClassPath(classes, SEARCH_BY_CLASSLOADER);

			if (CommonUtils.isNullOrEmpty(classpath)) {
				classpath = classes;
			}
		}

		return classpath;
	}
}
