package quochung.server.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import quochung.server.service.SubjectTypeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/subject-types")
@RequiredArgsConstructor
public class SubjectTypeController {
    private final SubjectTypeService subjectTypeService;

    @GetMapping
    public ResponseEntity<Object> getMethodName() {
        var subjectTypes = subjectTypeService.getAllSubjectTypes();
        return ResponseEntity.ok(subjectTypes);
    }
}
