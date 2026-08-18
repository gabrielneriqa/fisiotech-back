package com.app.fisiotech.profissional.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(
        name = "profissionais",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_profissional_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_profissional_registro", columnNames = "registro_profissional")
        }
)
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "registro_profissional", nullable = false, length = 20)
    private String registroProfissional;

    @Column(name = "especialidade", nullable = false, length = 120)
    private String especialidade;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    protected Profissional() {
    }

    public Profissional(String nome, String email, String senha, String registroProfissional, String especialidade) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.registroProfissional = registroProfissional;
        this.especialidade = especialidade;
    }

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profissional profissional)) return false;
        return Objects.equals(id, profissional.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
