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
package com.unibeta.vrules.base;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * vRules4j rules definition object which can be serialized to xml payload. It
 * can be serialized to xml directly by JAXB in vRules4j syntax.
 * 
 * @see com.unibeta.vrules.tools.Java2vRules.toXml(vRules4j, String);
 * @author jordan.xue
 */

@XmlRootElement(name = "vRules4j")
public class vRules4j {

    public vRules4j() {

    }

    /**
     * Global parameters for current xml payload.
     * 
     * @author jordan.xue
     */
    public static class Global {

        @XmlElement
        public String decisionMode = "false";
        @XmlElement
        public String assertion = "false";
        @XmlElement
        public String returnErrorId = "false";
        @XmlElement
        public String displayErrorValue = "true";
        @XmlElement
        public String toggleBreakpoint = "off";
        @XmlElement
        public String enableBinding = "false";
        @XmlElement
        public String mergeExtensions = "false";
        
        @XmlElement
        public String includes = "";
        
    }

    /**
     * Validation context definition
     * 
     * @author jordan.xue
     */
    public static class Context {

        @XmlAttribute
        public String className = "";
        @XmlAttribute
        public String name = "";

        @Override
        public boolean equals(java.lang.Object obj) {

            if (null == obj) {
                return false;
            }

            if (!Context.class.isAssignableFrom(obj.getClass())) {
                return false;
            } else {
                Context c = (Context) obj;
                if (this.name.equals(c.name)
                        && this.className.equals(c.className)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Constant definition
     * 
     * @author jordan.xue
     */
    public static class ConstantDefinition {

        @XmlAttribute
        public String value = "";
        @XmlAttribute
        public String name = "";
    }

    /**
     * Target validation entity object
     * 
     * @author jordan.xue
     */
    public static class Object {

        /**
         * Validation rule definition
         * 
         * @author jordan.xue
         */
        @XmlType(name = "com.unibeta.vrules.base.vRules4j.Object.Rule")
        public static class Rule {

            public static class Binding {

                @XmlAttribute
                public String wirings = "";
                @XmlAttribute
                public String foreach = "";
                @XmlAttribute
                public String var = "";
            }

            /**
             * Error message definition
             * 
             * @author jordan.xue
             */
            public static class ErrorMessage {

                @XmlAttribute
                public String returnId = "false";
                @XmlAttribute
                public String id = "";
                @XmlValue
                public String message = "";
            }

            @XmlAttribute
            public String desc = "";
            @XmlAttribute
            public String sequence = "";
            @XmlAttribute
            public String breakpoint = "off";
            @XmlAttribute
            public String isComplexType = "";
            @XmlAttribute
            public String isMapOrList = "";
            @XmlAttribute
            public String recursive = "";
            @XmlAttribute
            public String ifFalse = "";
            @XmlAttribute
            public String ifTrue = "";
            @XmlAttribute
            public String refId = "";
            @XmlAttribute
            public String depends = "";
            @XmlAttribute
            public String name = "";
            @XmlAttribute
            public String id = "";

            @XmlElement
            public Binding binding = new Binding();
            @XmlElement
            public String dataType = "";
            @XmlElement
            public String attributes = "";
            @XmlElement
            public String boolParam = "$";
            @XmlElement(name = "assert")
            public String assert_ = "";
            @XmlElement(name = "message")
            public ErrorMessage errorMessage = new ErrorMessage();
            @XmlElement
            public String decision = "";
        }

        @XmlAttribute
        public String id = "";
        @XmlAttribute
        public String name = "";
        @XmlAttribute
        public String extensions = "";
        @XmlAttribute
        public String desc = "";

        @XmlElement
        public String className = "";
        @XmlElement
        public String nillable = "false";
        @XmlElementWrapper(name = "rules")
        @XmlElement(name = "rule")
        public Rule[] rules = new Rule[] { new Rule() };
    }
    
    public static class Ruleset {
        @XmlAttribute
        public String id = "";
        @XmlAttribute
        public String desc = "";
        
        @XmlElement(name = "rule")
        public Rule[] rules = new Rule[] { new Rule() };
        
        @XmlType(name = "com.unibeta.vrules.base.vRules4j.Ruleset.Rule")
        public static class Rule {
            @XmlAttribute
            public String id = "";
            @XmlAttribute
            public String desc = "";
            
        }
    }

    @XmlElement
    public Global global = new Global();
    @XmlElement
    public String imports = "";
    @XmlElement
    public String java = "";
    @XmlElementWrapper(name = "contexts")
    @XmlElement(name = "context")
    public Context[] contexts = new Context[] { new Context() };
    @XmlElementWrapper(name = "decisionConstants")
    @XmlElement(name = "definition")
    public ConstantDefinition[] decisionConstants = new ConstantDefinition[] { new ConstantDefinition() };
    @XmlElement(name = "ruleset")
    public Ruleset[] rulesets = new Ruleset[] { new Ruleset() };
    @XmlElement(name = "object")
    public Object[] objects = new Object[] { new Object() };
    
}
