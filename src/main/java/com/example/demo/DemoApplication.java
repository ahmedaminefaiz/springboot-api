package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        User u = new User();
        u.setNom("Ahmed");
        u.setPrenom("Amine");
        u.setDateNaissance(LocalDate.of(2000, 1, 1));
        u.setVille("Berkane");

        userRepository.save(u);

        userRepository.findAll()
                .forEach(user -> System.out.println(user.getNom()));
    }
}
