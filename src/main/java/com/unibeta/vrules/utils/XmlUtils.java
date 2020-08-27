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
package com.unibeta.vrules.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common usible methods related with xml operation.
 * 
 * @author Jordan.xue
 */
public class XmlUtils {

    private static final String ENCODING_UTF_8 = "UTF-8";

    private static final String ORG_APACHE_XML_SERIALIZE_XMLSERIALIZER = "org.apache.xml.serialize.XMLSerializer";
    private static final String ORG_APACHE_XML_SERIALIZE_OUTPUT_FORMAT = "org.apache.xml.serialize.OutputFormat";
    private static final String INDENT_AMOUNT = "4";
    /*
     * private static final String HTTP_XML_APACHE_ORG_XSLT_INDENT_AMOUNT =
     * "{http://xml.apache.org/xslt}indent-amount"; private static final String
     * INDENT_YES = "yes";
     */
    private static final int FIRST_ELEMENT = 0;
    static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * Saves the document instance to xml file.
     * 
     * @throws Exception
     */
    public static void saveToXml(Document doc, String fileName)
            throws Exception {

        prettyPrint(paserDocumentToString(doc), fileName);

        // OutputStream outputStream = null;
        // Transformer transformer = null;
        //
        // try {
        // outputStream = new FileOutputStream(fileName);
        // transformer = TransformerFactory.newInstance().newTransformer();
        //
        // Source xmlSource = new DOMSource(doc);
        // Result target = new StreamResult(outputStream);
        //
        // transformer.setOutputProperty(OutputKeys.ENCODING, ENCORDING_UTF_8);
        // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //
        // transformer.transform(xmlSource, target);
        //
        // } catch (Exception e) {
        //
        // e.printStackTrace();
        //
        // throw e;
        // } finally {
        // try {
        // if (null != outputStream) {
        // outputStream.close();
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
    }

//    public static void main(String[] args) {
//
//        String filePath = "d:/GlobalConfig.xml";
//        try {
//            Java2vRules.digest(GlobalConfig.class, filePath);
//
//            saveToXml(getDocumentByFileName(filePath), "d:/xml0.xml");
//            plainPrint(paserDocumentToString(getDocumentByFileName(filePath)),
//                    "d:/xml1.xml");
//            
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    /**
     * Transforms the <code>Document</code> object to xml-formed string.
     * 
     * @param doc
     * @return
     * @throws WebException
     */
    public static String paserDocumentToString(Node doc) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Transformer transformer = null;
        String xmlString = "";

        try {
            transformer = TransformerFactory.newInstance().newTransformer();

            Source xmlSource = new DOMSource(doc);
            Result target = new StreamResult(outputStream);

            transformer.transform(xmlSource, target);

            xmlString = new String(outputStream.toByteArray());

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return xmlString;
    }

