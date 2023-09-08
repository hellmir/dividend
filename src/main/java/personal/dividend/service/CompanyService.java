package personal.dividend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import personal.dividend.exception.general.sub.AlreadyExistCompanyException;
import personal.dividend.exception.serious.sub.NoTickerException;
import personal.dividend.model.Company;
import personal.dividend.model.ScrapedResult;
import personal.dividend.persist.entity.CompanyEntity;
import personal.dividend.persist.entity.DividendEntity;
import personal.dividend.persist.repository.CompanyRepository;
import personal.dividend.persist.repository.DividendRepository;
import personal.dividend.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {

        log.info("save company -> " + ticker);

        boolean exists = companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new AlreadyExistCompanyException("already exists ticker -> " + ticker);
        }

        return storeCompanyAndDividend(ticker);

    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {

        log.info("store company and dividend -> " + ticker);

        //  ticker를 기준으로 회사를 스크래핑
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new NoTickerException("failed to scrap ticker -> " + ticker);
        }

        //  해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        //  스크래핑 결과
        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntityList);

        return company;

    }

    //  DB Query 이용 자동완성
/*    public List<String> getCompanyNamesByKeyword(String keyword) {

        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());

    }*/

    public void addAutocompleteKeyWord(String companyName) {
        trie.put(companyName, null);
    }

    public List<String> autocomplete(String keyword) {

        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {

        log.info("delete company -> " + ticker);

        var company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new EntityNotFoundException
                        ("failed to find company with ticker -> " + ticker));

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutocompleteKeyword(company.getName());

        return company.getName();

    }

}
