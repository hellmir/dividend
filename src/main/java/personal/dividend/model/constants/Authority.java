package personal.dividend.model.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Authority {

    ROLE_READ("읽기 권한"),
    ROLE_WRITE("쓰기 권한");

    private final String description;

}
