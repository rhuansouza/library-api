package com.costanzo.libraryapi.api;

import com.costanzo.libraryapi.exception.ApiErros;
import com.costanzo.libraryapi.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice//Classe com configurações globais
public class ApplicationControllerAdvice {

    //MethodArgumentNotValidException sempre que o objeto não for válido pela expressão @Valid sera lançada esta excessão
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();


        return new ApiErros(bindingResult);


    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(BusinessException ex){
        return new ApiErros(ex);


    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        return new ResponseEntity(new ApiErros(ex), ex.getStatus());
    }


}
