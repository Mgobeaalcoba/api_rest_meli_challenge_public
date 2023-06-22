package apiRest.model.dataClases

/**
 * Clase que representa la respuesta del servicio Top Secret.
 *
 * @property position Las coordenadas `x` e `y` que representan la posición actual de la nave.
 * @property message El mensaje recibido por los satélites, si se pudo decodificar. En caso contrario, será `null`.
 */
data class TopSecretResponse(
    val position: Coordinates,
    val message: String?
)