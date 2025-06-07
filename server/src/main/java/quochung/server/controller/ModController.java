package quochung.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.MessageDto;
import quochung.server.service.StudyMethodService;

@RestController
@RequestMapping("/api/mod")
@RequiredArgsConstructor
public class ModController {
    private final StudyMethodService studyMethodService;

    @GetMapping("/study-methods")
    public ResponseEntity<Object> getAllStudyMethods(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") long typeId,
            @RequestParam(defaultValue = "") String search) throws Exception {
        String message = "Lấy danh sách phương pháp học thành công";
        Object data = studyMethodService.getAllStudyMethodsOfMod(page, size, typeId, search);
        return ResponseEntity.ok(new MessageDto(message, data));
    }
}
