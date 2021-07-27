package com.costanzo.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Loan {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String customer;

    @Column(name = "customer_email")
    private String customerEmail;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;
    @Column
    private LocalDate loanDate;
    @Column
    private Boolean returned;

}
