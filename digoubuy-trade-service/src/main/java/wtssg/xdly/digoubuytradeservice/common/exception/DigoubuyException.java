package wtssg.xdly.digoubuytradeservice.common.exception;


import wtssg.xdly.digoubuytradeservice.common.constants.Constants;

public class DigoubuyException extends RuntimeException {

    private int statusCode = Constants.RESP_STATUS_INTERNAL_ERROR;

    public DigoubuyException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public DigoubuyException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
