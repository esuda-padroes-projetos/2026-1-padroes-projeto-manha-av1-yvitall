package com.yadot.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {

    @GetMapping("/boasvindas")
    public String boasVindas(){
        return "Inicio! SPRING BOOT + API REST + ANNOTATIONS + ROTAS + MAPING ";
    }
}
