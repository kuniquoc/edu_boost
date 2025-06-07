package quochung.server.payload.studyMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyMethodElementDTO {

    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    private Long typeId;

    private boolean favorite;
}
