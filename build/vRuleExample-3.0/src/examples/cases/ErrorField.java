package examples.cases;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationErrorXPath;


public class ErrorField {

    String errorId;
    @ValidationErrorXPath
    String xPath;
    
    public String getErrorId() {
    
        return errorId;
    }
    
    public void setErrorId(String errorId) {
    
        this.errorId = errorId;
    }
    
    public String getXPath() {
    
        return xPath;
    }
    
    public void setXPath(String path) {
    
        xPath = path;
    }
    
}
