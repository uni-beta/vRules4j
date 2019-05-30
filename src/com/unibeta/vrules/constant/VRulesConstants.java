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
package com.unibeta.vrules.constant;

import java.lang.management.ManagementFactory;

/**
 * @author Jordan.Xue
 */
public class VRulesConstants {
	public static final String $_ERROR_MESSAGE_FLAG = "$[errorMessage]:=";
	public static final String $_X_PATH = "${xPath}:=";
	public static final String JAVA_OBJECTS = "java.";
	public static final String NILLABLE_ERROR = "[NillableError]";
	public static final String NEED_DISPLAY_COLLECTION_NAME_FLAG = "#";

	public static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
	public static final String JAVA_BOOLEAN = "boolean";

	public static final String DYNAMIC_CLASSES_FOLDER_NAME = "." + ManagementFactory.getRuntimeMXBean().getName()
			+ ".bin";// ".bin";
}
