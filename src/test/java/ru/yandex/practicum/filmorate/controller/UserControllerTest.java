package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

class UserControllerTest {
    UserController controller = new UserController();
    String emailWithoutDog = "flowers";
    String emailWithoutLetters = "  ";
    String nullEmail = null;
    String nullLogin = null;
    String blankLogin = "  ";
    String loginWithSpace = "flower of_Village";
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    User correctUser = User.builder()
            .email("flowerofvillage@icloud.com")
            .login("Dendelion")
            .birthday(LocalDate.of(2000, 10, 17))
            .build();


    @Test
    public void theVoidCreateShouldWorkCorrectly() {
        User addedUser = controller.create(correctUser);
        Assertions.assertEquals(addedUser.getId(), correctUser.getId(), "Пользователь не был добавлен");
        Assertions.assertEquals(addedUser.getName(), correctUser.getLogin(), "При отсутствии имени контроллер не задал имени значение логина");
        User userWithEmailWithoutDog = correctUser.toBuilder().email(emailWithoutDog).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithEmailWithoutDog), "Контроллер пропустил пользователя с имейлом без собаки");
        User userWithEmailWithoutLetters = correctUser.toBuilder().email(emailWithoutLetters).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithEmailWithoutLetters), "Контроллер пропустил пользователя с пустым имейлом");
        User userWithNullEmail = correctUser.toBuilder().email(nullEmail).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithNullEmail), "Контроллер пропустил пользователя без имейла");
        User userWithAddressThatIsAlreadyExists = correctUser.toBuilder().build();
        Assertions.assertThrows(DuplicatedDataException.class, () -> controller.create(userWithAddressThatIsAlreadyExists), "Контроллер пропустил пользователя с имейлом другого пользователя");
        User userWithNullLogin = correctUser.toBuilder().login(nullLogin).email("gggg@ya.com").build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithNullLogin), "Контроллер пропустил пользователя без логина");
        User userWithBlankLogin = correctUser.toBuilder().login(blankLogin).email("bbbb@iclod.com").build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithBlankLogin), "контроллер пропустил пользователя с пустым логином");
        User userWithLoginWithSpace = correctUser.toBuilder().login(loginWithSpace).email("uuu@iclod.com").build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithLoginWithSpace), "Контроллер пропустил пользователя с пробелом в логине");
        User userWithBirthInTheFuture = correctUser.toBuilder().birthday(tomorrow).email("mmmm@icloud.com").build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.create(userWithBirthInTheFuture), "Контроллер пропустил пользователя с днем рождения в будущем");
    }

    @Test
    public void theVoidUpdateShouldWorkCorrectly() {
        User addedUser = controller.create(correctUser);
        User userWithNullId = addedUser.toBuilder().id(null).build();
        Assertions.assertThrows(ConditionsNotMetException.class, () -> controller.update(userWithNullId));
        User userWithIdThaiIsNotExist = addedUser.toBuilder().id(3000L).build();
        Assertions.assertThrows(NotFoundException.class, () -> controller.update(userWithIdThaiIsNotExist));
    }
}