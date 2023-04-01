package dataClases

import java.util.*

data class Satellite(
    var name: String,
    var distance: Double?,
    var message: List<String> = emptyList(),
) {
    init {
        this.name = name.lowercase(Locale.getDefault())
    }
    companion object {
        // Coordenadas conocidas de los tres satélites
        val coordenadasKenobi = Coordinates(-500.0, -200.0)
        val coordenadasSkywalker = Coordinates(100.0, -100.0)
        val coordenadasSato = Coordinates(500.0, 100.0)
    }

    fun updateDistance(distance: Double) {
        this.distance = distance
    }

    fun updateMessage(message: List<String>) {
        this.message = message
    }

    fun findPosition() : Coordinates {
        return when (this.name) {
            "kenobi" -> {
                coordenadasKenobi
            }
            "skywalker" -> {
                coordenadasSkywalker
            }
            "sato" -> {
                coordenadasSato
            }
            else -> {
                Coordinates(null,null)
            }
        }
    }
}