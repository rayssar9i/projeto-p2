package br.ufal.ic.myfood.models;

import java.io.Serializable;

/**
 * Representa um produto de uma empresa no sistema.
 *
 * Regras de negócio:
 *   - nome deve ser único DENTRO da mesma empresa (empresas diferentes podem ter produtos homônimos).
 *   - valor deve ser >= 0.
 *   - categoria não pode ser vazia.
 *
 * O id é gerado sequencialmente pelo ProdutoManager.
 */
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    id;
    private String nome;
    private float  valor;
    private String categoria;

    // ID da empresa à qual este produto pertence
    private int empresaId;

    public Produto(int id, String nome, float valor, String categoria, int empresaId) {
        this.id        = id;
        this.nome      = nome;
        this.valor     = valor;
        this.categoria = categoria;
        this.empresaId = empresaId;
    }

    // --- Getters e Setters ---

    public int    getId()          { return id; }

    public String getNome()        { return nome; }
    public void   setNome(String nome) { this.nome = nome; }

    public float  getValor()       { return valor; }
    public void   setValor(float valor) { this.valor = valor; }

    public String getCategoria()   { return categoria; }
    public void   setCategoria(String categoria) { this.categoria = categoria; }

    public int    getEmpresaId()   { return empresaId; }
}
