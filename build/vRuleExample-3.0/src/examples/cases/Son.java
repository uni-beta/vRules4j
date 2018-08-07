package examples.cases;

public class Son extends Father implements ISon{

    int id;
    int age;
    String name;
    double weight;
    Double tall;
    double income;

    
//    public double getIncome() {
//    
//        return income;
//    }

    
    public void setIncome(double income) {
    
        this.income = income;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

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

   
}
