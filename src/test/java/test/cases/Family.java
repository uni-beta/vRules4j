package test.cases;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name ="xuejunxing")
public class Family {
    @XmlElementWrapper(name = "jordanFather")
    Father father;
    @XmlElementWrapper(name = "MYSON")
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

    @XmlElementWrapper(name = "jordanFather")
    public Father getFather() {

        return father;
    }

    public void setFather(Father father) {

        this.father = father;
    }

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
    @XmlElementWrapper(name = "getMYSON")
    public Son getSon() {

        return son;
    }

    public void setSon(Son son) {

        this.son = son;
    }
}
