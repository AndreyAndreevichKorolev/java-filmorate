package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmControllerTest {

    FilmController controller = new FilmController();
    String failName = " ";
    String failDescription = new String(new char[201]);
    LocalDate failReleaseDate = LocalDate.of(1895, 12, 27);
    int failDuration = -3;
    Film film = Film.builder()
            .name("Гарри Поттер")
            .description("Фильм про волшебство")
            .releaseDate(LocalDate.of(2001, 11, 4))
            .duration(152)
            .build();

    @Test
    public void theVoidCreateShouldWorkCorrectly() {
        Film createdFilm = controller.create(film);
        Assertions.assertEquals(film.getId(), createdFilm.getId(), "Фильм не был добавлен");
        Film filmWithFailName = film.toBuilder().name(failName).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(filmWithFailName), "Контроллер ошибочно обработал неверное имя");
        Film filmWithFailDescription = film.toBuilder().description(failDescription).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(filmWithFailDescription), "Контроллер ошибочно обработал неверное описание");
        Film filmWithFailReleaseDate = film.toBuilder().releaseDate(failReleaseDate).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(filmWithFailReleaseDate), "Контроллер ошибочно обработал неверную дату выхода");
        Film filmWithFailDuration = film.toBuilder().duration(failDuration).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(filmWithFailDuration), "контроллер ошибочно обработал неверную продолжительность ");
    }

    @Test
    public void theVoidUpdateShouldWorkCorrectly() {
        Film filmWithNullId = film.toBuilder().build();
        controller.create(film);
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.update(filmWithNullId), "Контроллер пропустил фильм без Id");
        Film filmWithIdThatIsNotInDatabase = film.toBuilder().id(34L).build();
        Assertions.assertThrows(NotFoundException.class, () -> controller.update(filmWithIdThatIsNotInDatabase));
    }
}