# Proposta: sistema visual Material 3

## Intenção

Dar ao bloqueador de chamadas uma aparência Material 3 coerente, acessível e adaptável ao tema claro/escuro do Android, mantendo os controles e textos atuais em português.

## Abordagens consideradas

1. **Sistema adaptativo completo (recomendado):** definir papéis de cor para esquemas claro/escuro, manter suporte a cores dinâmicas do Android 12+, completar a escala tipográfica e criar tokens de forma e espaçamento. Aplicar esses tokens a cartões de configuração, estado do filtro e seção do serviço do Android.
2. **Organização mínima:** manter o visual e os componentes atuais, apenas substituir as cores roxas de fallback e organizar tipografia, formas e espaçamentos para uso futuro.

## Proposta padrão

Seguir a abordagem 1. Ela aproveita o Material 3 e as cores dinâmicas já presentes no projeto, dá um resultado visível agora e mantém uma identidade consistente quando as cores dinâmicas não estiverem disponíveis. O tema escuro deve usar os mesmos papéis semânticos do claro, com contraste adequado.

## Escopo

- Definir uma paleta fallback azul-petróleo/índigo apropriada ao app, com variantes claras e escuras e papéis semânticos de superfície, conteúdo, destaque e erro.
- Continuar usando cores dinâmicas em Android 12+; usar a paleta do app nas versões sem suporte ou quando o recurso estiver desativado.
- Completar os estilos tipográficos Material 3 e definir formas e espaçamentos compartilhados.
- Reorganizar a tela atual em superfícies Material 3 com hierarquia visual clara: estado/controle principal, escolha de tratamento de chamadas e status do serviço do Android.
- Preservar textos, regras, persistência e ações existentes.

## Fora do escopo

- Novas funcionalidades, navegação ou alterações no comportamento do bloqueador.
- Fontes ou bibliotecas externas.
- Mudanças na identidade ou no ícone do aplicativo.

## Restrições e chamadas de implementação

- Implementar em Compose com os componentes Material 3 já declarados no projeto.
- Manter preferências e lógica de chamada fora das mudanças visuais.
- Garantir leitura em tamanhos de fonte ampliados e nos temas claro e escuro.
- Usar ADB no emulador para conferir a tela após a implementação.
