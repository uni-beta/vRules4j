package test.com.unibeta.vrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unibeta.vrules.parsers.ObjectSerializer;

import junit.framework.TestCase;
import test.cases.Family;
import test.cases.Father;
import test.cases.Son;



public class BasicTester extends TestCase {

    public BasicTester(String string) {

        super(string);
    }

    protected Logger logger = LoggerFactory.getLogger(BasicTester.class);

    protected String fileName = "D:/eclipse/vRules-3.x/doc1/FatherRules2.xml";
    protected String fileName1 = "D:/eclipse/vRules-3.x/doc1/FatherRules1.xml";
    protected String dccFileName = "D:/eclipse/vRules-3.x/doc/validationRules.xml";
//     protected String fileName =
//     "D:/eclipse/vRuleExample/rules/validationRules.xml";

    protected Family family = buildFamily();
    protected Father father = buildFather();
    protected Son son = buildSon();

    {
//        BasicConfigurator.configure();
//        logger.setLevel(Level.INFO);

        family = buildFamily();
        father = buildFather();
        son = buildSon();
    }

    Father buildFather() {

        Father father = new Father();

        father.setAge(45);
        father.setIncome(new Double(200));
        father.setName("father");
        father.setWeight(100);

        List sons = new ArrayList();
        sons.add(buildSon());
        sons.add(buildSon());
        father.setSonList(sons);

        Map map = new HashMap();
        map.put("son1", buildSon());
        father.setSonMap(map);

        father.setSonArray (new Son[]{buildSon(),buildSon()});
        return father;
    }

    Son buildSon() {

        Son son = new Son();
        son.setId(12);
        son.setName("son");
        son.setWeight(80);
        son.setTall(new Double(156.5));
        son.setAge(18);
        son.setIncome(new Double(10));
       

        return son;
    }

    Family buildFamily() {

        Family family = new Family();

        family.setFather(buildFather());
        family.setSon(buildSon());
        family.setMembers(3);
        family.setRich(false);

        return family;
    }

    public  void testMain() {

        try {

//             Java2vRules.digest(new Class[]{null}, fileName);
            logger.info(ObjectSerializer.xStreamToXml(18));
            // logger.info("type name is ".split(",")[0]);
//            logger.info("" + Father.class.getSuperclass().getName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void testURLConfiguration(){
        try {
            com.unibeta.vrules.servlets.URLConfiguration.initClasspathURLs();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}