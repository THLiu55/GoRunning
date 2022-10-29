package com.example.gorunning.utils;

import android.util.Log;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MailSender {
    private static final String from = "android_proj@163.com";
    private static final String password = "OWZDJTUINROTLATC";
    private static final String host = "smtp.163.com";

    public static Session getMailSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };
        return Session.getDefaultInstance(properties, authenticator);
    }

    public static boolean sendMailToUser(String recipients, String title, String body) {
        Session session = getMailSession();
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipients));
            message.setSubject(title);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException ex) {
            Log.d("error", ex.toString());
            return false;
        }
        return true;
    }
}