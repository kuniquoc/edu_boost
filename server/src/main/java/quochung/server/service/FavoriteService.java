package quochung.server.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quochung.server.model.Favorite;
import quochung.server.model.StudyMethod;
import quochung.server.model.User;
import quochung.server.repository.FavoriteRepository;
import quochung.server.repository.StudyMethodRepository;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserDetailsServiceImplement userDetailsServiceImplement;

    @Autowired

    private StudyMethodRepository studyMethodRepository;

    public void addFavorite(Long studyMethodId) throws BadRequestException {
        Favorite favorite = new Favorite();
        User user = userDetailsServiceImplement.getCurrentUser();
        favorite.setUser(user);
        StudyMethod studyMethod = studyMethodRepository.findById(studyMethodId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học"));
        favorite.setStudyMethod(studyMethod);
        favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long studyMethodId) throws BadRequestException {
        Long userId = userDetailsServiceImplement.getCurrentUserId();
        Favorite favorite = favoriteRepository.findByUserIdAndStudyMethodId(userId, studyMethodId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy phương pháp học trong danh sách yêu thích"));
        favoriteRepository.delete(favorite);
    }
}
