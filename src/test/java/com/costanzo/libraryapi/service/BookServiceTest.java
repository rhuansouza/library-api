package com.costanzo.libraryapi.service;

import com.costanzo.libraryapi.exception.BusinessException;
import com.costanzo.libraryapi.model.entity.Book;
import com.costanzo.libraryapi.model.repository.BookRepository;
import com.costanzo.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach//executa antes de cada metodo de teste
    public void setUp(){
        this.service =  new BookServiceImpl(repository);
    }

    @Test
    public void saveBookTest(){
        //cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras").build());
        //execução
        Book savedBook = service.save(book);
        //verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");;
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        //execução
        //metodo para lançar excewption do pacote Assertj
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn já cadastrado.");

        //Nunca deve ser chamado o metodo save do repository
        Mockito.verify(repository, Mockito.never()).save(book);
    }


    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest(){
        Long id = 1l;

        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundbook = service.getByID(id);

        //verificações
        assertThat(foundbook.isPresent()).isTrue();
        assertThat(foundbook.get().getId()).isEqualTo(id);
        assertThat(foundbook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundbook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundbook.get().getIsbn()).isEqualTo(book.getIsbn());

    }


    @Test
    @DisplayName("Deve retornar vazio ao obter  um livro por Id quando ele não existe na base")
    public void bookNotFoundByIdTest(){
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> book = service.getByID(id);

        //verificações
        assertThat(book.isPresent()).isFalse();
    }



    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){

        Book book = Book.builder().id(1l).build();
        //execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->  service.delete(book));

        //verificações
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest(){
        Book book = new Book();
        //execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,() ->  service.delete(book));

        //verificações
        Mockito.verify(repository, Mockito.never()).delete(book);

    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){
        Book book = new Book();
        //execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,() ->  service.update(book));

        //verificações
        Mockito.verify(repository, Mockito.never()).delete(book);

    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest(){
        //cenário
        Long id = 1l;
        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulação
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        when(repository.save(updatingBook)).thenReturn(updatedBook);
        //execução
        Book book = service.update(updatingBook);

        //Verificações
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), pageRequest, 1);
        when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        //execução
        Page<Book> result = service.find(book, pageRequest);
        //verificação
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Deve obter um livro pelo Isbn")
    public void getBookByIsbnTest(){
        //cenário
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));
        //execução
        Optional<Book> book = service.getBookByIsbn(isbn);
        //validação
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getIsbn()).isEqualTo(isbn);
        assertThat(book.get().getId()).isEqualTo(1l);

        verify(repository).findByIsbn(isbn);


    }



}
