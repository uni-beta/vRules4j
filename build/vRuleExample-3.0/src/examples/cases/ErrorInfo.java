package examples.cases;

import javax.xml.bind.annotation.XmlRootElement;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationErrorMessage;
import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationErrorXPath;
@XmlRootElement
public class ErrorInfo {

    private String errorCode;
    @ValidationErrorMessage
    String errorMsg;
    @ValidationErrorXPath//(isErrorXpath = "true")
    String xPath;
    ErrorField[]  errorField;
    ErrorField error;
    
    
    public ErrorField getError() {
    
        return error;
    }


    
    public void setError(ErrorField error) {
    
        this.error = error;
    }


    public ErrorField[] getErrorField() {
    
        return errorField;
    }

    
    public void setErrorField(ErrorField[] errorField) {
    
        this.errorField = errorField;
    }

    public String getErrorCode() {
    
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
    
        this.errorCode = errorCode;
    }
    
   
    public String getErrorMsg() {
    
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
    
        this.errorMsg = errorMsg;
    }
    
    public String getXPath() {
    
        return xPath;
    }
    
    public void setXPath(String path) {
    
        xPath = path;
    }
}
