package personal.dividend.exception.general.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.general.AbstractGeneralException;

public class AlreadyExistCompanyException extends AbstractGeneralException {

    public AlreadyExistCompanyException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
