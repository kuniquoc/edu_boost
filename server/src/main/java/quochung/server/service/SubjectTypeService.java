package quochung.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quochung.server.model.SubjectType;
import quochung.server.repository.SubjectTypeRepository;

@Service
public class SubjectTypeService {
    @Autowired
    private SubjectTypeRepository subjectTypeRepository;

    public List<SubjectType> getAllSubjectTypes() {
        return subjectTypeRepository.findAll();
    }
}
