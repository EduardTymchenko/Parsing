package org.tymchenko;

public class CoefficientLine {
    private long id;
    private String name;

    private float price;

    @Override
    public String toString() {
        return "\t\t\t" + name + ", " + price + ", " + id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
