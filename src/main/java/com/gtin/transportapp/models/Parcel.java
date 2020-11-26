/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import javax.persistence.*;

@Entity
@Table
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int inTransportNumber;
    private String userName;
    private String content;
    private String weight;
    private String width;
    private String length;
    private String height;
    private String additionalComments;

    public int getInTransportNumber() {
        return inTransportNumber;
    }

    public void setInTransportNumber(int inTransportNumber) {
        this.inTransportNumber = inTransportNumber;
    }

    public Parcel(int id, String userName, String content, String weight, String width, String length, String height) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.weight = weight;
        this.width = width;
        this.length = length;
        this.height = height;
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

    @Override
    public String toString() {
        return "Parcel{" +
                "id=" + id +
                ", inTransportNumber=" + inTransportNumber +
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
