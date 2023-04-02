/**
 * API REST que recibe solicitudes para obtener la ubicación de un objeto y un mensaje de radio que se envía desde un punto
 * desconocido en el espacio exterior, a partir de información obtenida de tres satélites (Kenobi, Skywalker y Sato).
 *
 * Los datos de cada satélite incluyen la distancia entre el satélite y el objeto desconocido, así como un fragmento del mensaje completo.
 * El objetivo es calcular la ubicación del objeto y recuperar el mensaje original, que se ha transmitido como una lista de cadenas
 * en tres fragmentos distintos, uno por cada satélite.
 *
 * Este servicio tiene dos puntos finales:
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
package apiRest

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
 * Controlador REST para el punto final "/topsecret" que procesa la información de los tres satélites de una sola vez.
 * La ubicación del objeto desconocido y el mensaje completo se calculan a partir de la información recibida de los tres satélites.
 */
@RestController
@RequestMapping("/")
class TopSecretController {

    // Objetos satelites instanciados:
    private final val kenobi = dataClases.Satellite(enumClases.SatelliteName.KENOBI.name, 150.0, listOf("", "este", "es", "un", "mensaje"))
    private final val skywalker = dataClases.Satellite(enumClases.SatelliteName.SKYWALKER.name, 238.0, listOf("Hola", "este", "", "un", ""))
    private final val sato = dataClases.Satellite(enumClases.SatelliteName.SATO.name, 176.0, listOf("", "", "es", "un", "mensaje"))

    // Objeto TopSecretRequest instanciado:
    private val listSatellite = listOf(kenobi,skywalker,sato)

