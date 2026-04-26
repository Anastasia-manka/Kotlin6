package com.example.kt6_7.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kt6_7.di.AppModule
import com.example.kt6_7.presentation.viewmodel.HeartRateUiState
import com.example.kt6_7.presentation.viewmodel.HeartRateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateMonitorScreen(
    navController: NavController,
    deviceAddress: String
) {
    val context = LocalContext.current

    // Инициализация DI
    LaunchedEffect(Unit) {
        AppModule.init(context)
    }

    val connectToDeviceUseCase = remember { AppModule.provideConnectToDeviceUseCase() }
    val disconnectDeviceUseCase = remember { AppModule.provideDisconnectDeviceUseCase() }
    val subscribeToHeartRateUseCase = remember { AppModule.provideSubscribeToHeartRateUseCase() }

    val viewModel: HeartRateViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HeartRateViewModel(
                    connectToDeviceUseCase,
                    disconnectDeviceUseCase,
                    subscribeToHeartRateUseCase
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.connectToDevice(deviceAddress)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Монитор пульса") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.disconnect()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is HeartRateUiState.Connecting -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Подключение к устройству...")
                    }
                }

                is HeartRateUiState.Connected -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ожидание данных пульса...", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Убедитесь, что устройство передаёт данные",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is HeartRateUiState.Monitoring -> {
                    val heartRate = (uiState as HeartRateUiState.Monitoring).heartRate
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "$heartRate",
                            fontSize = 100.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ударов в минуту",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = {
                            viewModel.disconnect()
                            navController.navigateUp()
                        }) {
                            Text("Отключиться")
                        }
                    }
                }

                is HeartRateUiState.Disconnected -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Устройство отключено")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Вернуться к сканеру")
                        }
                    }
                }

                is HeartRateUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (uiState as HeartRateUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Назад")
                        }
                    }
                }

                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text("Подготовка...")
                    }
                }
            }
        }
    }
}