package personal.dividend.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import personal.dividend.model.Company;
import personal.dividend.model.Dividend;
import personal.dividend.model.ScrapedResult;
import personal.dividend.persist.entity.CompanyEntity;
import personal.dividend.persist.entity.DividendEntity;
import personal.dividend.persist.repository.CompanyRepository;
import personal.dividend.persist.repository.DividendRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static personal.dividend.model.constants.CacheKey.KEY_FINANCE;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private static final Logger log = LoggerFactory.getLogger(FinanceService.class);

    @Cacheable(key = "#companyName", value = KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {

        log.info("search company -> " + companyName);

        //  1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new EntityNotFoundException
                        ("failed to find company with companyName -> " + companyName));

        //  2. 조회된 회사의 아이디로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        //  3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);

    }

}
