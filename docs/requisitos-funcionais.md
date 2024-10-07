# Requisitos Funcionais

## RF01 - Criação de Mensagem

- O sistema deve permitir que qualquer usuário publique uma mensagem com:
    - O nome ou identificador do usuário.
    - O conteúdo da mensagem.
    - A data e hora de criação da mensagem, gerada automaticamente.

## RF02 - Validação do Conteúdo da Mensagem

- O sistema deve garantir que o conteúdo da mensagem:
    - Não seja vazio.
    - Respeite um limite máximo de caracteres = 280 caracteres

## RF03 - Contador de “Gostei”

- O sistema deve permitir que qualquer usuário visualize e interaja com o contador de “gostei” de uma mensagem.
- O contador deve começar com o valor 0.
- O contador deve ser incrementado em 1 cada vez que um usuário clicar no botão “gostei”.
- Um usuário pode incrementar o gostei apenas uma vez.

## RF04 - Visualização de Mensagens

- O sistema deve permitir que qualquer usuário visualize todas as mensagens publicadas, exibindo:
    - A data e hora de criação.
    - O nome ou identificador do usuário.
    - O conteúdo da mensagem.
    - O número atual de “gostei”.

## RF05 - Listagem de Mensagens

- O sistema deve permitir que os usuários listem todas as mensagens em ordem cronológica, com as mais recentes
aparecendo primeiro.

## RF06 - Recuperação de Mensagens Específicas

- O sistema deve permitir que um usuário recupere uma mensagem específica utilizando um identificador único

## RF07 - Atualização do Contador de “Gostei”

- O sistema deve permitir que o contador de “gostei” de uma mensagem seja atualizado em tempo real após cada interação
de um usuário.

## RF08 - Persistência de Dados

- O sistema deve armazenar todas as mensagens e contadores de “gostei” em um banco de dados para garantir que os dados 
sejam persistidos mesmo após reinicialização do sistema.

## RF09 - Edição de Mensagens

- O sistema pode permitir que o autor de uma mensagem edite o conteúdo da mensagem após a publicação.

## RF10 - Exclusão de Mensagens

- O sistema pode permitir que o autor ou um administrador exclua mensagens publicadas.