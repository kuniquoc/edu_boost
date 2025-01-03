package quochung.server.service;

import org.springframework.stereotype.Service;

import quochung.server.model.StudyMethod;
import quochung.server.model.SubjectType;
import quochung.server.payload.*;
import quochung.server.repository.SubjectTypeRepository;
import quochung.server.repository.EventStudyMethodRepository;
import quochung.server.repository.FavoriteRepository;
import quochung.server.repository.StudyMethodRepository;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@Service
public class StudyMethodService {
    @Autowired
    private StudyMethodRepository studyMethodRepository;

    @Autowired
    private SubjectTypeRepository subjectTypeRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private EventStudyMethodRepository eventStudyMethodRepository;

    @Autowired
    private UserDetailsServiceImplement userDetailsServiceImplement;

    public StudyMethodDetailDto createStudyMethod(CreateStudyMethodDto createStudyMethodRequest)
            throws BadRequestException {
        StudyMethod studyMethod = new StudyMethod();
        studyMethod.setName(createStudyMethodRequest.getName());
        studyMethod.setDescription(createStudyMethodRequest.getDescription());
        studyMethod.setThumbnail(createStudyMethodRequest.getThumbnail());
        studyMethod.setType(subjectTypeRepository.findById(createStudyMethodRequest.getTypeId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")));
        studyMethod.setDetail(createStudyMethodRequest.getDetail());
        studyMethodRepository.save(studyMethod);
        StudyMethodDetailDto studyMethodDetailDto = new StudyMethodDetailDto();
        studyMethodDetailDto.setId(studyMethod.getId());
        studyMethodDetailDto.setName(studyMethod.getName());
        studyMethodDetailDto.setDescription(studyMethod.getDescription());
        studyMethodDetailDto.setThumbnail(studyMethod.getThumbnail());
        studyMethodDetailDto.setTypeId(studyMethod.getType().getId());
        studyMethodDetailDto.setDetail(studyMethod.getDetail());
        return studyMethodDetailDto;
    }

    public StudyMethodList getAllStudyMethods(int page, int size, long typeId, String search, boolean isFavorite,
            boolean isLogin) throws BadRequestException {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<StudyMethod> pageResult = null;

        if (!isLogin && isFavorite) {
            return new StudyMethodList();
        }

        if (isLogin && isFavorite) {
            List<Long> studyMethodIds = favoriteRepository.findByUserId(userDetailsServiceImplement.getCurrentUserId())
                    .stream().map(favorite -> favorite.getStudyMethod().getId()).toList();
            if (typeId != 0 && !search.isEmpty()) {
                SubjectType type = subjectTypeRepository.findById(typeId)
                        .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học"));
                pageResult = studyMethodRepository.findByNameContainingAndTypeAndIdInIgnoreCase(search, type,
                        studyMethodIds,
                        pageRequest);
            } else if (typeId != 0) {
                SubjectType type = subjectTypeRepository.findById(typeId)
                        .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học"));
                pageResult = studyMethodRepository.findByTypeAndIdIn(type, studyMethodIds, pageRequest);
            } else if (!search.isEmpty()) {
                pageResult = studyMethodRepository.findByNameContainingAndIdInIgnoreCase(search, studyMethodIds,
                        pageRequest);
            } else {
                pageResult = studyMethodRepository.findByIdIn(studyMethodIds, pageRequest);
            }
        } else if (typeId != 0 && !search.isEmpty()) {
            pageResult = studyMethodRepository.findByNameContainingAndTypeIgnoreCase(search,
                    subjectTypeRepository.findById(typeId)
                            .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")),
                    pageRequest);
        } else if (typeId != 0) {
            pageResult = studyMethodRepository.findByType(subjectTypeRepository.findById(typeId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy loại môn học")), pageRequest);
        } else if (!search.isEmpty()) {
            pageResult = studyMethodRepository.findByNameContainingIgnoreCase(search, pageRequest);
        } else {
            pageResult = studyMethodRepository.findAll(pageRequest);
        }

        StudyMethodList studyMethodList = new StudyMethodList();
        studyMethodList.setStudyMethodElementDtos(pageResult.getContent().stream().map(
                studyMethod -> {
                    StudyMethodElementDto studyMethodElementDto = new StudyMethodElementDto();
                    studyMethodElementDto.setId(studyMethod.getId());
                    studyMethodElementDto.setName(studyMethod.getName());
                    studyMethodElementDto.setDescription(studyMethod.getDescription());
                    studyMethodElementDto.setThumbnail(studyMethod.getThumbnail());
                    studyMethodElementDto.setTypeId(studyMethod.getType().getId());
                    if (isLogin) {
                        studyMethodElementDto
                                .setFavorite(favoriteRepository.existsByUserIdAndStudyMethodId(
                                        userDetailsServiceImplement.getCurrentUserId(), studyMethod.getId()));
                    }
                    studyMethodElementDto.setFavorite(false);
                    return studyMethodElementDto;
                }).toList());
        studyMethodList.setTotalPages(pageResult.getTotalPages());
        studyMethodList.setCurrentPage(page);
        return studyMethodList;
    }

    public StudyMethodDetailDto getStudyMethodById(Long id, boolean isLogin) throws BadRequestException {
        StudyMethod studyMethod = studyMethodRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
        StudyMethodDetailDto studyMethodDetailDto = new StudyMethodDetailDto();
        studyMethodDetailDto.setId(studyMethod.getId());
        studyMethodDetailDto.setName(studyMethod.getName());
        studyMethodDetailDto.setDescription(studyMethod.getDescription());
        studyMethodDetailDto.setThumbnail(studyMethod.getThumbnail());
        studyMethodDetailDto.setTypeId(studyMethod.getType().getId());
        studyMethodDetailDto.setDetail(studyMethod.getDetail());
        if (isLogin) {
            studyMethodDetailDto.setFavorite(favoriteRepository.existsByUserIdAndStudyMethodId(
                    userDetailsServiceImplement.getCurrentUserId(), studyMethod.getId()));
        } else {
            studyMethodDetailDto.setFavorite(false);
        }
        return studyMethodDetailDto;
    }

    public StudyMethodDetailDto updateStudyMethod(StudyMethodDetailDto updateStudyMethodRequest)
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
        StudyMethodDetailDto studyMethodDetailDto = new StudyMethodDetailDto();
        studyMethodDetailDto.setId(studyMethod.getId());
        studyMethodDetailDto.setName(studyMethod.getName());
        studyMethodDetailDto.setDescription(studyMethod.getDescription());
        studyMethodDetailDto.setThumbnail(studyMethod.getThumbnail());
        studyMethodDetailDto.setTypeId(studyMethod.getType().getId());
        studyMethodDetailDto.setDetail(studyMethod.getDetail());
        return studyMethodDetailDto;
    }

    public void deleteStudyMethod(Long id) throws BadRequestException {
        StudyMethod studyMethod = studyMethodRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
        eventStudyMethodRepository.deleteByStudyMethodId(id);
        favoriteRepository.deleteByStudyMethodId(id);
        studyMethodRepository.delete(studyMethod);

    }
}
