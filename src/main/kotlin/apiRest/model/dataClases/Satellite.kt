package apiRest.model.dataClases

import java.util.*

/**
 * Clase que representa un satélite con su respectivo nombre, distancia y mensaje recibido.
 *
 * @property name El nombre del satélite.
 * @property distance La distancia del satélite a la fuente del mensaje.
 * @property message El mensaje recibido por el satélite.
 */
data class Satellite(
    var name: String,
    var distance: Double?,
    var message: List<String> = emptyList(),
) {
    /**
     * Inicializador que se ejecuta inmediatamente después de que un objeto de la clase Satellite es creado.
     * Convierte el nombre del satélite en minúsculas utilizando el idioma predeterminado del sistema.
     */
    init {
        this.name = name.lowercase(Locale.getDefault())
    }
    companion object {
        // Coordenadas conocidas de los tres satélites
        /**
         * Coordenadas del satélite Kenobi.
         */
        // Coordenadas conocidas de los tres satélites
        val coordinatesKenobi = Coordinates(-500.0, -200.0)
        /**
         * Coordenadas del satélite Skywalker.
         */
        val coordinatesSkywalker = Coordinates(100.0, -100.0)
        /**
         * Coordenadas del satélite Sato.
         */
        val coordinatesSato = Coordinates(500.0, 100.0)
    }

    /**
     * Actualiza la distancia del satélite a la fuente del mensaje.
     *
     * @param distance La nueva distancia del satélite.
     */
    fun updateDistance(distance: Double) {
        this.distance = distance
    }

    /**
     * Actualiza el mensaje recibido por el satélite.
     *
     * @param message El nuevo mensaje recibido por el satélite.
     */
    fun updateMessage(message: List<String>) {
        this.message = message
    }

    /**
     * Encuentra las coordenadas del satélite.
     *
     * @return Las coordenadas del satélite como un objeto de tipo [Coordinates].
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