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

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

/**
 * The error object mapping pool management class. Every validation rule should
 * have a unique token that mapping to the ErrorObjectPool.
 * 
 * @author jordan
 */
public class ErrorObjectPoolManager {

    private static Map<String, Object> errorMap = Collections
            .synchronizedMap(new Hashtable<String, Object>());

    public synchronized static String generateSequenceId() {

        return UUID.randomUUID().toString();
    }

    public static void addError(String id, Object obj) {

        errorMap.put(id, obj);
    }

    public static Object getError(String key) {

        return errorMap.get(key);
    }

}
