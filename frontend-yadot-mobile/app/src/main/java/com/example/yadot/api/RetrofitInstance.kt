package com.example.yadot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // ⚠️ ALTERE AQUI quando for testar em dispositivo real
    // Emulador Android: 10.0.2.2 acessa o localhost do PC
    // Dispositivo físico: use o IP da sua máquina (ex: 192.168.1.100)
    private const val BASE_URL = "https://yadot-api.up.railway.app/"

    val apiService: YadotApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Converte JSON ↔ Objeto
            .build()
            .create(YadotApiService::class.java)
    }
}