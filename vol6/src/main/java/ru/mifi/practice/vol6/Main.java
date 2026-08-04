package ru.mifi.practice.vol6;

import ru.mifi.practice.vol6.menu.AuthenticationMenu;
import ru.mifi.practice.vol6.menu.Context;
import ru.mifi.practice.vol6.menu.DeleteRegistrationMenu;
import ru.mifi.practice.vol6.menu.Menu;
import ru.mifi.practice.vol6.menu.RegistrationMenu;
import ru.mifi.practice.vol6.model.User;
import ru.mifi.practice.vol6.repository.RepositoryMutant;
import ru.mifi.practice.vol6.repository.UserRepositoryInMemory;
import ru.mifi.practice.vol6.security.Authentication;
import ru.mifi.practice.vol6.security.Security;
import ru.mifi.practice.vol6.storege.Storage;

import java.nio.file.Path;

/**
 * Сборка приложения: здесь и только здесь известно, что хранилище файловое, а ввод
 * консольный. Меню, репозиторий и аутентификация про это не знают.
 */
public final class Main {
    private static final String USERS = "users.json";

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Menu root = Menu.root();
        Storage storage = Storage.file(Path.of(args.length > 0 ? args[0] : USERS));
        RepositoryMutant<User, String> repository = storage.read(new UserRepositoryInMemory());
        prepare(root, repository);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> storage.write(repository)));
        try (Context context = Menu.defaultContext()) {
            root.select(context);
        }
    }

    private static void prepare(Menu root, RepositoryMutant<User, String> repository) {
        Security.Hash hash = Security.createHash();
        new AuthenticationMenu(Authentication.create(repository, hash)).register(root);
        new RegistrationMenu(repository, hash).register(root);
        new DeleteRegistrationMenu(repository).register(root);
    }
}
