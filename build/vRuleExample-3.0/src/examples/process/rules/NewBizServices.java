package examples.process.rules;

import examples.process.ServiceRule;
import examples.process.model.ServiceContext;


public class NewBizServices implements ServiceRule{

    public void process(ServiceContext serviceContext) throws Exception {

        System.out.println("NewBizServices processed!");
        if(false){
            throw new Exception("NewBizServices Exception!");
        }
        
    }
}
