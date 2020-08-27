package test.cases;

import javax.xml.bind.annotation.XmlElementWrapper;


public interface ISon {
    
    public final static String TEST_VALUE = "1"; 
    @XmlElementWrapper(name = "MYSON")
    public String getIfTif();

    
    public void setIfTif(String ifT) ;

    
    public String getOutTout() ;

    
    public void setOutTout(String outT) ;

    public int getId() throws Exception;

    public void setId(int id) ;
    @XmlElementWrapper(name = "MYSON")
    public String getName() throws Exception;
    public String getMyName(String id) throws Exception;

    public void setName(String name);

    public double getWeight() throws Exception;

    public void setWeight(double weight) ;

    
    public int getAge()throws Exception;

    
    public void setAge(int age) ;
    
    public Double getTall();

    
    public void setTall(Double tall) ;
}
