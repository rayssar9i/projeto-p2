package br.ufal.ic.myfood;

import br.ufal.ic.myfood.models.*;

/**
 * Facade do sistema MyFood — ponto de entrada do EasyAccept.
 *
 * O EasyAccept chama os métodos públicos desta classe diretamente a partir
 * dos scripts de teste (.txt). Os nomes e assinaturas dos métodos devem
 * corresponder exatamente ao que os scripts esperam.
 *
 * PADRÃO DE ESTADO (lazy loading + persistência):
 *   - O SystemState é carregado do disco na primeira chamada a getState().
 *   - zerarSistema()    → apaga o arquivo e cria estado vazio.
 *   - encerrarSistema() → salva o estado em disco para o próximo teste (_2).
 *   - Testes _2 não chamam zerarSistema, por isso encontram o estado salvo.
 */
public class Facade {

    /** Estado global do sistema (carregado do disco ou criado novo). */
    private SystemState state;

    /**
     * Lazy initialization: carrega o estado do disco na primeira chamada.
     * Garante persistência entre execuções EasyAccept separadas.
     */
    private SystemState getState() {
        if (state == null) {
            state = SystemState.carregar();
        }
        return state;
    }

    // =========================================================================
    // CONTROLE DO SISTEMA
    // =========================================================================

    /**
     * Apaga todos os dados e reinicia o sistema do zero.
     * Também apaga o arquivo de persistência para não reutilizar dados antigos.
     */
    public void zerarSistema() {
        SystemState.apagarArquivo();
        this.state = new SystemState();
    }

    /**
     * Salva o estado atual em disco e encerra o sistema.
     * Permite que testes _2 encontrem os dados criados pelos testes _1.
     */
    public void encerrarSistema() {
        getState().salvar();
    }

    // =========================================================================
    // US1 — USUÁRIOS
    // =========================================================================

    /** Retorna um atributo de um usuário pelo ID. Atributos: nome, email, senha, endereco, cpf */
    public String getAtributoUsuario(String id, String atributo) throws Exception {
        return getState().usuarioManager.getAtributoUsuario(id, atributo);
    }

