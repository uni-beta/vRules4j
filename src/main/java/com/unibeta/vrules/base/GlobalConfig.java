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

/**
 * <code>GlobalConfig</code> contains the global configuration attribuates for
 * the validation rules.
 * 
 * @author jordan
 */
public class GlobalConfig {

    private boolean returnErrorId = false;
    private boolean returnErrorValue = true;
    private String includes;
    private boolean toggleBreakpoint = false;
    private boolean decisionMode = false;
    private boolean assertion = false;
    private boolean enableBinding = false;
    private boolean mergeExtensions = false;

    public boolean isMergeExtensions() {

        return mergeExtensions;
    }

    public void setMergeExtensions(boolean mergeExtensions) {

        this.mergeExtensions = mergeExtensions;
    }

    public boolean isEnableBinding() {

        return enableBinding;
    }

    public void setEnableBinding(boolean newInstanceIfNull) {

        this.enableBinding = newInstanceIfNull;
    }

    public boolean isAssertion() {

        return assertion;
    }

    public void setAssertion(boolean assertion) {

        this.assertion = assertion;
    }

    public boolean isDecisionMode() {

        return decisionMode;
    }

    public void setDecisionMode(boolean decisionMode) {

        this.decisionMode = decisionMode;
    }

    public boolean isReturnErrorValue() {

        return returnErrorValue;
    }

    public void setReturnErrorValue(boolean returnErrorValue) {

        this.returnErrorValue = returnErrorValue;
    }

    public boolean isReturnErrorId() {

        return returnErrorId;
    }

    public void setReturnErrorId(boolean returnErrorId) {

        this.returnErrorId = returnErrorId;
    }

    public String getIncludes() {

        return includes;
    }

    public void setIncludes(String includes) {

        this.includes = includes;
    }

    public boolean isToggleBreakpoint() {

        return toggleBreakpoint;
    }

    public void setToggleBreakpoint(boolean toggleBreakpoint) {

        this.toggleBreakpoint = toggleBreakpoint;
    }
}
