package quochung.server.payload.user;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserList {
    int totalPages;
    int currentPage;
    List<UserElementDTO> userElementDTOs;
}
