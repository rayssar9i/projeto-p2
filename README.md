**MyFood - Sistema de Delivery (P2)**

Este repositório contém o projeto MyFood, um sistema de gerenciamento de delivery desenvolvido para a disciplina de Programação 2 (P2) no Instituto de Computação (IC-UFAL).

O sistema conecta Clientes e Donos de Empresa a estabelecimentos comerciais, com foco em uma lógica de negócio robusta e validação rigorosa através de testes de aceitação.

🚀 Como Executar o Projeto
Certifique-se de estar na raiz do projeto e que o arquivo lib/easyaccept.jar esteja presente. Utilize o terminal (PowerShell ou CMD) para os comandos abaixo:

1. Compilação Total
Compila todas as classes do sistema (modelos, exceções e fachada):

PowerShell
javac -cp "lib/easyaccept.jar;." -d . src/br/ufal/ic/myfood/*.java src/br/ufal/ic/myfood/models/*.java src/br/ufal/ic/myfood/exceptions/*.java


2. Execução de uma User Story Específica
Para validar uma funcionalidade individual (exemplo: US1.1):

PowerShell

	java -cp "lib/easyaccept.jar;." easyaccept.EasyAccept br.ufal.ic.myfood.Facade tests/us1_1.txt
	
3. Execução em Lote (Todos os Testes)
Para verificar o projeto completo de uma só vez:

PowerShell
Get-ChildItem tests/*.txt | ForEach-Object { java -cp "lib/easyaccept.jar;." br.ufal.ic.myfood.Main "tests/$($_.Name)" }
[!IMPORTANT]
Nota sobre a Classe Main: Para rodar os testes em sequência (Passo 3), verifique se a classe Main está configurada para receber os argumentos dos testes. Caso o programa não execute como esperado, certifique-se de descomentar o bloco de código específico para automação de testes dentro de src/br/ufal/ic/myfood/Main.java.

🛠️ Tecnologias e Conceitos
Linguagem: Java

Validação: EasyAccept (Testes de Aceitação)

Padrões: Fachada (Facade)

Instituição: UFAL - Instituto de Computação
