package test.com.unibeta.vrules.perf;

import junit.framework.Test;
import test.com.unibeta.vrules.engines.ValidationEngineTester;

import com.clarkware.junitperf.LoadTest;
import com.unibeta.vrules.parsers.ErrorObjectPoolManager;

class EngineLoadTester {

    public static Test suite() {

        int maxUsers = 1000;
        long maxElapsedTime = 60000;

        Test testCase = new ValidationEngineTester("testContext");
        Test loadTest = new LoadTest(testCase, maxUsers);
        // Test timedTest = new TimedTest(loadTest, maxElapsedTime);

        return loadTest;
    }

    public static void main(String[] args) {

//        junit.textui.TestRunner.run(suite());
        System.out.println("ErrorObjectPoolManager id is "
                + ErrorObjectPoolManager.generateSequenceId());
        ;
    }
}
