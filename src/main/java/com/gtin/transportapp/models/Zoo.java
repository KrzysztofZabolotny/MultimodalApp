/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import java.util.List;

public class Zoo {

    int size;
    String address;

    List<Animal> animals;

    public Zoo() {
    }

    public Zoo(int size, String address, List<Animal> animals) {
        this.size = size;
        this.address = address;
        this.animals = animals;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adres) {
        this.address = adres;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
