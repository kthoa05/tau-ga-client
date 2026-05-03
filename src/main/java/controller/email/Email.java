package controller.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class Email {

    // Gửi email có OTP
    public static String sendEmail(String toEmail) {
        String otp = sinhOTPRamDom();
        String subject = "Mã OTP xác nhận đổi mật khẩu";
        String message = "Mã OTP của bạn là: " + otp + "\nVui lòng không chia sẻ mã này cho bất kỳ ai.";

        sendEmail(toEmail, subject, message);
        return otp; // trả về OTP để lưu lại và so sánh sau
    }

    // Hàm gửi email chính
    public static void sendEmail(String toEmail, String subject, String messageText) {
        final String fromEmail = "hotanan75@gmail.com"; // email của chương trình
        final String password = "vcrbgsnbjsyzmtws"; // MẬT KHẨU ỨNG DỤNG

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "Hệ thống xác nhận"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            msg.setText(messageText, "UTF-8");

            System.out.println("Đang gửi email tới: " + toEmail);

            Transport.send(msg);
            System.out.println("Gửi email thành công tới: " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Gửi email thất bại: " + e.getMessage());
        }
    }

    // Sinh mã OTP ngẫu nhiên 6 chữ số
    public static String sinhOTPRamDom() {
        Random rdOTP = new Random();
        int otp = 100000 + rdOTP.nextInt(900000);
        return String.valueOf(otp);
    }
}
