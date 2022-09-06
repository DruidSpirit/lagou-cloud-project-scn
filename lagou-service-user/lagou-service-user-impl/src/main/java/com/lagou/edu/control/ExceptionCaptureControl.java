package com.lagou.edu.control;

import com.lagou.edu.ResModel;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class ExceptionCaptureControl {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResModel<Map<String,String>>  dealException(MethodArgumentNotValidException ex){

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> map = new HashMap<>();
        for (FieldError fieldError:bindingResult.getFieldErrors()) {
            map.put( fieldError.getDefaultMessage(),fieldError.getField() );
        }

        return ResModel.FAIL(map);
    }

}
