package test.com.unibeta.vrules.engines;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import test.cases.ErrorField;
import test.cases.ErrorInfo;
import test.cases.ISon;
import test.cases.Son;
import test.com.unibeta.vrules.BasicTester;

import com.unibeta.vrules.engines.DecisionEngine;
import com.unibeta.vrules.engines.DecisionEngineFactory;
import com.unibeta.vrules.tools.Java2vRules;

public class DecisionEngineTester extends BasicTester {

    test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
    {
    errorInfo.setErrorCode("ERROR_CODE_1");
    errorInfo.setErrorMsg("ERROR_MSG_BAD");
    errorInfo.setXPath("");
    
    ErrorField errorField = new ErrorField();
//    errorField.setErrorId("ERROR_FILED_1");
    errorInfo.setError(errorField);
    }
    DecisionEngine impl = DecisionEngineFactory.getInstance();
//    ValidationEngine impl = ValidationEngineFactory.getPlainEngineInstance();
    public DecisionEngineTester(String string) {

        super(string);
    }
    
    public void Interface() throws Exception{
        
        Type c[] = ISon.class.getGenericInterfaces();
        
        System.out.println(c.length);
        
        String errs = null;
        String sonFile = "E:/eclipse/vRules-2.x/doc1/ISon3.xml";
        try {
            Java2vRules.digest(new Class[]{ISon.class,Son.class}, sonFile);
            ISon s = son;
            errs = impl.decide(s, sonFile,"ISon");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
// logger.info("error is " + errs.length);
        
        if (errs != null) {
                logger.info(errs);
        }
        System.out.println("the father name is " +father.getName());
//        assertEquals(true, errs != null);
    }
    public void testContext() throws Exception{
        
        String errs = null;
        ErrorInfo decisionResult = null;
        String sonFile = "D:/eclipse/vRules-3.x/doc1/ISon2.xml";
        try {
            
            Map<String,Object> map = new LinkedHashMap<String, Object>();
            
            ISon s = son;
//            map.put("Father", father);
            map.put("Son", son);
//            map.put("Family", family);
//            com.unibeta.vrules.engines.dccimpls.rules.ISon4 v = new ISon4();
//            errs = v.validate(map);
//            Java2vRules.digest(map, sonFile,errorInfo);
            errs = impl.decide(map, sonFile);
//            list = impl.validate(map, sonFile,errorInfo);
//            list = impl.validate(new Object[]{son,family}, sonFile,errorInfo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
      
        
        if (errs != null) {
                System.out.println(errs);
        }
        if (decisionResult != null) {
                logger.info(decisionResult.getXPath());
//                logger.info(e.getErrorCode());
                logger.info(decisionResult.getErrorMsg());
//                logger.info(e.getError().getErrorId());
        }

    }
}
