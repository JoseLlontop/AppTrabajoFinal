package utn.appmoviles.apptrabajofinal.ui.data.network

// Esta clase es la intermediaria con el dominio
class AudioRepository {

    private val api = AudioService()

    suspend fun getAudios(): String {
        return api.getAudios()
    }
}