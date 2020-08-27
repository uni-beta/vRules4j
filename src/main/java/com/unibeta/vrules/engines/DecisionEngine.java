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
package com.unibeta.vrules.engines;

import java.util.Map;

/**
 * DecisionEngine is the core decision-making service interface
 * 
 * @author jordan.xue
 */
public interface DecisionEngine {

    /**
     * Make decisions via the specified object via the validation rules. The default id
     * is the class name.The parameter object can be whatever datatype.
     * 
     * @param object
     *            the object to be validated
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @return null if validate passed, otherwise return error messages. *
     * @throws NullPointerException
     *             if the input object is null.
     */
    public abstract String decide(Object object, String vRulesFileName)
            throws Exception;

    /**
     * Make decisions via the specified object by <code>entityId</code>.The parameter
     * object can be whatever datatype.
     * 
     * @param object
     *            the object to be validated
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @param entityId
     * @return null if validate passed, otherwise return error messages.
     * @throws NullPointerException
     *             if the input object is null.
     */
    public abstract String decide(Object object, String vRulesFileName,
            String entityId) throws Exception;

    /**
     * Make decisions via a array of objects via the specified rules' file.The input
     * parameter object can be whatever datatype.
     * 
     * @param objects
     *            the object to be validated
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @return null if validate passed, otherwise return error messages.
     * @throws NullPointerException
     *             if the input object is null.
     */
    public abstract String decide(Object[] objects, String vRulesFileName)
            throws Exception;

    /**
     * Make decisions via an array object via the specified rule's file, meanwhile
     * registering the returned error object instance type.
     * 
     * @param objects
     * @param vRulesFileName
     * @param decisionObj
     * @return the error object list, which is of <code>decisionObj</code> Class
     *         type, if validated failed.
     * @throws Exception
     */
    public abstract Object decide(Object[] objects, String vRulesFileName,
            Object decisionObj) throws Exception;

    /**
     * Make decisions via an object via the specified rule's file, meanwhile registering
     * the returned error object instance type.
     * 
     * @param object
     * @param vRulesFileName
     * @param decisionObj
     * @return the error object list, which is of <code>decisionObj</code> Class
     *         type, if validated failed.
     * @throws Exception
     */
    public abstract Object decide(Object object, String vRulesFileName,
            Object decisionObj) throws Exception;

    /**
     * Make decisions via the specified object by <code>entityId</code>, meanwhile
     * registering the returned error object .The parameter object can be
     * whatever datatype.
     * 
     * @param object
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @param entityId
     * @param decisionObj
     * @return the error object list, which is of <code>decisionObj</code> Class
     *         type, if validated failed.
     * @throws Exception.
     */
    public abstract Object decide(Object object, String vRulesFileName,
            String entityId, Object decisionObj) throws Exception;

    /**
     * Make decisions via the objects in Map and return error messages if validates
     * failed, the key of the map element can be the validation entityId
     * configured in validation files. It can be specially used for contexted
     * object validation. <br>
     * NOTE: If the key of the map is not matched with any entityId configured
     * in validation file, such element would be ignored.
     * 
     * @param objMap
     *            Mapped objects.
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @return null if validate passed, otherwise return error messages.
     * @throws Exception.
     */
    public abstract String decide(Map<String, Object> objMap,
            String vRulesFileName) throws Exception;

    /**
     * Make decisions via the objects in Map, the key of the map element can be the
     * validation entityId configured in validation files. It can be used for
     * contexted object validation. <br>
     * NOTE: If the key of the map is not matched with any entityId configured
     * in validation file, such element would be ignored.
     * 
     * @param objMap
     *            Mapped objects
     * @param vRulesFileName
     *            the full path name of rule configuration located.
     * @param decisionObj
     * @return null if validate passed, otherwise return error messages.
     * @throws Exception.
     */
    public abstract Object decide(Map<String, Object> objMap,
            String vRulesFileName, Object decisionObj) throws Exception;

}
