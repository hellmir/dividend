package personal.dividend.exception.significant.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.significant.AbstractSignificantException;

public class NotCorrectPasswordException extends AbstractSignificantException {

    public NotCorrectPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

}
