package apiRest.model.dataClases

/**
 * Class that models the coordinates of a geographic point.
 *
 * @property x The latitude of the geographic point.
 * @property y The longitude of the geographic point.
 */
data class Coordinates (
    val x : Double?,
    val y : Double?
    )