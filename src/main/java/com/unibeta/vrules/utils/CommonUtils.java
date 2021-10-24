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
package com.unibeta.vrules.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.unibeta.vrules.parsers.ConfigurationProxy;

/**
 * A common util class.
 * 
 * @author jordan.xue
 */
public class CommonUtils {

	private static final String ONE_POINT_SEPARATOR = ".";
	private static final String SEMICOLON_SEPARATOR = ";";
	private static final String COMMA_SEPARATOR = ",";
	private static final String CURRENT_PATH_SEPARATOR = "./";
	private static final String TWO_POINTS_SEPARATOR = "..";
	private static final String SUPER_PATH_SEPARATOR = "../";

	/**
	 * Converts the array object to list.
	 * 
	 * @param objs
	 *            Object array
	 * @param list
	 *            aimed List
	 */
	public static void copyArrayToList(Object[] objs, List list) {

		if (null == objs) {
			return;
		}

		if (null == list) {
			list = new ArrayList();
		}

		for (int i = 0; i < objs.length; i++) {
			list.add(objs[i]);
		}

	}

	/**
	 * Judges whether the object of specified type
	 * 
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public static boolean isInterfaceOf(Object obj, Class clazz) {

		List list = new ArrayList();

		CommonUtils.copyArrayToList(obj.getClass().getInterfaces(), list);
		if (list.contains(clazz)) {
			return true;
		}

		return false;
	}

	/**
	 * Judges whether the object of specified type
	 * 
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public static boolean isInterfaceOf(Class cls, Class clazz) {

		List list = new ArrayList();

		CommonUtils.copyArrayToList(cls.getInterfaces(), list);
		if (list.contains(clazz)) {
			return true;
		}

		return false;
	}

	/**
	 * Gets the simple anme of the class. For example, if the class name is
	 * "java.util.Map", the simple should be "Map".
	 * 
	 * @param clazz
	 * @return the class'simple name.
	 */
	public static String getClassSimpleName(Class clazz) {

		if (null == clazz) {
			return null;
		}
		String name = clazz.getName();
		// int index = name.lastIndexOf(".");
		// name = name.substring(index + 1);

		return getClassSimpleName(name);
	}

	/**
	 * Gets the simple anme of the class. For example, if the class name is
	 * "java.util.Map", the simple should be "Map".
	 * 
	 * @param clazz
	 * @return the class'simple name.
	 */
	private static String getClassSimpleName(String name) {

		// String name = clazz.getName();
		if (null == name) {
			return null;
		}

		int index = name.lastIndexOf(ONE_POINT_SEPARATOR);
		name = name.substring(index + 1);

		return name;
	}

	/**
	 * Gets the file patch name by given <code>fileName</code> value.
	 * 
	 * @param fileName
	 * @return the patch name.
	 */
	public static String getFilePathName(String fileName) {

		String filePath = null;
		File file = new File(fileName);

		if (file.isDirectory()) {
			return fileName;
		} else {
			String name = null;

			name = file.getName();
			int i = fileName.lastIndexOf(name);

			filePath = fileName.substring(0, i) + File.separator;
			return filePath;
		}

	}

	/**
	 * Fetches the file simple name by given file name.
	 * 
	 * @param fileName
	 * @return null if the fileNanme is of a directory. otherwise return the file
	 *         name.
	 */
	public static String getFileSimpleName(String fileName) {

		File file = new File(fileName);
		String name = file.getName();

		String fullName = file.getName();
		if (fullName.lastIndexOf(".") > 0) {
			name = fullName.substring(0, fullName.lastIndexOf("."));
		}

		return name;
	}

	private static String getFileFullName(String fileName) {

		File file = new File(fileName);
		String name = file.getPath();

		String fullName = file.getPath();
		if (fullName.lastIndexOf(".") > 0) {
			name = fullName.substring(0, fullName.lastIndexOf("."));
		}

		return name;
	}

