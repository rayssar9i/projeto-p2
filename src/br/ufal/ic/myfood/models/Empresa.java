package br.ufal.ic.myfood.models;

import java.io.Serializable;

/**
 * Representa uma empresa (restaurante) cadastrada no sistema.
 *
 * Regras de negócio:
 *   - Dois donos DIFERENTES não podem ter empresas com o mesmo nome.
 *   - O MESMO dono não pode ter duas empresas com mesmo nome E mesmo endereço.
 *   - Apenas usuários com CPF (donos) podem criar empresas.
 *
 * O id é gerado sequencialmente pelo EmpresaManager.
 */
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    id;
    private String nome;
    private String endereco;
    private String tipoCozinha;

    // ID (UUID) do usuário dono desta empresa
    private String donoId;

    public Empresa(int id, String nome, String endereco, String tipoCozinha, String donoId) {
        this.id         = id;
        this.nome       = nome;
        this.endereco   = endereco;
        this.tipoCozinha = tipoCozinha;
        this.donoId     = donoId;
    }

    // --- Getters e Setters ---

    public int    getId()          { return id; }

    public String getNome()        { return nome; }
    public void   setNome(String nome) { this.nome = nome; }

    public String getEndereco()    { return endereco; }
    public void   setEndereco(String endereco) { this.endereco = endereco; }

    public String getTipoCozinha() { return tipoCozinha; }
    public void   setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }

    public String getDonoId()      { return donoId; }
    public void   setDonoId(String donoId) { this.donoId = donoId; }
}
