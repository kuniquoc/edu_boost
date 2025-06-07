package quochung.server.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.SubjectType;
import quochung.server.repository.SubjectTypeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectTypeService {
    private final SubjectTypeRepository subjectTypeRepository;

    public List<SubjectType> getAllSubjectTypes() {
        return subjectTypeRepository.findAll();
    }
}
