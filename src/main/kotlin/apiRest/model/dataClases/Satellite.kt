package apiRest.model.dataClases

import java.util.*

/**
 * Class that represents a satellite with its respective name, distance and received message.
 *
 * @property name The name of the satellite.
 * @property distance The distance from the satellite to the source of the message.
 * @property message The message received by the satellite.
 */
data class Satellite(
    var name: String,
    var distance: Double?,
    var message: List<String> = emptyList(),
) {
    /**
     * Initializer that is executed immediately after an object of class Satellite is created.
     * Converts satellite name to lowercase using system default language.
     */
    init {
        this.name = name.lowercase(Locale.getDefault())
    }
    companion object {
        val coordinatesKenobi = Coordinates(-500.0, -200.0)
        val coordinatesSkywalker = Coordinates(100.0, -100.0)
        val coordinatesSato = Coordinates(500.0, 100.0)
    }

    /**
     * Updates the distance from the satellite to the source of the message.
     *
     * @param distance The new distance from the satellite.
     */
    fun updateDistance(distance: Double) {
        this.distance = distance
    }

    /**
     * Updates the message received by the satellite.
     *
     * @param message The new message received by the satellite.
     */
    fun updateMessage(message: List<String>) {
        this.message = message
    }

    /**
     * Find the coordinates of the satellite.
     *
     * @return The coordinates of the satellite as an object of type [Coordinates].
     */
    fun findPosition() : Coordinates {
        return when (this.name) {
            "kenobi" -> {
                coordinatesKenobi
            }
            "skywalker" -> {
                coordinatesSkywalker
            }
            "sato" -> {
                coordinatesSato
            }
            else -> {
                Coordinates(null,null)
            }
        }
    }
}
