package com.yadot.api.service;

import com.yadot.api.model.HabitModel;
import com.yadot.api.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }
    public List<HabitModel> getAll() {
        return habitRepository.findAll();
    }
    public HabitModel save(HabitModel newHabit) {
        if (newHabit.getHabitName()){

        }
            //.... como dar pull sem deletar o local

        return habitRepository.save(newHabit);
    }
    public void delete(Long id){HabitRepository.deleteById(id);}
}
