package com.railwayReservationPayment.Payment_Gateway.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPaymentSuccessEmail(String toEmail, String userName, String transactionId, double amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment Successful");
        message.setText("Hi " + userName + ",\n\nYour payment was successful.\nTransaction ID: " + transactionId +
                "\nAmount Paid: ₹" + amount + "\nThank you for using our service!");
        mailSender.send(message);
    }
}
