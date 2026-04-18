package br.ufal.ic.myfood;

import easyaccept.EasyAccept;

/**
 * Ponto de entrada alternativo — delega para Facade.main().
 * O EasyAccept também pode ser chamado diretamente por aqui.
 */
public class Main {
    public static void main(String[] args) {
        // US1 - Usuários
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_2.txt"});

        // US2 - Empresas
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_2.txt"});

        // US3 - Produtos
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_2.txt"});

        // US4 - Pedidos
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_2.txt"});
    }
}
