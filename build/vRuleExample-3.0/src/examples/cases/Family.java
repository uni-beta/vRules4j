package examples.cases;

import javax.xml.bind.annotation.XmlElementWrapper;

public class Family {
    @XmlElementWrapper(name = "jordanFather")
    Father father;
    Son son;
    boolean rich;
    int members;
    double totalIncome;

    
    public double getTotalIncome() {
    
        return totalIncome;
    }

    
    public void setTotalIncome(double totalIncome) {
    
        this.totalIncome = totalIncome;
    }

    public Father getFather() {

        return father;
    }

    public void setFather(Father father) {

        this.father = father;
    }
    @XmlElementWrapper(name = "myMember")
    public int getMembers() {

        return members;
    }

    public void setMembers(int members) {

        this.members = members;
    }

    public boolean isRich() {

        return rich;
    }

    public void setRich(boolean rich) {

        this.rich = rich;
    }

    public Son getSon() {

        return son;
    }

    public void setSon(Son son) {

        this.son = son;
    }
}
