package quochung.server.payload.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoItemDTO {
    private Long id;
    private String description;
    private boolean completed;
}
