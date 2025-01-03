package quochung.server.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import quochung.server.payload.event.CreateEventDTO;
import quochung.server.payload.event.EventDTO;
import quochung.server.payload.event.EventDetailDTO;
import quochung.server.payload.event.UpdateEventDetailDTO;
import quochung.server.service.EventService;
import org.springframework.web.bind.annotation.PutMapping;

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

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventDetailDTO eventDetailDTO) {
        try {
            eventService.updateEvent(eventDetailDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventDetail(@PathVariable Long eventId) {
        try {
            EventDetailDTO eventDetail = eventService.getEventDetail(eventId);
            return new ResponseEntity<>(eventDetail, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
