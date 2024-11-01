package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final HashMap<Long, User> users = new HashMap<>();
    private String warn = "Переданы некорректные данные";

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();
        if (email == null || email.isBlank() || (!email.contains("@"))) {
            log.warn(warn);
            throw new ConditionsNotMetException("Имейл не может быть пустым или не содержать символа '@'");
        }
        List<User> theSameEmailUser = users.values().stream().filter(userOfStream -> userOfStream.getEmail().equals(user.getEmail())).toList();
        if (!theSameEmailUser.isEmpty()) {
            log.warn("Использованы данные другого пользователя");
            throw new DuplicatedDataException("Невозможно использовать данный имейл, т.к он уже используется другим пользователем");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn(warn);
            throw new ConditionsNotMetException("Логин не может быть пустым или содержать пробелы");
        }
        if (birthday != null) {
            if (LocalDate.now().isBefore(birthday)) {
                log.warn(warn);
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }
        }

        if (name == null || name.isBlank()) {
            user.setName(login);
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Добавление пользователя прошло успешно: {} ", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn(warn);
            throw new ConditionsNotMetException("Для обновления данных пользователя необходимо указать Id");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Информация не найдена");
            throw new NotFoundException("Пользователь с указанным Id не найден - невозможно обновить данные");
        }
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();
        User oldUser = users.get(user.getId());

        if (email != null && (!email.isBlank()) && (email.contains("@"))) {
            List<User> theSameEmailUser = users.values().stream().filter(userOfStream -> userOfStream.getEmail().equals(user.getEmail())).toList();
            if (theSameEmailUser.isEmpty()) {
                oldUser.setEmail(user.getEmail());
            }
        }
        if (login != null && (!login.isBlank()) && (!login.contains(" "))) {
            String oldLogin = oldUser.getLogin();
            oldUser.setLogin(user.getLogin());
            if (oldLogin.equals(oldUser.getName())) {
                oldUser.setName(login);
            }
        }
        if (birthday != null && LocalDate.now().isAfter(birthday)) {
            oldUser.setBirthday(user.getBirthday());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }

        log.debug("Обновление пользовательских данных пользователя под Id - {}, прошло успешно", oldUser.getId());

        return oldUser;
    }

    @GetMapping
    public List<User> getAll() {
        List<User> allUsers = users.values().stream().toList();
        log.info("JОтправлен весь список пользователей");
        return allUsers;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
