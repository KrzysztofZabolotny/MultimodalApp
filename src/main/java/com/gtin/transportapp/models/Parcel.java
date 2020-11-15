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
    private String userName;
    private String content;

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
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
