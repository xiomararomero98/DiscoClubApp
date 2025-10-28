package com.example.discoclub.navigation

import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina



import com.example.discoclub.ui.components.AppTopBar // Barra superior
import com.example.discoclub.ui.components.AppDrawer // Drawer composable
import com.example.discoclub.ui.components.defaultDrawerItems // Ítems por defecto
import com.example.discoclub.ui.screen.HomeScreen // Pantalla Home
import com.example.discoclub.ui.screen.LoginScreenVm // Pantalla Login
import com.example.discoclub.ui.screen.PerfilScreenVm
import com.example.discoclub.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.discoclub.ui.screen.SplashScreen // Pantalla Splash
import com.example.discoclub.ui.screen.AdminScreen
import com.example.discoclub.ui.screen.PerfilScreen
import com.example.discoclub.ui.viewmodel.AuthViewModel



@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel        // <-- 1.- NUEVO: recibimos el VM inyectado desde MainActivity
) { // Recibe el controlador

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del drawer
    val scope = rememberCoroutineScope() // Necesario para abrir/cerrar drawer

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goAdmin: () -> Unit = { navController.navigate(Route.Admin.path) } // Ir a Admin
    val goPedido: () -> Unit = {navController.navigate(Route.Pedido.path)}

    ModalNavigationDrawer( // Capa superior con drawer lateral
        drawerState = drawerState, // Estado del drawer
        drawerContent = { // Contenido del drawer (menú)
            AppDrawer( // Nuestro componente Drawer
                currentRoute = null, // Puedes pasar navController.on
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goLogin() // Navega a Login
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goRegister() // Navega a Registro
                    },
                    onAdmin = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goAdmin() // Navega a Admin
                    },
                    onPedido = {
                        scope.launch { drawerState.close() }
                        goPedido()
                    }
                )
            )
        }
    ) {
        Scaffold( // Estructura base de pantalla
            topBar = { // Barra superior con íconos/menú
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
                    onHome = goHome,     // Botón Home
                    onLogin = goLogin,   // Botón Login
                    onRegister = goRegister, // Botón Registro
                    onPedido = goPedido
                )
            }
        ) { innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = Route.Splash.path, // Inicio: Home
                modifier = Modifier.padding(innerPadding) // Respeta topBar
            ) {
                composable(Route.Splash.path) { // destino splash
                    SplashScreen()
                }
                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        onGoLogin = goLogin,     // Botón para ir a Login
                        onGoRegister = goRegister // Botón para ir a Registro
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    //1 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (LoginScreenVm) para formularios/validación en tiempo real
                    LoginScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onLoginOkNavigateHome = goHome,            // Si el VM marca success=true, navegamos a Home
                        onGoRegister = goRegister                  // Enlace para ir a la pantalla de Registro
                    )
                }
                composable(Route.Register.path) { // Destino Registro
                    //2 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (RegisterScreenVm) para formularios/validación en tiempo real
                    RegisterScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onRegisteredNavigateLogin = goLogin,       // Si el VM marca success=true, volvemos a Login
                        onGoLogin = goLogin                        // Botón alternativo para ir a Login
                    )
                }
                composable(Route.Admin.path) {
                    com.example.discoclub.ui.screen.AdminScreen(

                        navController = navController,
                        vm = authViewModel   // se pasa el ViewModel al AdminScreen
                    )
                }
                // edición de perfil
                // ------------------------------------------------------------
                composable(
                    route = "EditarPerfil/{Id}",  //  acepta un parámetro dinámico (userId)
                    arguments = listOf(navArgument("id") { type = NavType.LongType }) //  Se define el tipo del parámetro como Long
                ) { backStackEntry ->

                    // Extrae el ID del usuario desde la ruta
                    val userId = backStackEntry.arguments?.getLong("id") ?: 0L

                    // Muestra la pantalla de perfil con ese ID
                    PerfilScreenVm(
                        vm = authViewModel,  // tu ViewModel de autenticación o usuarios
                        onPerfilGuardado = { navController.popBackStack() },
                        onCancelarClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}