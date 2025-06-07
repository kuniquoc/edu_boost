package quochung.server.payload.studyMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyMethodDetailDTO {

    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    private Long typeId;

    private String detail;

    private boolean favorite;
}
