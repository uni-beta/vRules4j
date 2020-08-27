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
package com.unibeta.vrules.engines.bshimpls;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import bsh.Interpreter;

import com.unibeta.vrules.base.Rule;

/**
 * <code>Interpreter</code> is the dynamic java program executer.
 * 
 * @author Jordan.xue
 */
public class VRulesBshInterpreter {

    /**
     * executes the rule by predicate.
     * 
     * @param rule
     * @return
     * @throws Exception
     */
    public static boolean execute(Rule rule, String declaredMethods)
            throws Exception {

        Interpreter interpreter = new Interpreter();

        String script = declaredMethods + ";" + " boolean "
                + rule.getOutputBool() + " = false;" + rule.getPredicate();

        Map values = rule.getValues();
        Set set = values.keySet();
        for (Iterator i = set.iterator(); i.hasNext();) {

            String key = (String) i.next();
            interpreter.set(key, values.get(key));
        }

        if (set == null || set.size() == 0) {
            interpreter.set(rule.getInputObjects(), null);
        }

        interpreter.eval(script);
        Boolean result = (Boolean) interpreter.get(rule.getOutputBool());

        return result.booleanValue();
    }
}
