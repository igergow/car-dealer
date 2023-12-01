import java.util.Objects;

public class Car {
    private String brand;
    private String model;
    private double price;

    public Car(String brand, String model, double price) {
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    public String getModel() { return brand + " " + model; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return "Car@" + "brand=" + brand + ":model=" + model + ":price=" + price;
    }
}