package com.yadot.api.controller;

import com.yadot.api.model.HabitModel;
import com.yadot.api.service.HabitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    public HabitModel create(@RequestBody HabitModel habitModel) { return habitoService.save(habitModel);}

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {habitoService.deleteHabit(id);}
}
// GET, POST, PUT, DELETE
