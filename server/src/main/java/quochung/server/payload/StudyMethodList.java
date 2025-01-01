package quochung.server.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyMethodList {
    List<StudyMethodElementDto> studyMethodElementDtos;
    int totalPages;
    int currentPage;
}
