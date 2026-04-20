package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Produtos.
 *
 * Responsabilidades:
 *   - Criação e edição de produtos com validações
 *   - Consulta de atributos por nome + empresa
 *   - Listagem de produtos de uma empresa
 *   - Busca por ID ou por nome+empresa (usada por PedidoManager)
 *
 * Serializable para persistir junto ao SystemState.
 */
public class ProdutoManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Produto> produtos;

    // IDs gerados sequencialmente a partir de 1
    private int proximoId;

    public ProdutoManager() {
        this.produtos  = new ArrayList<>();
        this.proximoId = 1;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO E EDIÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria um novo produto para uma empresa.
     *
     * Validações (conforme us3_1.txt):
     *   - nome não pode ser nulo/vazio
     *   - valor deve ser >= 0 (negativo é inválido)
     *   - categoria não pode ser nula/vazia
     *   - não pode existir produto com mesmo nome na mesma empresa
     *
     * @return ID sequencial do produto criado
     */
    public int criarProduto(int empresaId, String nome, float valor,
                            String categoria) throws Exception {

        if (nome     == null || nome.isEmpty())     throw new Exception("Nome invalido");
        if (valor    <  0)                          throw new Exception("Valor invalido");
        if (categoria == null || categoria.isEmpty()) throw new Exception("Categoria invalido");

        // Nome deve ser único dentro da mesma empresa
        for (Produto p : produtos) {
            if (p.getEmpresaId() == empresaId && p.getNome().equals(nome)) {
                throw new Exception("Ja existe um produto com esse nome para essa empresa");
            }
        }

        produtos.add(new Produto(proximoId, nome, valor, categoria, empresaId));
        return proximoId++;
    }

    /**
     * Edita os dados de um produto existente pelo ID.
     *
     * Mesmas validações de criarProduto + verificação de existência do produto.
     */
    public void editarProduto(int produtoId, String nome, float valor,
                              String categoria) throws Exception {

        if (nome     == null || nome.isEmpty())     throw new Exception("Nome invalido");
        if (valor    <  0)                          throw new Exception("Valor invalido");
        if (categoria == null || categoria.isEmpty()) throw new Exception("Categoria invalido");

        Produto produto = buscarPorId(produtoId);
        if (produto == null) throw new Exception("Produto nao cadastrado");

        // Aplica as alterações diretamente no objeto (já está na lista por referência)
        produto.setNome(nome);
        produto.setValor(valor);
        produto.setCategoria(categoria);
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor de um atributo de um produto, buscado pelo nome + empresa.
     *
     * Atributos suportados: nome, valor, categoria, empresa
     * O atributo "empresa" retorna o NOME da empresa (resolve via EmpresaManager).
     * O atributo "valor" é formatado com 2 casas decimais (ex: "4.40", não "4.4").
     */
    public String getProduto(String nome, int empresaId, String atributo,
                             EmpresaManager empresaManager) throws Exception {

        Produto produto = produtos.stream()
                .filter(p -> p.getEmpresaId() == empresaId && p.getNome().equals(nome))
                .findFirst()
                .orElse(null);

        if (produto == null) throw new Exception("Produto nao encontrado");

        switch (atributo.toLowerCase()) {
            case "nome":      return produto.getNome();
            case "valor":
                // String.format garante "4.40" e não "4.4" — exigido pelos testes
               return String.format(java.util.Locale.US, "%.2f", produto.getValor());
            case "categoria": return produto.getCategoria();
            case "empresa":
                // Resolve ID → nome da empresa
                Empresa empresa = empresaManager.buscarPorId(empresaId);
                return empresa != null ? empresa.getNome() : "";
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    /**
     * Lista os nomes de todos os produtos de uma empresa.
     *
     * Formato de saída: {[nome1, nome2]} ou {[]} se a empresa não tiver produtos.
     * Lança exceção se a empresa não existir.
     */
    public String listarProdutos(int empresaId,
                                 EmpresaManager empresaManager) throws Exception {

        if (empresaManager.buscarPorId(empresaId) == null) {
            throw new Exception("Empresa nao encontrada");
        }

        List<String> nomes = produtos.stream()
                .filter(p -> p.getEmpresaId() == empresaId)
                .map(Produto::getNome)
                .collect(Collectors.toList());

        return "{[" + String.join(", ", nomes) + "]}";
    }

    // -------------------------------------------------------------------------
    // BUSCAS (usadas por outros managers)
    // -------------------------------------------------------------------------

    /**
     * Busca produto pelo ID. Retorna null se não encontrado.
     * Usado por PedidoManager para validar/recuperar produtos.
     */
    public Produto buscarPorId(int id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca produto pelo nome dentro de uma empresa. Retorna null se não encontrado.
     * Usado por PedidoManager para removerProduto (que recebe nome, não ID).
     */
    public Produto buscarPorNomeNaEmpresa(String nome, int empresaId) {
        return produtos.stream()
                .filter(p -> p.getEmpresaId() == empresaId && p.getNome().equals(nome))
                .findFirst()
                .orElse(null);
    }
}
