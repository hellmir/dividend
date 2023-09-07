package personal.dividend.scraper;

import personal.dividend.model.Company;
import personal.dividend.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);

}
