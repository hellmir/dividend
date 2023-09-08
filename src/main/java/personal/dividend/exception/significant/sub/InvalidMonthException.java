package personal.dividend.exception.significant.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.significant.AbstractSignificantException;

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
