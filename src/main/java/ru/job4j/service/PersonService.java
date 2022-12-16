package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person create(Person person) {
        return personRepository.save(person);
    }

    public boolean update(Person person) {
        if (!personRepository.existsById(person.getId())) {
            return false;
        }
        personRepository.save(person);
        return true;
    }

    public boolean delete(Person person) {
        if (!personRepository.existsById(person.getId())) {
            return false;
        }
        personRepository.delete(person);
        return true;
    }
}
