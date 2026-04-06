package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource; // datasource Spring Boot

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Affiche l'URL de la base pour vérifier que la connexion est OK
        System.out.println("Datasource URL: " + dataSource.getConnection().getMetaData().getURL());
    }
}
