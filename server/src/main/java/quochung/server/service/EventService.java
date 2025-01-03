package quochung.server.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quochung.server.model.Event;
import quochung.server.model.EventStudyMethod;
import quochung.server.model.Reminder;
import quochung.server.model.Schedule;
import quochung.server.model.TodoItem;
import quochung.server.payload.event.CreateEventDTO;
import quochung.server.payload.event.EventDTO;
import quochung.server.payload.event.EventDetailDTO;
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
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private EventStudyMethodRepository eventStudyMethodRepository;

    @Autowired
    private StudyMethodRepository studyMethodRepository;

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

    public EventDetailDTO getEventDetail(Long eventId) throws BadRequestException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new BadRequestException());
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
        eventDetailDTO.setReminderIds(event.getReminders().stream().map(Reminder::getId).collect(Collectors.toList()));
        eventDetailDTO.setStudyMethods(eventStudyMethodRepository.findByEventId(eventId).stream()
                .map(eventStudyMethod -> {
                    StudyMethodDTO studyMethodDTO = new StudyMethodDTO();
                    studyMethodDTO.setId(eventStudyMethod.getStudyMethod().getId());
                    studyMethodDTO.setName(eventStudyMethod.getStudyMethod().getName());
                    return studyMethodDTO;
                }).collect(Collectors.toList()));
        return eventDetailDTO;
    }

    public boolean updateEvent(UpdateEventDetailDTO updateEvent) throws BadRequestException {
        Event event = eventRepository.findById(updateEvent.getId()).orElseThrow(() -> new BadRequestException());
        event.setTitle(updateEvent.getTitle());
        event.setDate(updateEvent.getDate());
        event.setStartTime(updateEvent.getStartTime());
        event.setEndTime(updateEvent.getEndTime());
        List<TodoItemDTO> todoItems = updateEvent.getTodoItems();
        List<TodoItem> todoItemsEntity = todoItems.stream().map(todoItemDTO -> {
            TodoItem todoItem = new TodoItem();
            todoItem.setId(todoItemDTO.getId());
            todoItem.setDescription(todoItemDTO.getDescription());
            todoItem.setCompleted(todoItemDTO.isCompleted());
            todoItem.setEvent(event);
            return todoItem;
        }).collect(Collectors.toList());
        event.setTodoItems(todoItemsEntity);

        List<Long> reminderIds = updateEvent.getReminderIds();
        // Xóa các reminders không còn trong danh sách reminderIds
        event.getReminders().removeIf(reminder -> !reminderIds.contains(reminder.getId()));

        // Thêm các reminders mới từ repository nếu chúng chưa có
        Set<Long> existingReminderIds = event.getReminders().stream()
                .map(Reminder::getId)
                .collect(Collectors.toSet());

        for (Long reminderId : reminderIds) {
            if (!existingReminderIds.contains(reminderId)) {
                reminderRepository.findById(reminderId).ifPresent(event.getReminders()::add);
            }
        }

        List<Long> studyMethodIds = updateEvent.getStudyMethodIds();
        List<EventStudyMethod> eventStudyMethods = eventStudyMethodRepository.findByEventId(event.getId());
        // Xóa các studyMethods không còn trong danh sách studyMethodIds
        eventStudyMethods
                .removeIf(eventStudyMethod -> !studyMethodIds.contains(eventStudyMethod.getStudyMethod().getId()));

        // Thêm các studyMethods mới từ repository nếu chúng chưa có
        Set<Long> existingStudyMethodIds = eventStudyMethods.stream()
                .map(eventStudyMethod -> eventStudyMethod.getStudyMethod().getId())
                .collect(Collectors.toSet());

        for (Long studyMethodId : studyMethodIds) {
            if (!existingStudyMethodIds.contains(studyMethodId)) {
                EventStudyMethod eventStudyMethod = new EventStudyMethod();
                eventStudyMethod.setEvent(event);
                eventStudyMethod.setStudyMethod(studyMethodRepository.findById(studyMethodId).orElse(null));
                eventStudyMethodRepository.save(eventStudyMethod);
            }
        }

        return eventRepository.save(event) != null;
    }

    public void deleteEvent(Long eventId) throws BadRequestException {
        todoItemRepository.deleteByEventId(eventId);
        reminderRepository.deleteByEventId(eventId);
        eventRepository.deleteById(eventId);
    }
}
