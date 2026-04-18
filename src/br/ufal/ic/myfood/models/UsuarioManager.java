package br.ufal.ic.myfood.models;

import br.ufal.ic.myfood.exceptions.UsuarioJaExisteException;
import br.ufal.ic.myfood.exceptions.UsuarioNaoExisteException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia todos os usuários do sistema.
 *
 * Responsabilidades:
 *   - Criação de usuários (cliente e dono de empresa) com validações
 *   - Autenticação via login
 *   - Consulta de atributos por ID
 *   - Busca de usuário por ID (usado pelos outros managers)
 *
 * Serializable para persistir junto ao SystemState.
 */
public class UsuarioManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Usuario> usuarioList;

    public UsuarioManager() {
        this.usuarioList = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria um novo usuário com validações completas.
     *
     * Validações (conforme us1_1.txt):
     *   - nome, email, senha, endereco não podem ser nulos/vazios
     *   - email deve conter "@" (formato mínimo)
     *   - email deve ser único no sistema
     *   - cpf, quando fornecido, deve ter exatamente 14 caracteres (XXX.XXX.XXX-XX)
     */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String cpf) throws Exception {

        // Validações de campos obrigatórios
        if (nome     == null || nome.isEmpty())     throw new Exception("Nome invalido");
        if (email    == null || email.isEmpty())    throw new Exception("Email invalido");
        if (senha    == null || senha.isEmpty())    throw new Exception("Senha invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco invalido");

        // Validação de formato de email — deve conter "@"
        if (!email.contains("@")) throw new Exception("Email invalido");

        // Validação de CPF quando fornecido (usuário do tipo dono de empresa)
        // Formato esperado: XXX.XXX.XXX-XX = 14 caracteres
        if (cpf != null && !cpf.isEmpty() && cpf.length() != 14) {
            throw new Exception("CPF invalido");
        }

        // Email deve ser único entre todos os usuários
        for (Usuario u : usuarioList) {
            if (u.getEmail().equals(email)) throw new UsuarioJaExisteException();
        }

        usuarioList.add(new Usuario(nome, email, senha, endereco, cpf));
    }

    // -------------------------------------------------------------------------
    // AUTENTICAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Autentica um usuário pelo email e senha.
     *
     * @return ID (UUID) do usuário autenticado
     * @throws Exception com mensagem "Login ou senha invalidos" em qualquer falha
     */
    public String login(String email, String senha) throws Exception {
        // Campos vazios já são credenciais inválidas
        if (email == null || email.isEmpty()) throw new Exception("Login ou senha invalidos");
        if (senha == null || senha.isEmpty()) throw new Exception("Login ou senha invalidos");

        for (Usuario u : usuarioList) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u.getId();
            }
        }
        throw new Exception("Login ou senha invalidos");
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor de um atributo de um usuário pelo seu ID.
     *
     * Atributos suportados: nome, email, senha, endereco, cpf
     */
    public String getAtributoUsuario(String id, String atributo) throws Exception {
        for (Usuario u : usuarioList) {
            if (u.getId().equals(id)) {
                switch (atributo.toLowerCase()) {
                    case "nome":     return u.getNome();
                    case "email":    return u.getEmail();
                    case "senha":    return u.getSenha();
                    case "endereco": return u.getEndereco();
                    case "cpf":      return u.getCpf();
                    default:         throw new Exception("Atributo invalido");
                }
            }
        }
        throw new UsuarioNaoExisteException();
    }

    /**
     * Busca e retorna um usuário pelo seu ID.
     * Retorna null se não encontrado.
     * Usado pelos outros managers para resolver referências ID → objeto.
     */
    public Usuario buscarPorId(String id) {
        return usuarioList.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
