/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gtin.transportapp.services.Utilities.timeStamp;

@Entity
@Table
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //@Column(unique = true)
    private String userName;
    private String companyName;
    private String name;
    private String surname;
    private String street;
    private String city;
    private String zip;
    private String email;
    private String password;
    private String code;
    private String phone;
    private String role;
    private String creationDate;
    @Transient
    private int oneTimeCode;



    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "parcel_id")
    List<Parcel> parcels = new ArrayList<>();

    public Client() {
        setCreationDate();
    }

    public Client(String userName, String name, String surname, String phone, String email) {
        this.userName = userName;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
    }

    public Client(int id, String userName, String name, String surname, String phone, String email, List<Parcel> parcels) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.parcels = parcels;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getOneTimeCode() {
        return oneTimeCode;
    }

    public void setOneTimeCode(int oneTimeCode) {
        this.oneTimeCode = oneTimeCode;
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

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String lastName) {
        this.surname = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String postalCode) {
        this.zip = postalCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public  void setCreationDate(){
        creationDate = timeStamp();
    }

//    @Override
//    public String toString() {
//
//        return "Username: "+userName+"\n"
//                +"Name: "+name+"\n"
//                +"Surname: "+surname+"\n"
//                +"City: "+city+"\n"
//                +"Street: "+street+"\n"
//                +"Zip: "+zip+"\n"
//                +"Phone number: "+code+phone+"\n";
//
//    }


    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", oneTimeCode=" + oneTimeCode +
                ", parcels=" + parcels +
                '}';
    }
}