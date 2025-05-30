package com.railway.reservation_service.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(String toEmail, String userName, String pnr) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Railway Reservation Confirmed");
        message.setText("Hi " + userName + ",\n\nYour booking is confirmed.\nPNR: "
                + pnr + "\nThank you for using our service");
        mailSender.send(message);
    }
}
