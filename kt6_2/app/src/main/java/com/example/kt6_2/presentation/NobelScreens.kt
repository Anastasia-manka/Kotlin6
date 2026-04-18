package com.example.kt6_2.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.kt6_2.data.api.NobelApi
import com.example.kt6_2.data.repository.NobelRepositoryImpl
import com.example.kt6_2.domain.usecase.GetPrizesUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.di.AppModule
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NobelViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedYear by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    val categories = listOf(
        "physics" to "Физика",
        "chemistry" to "Химия",
        "medicine" to "Медицина",
        "literature" to "Литература",
        "peace" to "Мир",
        "economics" to "Экономика"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Нобелевские лауреаты",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    AssistChip(
                        onClick = { showFilters = !showFilters },
                        label = {
                            Text(
                                if (showFilters) "Скрыть фильтры" else "Показать фильтры",
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Фильтры", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = selectedYear,
                            onValueChange = {
                                selectedYear = it
                                android.util.Log.d("NobelDebug", "UI: введён год = '$it'")
                                viewModel.filterByYear(it.ifEmpty { null })
                            },
                            label = { Text("Год (например, 2020)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { (code, name) ->
                                FilterChip(
                                    selected = selectedCategory == code,
                                    onClick = {
                                        val newCategory = if (selectedCategory == code) "" else code
                                        android.util.Log.d("NobelDebug", "UI: выбрана категория = '$newCategory' (код='$code')")
                                        selectedCategory = newCategory
                                        viewModel.filterByCategory(selectedCategory.ifEmpty { null })
                                    },
                                    label = { Text(name) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                android.util.Log.d("NobelDebug", "UI: нажата кнопка очистки")
                                selectedYear = ""
                                selectedCategory = ""
                                viewModel.clearFilters()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Очистить фильтры")
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is NobelUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Загрузка данных...")
                        }
                    }

                    is NobelUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (uiState as NobelUiState.Error).message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                viewModel.clearFilters()
                            }) {
                                Text("Повторить")
                            }
                        }
                    }

                    is NobelUiState.Success -> {
                        val prizes = (uiState as NobelUiState.Success).prizes
                        if (prizes.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Нет данных по указанным критериям")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.clearFilters() }) {
                                    Text("Сбросить фильтры")
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(prizes) { prize ->
                                    PrizeItem(
                                        prize = prize,
                                        onClick = {
                                            navController.navigate("detail/${prize.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrizeItem(
    prize: NobelPrize,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = prize.awardYear,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = prize.categoryFullName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prize.laureateName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prize.shortMotivation,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: NobelViewModel,
    prizeId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val prize = if (uiState is NobelUiState.Success) {
        (uiState as NobelUiState.Success).prizes.find { it.id == prizeId }
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (prize != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = prize.laureateName,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "${prize.awardYear} — ${prize.categoryFullName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider()

                Text(
                    text = "Обоснование:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = prize.motivation ?: "Описание отсутствует",
                    style = MaterialTheme.typography.bodyMedium
                )

                prize.country?.let {
                    Text(
                        text = "Страна: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Данные не найдены")
            }
        }
    }
}

@Composable
fun NobelApp() {
    val navController = rememberNavController()

    val viewModel: NobelViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return NobelViewModel(GetPrizesUseCase(AppModule.repository)) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController, viewModel)
        }
        composable("detail/{prizeId}") { backStackEntry ->
            val prizeId = backStackEntry.arguments?.getString("prizeId") ?: ""
            DetailScreen(navController, viewModel, prizeId)
        }
    }
}