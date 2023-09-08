package personal.dividend.exception.general.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.general.AbstractGeneralException;

public class AlreadyExistUserException extends AbstractGeneralException {

    public AlreadyExistUserException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }

}
