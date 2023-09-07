package personal.dividend.exception.impl;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.AbstractException;

public class NotExistUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 아이디입니다.";
    }

}
