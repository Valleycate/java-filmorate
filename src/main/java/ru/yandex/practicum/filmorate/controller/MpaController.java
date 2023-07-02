package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public MpaModel getMPAbyId(@PathVariable Integer id) {
        return mpaService.getMpaModel(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MpaModel> getAllMPA() {
        return mpaService.findAllMPA();
    }
}
