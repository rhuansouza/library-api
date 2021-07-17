package com.costanzo.libraryapi.api.resource;

import com.costanzo.libraryapi.api.dto.BookDTO;
import com.costanzo.libraryapi.exception.ApiErros;
import com.costanzo.libraryapi.exception.BusinessException;
import com.costanzo.libraryapi.model.entity.Book;
import com.costanzo.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {


        private BookService service;
        private ModelMapper modelMapper;

        public BookController(BookService service, ModelMapper mapper) {
                this.service = service;
                this.modelMapper = mapper;
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public BookDTO create(@RequestBody  @Valid BookDTO dto){
                Book entity = modelMapper.map(dto, Book.class);

                entity = service.save(entity);

                return modelMapper.map(entity, BookDTO.class);
        }

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

}
