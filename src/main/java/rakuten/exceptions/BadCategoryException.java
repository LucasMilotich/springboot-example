package rakuten.exceptions;


public class BadCategoryException extends RuntimeException{
    public BadCategoryException(String exception) {
        super(exception);
    }
}