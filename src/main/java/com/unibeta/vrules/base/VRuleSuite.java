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
package com.unibeta.vrules.base;

import java.util.List;
import java.util.Map;

/**
 * <code>VRuleSuite</code> is represention object of every vRules configuration
 * file.
 * 
 * @author Jordan.Xue
 */
public class VRuleSuite {

    private GlobalConfig globalConfig;
    private String declaredMethods;
    private String imports;
    private Map<String, Ruleset> rulesets;
    private Map<String, ObjectEntity> objectEntities;
    private List<VRules4jContext> contexts;
    private List<DecisionConstantDefinition> decisionConstantDefinitions;

    public Map<String, Ruleset> getRulesets() {

        return rulesets;
    }

    public void setRulesets(Map<String, Ruleset> rulesets) {

        this.rulesets = rulesets;
    }

    public String getDeclaredMethods() {

        return declaredMethods;
    }

    public void setDeclaredMethods(String declaredMethods) {

        this.declaredMethods = declaredMethods;
    }

    public GlobalConfig getGlobalConfig() {

        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {

        this.globalConfig = globalConfig;
    }

    public String getImports() {

        return imports;
    }

    public void setImports(String imports) {

        this.imports = imports;
    }

    public Map<String, ObjectEntity> getObjectEntities() {

        return objectEntities;
    }

    public void setObjectEntities(Map<String, ObjectEntity> objectEntities) {

        this.objectEntities = objectEntities;
    }

    public List<VRules4jContext> getContexts() {

        return contexts;
    }

    public void setContexts(List<VRules4jContext> contexts) {

        this.contexts = contexts;
    }

    public List<DecisionConstantDefinition> getDecisionConstantDefinitions() {

        return decisionConstantDefinitions;
    }

    public void setDecisionConstantDefinitions(
            List<DecisionConstantDefinition> decisionConstantDefinitions) {

        this.decisionConstantDefinitions = decisionConstantDefinitions;
    }

}
