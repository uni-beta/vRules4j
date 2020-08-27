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

import java.util.Map;

import com.unibeta.vrules.constant.VRulesConstants;

public class Rule implements Comparable<Rule> {

    private String id;
    private String refId;
    private boolean isComplexType = false;
    private boolean recursive = false;
    private boolean isMapOrList = false;
    private String ifTrue;
    private String ifFalse;
    private String desc;

    private String inputObjects;
    private String outputBool;

    private String dataType;

    private String predicate;

    private Map<String, Object> values;

    private String errorMessage;
    private Object errorObject;
    private String name;
    private long sequence = 0;
    private boolean breakpoint = false;
    private String depends;
    private Binding binding;

    public Binding getBinding() {

        return binding;
    }

    public void setBinding(Binding binding) {

        this.binding = binding;
    }

    public String getDepends() {

        return depends;
    }

    public void setDepends(String depends) {

        this.depends = depends;
    }

    public boolean judgeNameContainsNeedDisplayCollectionNameFlag() {

        return name.indexOf(VRulesConstants.NEED_DISPLAY_COLLECTION_NAME_FLAG) == 0;
    }

    public String getName(boolean needJudge) {

        if (needJudge == true) {
            return name;
        } else {
            return getName();
        }

    }

    public String getName() {

        if (null!= name &&name.indexOf(VRulesConstants.NEED_DISPLAY_COLLECTION_NAME_FLAG) == 0) {
            return name.substring(1);
        } else {
            return name;
        }
    }

    public void setName(String xmlElementName) {

        this.name = xmlElementName;
    }

    public String getDataType() {

        return dataType;
    }

    public void setDataType(String dataType) {

        this.dataType = dataType;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {

        this.desc = desc;
    }

    public String getErrorMessage() {

        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getPredicate() {

        return predicate;
    }

    public void setPredicate(String predicate) {

        this.predicate = predicate;
    }

    public String getInputObjects() {

        return inputObjects;
    }

    public void setInputObjects(String input) {

        this.inputObjects = input;
    }

    public String getOutputBool() {

        return outputBool;
    }

    public void setOutputBool(String output) {

        this.outputBool = output;
    }

    public Map<String, Object> getValues() {

        return values;
    }

    public void setValues(Map<String, Object> values) {

        this.values = values;
    }

    public boolean isComplexType() {

        return isComplexType;
    }

    public void setComplexType(boolean isComplexType) {

        this.isComplexType = isComplexType;
    }

    public boolean isRecursive() {

        return recursive;
    }

    public void setRecursive(boolean recursive) {

        this.recursive = recursive;
    }

    public boolean equals(Object obj) {

        if (!(obj instanceof Rule)) {
            return false;
        }
        Rule rule = (Rule) obj;

        if (rule.getId().equals(this.id)) {
            return true;
        }

        return false;
    }

    public String getRefId() {

        return refId;
    }

    public void setRefId(String refId) {

        this.refId = refId;
    }

    public boolean isMapOrList() {

        return isMapOrList;
    }

    public void setMapOrList(boolean isMapOrList) {

        this.isMapOrList = isMapOrList;
    }

    public Object getErrorObject() {

        return errorObject;
    }

    public void setErrorObject(Object errorObject) {

        this.errorObject = errorObject;
    }

    public long getSequence() {

        return sequence;
    }

    public void setSequence(long sequence) {

        this.sequence = sequence;
    }

    public int compareTo(Rule o) {

        int result = 0;

        if (null == o) {
            result = 1;
        }

        if (null == this) {
            result = -1;
        }

        if (this.getSequence() > o.getSequence()) {
            result = 1;
        } else {
            result = -1;
        }

        return result;
    }

    public boolean isBreakpoint() {

        return breakpoint;
    }

    public void setBreakpoint(boolean breakpoint) {

        this.breakpoint = breakpoint;
    }

    public String getIfTrue() {

        return ifTrue;
    }

    public void setIfTrue(String ifTrue) {

        this.ifTrue = ifTrue;
    }

    public String getIfFalse() {

        return ifFalse;
    }

    public void setIfFalse(String ifFalse) {

        this.ifFalse = ifFalse;
    }

    public static class Binding {

        private String wirings = "";
        private String foreach = "";
        private String var = "";
        private String src = "";

        public String getSrc() {

            return src;
        }

        public void setSrc(String src) {

            this.src = src;
        }

        public String getWirings() {

            return wirings;
        }

        public void setWirings(String wirings) {

            this.wirings = wirings;
        }

        public String getForeach() {

            return foreach;
        }

        public void setForeach(String foreach) {

            this.foreach = foreach;
        }

        public String getVar() {

            return var;
        }

        public void setVar(String var) {

            this.var = var;
        }

    }
}
