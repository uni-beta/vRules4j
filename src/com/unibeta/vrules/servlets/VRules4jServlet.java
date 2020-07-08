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
package com.unibeta.vrules.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>VRulesEngineServlet</code> is a servelt used to plug vRuels4j into the
 * container as a standalone component. If vRules is deployed to Web Container
 * (such as Tomcat, Websphere, or Weblogic), configure below context into
 * web.xml to enable vRules4j engine for JaveEE.<br/>
 * <br/>
 * For Example:
 * 
 * <pre>
 *   &lt;servlet&gt;
 *         &lt;servlet-name&gt;vRules4jServlet&lt;/servlet-name&gt;
 *         &lt;servlet-class&gt;
 *             com.unibeta.vrules.servlets.VRules4jServlet
 *         &lt;/servlet-class&gt;
 *         &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *   &lt;/servlet&gt;
 * </pre>
 * 
 * Notes: It was optional from v3.1.x version. User do not have to do servlet
 * configuration in v3.1 above, the engine will detect the classpath in runtime.<br>
 * For IBM WebSphere container, it is mandatory. 
 * 
 * @author Jordan.Xue
 */
public class VRules4jServlet implements Servlet {

    private static Logger log = LoggerFactory.getLogger(VRules4jServlet.class);

    public void destroy() {

        // TODO Auto-generated method stub

    }

    public ServletConfig getServletConfig() {

        // TODO Auto-generated method stub
        return null;
    }

    public String getServletInfo() {

        // TODO Auto-generated method stub
        return null;
    }

    public void init(ServletConfig conf) throws ServletException {

        String realPath = conf.getServletContext().getRealPath("");
        URLConfiguration.setInContainer(true);
        URLConfiguration.setRealPath(realPath);

        try {
            URLConfiguration.initClasspathURLs();
        } catch (Exception e) {

            e.printStackTrace();
            log.error("vRules4j servlet gets the calsspath URL failed!");
        }

        log.info("vRules4jServlet get the real path is :" + realPath);

    }

    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {

        // empty
    }
}
