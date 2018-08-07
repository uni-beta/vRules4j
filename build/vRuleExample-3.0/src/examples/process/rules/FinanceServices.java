package examples.process.rules;

import examples.process.ServiceRule;
import examples.process.model.ServiceContext;


public class FinanceServices implements ServiceRule{

    public void process(ServiceContext serviceContext) throws Exception {

        System.out.println("FinanceServices processed!");
        
    }

}
