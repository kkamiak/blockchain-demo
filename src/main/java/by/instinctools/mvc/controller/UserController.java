package by.instinctools.mvc.controller;

import by.instinctools.domain.entity.User;
import by.instinctools.domain.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;

@RestController(value = "/api")
public class UserController {

    private final UserRepository repository;

    public UserController(final UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping(path = "/users/enter")
    public Map<String, String> enter(final String email) {

        final String token;

        if (repository.existsByEmail(email)) {
            final User user = repository.findByEmail(email);
            token = user.getToken();
        } else {
            final User user = new User();
            user.setToken(randomUUID().toString());
            user.setEmail(email);

            token = repository.save(user).getToken();
        }

        return singletonMap("token", token);
    }

    @GetMapping(path = "/users/me")
    public Boolean me(final String token) {
        return repository.existsByToken(token);
    }

    @PostMapping(path = "/blockchain")
    public void blockchain() {
    }
}
