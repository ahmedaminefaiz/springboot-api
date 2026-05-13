package org.urban.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.urban.alert.entity.User;
import org.urban.alert.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostMapping("/users")
    public User createUser() {
        User u = new User();
        u.setNom("Ahmed");
        u.setPrenom("Amine");
        u.setDateNaissance(LocalDate.of(2000, 1, 1));
        u.setVille("Berkane");
        return userRepository.save(u);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
