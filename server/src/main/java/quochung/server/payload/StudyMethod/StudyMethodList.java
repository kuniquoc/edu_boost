package quochung.server.payload.studyMethod;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyMethodList {
    List<StudyMethodElementDTO> studyMethodElementDTOs;
    int totalPages;
    int currentPage;
}
