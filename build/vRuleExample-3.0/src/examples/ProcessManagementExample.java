package examples;

import java.util.HashMap;
import java.util.Map;

import com.unibeta.vrules.engines.ValidationEngine;
import com.unibeta.vrules.engines.ValidationEngineFactory;

import examples.process.ServiceRule;
import examples.process.model.ProcessRuleModel;
import examples.process.model.ServiceContext;

public class ProcessManagementExample {

    static String fileName = "D:/eclipse/vRuleExample-3.0/rules/process/ProcessManagementExampleRules.xml";

    public static void main(String[] args) {
        ValidationEngine engine = ValidationEngineFactory.getInstance();
        Map<String, Object> map = new HashMap<String,Object>();
        
        String[] results = null;
        try {
            map.put("NewBizServices", new ProcessRuleModel());
            map.put("c", new ServiceContext());
            
            results = engine.validate(map,fileName);
            if(null != results){
                for(String s: results){
                    System.out.println(s);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    boolean invoke(String className,ServiceContext serviceContext) throws Exception{
        
        Class clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        ServiceRule rule = null;
        
        if(obj instanceof ServiceRule){
            rule = (ServiceRule)obj;
            rule.process(serviceContext);
        }
        
        return true;
    }
}
