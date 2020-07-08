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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unibeta.vrules.parsers.ObjectSerializer;
import com.unibeta.vrules.utils.CommonUtils;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * The common util functions for Interpreter.
 * 
 * @author jordan.xue
 * @since v2.1.2
 * @Date 2009-06-20
 */
public class InterpreterUtils {

	private static final String STRING_FORMAT = "@StRiNg-FoRmAt@";
	private static final String REGEX_EVAL_EXPRESSION = "\\{[\\s\\S]*\\}";
	private static Logger log = LoggerFactory.getLogger(InterpreterUtils.class);
	private static Pattern evalPattern = Pattern.compile(REGEX_EVAL_EXPRESSION, Pattern.DOTALL);
	private static Boolean $hasBsh = null;
	private static ThreadLocal<Interpreter> bshThreadLocal = new ThreadLocal<Interpreter>();

	static public Interpreter getBsh() {
		Interpreter bsh = bshThreadLocal.get();

		if (null == bsh) {
			bsh = new Interpreter();
			bshThreadLocal.set(bsh);
		}

		return bsh;

	}

	/**
	 * Gets the value by context input.
	 * 
	 * @param dataMap
	 * @param context
	 * @return data map's value by key if hit value by key, otherwise return value
	 *         that can be wrapped by classname.
	 * @throws Exception
	 */
	public static Object getValueFromCollectionByContext(Map<String, Object> dataMap, String className, String name)
			throws Exception {

		if (dataMap.get(name) != null) {
			return dataMap.get(name);
		} else {
			return getValueFromCollectionByClassName(dataMap.values(), className);
		}
	}

