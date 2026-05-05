package com.example.demo.dto;

public class UserSummaryResponse {
    private Long id;
    private String phone;
    private String nom;
    private String prenom;
    private String ville;
    private String role;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}