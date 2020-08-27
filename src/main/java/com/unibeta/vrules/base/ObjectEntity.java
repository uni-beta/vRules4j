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

import com.unibeta.vrules.utils.CommonUtils;

public class ObjectEntity implements Comparable<ObjectEntity> {

    private String id;
    private String extension;
    private String description;

    private String className;

    private List<Rule> rules;
    private boolean nill = false;
    private String name;
    private long sequence = 0;
//    private boolean breakpoint = false;
//    private String depends;

    //    
    public String getName() {

        return name;
    }

    public void setName(String xmlElementName) {

        this.name = xmlElementName;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {

        this.className = className;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public List<Rule> getRules() {

        return rules;
    }

    public void setRules(List<Rule> rules) {

        this.rules = rules;
    }

    public boolean isNill() {

        return nill;
    }

    public void setNill(boolean nill) {

        this.nill = nill;
    }

    public String getExtension() {

        return extension;
    }

    public void setExtension(String extension) {

        this.extension = extension;
    }

    public long getSequence() {

        return sequence;
    }

    public void setSequence(long sequence) {

        this.sequence = sequence;
    }

    public int compareTo(ObjectEntity o) {

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

    public boolean containsRule(String ruleId){
        
        for(Rule r: this.getRules()){
            if(ruleId.equals(r.getId())){
                return true;
            }
        }
        return false;
    }
}
