package quochung.server.payload;

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
