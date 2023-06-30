package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaController {
    @Autowired
    private final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public MpaModel getMPAbyId(@PathVariable Integer id) {
        return mpaService.getMpaModel(id);
    }

    @GetMapping("/mpa")
    public List<MpaModel> getAllMPA() {
        return mpaService.findAllMPA();
    }
}
