package test.cases;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Son extends Father implements ISon{
    public final static String TEST_VALUE = "1"; 
    int test;
    int id;
    int age;
    @XmlElementWrapper(name = "MYSON")
    String name;
    double weight;
    Double tall;
    double income;
    String ifTif;
    String outTout;

    
//    public double getIncome() {
//    
//        return income;
//    }

    
    @XmlElementWrapper(name = "MYSON")
    public String getIfTif() {
    
        return ifTif;
    }

    
    public void setIfTif(String ifT) {
    
        this.ifTif = ifT;
    }

    
    public String getOutTout() {
    
        return outTout;
    }

    
    public void setOutTout(String outT) {
    
        this.outTout = outT;
    }

    public void setIncome(double income) {
    
        this.income = income;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }
    @XmlElementWrapper(name = "MYSON")
    public String getName() throws Exception{

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getWeight() {

        return weight;
    }

    public void setWeight(double weight) {

        this.weight = weight;
    }

    
    public int getAge() {
    
        return age;
    }

    
    public void setAge(int age) {
    
        this.age = age;
    }

    
    public Double getTall() {
    
        return tall;
    }

    
    public void setTall(Double tall) {
    
        this.tall = tall;
    }


    public String getMyName(String id) throws Exception {

        // TODO Auto-generated method stub
        return null;
    }


    
    public int getTest() {
    
        return test;
    }


    
    public void setTest(int test) {
    
        this.test = test;
    }


    
    public Double getIncome() {
    
        return income;
    }

   
}
