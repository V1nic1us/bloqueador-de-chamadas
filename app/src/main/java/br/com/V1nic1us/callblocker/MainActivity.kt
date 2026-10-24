package br.com.V1nic1us.callblocker

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import br.com.V1nic1us.callblocker.theme.AppSpacing
import br.com.V1nic1us.callblocker.theme.BloqueadorDeChamadasTheme

class MainActivity : ComponentActivity() {
  private val preferences by lazy { CallFilterPreferences(this) }
  private val roleManager by lazy { getSystemService(RoleManager::class.java) }

  private var filterEnabled by mutableStateOf(false)
  private var selectedMode by mutableStateOf(Mode.SILENCE)
  private var roleAvailable by mutableStateOf(false)
  private var roleHeld by mutableStateOf(false)
  private var contactsPermissionDenied by mutableStateOf(false)

  private val requestContactsPermission =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (granted) {
        preferences.setEnabled(true)
        filterEnabled = true
        contactsPermissionDenied = false
      } else {
        preferences.setEnabled(false)
        filterEnabled = false
        contactsPermissionDenied = true
      }
    }

  private val requestRole =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      refreshRoleState()
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    filterEnabled = preferences.isEnabled()
    selectedMode = preferences.mode()
    refreshRoleState()

    enableEdgeToEdge()
    setContent {
      BloqueadorDeChamadasTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          SettingsScreen(
            filterEnabled = filterEnabled,
            selectedMode = selectedMode,
            roleAvailable = roleAvailable,
            roleHeld = roleHeld,
            contactsPermissionDenied = contactsPermissionDenied,
            onFilterEnabledChange = { enabled ->
              if (enabled && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
              } else {
                preferences.setEnabled(enabled)
                filterEnabled = enabled
                contactsPermissionDenied = false
              }
            },
            onModeChange = { mode ->
              preferences.setMode(mode)
              selectedMode = mode
            },
            onRequestRole = {
              if (roleAvailable && !roleHeld) {
                requestRole.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
              }
            },
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    refreshRoleState()
  }

  private fun refreshRoleState() {
    roleAvailable = roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)
    roleHeld = roleAvailable && roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
  }
}

@Composable
private fun SettingsScreen(
  filterEnabled: Boolean,
  selectedMode: Mode,
  roleAvailable: Boolean,
  roleHeld: Boolean,
  contactsPermissionDenied: Boolean,
  onFilterEnabledChange: (Boolean) -> Unit,
  onModeChange: (Mode) -> Unit,
  onRequestRole: () -> Unit,
) {
  Column(
    modifier =
      Modifier.fillMaxSize()
        .safeDrawingPadding()
        .verticalScroll(rememberScrollState())
        .padding(AppSpacing.large),
    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
  ) {
    Text("Bloqueador de chamadas", style = MaterialTheme.typography.headlineLarge)

    FilterControlCard(filterEnabled, contactsPermissionDenied, onFilterEnabledChange)
    TreatmentOptionsCard(filterEnabled, selectedMode, onModeChange)
    ScreeningRoleCard(roleAvailable, roleHeld, onRequestRole)
  }
}

@Composable
private fun FilterControlCard(
  filterEnabled: Boolean,
  contactsPermissionDenied: Boolean,
  onFilterEnabledChange: (Boolean) -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
  ) {
    Column(
      modifier = Modifier.padding(AppSpacing.large),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
    ) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .toggleable(
              value = filterEnabled,
              role = Role.Switch,
              onValueChange = onFilterEnabledChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
      ) {
        Text(
          "Filtrar chamadas fora dos contatos",
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = filterEnabled, onCheckedChange = null)
      }
      Text(
        "Ao ativar, o app aplica a opção abaixo às chamadas encaminhadas pelo Android.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      if (contactsPermissionDenied) {
        Text(
          "Permita o acesso aos contatos para ativar o filtro.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

@Composable
private fun TreatmentOptionsCard(
  filterEnabled: Boolean,
  selectedMode: Mode,
  onModeChange: (Mode) -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
  ) {
    Column(
      modifier = Modifier.padding(AppSpacing.large),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
    ) {
      Text("O que fazer com essas chamadas", style = MaterialTheme.typography.titleLarge)
      ModeChoice(
        label = "Recusar automaticamente",
        mode = Mode.REJECT,
        selectedMode = selectedMode,
        enabled = filterEnabled,
        onModeChange = onModeChange,
      )
      ModeChoice(
        label = "Tocar em silêncio",
        mode = Mode.SILENCE,
        selectedMode = selectedMode,
        enabled = filterEnabled,
        onModeChange = onModeChange,
      )
    }
  }
}

@Composable
private fun ScreeningRoleCard(
  roleAvailable: Boolean,
  roleHeld: Boolean,
  onRequestRole: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
  ) {
    Column(
      modifier = Modifier.padding(AppSpacing.large),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
    ) {
      Text("Serviço de filtragem do Android", style = MaterialTheme.typography.titleLarge)
      Text(
        when {
          roleHeld -> "Este app está definido como serviço de filtragem de chamadas."
          roleAvailable -> "Para o filtro funcionar, escolha este app como serviço de filtragem de chamadas do Android."
          else -> "O serviço de filtragem de chamadas não está disponível neste dispositivo."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = if (roleHeld) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      )
      if (roleAvailable && !roleHeld) {
        Button(onClick = onRequestRole, modifier = Modifier.fillMaxWidth()) {
          Text("Definir como app de filtragem")
        }
      }
    }
  }
}

@Composable
private fun ModeChoice(
  label: String,
  mode: Mode,
  selectedMode: Mode,
  enabled: Boolean,
  onModeChange: (Mode) -> Unit,
) {
  val optionShape = MaterialTheme.shapes.medium
  val optionContainer =
    if (enabled && selectedMode == mode) {
      MaterialTheme.colorScheme.primaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceContainerLow
    }

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(optionShape)
        .background(optionContainer, optionShape)
        .selectable(
          selected = selectedMode == mode,
          enabled = enabled,
          role = Role.RadioButton,
          onClick = { onModeChange(mode) },
        )
        .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
      selected = selectedMode == mode,
      onClick = null,
      enabled = enabled,
    )
    Text(
      text = label,
      modifier = Modifier.padding(start = AppSpacing.small),
      color = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        selectedMode == mode -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
      },
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}
