package com.example.bloqueadordechamadas

import android.app.role.RoleManager
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
import androidx.compose.ui.unit.dp
import com.example.bloqueadordechamadas.theme.BloqueadorDeChamadasTheme

class MainActivity : ComponentActivity() {
  private val preferences by lazy { CallFilterPreferences(this) }
  private val roleManager by lazy { getSystemService(RoleManager::class.java) }

  private var filterEnabled by mutableStateOf(false)
  private var selectedMode by mutableStateOf(Mode.SILENCE)
  private var roleAvailable by mutableStateOf(false)
  private var roleHeld by mutableStateOf(false)

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
            onFilterEnabledChange = { enabled ->
              preferences.setEnabled(enabled)
              filterEnabled = enabled
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
  onFilterEnabledChange: (Boolean) -> Unit,
  onModeChange: (Mode) -> Unit,
  onRequestRole: () -> Unit,
) {
  Column(
    modifier =
      Modifier.fillMaxSize()
        .safeDrawingPadding()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    Text("Bloqueador de chamadas", style = MaterialTheme.typography.headlineMedium)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .toggleable(
              value = filterEnabled,
              role = Role.Switch,
              onValueChange = onFilterEnabledChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          "Filtrar chamadas fora dos contatos",
          modifier = Modifier.weight(1f).padding(end = 12.dp),
          style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = filterEnabled, onCheckedChange = null)
      }
      Text(
        "Ao ativar, o app aplica a opção abaixo às chamadas encaminhadas pelo Android.",
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("O que fazer com essas chamadas", style = MaterialTheme.typography.titleMedium)
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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text("Serviço de filtragem do Android", style = MaterialTheme.typography.titleMedium)
      Text(
        when {
          roleHeld -> "Este app está definido como serviço de filtragem de chamadas."
          roleAvailable -> "Para o filtro funcionar, escolha este app como serviço de filtragem de chamadas do Android."
          else -> "O serviço de filtragem de chamadas não está disponível neste dispositivo."
        },
        style = MaterialTheme.typography.bodyMedium,
      )
      if (roleAvailable && !roleHeld) {
        Button(onClick = onRequestRole) { Text("Definir como app de filtragem") }
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
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .selectable(
          selected = selectedMode == mode,
          enabled = enabled,
          role = Role.RadioButton,
          onClick = { onModeChange(mode) },
        )
        .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
      selected = selectedMode == mode,
      onClick = null,
      enabled = enabled,
    )
    Text(
      text = label,
      modifier = Modifier.padding(start = 8.dp),
      color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}
