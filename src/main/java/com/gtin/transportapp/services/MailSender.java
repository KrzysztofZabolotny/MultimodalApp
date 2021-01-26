package com.gtin.transportapp.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
/*mrrdnqjnnuqulhzc*/


public class MailSender implements Runnable {
    public static Properties properties;
    public static Session session;

    public static final String SENDER = "multimodaltransportapp@gmail.com";
    public static final String PASSWORD = "mrrdnqjnnuqulhzc";
    private final String RECIPIENT;
    private static final String TITLE = "Powiadomienie z aplikacji Transport Terminal";
    private final String CONTENT;

    public MailSender(String recipient, String content){
        this.RECIPIENT = recipient;
        this.CONTENT = content;
    }

    public void run(){
        try {
            sendMail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setupProperties(){
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.enable", "true");
    }
    public static void setupSession(){
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER, PASSWORD);
            }
        });
    }
    public void sendMail() throws Exception{
        setupProperties();
        setupSession();
        Message message = prepareMessage(session, RECIPIENT, CONTENT);
        Transport.send(message);
    }
    private static Message prepareMessage(Session session, String recipient, String content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
        message.setSubject(TITLE);
        message.setText(content);
        return message;
    }
}