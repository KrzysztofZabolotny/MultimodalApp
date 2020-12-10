/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private String driverId;
    private String companyName;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "parcel_id")
    List<Parcel> parcels = new ArrayList<>();

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }


    public Transport() {

    }

    public Transport(int id, LocalDate departureDate, String destination, String driverId, List<Parcel> parcels) {
        this.id = id;
        this.departureDate = departureDate;
        this.destination = destination;
        this.driverId = driverId;
        this.parcels = parcels;
    }

    public Transport(int id, LocalDate departureDate, String destination, String driverId) {
        this.id = id;
        this.departureDate = departureDate;
        this.destination = destination;
        this.driverId = driverId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String clientId) {
        this.driverId = clientId;
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
                ", clientId='" + driverId + '\'' +
                ", parcels=" + parcels +
                '}';
    }
}
