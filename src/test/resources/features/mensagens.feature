# language: pt
Funcionalidade: API Rest - Mensagens

  Cenário: Registrar uma nova mensagem
    Quando submeter uma nova mensagem
    Então a mensagem é registrada com sucesso

  Cenário: Buscar uma mensagem existente
    Dado que uma mensagem já foi registrada
    Quando buscar a mensagem registrada
    Então a mensagem é exibida com sucesso

  Cenário: Remover mensagem existente
    Dado que uma mensagem já foi registrada
    Quando requisitar a remoção da mensagem
    Então a mensagem é removida com sucesso
