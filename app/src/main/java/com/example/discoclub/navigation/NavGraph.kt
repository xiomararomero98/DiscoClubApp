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
import com.example.discoclub.data.local.database.AppDatabase
import com.example.discoclub.data.repository.ProductosRepository
import com.example.discoclub.data.repository.CarritoRepository
import com.example.discoclub.ui.viewmodel.ProductsViewModel
import com.example.discoclub.ui.viewmodel.CartViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable // Gráfico de navegación + Drawer + Scaffold + BottomBar (solo client/*)
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel        // VM inyectado desde MainActivity (como ya tenías)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goAdmin: () -> Unit = { navController.navigate(Route.Admin.path) }
    val goPedido: () -> Unit = { navController.navigate(Route.Pedido.path) }
    val goProducts = { navController.navigate(Route.Products.path) }
    val goCart = { navController.navigate(Route.Cart.path) }
    val goProfile = { navController.navigate(Route.Profile.path) }

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
                    },
                    onAdmin = {
                        scope.launch { drawerState.close() }
                        goAdmin()
                    },
                    onPedido = {
                        scope.launch { drawerState.close() }
                        goPedido()
                    },
                    onProducts = {
                        scope.launch { drawerState.close() }
                        goProducts()
                    },
                    onCart = {
                        scope.launch { drawerState.close() }
                        goCart()
                    },
                    onProfile = {
                        scope.launch { drawerState.close() }
                        goProfile()
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
                    onRegister = goRegister,
                    onAdmin = goAdmin,
                    onPedido = goPedido,
                    onCart = goCart,
                    onProfile = goProfile,
                    onProducts = goProducts

                )
            },
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
                            navController.navigate(Route.Products.path) {
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
                composable(Route.Products.path) {
                    ProductsScreen(
                        // Tu pantalla ya era “base”; ahora pásale el VM real
                        vm = productsVm,
                        onGoCart = goCart,
                        onGoProfile = goProfile
                    )
                }
                composable(Route.Cart.path) {
                    CartScreen(
                        vm = cartVm,
                        onGoProducts = goProducts,
                        onGoProfile = goProfile
                    )
                }
                composable(Route.Profile.path) {
                    PerfilScreen(
                        onGuardarClick = { _, _, _, _ -> }, // función con 4 parámetros vacíos
                        onCancelarClick = { navController.popBackStack() } // vuelve atrás
                    )
                }
                composable(Route.Admin.path) {
                    AdminScreen(
                        navController = navController,
                        vm = authViewModel
                    )
                }
            }
        }
    }
}

