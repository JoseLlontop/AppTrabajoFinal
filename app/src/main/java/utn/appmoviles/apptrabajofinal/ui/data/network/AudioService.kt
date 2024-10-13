package utn.appmoviles.apptrabajofinal.ui.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utn.appmoviles.apptrabajofinal.core.network.RetrofitHelper

// Es el encargado de llamar a los endpoint relacionado con el Audio
class AudioService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getAudios():String {
        return withContext(Dispatchers.IO) {
            val reponse = retrofit.create(Audio::class.java).getAudios()
            reponse.body()?.audio ?: ""
        }
    }
}