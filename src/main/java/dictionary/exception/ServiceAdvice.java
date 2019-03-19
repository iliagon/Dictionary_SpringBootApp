package dictionary.exception;

import dictionary.model.dto.ErrorMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
class ServiceAdvice {
    /**
     * Aggregate all exceptions<br>
     * Mapped the exception into a valid response
     */
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    ErrorMessageDto wordNotFoundHandler(HttpServletResponse res, ServiceException ex) {
        log.error("", ex);
        res.setStatus(ex.getHttpStatus().value());
        return new ErrorMessageDto()
                .setCode(ex.getCode())
                .setDevMessage(ex.getMessage());
    }
}
