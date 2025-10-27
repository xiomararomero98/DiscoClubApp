package com.example.discoclub.navigation

import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.* // Scaffold, App bars, NavigationBar
import androidx.compose.runtime.* // Composable, remember
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import androidx.navigation.compose.navigation // Subgráfico
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina
import androidx.compose.ui.platform.LocalContext

import com.example.discoclub.ui.components.AppTopBar // Barra superior
import com.example.discoclub.ui.components.AppDrawer // Drawer composable
import com.example.discoclub.ui.components.defaultDrawerItems // Ítems por defecto
import com.example.discoclub.ui.screen.* // Home/Login/Register/Products/Cart/Profile
import com.example.discoclub.ui.viewmodel.AuthViewModel

// NUEVO: DB/Repos/VM de cliente
import com.example.discoclub.data.local.database.AppDatabase
import com.example.discoclub.data.repository.ProductosRepository
import com.example.discoclub.data.repository.CarritoRepository
import com.example.discoclub.ui.viewmodel.ProductsViewModel
import com.example.discoclub.ui.viewmodel.CartViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.discoclub.ui.components.ClientBottomBar


@Composable // Gráfico de navegación + Drawer + Scaffold + BottomBar (solo client/*)
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel        // VM inyectado desde MainActivity (como ya tenías)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit      = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit     = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit  = { navController.navigate(Route.Register.path) }

    // NUEVO: Instancia DB y Repos para el flujo Cliente
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val productsRepo = remember { ProductosRepository(db.productosDao()) }
    val cartRepo = remember { CarritoRepository(db.carritoDao()) }

    // NUEVO: Factories inline (sin crear archivos extra)
    val productsVm: ProductsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
                    return ProductsViewModel(productsRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
            }
        }
    )
    val cartVm: CartViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                    return CartViewModel(cartRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
            }
        }
    )

    // Saber la ruta actual (para mostrar/ocultar BottomBar en client/*)
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: ""

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null, // podrías pasar navController si tu Drawer lo usa
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister
                )
            },
            // NUEVO: BottomBar solo en rutas client/*
            bottomBar = {
                if (currentRoute.startsWith(Route.Client.Root.path)) {
                    ClientBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { dest ->
                            navController.navigate(dest) {
                                // evitamos crecer el backstack al cambiar de tab
                                popUpTo(Route.Client.Products.path) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                // ---------- RUTAS PÚBLICAS ----------
                composable(Route.Home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            // Al loguear OK, entrar al subgráfico Cliente (Products como start)
                            navController.navigate(Route.Client.Products.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // ---------- SUBGRÁFICO CLIENTE ----------
                navigation(
                    startDestination = Route.Client.Products.path,
                    route = Route.Client.Root.path
                ) {
                    composable(Route.Client.Products.path) {
                        ProductsScreen(
                            // Tu pantalla ya era “base”; ahora pásale el VM real
                            vm = productsVm,
                            onGoCart = { navController.navigate(Route.Client.Cart.path) },
                            onGoProfile = { navController.navigate(Route.Client.Profile.path) }
                        )
                    }
                    composable(Route.Client.Cart.path) {
                        CartScreen(
                            vm = cartVm,
                            onGoProducts = { navController.navigate(Route.Client.Products.path) },
                            onGoProfile = { navController.navigate(Route.Client.Profile.path) }
                        )
                    }
                    composable(Route.Client.Profile.path) {
                        ProfileScreen(
                            onLogout = {
                                // Aquí podrías limpiar sesión si ya tienes SessionManager
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Client.Root.path) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
