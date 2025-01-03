package quochung.server.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public String setTextWithCode(String name, String code) {
        return "<h1>Chào mừng bạn đến với hệ thống quản lý sự kiện</h1>"
                + "<p>Chúc mừng bạn đã đăng ký tài khoản thành công. Dưới đây là mã xác thực của bạn:</p>"
                + "<h2>" + code + "</h2>"
                + "<p>Để xác thực tài khoản, vui lòng nhập mã xác thực trên vào trang web.</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Quản trị viên</p>";
    }

    public String setTextWithReminder(String name, String eventTitle, String eventStartTime) {
        return "<h1>Chào " + name + "</h1>"
                + "<p>Đây là lời nhắc nhở về sự kiện " + eventTitle + " sắp diễn ra vào lúc " + eventStartTime + "</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Quản trị viên</p>";
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // "true" để chỉ định email là HTML

        mailSender.send(mimeMessage);
    }

    public void sendCodeEmail(String to, String code) throws MessagingException {
        sendHtmlEmail(to, "Mã xác thực tài khoản", setTextWithCode("", code));
    }

    public void sendReminderEmail(String to, String name, String eventTitle, String eventStartTime)
            throws MessagingException {
        sendHtmlEmail(to, "Nhắc nhở sự kiện", setTextWithReminder(name, eventTitle, eventStartTime));
    }

    public void sendPasswordEmail(String to, String password) throws MessagingException {
        sendHtmlEmail(to, "Mật khẩu mới", "<h1>Mật khẩu mới của bạn là: " + password + "</h1>");
    }
}
