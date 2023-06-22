package apiRest.service

import apiRest.model.dataClases.Coordinates
import apiRest.model.dataClases.Satellite
import apiRest.model.enumClases.SatelliteName
import kotlin.math.pow

class SatelliteService {

    // Objetos satelites instanciados:
    private final val kenobi = Satellite(SatelliteName.KENOBI.name, 150.0, listOf("", "este", "es", "un", "mensaje"))
    private final val skywalker = Satellite(SatelliteName.SKYWALKER.name, 238.0, listOf("Hola", "este", "", "un", ""))
    private final val sato = Satellite(SatelliteName.SATO.name, 176.0, listOf("", "", "es", "un", "mensaje"))

    // Creo una lista de satellites:
    val listSatellite = listOf(kenobi,skywalker,sato)

    /**
     * Esta función calcula las coordenadas de la fuente del mensaje a partir de las distancias a tres satélites
     * conocidos y sus respectivas coordenadas.
     *
     * @param satellites Lista de tres objetos de tipo [dataClases.Satellite] que representan los satélites conocidos.
     * @return Retorna un objeto de tipo [dataClases.Coordinates] que contiene las coordenadas de la fuente del mensaje
     * si se pudieron calcular. Si no es posible triangular las coordenadas, retorna un objeto con coordenadas nulas.
     */
    fun getLocation(satellites: List<Satellite>): Coordinates {

        // Se valida que se reciban las tres distancias. Caso contrario no posible triangular y devolvemos null
        if (satellites.size != 3) {
            return Coordinates(null,null)
        }
        satellites.forEach {
            if (it.distance == null) {
                return Coordinates(null,null)
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
        val x1 = Satellite.coordinatesKenobi.x
        val y1 = Satellite.coordinatesKenobi.y
        val x2 = Satellite.coordinatesSkywalker.x
        val y2 = Satellite.coordinatesSkywalker.y
        val x3 = Satellite.coordinatesSato.x
        val y3 = Satellite.coordinatesSato.y

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

        return Coordinates(x,y)
    }

    /**
     * Esta función devuelve el mensaje original recibido por los satélites.
     *
     * El mensaje recibido por cada satélite se ingresa en el orden [0] = Kenobi, [1] = Skywalker, [2] = Sato.
     * Se recorren los mensajes de cada satélite y se construye el mensaje original. Si un satélite no tiene un
     * caracter para una determinada posición del mensaje, se utiliza el caracter de otro satélite.
     *
     * @param satellites La lista de satélites que recibieron el mensaje.
     * @return El mensaje original recibido por los satélites.
     */
    fun getMessage(satellites: List<Satellite>): String {

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