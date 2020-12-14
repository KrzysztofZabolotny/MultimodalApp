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
    private String weight;
    private String width;
    private String length;
    private String height;
    private String destination;
    private String status;
    private String additionalComments;
    private LocalDate departureDate;


    public Parcel(int id, String userName, String content, String weight, String width, String length, String height) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.weight = weight;
        this.width = width;
        this.length = length;
        this.height = height;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Parcel(){

    }

    public Parcel(String userName,String content) {
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

    public static int calculateValueEnhanced(int weight) {

        int group = 0;

        if (weight <= 10) group = -1;
        if (weight > 10 && weight <= 20) group = 0;
        if (weight > 20) group = 1;

        int value = switch (group) {

            case -1 -> 500;
            case 0 -> 700;
            case 1 -> 1000;

            default -> throw new IllegalStateException("Unexpected value:" + group);
        };

        return value;
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
