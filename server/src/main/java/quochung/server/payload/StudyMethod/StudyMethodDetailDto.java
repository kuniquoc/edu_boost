package quochung.server.payload.StudyMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyMethodDetailDto {

    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    private Long typeId;

    private String detail;

    private boolean isFavorite;
}
