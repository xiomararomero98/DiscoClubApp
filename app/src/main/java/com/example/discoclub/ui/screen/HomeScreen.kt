package com.example.discoclub.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//crear nombre del archivo en el caché

private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}

//obtener la uri del archivo en el cache

private fun getImageUriForFile(context: Context, file: File): Uri{
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority, file)
}

@Composable
fun HomeScreen(
    onGoLogin: () -> Unit, //accion a login
    onGoRegister: () -> Unit //accion a registro
){
    //contexto
    val  context = LocalContext.current

    //guardar la ultima foto tomada por la camara
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }

    //temporal para guardar la uri actual de la foto que tomara la camara
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    //launcher para la camara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {success ->
        if (success){
            //si tomo la foto
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "foto tomada correctamente", Toast.LENGTH_SHORT).show()
        }else{
            pendingCaptureUri =null
            Toast.makeText(context, "no se tomo ninguna foto", Toast.LENGTH_SHORT ).show()
        }
    }

    val bg = MaterialTheme.colorScheme.surfaceVariant // fondo agradable para home

    Box( //contenedor a pantalla completa
        modifier = Modifier
            .fillMaxSize() //ocupa todo
            .background(bg) //aplica fondo
            .padding(16.dp), //margen interior
        contentAlignment = Alignment.Center //centra el contenido
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally //centra hijos
        ){
            //Cabecera como Row (ejemplo de estructura)
            Row(
                verticalAlignment = Alignment.CenterVertically //centra vertical
            ){
                Text( //titulo home
                    text = "Home",
                    style = MaterialTheme.typography.headlineSmall, //estilo titulo
                    fontWeight = FontWeight.SemiBold //seminegrita
                )

                Spacer(Modifier.width(8.dp)) //separacion horizontal
            }

            Spacer(Modifier.height(20.dp)) //separacion

            //tarjeta con un mini hero
            ElevatedCard(
                modifier = Modifier.fillMaxWidth() //ancho completo
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), //margen interno de la card
                    horizontalAlignment = Alignment.CenterHorizontally //centrado
                ){
                    Text(
                        "Demostración de navegación con TopBar + Drawer + Botones",
                        style = MaterialTheme.typography.bodyMedium //texto base
                    )
                }
            }
            Spacer(Modifier.height(24.dp)) //separacion
            //nueva UI para la cámara
            ElevatedCard( //card elevada para remarcar contenido
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp) //margen interno de la card
                ) {
                    Text(
                        text = "Captura de foto con cámara del dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    //si no se ha tomado foto se muestra un texto
                    if (photoUriString.isNullOrEmpty()){
                        Text(
                            text = "No se ha tomado fotos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                    }else{
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(photoUriString)).crossfade(true)
                                .build(),
                            contentDescription = "Foto Tomada",
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    //manipular si la alert es visible o no
                    var  showDialog by remember { mutableStateOf(false) }
                    //boton para abrir la camara
                    Button(onClick =  {
                        val  file = createTempImageFile(context)
                        val uri = getImageUriForFile(context,file)
                        pendingCaptureUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        Text(
                            if (photoUriString.isNullOrEmpty()) "Abrir camara"
                            else "Volver a tomar foto"
                        )
                    }
                    //boton para eliminar foto

                    if (!photoUriString.isNullOrEmpty()){
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = {showDialog=true}) {
                            Text("Eliminar foto")
                        }
                    }
                    //la alerta
                    if (showDialog){
                        AlertDialog(
                            onDismissRequest = {showDialog = false },
                            title = {Text("Confirmación")},
                            text = {Text("¿Desea eliminar la foto?")},
                            confirmButton = {
                                TextButton(onClick = {
                                    photoUriString = null
                                    showDialog = false
                                    Toast.makeText(context, "foto eliminada", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
            //Botones de navegación principales
            Row( //dos botones en fila
                horizontalArrangement = Arrangement.spacedBy(12.dp) //espacio entre borones
            ){
                Button(onClick = onGoLogin) {Text("Ir al Login") } //navega al login
                OutlinedButton(onClick = onGoRegister) {Text("Ir a Registro") } //navega a registro
            }
        }
    }
}

