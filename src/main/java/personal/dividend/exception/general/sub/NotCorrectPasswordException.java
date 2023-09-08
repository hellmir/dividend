package personal.dividend.exception.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.AbstractException;

public class NotCorrectPasswordException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "일치하지 않는 비밀번호 입니다.";
    }

}
