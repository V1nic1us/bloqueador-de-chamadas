# Contact Exceptions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Liberar chamadas de contatos salvos quando o filtro de chamadas estiver ativo.

**Architecture:** A política pura receberá o resultado da consulta de contato (`isKnownContact`) e manterá a decisão determinística. O serviço fará a consulta via `ContactsContract`; a atividade solicitará `READ_CONTACTS` antes de ativar o filtro e exibirá uma orientação quando a permissão for negada.

**Tech Stack:** Kotlin, Android Telecom `CallScreeningService`, `ContactsContract`, Jetpack Compose, JUnit.

**Spec:** `docs/superpowers/specs/2026-10-24-contact-exceptions-design.md`

## Global Constraints

- Não bloquear contatos salvos quando o filtro estiver ativo.
- Chamadas desconhecidas continuam usando o modo selecionado.
- Sem permissão de contatos, não liberar chamadas não verificáveis.
- Não alterar o comportamento de chamadas de saída, handles não telefônicos ou endereços vazios.

## Review Focus

- Contato salvo com formatação diferente: testar comparação normalizada.
- Número desconhecido: testar manutenção de `REJECT` e `SILENCE`.
- Permissão ausente: testar fallback seguro.
- Chamada sem handle ou não telefônica: testar `ALLOW`.
- Desativação do filtro: testar `ALLOW` sem consulta de contato.

### Task 1: Política de triagem

**Files:**
- Modify: `app/src/main/java/br/com/V1nic1us/callblocker/CallScreeningPolicy.kt`
- Test: `app/src/test/java/br/com/V1nic1us/callblocker/CallScreeningPolicyTest.kt`

**Interfaces:**
- Produces `CallScreeningPolicy.evaluate(..., isKnownContact: Boolean)` with default `false` for callers that do not have contact data.

- [ ] Escrever testes falhando para permitir contato conhecido e manter desconhecido bloqueado/silenciado.
- [ ] Executar `./gradlew test` e confirmar falha pelos novos casos.
- [ ] Implementar o curto-circuito `ALLOW` para `isKnownContact` quando o filtro estiver ativo.
- [ ] Executar os testes unitários e confirmar todos passando.

### Task 2: Consulta de contatos e permissão

**Files:**
- Create: `app/src/main/java/br/com/V1nic1us/callblocker/ContactLookup.kt`
- Modify: `app/src/main/java/br/com/V1nic1us/callblocker/CallScreeningService.kt`
- Modify: `app/src/main/java/br/com/V1nic1us/callblocker/MainActivity.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Test: `app/src/androidTest/java/br/com/V1nic1us/callblocker/ContactLookupInstrumentedTest.kt`

**Interfaces:**
- `ContactLookup(context: Context).contains(phoneNumber: String): Boolean`.
- `CallScreeningService` passa `contains(address)` à política apenas para chamadas telefônicas recebidas.

- [ ] Declarar `android.permission.READ_CONTACTS` no manifesto.
- [ ] Criar teste instrumentado para número salvo, número ausente e número formatado.
- [ ] Implementar consulta com `PhoneLookup.CONTENT_FILTER_URI`, retornando `false` em caso de permissão ausente/erro.
- [ ] Solicitar a permissão antes de ativar o filtro; se negada, desfazer a ativação e mostrar orientação na tela.
- [ ] Executar testes instrumentados e confirmar comportamento da permissão.

### Task 3: Verificação completa

- [ ] Executar `./gradlew test`.
- [ ] Executar `./gradlew assembleDebug`.
- [ ] Revisar `git diff` e confirmar que apenas o fluxo de contatos foi alterado.
