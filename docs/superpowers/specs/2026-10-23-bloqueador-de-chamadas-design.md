# Bloqueador de chamadas fora dos contatos

## Objetivo

Criar um app Android nativo que permita à pessoa filtrar chamadas de números que não estão nos contatos, escolhendo entre recusar a chamada automaticamente ou silenciar o toque.

## Escopo da primeira versão

- Tela principal com controle para ativar ou desativar o filtro.
- Opção de comportamento com duas alternativas: **Recusar automaticamente** e **Tocar em silêncio**.
- Persistência da preferência escolhida, inclusive após fechar ou reiniciar o app.
- Serviço Android `CallScreeningService` para aplicar o comportamento às chamadas encaminhadas pelo sistema.
- Orientação e solicitação para o usuário definir o app como serviço de filtragem de chamadas do Android.
- Quando o filtro estiver desligado, o serviço permite as chamadas sem alterar seu comportamento.
- Interface em português, com textos que expliquem que a configuração depende da escolha do app nas configurações do Android.

## Comportamento de filtragem

A filtragem se aplica a chamadas de números fora dos contatos. Com a opção de recusa, o serviço desautoriza e rejeita a chamada. Com a opção de silêncio, o serviço permite a chamada e instrui o Telecom a não tocar o toque; a chamada ainda pode aparecer no discador padrão. Chamadas de contatos não devem ser afetadas.

A implementação usará o encaminhamento de chamadas não salvas em contatos oferecido pela API de filtragem do Android. O app não precisa ler nem copiar a lista de contatos para decidir o caso padrão. Números privados, indisponíveis ou sem esquema telefônico não devem ser tratados como se fossem números desconhecidos comuns caso o Android não os encaminhe ao serviço.

## Arquitetura

- **Interface Android**: apresenta estado do filtro, escolha do comportamento e acesso ao fluxo de configuração do papel de filtragem.
- **Preferências locais**: guarda se o filtro está ativo e qual comportamento foi escolhido.
- **Serviço de filtragem**: lê as preferências no momento de cada chamada e responde usando `CallScreeningService.CallResponse`.
- **Manifesto**: declara o serviço com a permissão de binding e o intent filter exigidos pelo Android.

A escolha do app como serviço de filtragem é controlada pelo usuário através do mecanismo de papel do Android; o app não pode ativar esse papel silenciosamente. A interface deve mostrar se o papel está concedido e permitir abrir o fluxo de solicitação quando necessário.

## Plataforma e dependências

Projeto Android nativo em Kotlin, usando as APIs da plataforma e sem serviço remoto. A versão mínima será Android 10 (API 29), necessária para oferecer ambos os comportamentos com a API oficial. Embora a filtragem básica exista desde Android 7 (API 24), manter a versão mínima em 29 evita uma opção de silêncio que não possa ser cumprida em versões antigas.

## Fora do escopo

- Bloqueio por listas personalizadas, prefixos, spam ou regras de horário.
- Histórico próprio de chamadas filtradas.
- Sincronização, conta, backend ou envio de dados para terceiros.
- Substituir o app de telefone padrão ou gerenciar mensagens de voz.

## Critérios de aceite

1. A tela permite ativar e desativar o filtro e escolher qualquer um dos dois modos.
2. Preferências continuam disponíveis após reiniciar o processo do app.
3. Ao receber uma chamada filtrada com o modo de recusa ativo, o serviço responde para rejeitá-la.
4. Ao receber uma chamada filtrada com o modo de silêncio ativo, o serviço permite a chamada e silencia o toque.
5. Com o filtro desligado, a chamada é permitida sem silenciamento.
6. A tela comunica quando o app ainda não recebeu o papel de filtragem e oferece o fluxo do Android para solicitá-lo.
7. O comportamento indisponível em versões antigas do Android não é oferecido como se fosse compatível.

## Referências

- [Screen calls with a call screening service](https://developer.android.com/develop/connectivity/telecom/dialer-app/screen-calls)
- [CallScreeningService.CallResponse.Builder](https://developer.android.com/reference/android/telecom/CallScreeningService.CallResponse.Builder)
