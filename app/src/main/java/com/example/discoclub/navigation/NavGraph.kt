package com.example.discoclub.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.example.discoclub.ui.viewmodel.AuthViewModel

@Composable //grafico de navegacgion + drawer + scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel
){ //recibe el controlador
    val drawState = rememberDrawerState(initialValue = DrawerValue.Closed) //estado del drawer
    val scope = rememberCoroutineScope() // necesario para abrir y cerrar el drawer

    //helpers de navegacion (reutilizamos en topbar/drawer/botones)
    val  goHome: () -> Unit
}