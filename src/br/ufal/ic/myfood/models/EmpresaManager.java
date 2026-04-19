package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Empresas.
 *
 * Responsabilidades:
 *   - Criação de empresas com validações de duplicidade e permissão
 *   - Consulta de empresas por dono, nome e índice
 *   - Consulta de atributos de uma empresa por ID
 *   - Busca por ID (usada pelos outros managers)
 *
 * Serializable para persistir junto ao SystemState.
 */
public class EmpresaManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Empresa> empresas;

    // IDs gerados sequencialmente a partir de 1
    private int proximoId;

    public EmpresaManager() {
        this.empresas   = new ArrayList<>();
        this.proximoId  = 1;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria uma nova empresa para um dono.
     *
     * Validações (conforme us2_1.txt):
     *   - Usuário deve ter CPF (ser dono de empresa)
     *   - Nenhum outro dono pode ter empresa com o mesmo nome
     *   - O mesmo dono não pode ter empresa com mesmo nome E mesmo endereço
     *
     * @return ID sequencial da empresa criada
     */
    public int criarEmpresa(String tipoEmpresa, String donoId, String nome,
                            String endereco, String tipoCozinha,
                            Usuario dono) throws Exception {

        // Apenas donos (com CPF) podem criar empresas
        if (dono.getCpf() == null || dono.getCpf().isEmpty()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        for (Empresa e : empresas) {
            // Outro dono com o mesmo nome → proibido globalmente
            if (e.getNome().equals(nome) && !e.getDonoId().equals(donoId)) {
                throw new Exception("Empresa com esse nome ja existe");
            }
            // Mesmo dono, mesmo nome, mesmo endereço → proibido
            if (e.getNome().equals(nome) && e.getDonoId().equals(donoId)
                    && e.getEndereco().equals(endereco)) {
                throw new Exception("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        }

        empresas.add(new Empresa(proximoId, nome, endereco, tipoCozinha, donoId));
        return proximoId++;
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Retorna todas as empresas de um dono no formato:
     * {[[nome1, end1], [nome2, end2]]}
     *
     * Lança exceção se o usuário não for dono (sem CPF).
     */
    public String getEmpresasDoUsuario(String donoId, Usuario dono) throws Exception {
        if (dono.getCpf() == null || dono.getCpf().isEmpty()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        List<Empresa> dosDono = empresas.stream()
                .filter(e -> e.getDonoId().equals(donoId))
                .collect(Collectors.toList());

        // Monta string no formato exigido pelos testes EasyAccept
        StringBuilder sb = new StringBuilder("{[[");
        for (int i = 0; i < dosDono.size(); i++) {
            sb.append("[")
              .append(dosDono.get(i).getNome())
              .append(", ")
              .append(dosDono.get(i).getEndereco())
              .append("]");
            if (i < dosDono.size() - 1) sb.append(", ");
        }
        sb.append("]]}");
        return sb.toString();
    }

    /**
     * Retorna o ID de uma empresa do dono pelo nome e índice.
     *
     * Útil para donos com múltiplas empresas de mesmo nome em endereços diferentes.
     * Índice 0 = primeira cadastrada com aquele nome.
     */
    public int getIdEmpresa(String donoId, String nome, int indice) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (indice < 0)                     throw new Exception("Indice invalido");

        List<Empresa> encontradas = empresas.stream()
                .filter(e -> e.getDonoId().equals(donoId) && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (encontradas.isEmpty())            throw new Exception("Nao existe empresa com esse nome");
        if (indice >= encontradas.size())     throw new Exception("Indice maior que o esperado");

        return encontradas.get(indice).getId();
    }

    /**
     * Retorna o valor de um atributo de uma empresa pelo ID.
     *
     * Atributos suportados: nome, endereco, tipoCozinha, dono
     * O atributo "dono" retorna o NOME do usuário dono (resolve via UsuarioManager).
     */
    public String getAtributoEmpresa(int empresaId, String atributo,
                                     UsuarioManager usuarioManager) throws Exception {

        //verifica se empresa existe primeiro -> corrigindo erro  
        Empresa empresa = buscarPorId(empresaId);
        if (empresa == null) throw new Exception("Empresa nao cadastrada");
                                
        // Atributo vazio → inválido antes de qualquer outra verificação
        if (atributo == null || atributo.isEmpty()) throw new Exception("Atributo invalido");

       
        switch (atributo.toLowerCase()) {
            case "nome":        return empresa.getNome();
            case "endereco":    return empresa.getEndereco();
            case "tipocozinha": return empresa.getTipoCozinha();
            case "dono":
                // Resolve ID → nome do usuário dono
                Usuario dono = usuarioManager.buscarPorId(empresa.getDonoId());
                return dono != null ? dono.getNome() : "";
            default:
                throw new Exception("Atributo invalido");
        }
    }

    /**
     * Busca e retorna uma empresa pelo ID.
     * Retorna null se não encontrada.
     * Usado pelos outros managers para validar existência.
     */
    public Empresa buscarPorId(int id) {
        return empresas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
               .orElse(null);
               ;
    }
}
