package com.example.yadot.api

import com.example.yadot.model.*
import retrofit2.Response
import retrofit2.http.*

interface YadotApiService {

    // ========== USUÁRIOS ==========

    @POST("usuarios/cadastro")
    suspend fun cadastrar(@Body request: UsuarioCadastroRequest): Response<UsuarioResponse>

    @POST("usuarios/login")
    suspend fun login(@Body request: UsuarioLoginRequest): Response<UsuarioResponse>

    // ========== HÁBITOS ==========

    @POST("habitos")
    suspend fun criarHabito(@Body request: HabitoRequest): Response<HabitoResponse>

    @GET("habitos/usuario/{usuarioId}")
    suspend fun listarHabitos(@Path("usuarioId") usuarioId: Long): Response<List<HabitoResponse>>

    @GET("habitos/hoje/{usuarioId}")
    suspend fun listarHabitosDoDia(@Path("usuarioId") usuarioId: Long): Response<List<HabitoResponse>>

    @DELETE("habitos/{id}")
    suspend fun deletarHabito(@Path("id") id: Long): Response<Unit>

    // ========== CHECK-INS ==========

    @POST("checkins")
    suspend fun realizarCheckin(@Body request: CheckinRequest): Response<CheckinResponse>

    @GET("checkins/progresso/{usuarioId}")
    suspend fun progressoDoDia(@Path("usuarioId") usuarioId: Long): Response<ProgressoResponse>
}