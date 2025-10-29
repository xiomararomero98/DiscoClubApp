package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.discoclub.data.repository.PedidoRepository

class PedidosViewModelFactory(private val repository: PedidoRepository): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)){
            return PedidosViewModel(repository) as T
        }
        throw IllegalArgumentException("Uknown ViewModel Class: ${modelClass.name}")
    }
}