package personal.dividend.exception.serious.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.serious.AbstractSeriousException;

public class InvalidMonthException extends AbstractSeriousException {

    public InvalidMonthException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_GATEWAY.value();
    }

}
