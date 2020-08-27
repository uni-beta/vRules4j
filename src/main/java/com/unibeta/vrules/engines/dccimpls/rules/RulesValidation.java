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
package com.unibeta.vrules.engines.dccimpls.rules;

import java.util.List;
import java.util.Map;

/**
 * A interface providing the validation service for all rules.
 * 
 * @author jordan.xue
 */
public interface RulesValidation {

    /**
     * Validates the specified object according to the entityId which has been
     * defined in the rules configuration file.
     * 
     * @param obj
     *            the object to be validated.
     * @param entityId
     *            the object id in the configuration file.
     * @param ruleset
     *            the ruleset to be executed, if return null with the given key,
     *            it will run all rules by default.
     * @return error messages, if validated failed.
     * @throws Exception
     */
    public String[] validate(Object obj, String entityId,
            Map<String, List<String>> ruleset) throws Exception;

    /**
     * Validates the <code>Map<String,Object></code>
     * 
     * @param object
     *            The map object to be validated, the key is the entityId, value
     *            is the object to be validated.
     * @param ruleset
     *            the ruleset to be executed, if return null with the given key,
     *            it will run all rules by default.
     * @return the error messages if any failed.
     * @throws Exception
     */
    public String[] validate(Map<String, Object> obj,
            Map<String, List<String>> ruleset) throws Exception;
}
