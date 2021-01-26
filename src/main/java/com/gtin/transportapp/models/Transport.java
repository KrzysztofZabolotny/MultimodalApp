/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
    private String driverPhoneNumber;
    private int numberOfParcels = 0;
    private int transportValue = 0;
    private int capacity;
    private int ballast = 0;
    private int value = 0;
    private String pricing;
    private String status;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "price_id")
    List<PriceRange> priceRanges = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "parcel_id")
    List<Parcel> parcels = new ArrayList<>();



    public Transport() {

    }

    public Transport(int id, LocalDate departureDate, String destination, String driverId, List<PriceRange> priceRanges) {
        this.id = id;
        this.departureDate = departureDate;
        this.destination = destination;
        this.driverId = driverId;
        this.priceRanges = priceRanges;
    }



//    public Transport(int id, LocalDate departureDate, String destination, String driverId, List<Parcel> parcels) {
//        this.id = id;
//        this.departureDate = departureDate;
//        this.destination = destination;
//        this.driverId = driverId;
//        this.parcels = parcels;
//    }

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

    public int getNumberOfParcels() {
        return numberOfParcels;
    }

    public void setNumberOfParcels(int numberOfParcels) {
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

    public int getBallast() {
        return ballast;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setBallast(int load) {
        this.ballast = load;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getPricing() {
        return concatenatePrices();
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public void increaseParcelCount(){
        int numberOfParcels = this.getNumberOfParcels();
        numberOfParcels++;
        this.setNumberOfParcels(numberOfParcels);
    }

    public boolean permitLoading(int parcelWeight){

        return this.ballast +parcelWeight<this.capacity;
    }

    public String concatenatePrices(){

        List<PriceRange> priceRanges = this.getPriceRanges();
        StringBuilder result = new StringBuilder();
        for (PriceRange priceRange: priceRanges){
            result.append(priceRange).append("\n\n");
        }

        return result.toString();

    }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ",\n departureDate=" + departureDate +
                ",\n destination='" + destination + '\'' +
                ",\n driverId='" + driverId + '\'' +
                ",\n companyName='" + companyName + '\'' +
                ",\n numberOfParcels=" + numberOfParcels +
                ",\n transportValue=" + transportValue +
                ",\n capacity=" + capacity +
                ",\n ballast=" + ballast +
                ",\n value=" + value +
                ",\n status='" + status + '\'' +
                ",\n priceRanges=" + priceRanges +
                ",\n parcels=" + parcels +
                '}';
    }
    public String transportSummary() {

        StringBuilder prices= new StringBuilder(new String());

        for (PriceRange priceRange: this.getPriceRanges()){
            if(priceRange!=null)
            prices.append(priceRange).append("\n");

        }


        return
                "Data wyjazdu: " + departureDate
                        + "\nKierunek: "+destination
                        +"\nNazwa firmy: "+companyName
                        +"\nŁadowność: "+capacity+"kg"
                        +"\nCennik: "
                        +"\n"
                        +prices;
    }
}