    /**
     * Método que procesa la petición POST en la URL "/topsecret" y devuelve la ubicación y mensaje del objeto desconocido.
     *
     * @param request objeto [dataClases.TopSecretRequest] que contiene la información de los satélites.
     * @return objeto [ResponseEntity] que contiene la ubicación y mensaje del objeto desconocido o un mensaje de error si no se recibió información suficiente.
     */
    @PostMapping("/topsecret")
    fun processTopSecret(@RequestBody request: dataClases.TopSecretRequest): ResponseEntity<Any> {
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
            ResponseEntity.ok(dataClases.TopSecretResponse(dataClases.Coordinates(x, y), message))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }
    }

    /**
     * POST endpoint que recibe y actualiza la información de un satélite en particular.
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
     * Controlador para la solicitud GET "/topsecret_split/{satellite_name}", que devuelve la información de un satélite individual.
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
                val response = dataClases.TopSecretResponse(
                    dataClases.Coordinates(x, y),
                    satellite.message.joinToString(" ")
                )
                ResponseEntity(response, HttpStatus.OK)
            } else {
                val response = dataClases.TopSecretResponse(
                    dataClases.Coordinates(x, y),
                    "Error: no hay suficiente información para mostrar el mensaje completo"
                )
                ResponseEntity(response, HttpStatus.OK)
            }
        } else {
            val response = mapOf("message" to "RESPONSE CODE: ${HttpStatus.NOT_FOUND.value()}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }
    }

    /**
     * Esta función calcula las coordenadas de la fuente del mensaje a partir de las distancias a tres satélites
     * conocidos y sus respectivas coordenadas.
     *
     * @param satellites Lista de tres objetos de tipo [dataClases.Satellite] que representan los satélites conocidos.
     * @return Retorna un objeto de tipo [dataClases.Coordinates] que contiene las coordenadas de la fuente del mensaje
     * si se pudieron calcular. Si no es posible triangular las coordenadas, retorna un objeto con coordenadas nulas.
     */
    private fun getLocation(satellites: List<dataClases.Satellite>): dataClases.Coordinates {

        // Se valida que se reciban las tres distancias. Caso contrario no posible triangular y devolvemos null
        if (satellites.size != 3) {
            return dataClases.Coordinates(null,null)
        }
        satellites.forEach {
            if (it.distance == null) {
                return dataClases.Coordinates(null,null)
            }
        }

        // Distancia a cada satélite desde la fuente del mensaje. La suposición acá es que vienen en el orden propuesto
        // abajo. Que es el mismo orden en que acomodé las coordenadas del satélite arriba. (kenobi, skywalker, sato)
        val distanceKenobi = satellites[0].distance
        val distanceSkywalker = satellites[1].distance
        val distanceSato = satellites[2].distance

        // Se calcula la distancia al cuadrado entre cada satélite y la fuente del mensaje
        val d1Sq = distanceKenobi!!.pow(2)
        val d2Sq = distanceSkywalker!!.pow(2)
        val d3Sq = distanceSato!!.pow(2)

        // Se obtienen las coordenadas de cada satélite.
        val x1 = dataClases.Satellite.coordinatesKenobi.latitude
        val y1 = dataClases.Satellite.coordinatesKenobi.longitude
        val x2 = dataClases.Satellite.coordinatesSkywalker.latitude
        val y2 = dataClases.Satellite.coordinatesSkywalker.longitude
        val x3 = dataClases.Satellite.coordinatesSato.latitude
        val y3 = dataClases.Satellite.coordinatesSato.longitude

        // Se calculan las coordenadas obtenidas al cuadrado para luego usarse para obtener las diferencias:
        val x1Sq = x1!!.pow(2)
        val y1Sq = y1!!.pow(2)
        val x2Sq = x2!!.pow(2)
        val y2Sq = y2!!.pow(2)
        val x3Sq = x3!!.pow(2)
        val y3Sq = y3!!.pow(2)

        // Se calculan las diferencias de coordenadas entre los satélites y la fuente del mensaje
        val a = (x2 - x1) * 2
        val b = (y2 - y1) * 2
        val c = d1Sq - d2Sq - x1Sq + x2Sq - y1Sq + y2Sq
        val d = (x3 - x2) * 2
        val e = (y3 - y2) * 2
        val f = d2Sq - d3Sq - x2Sq + x3Sq - y2Sq + y3Sq

        // Se resuelve el sistema de ecuaciones lineales para obtener las coordenadas de la fuente del mensaje
        // que luego retornaremos.
        val x = (c * e - f * b) / (e * a - b * d)
        val y = (c * d - a * f) / (b * d - a * e)

        return dataClases.Coordinates(x,y)
    }

    /**
     * Devuelve el mensaje original recibido por los satélites.
     *
     * El mensaje recibido por cada satélite se ingresa en el orden [0] = Kenobi, [1] = Skywalker, [2] = Sato.
     * Se recorren los mensajes de cada satélite y se construye el mensaje original. Si un satélite no tiene un
     * caracter para una determinada posición del mensaje, se utiliza el caracter de otro satélite.
     *
     * @param satellites La lista de satélites que recibieron el mensaje.
     * @return El mensaje original recibido por los satélites.
     */
    private fun getMessage(satellites: List<dataClases.Satellite>): String {

        // Misma suposición que en getLocation(). Los mensajes ingresan ordenados [0] = Kenobi, [1] = Skywalker, [2] = Sato
        val messageKenobi = satellites[0].message
        val messageSkywalker = satellites[1].message
        val messageSato = satellites[2].message

        val mensajeCompletoList  = mutableListOf<String>()
        var largoMensajeCompletoList = 0

        satellites.forEach {
            if (it.message.size > largoMensajeCompletoList) {
                largoMensajeCompletoList = it.message.size
            }
        }

        repeat(largoMensajeCompletoList) {
            mensajeCompletoList.add("")
        }

        for (i in mensajeCompletoList.indices) {
            if (mensajeCompletoList[i] == "") {
                if (messageKenobi[i] != "") {
                    mensajeCompletoList[i] = messageKenobi[i]
                } else if (messageSkywalker[i] != "") {
                    mensajeCompletoList[i] = messageSkywalker[i]
                } else {
                    mensajeCompletoList[i] = messageSato[i]
                }
            }
        }

        return mensajeCompletoList.joinToString(" ")
    }
}