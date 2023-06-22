package apiRest.model.dataClases

/**
 * Class representing the response from the Top Secret service.
 *
 * @property position The `x` and `y` coordinates that represent the current position of the ship.
 * @property message The message received by the satellites, if it could be decoded. Otherwise, it will be `null`.
 */
data class TopSecretResponse(
    val position: Coordinates,
    val message: String?
)