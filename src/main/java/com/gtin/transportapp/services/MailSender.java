package com.gtin.transportapp.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {
    public static Properties properties;
    public static Session session;

    public static final String SENDER = "multimodaltransportapp@gmail.com";
    public static final String PASSWORD = "multimodal2020";

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
    public static void setupMessage(String recipient,String title, String content) throws Exception{
        Message message = prepareMessage(session, recipient, title, content );
        Transport.send(message);
    }
    public static void sendMail(String recipient, String title, String content) throws Exception {

        setupProperties();
        setupSession();
        setupMessage(recipient,title,content);

    }
    private static Message prepareMessage(Session session, String recipient, String title, String content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
        message.setSubject(title);
        message.setText(content);
        return message;
    }
}