package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.event.CreateEventDTO;
import quochung.server.payload.event.EventDTO;
import quochung.server.payload.event.EventDetailDTO;
import quochung.server.payload.event.UpdateEventDetailDTO;
import quochung.server.service.EventService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
        private final EventService eventService;

        @PreAuthorize("hasRole('USER')")
        @PostMapping("/{scheduleId}")
        public ResponseEntity<Object> createEvent(@PathVariable Long scheduleId, @RequestBody CreateEventDTO eventDTO)
                        throws BadRequestException {
                EventDTO createdEvent = eventService.createEvent(scheduleId, eventDTO);
                return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        }

        @PreAuthorize("hasRole('USER')")
        @PutMapping("/{eventId}")
        public ResponseEntity<Object> updateEvent(@PathVariable Long eventId,
                        @RequestBody UpdateEventDetailDTO eventDetailDTO) throws BadRequestException {
                eventService.updateEvent(eventDetailDTO);
                return new ResponseEntity<>(HttpStatus.OK);
        }

        @PreAuthorize("hasRole('USER')")
        @GetMapping("/{eventId}")
        public ResponseEntity<Object> getEventDetail(@PathVariable Long eventId) throws BadRequestException {
                EventDetailDTO eventDetail = eventService.getEventDetail(eventId);
                return new ResponseEntity<>(eventDetail, HttpStatus.OK);
        }

        @PreAuthorize("hasRole('USER')")
        @DeleteMapping("/{eventId}")
        public ResponseEntity<Object> deleteEvent(@PathVariable Long eventId) throws BadRequestException {
                eventService.deleteEvent(eventId);
                return new ResponseEntity<>(HttpStatus.OK);
        }
}
