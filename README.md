Relatório de Projeto: MyFood
Aluna: Rayssa Rodrigues Pedro da Silva
Disciplina: Programação 2 (P2)
1. Visão Geral
O MyFood é um sistema de gerenciamento de delivery de comida que conecta usuários (Clientes e Donos de Empresa) a estabelecimentos comerciais. O projeto foi focado na robustez da lógica de negócio e na validação rigorosa através de testes de aceitação.
2. Tecnologias Utilizadas
Linguagem: Java 8+
Testes: EasyAccept (Testes de aceitação via scripts .txt)
Persistência: Serialização de objetos em Java (arquivo system_state.dat).
Arquitetura: * Padrão Facade: Centraliza a comunicação com os testes.
Managers Especializados: O sistema é dividido em UsuarioManager, EmpresaManager, ProdutoManager e PedidoManager para melhor organização do código.
3. Funcionalidades Implementadas
US1 (Usuários): Cadastro de Clientes e Donos de Empresa, validação de emails únicos e sistema de login.
US2 (Empresas): Criação de restaurantes/mercados, garantindo que não existam duplicatas de nomes em um mesmo local e validando permissões de dono.
US3 (Produtos): Gestão de cardápio com suporte a diferentes categorias e atributos monetários.
US4 (Pedidos): Sistema de carrinho de compras que permite adicionar/remover produtos, calcular o valor total automaticamente e gerenciar o estado do pedido ("aberto" para "preparando").
4. Como Executar os Testes (Via Terminal)
Para rodar o projeto e validar as funcionalidades, siga os comandos abaixo no terminal (PowerShell ou CMD):
Passo 1: Compilação Total
Este comando compila todas as classes, incluindo modelos, exceções e a fachada principal:
PowerShell
javac -cp "lib/easyaccept.jar;." -d . src/br/ufal/ic/myfood/*.java src/br/ufal/ic/myfood/models/*.java src/br/ufal/ic/myfood/exceptions/*.java

Passo 2: Execução de uma User Story específica
Para rodar um teste individual (exemplo US1.1):
PowerShell
java -cp "lib/easyaccept.jar;." easyaccept.EasyAccept br.ufal.ic.myfood.Facade tests/us1_1.txt

Passo 3: Rodar todos os testes em sequência
Para verificar o projeto completo de uma vez:
PowerShell
Get-ChildItem tests/*.txt | ForEach-Object { java -cp "lib/easyaccept.jar;." br.ufal.ic.myfood.Main "tests/$($_.Name)" }

Talvez seja necessário deixar em comentário os arquivos ativos na main, e ativar o codigo que esta em comentário para rodar testes em sequência

5. Notas de Implementação (Destaques Técnicos)
Formatação Decimal: Foi utilizado java.util.Locale.US em todos os retornos de valores monetários para garantir que o sistema utilize o ponto (.) como separador decimal, independentemente da configuração regional do computador.
Hierarquia de Erros: As validações de existência de objetos (como IDs de empresas e pedidos) foram priorizadas em relação às validações de atributos, conforme exigido pelos roteiros de teste.
Limpeza de Estado: Para garantir testes limpos, o método zerarSistema apaga o arquivo de persistência, permitindo uma execução do zero.
