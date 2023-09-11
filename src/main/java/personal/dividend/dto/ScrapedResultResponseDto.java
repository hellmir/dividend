package personal.dividend.dto;

import lombok.Getter;
import lombok.Setter;
import personal.dividend.model.Company;
import personal.dividend.model.Dividend;

import java.util.List;

@Getter
@Setter
public class ScrapedResultResponseDto {
    private Company company;
    private List<Dividend> dividends;
}
