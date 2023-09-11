package personal.dividend.service;

import personal.dividend.dto.ScrapedResultResponseDto;

public interface FinanceService {
    ScrapedResultResponseDto getDividendByCompanyName(String companyName);
}
