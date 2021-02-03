/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import javax.persistence.*;

@Entity
@Table
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fromUserName;
    private String toUserName;
    private String messageContent;

    public Message() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String formUserName) {
        this.fromUserName = formUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", fromUserName='" + fromUserName + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