    /**
     * Gets <code>Document</code> object instance by given file name. fileName -
     * file name.
     * 
     * @return -<code>Document</code> object instance
     * @throws Exception
     */
    public static Document getDocumentByFileName(String fileName)
            throws Exception {

        DocumentBuilder docBuilder = null;
        Document doc = null;
        InputStream inputStream = null;

        try {
            docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            inputStream = new FileInputStream(new File(fileName));
            doc = docBuilder.parse(inputStream);
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw e;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw e;
                }
            }
        }

        return doc;
    }
    
    /**
     * Gets <code>Document</code> object instance by given file name. fileName -
     * file name.
     * 
     * @return -<code>Document</code> object instance
     * @throws Exception
     */
    public static Document getDocumentFromStream(InputStream inputStream )
            throws Exception {

        DocumentBuilder docBuilder = null;
        Document doc = null;

        try {
            docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            doc = docBuilder.parse(inputStream);
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw e;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw e;
                }
            }
        }

        return doc;
    }

    /**
     * @param payload
     * @return
     * @throws Exception
     */
    public static Document paserStringToDocument(String payload)
            throws Exception {

        DocumentBuilder docBuilder = null;
        Document doc = null;
        InputStream inputStream = null;

        try {
            docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            inputStream = new StringBufferInputStream(payload);

            doc = docBuilder.parse(inputStream);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw e;
                }
            }
        }

        return doc;
    }

    /**
     * Gets <code>Document</code> object instance by given file name. fileName -
     * file name.
     * 
     * @return -<code>Document</code> object instance
     * @throws Exception
     */
    public static Document initDocument() throws Exception {

        DocumentBuilder docBuilder = null;
        Document doc = null;

        try {
            docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();

            doc = docBuilder.newDocument();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        } finally {

        }

        return doc;
    }

    /**
     * Gets the first by given target name from specified element.
     * 
     * @param root
     * @param targetName
     * @return
     */
    public static Element getFirstElementByTargetName(Element root,
            String targetName) {

        NodeList nodeList = root.getElementsByTagName(targetName);
        Element element = (Element) nodeList.item(FIRST_ELEMENT);

        return element;
    }

    /**
     * Gets the node value by specified targ name.
     * 
     * @param root
     * @param targName
     *            - target name .
     * @return - node vlaue.
     */
    public static String getNodeValueByTargetName(Element root, String targName) {

        String value = "";
        Element element = getFirstElementByTargetName(root, targName);

        if (null != element && null != element.getFirstChild()) {
            // String str = element.getFirstChild().getTextContent(); for jdk5.0
            // above
            String str = element.getFirstChild().getNodeValue();
            value = str == null ? "" : str;
        }

        return value;
    }

    /**
     * Creates a new element and set the value.
     * 
     * @param doc
     * @param targName
     * @param value
     * @return
     */
    public static Element createNewElement(Document doc, String targName,
            String value) {

        if (null == value) {
            value = "";
        }

        Element element = doc.createElement(targName);
        // element.setTextContent(value); for jdk5.0 above

        element.setNodeValue(value);
        return element;
    }

    /**
     * Appends a new element to given root.
     * 
     * @param doc
     * @param root
     * @param targName
     * @param value
     * @return
     */
    public static Node appendChildElement(Document doc, Element root,
            String targName, String value) {

        Node node = root.appendChild(createNewElement(doc, targName, value));

        return node;
    }

    /**
     * Prints the xml in pretty format.
     * 
     * @param utf8XML
     *            the xml String.
     * @param fileName
     *            file name to save in.
     * @throws Exception
     * @throws Exception
     */
    public static void prettyPrint(String xmlStr, String fileName)
            throws Exception {
        
        if(xmlStr == null){
            return ;
        }
        
        String utf8XML = new String(xmlStr.getBytes(ENCODING_UTF_8),ENCODING_UTF_8);

        try {
            Document doc = paserStringToDocument(utf8XML);
            FileOutputStream os = new FileOutputStream(fileName);

            try {
                Class.forName(ORG_APACHE_XML_SERIALIZE_OUTPUT_FORMAT);
                Class.forName(ORG_APACHE_XML_SERIALIZE_XMLSERIALIZER);

                prettyPrint(doc, os);
            } catch (Exception e) {
                Class.forName("org.dom4j.Document");

                plainPrint(dom4jFormat(utf8XML), fileName);
            }finally{
                if (null!= os) {
                    os.close();
                }
            }
            

        } catch (Exception e) {
            plainPrint(utf8XML, fileName);
        }
    }

    private static void plainPrint(String xmlStr, String fileName)
            throws IOException {

        Writer bufferedWriter = null;
        
        try {
            
            FileOutputStream fos = new FileOutputStream(fileName);
            bufferedWriter= new OutputStreamWriter(fos, ENCODING_UTF_8);
            
            bufferedWriter.write(xmlStr);
            
            bufferedWriter.close();
            fos.close();
            
//            FileWriter fileWriter = null;
//            fileWriter = new FileWriter(fileName);
//            bufferedWriter = new BufferedWriter(fileWriter);
//            bufferedWriter.write(xmlStr);
        } catch (IOException e1) {
            e1.printStackTrace();
            logger.error(e1.getMessage(), e1);
            throw e1;
        } finally {
            if (bufferedWriter!= null) {
                bufferedWriter.close();
            }
           
        }
    }

    /**
     * Prints the xml in pretty format.
     * 
     * @param doc
     * @param out
     * @throws Exception
     * @throws Exception
     */
    /*
     * public static void prettyPrintByDomj4(Document doc, OutputStream out)
     * throws Exception { org.dom4j.io.OutputFormat format = new
     * org.dom4j.io.OutputFormat(INDENT_AMOUNT); XMLWriter writer = null; try {
     * writer = new XMLWriter(out, format); writer.write(doc); } catch
     * (Exception e) { logger.error(e); throw e; } finally { writer.close(); } }
     */

    private static void prettyPrint(Document doc, OutputStream out)
            throws Exception {

        if(doc == null){
            return ;
        }

        try {

            OutputFormat format = new OutputFormat(doc);
            Writer writer = new OutputStreamWriter(out, ENCODING_UTF_8);
            
            // format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(Integer.valueOf(INDENT_AMOUNT).intValue());
            format.setEncoding(ENCODING_UTF_8);

            XMLSerializer serializer = new XMLSerializer(writer, format);
            serializer.serialize(doc);
        } catch (NoClassDefFoundError e) {
            String xml = paserDocumentToString(doc);
            xml = dom4jFormat(xml);

            out.write(xml.getBytes(ENCODING_UTF_8));
        }
    }

    /**
     * Format xml into well-formed style and return formatted xml.
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public static String formatXML(String str) throws Exception {

        if (null == str) {
            return null;
        }

        String formatedStr = null;

        try {
            formatedStr = prettyFormat(str);
        } catch (Exception e) {

            formatedStr = dom4jFormat(str);
        }

        return new String(formatedStr.getBytes(ENCODING_UTF_8),ENCODING_UTF_8);

    }

    private static String prettyFormat(String str) throws Exception {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            Document doc = paserStringToDocument(str);
            prettyPrint(doc, os);
        } finally {
            os.close();
        }

        return os.toString();
    }

    private static String dom4jFormat(String str) throws DocumentException,
            IOException {

        SAXReader reader = new SAXReader();
        StringReader in = new StringReader(str);

        org.dom4j.Document doc = reader.read(in);
        org.dom4j.io.OutputFormat formater = org.dom4j.io.OutputFormat
                .createPrettyPrint();
        formater.setIndent(true);
        formater.setIndentSize(Integer.parseInt(INDENT_AMOUNT));
        // formater=OutputFormat.createCompactFormat();
        formater.setEncoding(ENCODING_UTF_8);

        OutputStream out = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(out, ENCODING_UTF_8);
        XMLWriter writer = new XMLWriter(w, formater);

        writer.write(doc);
        writer.close();

        return out.toString();
    }
}
