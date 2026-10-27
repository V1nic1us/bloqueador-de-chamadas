# Bloqueador de Chamadas

Aplicativo Android que filtra chamadas recebidas de números que não estão nos contatos do usuário. Números salvos nos contatos são permitidos; números desconhecidos podem ser rejeitados ou silenciados conforme a configuração escolhida.

## Requisitos

- Android Studio recente com suporte ao Android SDK 36.
- JDK 17.
- Android SDK Platform 36 e Build Tools compatíveis.
- Dispositivo ou emulador com Android 10/API 29 ou superior para instalação e testes instrumentados.
- `adb` disponível no `PATH` para instalar o APK diretamente em um dispositivo.

O projeto usa o Gradle Wrapper, portanto não é necessário instalar o Gradle separadamente.

## Principais dependências

As versões completas ficam em [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

- Kotlin `2.3.20`.
- Android Gradle Plugin `9.0.1`.
- Jetpack Compose com BOM `2026.03.01`.
- Material 3, Activity Compose e AndroidX Core KTX.
- Lifecycle Compose, ViewModel Compose e Navigation 3.
- Kotlin Coroutines Test e JUnit `4.13.2`.
- AndroidX Test, JUnit4 e Espresso para testes instrumentados.

Configuração Android do aplicativo:

- `applicationId`: `br.com.V1nic1us.callblocker`.
- `compileSdk`: `36`.
- `targetSdk`: `36`.
- `minSdk`: `29`.
- `versionName`: `1.0.0`.

## Clonar o projeto

```bash
git clone https://github.com/V1nic1us/bloqueador-de-chamadas.git
cd bloqueador-de-chamadas
```

No Linux/macOS, dê permissão de execução ao Gradle Wrapper se necessário:

```bash
chmod +x gradlew
```

## Build

Para gerar o APK de debug:

```bash
./gradlew assembleDebug
```

O APK será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Para executar uma verificação comum de testes e build:

```bash
./gradlew test assembleDebug
```

Para gerar uma versão de release:

```bash
./gradlew assembleRelease
```

O build de release não está configurado para assinatura de distribuição; para publicar o aplicativo, configure uma chave de assinatura apropriada.

### Deploy pelo GitHub Actions

O workflow de release não é acionado por `push` de tags. Ele executa somente quando uma pull request é fechada com merge na branch `main`:

```yaml
pull_request:
  branches:
    - main
  types:
    - closed
```

Depois de confirmar o merge da PR, o GitHub Actions busca a última tag semântica (`vMAJOR.MINOR.PATCH`), incrementa o número do patch e publica a próxima versão (`v1.0.1`, depois `v1.0.2`, e assim por diante) com o APK de debug. O workflow é ignorado quando a PR é fechada sem merge.

## Instalar no dispositivo ou emulador

1. Ative as opções de desenvolvedor e a depuração USB no dispositivo, ou inicie um emulador com Android 10/API 29 ou superior.
2. Confirme que o dispositivo está autorizado:

   ```bash
   adb devices
   ```

3. Instale o APK de debug diretamente:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

   Alternativamente, compile e instale com o Gradle:

   ```bash
   ./gradlew installDebug
   ```

4. Abra o aplicativo e conceda a permissão de leitura dos contatos.
5. Ative o aplicativo como serviço de triagem de chamadas nas configurações exibidas pelo aplicativo.
6. Escolha se chamadas de números desconhecidos devem ser rejeitadas ou silenciadas.

Para remover a instalação via `adb`:

```bash
adb uninstall br.com.V1nic1us.callblocker
```

## Testes

### Testes unitários JVM

Executa os testes de regras de filtragem, preferências e lógica independente do Android:

```bash
./gradlew test
```

O relatório HTML fica em:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

### Testes instrumentados

Com um emulador ou dispositivo autorizado conectado:

```bash
./gradlew connectedDebugAndroidTest
```

Esses testes cobrem a interface, preferências, leitura de contatos e fluxo de permissões. O relatório fica em:

```text
app/build/reports/androidTests/connected/debug/index.html
```

Verifique antes se há um dispositivo autorizado:

```bash
adb devices
```

Um dispositivo com status `unauthorized` precisa aceitar a solicitação de depuração USB antes da execução.

## Funcionalidades e permissões

- O aplicativo usa `READ_CONTACTS` para identificar números salvos na agenda.
- O serviço usa a API `CallScreeningService` do Android.
- É necessário conceder o papel de serviço de triagem de chamadas ao aplicativo.
- Chamadas de contatos conhecidos são permitidas quando o filtro está ativo.
- Se a permissão de contatos for negada, o filtro não é ativado.
- A chamada real em um dispositivo físico deve ser validada manualmente; ela não é substituída pelos testes automatizados.

## Estrutura principal

- `app/src/main/java/br/com/V1nic1us/callblocker/`: código Kotlin da aplicação.
- `app/src/main/res/`: manifesto, temas, strings e recursos Android.
- `app/src/test/`: testes unitários JVM.
- `app/src/androidTest/`: testes instrumentados em dispositivo/emulador.
- `docs/superpowers/`: especificações e planos de implementação.
