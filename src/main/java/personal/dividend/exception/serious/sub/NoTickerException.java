package personal.dividend.exception.serious.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.serious.AbstractSeriousException;

public class NoTickerException extends AbstractSeriousException {

    public NoTickerException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.SERVICE_UNAVAILABLE.value();
    }

}
