package personal.dividend.service.impl;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import personal.dividend.dto.CompanyEntityResponseDto;
import personal.dividend.dto.CompanyResponseDto;
import personal.dividend.exception.general.sub.AlreadyExistCompanyException;
import personal.dividend.exception.serious.sub.NoTickerException;
import personal.dividend.model.Company;
import personal.dividend.model.ScrapedResult;
import personal.dividend.persist.entity.CompanyEntity;
import personal.dividend.persist.entity.DividendEntity;
import personal.dividend.persist.repository.CompanyRepository;
import personal.dividend.persist.repository.DividendRepository;
import personal.dividend.scraper.Scraper;
import personal.dividend.service.AutoCompleteService;
import personal.dividend.service.CompanyService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final AutoCompleteService autoCompleteService;

    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 30)
    public CompanyResponseDto save(String ticker) {

        log.info("Beginning to scrap and save company and dividend by ticker: " + ticker);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        boolean exists = companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new AlreadyExistCompanyException("already exists ticker: " + ticker);
        }

        return storeCompanyAndDividend(ticker, stopWatch);

    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 20)
    public Page<CompanyEntityResponseDto> getAllCompanies(Pageable pageable) {

        log.info("Beginning to retrieve all companies");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        companyRepository.findAll(pageable);

        Page<CompanyEntity> companyEntities = companyRepository.findAll(pageable);

        List<CompanyEntityResponseDto> dtos = companyEntities.getContent().stream()
                .map(company -> modelMapper.map(company, CompanyEntityResponseDto.class))
                .collect(Collectors.toList());

        stopWatch.stop();

        log.info("All companies retrieved successfully\n Retrieving task execution time: {} ms",
                stopWatch.getTotalTimeMillis());

        return new PageImpl<>(dtos, pageable, companyEntities.getTotalElements());

    }

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 10)
    public String deleteCompany(String ticker) {

        log.info("Beginning to delete company by ticker: " + ticker);

        var company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new EntityNotFoundException
                        ("failed to find company with ticker: " + ticker));

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        autoCompleteService.deleteAutocompleteKeyword(company.getName());

        log.info("Company deleted successfully: " + ticker);

        return company.getName();

    }

    private CompanyResponseDto storeCompanyAndDividend(String ticker, StopWatch stopWatch) {

        //  ticker를 기준으로 회사를 스크래핑
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new NoTickerException("failed to scrap ticker: " + ticker);
        }

        //  해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        //  스크래핑 결과
        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntityList);

        stopWatch.stop();

        log.info("Company and dividend saved successfully: {}\n Retrieving task execution time: {} ms",
                ticker, stopWatch.getTotalTimeMillis());

        return modelMapper.map(company, CompanyResponseDto.class);

    }

}