	/**
	 * Sets the first letter to type to match the class name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String generateClassNameByFileFullName(String fileName) {

		if (null == fileName) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		// String filePathName = getFilePathName(fileName);
		String fileFullName = ConfigurationProxy.isEnableFullClassNameMode() ? getFileFullName(fileName)
				: getFileSimpleName(fileName);

		String name = fileFullName.trim().replace(".", "-").replace("@", "-").replace("#", "-").replace("$", "-")
				.replace("&", "-").replace("*", "-").replace("^", "-").replace("%", "-").replace("!", "-")
				.replace(",", "-").replace("/", "-").replace("\\", "-").replace("+", "-").replace("?", "-")
				.replace(":", "-").replace("(", "-").replace(")", "-").replace("[", "-").replace("]", "-")
				.replace("{", "-").replace("}", "-");

		int maxLength = 127;

		if (name.length() > maxLength) {
			name = "v-" + name.substring(name.length() - maxLength);
		}

		String[] names = name.split("-");

		for (String n : names) {

			if (CommonUtils.isNullOrEmpty(n)) {
				continue;
			}

			if (n.trim().length() == 1) {
				sb.append(n.trim().toUpperCase());
			} else {
				String str = n.substring(0, 1).toUpperCase() + n.substring(1);
				sb.append(str.trim());
			}

		}

		return sb.toString();
	}

	/**
	 * Converts the Array Map or List object to a List.
	 * 
	 * @param obj
	 * @return
	 */
	public static List getRepeatedObjectList(Object obj) {

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

	/**
	 * Try to find specified file called <code>findFileName</code> under the root
	 * file <code>rootFileName</code>.
	 * 
	 * @param rootFileName
	 *            - the root file name.
	 * @param targetFileNameRegex
	 *            - the file to be find under the root file nmae's folder, it is
	 *            regex expression.
	 * @param ignoreRegex
	 * @param needNested
	 * @return - the found file's name if find successfully; otherwise return null.
	 */
	public static List<String> searchFilesUnder(String rootFileName, String targetFileNameRegex, String ignoreRegex,
			boolean needNested) {

		List<String> list = new ArrayList<String>();

		if (CommonUtils.isNullOrEmpty(rootFileName) || CommonUtils.isNullOrEmpty(targetFileNameRegex)) {
			return list;
		}

		Pattern ignorePattern = null;
		Pattern targetPattern = Pattern.compile(targetFileNameRegex);

		if (!CommonUtils.isNullOrEmpty(ignoreRegex)) {
			ignorePattern = Pattern.compile(ignoreRegex, Pattern.DOTALL);
		} else {
			ignorePattern = Pattern.compile("^<>&#@!$<>", Pattern.DOTALL);
		}

		File parent = new File(rootFileName);
		File[] subFiles = parent.listFiles();

		if (null == parent) {
			return list;
		}

		if (null != ignorePattern) {
			if (targetPattern.matcher(parent.getName()).find() && !ignorePattern.matcher(parent.getName()).find()) {
				list.add(parent.getPath());
			}
		} else {
			if (targetPattern.matcher(parent.getName()).find()) {
				list.add(parent.getPath());
			}
		}

		String findPath = null;
		if (null != subFiles && subFiles.length > 0) {

			for (int i = 0; i < subFiles.length; i++) {
				if (null != ignorePattern) {
					if (needNested && subFiles[i].isDirectory()) {
						list.addAll(
								searchFilesUnder(subFiles[i].getPath(), targetFileNameRegex, ignoreRegex, needNested));
					} else if (targetPattern.matcher(subFiles[i].getName()).find()
							&& !ignorePattern.matcher(subFiles[i].getName()).find()) {
						findPath = subFiles[i].getPath();
						if (findPath != null) {
							list.add(findPath);
						}
					}
				} else {
					if (needNested && subFiles[i].isDirectory()) {
						list.addAll(
								searchFilesUnder(subFiles[i].getPath(), targetFileNameRegex, ignoreRegex, needNested));
					} else if (targetPattern.matcher(subFiles[i].getName()).find()) {
						findPath = subFiles[i].getPath();
						if (findPath != null) {
							list.add(findPath);
						}
					}
				}
			}

		}

		return list;
	}

	/**
	 * Find the file from child to parent.
	 * 
	 * @param fileName
	 * @param targetFileNameRegex
	 * @return the fond file full path.
	 */
	public static List<String> searchFilesAbove(String fileName, String targetFileNameRegex, String ignoreRegex,
			int superIndex) {

		List<String> list = new ArrayList<String>();

		if (CommonUtils.isNullOrEmpty(fileName) || CommonUtils.isNullOrEmpty(targetFileNameRegex)) {
			return list;
		}

		Pattern targetPattern = Pattern.compile(targetFileNameRegex);
		Pattern ignorePattern = null;

		if (!CommonUtils.isNullOrEmpty(ignoreRegex)) {
			ignorePattern = Pattern.compile(ignoreRegex, Pattern.DOTALL);
		}

		File file = new File(fileName);
		File parent = file;

		if (null == parent) {
			return list;
		}

		if (targetPattern.matcher(parent.getName()).find() && null != ignorePattern
				&& !ignorePattern.matcher(parent.getName()).find()) {
			list.add(parent.getPath());
		}

		if (parent == null) {
			return list;
		}

		int index = 0;
		do {
			File tp = parent.getParentFile();
			if (null == tp) {
				break;
			} else {
				parent = tp;
				File[] files = parent.listFiles();
				for (int i = 0; i < files.length; i++) {
					list.addAll(searchFilesUnder(files[i].getPath(), targetFileNameRegex, ignoreRegex, false));

				}
			}

			index++;
		} while (parent != null && parent.isDirectory() && index < superIndex);

		return list;
	}

	/**
	 * Find given file's parent path with superIndex.
	 * 
	 * @param currentFileName
	 * @param superIndex
	 * @return
	 */
	public static String findSuperParentFileName(String currentFileName, int superIndex) {

		File f = new File(currentFileName);
		File p = f;

		for (int i = 0; i < superIndex; i++) {
			if (null != p.getParentFile()) {
				p = p.getParentFile();
			} else {
				break;
			}
		}

		return p.getPath();
	}

	/**
	 * Digest the inclues file name via given current file name. '../' stands for
	 * the previous patch; './' stands for current sub folder name.
	 * 
	 * @param includes
	 *            included files abstract patch.
	 * @param currentFileName
	 *            current file full path.
	 * @return the included file names array.
	 */
	public static String[] fetchIncludesFileNames(String includes, String currentFileName) {

		if (null == includes || includes.trim().length() <= 0) {
			return new String[0];
		}

		if (currentFileName == null) {
			return new String[0];
		}

		// String separator1 = generateFileSeparator(includes);
		// String separator2 = generateFileSeparator(currentFileName);

		String filePath = getFilePathName(currentFileName);

		String split = COMMA_SEPARATOR;
		if (includes.indexOf(COMMA_SEPARATOR) < 0) {
			split = SEMICOLON_SEPARATOR;
		}

		String[] includedFiels = includes.split(split);

		for (int i = 0; i < includedFiels.length; i++) {
			String str = includedFiels[i].trim();
			String abstractFilePath = getAbstractFilePath(str);
			String crrentPath = filePath;

			if (str.indexOf(SUPER_PATH_SEPARATOR) > -1) {
				crrentPath = getAbstractParentPath(str, crrentPath);

				includedFiels[i] = crrentPath + abstractFilePath;
			} else {
				includedFiels[i] = filePath + abstractFilePath;
			}

		}

		return includedFiels;
	}

	private static String getAbstractParentPath(String str, String crrentPath) {

		int times = getSuperDirTimes(str);

		for (int j = 0; j < times; j++) {
			File file = new File(crrentPath);
			file = file.getParentFile();

			if (file != null) {
				crrentPath = file.getPath();
			} else {
				crrentPath = "/";
			}
		}
		return crrentPath;
	}

	private static int getSuperDirTimes(String str) {

		int times = 0;
		while (str.indexOf(TWO_POINTS_SEPARATOR) > -1) {
			str = str.substring(0, str.lastIndexOf(SUPER_PATH_SEPARATOR));
			times++;
		}
		return times;
	}

	private static String getAbstractFilePath(String str) {

		if (null == str) {
			return null;
		}

		int flag = str.lastIndexOf(CURRENT_PATH_SEPARATOR);
		if (flag >= 0) {
			str = str.substring(flag + 1);
		}

		return str;
	}

	/**
	 * Handles the sprecial expression for calculation operation.
	 * 
	 * @param pred
	 * @return
	 */
	public static String handleSpecialExpression(String pred) {

		if (CommonUtils.isNullOrEmpty(pred)) {
			return pred;
		}

		pred = pred.replace("@right_unsigned_shift_assign", ">>>=");
		pred = pred.replace("@right_shift_assign", ">>=");
		pred = pred.replace("@left_shift_assign", "<<=");
		pred = pred.replace("@or_assign", "|=");
		pred = pred.replace("@and_assign", "&=");
		pred = pred.replace("@right_unsigned_shift", ">>>");
		pred = pred.replace("@right_shift", ">>");
		pred = pred.replace("@left_shift", "<<");
		pred = pred.replace("@bitwise_or", "|");
		pred = pred.replace("@bitwise_and", "&");
		pred = pred.replace("@and", "&&");
		pred = pred.replace("@or", "||");
		pred = pred.replace("@lteq", "<=");
		pred = pred.replace("@gteq", ">=");
		pred = pred.replace("@gt", ">");
		pred = pred.replace("@lt", "<");

		return pred;
	}

	/**
	 * Reserves the special exression. refer to handleSpecialExpression.
	 * 
	 * @param pred
	 * @return
	 */
	public static String reverseSpecialExpression(String pred) {

		// pred = pred.replace(">>>=", "@right_unsigned_shift_assign");
		// pred = pred.replace(">>=", "@right_shift_assign");
		pred = pred.replace("<<=", "@left_shift_assign");
		// pred = pred.replace("|=", "@or_assign");
		// pred = pred.replace("&=", "@and_assign");
		// pred = pred.replace(">>>", "@right_unsigned_shift");
		// pred = pred.replace(">>", "@right_shift");
		pred = pred.replace("<<", "@left_shift");
		// pred = pred.replace("|", "@bitwise_or");
		pred = pred.replace("&&", "@and");
		pred = pred.replace("&", "@bitwise_and");
		// pred = pred.replace("||", "@or");
		pred = pred.replace("<=", "@lteq");
		// pred = pred.replace(">=", "@gteq");
		// pred = pred.replace(">", "@gt");
		pred = pred.replace("<", "@lt");
		return pred;
	}

	// private static String generateFileSeparator(String path) {
	//
	// String separator = "/";
	// if (path.indexOf(separator) < 0) {
	// separator = File.separator;
	// }
	// return separator;
	// }

	/**
	 * Judges whether the input object is null or empty. Return true, if the obj is
	 * null or obj.toString().trim().length()==0;
	 */
	public static boolean isNullOrEmpty(Object obj) {

		if (null == obj) {
			return true;
		} else {
			if (obj.toString().trim().length() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Copies the source file to destination file. If srcFileName name equals to
	 * destFileName, the file will not be copied.
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @throws Exception
	 */

	public static void copyFile(String src, String dest) throws Exception {

		File destFile = new File(dest);
		FileInputStream srcIs = new FileInputStream(src);
		FileOutputStream destOut = new FileOutputStream(destFile);

		byte[] data = new byte[srcIs.available()];

		if (!destFile.exists()) {
			destFile.mkdirs();
		}
		srcIs.read(data);
		destOut.write(data);

		srcIs.close();
		destOut.close();
	}

	/**
	 * Copy file or entire folder contents to dest path.
	 * 
	 * @param src
	 * @param dest
	 * @throws Exception
	 */
	public static void copyFiles(String src, String dest) throws Exception {

		if (CommonUtils.isNullOrEmpty(src) || CommonUtils.isNullOrEmpty(dest)) {
			return;

		}

		File srcFile = new File(src);
		File destFile = new File(dest);

		if (!srcFile.exists()) {
			throw new Exception(src + " is not exist");
		}

		if (srcFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			copyFile(src, dest);
		} else {
			File[] files = srcFile.listFiles();
			if (null != files) {
				for (File f : files) {
					copyFiles(f.getPath(), dest + File.separator + f.getName());
					;
				}
			}
		}
	}

}
