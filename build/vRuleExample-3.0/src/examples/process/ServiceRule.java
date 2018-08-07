package examples.process;

import examples.process.model.ServiceContext;


public interface ServiceRule {

    public void process(ServiceContext serviceContext) throws Exception;
}
