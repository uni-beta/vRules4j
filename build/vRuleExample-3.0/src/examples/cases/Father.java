package examples.cases;

import java.util.List;
import java.util.Map;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationGenericType;
import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationRules;

public class Father extends Family{

    @ValidationRules(desc="", errorMessage="", outputBool="", predicate="name!=null", breakpoint = "", depends = "", name = "", sequence = "3")
    String name;
    int age;
    @ValidationRules(desc="", errorMessage="weight should larger than 100!", outputBool="", predicate="weight > 100", breakpoint = "", depends = "", name = "", sequence = "4")
    double weight;
    Double income;
    @ValidationRules(desc="", errorMessage="sonList is null!", outputBool="", predicate="sonList!= null", breakpoint = "", depends = "", name = "", sequence = "5")
    @ValidationGenericType(className="examples.cases.Son")
    List sonList ;
    @ValidationGenericType(className="examples.cases.Son")
    Map sonMap;
    @ValidationGenericType(className="examples.cases.Father")
    Son[] sonArray;

    
    public Son[] getSonArray() {
    
        return sonArray;
    }

    
    public void setSonArray(Son[] sonArray) {
    
        this.sonArray = sonArray;
    }

    public int getAge() {

        return age;
    }

    public void setAge(int age) {

        this.age = age;
    }

    public Double getIncome() {

        return income;
    }

    public void setIncome(Double income) {

        this.income = income;
    }

    public String getName() throws Exception {

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

    
    public List getSonList() {
    
        return sonList;
    }

    
    public void setSonList(List sons) {
    
        this.sonList = sons;
    }

    
    public Map getSonMap() {
    
        return sonMap;
    }

    
    public void setSonMap(Map sonMap) {
    
        this.sonMap = sonMap;
    }
}
