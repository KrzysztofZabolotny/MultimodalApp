/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    private String destination;
    private String clientId;

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "parcel_id")
    List<Parcel> parcels = new ArrayList<>();

    public Transport(){

    }
    public Transport(int id, LocalDate departureDate, String destination, String clientId, List<Parcel> parcels) {
        this.id = id;
        this.departureDate = departureDate;
        this.destination = destination;
        this.clientId = clientId;
        this.parcels = parcels;
    }

    public Transport(int id, LocalDate departureDate, String destination, String clientId) {
        this.id = id;
        this.departureDate = departureDate;
        this.destination = destination;
        this.clientId = clientId;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", departureDate=" + departureDate +
                ", destination='" + destination + '\'' +
                ", clientId='" + clientId + '\'' +
                ", parcels=" + parcels +
                '}';
    }
}
