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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationErrorMessage;
import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationErrorXPath;
import com.unibeta.vrules.constant.VRulesConstants;
import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.parsers.ErrorObjectPoolManager;
import com.unibeta.vrules.reflect.ObjectReflector;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * <code>ValidationEngineDccImpl</code> is implemented by dynamic compiling
 * technical of java. The purpose is for enhancing the performance.
 * 
 * @author jordan.xue
 */
public class ValidationEngineDccImpl implements ValidationEngine {

	private static final String $JORDAN_XUE$ = "$jordan.xue$";
	private static final String GET = "get";
	private static final String SET = "set";

	private String currentErrorSequenceId = "0";
	protected String engineMode = null;

	/**
	 * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
	 */
	public String[] validate(Object object, String fileName) throws Exception {

		if (null == object) {
			throw new RuntimeException("The input valdation object is null!");
		}

		String id = CommonUtils.getClassSimpleName(object.getClass());
		return parseErrorStrings(validate(object, fileName, id));
	}

	/**
	 * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
	 */
	public String[] validate(Object object, String fileName, String entityId) throws Exception {

		if (null == object) {
			throw new RuntimeException("The input valdation object is null!");
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();

		return parseErrorStrings(coreDccEngine.validate(object, fileName, entityId, null, engineMode));
	}

	/**
	 * @see com.unibeta.vrules.engines.ValidationEngine.validate()$
	 */
	public String[] validate(Object[] objects, String fileName) throws Exception {

		if (null == objects) {
			throw new RuntimeException("The input valdation object is null!");
		}

		Map objMap = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];

			if (null != object) {
				objMap.put(CommonUtils.getClassSimpleName(object.getClass()), object);
			}
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();
		return parseErrorStrings(coreDccEngine.validate(objMap, fileName, null, null, engineMode));
	}

	public List validate(Object[] objects, String fileName, Object errorObj) throws Exception {

		if (null == objects) {
			throw new RuntimeException("The input valdation object is null!");
		}
		if (null == errorObj) {
			throw new RuntimeException("The input register error object is null!");
		}

		Map objMap = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (null != object) {
				objMap.put(CommonUtils.getClassSimpleName(object.getClass()), object);
			}
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();
		String[] errorStrs = (String[]) coreDccEngine.validate(objMap, fileName, null, errorObj, engineMode);

		return generateErrorObject(errorStrs, errorObj.getClass());
	}

