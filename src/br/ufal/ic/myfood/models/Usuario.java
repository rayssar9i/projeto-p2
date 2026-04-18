package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Representa um usuário do sistema MyFood.
 *
 * Dois tipos de usuário:
 *   - Cliente: criado SEM cpf → pode fazer pedidos
 *   - Dono de empresa: criado COM cpf → pode criar empresas e produtos
 *
 * O id é gerado automaticamente como UUID único.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String endereco;

    // Null = cliente comum | Preenchido = dono de empresa
    private String cpf;

    public Usuario(String nome, String email, String senha, String endereco) {
        this(nome, email, senha, endereco, null);
    }

    public Usuario(String nome, String email, String senha, String endereco, String cpf) {
        this.nome     = nome;
        this.email    = email;
        this.senha    = senha;
        this.endereco = endereco;
        this.cpf      = cpf;
        this.id       = UUID.randomUUID().toString(); // ID único gerado pelo sistema
    }

    // --- Getters e Setters ---

    public String getId()      { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome()    { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail()   { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha()   { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCpf()     { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
