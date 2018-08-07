package examples.cases;

import javax.xml.bind.annotation.XmlElementWrapper;

public interface ISon {

    public int getId();

    public void setId(int id);

    @XmlElementWrapper(name = "MYSON")
    public String getName()throws Exception;

    public void setName(String name);

    public double getWeight();

    public void setWeight(double weight);

    public int getAge();

    public void setAge(int age);

    public Double getTall();

    public void setTall(Double tall);
}
