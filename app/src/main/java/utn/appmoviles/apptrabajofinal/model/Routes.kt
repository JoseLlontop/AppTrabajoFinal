package utn.appmoviles.apptrabajofinal.model

sealed class Routes(val route: String) {
    data object PantallaLogin:Routes("pantallaLogin")
    data object PantallaPrincipal:Routes("pantallaPrincipal")
    data object PantallaConfiguracion:Routes("pantallaConfiguracion")
    data object PantallaEntrenamientoSonido:Routes("pantallaEntrenamientoSonido")
    data object PantallaEnvioWhastsApp:Routes("pantallaEnvioWhatsApp")
}