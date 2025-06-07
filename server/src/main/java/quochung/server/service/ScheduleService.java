package quochung.server.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import quochung.server.model.Event;
import quochung.server.model.Schedule;
import quochung.server.payload.ScheduleDTO;
import quochung.server.repository.EventRepository;
import quochung.server.repository.ScheduleRepository;
import quochung.server.repository.TodoItemRepository;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private UserDetailsServiceImplement userDetailsService;

    public ScheduleDTO createSchedule(String name) {
        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule.setUser(userDetailsService.getCurrentUser());
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
