package test.com.unibeta.vrules.tools;

import test.cases.Father;
import test.cases.Son;
import test.com.unibeta.vrules.BasicTester;

import com.unibeta.vrules.base.vRules4j;
import com.unibeta.vrules.parsers.ObjectSerializer;
import com.unibeta.vrules.tools.Java2vRules;


public class Java2vRulesTester extends BasicTester {

    public Java2vRulesTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void testDegister(){
        Java2vRules java2vRules = new Java2vRules();
        try {
            test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
            errorInfo.setErrorCode("");
            errorInfo.setErrorMsg("");
            errorInfo.setXPath("");
            long start = System.currentTimeMillis();
            
//            java2vRules.digest(Father.class, fileName,errorInfo);
            java2vRules.digest(Son.class, fileName);
//            java2vRules.copyFile(fileName, fileName1);
            long end = System.currentTimeMillis();
//            Object[] os = new Object[]{};
//            Class[] interfaces = Son.class.getInterfaces();
//            System.out.println(interfaces.toString());
//            for(Class c: interfaces){
//                System.out.println(c.toString());
//            }
            logger.info("the cost is " + (end-start)/1000.00);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void testToXML(){
        String str = null;
        try {
            str = Java2vRules.toXml(new vRules4j(), "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(str);
    }
    
}
