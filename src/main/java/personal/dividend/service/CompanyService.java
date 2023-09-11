package personal.dividend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import personal.dividend.dto.CompanyEntityResponseDto;
import personal.dividend.dto.CompanyResponseDto;

import java.util.List;

public interface CompanyService {

    CompanyResponseDto save(String ticker);

    Page<CompanyEntityResponseDto> getAllCompanies(Pageable pageable);

    String deleteCompany(String ticker);

    public void addAutocompleteKeyWord(String companyName);

    List<String> autocomplete(String keyword);

    void deleteAutocompleteKeyword(String keyword);

}
