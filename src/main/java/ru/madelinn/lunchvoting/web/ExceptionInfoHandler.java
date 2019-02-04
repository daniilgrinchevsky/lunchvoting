package ru.madelinn.lunchvoting.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.madelinn.lunchvoting.util.Util;
import ru.madelinn.lunchvoting.util.exception.ApplicationException;
import ru.madelinn.lunchvoting.util.exception.ErrorInfo;
import ru.madelinn.lunchvoting.util.exception.ErrorType;
import ru.madelinn.lunchvoting.util.exception.IllegalRequestDataException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    public static final String EXCEPTION_DUPLICATE_EMAIL = "exception.user.duplicateEmail";
    public static final String EXCEPTION_DUPLICATE_NAME = "exception.restaurant.duplicateName";

    private static final Map<String, String> CONSTRAINTS_MAP = Collections.unmodifiableMap(
            new HashMap<String, String>() {
                {
                    put("users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL);
                    put("restaurant_unique_name_idx", EXCEPTION_DUPLICATE_NAME);
                }
            });

    @Autowired
    private MessageUtil messageUtil;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorInfo> applicationError(HttpServletRequest request, ApplicationException appEx){
        ErrorInfo errorInfo = logAndGetErrorInfo(request, appEx, false, appEx.getType(), messageUtil.getMessage(appEx));
        return new ResponseEntity<>(errorInfo, appEx.getHttpStatus());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = Util.getRootCause(e).getMessage();
        if (rootMsg != null){
            String lowerCaseMsg = rootMsg.toLowerCase();
            Optional<Map.Entry<String, String>> entry = CONSTRAINTS_MAP.entrySet().stream()
                    .filter(it -> lowerCaseMsg.contains(it.getKey()))
                    .findAny();
            if (entry.isPresent()) {
                return logAndGetErrorInfo(req, e, false, ErrorType.VALIDATION_ERROR, messageUtil.getMessage(entry.get().getValue()));
            }
        }
        return logAndGetErrorInfo(req, e, true, ErrorType.DATA_ERROR);
    }
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo bindValidationError(HttpServletRequest req, Exception e){
        BindingResult result = e instanceof BindException ?
                ((BindException) e).getBindingResult() : ((MethodArgumentNotValidException) e).getBindingResult();

        String[] details = result.getFieldErrors().stream()
                .map(fe -> messageUtil.getMessage(fe))
                .toArray(String[]::new);
        return logAndGetErrorInfo(req, e, false, ErrorType.VALIDATION_ERROR, details);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e){
        return logAndGetErrorInfo(req, e, false, ErrorType.VALIDATION_ERROR);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorInfo wrongRequest(HttpServletRequest req, NoHandlerFoundException e){
        return logAndGetErrorInfo(req, e, false, ErrorType.WRONG_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e){
        return logAndGetErrorInfo(req, e, true, ErrorType.APP_ERROR);
    }


    private ErrorInfo logAndGetErrorInfo(HttpServletRequest request, Exception e, boolean logException, ErrorType errorType, String... details){
        Throwable rootCause = Util.getRootCause(e);
        if(logException)
            log.error(errorType + " at request " + request.getRequestURL(), rootCause);
        else
            log.warn("{} at request {}: {}", errorType, request.getRequestURL(), rootCause.toString());
        return new ErrorInfo(request.getRequestURL(), errorType,
                messageUtil.getMessage(errorType.getErrorCode()),
                details.length !=0 ? details : new String[]{Util.getMessage(rootCause)});
    }
}
