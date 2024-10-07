# Requisitos Não Funcionais

## RNF01 - Desempenhotags: #fiap #requisitos
## RNF01 - Desempenho

- A API deve ser capaz de processar solicitações de publicação e visualização de mensagens com uma latência máxima de
100ms sob condições normais de operação.

## RNF02 - Escalabilidade

- A API deve ser desenvolvida de forma a permitir fácil escalabilidade horizontal, podendo adicionar novos servidores
para lidar com o aumento do tráfego.
- Deve ser possível distribuir a carga de trabalho entre vários servidores ou instâncias de forma eficiente.

## RNF03 - Confiabilidade

- A API deve garantir uma disponibilidade mínima de 99,9%, ou seja, o sistema deve estar disponível para uso 99,9% do
tempo durante um ano.
- Em caso de falha, o sistema deve se recuperar automaticamente (failover) e continuar processando as solicitações sem
perda de dados.

## RNF04 - Manutenibilidade

- O código da API deve ser modular e seguir boas práticas de desenvolvimento para garantir fácil manutenção e
atualização.

## RNF05 - Documentação

- O sistema deve ser documentado adequadamente, tanto no nível de código (comentários) quanto nos serviços expostos
(documentação de API, como OpenAPI/Swagger).

## RNF06 - Segurança

- Mesmo sem autenticação para os usuários que publicam mensagens, a API deve ser protegida contra ataques comuns, como
injeção de SQL, Cross-Site Scripting (XSS) e Cross-Site Request Forgery (CSRF).
- O tráfego entre os clientes e a API deve ser criptografado usando HTTPS para garantir a confidencialidade dos dados
transmitidos.
- Deve haver mecanismos para limitar o número de requisições de um mesmo endereço IP (rate-limiting), evitando abusos e
sobrecarga do sistema.

## RNF07 - Capacidade de Manipulação de Erros

- A API deve fornecer mensagens de erro claras e padronizadas (por exemplo, usando códigos de status HTTP apropriados
como 400 para erros de requisição ou 500 para erros internos do servidor).
- Em caso de falha ao processar uma solicitação (por exemplo, erro de banco de dados), a API deve retornar uma resposta
clara e descritiva sobre o erro e tentar mitigar o impacto da falha.

## RNF08 - Log

- O sistema deve registrar logs detalhados de todas as operações, para fins de auditoria e monitoramento.

- A API deve ser capaz de processar solicitações de publicação e visualização de mensagens com uma latência máxima de
100ms sob condições normais de operação.

## RNF02 - Escalabilidade

- A API deve ser desenvolvida de forma a permitir fácil escalabilidade horizontal, podendo adicionar novos servidores
para lidar com o aumento do tráfego.
- Deve ser possível distribuir a carga de trabalho entre vários servidores ou instâncias de forma eficiente.

## RNF03 - Confiabilidade

- A API deve garantir uma disponibilidade mínima de 99,9%, ou seja, o sistema deve estar disponível para uso 99,9% do
tempo durante um ano.
- Em caso de falha, o sistema deve se recuperar automaticamente (failover) e continuar processando as solicitações sem
perda de dados.

## RNF04 - Manutenibilidade

- O código da API deve ser modular e seguir boas práticas de desenvolvimento para garantir fácil manutenção e
atualização.

## RNF05 - Documentação

- O sistema deve ser documentado adequadamente, tanto no nível de código (comentários) quanto nos serviços expostos
(documentação de API, como OpenAPI/Swagger).

## RNF06 - Segurança

- Mesmo sem autenticação para os usuários que publicam mensagens, a API deve ser protegida contra ataques comuns, como
injeção de SQL, Cross-Site Scripting (XSS) e Cross-Site Request Forgery (CSRF).
- O tráfego entre os clientes e a API deve ser criptografado usando HTTPS para garantir a confidencialidade dos dados
transmitidos.
- Deve haver mecanismos para limitar o número de requisições de um mesmo endereço IP (rate-limiting), evitando abusos e
sobrecarga do sistema.

## RNF07 - Capacidade de Manipulação de Erros

- A API deve fornecer mensagens de erro claras e padronizadas (por exemplo, usando códigos de status HTTP apropriados
como 400 para erros de requisição ou 500 para erros internos do servidor).
- Em caso de falha ao processar uma solicitação (por exemplo, erro de banco de dados), a API deve retornar uma resposta
clara e descritiva sobre o erro e tentar mitigar o impacto da falha.

## RNF08 - Log

- O sistema deve registrar logs detalhados de todas as operações, para fins de auditoria e monitoramento.