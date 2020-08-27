package test.com.unibeta.vrules.engines.bshimpls;

import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.engines.bshimpls.ValidationEngineBshImpl;

import test.cases.Son;
import test.com.unibeta.vrules.BasicTester;

public class ValidationEngineImplTester extends BasicTester {

    ValidationEngine impl = new ValidationEngineBshImpl();

    public ValidationEngineImplTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void testValidate1() throws Exception {

        Son c = new Son();
        c.setId(20);
        c.setName("jordan");
        c.setWeight(120);

        long start = System.currentTimeMillis();
        String[] errs = impl.validate(son, fileName);

        long end = System.currentTimeMillis();

        logger.info("time cost " + (end - start) / 1000.00);
        // logger.info("error is " + errs.length);
        // for (String str : errs) {
        // logger.info(str);
        // }
        assertEquals(true, errs != null);
    }

    public void testValidate2() throws Exception {

        Son c = new Son();
        c.setId(20);
        c.setName("jordan");
        c.setWeight(120);

        String[] errs = null;
        long start = System.currentTimeMillis();
        errs = impl.validate(c, fileName);
        long end = System.currentTimeMillis();
        logger.info("time cost " + (end - start) / 1000.00);

        // logger.info("error is " + errs.length);
        // for (String str : errs) {
        // logger.info(str);
        // }
        assertEquals(true, errs != null);
    }
}
