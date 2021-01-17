/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;


import javax.persistence.Id;
import javax.persistence.*;

@Entity
@Table
public class PriceRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;
    private int fromWeight;
    private int price;
    private int toWeight;
    @ManyToOne(cascade = CascadeType.ALL)
    private Transport transport;
    public PriceRange(){

    }

    public PriceRange(int fromWeight, int toWeight, int price) {
        this.fromWeight = fromWeight;
        this.price = price;
        this.toWeight = toWeight;
    }

    public PriceRange(int id, int fromWeight, int toWeight, int price) {
        this.id = id;
        this.fromWeight = fromWeight;
        this.price = price;
        this.toWeight = toWeight;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromWeight() {
        return fromWeight;
    }

    public void setFromWeight(int fromWeight) {
        this.fromWeight = fromWeight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getToWeight() {
        return toWeight;
    }

    public void setToWeight(int toWeight) {
        this.toWeight = toWeight;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    @Override
    public String toString() {
        return fromWeight+" kg do "+toWeight+" kg"+" -> "+price+" Nok";
    }
}
