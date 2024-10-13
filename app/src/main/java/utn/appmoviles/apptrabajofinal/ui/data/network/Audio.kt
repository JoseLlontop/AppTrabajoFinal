package utn.appmoviles.apptrabajofinal.ui.data.network

import retrofit2.Response
import retrofit2.http.GET
import utn.appmoviles.apptrabajofinal.ui.data.network.response.AudioResponse

interface Audio {

    @GET("/getAudios")
    suspend fun getAudios(): Response<AudioResponse>
}