	/**
	 * Gets the value if the given className is equal or assignable with the value
	 * instance from map.
	 * 
	 * @param map
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Object getValueFromCollectionByClassName(Collection<Object> c, String className) throws Exception {

		Object obj = null;

		if (null == c) {
			return null;
		}

		for (Object o : c) {
			if (null != o) {
				Class<?> type = null;

				try {
					if (!o.getClass().isArray()) {
						type = Class.forName(className);

					} else {
						className = "L" + className;

						while (className.lastIndexOf("[]") > 0) {
							className = "[" + className.substring(0, className.lastIndexOf("[]"));
						}

						type = Class.forName(className + ";");
					}

					if (o.getClass().getCanonicalName().equals(className) || type.isAssignableFrom(o.getClass())
							|| CommonUtils.isInterfaceOf(o, type)) {
						obj = o;
						break;
					}
				} catch (ClassNotFoundException e) {
					if (o.getClass().getCanonicalName().equals(className)) {
						obj = o;
						break;
					}
				}

			}
		}

		return obj;
	}

	/**
	 * Gets the devlared filed type from className by given fieldName.
	 * 
	 * @param className
	 * @param attributeName
	 * @return
	 */
	public static Class getDeclaredFieldType(String className, String fieldName) {

		Class type = null;
		try {
			Class clazz = Class.forName(className);
			Field field = clazz.getDeclaredField(fieldName);

			type = field.getType();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return type;
	}

	/**
	 * Check whether contains BeanShell in current classpath.
	 * 
	 * @return true if has, otherwise return false.
	 */
	public static boolean hasBsh() {

		if (null != $hasBsh) {
			return $hasBsh;
		}

		try {
			Class.forName("bsh.Interpreter");
		} catch (ClassNotFoundException e) {
			log.warn("'bsh.Interpreter' was not found in current classpath, relevant evaluation will be disabled. ");
			$hasBsh = false;
			return false;
		}

		$hasBsh = true;
		return true;
	}

	/**
	 * Evaluates the input msg with give bsh context.
	 * 
	 * @param bsh
	 * @param msg
	 * @return
	 */
	public static String bshEval(Interpreter bsh, String msg) {

		if (!hasBsh() || null == bsh || null == msg || !evalPattern.matcher(msg).find()) {
			return msg;
		}
		String[] exprs = msg.split("#");
		String result = new String(msg);

		// List<String> results = new ArrayList<String>();
		// StringBuffer format = new StringBuffer();
		for (String expr : exprs) {
			if (evalPattern.matcher(expr).find()) {

				int end = resolveValidEndIndex(expr);// expr.indexOf("}");

				if (end < 0) {
					continue;
				}
				String express = expr.substring(1, end);
				Object o = null;
				String segment = "#" + expr.substring(0, end + 1);
				String r = null;
				try {
					o = bsh.eval(express.replaceAll("\\s+", " "));
					r = ObjectSerializer.xStreamToXml(o);
					// results.add(r);
				} catch (EvalError e) {
					String errormsg = "'evaluate #{" + express + "} error, caused by " + e.getMessage() + "'";
					log.error(errormsg, e);
					// results.add(errormsg);
					r = "[" + errormsg + "]";
				}

				result = result.replace(segment, r);

			}
			// format.append(expr.replaceAll(REGEX_EVAL_EXPRESSION,
			// STRING_FORMAT).replace("%", "%%")
			// .replace(STRING_FORMAT, "%s"));
		}

		// return String.format(format.toString(), results.toArray());
		return result;
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * String msg = "this is 40%s, and value is #{print(\"hello\");return \"yes\"}";
	 * String s = InterpreterUtils.bshEval(new Interpreter(), msg);
	 * System.out.println(s); }
	 */

	private static int resolveValidEndIndex(String expr) {

		if (null == expr) {
			return -1;
		}

		Stack<Integer> stack = new Stack<Integer>();
		int lastIndex = expr.lastIndexOf("}");

		for (int i = lastIndex; i > 0; i--) {
			if (expr.charAt(i) == '}') {
				stack.push(i);
			}

			if (expr.charAt(i) == '{' && !stack.isEmpty()) {
				stack.pop();
			}
		}
		if (!stack.isEmpty()) {
			return stack.pop();
		} else {
			return -1;
		}
	}

	/**
	 * Evaluates the input msg with give bsh context.
	 * 
	 * @param bsh
	 * @param msg
	 * @return
	 */
	public static String plainEval(Object o, String msg) {

		if (null == msg) {
			return msg;
		}

		String result = "";
		if (evalPattern.matcher(msg).find()) {
			int start = msg.indexOf("${");
			String prefix = "${";
			int end = msg.lastIndexOf("}");

			if (start < 0) {
				start = msg.indexOf("#{");
				prefix = "#{";
			}

			String express = msg.substring(start + 2, end);

			result = msg.replace(prefix + express + "}", null == o ? "null" : o.toString());
		} else {
			result = msg.trim();
		}

		return result.trim();
	}

	public static String findExpression(String msg) {

		if (null == msg) {
			return null;
		}

		if (evalPattern.matcher(msg).find()) {
			int start = msg.indexOf("${");
			String prefix = "${";
			int end = msg.lastIndexOf("}");

			if (start < 0) {
				start = msg.indexOf("#{");
				prefix = "#{";
			}

			String express = msg.substring(start + 2, end);
			return express;

		} else {
			return null;
		}
	}

	/**
	 * Assign variable's value to given bsh.
	 * 
	 * @param bsh
	 * @param name
	 * @param obj
	 */
	public static void bshSet(Interpreter bsh, String name, Object obj) {

		if (null == bsh) {
			return;
		}

		try {
			bsh.set(name, obj);
		} catch (EvalError e) {
			log.error("BeanShell set variable error caused by " + e.getMessage(), e);
		}
	}

	/**
	 * Unassign variable from given bsh.
	 * 
	 * @param bsh
	 * @param name
	 */
	public static void bshUnset(Interpreter bsh, String name) {

		if (null == bsh) {
			return;
		}

		// try {
		// bsh.unset(name);
		// } catch (EvalError e) {
		// log.error("BeanShell unset variable error caused by " + e.getMessage(), e);
		// }
	}

	public static boolean isNumericType(String className) {

		if (JAVA_NUMBERRIC_TYPE_LIST.contains(className)) {
			return true;
		} else {

			return false;
		}
	}

	public static boolean isBooleanType(String className) {

		if (boolean.class.getCanonicalName().equals(className) || Boolean.class.getCanonicalName().equals(className)) {
			return true;
		} else {
			return false;
		}
	}

	private static List<String> JAVA_NUMBERRIC_TYPE_LIST = buildNumericTypeList();

	private static List<String> buildNumericTypeList() {

		JAVA_NUMBERRIC_TYPE_LIST = new ArrayList<String>();
		JAVA_NUMBERRIC_TYPE_LIST.add(int.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(long.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(byte.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(short.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(float.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(double.class.getName());
		JAVA_NUMBERRIC_TYPE_LIST.add(char.class.getName());

		return JAVA_NUMBERRIC_TYPE_LIST;
	}

	@SuppressWarnings("unchecked")
	public static List getRepeatedObjectList(Object obj) {

		List list = new ArrayList();

		if (null == obj) {
			return list;
		}

		if (obj instanceof Object[]) {
			Object[] objs = (Object[]) obj;
			CommonUtils.copyArrayToList(objs, list);
		} else if (CommonUtils.isInterfaceOf(obj, Map.class)) {
			Map map = (Map) obj;
			Set keys = map.keySet();

			list.addAll(map.values());
			// for (Iterator i = keys.iterator(); i.hasNext();) {
			// list.add(map.get(i.next()));
			// }
		} else if (CommonUtils.isInterfaceOf(obj, List.class)) {
			List ls = (List) obj;
			for (Iterator i = ls.iterator(); i.hasNext();) {
				list.add(i.next());
			}
		}
		return list;
	}

	/**
	 * Converts the array object to list.
	 * 
	 * @param objs
	 *            Object array
	 * @param list
	 *            aimed List
	 */
	public static void copyArrayToList(Object[] objs, List list) {

		CommonUtils.copyArrayToList(objs, list);
	}
}
