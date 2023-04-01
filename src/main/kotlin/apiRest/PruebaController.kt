package apiRest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.pow

@SpringBootApplication
class RunApplication

@RestController
@RequestMapping("/")
class TopSecretController {

    // Objetos satelites instanciados:
    private final val kenobi = dataClases.Satellite(enumClases.SatelliteName.KENOBI.name, 150.0, listOf("", "este", "es", "un", "mensaje"))
    private final val skywalker = dataClases.Satellite(enumClases.SatelliteName.SKYWALKER.name, 238.0, listOf("Hola", "este", "", "un", ""))
    private final val sato = dataClases.Satellite(enumClases.SatelliteName.SATO.name, 176.0, listOf("", "", "es", "un", "mensaje"))

    // Objeto TopSecretRequest instanciado:
    private val listSatellite = listOf(kenobi,skywalker,sato)

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