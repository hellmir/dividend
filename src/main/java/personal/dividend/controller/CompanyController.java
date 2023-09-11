package personal.dividend.controller;


import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import personal.dividend.dto.CompanyEntityResponseDto;
import personal.dividend.dto.CompanyResponseDto;
import personal.dividend.exception.serious.sub.NoTickerException;
import personal.dividend.model.Company;
import personal.dividend.model.constants.CacheKey;
import personal.dividend.service.CompanyService;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {

        //  Trie 이용 자동완성
        var result = companyService.autocomplete(keyword);

        //  DB Query 이용 자동완성
//        var result = companyService.getCompanyNamesByKeyword(keyword);

        return ResponseEntity.ok(result);

    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {

        Page<CompanyEntityResponseDto> companies = companyService.getAllCompanies(pageable);

        return ResponseEntity.ok(companies);

    }

    /**
     * 회사 및 배당금 정보 추가
     *
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {

        String ticker = request.getTicker().trim();

        if (ObjectUtils.isEmpty(ticker)) {
            throw new NoTickerException("failed to scrap ticker -> " + ticker);
        }

        CompanyResponseDto companyResponseDto = companyService.save(ticker);

        companyService.addAutocompleteKeyWord(companyResponseDto.getName());

        return ResponseEntity.ok(companyResponseDto);

    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {

        String companyName = companyService.deleteCompany(ticker);

        clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);

    }

    public void clearFinanceCache(String companyName) {
        redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }

}
