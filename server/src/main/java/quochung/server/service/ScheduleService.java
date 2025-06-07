package quochung.server.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.Event;
import quochung.server.model.Schedule;
import quochung.server.model.User;
import quochung.server.payload.schedule.ScheduleDTO;
import quochung.server.repository.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    private final EventRepository eventRepository;

    private final TodoItemRepository todoItemRepository;

    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final EventStudyMethodRepository eventStudyMethodRepository;

    public ScheduleDTO createSchedule(String name) {
        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule.setUser(user);
        scheduleRepository.save(schedule);
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setName(schedule.getName());
        scheduleDTO.setStartDate(schedule.getStartDate());
        scheduleDTO.setEndDate(schedule.getEndDate());
        return scheduleDTO;
    }

    public void updateSchedule(Long scheduleId, String name) throws BadRequestException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new BadRequestException());
        schedule.setName(name);
        scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long scheduleId) throws BadRequestException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new BadRequestException());
        List<Event> events = schedule.getEvents();
        for (Event event : events) {
            todoItemRepository.deleteByEventId(event.getId());
            reminderRepository.deleteByEventId(event.getId());
            eventStudyMethodRepository.deleteByEventId(event.getId());
        }
        eventRepository.deleteByScheduleId(scheduleId);
        scheduleRepository.delete(schedule);
    }

    public List<ScheduleDTO> getSchedulesByUserId() {
        long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
        return scheduleRepository.findByUserId(userId).stream().map(schedule -> {
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            scheduleDTO.setId(schedule.getId());
            scheduleDTO.setName(schedule.getName());
            scheduleDTO.setStartDate(schedule.getStartDate());
            scheduleDTO.setEndDate(schedule.getEndDate());
            return scheduleDTO;
        }).toList();
    }
}
