package br.ufal.ic.myfood.models;

import java.io.*;

/**
 * Agrupa TODO o estado do sistema em um único objeto serializável.
 *
 * PADRÃO DE PERSISTÊNCIA:
 *   - zerarSistema()    → cria um novo SystemState vazio + apaga o arquivo
 *   - encerrarSistema() → salva o SystemState atual em "system_state.dat"
 *   - Inicialização     → se o arquivo existir, carrega; senão, cria novo
 *
 * Isso permite que os testes _2 (persistência) encontrem os dados criados
 * pelos testes _1, pois o _1 termina com encerrarSistema() (salva) e
 * o _2 começa sem zerarSistema() (carrega do arquivo).
 *
 * Todos os managers são Serializable, então basta serializar este objeto.
 */
public class SystemState implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Caminho do arquivo de persistência (relativo ao diretório de execução). */
    public static final String FILE_PATH = "system_state.dat";

    // Os quatro managers que compõem o estado completo do sistema
    public UsuarioManager usuarioManager;
    public EmpresaManager empresaManager;
    public ProdutoManager produtoManager;
    public PedidoManager  pedidoManager;

    /** Cria um estado completamente novo (sem nenhum dado). */
    public SystemState() {
        this.usuarioManager = new UsuarioManager();
        this.empresaManager = new EmpresaManager();
        this.produtoManager = new ProdutoManager();
        this.pedidoManager  = new PedidoManager();
    }

    // -------------------------------------------------------------------------
    // PERSISTÊNCIA
    // -------------------------------------------------------------------------

    /**
     * Salva o estado atual em arquivo binário via serialização Java.
     * Chamado por encerrarSistema() na Facade.
     */
    public void salvar() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Erro ao salvar estado: " + e.getMessage());
        }
    }

    /**
     * Carrega o estado a partir do arquivo salvo.
     *
     * Se o arquivo não existir (primeira execução ou após zerarSistema),
     * retorna um estado novo e vazio.
     */
    public static SystemState carregar() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new SystemState(); // primeira execução
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (SystemState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar estado: " + e.getMessage());
            return new SystemState();
        }
    }

    /**
     * Apaga o arquivo de persistência do disco.
     * Chamado por zerarSistema() para garantir que o estado zerado
     * persista mesmo após um encerrarSistema() subsequente.
     */
    public static void apagarArquivo() {
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
    }
}
