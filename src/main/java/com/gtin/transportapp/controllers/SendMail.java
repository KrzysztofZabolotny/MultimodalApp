package com.gtin.transportapp.controllers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {
    public static Properties properties;
    public static Session session;

    public static final String SENDER = "multimodaltransportapp@gmail.com";
    public static final String PASSWORD = "password";

    public static void setupProperties(){
        properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
    }
    public static void setupSession(){
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER, PASSWORD);
            }
        });
    }
    public static void setupMessage(String recipient,String title, String content) throws Exception{
        Message message = prepareMessage(session, SENDER, recipient, title, content );
        Transport.send(message);
        System.out.println("Message sent successfully");
    }
    public static void sendMail(String recipient, String title, String content) throws Exception {

        setupProperties();
        setupSession();
        setupMessage(recipient,title,content);

    }
    private static Message prepareMessage(Session session, String myMail, String recipient, String title, String content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myMail));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
        message.setSubject(title);
        message.setText(content);
        return message;
    }
}