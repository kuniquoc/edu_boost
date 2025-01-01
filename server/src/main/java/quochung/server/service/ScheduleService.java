package quochung.server.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import quochung.server.model.Schedule;
import quochung.server.payload.ScheduleDTO;
import quochung.server.repository.ScheduleRepository;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

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

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
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
