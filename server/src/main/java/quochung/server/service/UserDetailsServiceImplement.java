package quochung.server.service;

import java.util.Set;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import quochung.server.model.Event;
import quochung.server.model.Role;
import quochung.server.model.Schedule;
import quochung.server.model.User;
import quochung.server.payload.User.PasswordUpdateDto;
import quochung.server.payload.User.UserDetailDto;
import quochung.server.payload.User.UserProfileDto;
import quochung.server.repository.EventRepository;
import quochung.server.repository.RoleRepository;
import quochung.server.repository.ScheduleRepository;
import quochung.server.repository.TodoItemRepository;
import quochung.server.repository.UserRepository;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với tên đăng nhập: " + username));

        return new UserDetailsImplement(user);
    }

    public Long getCurrentUserId() {
        return ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(
                        () -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + getCurrentUserId()));
    }

    public UserProfileDto getUserProfile() {
        User user = getCurrentUser();
        return new UserProfileDto(user.getFullName(), user.getBirthday(), user.getEmail(), user.getPhone(),
                user.getGender(), user.isEmailVerified());
    }

    public Collection<? extends GrantedAuthority> getRole() {
        return ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getAuthorities();
    }

    public void updateUserProfile(UserProfileDto userProfile) {
        User user = getCurrentUser();
        user.setFullName(userProfile.getFullName());
        user.setBirthday(userProfile.getBirthday());
        user.setEmail(userProfile.getEmail());
        user.setPhone(userProfile.getPhone());
        user.setGender(userProfile.getGender());
        userRepository.save(user);
    }

    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser() {
        User user = getCurrentUser();
        List<Schedule> schedules = user.getSchedules();
        for (Schedule schedule : schedules) {
            List<Event> events = schedule.getEvents();
            for (Event event : events) {
                todoItemRepository.deleteByEventId(event.getId());
            }
            eventRepository.deleteByScheduleId(schedule.getId());
        }
        scheduleRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
    }

    public List<UserDetailDto> getAllUsers() {
        List<UserDetailDto> userList = userRepository.findAll().stream()
                .map(user -> new UserDetailDto(user.getId(), user.getRoles(), user.getFullName(), user.getBirthday(),
                        user.getEmail(), user.getPhone(), user.getGender()))
                .toList();
        return userList;
    }

    public UserDetailDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        return new UserDetailDto(user.getId(), user.getRoles(), user.getFullName(), user.getBirthday(),
                user.getEmail(), user.getPhone(), user.getGender());
    }

    public void updatePassword(Long userId, PasswordUpdateDto passwordUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        if (!passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));

        List<Schedule> schedules = user.getSchedules();
        for (Schedule schedule : schedules) {
            List<Event> events = schedule.getEvents();
            for (Event event : events) {
                todoItemRepository.deleteByEventId(event.getId());
            }
            eventRepository.deleteByScheduleId(schedule.getId());
        }
        scheduleRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
    }

    public void updateRole(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        user.setRoles(roles);
        userRepository.save(user);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
