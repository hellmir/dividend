package personal.dividend.exception.general.sub;

import org.springframework.http.HttpStatus;
import personal.dividend.exception.general.AbstractGeneralException;

public class NoCompanyException extends AbstractGeneralException {

    public NoCompanyException() {
        super("존재하지 않는 회사명 입니다.");
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
