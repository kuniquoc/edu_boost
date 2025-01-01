package quochung.server.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import quochung.server.payload.CreateEventDTO;
import quochung.server.payload.EventDTO;
import quochung.server.service.EventService;

@RestController
@RequestMapping("/api/events")
@Transactional
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getEventsByScheduleId(
            @PathVariable Long scheduleId,
            @RequestParam(name = "startDate") String startDateString // Lấy startDate dưới dạng String
    ) {
        LocalDate startDate = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE); // Chuyển đổi String
                                                                                                  // thành LocalDate
        List<EventDTO> events = eventService.getEventsByScheduleIdAndStartDate(scheduleId, startDate);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/{scheduleId}")
    public ResponseEntity<?> createEvent(@PathVariable Long scheduleId, @RequestBody CreateEventDTO eventDTO) {
        try {
            EventDTO createdEvent = eventService.createEvent(scheduleId, eventDTO);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
