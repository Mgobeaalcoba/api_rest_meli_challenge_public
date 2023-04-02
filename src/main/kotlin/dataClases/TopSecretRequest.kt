package dataClases

/**
 * Clase que representa la solicitud para el servicio "TopSecret".
 *
 * @param satellites una lista de objetos [Satellite] que contienen información sobre los satélites que recibieron el mensaje.
 */
data class TopSecretRequest(
    val satellites: List<Satellite>
)