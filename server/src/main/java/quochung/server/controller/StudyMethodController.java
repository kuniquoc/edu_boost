package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;

import quochung.server.payload.*;
import quochung.server.payload.studyMethod.CreateStudyMethodDTO;
import quochung.server.payload.studyMethod.StudyMethodDetailDTO;
import quochung.server.service.StudyMethodService;
// import quochung.server.util.HtmlSanitizer;

@RestController
@RequestMapping("/api/study-methods")
@RequiredArgsConstructor
public class StudyMethodController {
        private final StudyMethodService studyMethodService;

        @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
        @PostMapping
        public ResponseEntity<Object> createStudyMethod(@RequestBody CreateStudyMethodDTO createStudyMethodDto)
                        throws BadRequestException {
                String message = "Phương pháp học được tạo thành công";
                Object data = studyMethodService.createStudyMethod(createStudyMethodDto);
                return ResponseEntity.status(201).body(new MessageDto(message, data));
        }

        @PreAuthorize("hasRole('USER')")
        @GetMapping
        public ResponseEntity<Object> getAllStudyMethods(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "6") int size,
                        @RequestParam(defaultValue = "0") long typeId,
                        @RequestParam(defaultValue = "") String search,
                        @RequestParam(defaultValue = "false") boolean favorite) throws BadRequestException {
                String message = "Lấy danh sách phương pháp học thành công";
                Object data = studyMethodService.getAllStudyMethods(page, size, typeId, search, favorite, true);
                return ResponseEntity.ok(new MessageDto(message, data));
        }

        @GetMapping("/public")
        public ResponseEntity<Object> getAllStudyMethodsPublic(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size,
                        @RequestParam(defaultValue = "0") long typeId,
                        @RequestParam(defaultValue = "") String search,
                        @RequestParam(defaultValue = "false") boolean favorite) throws BadRequestException {
                String message = "Lấy danh sách phương pháp học thành công";
                Object data = studyMethodService.getAllStudyMethods(page, size, typeId, search, favorite, false);
                return ResponseEntity.ok(new MessageDto(message, data));
        }

        @PreAuthorize("hasRole('USER')")
        @GetMapping("/{id}")
        public ResponseEntity<Object> getStudyMethodById(@PathVariable Long id) throws BadRequestException {
                String message = "Lấy thông tin phương pháp học thành công";
                Object data = studyMethodService.getStudyMethodById(id, true);
                return ResponseEntity.ok(new MessageDto(message, data));
        }

        @GetMapping("/public/{id}")
        public ResponseEntity<Object> getStudyMethodByIdPublic(@PathVariable Long id) throws BadRequestException {
                String message = "Lấy thông tin phương pháp học thành công";
                Object data = studyMethodService.getStudyMethodById(id, false);
                return ResponseEntity.ok(new MessageDto(message, data));
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
        @PutMapping()
        public ResponseEntity<Object> updateStudyMethod(@RequestBody StudyMethodDetailDTO studyMethodDetailDto)
                        throws BadRequestException {
                // studyMethodDetailDto.setDetail(HtmlSanitizer.escapeHtml(studyMethodDetailDto.getDetail()));

                String message = "Cập nhật phương pháp học thành công";
                Object data = studyMethodService.updateStudyMethod(studyMethodDetailDto);
                return ResponseEntity.ok(new MessageDto(message, data));
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Object> deleteStudyMethod(@PathVariable Long id) throws BadRequestException {
                studyMethodService.deleteStudyMethod(id);
                return ResponseEntity.noContent().build();
        }
}
