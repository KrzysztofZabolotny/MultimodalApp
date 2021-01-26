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
    private String status = "OCZEKUJE NA ZATWIERDZENIE";
    private String additionalComments;
    private String inTransportName;
    private String owner;
    private String ownerPhoneNumber;
    private String driverEmail;
    private String driverPhoneNumber;

    private int weight;
    private int width;
    private int length;
    private int height;
    private int inTransportNumber;
    private int value;
    private LocalDate departureDate;

    private String senderName;
    private String senderSurname;
    private String senderStreet;
    private String senderCity;
    private String senderZip;
    private String senderCountry;
    private String senderPhoneNumber;

    private String receiverName;
    private String receiverSurname;
    private String receiverStreet;
    private String receiverCity;
    private String receiverZip;
    private String receiverCountry;
    private String receiverPhoneNumber;


    public Parcel() {

    }



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

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
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


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderSurname() {
        return senderSurname;
    }

    public void setSenderSurname(String senderSurname) {
        this.senderSurname = senderSurname;
    }

    public String getSenderStreet() {
        return senderStreet;
    }

    public void setSenderStreet(String senderStreet) {
        this.senderStreet = senderStreet;
    }

    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }

    public String getSenderZip() {
        return senderZip;
    }

    public void setSenderZip(String senderZip) {
        this.senderZip = senderZip;
    }

    public String getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(String senderCountry) {
        this.senderCountry = senderCountry;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverSurname() {
        return receiverSurname;
    }

    public void setReceiverSurname(String receiverSurname) {
        this.receiverSurname = receiverSurname;
    }

    public String getReceiverStreet() {
        return receiverStreet;
    }

    public void setReceiverStreet(String receiverStreet) {
        this.receiverStreet = receiverStreet;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getReceiverCountry() {
        return receiverCountry;
    }

    public void setReceiverCountry(String receiverCountry) {
        this.receiverCountry = receiverCountry;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
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

    public String formatAddress() {

        return receiverStreet
                + "\n"
                + receiverCity
                + "\n"
                + receiverZip
                + "\n"
                + receiverCountry;
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

    public String parcelSummary() {
        return
                "Adres dostawy:\n"
                        + receiverStreet
                        + "\n"
                        + receiverCity
                        + "\n"
                        + receiverZip
                        + "\n"
                        + receiverCountry
                        + "\n\nWymiary:"
                        + "\nwysokosc: " + height + "cm"
                        + "\nszerokosc: " + width + "cm"
                        + "\ndługość: " + length + "cm"
                        + "\nWaga: " + weight + "kg"
                        + "\n\nZawartość: " + content
                        + "\nKomentarz: " + additionalComments
                        + "\nKwota do zapłaty po akceptacji: " + value + "Nok";
    }
}
