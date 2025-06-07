package quochung.server.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.Reminder;
import quochung.server.model.User;

import quochung.server.repository.ReminderRepository;
import quochung.server.util.EmailUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import quochung.server.repository.VerificationCodeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReminderService {
    private final EmailUtil emailUtil;

    private final ReminderRepository reminderRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    @Async
    @Scheduled(cron = "*/10 * * * * *")
    public void checkRemindersAndSendEmails() throws MessagingException {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Reminder> reminders = reminderRepository.findByScheduledTimeBefore(currentTime);

        for (Reminder reminder : reminders) {
            User user = reminder.getEvent().getSchedule().getUser();
            if (!user.isEmailVerified() || reminder.isSent()) {
                continue;
            }

            String email = reminder.getEvent().getSchedule().getUser().getEmail();
            String name = reminder.getEvent().getSchedule().getUser().getFullName();
            String eventTitle = reminder.getEvent().getTitle();
            String eventStartTime = reminder.getEvent().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String eventDate = reminder.getEvent().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            emailUtil.sendHtmlEmail(email, "Nhắc nhở sự kiện", emailUtil
                    .setTextWithReminder(name, eventTitle, eventStartTime, eventDate));
            reminder.setSent(true);
            reminderRepository.save(reminder);
        }
    }

    @Async
    @Scheduled(cron = "* * 0 * * *")
    public void deleteExpiredVerificationCodes() {
        LocalDateTime now = LocalDateTime.now();
        verificationCodeRepository.deleteByExpiresAtBefore(now);
    }
}
