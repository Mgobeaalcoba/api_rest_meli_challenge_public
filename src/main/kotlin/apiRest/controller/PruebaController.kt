package apiRest

/**
 * API REST que recibe solicitudes para obtener la ubicación de un objeto y un mensaje de radio que se envía desde un punto
 * desconocido en el espacio exterior, a partir de información obtenida de tres satélites (Kenobi, Skywalker y Sato).
 *
 * Los datos de cada satélite incluyen la distancia entre el satélite y el objeto desconocido, así como un fragmento del mensaje completo.
 * El objetivo es calcular la ubicación del objeto y recuperar el mensaje original, que se ha transmitido como una lista de cadenas
 * en tres fragmentos distintos, uno por cada satélite.
 *
 * Este servicio tiene dos endpoints:
 *  - "/topsecret", que recibe y procesa la información de los tres satélites de una sola vez.
 *  - "/topsecret_split", que permite enviar la información de cada satélite por separado y recuperar la información completa del mensaje
 *    cuando se haya obtenido información de todos los satélites.
 *
 * @see [dataClases.Satellite]
 * @see [dataClases.TopSecretRequest]
 * @see [dataClases.TopSecretResponse]
 * @see [dataClases.Coordinates]
 * @see [enumClases.SatelliteName]
 */

import apiRest.model.enumClases.SatelliteName
import apiRest.model.dataClases.Coordinates
import apiRest.model.dataClases.Satellite
import apiRest.model.dataClases.TopSecretRequest
import apiRest.model.dataClases.TopSecretResponse
import apiRest.service.getLocation
import apiRest.service.getMessage
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.pow

/**
 * Punto de inicio de la aplicación Spring Boot.
 */
@SpringBootApplication
class RunApplication

/**
 * Controlador REST para los endpoints "/topsecret" y "/topsecret_split/{satellite_name} que procesa la información de los tres satélites de una sola vez en el primer endpoint
 * o de cada satélite en el segundo endpoint. El controlador cuanta con funciones que le permiten obtener:
 * 1- La ubicación del objeto desconocido.
 * 2- El mensaje completo se calculan a partir de la información recibida de los tres satélites.
 */
@RestController
@RequestMapping("/")
class TopSecretController {

    // Objetos satelites instanciados:
    private final val kenobi = Satellite(SatelliteName.KENOBI.name, 150.0, listOf("", "este", "es", "un", "mensaje"))
    private final val skywalker = Satellite(SatelliteName.SKYWALKER.name, 238.0, listOf("Hola", "este", "", "un", ""))
    private final val sato = Satellite(SatelliteName.SATO.name, 176.0, listOf("", "", "es", "un", "mensaje"))

    // Objeto TopSecretRequest instanciado:
    private val listSatellite = listOf(kenobi,skywalker,sato)

    /**
     * Método que procesa la petición POST en la URL "/topsecret" y devuelve la ubicación y mensaje del objeto desconocido.
     *
     * @param request objeto [dataClases.TopSecretRequest] que contiene la información de los satélites.
     * @return objeto [ResponseEntity] que contiene la ubicación y mensaje del objeto desconocido o un mensaje de error si no se recibió información suficiente.
     */
    @PostMapping("/topsecret")
    fun processTopSecret(@RequestBody request: TopSecretRequest): ResponseEntity<Any> {
        val (x: Double?, y: Double?) = getLocation(request.satellites)
        val message = getMessage(request.satellites)
        val response = mapOf("message" to "RESPONSE CODE: ${HttpStatus.NOT_FOUND.value()}")
        return if (x != null && y != null) {
            // Extra: Actualizo en el post /topsecret los valores de mis satelites
            for (satellite in request.satellites) {
                val name = satellite.name.lowercase()
                val mySatellite = listSatellite.find { it.name == name }
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
     * Método que procesa la petición POST en el endpoint "/topsecret_split/{satellite_name}". El mismo recibe y actualiza la información de un satélite en particular.
     * Si el nombre del satélite existe en la lista de satélites registrados, se actualiza su información con los valores recibidos en el body.
     * Si no existe, se retorna un error 400 Bad Request indicando que el satélite no fue encontrado.
     * @param satellite_name El nombre del satélite a actualizar.
     * @param body El cuerpo de la petición HTTP, que contiene la distancia y el mensaje recibidos desde el satélite.
     * @return Un objeto ResponseEntity que contiene un mensaje de éxito si el satélite fue actualizado, o un error 400 Bad Request si no se encontró el satélite.
     */
    @PostMapping("/topsecret_split/{satellite_name}")
    fun postSplitSatellite(@PathVariable satellite_name: String, @RequestBody body: Map<String, Any>): ResponseEntity<Any> {

        // Verifico si el nombre del satélite existe en la lista de satélites registrados y si es así traerlo a la fun:
        val satelliteData = listSatellite.find { it.name == satellite_name.lowercase() }
            ?: return ResponseEntity.badRequest().body("Satellite ${satellite_name.lowercase()} not found.") // if null return ...

        // Actualizar la distancia y el mensaje del satélite con los valores recibidos
        satelliteData.updateDistance(body["distance"] as Double)
        @Suppress("UNCHECKED_CAST")
        satelliteData.updateMessage(body["message"] as List<String>)
        return ResponseEntity.ok("Data for satellite ${satellite_name.lowercase()} updated successfully.")
    }

    /**
     * Método para la petición GET "/topsecret_split/{satellite_name}", que devuelve la información de un satélite individual.
     * Si el satélite no tiene suficiente información para determinar la ubicación y el mensaje del objeto desconocido, se devuelve un mensaje de error.
     *
     * @param satellite_name el nombre del satélite para el que se solicita información
     *
     * @return un objeto ResponseEntity con la información del objeto desconocido y el mensaje completo, o un mensaje de error si no hay suficiente información.
     */
    @GetMapping("/topsecret_split/{satellite_name}")
    fun getSplitSatellite(@PathVariable satellite_name: String): ResponseEntity<Any> {
        val satellite = listSatellite.find { it.name == satellite_name.lowercase() }
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