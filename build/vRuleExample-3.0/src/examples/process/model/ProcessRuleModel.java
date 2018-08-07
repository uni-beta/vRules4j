package examples.process.model;

public class ProcessRuleModel {

    ProcessRuleModel processModel = null;

    public ProcessRuleModel getProcessModel() {

        if(null == processModel){
            processModel = this;
        }
        return processModel;
    }

    public void setProcessModel(ProcessRuleModel processModel) {

        this.processModel = processModel;
    }

}
