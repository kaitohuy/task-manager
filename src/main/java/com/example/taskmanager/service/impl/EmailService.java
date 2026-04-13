package com.example.taskmanager.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác nhận tài khoản - Task Manager";
        String verificationUrl = frontendUrl + "/auth/verify-email?token=" + token;
        String content = "<p>Chào bạn,</p>"
                + "<p>Vui lòng click vào link bên dưới để xác nhận tài khoản của bạn:</p>"
                + "<a href=\"" + verificationUrl + "\">Xác nhận tài khoản</a>"
                + "<p>Link này sẽ hết hạn trong 24 giờ.</p>";

        sendEmail(to, subject, content);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Đặt lại mật khẩu - Task Manager";
        String resetUrl = frontendUrl + "/auth/reset-password?token=" + token;
        String content = "<p>Chào bạn,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link bên dưới để thực hiện:</p>"
                + "<a href=\"" + resetUrl + "\">Đặt lại mật khẩu</a>"
                + "<p>Link này sẽ hết hạn trong 1 giờ.</p>"
                + "<p>Nếu bạn không yêu cầu việc này, hãy bỏ qua email này.</p>";

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi mail: " + e.getMessage());
        }
    }
}
