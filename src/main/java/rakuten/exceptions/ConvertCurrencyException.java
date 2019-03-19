package rakuten.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConvertCurrencyException extends RuntimeException{
    public ConvertCurrencyException(String exception) {
        super(exception);
    }
}
