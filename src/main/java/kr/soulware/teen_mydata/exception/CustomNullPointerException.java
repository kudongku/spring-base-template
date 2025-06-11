package kr.soulware.teen_mydata.exception;

public class CustomNullPointerException extends BusinessException {

    public CustomNullPointerException(String message) {
        super(message, 404);
    }

}
