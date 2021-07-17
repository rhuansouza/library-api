package com.costanzo.libraryapi.service.impl;

import com.costanzo.libraryapi.exception.BusinessException;
import com.costanzo.libraryapi.model.entity.Book;
import com.costanzo.libraryapi.model.repository.BookRepository;
import com.costanzo.libraryapi.service.BookService;

public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("isbn já cadastrado.");
        }
        return repository.save(book);
    }



}
