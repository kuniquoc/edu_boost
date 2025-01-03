package quochung.server.payload.StudyMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateStudyMethodDto {

    private String name;

    private String description;

    private String thumbnail;

    private Long typeId;

    private String detail;

}
