package com.yadot.api.controller;

import com.yadot.api.model.HabitModel;
import com.yadot.api.service.HabitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/habito")
public class HabitController {
    private final HabitService habitoService;

    public HabitController(HabitService habitoService) {
        this.habitoService = habitoService;
    }

    //list
    @GetMapping
    public List<HabitModel> getAll(){return habitoService.getAll();}

    @PostMapping
    public HabitModel create() {
        HabitService.
    }
}

// GET, POST, PUT, DELETE
