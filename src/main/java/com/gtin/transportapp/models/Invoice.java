/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import com.gtin.transportapp.services.Utilities;

import javax.persistence.*;

@Entity
@Table
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int transportId;
    String transportDetails;
    String transportOwner;
    String status;
    String deliveryDate;

    double debt;
    double numberOfParcels;


    public Invoice() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public String getTransportDetails() {
        return transportDetails;
    }

    public void setTransportDetails(String transportDetails) {
        this.transportDetails = transportDetails;
    }

    public String getTransportOwner() {
        return transportOwner;
    }

    public void setTransportOwner(String transportOwner) {
        this.transportOwner = transportOwner;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getNumberOfParcels() {
        return numberOfParcels;
    }

    public void setNumberOfParcels(double numberOfParcels) {
        this.numberOfParcels = numberOfParcels;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Invoice populateAfterDelivery(Transport transport) {

        int value = 0;
        for(Parcel parcel: transport.getParcels()){
            value+=parcel.getValue();

        }

        this.setTransportId(transport.getId());
        this.setDebt(Utilities.calculateInvoice(value));
        this.setStatus("Delivered");
        this.setDeliveryDate(Utilities.timeStamp());
        this.setTransportOwner(transport.getDriverId());
        this.setNumberOfParcels(transport.getNumberOfParcels());
        this.setTransportDetails(transport.getCompanyName() + "," + transport.getDestination() + "," + transport.getPricing());

        return this;

    }
}
