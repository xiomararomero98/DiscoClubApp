package com.example.discoclub.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.discoclub.R

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo con las plantas moradas
        Image(
            painter = painterResource(id = R.drawable.fondomatilda),
            contentDescription = "Fondo Matilda",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp), // Ajusta la distancia desde arriba (sube o baja el logo)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo centrado
            Image(
                painter = painterResource(id = R.drawable.logomatilda),
                contentDescription = "Logo Matilda",
                modifier = Modifier
                    .height(220.dp) // Aumenta el tamaño del logo
                    .width(300.dp), // Opcional, para hacerlo más ancho
                contentScale = ContentScale.Fit
            )
        }
    }
}