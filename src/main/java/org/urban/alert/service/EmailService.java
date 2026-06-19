package org.urban.alert.service;

public interface EmailService {

    void send(String to, String subject, String body);
}