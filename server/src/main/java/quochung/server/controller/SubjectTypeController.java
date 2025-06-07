package quochung.server.controller;

import org.springframework.web.bind.annotation.RestController;

import quochung.server.service.SubjectTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/subject-types")
@Transactional
public class SubjectTypeController {
    @Autowired
    private SubjectTypeService subjectTypeService;

    @GetMapping
    public ResponseEntity<?> getMethodName() {
        var subjectTypes = subjectTypeService.getAllSubjectTypes();
        return ResponseEntity.ok(subjectTypes);
    }
}
