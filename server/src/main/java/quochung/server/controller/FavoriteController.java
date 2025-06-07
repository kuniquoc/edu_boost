package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import quochung.server.payload.MessageDto;
import quochung.server.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
@Transactional
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/{studyMethodId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addFavorite(@RequestParam Long studyMethodId) {
        try {
            favoriteService.addFavorite(studyMethodId);
            return ResponseEntity.status(201)
                    .body(new MessageDto("Thêm phương pháp học vào danh sách yêu thích thành công"));
        } catch (BadRequestException e) {
            return ResponseEntity.status(404).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @DeleteMapping("/{studyMethodId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> removeFavorite(@RequestParam Long studyMethodId) {
        try {
            favoriteService.removeFavorite(studyMethodId);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(404).body(new MessageDto(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }
}
