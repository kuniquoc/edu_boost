package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import quochung.server.payload.*;
import quochung.server.service.StudyMethodService;
// import quochung.server.util.HtmlSanitizer;

@RestController
@RequestMapping("/api/study-methods")
@Transactional
public class StudyMethodController {
    @Autowired
    private StudyMethodService studyMethodService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createStudyMethod(@RequestBody CreateStudyMethodDto createStudyMethodDto) {
        try {
            String message = "Phương pháp học được tạo thành công";
            Object data = studyMethodService.createStudyMethod(createStudyMethodDto);
            return ResponseEntity.status(201).body(new MessageDto(message, data));
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStudyMethods(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") long typeId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "false") boolean favorite) {
        try {
            String message = "Lấy danh sách phương pháp học thành công";
            Object data = studyMethodService.getAllStudyMethods(page, size, typeId, search, favorite, true);
            return ResponseEntity.ok(new MessageDto(message, data));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllStudyMethodsPublic(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") long typeId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "false") boolean favorite) {
        try {
            String message = "Lấy danh sách phương pháp học thành công";
            Object data = studyMethodService.getAllStudyMethods(page, size, typeId, search, favorite, false);
            return ResponseEntity.ok(new MessageDto(message, data));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudyMethodById(@PathVariable Long id) {
        try {
            String message = "Lấy thông tin phương pháp học thành công";
            Object data = studyMethodService.getStudyMethodById(id, true);
            return ResponseEntity.ok(new MessageDto(message, data));
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getStudyMethodByIdPublic(@PathVariable Long id) {
        try {
            String message = "Lấy thông tin phương pháp học thành công";
            Object data = studyMethodService.getStudyMethodById(id, false);
            return ResponseEntity.ok(new MessageDto(message, data));
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudyMethod(@RequestBody StudyMethodDetailDto studyMethodDetailDto) {
        try {
            // studyMethodDetailDto.setDetail(HtmlSanitizer.escapeHtml(studyMethodDetailDto.getDetail()));

            String message = "Cập nhật phương pháp học thành công";
            Object data = studyMethodService.updateStudyMethod(studyMethodDetailDto);
            return ResponseEntity.ok(new MessageDto(message, data));
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudyMethod(@PathVariable Long id) {
        try {
            studyMethodService.deleteStudyMethod(id);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }
}
