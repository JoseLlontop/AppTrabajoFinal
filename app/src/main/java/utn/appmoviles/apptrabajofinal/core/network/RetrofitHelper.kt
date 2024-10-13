package utn.appmoviles.apptrabajofinal.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val URL = "localHost:xxxx/"

object RetrofitHelper {

    // Retornamos la instancia de Retrofit
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}