/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String content;
    private String destination;
    private String status = "REGISTERED";
    private String additionalComments;
    private String inTransportName;
    private int weight;
    private int width;
    private int length;
    private int height;
    private int inTransportNumber;
    private int value;
    private LocalDate departureDate;
    private String address;
    private String city;
    private String country;
    private String zip;


    public Parcel(String userName, String content, int weight, int width, int length, int height, String destination, String status) {
        this.userName = userName;
        this.content = content;
        this.weight = weight;
        this.width = width;
        this.length = length;
        this.height = height;
        this.destination = destination;
        this.status = status;
    }

    public String getInTransportName() {
        return inTransportName;
    }

    public void setInTransportName(String inTransportName) {
        this.inTransportName = inTransportName;
    }

    public int getInTransportNumber() {
        return inTransportNumber;
    }

    public void setInTransportNumber(int inTransportNumber) {
        this.inTransportNumber = inTransportNumber;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Parcel() {

    }

    public Parcel(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public Parcel(int id, String userName, String content) {
        this.id = id;
        this.userName = userName;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "Parcel{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", weight='" + weight + '\'' +
                ", width='" + width + '\'' +
                ", length='" + length + '\'' +
                ", height='" + height + '\'' +
                ", additionalComments='" + additionalComments + '\'' +
                '}';
    }
}
