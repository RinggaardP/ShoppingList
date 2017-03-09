package org.projects.shoppinglist;

/**
 * Created by Camilla on 16-02-2017.
 */

public class Product {

    String name;
    int quantity;

    public Product() {} //Empty constructor we will need later!

    public Product(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name+" "+quantity;
    }

}

