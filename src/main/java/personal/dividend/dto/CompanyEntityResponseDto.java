package personal.dividend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyEntityResponseDto {
    private Long id;
    private String ticker;
    private String name;
}
