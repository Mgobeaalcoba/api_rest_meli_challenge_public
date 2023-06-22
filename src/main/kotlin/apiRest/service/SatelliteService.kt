package apiRest.service

import apiRest.model.dataClases.Coordinates
import apiRest.model.dataClases.Satellite
import apiRest.model.enumClases.SatelliteName
import kotlin.math.pow

class SatelliteService {

    private val kenobi = Satellite(SatelliteName.KENOBI.name, 150.0, listOf("", "este", "es", "un", "mensaje"))
    private val skywalker = Satellite(SatelliteName.SKYWALKER.name, 238.0, listOf("Hola", "este", "", "un", ""))
    private val sato = Satellite(SatelliteName.SATO.name, 176.0, listOf("", "", "es", "un", "mensaje"))

    val listSatellite = listOf(kenobi,skywalker,sato)

    /**
     * This function calculates the coordinates of the message source from the distances to three satellites
     * known and their respective coordinates.
     *
     * @param satellites List of three objects of type [dataClasses.Satellite] that represent the known satellites.
     * @return Returns an object of type [dataClasses.Coordinates] that contains the coordinates of the source of the message
     * if they could be calculated. If it is not possible to triangulate the coordinates, it returns an object with null coordinates.
     */
    fun getLocation(satellites: List<Satellite>): Coordinates {

        // It is validated that the three distances are received. Otherwise it is not possible to triangulate and we return null
        if (satellites.size != 3) {
            return Coordinates(null,null)
        }
        satellites.forEach {
            if (it.distance == null) {
                return Coordinates(null,null)
            }
        }

        val distanceKenobi = satellites[0].distance
        val distanceSkywalker = satellites[1].distance
        val distanceSato = satellites[2].distance

        val d1Sq = distanceKenobi!!.pow(2)
        val d2Sq = distanceSkywalker!!.pow(2)
        val d3Sq = distanceSato!!.pow(2)

        val x1 = Satellite.coordinatesKenobi.x
        val y1 = Satellite.coordinatesKenobi.y
        val x2 = Satellite.coordinatesSkywalker.x
        val y2 = Satellite.coordinatesSkywalker.y
        val x3 = Satellite.coordinatesSato.x
        val y3 = Satellite.coordinatesSato.y

        val x1Sq = x1!!.pow(2)
        val y1Sq = y1!!.pow(2)
        val x2Sq = x2!!.pow(2)
        val y2Sq = y2!!.pow(2)
        val x3Sq = x3!!.pow(2)
        val y3Sq = y3!!.pow(2)

        val a = (x2 - x1) * 2
        val b = (y2 - y1) * 2
        val c = d1Sq - d2Sq - x1Sq + x2Sq - y1Sq + y2Sq
        val d = (x3 - x2) * 2
        val e = (y3 - y2) * 2
        val f = d2Sq - d3Sq - x2Sq + x3Sq - y2Sq + y3Sq

        val x = (c * e - f * b) / (e * a - b * d)
        val y = (c * d - a * f) / (b * d - a * e)

        return Coordinates(x,y)
    }

    /**
     * This function returns the original message received by the satellites.
     *
     * The message received by each satellite is entered in the order [0] = Kenobi, [1] = Skywalker, [2] = Sato.
     * The messages from each satellite are traversed and the original message is built. If a satellite does not have a
     * character for a certain position of the message, the character of another satellite is used.
     *
     * @param satellites The list of satellites that received the message.
     * @return The original message received by the satellites.
     */
    fun getMessage(satellites: List<Satellite>): String {

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