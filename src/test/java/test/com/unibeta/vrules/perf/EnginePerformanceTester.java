package test.com.unibeta.vrules.perf;

import junit.framework.Test;
import junit.framework.TestSuite;


class EnginePerformanceTester{

    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.addTest(EgnineTimeTester.suite());
        suite.addTest(EngineLoadTester.suite());
        
        return suite;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}