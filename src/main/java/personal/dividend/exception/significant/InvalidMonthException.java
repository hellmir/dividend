package personal.dividend.exception.significant;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.general.AbstractGeneralException;

public class InvalidMonthException extends AbstractSignificantException {

    public InvalidMonthException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
