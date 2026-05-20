package com.example.yadot.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class UsuarioCadastroRequest(
    val nome: String,
    val sobrenome: String,
    val email: String,
    val senhaHash: String
)

data class UsuarioLoginRequest(
    val email: String,
    val senhaHash: String
)

data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val sobrenome: String,
    val email: String
)

data class HabitoRequest(
    val usuarioId: Long,
    val habitName: String,
    val categoria: String,
    val habitIcon: String,
    val diasDaSemana: List<String>
)

data class HabitoResponse(
    val habitId: Long,
    val habitName: String,
    val categoria: String,
    val habitIcon: String,
    val diasDaSemana: List<String>
)

data class CheckinRequest(
    val habitId: Long,
    val dataCheckin: String
)

data class CheckinResponse(
    val checkinId: Long,
    val habitId: Long,
    val dataCheckin: String
)

data class ProgressoResponse(
    val concluidos: Long,
    val pendentes: Long,
    val total: Long
)

// ─────────────────────────────────────────────────────────────
//  INTERFACE DA API
// ─────────────────────────────────────────────────────────────

interface YadotApi {

    @POST("usuarios/cadastro")
    suspend fun cadastrarUsuario(@Body body: UsuarioCadastroRequest): UsuarioResponse

    @POST("usuarios/login")
    suspend fun login(@Body body: UsuarioLoginRequest): UsuarioResponse

    @GET("usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>

    @POST("habitos")
    suspend fun criarHabito(@Body body: HabitoRequest): HabitoResponse

    @GET("habitos/usuario/{id}")
    suspend fun listarHabitosDoUsuario(@Path("id") usuarioId: Long): List<HabitoResponse>

    @GET("habitos/hoje/{usuarioId}")
    suspend fun listarHabitosDeHoje(@Path("usuarioId") usuarioId: Long): List<HabitoResponse>

    @PUT("habitos/{id}")
    suspend fun editarHabito(@Path("id") id: Long, @Body body: HabitoRequest): HabitoResponse

    @DELETE("habitos/{id}")
    suspend fun deletarHabito(@Path("id") id: Long)

    @POST("checkins")
    suspend fun realizarCheckin(@Body body: CheckinRequest): CheckinResponse

    @GET("checkins/habito/{habitoId}")
    suspend fun historicoCheckins(@Path("habitoId") habitoId: Long): List<CheckinResponse>

    @GET("checkins/progresso/{usuarioId}")
    suspend fun progressoDoDia(@Path("usuarioId") usuarioId: Long): ProgressoResponse
}

// ─────────────────────────────────────────────────────────────
//  SINGLETON RETROFIT
// ─────────────────────────────────────────────────────────────

object RetrofitClient {
    private const val BASE_URL = "https://yadot-api.up.railway.app/"

    val api: YadotApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YadotApi::class.java)
    }
}