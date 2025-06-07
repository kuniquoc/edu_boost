package quochung.server.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import quochung.server.model.Favorite;
import quochung.server.model.StudyMethod;
import quochung.server.model.User;
import quochung.server.repository.FavoriteRepository;
import quochung.server.repository.StudyMethodRepository;
import quochung.server.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {
        private final FavoriteRepository favoriteRepository;

        private final StudyMethodRepository studyMethodRepository;

        private final UserRepository userRepository;

        public void addFavorite(Long studyMethodId) throws BadRequestException {
                Favorite favorite = new Favorite();
                Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                                .getId();
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Không tìm thấy người dùng với id: " + userId));
                favorite.setUser(user);
                StudyMethod studyMethod = studyMethodRepository.findById(studyMethodId)
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
                favorite.setStudyMethod(studyMethod);
                favoriteRepository.save(favorite);
        }

        public void removeFavorite(Long studyMethodId) throws BadRequestException {
                Long userId = ((UserDetailsImplement) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                                .getId();
                favoriteRepository.deleteByUserIdAndStudyMethodId(userId, studyMethodId);
        }
}
