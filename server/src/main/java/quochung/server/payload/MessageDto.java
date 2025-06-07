package quochung.server.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto {
    private String message;
    private Object data = null;

    public MessageDto(String message) {
        this.message = message;
    }
}
