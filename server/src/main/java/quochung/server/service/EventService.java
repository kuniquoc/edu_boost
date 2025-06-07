package quochung.server.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.Event;
import quochung.server.model.EventStudyMethod;
import quochung.server.model.Reminder;
import quochung.server.model.Schedule;
import quochung.server.model.TodoItem;
import quochung.server.payload.event.CreateEventDTO;
import quochung.server.payload.event.EventDTO;
import quochung.server.payload.event.EventDetailDTO;
import quochung.server.payload.event.ReminderDTO;
import quochung.server.payload.event.StudyMethodDTO;
import quochung.server.payload.event.UpdateEventDetailDTO;
import quochung.server.payload.event.TodoItemDTO;
import quochung.server.repository.EventRepository;
import quochung.server.repository.EventStudyMethodRepository;
import quochung.server.repository.ReminderRepository;
import quochung.server.repository.ScheduleRepository;
import quochung.server.repository.StudyMethodRepository;
import quochung.server.repository.TodoItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final TodoItemRepository todoItemRepository;

    private final ReminderRepository reminderRepository;

    private final EventStudyMethodRepository eventStudyMethodRepository;

    private final StudyMethodRepository studyMethodRepository;

    private final ScheduleRepository scheduleRepository;

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
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(BadRequestException::new);
        if (eventRepository.existsByOverlappingTime(scheduleId, createEventDTO.getDate(), createEventDTO.getStartTime(),
                createEventDTO.getEndTime())) {
            throw new BadRequestException("Thời gian sự kiện trùng với sự kiện khác");
        }

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

    public EventDetailDTO getEventDetail(Long eventId) throws BadRequestException {
        Event event = eventRepository.findById(eventId).orElseThrow(BadRequestException::new);
        EventDetailDTO eventDetailDTO = new EventDetailDTO();
        eventDetailDTO.setId(event.getId());
        eventDetailDTO.setTitle(event.getTitle());
        eventDetailDTO.setDate(event.getDate());
        eventDetailDTO.setStartTime(event.getStartTime());
        eventDetailDTO.setEndTime(event.getEndTime());
        eventDetailDTO.setDescription(event.getDescription());
        eventDetailDTO.setTodoItems(event.getTodoItems().stream().map(todoItem -> {
            TodoItemDTO todoItemDTO = new TodoItemDTO();
            todoItemDTO.setId(todoItem.getId());
            todoItemDTO.setDescription(todoItem.getDescription());
            todoItemDTO.setCompleted(todoItem.isCompleted());
            return todoItemDTO;
        }).collect(Collectors.toList()));

        eventDetailDTO.setReminders(event.getReminders().stream().map(reminder -> {
            ReminderDTO reminderDTO = new ReminderDTO();
            reminderDTO.setId(reminder.getId());
            reminderDTO.setScheduledTime(reminder.getScheduledTime());
            return reminderDTO;
        }).collect(Collectors.toList()));

        eventDetailDTO.setStudyMethods(eventStudyMethodRepository.findByEventId(eventId).stream()
                .map(eventStudyMethod -> {
                    StudyMethodDTO studyMethodDTO = new StudyMethodDTO();
                    studyMethodDTO.setId(eventStudyMethod.getStudyMethod().getId());
                    studyMethodDTO.setName(eventStudyMethod.getStudyMethod().getName());
                    return studyMethodDTO;
                }).collect(Collectors.toList()));
        return eventDetailDTO;
    }

    public void updateEvent(UpdateEventDetailDTO updateEvent) throws BadRequestException {
        Event event = eventRepository.findById(updateEvent.getId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy sự kiện"));
        event.setTitle(updateEvent.getTitle());
        event.setDate(updateEvent.getDate());
        event.setStartTime(updateEvent.getStartTime());
        event.setEndTime(updateEvent.getEndTime());
        event.setDescription(updateEvent.getDescription());

        event.getTodoItems().clear();
        List<TodoItemDTO> todoItems = updateEvent.getTodoItems();
        for (TodoItemDTO todoItemDTO : todoItems) {
            TodoItem todoItem;
            if (todoItemDTO.getId() == 0) {
                todoItem = new TodoItem();
                todoItem.setDescription(todoItemDTO.getDescription());
                todoItem.setCompleted(todoItemDTO.isCompleted());
                todoItem.setEvent(event);
                event.getTodoItems().add(todoItem);
            } else {
                todoItem = todoItemRepository.findById(todoItemDTO.getId())
                        .orElseThrow(() -> new BadRequestException("Không tìm thấy todoItem"));
                todoItem.setDescription(todoItemDTO.getDescription());
                todoItem.setCompleted(todoItemDTO.isCompleted());
                todoItem.setEvent(event);
                event.getTodoItems().add(todoItem);
            }
            todoItemRepository.save(todoItem);
        }

        event.getReminders().clear();
        List<ReminderDTO> reminders = updateEvent.getReminders();
        for (ReminderDTO reminderDTO : reminders) {
            Reminder reminder;
            if (reminderDTO.getId() == 0) {
                reminder = new Reminder();
                reminder.setScheduledTime(reminderDTO.getScheduledTime());
                reminder.setEvent(event);
                event.getReminders().add(reminder);
            } else {
                reminder = reminderRepository.findById(reminderDTO.getId())
                        .orElseThrow(() -> new BadRequestException("Không tìm thấy reminder"));
                reminder.setScheduledTime(reminderDTO.getScheduledTime());
                if (reminderDTO.getScheduledTime().isAfter(event.getDate().atTime(event.getStartTime()))) {
                    reminder.setSent(false);
                }
                event.getReminders().add(reminder);
            }
            reminderRepository.save(reminder);
        }


        List<Long> studyMethodIds = updateEvent.getStudyMethodIds();
        List<EventStudyMethod> eventStudyMethods = eventStudyMethodRepository.findByEventId(event.getId());
        // Xóa các studyMethods không còn trong danh sách studyMethodIds
        for (EventStudyMethod eventStudyMethod : eventStudyMethods) {
            if (!studyMethodIds.contains(eventStudyMethod.getStudyMethod().getId())) {
                eventStudyMethodRepository.delete(eventStudyMethod);
            }
        }

        // Thêm các studyMethods mới từ repository nếu chúng chưa có
        List<Long> existingStudyMethodIds = eventStudyMethods.stream()
                .map(eventStudyMethod -> eventStudyMethod.getStudyMethod().getId()).toList();

        for (Long studyMethodId : studyMethodIds) {
            if (!existingStudyMethodIds.contains(studyMethodId)) {
                EventStudyMethod eventStudyMethod = new EventStudyMethod();
                eventStudyMethod.setEvent(event);
                eventStudyMethod.setStudyMethod(studyMethodRepository.findById(studyMethodId).orElseThrow(() -> new BadRequestException("Không tìm thấy studyMethod")));
                eventStudyMethodRepository.save(eventStudyMethod);
            }
        }
        eventRepository.save(event);
    }

    public void deleteEvent(Long eventId){
        todoItemRepository.deleteByEventId(eventId);
        reminderRepository.deleteByEventId(eventId);
        eventStudyMethodRepository.deleteByEventId(eventId);
        eventRepository.deleteById(eventId);
    }
}
