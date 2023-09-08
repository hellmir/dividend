package personal.dividend.exception.general.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.general.AbstractGeneralException;

public class NotCorrectPasswordException extends AbstractGeneralException {

    public NotCorrectPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
