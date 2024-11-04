package utn.appmoviles.apptrabajofinal.model

sealed class Routes(val route: String) {
    data object PantallaPrincipal:Routes("pantallaPrincipal")
    data object PantallaEntrenamientoSonido:Routes("pantallaEntrenamientoSonido")
}