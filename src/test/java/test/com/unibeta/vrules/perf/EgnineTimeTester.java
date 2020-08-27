package test.com.unibeta.vrules.perf;

import junit.framework.Test;
import test.com.unibeta.vrules.engines.ValidationEngineTester;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TimedTest;


class EgnineTimeTester{
public static Test suite() {
     
        int maxUsers = 100;
        long maxElapsedTime = 15000;
        
        Test testCase = new ValidationEngineTester("testContext");
        Test loadTest = new LoadTest(testCase, maxUsers);
//        Test timedTest = new TimedTest(testCase, maxElapsedTime);

        return loadTest;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}