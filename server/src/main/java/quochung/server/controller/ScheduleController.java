package quochung.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import quochung.server.payload.ScheduleDTO;
import quochung.server.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@Transactional
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping()
    public ResponseEntity<?> getSchedulesByUserId() {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByUserId();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createSchedule(@RequestBody String name) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(name);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId, @RequestBody String name) {
        try {
            scheduleService.updateSchedule(scheduleId, name);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}