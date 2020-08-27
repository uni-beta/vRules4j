package test.cases;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationGenericType;
import com.unibeta.vrules.annotation.VRules4jAnnotations.ValidationRules;

public class Father extends Family{
     String name;
    @ValidationRules(desc = "age > 12", errorMessage = "father's age is invalid!", outputBool = "", predicate = "age <0", breakpoint = "", depends = "", name = "", sequence = "")
    int age;
    @ValidationGenericType(className="test.cases.Son")
    double weight;
    Double income;
    @ValidationGenericType(className="test.cases.Son")
    List<Son> sonList;
    @ValidationGenericType(className="test.cases.Son")
    Map<String,Son> sonMap;
    @ValidationGenericType(className="test.cases.Son")
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
    
    @ValidationRules(breakpoint = "on", depends = "", desc = "jordan", errorMessage = "ok", name = "ok", outputBool = "", predicate = "true", sequence = "")
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
