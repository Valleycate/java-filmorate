package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.DAO.storage.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public List<MpaModel> findAllMPA() {
        return mpaDbStorage.findAllMPA();
    }

    public MpaModel getMpaModel(int id) {
        return mpaDbStorage.getMpaModel(id);
    }
}
