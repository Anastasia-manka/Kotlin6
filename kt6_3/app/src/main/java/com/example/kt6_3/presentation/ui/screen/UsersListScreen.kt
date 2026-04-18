package com.example.kt6_3.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kt6_3.di.AppModule
import com.example.kt6_3.presentation.ui.component.UserItem
import com.example.kt6_3.presentation.viewmodel.UsersListUiState
import com.example.kt6_3.presentation.viewmodel.UsersListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val getUsersUseCase = AppModule.provideGetUsersUseCase(context)
    val logoutUseCase = AppModule.provideLogoutUseCase(context)

    val viewModel: UsersListViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UsersListViewModel(getUsersUseCase, logoutUseCase) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val shouldNavigateToLogin by viewModel.shouldNavigateToLogin.collectAsState()

    LaunchedEffect(shouldNavigateToLogin) {
        if (shouldNavigateToLogin) {
            navController.navigate("login") {
                popUpTo("users_list") { inclusive = true }
            }
            viewModel.onNavigateToLoginComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Выйти")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is UsersListUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Загрузка пользователей...")
                    }
                }

                is UsersListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (uiState as UsersListUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Повторить")
                        }
                    }
                }

                is UsersListUiState.Success -> {
                    val users = (uiState as UsersListUiState.Success).users
                    if (users.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Пользователи не найдены")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(users) { user ->
                                UserItem(
                                    user = user,
                                    onClick = {
                                        navController.navigate("user_detail/${user.id}")
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