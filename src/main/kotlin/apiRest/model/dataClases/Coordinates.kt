package apiRest.model.dataClases

/**
 * Clase que modela las coordenadas de un punto geográfico.
 *
 * @property x La latitud del punto geográfico.
 * @property y La longitud del punto geográfico.
 */
data class Coordinates (
    val x : Double?,
    val y : Double?
    )