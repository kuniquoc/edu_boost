package quochung.server.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quochung.server.model.Event;
import quochung.server.model.Schedule;
import quochung.server.payload.CreateEventDTO;
import quochung.server.payload.EventDTO;
import quochung.server.repository.EventRepository;
import quochung.server.repository.ScheduleRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    public List<EventDTO> getEventsByScheduleIdAndStartDate(Long scheduleId, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return eventRepository.findByScheduleIdAndStartDateBetween(scheduleId, startDate, endDate).stream()
                .map(event -> {
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setId(event.getId());
                    eventDTO.setTitle(event.getTitle());
                    eventDTO.setDate(event.getDate());
                    eventDTO.setStartTime(event.getStartTime());
                    eventDTO.setEndTime(event.getEndTime());
                    return eventDTO;
                }).collect(Collectors.toList());
    }

    public EventDTO createEvent(Long scheduleId, CreateEventDTO createEventDTO) throws BadRequestException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new BadRequestException());
        Event event = new Event();
        event.setTitle(createEventDTO.getTitle());
        event.setDate(createEventDTO.getDate());
        event.setStartTime(createEventDTO.getStartTime());
        event.setEndTime(createEventDTO.getEndTime());
        event.setDescription("");
        event.setSchedule(schedule);
        eventRepository.save(event);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDate(event.getDate());
        eventDTO.setStartTime(event.getStartTime());
        eventDTO.setEndTime(event.getEndTime());
        return eventDTO;
    }
}
