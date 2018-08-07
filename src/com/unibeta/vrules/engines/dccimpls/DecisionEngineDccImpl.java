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

import java.util.List;
import java.util.Map;

import com.unibeta.vrules.engines.DecisionEngine;

/**
 * Dynamic compiling implementation for DecisionEngine.
 * 
 * @author jordan.xue
 */
public class DecisionEngineDccImpl extends ValidationEngineDccImpl implements
        DecisionEngine {

    {
        super.engineMode = RulesInterpreter.VRULES_ENGINE_MODE_DECISION;
    }

    public String decide(Object object, String vRulesFileName) throws Exception {

        // TODO Auto-generated method stub
        String[] validate = super.validate(object, vRulesFileName);

        if (null != validate) {
            return validate[0];
        } else {
            return null;
        }
    }

    public String decide(Object object, String vRulesFileName, String entityId)
            throws Exception {

        // TODO Auto-generated method stub
        String[] validate = super.validate(object, vRulesFileName, entityId);

        if (null != validate) {
            return validate[0];
        } else {
            return null;
        }
    }

    public String decide(Object[] objects, String vRulesFileName)
            throws Exception {

        // TODO Auto-generated method stub
        String[] validate = super.validate(objects, vRulesFileName);

        if (null != validate) {
            return validate[0];
        } else {
            return null;
        }
    }

    public Object decide(Object[] objects, String vRulesFileName,
            Object decisionObj) throws Exception {

        // TODO Auto-generated method stub
        List validate = super.validate(objects, vRulesFileName, decisionObj);

        if (null != validate) {
            return validate.get(0);
        } else {
            return null;
        }

    }

    public Object decide(Object object, String vRulesFileName,
            Object decisionObj) throws Exception {

        // TODO Auto-generated method stub
        List validate = super.validate(object, vRulesFileName, decisionObj);

        if (null != validate) {
            return validate.get(0);
        } else {
            return null;
        }
    }

    public Object decide(Object object, String vRulesFileName, String entityId,
            Object decisionObj) throws Exception {

        // TODO Auto-generated method stub
        List validate = super.validate(object, vRulesFileName, entityId,
                decisionObj);

        if (null != validate) {
            return validate.get(0);
        } else {
            return null;
        }
    }

    public String decide(Map<String, Object> objMap, String vRulesFileName)
            throws Exception {

        // TODO Auto-generated method stub
        String[] validate = super.validate(objMap, vRulesFileName);

        if (null != validate) {
            return validate[0];
        } else {
            return null;
        }

    }

    public Object decide(Map<String, Object> objMap, String vRulesFileName,
            Object decisionObj) throws Exception {

        // TODO Auto-generated method stub
        List validate = super.validate(objMap, vRulesFileName, decisionObj);

        if (null != validate) {
            return validate.get(0);
        } else {
            return null;
        }

    }
}
