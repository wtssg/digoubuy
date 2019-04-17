package wtssg.xdly.digoubuyuserservice.common.resp;

import lombok.Data;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;

/**
 * Created by JackWangon[www.coder520.com] 2018/1/9.
 */
@Data
public class ApiResult<T> {

    private int code = Constants.RESP_STATUS_OK;

    private String message;

    private T data;

    public ApiResult() {
    }

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
