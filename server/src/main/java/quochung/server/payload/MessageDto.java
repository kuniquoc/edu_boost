package quochung.server.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
    private String message;
    private Object data = null;

    public MessageDto(String message) {
        this.message = message;
    }
}
