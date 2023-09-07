package personal.dividend.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.dividend.model.Company;
import personal.dividend.model.ScrapedResult;
import personal.dividend.persist.entity.CompanyEntity;
import personal.dividend.persist.entity.DividendEntity;
import personal.dividend.persist.repository.CompanyRepository;
import personal.dividend.persist.repository.DividendRepository;
import personal.dividend.scraper.Scraper;

import java.util.List;

import static personal.dividend.model.constants.CacheKey.KEY_FINANCE;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")

    public void yahooFinanceScheduling() {

        log.info("scraping scheduler is started");

        List<CompanyEntity> companies = companyRepository.findAll();

        //  회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {

            log.info("scraping scheduler is started -> " + company.getName());

            ScrapedResult scrapedResult = yahooFinanceScraper
                    .scrap(new Company(company.getTicker(), company.getName()));


            //  스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = dividendRepository
                                .existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });

            //  연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }

}
