package allcom.controller;

public class RetMessage {

    //private final long id;
    private final String errorCode;
    private final String errorMessage;
    private final String retContent;

    public RetMessage(String errorCode,String errorMessage,String retContent) {
        this.errorCode = errorCode;
        this.retContent = retContent;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRetContent() {
        return retContent;
    }
}