    /** Cria um usuário do tipo Cliente (sem CPF). */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco) throws Exception {
        getState().usuarioManager.criarUsuario(nome, email, senha, endereco, null);
    }

    /**
     * Cria um usuário do tipo Dono de Empresa (com CPF).
     * CPF deve ter 14 caracteres no formato XXX.XXX.XXX-XX.
     * CPF vazio ("") é tratado como inválido (dono sem CPF não faz sentido).
     */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String cpf) throws Exception {
        // CPF não fornecido ou vazio → inválido para dono de empresa
        if (cpf == null || cpf.isEmpty()) throw new Exception("CPF invalido");
        getState().usuarioManager.criarUsuario(nome, email, senha, endereco, cpf);
    }

    /** Autentica e retorna o ID do usuário. Lança exceção se credenciais inválidas. */
    public String login(String email, String senha) throws Exception {
        return getState().usuarioManager.login(email, senha);
    }

    // =========================================================================
    // US2 — EMPRESAS
    // =========================================================================

    /**
     * Cria uma nova empresa (restaurante) para um dono.
     * @param tipoEmpresa tipo da empresa (ex: "restaurante")
     * @param dono        ID do usuário dono (deve ter CPF)
     * @return ID sequencial da empresa criada
     */
    public int criarEmpresa(String tipoEmpresa, String dono, String nome,
                            String endereco, String tipoCozinha) throws Exception {
        Usuario donoObj = getState().usuarioManager.buscarPorId(dono);
        if (donoObj == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.criarEmpresa(
                tipoEmpresa, dono, nome, endereco, tipoCozinha, donoObj);
    }

    /** Retorna todas as empresas de um dono no formato {[[nome1, end1], ...]}. */
    public String getEmpresasDoUsuario(String idDono) throws Exception {
        Usuario dono = getState().usuarioManager.buscarPorId(idDono);
        if (dono == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.getEmpresasDoUsuario(idDono, dono);
    }

    /**
     * Retorna o ID de uma empresa do dono pelo nome e índice.
     * Índice 0 = primeira empresa cadastrada com aquele nome.
     */
    public int getIdEmpresa(String idDono, String nome, int indice) throws Exception {
        return getState().empresaManager.getIdEmpresa(idDono, nome, indice);
    }

    /** Retorna um atributo de uma empresa pelo ID. Atributos: nome, endereco, tipoCozinha, dono */
    public String getAtributoEmpresa(int empresa, String atributo) throws Exception {
        return getState().empresaManager.getAtributoEmpresa(
                empresa, atributo, getState().usuarioManager);
    }

    // =========================================================================
    // US3 — PRODUTOS
    // =========================================================================

    /**
     * Cria um produto para uma empresa.
     * @return ID sequencial do produto criado
     */
    public int criarProduto(int empresa, String nome, float valor,
                            String categoria) throws Exception {
        return getState().produtoManager.criarProduto(empresa, nome, valor, categoria);
    }

    /** Edita os dados de um produto existente pelo ID. */
    public void editarProduto(int produto, String nome, float valor,
                              String categoria) throws Exception {
        getState().produtoManager.editarProduto(produto, nome, valor, categoria);
    }

    /** Retorna um atributo de um produto pelo nome + empresa. Atributos: nome, valor, categoria, empresa */
    public String getProduto(String nome, int empresa, String atributo) throws Exception {
        return getState().produtoManager.getProduto(
                nome, empresa, atributo, getState().empresaManager);
    }

    /** Lista nomes de todos os produtos de uma empresa no formato {[nome1, nome2]}. */
    public String listarProdutos(int empresa) throws Exception {
        return getState().produtoManager.listarProdutos(
                empresa, getState().empresaManager);
    }

    // =========================================================================
    // US4 — PEDIDOS
    // =========================================================================

    /**
     * Cria um pedido de um cliente para uma empresa.
     * Apenas clientes (sem CPF) podem criar pedidos.
     * @return número sequencial do pedido criado
     */
    public int criarPedido(String cliente, int empresa) throws Exception {
        Usuario clienteObj = getState().usuarioManager.buscarPorId(cliente);
        if (clienteObj == null) throw new Exception("Usuario nao encontrado");
        return getState().pedidoManager.criarPedido(cliente, empresa, clienteObj);
    }

    /**
     * Adiciona um produto ao pedido.
     * O produto deve pertencer à empresa do pedido.
     */
    public void adicionarProduto(int numero, int produto) throws Exception {
        getState().pedidoManager.adicionarProduto(
                numero, produto, getState().produtoManager);
    }

    /** Retorna um atributo de um pedido pelo número. Atributos: cliente, empresa, estado, produtos, valor */
    public String getPedidos(int pedido, String atributo) throws Exception {
        return getState().pedidoManager.getPedidos(
                pedido, atributo,
                getState().usuarioManager,
                getState().empresaManager,
                getState().produtoManager);
    }

    /** Fecha o pedido, mudando o estado para "preparando". Não aceita mais alterações após isso. */
    public void fecharPedido(int numero) throws Exception {
        getState().pedidoManager.fecharPedido(numero);
    }

    /**
     * Remove UMA ocorrência de um produto (pelo nome) de um pedido aberto.
     * Se o produto aparecer mais de uma vez, apenas a primeira ocorrência é removida.
     */
    public void removerProduto(int pedido, String produto) throws Exception {
        getState().pedidoManager.removerProduto(
                pedido, produto, getState().produtoManager);
    }

    /**
     * Retorna o número de um pedido de um cliente em uma empresa pelo índice.
     * Índice 0 = pedido mais antigo; inclui pedidos em qualquer estado.
     */
    public int getNumeroPedido(String cliente, int empresa, int indice) throws Exception {
        return getState().pedidoManager.getNumeroPedido(cliente, empresa, indice);
    }

    // =========================================================================
    // MAIN — executa todos os testes EasyAccept em sequência
    // =========================================================================

    public static void main(String[] args) {
        // US1 - Usuários
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_2.txt"});

        // US2 - Empresas
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_2.txt"});

        // US3 - Produtos
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_2.txt"});

        // US4 - Pedidos
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_2.txt"});
    }
}
