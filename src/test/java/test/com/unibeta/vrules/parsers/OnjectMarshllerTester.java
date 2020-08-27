package test.com.unibeta.vrules.parsers;

import test.cases.ErrorInfo;
import test.cases.Father;
import test.com.unibeta.vrules.BasicTester;

import com.unibeta.vrules.parsers.ObjectSerializer;

public class OnjectMarshllerTester extends BasicTester {

    public OnjectMarshllerTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void MarshalToXml() {

        try {
            test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
            errorInfo.setErrorCode("");
            errorInfo.setErrorMsg("");
            errorInfo.setXPath("");

            String str = ObjectSerializer.marshalToXml(errorInfo);

            logger.info("\n" + str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testUnmarshalToObject() {

        test.cases.ErrorInfo errorInfo = new test.cases.ErrorInfo();
        errorInfo.setErrorCode("");
        errorInfo.setErrorMsg("");
        errorInfo.setXPath("");
        try {
            errorInfo = (ErrorInfo) ObjectSerializer.unmarshalToObject(ObjectSerializer
                    .marshalToXml(errorInfo), ErrorInfo.class);
            logger.info("the vlaue is " + errorInfo.toString() );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
