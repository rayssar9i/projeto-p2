package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Pedidos.
 *
 * Responsabilidades:
 *   - Criação de pedidos com validações de perfil e estado
 *   - Adição e remoção de produtos (respeitando estado do pedido)
 *   - Fechamento de pedidos
 *   - Consulta de atributos por número do pedido
 *   - Recuperação de número de pedido por cliente/empresa/índice
 *
 * Serializable para persistir junto ao SystemState.
 */
public class PedidoManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pedido> pedidos;

    // Números gerados sequencialmente a partir de 1
    private int proximoNumero;

    public PedidoManager() {
        this.pedidos        = new ArrayList<>();
        this.proximoNumero  = 1;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria um novo pedido para um cliente em uma empresa.
     *
     * Validações (conforme us4_1.txt):
     *   - Apenas clientes (sem CPF) podem criar pedidos
     *   - Não pode existir pedido "aberto" do mesmo cliente na mesma empresa
     *
     * @return número sequencial do pedido criado
     */
    public int criarPedido(String clienteId, int empresaId, Usuario cliente) throws Exception {

        // Donos de empresa (com CPF) não podem fazer pedidos
        if (cliente.getCpf() != null && !cliente.getCpf().isEmpty()) {
            throw new Exception("Dono de empresa nao pode fazer um pedido");
        }

        // Verifica se já existe pedido aberto do mesmo cliente na mesma empresa
        boolean temAberto = pedidos.stream()
                .anyMatch(p -> p.getClienteId().equals(clienteId)
                        && p.getEmpresaId() == empresaId
                        && p.isAberto());

        if (temAberto) {
            throw new Exception("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
        }

        pedidos.add(new Pedido(proximoNumero, clienteId, empresaId));
        return proximoNumero++;
    }

    // -------------------------------------------------------------------------
    // ALTERAÇÃO DE PRODUTOS
    // -------------------------------------------------------------------------

    /**
     * Adiciona um produto ao pedido pelo número do pedido e ID do produto.
     *
     * Validações:
     *   - Pedido deve existir e estar aberto
     *   - Produto deve pertencer à empresa do pedido
     */
    public void adicionarProduto(int numeroPedido, int produtoId,
                                  ProdutoManager produtoManager) throws Exception {

        Pedido pedido = buscarPorNumero(numeroPedido);

        // Pedido não encontrado → mensagem específica do us4_1
        if (pedido == null) throw new Exception("Nao existe pedido em aberto");

        // Pedido fechado não aceita novos produtos
        if (!pedido.isAberto()) {
            throw new Exception("Nao e possivel adcionar produtos a um pedido fechado");
        }

        // Produto deve existir e pertencer à empresa do pedido
        Produto produto = produtoManager.buscarPorId(produtoId);
        if (produto == null || produto.getEmpresaId() != pedido.getEmpresaId()) {
            throw new Exception("O produto nao pertence a essa empresa");
        }

        pedido.adicionarProduto(produtoId);
    }

    /**
     * Remove UMA ocorrência de um produto (pelo nome) de um pedido aberto.
     *
     * Validações:
     *   - Nome do produto não pode ser vazio
     *   - Pedido deve existir e estar aberto
     *   - Produto deve estar na lista do pedido
     */
    public void removerProduto(int numeroPedido, String nomeProduto,
                               ProdutoManager produtoManager) throws Exception {

        if (nomeProduto == null || nomeProduto.isEmpty()) {
            throw new Exception("Produto invalido");
        }

        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        // Pedido fechado não aceita remoção
        if (!pedido.isAberto()) {
            throw new Exception("Nao e possivel remover produtos de um pedido fechado");
        }

        // Busca o produto pelo nome dentro da empresa do pedido
        Produto produto = produtoManager.buscarPorNomeNaEmpresa(nomeProduto, pedido.getEmpresaId());

        // Produto não existe na empresa OU não está na lista do pedido
        if (produto == null || !pedido.getProdutosIds().contains(produto.getId())) {
            throw new Exception("Produto nao encontrado");
        }

        // Remove apenas UMA ocorrência (a primeira encontrada)
        pedido.removerProduto(produto.getId());
    }

    // -------------------------------------------------------------------------
    // FECHAMENTO
    // -------------------------------------------------------------------------

    /**
     * Fecha o pedido, mudando o estado de "aberto" para "preparando".
     * Após fechado, nenhuma alteração é permitida.
     */
    public void fecharPedido(int numeroPedido) throws Exception {
        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");
        pedido.fechar();
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor de um atributo de um pedido pelo número.
     *
     * Atributos suportados: cliente, empresa, estado, produtos, valor
     *   - "cliente"  → nome do usuário cliente
     *   - "empresa"  → nome da empresa
     *   - "estado"   → "aberto" ou "preparando"
     *   - "produtos" → {[nome1, nome2, ...]}
     *   - "valor"    → soma formatada com 2 casas decimais
     */
    public String getPedidos(int numeroPedido, String atributo,
                             UsuarioManager usuarioManager,
                             EmpresaManager empresaManager,
                             ProdutoManager produtoManager) throws Exception {

        // Atributo vazio → inválido antes de buscar o pedido
        if (atributo == null || atributo.isEmpty()) throw new Exception("Atributo invalido");

        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        switch (atributo.toLowerCase()) {
            case "cliente":
                // Resolve ID → nome do cliente
                Usuario cliente = usuarioManager.buscarPorId(pedido.getClienteId());
                return cliente != null ? cliente.getNome() : "";

            case "empresa":
                // Resolve ID → nome da empresa
                Empresa empresa = empresaManager.buscarPorId(pedido.getEmpresaId());
                return empresa != null ? empresa.getNome() : "";

            case "estado":
                return pedido.getEstado();

            case "produtos":
                // Mapeia IDs → nomes dos produtos, mantendo a ordem de inserção
                List<String> nomes = pedido.getProdutosIds().stream()
                        .map(id -> {
                            Produto p = produtoManager.buscarPorId(id);
                            return p != null ? p.getNome() : "";
                        })
                        .collect(Collectors.toList());
                //return "{[" + String.join(", ", nomes) + "]}";
                return "{[" + String.join(", ", nomes) + "]}";

            case "valor":
                // Soma os valores de todos os produtos (incluindo repetições)
                float total = 0f;
                for (int id : pedido.getProdutosIds()) {
                    Produto p = produtoManager.buscarPorId(id);
                    if (p != null) total += p.getValor();
                }
                // Formata com 2 casas decimais (ex: "6.20", não "6.2")
                return String.format(java.util.Locale.US, "%.2f", total);//teste pra corrigirt o erro de . ou , 

            default:
                throw new Exception("Atributo nao existe");
        }
    }

    /**
     * Retorna o número de um pedido de um cliente em uma empresa pelo índice.
     *
     * Os pedidos são ordenados do mais antigo para o mais novo (menor número = índice 0).
     * Inclui pedidos em qualquer estado ("aberto" e "preparando").
     */
    public int getNumeroPedido(String clienteId, int empresaId, int indice) throws Exception {
        List<Pedido> filtrados = pedidos.stream()
                .filter(p -> p.getClienteId().equals(clienteId)
                        && p.getEmpresaId() == empresaId)
                // Ordena do mais antigo (menor número) para o mais novo
                .sorted((a, b) -> Integer.compare(a.getNumero(), b.getNumero()))
                .collect(Collectors.toList());

        if (filtrados.isEmpty() || indice >= filtrados.size()) {
            throw new Exception("Pedido nao encontrado");
        }

        return filtrados.get(indice).getNumero();
    }

    // -------------------------------------------------------------------------
    // BUSCA INTERNA
    // -------------------------------------------------------------------------

    /**
     * Busca pedido pelo número. Retorna null se não encontrado.
     */
    public Pedido buscarPorNumero(int numero) {
        return pedidos.stream()
                .filter(p -> p.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }
}
