package utn.appmoviles.apptrabajofinal.model

sealed class Routes(val route: String) {
    data object PantallaPrincipal:Routes("pantallaPrincipal")
    data object PantallaGrabacion:Routes("pantallaGrabacion")
}