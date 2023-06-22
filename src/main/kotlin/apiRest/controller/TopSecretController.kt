package apiRest.controller

/**
 * REST API that receives requests to get the location of an object and a radio message that is sent from a point
 * unknown in outer space, based on information obtained from three satellites (Kenobi, Skywalker and Sato).
 *
 * The data for each satellite includes the distance between the satellite and the unknown object, as well as a fragment of the complete message.
 * The goal is to calculate the location of the object and retrieve the original message, which has been transmitted as a list of strings
 * in three different fragments, one for each satellite.
 *
 * This service has two endpoints:
 * - "/topsecret", which receives and processes information from all three satellites at once.
 * - "/topsecret_split", which allows sending the information of each satellite separately and recovering the complete information of the message
 * when information has been obtained from all satellites.
 *
 * @see [dataClasses.Satellite]
 * @see [dataClasses.TopSecretRequest]
 * @see [dataClasses.TopSecretResponse]
 * @see [dataClasses.Coordinates]
 * @see [enumClasses.SatelliteName]
 */

import apiRest.model.enumClases.SatelliteName
import apiRest.model.dataClases.Coordinates
import apiRest.model.dataClases.Satellite
import apiRest.model.dataClases.TopSecretRequest
import apiRest.model.dataClases.TopSecretResponse
import apiRest.service.SatelliteService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Starting point of the Spring Boot application.
 */
@SpringBootApplication
class RunApplication

/**
 * REST controller for "/topsecret" and "/topsecret_split/{satellite_name} endpoints that processes information from all three satellites at once on the first endpoint
 * or from each satellite on the second endpoint. The controller has functions that allow you to obtain:
 * 1- The location of the unknown object.
 * 2- The complete message is calculated from the information received from the three satellites.
 */
@RestController
@RequestMapping("/")
class TopSecretController {

    private lateinit var satelliteService: SatelliteService

    /**
     * Method that processes the POST request at the URL "/topsecret" and returns the location and message of the unknown object.
     *
     * @param request object [dataClases.TopSecretRequest] that contains the information of the satellites.
     * @return [ResponseEntity] object containing the location and message of the unknown object or an error message if not enough information was received.
     */
    @PostMapping("/topsecret")
    fun processTopSecret(@RequestBody request: TopSecretRequest): ResponseEntity<Any> {
        satelliteService = SatelliteService()
        val (x: Double?, y: Double?) = satelliteService.getLocation(request.satellites)
        val message = satelliteService.getMessage(request.satellites)
        val response = mapOf("message" to "RESPONSE CODE: ${HttpStatus.NOT_FOUND.value()}")
        return if (x != null && y != null) {
            // Extra: I update the values of my satellites in the post /topsecret
            for (satellite in request.satellites) {
                val name = satellite.name.lowercase()
                val mySatellite = satelliteService.listSatellite.find { it.name == name }
                mySatellite?.run {
                    updateDistance(satellite.distance!!)
                    updateMessage(satellite.message)
                }
            }
            ResponseEntity.ok(TopSecretResponse(Coordinates(x, y), message))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }
    }

    /**
     * Method that processes the POST request on the endpoint "/topsecret_split/{satellite_name}". It receives and updates the information from a particular satellite.
     * If the name of the satellite exists in the list of registered satellites, its information is updated with the values received in the body.
     * If it does not exist, a 400 Bad Request error is returned indicating that the satellite was not found.
     * @param satellite_name The name of the satellite to update.
     * @param body The body of the HTTP request, containing the distance and the message received from the satellite.
     * @return A ResponseEntity object containing a success message if the satellite was updated, or a 400 Bad Request error if the satellite was not found.
     */
    @PostMapping("/topsecret_split/{satellite_name}")
    fun postSplitSatellite(@PathVariable satellite_name: String, @RequestBody body: Map<String, Any>): ResponseEntity<Any> {
        satelliteService = SatelliteService()
        // Verifico si el nombre del satélite existe en la lista de satélites registrados y si es así traerlo a la fun:
        val satelliteData = satelliteService.listSatellite.find { it.name == satellite_name.lowercase() }
            ?: return ResponseEntity.badRequest().body("Satellite ${satellite_name.lowercase()} not found.") // if null return ...

        satelliteData.updateDistance(body["distance"] as Double)
        @Suppress("UNCHECKED_CAST")
        satelliteData.updateMessage(body["message"] as List<String>)
        return ResponseEntity.ok("Data for satellite ${satellite_name.lowercase()} updated successfully.")
    }

    /**
     * Method for the GET request "/topsecret_split/{satellite_name}", which returns the information of an individual satellite.
     * If the satellite does not have enough information to determine the location and message of the unknown object, an error message is returned.
     *
     * @param satellite_name the name of the satellite for which information is requested
     *
     * @return a ResponseEntity object with the unknown object information and the full message, or an error message if there is not enough information.
     */
    @GetMapping("/topsecret_split/{satellite_name}")
    fun getSplitSatellite(@PathVariable satellite_name: String): ResponseEntity<Any> {
        satelliteService = SatelliteService()
        val satellite = satelliteService.listSatellite.find { it.name == satellite_name.lowercase() }
        return if (satellite != null) {
            val (x, y) = satellite.findPosition()
            val messegeIsEmpty = (satellite.message.find { it == "" })
            if (messegeIsEmpty == null) {
                val response = TopSecretResponse(
                    Coordinates(x, y),
                    satellite.message.joinToString(" ")
                )
                ResponseEntity(response, HttpStatus.OK)
            } else {
                val response = TopSecretResponse(
                    Coordinates(x, y),
                    "Error: no hay suficiente información para mostrar el mensaje completo"
                )
                ResponseEntity(response, HttpStatus.OK)
            }
        } else {
            val response = mapOf("message" to "RESPONSE CODE: ${HttpStatus.NOT_FOUND.value()}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }
    }

}