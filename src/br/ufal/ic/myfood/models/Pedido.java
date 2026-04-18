package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um pedido feito por um cliente a uma empresa.
 *
 * Ciclo de vida do estado:
 *   "aberto"     → pedido criado, aceita adição e remoção de produtos
 *   "preparando" → fechado via fecharPedido(), não aceita mais alterações
 *
 * Regras de negócio:
 *   - Apenas clientes (sem CPF) podem criar pedidos.
 *   - Não pode haver dois pedidos "aberto" do mesmo cliente na mesma empresa.
 *   - Produtos adicionados devem pertencer à empresa do pedido.
 *   - O mesmo produto pode ser adicionado mais de uma vez.
 *   - removerProduto remove APENAS UMA ocorrência (a primeira encontrada).
 *
 * O número é gerado sequencialmente pelo PedidoManager.
 */
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    numero;
    private String clienteId;  // ID (UUID) do cliente
    private int    empresaId;  // ID sequencial da empresa
    private String estado;     // "aberto" ou "preparando"

    // Lista de IDs de produtos — pode conter repetições (mesmo produto várias vezes)
    private List<Integer> produtosIds;

    public Pedido(int numero, String clienteId, int empresaId) {
        this.numero     = numero;
        this.clienteId  = clienteId;
        this.empresaId  = empresaId;
        this.estado     = "aberto";       // sempre inicia aberto
        this.produtosIds = new ArrayList<>();
    }

    // --- Getters ---

    public int           getNumero()      { return numero; }
    public String        getClienteId()   { return clienteId; }
    public int           getEmpresaId()   { return empresaId; }
    public String        getEstado()      { return estado; }
    public List<Integer> getProdutosIds() { return produtosIds; }

    // --- Ações de negócio ---

    /** Adiciona o ID de um produto à lista (permite duplicatas). */
    public void adicionarProduto(int produtoId) {
        this.produtosIds.add(produtoId);
    }

    /**
     * Remove UMA ocorrência do produto com o ID fornecido.
     * Integer.valueOf() garante remoção por VALOR, não por índice.
     *
     * @return true se encontrou e removeu, false se não encontrou
     */
    public boolean removerProduto(int produtoId) {
        return this.produtosIds.remove(Integer.valueOf(produtoId));
    }

    /** Fecha o pedido, impedindo novas alterações. */
    public void fechar() {
        this.estado = "preparando";
    }

    /** Verifica se o pedido ainda está aberto para alterações. */
    public boolean isAberto() {
        return "aberto".equals(this.estado);
    }
}
