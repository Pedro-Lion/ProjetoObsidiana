package school.sptech.domain.model;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;

    public Usuario(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Long getId()     { return id; }
    public String getNome() { return nome; }
    public String getEmail(){ return email; }
    public String getSenha(){ return senha; }
}