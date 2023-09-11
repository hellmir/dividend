package personal.dividend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import personal.dividend.dto.CompanyEntityResponseDto;
import personal.dividend.dto.CompanyResponseDto;

public interface CompanyService {

    CompanyResponseDto save(String ticker);

    Page<CompanyEntityResponseDto> getAllCompanies(Pageable pageable);

    String deleteCompany(String ticker);

}
