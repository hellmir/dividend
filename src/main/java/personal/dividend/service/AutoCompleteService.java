package personal.dividend.service;

import java.util.List;

public interface AutoCompleteService {

    void addAutocompleteKeyWord(String companyName);

    List<String> autocomplete(String keyword);

    void deleteAutocompleteKeyword(String keyword);

}
