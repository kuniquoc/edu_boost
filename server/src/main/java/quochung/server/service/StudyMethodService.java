package quochung.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.StudyMethod;
import quochung.server.model.SubjectType;
import quochung.server.model.User;
import quochung.server.payload.studyMethod.CreateStudyMethodDTO;
import quochung.server.payload.studyMethod.StudyMethodDetailDTO;
import quochung.server.payload.studyMethod.StudyMethodElementDTO;
import quochung.server.payload.studyMethod.StudyMethodList;
import quochung.server.repository.SubjectTypeRepository;
import quochung.server.repository.UserRepository;
import quochung.server.repository.EventStudyMethodRepository;
import quochung.server.repository.FavoriteRepository;
import quochung.server.repository.StudyMethodRepository;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyMethodService {
        private final StudyMethodRepository studyMethodRepository;

        private final SubjectTypeRepository subjectTypeRepository;

        private final FavoriteRepository favoriteRepository;

        private final EventStudyMethodRepository eventStudyMethodRepository;

        private final UserRepository userRepository;

        public StudyMethodDetailDTO createStudyMethod(CreateStudyMethodDTO createStudyMethodRequest)
                        throws BadRequestException {

                Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                                .getId();
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Không tìm thấy người dùng với id: " + userId));

                StudyMethod studyMethod = new StudyMethod();
                studyMethod.setName(createStudyMethodRequest.getName());
                studyMethod.setDescription(createStudyMethodRequest.getDescription());
                studyMethod.setThumbnail(createStudyMethodRequest.getThumbnail());
                studyMethod.setType(subjectTypeRepository.findById(createStudyMethodRequest.getTypeId())
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")));
                studyMethod.setDetail(createStudyMethodRequest.getDetail());
                studyMethod.setUser(user);
                studyMethodRepository.save(studyMethod);
                StudyMethodDetailDTO studyMethodDetailDTO = new StudyMethodDetailDTO();
                studyMethodDetailDTO.setId(studyMethod.getId());
                studyMethodDetailDTO.setName(studyMethod.getName());
                studyMethodDetailDTO.setDescription(studyMethod.getDescription());
                studyMethodDetailDTO.setThumbnail(studyMethod.getThumbnail());
                studyMethodDetailDTO.setTypeId(studyMethod.getType().getId());
                studyMethodDetailDTO.setDetail(studyMethod.getDetail());
                return studyMethodDetailDTO;
        }

        public StudyMethodList getAllStudyMethods(int page, int size, long typeId, String search, boolean isFavorite,
                        boolean isLogin) throws BadRequestException {
                PageRequest pageRequest = PageRequest.of(page - 1, size);
                Page<StudyMethod> pageResult;

                if (!isLogin && isFavorite) {
                        return new StudyMethodList();
                }

                if (isLogin && isFavorite) {
                        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                        .getPrincipal())
                                        .getId();
                        List<Long> studyMethodIds = favoriteRepository.findByUserId(userId)
                                        .stream().map(favorite -> favorite.getStudyMethod().getId()).toList();
                        if (typeId != 0 && !search.isEmpty()) {
                                SubjectType type = subjectTypeRepository.findById(typeId)
                                                .orElseThrow(() -> new BadRequestException(
                                                                "Không tìm thấy loại môn học"));
                                pageResult = studyMethodRepository.findByNameContainingAndTypeAndIdInIgnoreCase(search,
                                                type,
                                                studyMethodIds,
                                                pageRequest);
                        } else if (typeId != 0) {
                                SubjectType type = subjectTypeRepository.findById(typeId)
                                                .orElseThrow(() -> new BadRequestException(
                                                                "Không tìm thấy loại môn học"));
                                pageResult = studyMethodRepository.findByTypeAndIdIn(type, studyMethodIds, pageRequest);
                        } else if (!search.isEmpty()) {
                                pageResult = studyMethodRepository.findByNameContainingAndIdInIgnoreCase(search,
                                                studyMethodIds,
                                                pageRequest);
                        } else {
                                pageResult = studyMethodRepository.findByIdIn(studyMethodIds, pageRequest);
                        }
                } else if (typeId != 0 && !search.isEmpty()) {
                        pageResult = studyMethodRepository.findByNameContainingAndTypeIgnoreCase(search,
                                        subjectTypeRepository.findById(typeId)
                                                        .orElseThrow(() -> new BadRequestException(
                                                                        "Không tìm thấy loại môn học")),
                                        pageRequest);
                } else if (typeId != 0) {
                        pageResult = studyMethodRepository.findByType(subjectTypeRepository.findById(typeId)
                                        .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")),
                                        pageRequest);
                } else if (!search.isEmpty()) {
                        pageResult = studyMethodRepository.findByNameContainingIgnoreCase(search, pageRequest);
                } else {
                        pageResult = studyMethodRepository.findAll(pageRequest);
                }

                StudyMethodList studyMethodList = new StudyMethodList();
                studyMethodList.setStudyMethodElementDTOs(pageResult.getContent().stream().map(
                                studyMethod -> {
                                        StudyMethodElementDTO studyMethodElementDTO = new StudyMethodElementDTO();
                                        studyMethodElementDTO.setId(studyMethod.getId());
                                        studyMethodElementDTO.setName(studyMethod.getName());
                                        studyMethodElementDTO.setDescription(studyMethod.getDescription());
                                        studyMethodElementDTO.setThumbnail(studyMethod.getThumbnail());
                                        studyMethodElementDTO.setTypeId(studyMethod.getType().getId());
                                        if (isLogin) {
                                                Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext()
                                                                .getAuthentication()
                                                                .getPrincipal())
                                                                .getId();
                                                boolean favorite = favoriteRepository.existsByUserIdAndStudyMethodId(
                                                                userId,
                                                                studyMethod.getId());
                                                studyMethodElementDTO
                                                                .setFavorite(favorite);
                                        } else {
                                                studyMethodElementDTO.setFavorite(false);
                                        }
                                        return studyMethodElementDTO;
                                }).toList());
                studyMethodList.setTotalPages(pageResult.getTotalPages());
                studyMethodList.setCurrentPage(page);
                return studyMethodList;
        }

        public StudyMethodList getAllStudyMethodsOfMod(int page, int size, long typeId, String search)
                        throws BadRequestException {
                PageRequest pageRequest = PageRequest.of(page - 1, size);
                Page<StudyMethod> pageResult;

                if (typeId != 0 && !search.isEmpty()) {
                        pageResult = studyMethodRepository.findByNameContainingAndTypeIgnoreCase(search,
                                        subjectTypeRepository.findById(typeId)
                                                        .orElseThrow(() -> new BadRequestException(
                                                                        "Không tìm thấy loại môn học")),
                                        pageRequest);
                } else if (typeId != 0) {
                        pageResult = studyMethodRepository.findByType(subjectTypeRepository.findById(typeId)
                                        .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")),
                                        pageRequest);
                } else if (!search.isEmpty()) {
                        pageResult = studyMethodRepository.findByNameContainingIgnoreCase(search, pageRequest);
                } else {
                        pageResult = studyMethodRepository.findAll(pageRequest);
                }

                Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                                .getId();

                StudyMethodList studyMethodList = new StudyMethodList();
                studyMethodList.setStudyMethodElementDTOs(pageResult.getContent().stream()
                                .filter(studyMethod -> studyMethod.getUser() != null
                                                && studyMethod.getUser().getId() == userId)
                                .map(studyMethod -> {
                                        StudyMethodElementDTO dto = new StudyMethodElementDTO();
                                        dto.setId(studyMethod.getId());
                                        dto.setName(studyMethod.getName());
                                        dto.setDescription(studyMethod.getDescription());
                                        dto.setThumbnail(studyMethod.getThumbnail());
                                        dto.setTypeId(studyMethod.getType().getId());
                                        dto.setFavorite(false);
                                        return dto;
                                }).toList());
                studyMethodList.setTotalPages(pageResult.getTotalPages());
                studyMethodList.setCurrentPage(page);
                return studyMethodList;
        }

        public StudyMethodDetailDTO getStudyMethodById(Long id, boolean isLogin) throws BadRequestException {
                StudyMethod studyMethod = studyMethodRepository.findById(id)
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
                StudyMethodDetailDTO studyMethodDetailDTO = new StudyMethodDetailDTO();
                studyMethodDetailDTO.setId(studyMethod.getId());
                studyMethodDetailDTO.setName(studyMethod.getName());
                studyMethodDetailDTO.setDescription(studyMethod.getDescription());
                studyMethodDetailDTO.setThumbnail(studyMethod.getThumbnail());
                studyMethodDetailDTO.setTypeId(studyMethod.getType().getId());
                studyMethodDetailDTO.setDetail(studyMethod.getDetail());
                if (isLogin) {
                        Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                        .getPrincipal())
                                        .getId();
                        studyMethodDetailDTO
                                        .setFavorite(favoriteRepository.existsByUserIdAndStudyMethodId(userId,
                                                        studyMethod.getId()));
                } else {
                        studyMethodDetailDTO.setFavorite(false);
                }
                return studyMethodDetailDTO;
        }

        public StudyMethodDetailDTO updateStudyMethod(StudyMethodDetailDTO updateStudyMethodRequest)
                        throws BadRequestException {
                StudyMethod studyMethod = studyMethodRepository.findById(updateStudyMethodRequest.getId())
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
                studyMethod.setName(updateStudyMethodRequest.getName());
                studyMethod.setDescription(updateStudyMethodRequest.getDescription());
                studyMethod.setThumbnail(updateStudyMethodRequest.getThumbnail());
                studyMethod.setType(subjectTypeRepository.findById(updateStudyMethodRequest.getTypeId()).orElseThrow(
                                () -> new BadRequestException("Không tìm thấy loại môn học")));
                studyMethod.setDetail(updateStudyMethodRequest.getDetail());
                studyMethodRepository.save(studyMethod);
                StudyMethodDetailDTO studyMethodDetailDTO = new StudyMethodDetailDTO();
                studyMethodDetailDTO.setId(studyMethod.getId());
                studyMethodDetailDTO.setName(studyMethod.getName());
                studyMethodDetailDTO.setDescription(studyMethod.getDescription());
                studyMethodDetailDTO.setThumbnail(studyMethod.getThumbnail());
                studyMethodDetailDTO.setTypeId(studyMethod.getType().getId());
                studyMethodDetailDTO.setDetail(studyMethod.getDetail());
                return studyMethodDetailDTO;
        }

        public void deleteStudyMethod(Long id) throws BadRequestException {
                StudyMethod studyMethod = studyMethodRepository.findById(id)
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
                eventStudyMethodRepository.deleteByStudyMethodId(id);
                favoriteRepository.deleteByStudyMethodId(id);
                studyMethodRepository.delete(studyMethod);
        }
}
