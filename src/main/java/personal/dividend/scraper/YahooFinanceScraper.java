package personal.dividend.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import personal.dividend.exception.serious.sub.InvalidMonthException;
import personal.dividend.model.Company;
import personal.dividend.model.Dividend;
import personal.dividend.model.ScrapedResult;
import personal.dividend.model.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL
            = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86_400;  // 60 * 60 * 24

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceScraper.class);

    @Override
    public ScrapedResult scrap(Company company) {

        log.info("Beginning to scrap dividend for company: " + company.getTicker());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        var scarpResult = new ScrapedResult();
        scarpResult.setCompany(company);

        try {

            long now = System.currentTimeMillis() / 1_000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);
            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();

            for (Element e : tbody.children()) {

                String txt = e.text();

                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new InvalidMonthException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));

            }

            scarpResult.setDividends(dividends);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stopWatch.stop();

        log.info("Dividend scrapped successfully: {}", company.getTicker());

        return scarpResult;

    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {

        log.info("Beginning to scrap company by ticker: " + ticker);

        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {

            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[0].trim();

            log.info("Company scraped successfully: {}", ticker);

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
