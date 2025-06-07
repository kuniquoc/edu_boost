package quochung.server.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.MessageDto;
import quochung.server.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
        private final FavoriteService favoriteService;

        @PostMapping("/{studyMethodId}")
        @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<Object> addFavorite(@PathVariable Long studyMethodId) throws BadRequestException {
                favoriteService.addFavorite(studyMethodId);
                return ResponseEntity.status(201)
                                .body(new MessageDto("Thêm phương pháp học vào danh sách yêu thích thành công"));
        }

        @DeleteMapping("/{studyMethodId}")
        @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<Object> removeFavorite(@PathVariable Long studyMethodId) throws BadRequestException {
                favoriteService.removeFavorite(studyMethodId);
                return ResponseEntity.noContent().build();
        }
}
