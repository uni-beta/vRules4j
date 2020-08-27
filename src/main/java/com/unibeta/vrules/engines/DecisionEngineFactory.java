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
package com.unibeta.vrules.engines;

import com.unibeta.vrules.engines.dccimpls.DecisionEngineDccImpl;

/**
 * <code>DecisionEngine</code> factory used to get the instance of core
 * DecisionEngine instance. It is of thread-safe.
 * 
 * @author Jordan.xue
 */
public class DecisionEngineFactory {

    private static ThreadLocal<DecisionEngine> localDynamicEngineThread = new ThreadLocal<DecisionEngine>();

    /**
     * Gets a thread pooled dynamic decision engine instance, which is
     * thread-safe.
     * 
     * @return <code>ValidationEngine</code> instance.
     */
    public static DecisionEngine getInstance() {

        return getDynamicEngineInstance();
    }

    /**
     * Gets a thread pooled dynamic validation engine instance, which is of
     * thread-safed. DynamicEngine has much higher performance than the
     * PlainEngine, which is recommended to use.
     * 
     * @return <code>ValidationEngine</code> instance
     */
    private static DecisionEngine getDynamicEngineInstance() {

        DecisionEngine instance = localDynamicEngineThread.get();

        if (null == instance) {

            initDynamicEngine();
        }

        instance = localDynamicEngineThread.get();

        return instance;
    }

    private static void initDynamicEngine() {

        DecisionEngine engine = new DecisionEngineDccImpl();

        if (null != engine) {
            localDynamicEngineThread.set(engine);
        }
    }
}
