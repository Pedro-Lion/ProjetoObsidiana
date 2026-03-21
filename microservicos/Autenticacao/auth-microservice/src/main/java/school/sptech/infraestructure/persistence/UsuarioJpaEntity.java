package school.sptech.infraestructure.persistence;

import jakarta.persistence.*;

// Entidade JPA na camada de infra (não é o model de domínio)
@Entity
@Table(name = "usuario") // mesmo nome de tabela do backend principal
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    // getters
    public Long getId()     { return id; }
    public String getNome() { return nome; }
    public String getEmail(){ return email; }
    public String getSenha(){ return senha; }

    // setters
    public void setNome(String nome)   { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}