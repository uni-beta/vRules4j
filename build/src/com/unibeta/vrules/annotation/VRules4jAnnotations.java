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
package com.unibeta.vrules.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <code>VRules4jAnnotations</code> contains the annotation used for vRuels4j
 * project.
 * 
 * @author jordan
 */
public class VRules4jAnnotations {

    /**
     * Indentifies the current field's validation rules.
     * 
     * @author jordan
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidationRules {

        String name();

        String outputBool();

        String predicate();

        String depends();

        String sequence();

        String breakpoint();

        String errorMessage();

        String desc();

    }

    /**
     * Indentifies the current field's validation reference generic type. It is
     * only to be used for the <code>Map</code> or <code>List</code> types.
     * 
     * @author jordan
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidationGenericType {

        String className();
    }

    /**
     * Indentifies the conresponding field which will be filled the error xPath
     * information.
     * 
     * @author jordan
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidationErrorXPath {
        //String isErrorXpath();
    }

    /**
     * Indentifies the conresponding field which will be filled the error message
     * information.
     * 
     * @author jordan
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidationErrorMessage {
        //String isErrorXpath();
    }

}
