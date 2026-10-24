# Exceção para contatos salvos

## Objetivo

Quando o filtro estiver ativo, chamadas de números salvos nos contatos devem ser permitidas. Apenas chamadas recebidas de números que não estejam nos contatos devem seguir o modo escolhido pelo usuário: recusar ou silenciar.

## Escopo

- Adicionar leitura de contatos com `READ_CONTACTS`.
- Solicitar a permissão na tela principal antes de ativar o filtro.
- Consultar o catálogo de contatos no momento da triagem da chamada.
- Normalizar números antes da comparação para evitar diferenças de formatação.
- Manter o bloqueio seguro quando a permissão não estiver concedida: chamadas não verificáveis continuam sendo tratadas como fora dos contatos.

## Fora do escopo

- Criar uma lista manual de números permitidos.
- Alterar os modos de recusar ou silenciar.
- Sincronizar contatos ou armazenar cópias locais deles.

## Abordagens consideradas

1. Consultar `ContactsContract` no serviço: escolhido porque usa a agenda real do aparelho e atende diretamente ao comportamento esperado.
2. Confiar em metadados de contato entregues por `Call.Details`: descartado porque a disponibilidade varia entre versões e fabricantes.
3. Manter uma lista própria no app: descartado porque não acompanha automaticamente os contatos do usuário.

## Critério de sucesso

Com o filtro ativo, uma chamada recebida de um contato salvo retorna `ALLOW`; uma chamada de número desconhecido mantém `REJECT` ou `SILENCE`, conforme o modo configurado. Sem permissão de contatos, o app não libera chamadas que não consegue verificar.
