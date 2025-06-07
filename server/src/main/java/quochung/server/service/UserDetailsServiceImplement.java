package quochung.server.service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.*;
import quochung.server.payload.user.PasswordUpdateDto;
import quochung.server.payload.user.RoleDTO;
import quochung.server.payload.user.UserDetailDto;
import quochung.server.payload.user.UserElementDTO;
import quochung.server.payload.user.UserList;
import quochung.server.payload.user.UserProfileDto;
import quochung.server.repository.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImplement implements UserDetailsService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final EventRepository eventRepository;
    private final TodoItemRepository todoItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReminderRepository reminderRepository;
    private final EventStudyMethodRepository eventStudyMethodRepository;
    private final StudyMethodRepository studyMethodRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với tên đăng nhập: " + username));
        return new UserDetailsImplement(user);
    }

    public UserProfileDto getUserProfile() {
        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        return new UserProfileDto(user.getFullName(), user.getBirthday(), user.getEmail(), user.getPhone(),
                user.getGender(), user.isEmailVerified());
    }

    public Collection<? extends GrantedAuthority> getRole() {
        return ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getAuthorities();
    }

    public void updateUserProfile(UserProfileDto userProfile) {
        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        user.setFullName(userProfile.getFullName());
        user.setBirthday(userProfile.getBirthday());
        user.setEmail(userProfile.getEmail());
        user.setPhone(userProfile.getPhone());
        user.setGender(userProfile.getGender());
        userRepository.save(user);
    }

    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        if (!passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser() {
        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getId();
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

    public UserList getAllUsers(int page, int size, long roleId, String search) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        Page<User> pageResult = null;

        if (roleId != 0 && !search.isEmpty()) {
            pageResult = userRepository.findByUserRoles_Role_IdAndFullNameContainingIgnoreCase(roleId, search,
                    pageable);
        } else if (roleId != 0) {
            pageResult = userRepository.findByUserRoles_Role_Id(roleId, pageable);
        } else if (!search.isEmpty()) {
            pageResult = userRepository.findByFullNameContainingIgnoreCase(search, pageable);
        } else {
            pageResult = userRepository.findAll(pageable);
        }

        UserList userList = new UserList();
        userList.setUserElementDTOs(pageResult.getContent().stream().map(user -> {
            UserElementDTO userElementDTO = new UserElementDTO();
            userElementDTO.setId(user.getId());
            userElementDTO.setRoles(user.getUserRoles().stream().map(userRole -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(userRole.getRole().getId());
                roleDTO.setRoleName(userRole.getRole().getRoleName());
                return roleDTO;
            }).collect(Collectors.toSet()));
            userElementDTO.setFullName(user.getFullName());
            userElementDTO.setBirthday(user.getBirthday());
            userElementDTO.setEmail(user.getEmail());
            userElementDTO.setPhone(user.getPhone());
            userElementDTO.setGender(user.getGender());
            return userElementDTO;
        }).collect(Collectors.toList()));
        userList.setTotalPages(pageResult.getTotalPages());
        userList.setCurrentPage(pageResult.getNumber() + 1);
        return userList;
    }

    public UserDetailDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        return new UserDetailDto(user.getId(),
                user.getUserRoles().stream().map(userRole -> {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setId(userRole.getRole().getId());
                    roleDTO.setRoleName(userRole.getRole().getRoleName());
                    return roleDTO;
                }).collect(Collectors.toSet()),
                user.getFullName(), user.getBirthday(),
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
                reminderRepository.deleteByEventId(event.getId());
                eventStudyMethodRepository.deleteByEventId(event.getId());
            }
            eventRepository.deleteByScheduleId(schedule.getId());
        }
        scheduleRepository.deleteByUserId(user.getId());
        List<StudyMethod> studyMethods = studyMethodRepository.findByUserId(user.getId());
        for (StudyMethod studyMethod : studyMethods) {
            eventStudyMethodRepository.deleteByStudyMethodId(studyMethod.getId());
            favoriteRepository.deleteByStudyMethodId(studyMethod.getId());
        }
        studyMethodRepository.deleteByUserId(user.getId());
        favoriteRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    public void updateRole(Long userId, Set<RoleDTO> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));

        // Xóa tất cả vai trò cũ của người dùng trước khi cập nhật
        userRoleRepository.deleteByUserId(userId);

        if (roles.isEmpty()) {
            // Nếu không có vai trò nào được gửi, chỉ cần lưu user sau khi xóa quyền cũ
            user.setUserRoles(new ArrayList<>());
            userRepository.save(user);
            return;
        }

        List<UserRole> userRoles = roles.stream().map(role -> {
            Role newRole = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền với id: " + role.getId()));
            UserRole userRole = new UserRole(); // Tạo UserRole mới
            userRole.setUser(user); // Gán user cho UserRole
            userRole.setRole(newRole); // Gán role cho UserRole
            return userRole;
        }).toList();

        // Gán danh sách vai trò mới cho user
        user.getUserRoles().clear();
        user.getUserRoles().addAll(userRoles);

        // Lưu lại user, Hibernate sẽ tự động lưu các UserRole thông qua cascade
        userRepository.save(user);
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setRoleName(role.getRoleName());
            return roleDTO;
        }).toList();
    }

    public Set<Role> getUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + userId));
        return user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toSet());
    }
}
