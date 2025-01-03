package quochung.server.service;

import jakarta.mail.MessagingException;
import quochung.server.model.Reminder;
import quochung.server.repository.ReminderRepository;
import quochung.server.util.EmailUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {
    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ReminderRepository reminderRepository;

    @Async
    @Scheduled(cron = "0 * * * * *")
    public void checkRemindersAndSendEmails() throws MessagingException {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Reminder> reminders = reminderRepository.findByScheduledTimeBefore(currentTime);

        for (Reminder reminder : reminders) {
            String email = reminder.getEvent().getSchedule().getUser().getEmail();
            String name = reminder.getEvent().getSchedule().getUser().getFullName();
            String eventTitle = reminder.getEvent().getTitle();
            String eventStartTime = reminder.getEvent().getStartTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
            emailUtil.sendHtmlEmail(email, "Nhắc nhở sự kiện", emailUtil
                    .setTextWithReminder(name, eventTitle, eventStartTime));
        }
    }
}
