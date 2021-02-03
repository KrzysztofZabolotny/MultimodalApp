/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
        private String destination;
    private String status = "OCZEKUJE NA ZATWIERDZENIE";
    private String additionalComments;
        private String inTransportName;
    private int inTransportNumber;
    private String owner;
    private String ownerPhoneNumber;
    private String driverEmail;
    private String driverPhoneNumber;

    private LocalDate departureDate;

    private String passengerName;
    private String passengerSurname;
    private String passengerStreet;
    private String passengerCity;
    private String passengerZip;
    private String passengerCountry;
    private String passengerPhoneNumber;

    private String destinationStreet;
    private String destinationCity;
    private String destinationZip;
    private String destinationCountry;


    public Passenger() {

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

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerSurname() {
        return passengerSurname;
    }

    public void setPassengerSurname(String passengerSurname) {
        this.passengerSurname = passengerSurname;
    }

    public String getPassengerStreet() {
        return passengerStreet;
    }

    public void setPassengerStreet(String passengerStreet) {
        this.passengerStreet = passengerStreet;
    }

    public String getPassengerCity() {
        return passengerCity;
    }

    public void setPassengerCity(String passengerCity) {
        this.passengerCity = passengerCity;
    }

    public String getPassengerZip() {
        return passengerZip;
    }

    public void setPassengerZip(String passengerZip) {
        this.passengerZip = passengerZip;
    }

    public String getPassengerCountry() {
        return passengerCountry;
    }

    public void setPassengerCountry(String passengerCountry) {
        this.passengerCountry = passengerCountry;
    }

    public String getPassengerPhoneNumber() {
        return passengerPhoneNumber;
    }

    public void setPassengerPhoneNumber(String passengerPhoneNumber) {
        this.passengerPhoneNumber = passengerPhoneNumber;
    }

    public String getDestinationStreet() {
        return destinationStreet;
    }

    public void setDestinationStreet(String destinationStreet) {
        this.destinationStreet = destinationStreet;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                ", additionalComments='" + additionalComments + '\'' +
                ", inTransportName='" + inTransportName + '\'' +
                ", inTransportNumber=" + inTransportNumber +
                ", owner='" + owner + '\'' +
                ", ownerPhoneNumber='" + ownerPhoneNumber + '\'' +
                ", driverEmail='" + driverEmail + '\'' +
                ", driverPhoneNumber='" + driverPhoneNumber + '\'' +
                ", departureDate=" + departureDate +
                ", passengerName='" + passengerName + '\'' +
                ", passengerSurname='" + passengerSurname + '\'' +
                ", passengerStreet='" + passengerStreet + '\'' +
                ", passengerCity='" + passengerCity + '\'' +
                ", passengerZip='" + passengerZip + '\'' +
                ", passengerCountry='" + passengerCountry + '\'' +
                ", passengerPhoneNumber='" + passengerPhoneNumber + '\'' +
                ", destinationStreet='" + destinationStreet + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", destinationZip='" + destinationZip + '\'' +
                ", destinationCountry='" + destinationCountry + '\'' +
                '}';
    }
}
