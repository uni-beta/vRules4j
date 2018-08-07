package examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.engines.ValidationEngineFactory;

import examples.cases.ErrorField;
import examples.cases.ErrorInfo;
import examples.cases.Family;
import examples.cases.Father;
import examples.cases.Son;

public class ValidationExample {

    public static Family family = new Family();
    public static Father father = new Father();
    public static Son son = new Son();

    static Father buildFather() {

        father = new Father();

        father.setAge(45);
        father.setIncome(new Double(200));
        father.setName("father");
        father.setWeight(100);

        List<Son> sons = new ArrayList<Son>();
        sons.add(buildSon());
        sons.add(buildSon());
        father.setSonList(sons);

        Map<String, Son> map = new HashMap<String, Son>();
        map.put("son1", buildSon());
        father.setSonMap(map);

        father.setSonArray(new Son[] { buildSon(), buildSon() });
        return father;
    }

    static Son buildSon() {

        son = new Son();
        son.setId(12);
        son.setName("son");
        son.setWeight(80);
        son.setTall(new Double(163.6));
        son.setAge(18);

        return son;
    }

    static Family buildFamily() {

        family = new Family();

        family.setFather(buildFather());
        family.setSon(buildSon());
        family.setMembers(3);
        family.setRich(false);

        return family;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        BasicConfigurator.configure();
        String fileName = "D:/eclipse/vRuleExample-3.0/rules/exampleRules.xml";

        String errs[] = null;
        List<examples.cases.ErrorInfo> list = null;
        examples.cases.ErrorInfo info = new ErrorInfo();
        
        info.setErrorCode("CODE_1");
        info.setErrorMsg("");
        info.setXPath("");
        
        ErrorField errorField = new ErrorField();
        errorField.setErrorId("ERROR_ID_1");
        errorField.setXPath("");
        info.setErrorField(new ErrorField[]{errorField,errorField});
        info.setError(errorField);

        try {
            buildFamily();
            ValidationEngine engine = ValidationEngineFactory.getInstance();

            //list = engine.validate(new Object[] { family }, fileName,info);
//            Java2vRules.digest(new Class[]{ISon.class}, fileName,info);

            Map<String,Object> map = new HashMap();
            map.put("ISon", son);
            map.put("Family", family);

            long start = System.currentTimeMillis();
            
            list = engine.validate(new Object[] {  son, family, father  }, fileName,info);
            errs = engine.validate(new Object[] {  family,son, family, father },
                    fileName);       
            long end = System.currentTimeMillis();

            System.out.println("the cost is " + (end - start) / 1000.00);
            
            errs = engine.validate( map ,
                    fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != list) {
            System.out.println("error is " + list.size());
            for (ErrorInfo e : list) {
                System.out.println("" + e.getXPath() +":" + e.getErrorCode());
                System.out.println("filed value is " + e.getError().getErrorId());
            }

        }

        if (null != errs) {
            System.out.println("Error messages are:" + errs.length);
            for (int i = 0; i < errs.length; i++) {
                // System.out.println(errs[i].substring(errs[i].indexOf("${xPath}:=")+10));
                System.out.println(errs[i]);
            }

        }

        System.out.println("End");
    }
}
