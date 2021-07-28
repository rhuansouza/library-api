package com.costanzo.libraryapi.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Agendador {

    //@Scheduled(fixedDelay = 5000)
    public void executar(){
        System.out.println("Executou o Agendador");
    }
}