	public List validate(Object object, String fileName, Object errorObj) throws Exception {

		if (null == object || null == errorObj) {
			throw new RuntimeException("The input valdation object or register error object is null!");
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();

		String id = CommonUtils.getClassSimpleName(object.getClass());
		String[] errorStrs = coreDccEngine.validate(object, fileName, id, errorObj, engineMode);

		return generateErrorObject(errorStrs, errorObj.getClass());
	}

	public List validate(Object object, String fileName, String entityId, Object errorObj) throws Exception {

		if (null == object || null == errorObj) {
			throw new RuntimeException("The input valdation object or register error object is null!");
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();

		String[] errorStrs = coreDccEngine.validate(object, fileName, entityId, errorObj, engineMode);

		return generateErrorObject(errorStrs, errorObj.getClass());
	}

	private List generateErrorObject(String[] errorStrs, Class class1) throws Exception {

		if (null == errorStrs || errorStrs.length == 0) {
			return null;
		}

		List list = new ArrayList();
		for (String err : errorStrs) {

			int i = err.indexOf(VRulesConstants.$_ERROR_MESSAGE_FLAG);

			String id = null;
			if (i < 0) {
				// continue;
				err = $JORDAN_XUE$ + VRulesConstants.NILLABLE_ERROR + ": " + err;
				id = currentErrorSequenceId;
			} else {
				id = err.substring(0, i);
				currentErrorSequenceId = id;
			}

			Object error = new Object();
			Object error2 = ErrorObjectPoolManager.getError(id);

			if (null != error2) {
				error = BeanUtils.cloneBean(error2);
			}

			if (null != error) {

				String errorMsg = err;
				if (errorMsg.indexOf($JORDAN_XUE$) < 0) {
					errorMsg = err.substring(
							err.indexOf(VRulesConstants.$_ERROR_MESSAGE_FLAG)
									+ VRulesConstants.$_ERROR_MESSAGE_FLAG.length(),
							err.indexOf(VRulesConstants.$_X_PATH));
				}
				String xPath = err.substring(err.indexOf(VRulesConstants.$_X_PATH) + VRulesConstants.$_X_PATH.length());

				error = fillValidationResultValue(errorMsg, error);
				error = fillValidationResultValue(xPath, error);

				list.add(error);
			}
		}

		// printErrorInfo();
		return list;
	}

	private Object fillValidationResultValue(String value, Object errorObj) throws Exception {

		Class cls = errorObj.getClass();

		Field[] fields = ObjectReflector.getAllJavaBeanFields(cls);

		for (Field field : fields) {

			recursiveIntoSubObject(value, errorObj, cls, field);

			Annotation[] annotations = field.getAnnotations();
			if (annotations != null) {

				for (Annotation tag : annotations) {
					String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					if (tag instanceof ValidationErrorXPath) {
						String name = SET + fieldName;

						Method mtd = cls.getMethod(name, field.getType());
						mtd.invoke(errorObj, value);

						// field.set(error, xPath);
					} else if (tag instanceof ValidationErrorMessage) {
						String setName = SET + fieldName;
						String getName = GET + fieldName;

						Method getMethod = cls.getMethod(getName, new Class[] {});
						Object obj = getMethod.invoke(errorObj, null);

						if (null == obj || obj.toString() == null || obj.toString().trim().length() == 0
								|| value.indexOf($JORDAN_XUE$) >= 0) {

							if (value.indexOf($JORDAN_XUE$) >= 0) {
								value = value.substring(12);
							}

							Method setMethod = cls.getMethod(setName, field.getType());
							setMethod.invoke(errorObj, value);

						}
					}
				}
			}

		}
		return errorObj;
	}

	private void recursiveIntoSubObject(String value, Object errorObj, Class cls, Field field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {

		if (!field.getType().isPrimitive() && !field.getType().getName().startsWith(VRulesConstants.JAVA_OBJECTS)) {

			String fieldName = field.getName();
			String methodName = GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			Method m = cls.getMethod(methodName, null);

			Object subObj = m.invoke(errorObj, null);

			if (null == subObj) {
				return;
			}

			if (subObj.getClass().isArray()) {
				Object[] objs = (Object[]) subObj;
				for (Object o : objs) {
					if (null != o) {
						fillValidationResultValue(value, o);
					}
				}

			} else {
				fillValidationResultValue(value, subObj);
			}
		}
	}

	private String[] parseErrorStrings(String[] strs) {

		if (null == strs) {
			return null;
		}

		for (int i = 0; i < strs.length; i++) {
			String str = strs[i];

			if (str != null) {
				int lastIndexOf = str.lastIndexOf(VRulesConstants.$_ERROR_MESSAGE_FLAG);

				if (lastIndexOf >= 0) {
					str = str.substring(lastIndexOf + VRulesConstants.$_ERROR_MESSAGE_FLAG.length());
				}
			}

			strs[i] = str;
		}

		return strs;
	}

	public String[] validate(Map<String, Object> objMap, String fileName) throws Exception {

		if (null == objMap) {
			throw new RuntimeException("The input valdation object is null!");
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();
		return parseErrorStrings(coreDccEngine.validate(objMap, fileName, null, null, engineMode));
	}

	public List validate(Map<String, Object> objMap, String fileName, Object errorObj) throws Exception {

		if (null == objMap || null == errorObj) {
			throw new RuntimeException("The input valdation object or register error object is null!");
		}

		CoreDccEngine coreDccEngine = new CoreDccEngine();

		String[] errorStrs = coreDccEngine.validate(objMap, fileName, null, errorObj, engineMode);

		return generateErrorObject(errorStrs, errorObj.getClass());
	}
}
