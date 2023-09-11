package personal.dividend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberEntityResponseDto {

    private Long id;
    private String username;
    private String password;
    private List<String> roles;

}
