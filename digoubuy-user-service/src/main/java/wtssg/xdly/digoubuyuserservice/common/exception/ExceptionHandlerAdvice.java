package wtssg.xdly.digoubuyuserservice.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;
import wtssg.xdly.digoubuyuserservice.common.resp.ApiResult;

import java.util.List;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ApiResult(Constants.RESP_STATUS_INTERNAL_ERROR, "系统异常，请稍后重试");
    }

    @ExceptionHandler(DigoubuyException.class)
    public ApiResult handleException(DigoubuyException e) {
        return new ApiResult(e.getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handlerIllegalParamException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = "参数不合法";
        if (!errors.isEmpty()) {
            message = errors.get(0).getDefaultMessage();
        }
        return new ApiResult(Constants.RESP_STATUS_BADREQUEST, message);
    }
}
