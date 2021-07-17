package com.costanzo.libraryapi.api.resource;

import com.costanzo.libraryapi.api.dto.BookDTO;
import com.costanzo.libraryapi.exception.BusinessException;
import com.costanzo.libraryapi.model.entity.Book;
import com.costanzo.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith versão do junit 4
@ExtendWith(SpringExtension.class) //versão do junit 5
@ActiveProfiles("test")//rodar com perfil de teste
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;



    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createdBookTest() throws Exception{

        BookDTO dto = createNewBook();

        Book savedBook = Book.builder().id(10l).author("Arthur").title("As aventuras").isbn("001").build();
        //BDDMockito
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        //Mockito
       // Mockito.when(service.save(Mockito.any(Book.class))).thenReturn(savedBook);

        //transforma um objeto em json
        String json = new ObjectMapper().writeValueAsString(dto);

        //definir uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON) //conteudo que estou passando
                .accept(MediaType.APPLICATION_JSON)//conteudo que o servidor aceita
                .content(json);//corpo da requisição

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value("As aventuras"))
                .andExpect(jsonPath("author").value("Arthur"))
                .andExpect(jsonPath("isbn").value("001"));


    }



    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
    public void createdInvalidBookTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new BookDTO());


        //definir uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON) //conteudo que estou passando
                .accept(MediaType.APPLICATION_JSON)//conteudo que o servidor aceita
                .content(json);//corpo da requisição

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }


    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception{

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));  //Erro de Negócio

        //definir uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON) //conteudo que estou passando
                .accept(MediaType.APPLICATION_JSON)//conteudo que o servidor aceita
                .content(json);//corpo da requisição

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));

    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
    }
}
