package apiRest.model.dataClases

/**
 * Class representing the request for the "TopSecret" service.
 *
 * @param satellites a list of [Satellite] objects containing information about the satellites that received the message.
 */
data class TopSecretRequest(
    val satellites: List<Satellite>
)