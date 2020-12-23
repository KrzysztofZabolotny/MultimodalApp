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
    private String numberOfParcels = "0";
    private int transportValue = 0;
    private int capacity;
    private int load;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "range_id")
    List<PriceRange> priceRanges = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "parcel_id")
    List<Parcel> parcels = new ArrayList<>();


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNumberOfParcels() {
        return numberOfParcels;
    }

    public void setNumberOfParcels(String numberOfParcels) {
        this.numberOfParcels = numberOfParcels;
    }

    public int getTransportValue() {
        return transportValue;
    }

    public void setTransportValue(int transportValue) {
        this.transportValue = transportValue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<PriceRange> getPriceRanges() {
        return priceRanges;
    }

    public void setPriceRanges(List<PriceRange> priceRanges) {
        this.priceRanges = priceRanges;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }
    public void increaseParcelCount(){
        int numberOfParcels = Integer.parseInt(this.getNumberOfParcels());
        numberOfParcels++;
        this.setNumberOfParcels(String.valueOf(numberOfParcels));
    }

    public boolean permitLoading(int parcelWeight){

        return this.load+parcelWeight<this.capacity;
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
