package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.event.EventDTO;
import quochung.server.payload.schedule.ScheduleDTO;
import quochung.server.service.EventService;
import quochung.server.service.ScheduleService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final EventService eventService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<Object> getSchedulesByUserId() {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByUserId();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{scheduleId}")
    public ResponseEntity<Object> getEventsByScheduleId(
            @PathVariable Long scheduleId,
            @RequestParam(name = "startDate") String startDateString // Lấy startDate dưới dạng String
    ) {
        LocalDate startDate = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE); // Chuyển đổi String
                                                                                                  // thành LocalDate
        List<EventDTO> events = eventService.getEventsByScheduleIdAndStartDate(scheduleId, startDate);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<Object> createSchedule(@RequestBody String name) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(name);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Object> updateSchedule(@PathVariable Long scheduleId, @RequestBody String name)
            throws BadRequestException {
        scheduleService.updateSchedule(scheduleId, name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Object> deleteSchedule(@PathVariable Long scheduleId) throws BadRequestException {
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}