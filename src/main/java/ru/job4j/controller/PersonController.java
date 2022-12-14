package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonCredentials;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final PersonService persons;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = persons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check login"
                        ));
        return new ResponseEntity<>(
                person,
                HttpStatus.OK);
    }

    @PostMapping("/")
    @Validated
    public ResponseEntity<Person> create(@Valid @RequestBody PersonCredentials personDTO) {
        if (personDTO.getLogin().equals(personDTO.getPassword())) {
            throw new IllegalArgumentException("Login and password must be not equal!");
        }
        Person person = new Person();
        person.setLogin(personDTO.getLogin());
        person.setPassword(personDTO.getPassword());
        return new ResponseEntity<>(
                persons.create(person),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/")
    @Validated
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody Person person) {
        var personOpt = persons.findByLogin(person.getLogin());
        if (personOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found for update! Check login.");
        }
        if (!persons.update(personOpt.get())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found for update! Check id.");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (!persons.delete(person)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person with such id!");
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }
}
