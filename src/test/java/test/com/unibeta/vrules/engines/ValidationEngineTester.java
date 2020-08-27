package test.com.unibeta.vrules.engines;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import test.cases.ErrorField;
import test.cases.ErrorInfo;
import test.cases.ISon;
import test.cases.Son;
import test.com.unibeta.vrules.BasicTester;
import bsh.EvalError;
import bsh.Interpreter;

import com.unibeta.vrules.base.Rule;
import com.unibeta.vrules.base.VRuleSuite;
import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.engines.ValidationEngineFactory;
import com.unibeta.vrules.tools.Java2vRules;

public class ValidationEngineTester extends BasicTester {

    test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
    {
    errorInfo.setErrorCode("ERROR_CODE_1");
    errorInfo.setErrorMsg("ERROR_MSG_BAD");
//    errorInfo.setXPath("");
    
    ErrorField errorField = new ErrorField();
//    errorField.setErrorId("ERROR_FILED_1");
    errorInfo.setError(errorField);
    }
   ValidationEngine impl = ValidationEngineFactory.getInstance();
//    ValidationEngine impl = ValidationEngineFactory.getPlainEngineInstance();
    public ValidationEngineTester(String string) {

        super(string);
    }
    
    public void Interface() throws Exception{
        
        Type c[] = ISon.class.getGenericInterfaces();
        
        System.out.println(c.length);
        
        String[] errs = null;
        String sonFile = "E:/eclipse/vRules-2.x/doc1/ISon1.xml";
        try {
            Java2vRules.digest(new Class[]{ISon.class,Son.class}, sonFile);
            ISon s = son;
            errs = impl.validate(s, sonFile,"ISon");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
// logger.info("error is " + errs.length);
        
        if (errs != null) {
            for (int i = 0; i < errs.length; i++) {
                logger.info(errs[i]);
            }
        }
        System.out.println("the father name is " +father.getName());
//        assertEquals(true, errs != null);
    }
    
    public void testBsh(){
        String f = "D:/eclipse/vRules-3.x/doc1/ISon5xml.java";
        try {
            Object o = new Interpreter().source(f);
            System.out.println(o.getClass().getCanonicalName());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EvalError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void testGroovy(){
        String f = "D:/eclipse/vRules-3.x/doc1/ISon5xml.java";
        
        
        ClassLoader cl =  ClassLoader.getSystemClassLoader();
           GroovyClassLoader groovyCl = new GroovyClassLoader(cl);
           
           try {
            Class clazz = groovyCl.parseClass(new File(f));
            Object o = clazz.newInstance();
            
            System.out.println(groovyCl.getURLs().length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    public void testContext() throws Exception{
        
        String[] errs = null;
        List<ErrorInfo> list = null;
        String sonFile = "D:/eclipse/vRules-3.x/doc1/test/ISon3.vrules.xml";
        try {
            
            Map<String,Object> map = new LinkedHashMap<String, Object>();
            List<ISon> l = new ArrayList<ISon>();
            ISon s = son;
//            map.put("Father", father);
            map.put("Son", son);
//            map.put("intData", 1);
//            l.add(s);
//            map.put("list", l);
//            map.put("array", new ErrorInfo[]{new ErrorInfo()});
            
            boolean a =  (boolean)new Boolean("");
//            map.put("Family", family);
//            com.unibeta.vrules.engines.dccimpls.rules.ISon4 v = new ISon4();
//            errs = v.validate(map);
//            Java2vRules.digest(map, sonFile,errorInfo);
//            
//            list = impl.validate(map, sonFile,errorInfo);
            Object obj = new Rule();
            Object obj1 = new VRuleSuite();
//            list = impl.validate(new Object[]{obj,obj1}, sonFile,errorInfo);
            errs = impl.validate(map, sonFile);
            
//            System.out.println(new XStream().toXML(obj));
//            System.out.println(new XStream().toXML(obj1));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (errs != null) {
            logger.info("error is " + errs.length);
            for (int i = 0; i < errs.length; i++) {
                logger.info(errs[i]);
            }
        }
        if (list != null) {
            logger.info("error size is " + list.size());
//            logger.info("error is " + ObjectSerializer.xStreamToXml(list));
//            for (ErrorInfo e: list) {
//                logger.info(ObjectSerializer.xStreamToXml(e.getXPath()));
////                logger.info(e.getErrorCode());
////                logger.info(e.getErrorMsg());
////                logger.info(e.getError().getErrorId());
//            }
        }

    }
    public void Validate1() throws Exception {

       
        String[] errs = null;

        try {
            errs = impl.validate(father, fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
//       
//        try {
//            for(int i =0;i<1; i++){
//                long start = System.currentTimeMillis();
//                errs = impl.validate(father, fileName);
//                long end = System.currentTimeMillis();
//                logger.info("time cost " + (end - start) / 1000.00 + " s");
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        

       
        logger.info("error is " + errs.length);
        
        if (errs != null) {
            for (int i = 0; i < errs.length; i++) {
                logger.info(errs[i]);
            }
        }
        System.out.println("the father name is " +father.getName());
        assertEquals(true, errs != null);
    }

    public void Validate2() {

        String[] errs = null;
        List errList = null;
        long start = System.currentTimeMillis();
        
        test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
        errorInfo.setErrorCode("");
        errorInfo.setErrorMsg("");
//        errorInfo.setXPath("");
        
        try {
            errList = impl.validate(new Object[]{son,father}, fileName,errorInfo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        logger.info("time cost " + (end - start) / 1000.00 + " s");

         if (errs != null) {
         logger.info("error is " + errs.length);
         for (int i = 0; i < errs.length; i++) {
//         logger.info(""+errs[i].substring(errs[i].indexOf("${xPath}:=")+10));
             logger.info(""+errs[i]);
         }
         }

        //assertEquals(true, errs != null);
    }
    
    public void Validate_List() {

        String[] errs = null;
        List<ErrorInfo> errList = null;
        long start = System.currentTimeMillis();
        
        ErrorInfo errorInfo = new test.cases.ErrorInfo();
        errorInfo.setErrorCode("");
        errorInfo.setErrorMsg("");
//        errorInfo.setXPath("");
        
        ErrorField errorField = new ErrorField();
//        errorField.setErrorId("");
        errorField.setXPath("");
        errorInfo.setErrorField(new ErrorField[]{errorField,errorField});
        
        
        try {
            for (int i = 0; i < 1; i++) {
                long s = System.currentTimeMillis();
                errList = impl.validate(new Object[] { son }, fileName,errorInfo);
                //impl.validate(new Object[] { new SearchPolicyInputVO() }, fileName1,errorInfo);
                long e = System.currentTimeMillis();
                logger.info("errList time cost " + (e - s) / 1000.00 + " s");
                
                s = System.currentTimeMillis();
                errs = impl.validate(new Object[] { son }, fileName);
                e = System.currentTimeMillis();
                logger.info(" errString time cost " + (e - s) / 1000.00 + " s");
            }            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        logger.info("time cost " + (end - start) / 1000.00 + " s");

         if (errList != null) {
            //logger.info("error is " + errs.length);
            for (Object e : errList) {
                if(e != null && e instanceof ErrorInfo){
                logger.info(((ErrorInfo)e).getErrorMsg() + ": and xpath is " + ((ErrorInfo)e).getXPath());
                }
                else{
                    System.out.println("the type is " + e.getClass().getName());
                }
                //logger.info("errorField xpath is : " + e.getErrorField()[0].getXPath());
            }
        }

        //assertEquals(true, errs != null);
    }

    public void T() {

        // ArrayList l = new ArrayList();
        // Map m = new HashMap();
        //
        // Class[] lc = l.getClass().getInterfaces();
        // Class[] mc = m.getClass().getInterfaces();
        //        
        // logger.info("List");
        // for (Class c : lc) {
        // logger.info(c.getName());
        // }
        //
        // logger.info("Map");
        // for (Class c : mc) {
        // logger.info(c.getName());
        // }
        // List obj = new ArrayList();
        // List list = new ArrayList();
        //
        // CommonUtils.converArrayToList(obj.getClass().getInterfaces(), list);
        // if (list.contains(List.class)) {
        //
        // logger.info("sdfsdf");
        // }
        String[] str = new String[2];
        // HashMap map = new HashMap();
        // if(map instanceof Object ){
        // logger.info("true");
        // }else{
        // logger.info("false");
        // }

        Class[] cs = str.getClass().getClasses();
        // logger.info("" + str.getClass().getSuperclass().getName());
        for (int i = 0; i < cs.length; i++) {
            logger.info("" + cs[i].getName());
        }

    }

    public void testVRulesjToXML() {

        String sonFile = "D:/eclipse/vRules-3.x/doc1/test/files/ErrorInfo11.xml";

        try {
             Java2vRules.digest(new ErrorField(), sonFile);
//            Object obj = ObjectSerializer.unmarshalToObject(
//                    XmlUtils.getDocumentByFileName(sonFile), vRules4j.class);
//            String xml = ObjectSerializer.marshalToXml(obj);
            
//            System.out.println(xml);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
