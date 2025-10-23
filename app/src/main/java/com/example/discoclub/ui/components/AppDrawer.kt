package com.example.discoclub.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

//pequeña data class para representar cada opcion del drawer

data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, //texto a mostrar
    val icon: ImageVector, //Icono del item
    val onClick: () -> Unit //Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    curentRoute: String?, //ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, //lista de items a mostrar
    modifier: Modifier = Modifier //modificador opcional
){
    ModalDrawerSheet( //hoja que contiene el contenido del drawer
        modifier = modifier //modificador encadenable
    ){
        //Recorremos las opciones y pintamos items
        items.forEach { item -> //por cada item
            NavigationDrawerItem( //items con estados Material
                label = { Text(item.label) }, //texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label ) }, //icono
                modifier = Modifier, //sin mods extra
                colors = NavigationDrawerItemDefaults.colors() //estilo por defecto
            )
        }
    }
}