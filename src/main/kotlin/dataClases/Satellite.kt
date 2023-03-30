package dataClases

data class Satellite(
    val name: String,
    var distance: Double?,
    var message: List<String> = emptyList(),
) {
    companion object {
        // Coordenadas conocidas de los tres satélites
        val coordenadasKenobi = Pair(-500.0, -200.0)
        val coordenadasSkywalker = Pair(100.0, -100.0)
        val coordenadasSato = Pair(500.0, 100.0)
    }

    fun updateDistance(distance: Double) {
        this.distance = distance
    }

    fun updateMessage(message: List<String>) {
        this.message = message
    }

    fun findPosition() :Pair<Double?, Double?> {
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
                Pair(null, null)
            }
        }
    }
}