package main

import kotlin.math.pow

fun main() {

	println("Bienvenido!!! Hemos detectado una nave imperial que está a la deriva en un campo de asteroides..")
	val distancias = arrayOf(150f, 238f, 176f)
	val position = getLocation(distancias)
	if (position != null) {
		println("La posición de la nave enemiga es x: ${position.first}, y: ${position.second}")
	} else {
		println("No pudimos obtener la posición dado que nos faltó un dato de distancia.")
	}
	println("También hemos podido reconstruir el contenido del mensaje transmitido por la nave imperial:")
	val mensajesEncriptados: List<List<String>> = listOf(
		listOf("", "este", "es", "un", "mensaje"),
		listOf("Hola", "este", "", "un", ""),
		listOf("", "", "es", "un", "mensaje")
	)
	val mensaje = getMessege(mensajesEncriptados)
	println("El contendido del mensaje es: '$mensaje'.")
	println("Con esta información podrás provocarle un duro golpe al Imperio Galactico")
}

// Disclaimer: El algoritmo que he desarrollado para determinar las coordenadas del emisor del mensaje utiliza una
// técnica de trilateración que usa las localizaciones conocidas de dos o más puntos de referencia, y la distancia
// medida entre el emisor del mensaje y cada satelite.
// Explicación visual de la solución armada en Figma:
// https://www.figma.com/file/XUbwl0hIkeKKW8CoYC2YG4/Trilateraci%C3%B3n---Meli-Challenge?node-id=0%3A1&t=AWzd88m2NioecRe7-1

// Función que recibe las distancias a los tres satélites y devuelve las coordenadas del emisor del mensaje
fun getLocation(distancias: Array<Float>): Pair<Float, Float>? {

	// Coordenadas conocidas de los tres satélites
	val coordenadasKenobi = Pair(-500f, -200f)
	val coordenadasSkywalker = Pair(100f, -100f)
	val coordenadasSato = Pair(500f, 100f)

	// Se valida que se reciban las tres distancias. Caso contrario no posible triangular y devolvemos null
	if (distancias.size != 3) {
		return null
	}

	// Distancia a cada satélite desde la fuente del mensaje. La suposición acá es que vienen en el orden propuesto
	// abajo. Que es el mismo orden en que acomodé las coordenadas del satelite arriba. (kenobi, skywalker, sato)
	val distanciaKenobi = distancias[0]
	val distanciaSkywalker = distancias[1]
	val distanciaSato = distancias[2]

	// Se calcula la distancia al cuadrado entre cada satélite y la fuente del mensaje
	val d1Sq = distanciaKenobi.pow(2)
	val d2Sq = distanciaSkywalker.pow(2)
	val d3Sq = distanciaSato.pow(2)

	// Se obtienen las coordenadas de cada satélite.
	val x1 = coordenadasKenobi.first
	val y1 = coordenadasKenobi.second
	val x2 = coordenadasSkywalker.first
	val y2 = coordenadasSkywalker.second
	val x3 = coordenadasSato.first
	val y3 = coordenadasSato.second

	// Se calculan las coordenadas obtenidas al cuadrado para luego usarse para obtener las diferencias:
	val x1Sq = x1.pow(2)
	val y1Sq = y1.pow(2)
	val x2Sq = x2.pow(2)
	val y2Sq = y2.pow(2)
	val x3Sq = x3.pow(2)
	val y3Sq = y3.pow(2)

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

	return Pair(x, y)
}


fun getMessege(messeges : List<List<String>>) : String {

	// Misma suposición que en getLocation(). Los mensajes ingresan ordenados [0] = Kenobi, [1] = Skywalker, [2] = Sato
	val mensajeKenobi = messeges[0]
	val mensajeSkywalker = messeges[1]
	val mensajeSato = messeges[2]

	/* Ejemplo
    Kenobi: [ "", "este", "es", "un", "mensaje"]
    Skywalker: [ "Hola", "este", "", "un", "mensaje"]
    Sato: [ "", "", "es", "", "mensaje"]
     */

	val mensajeCompletoList  = mutableListOf<String>()
	var largoMensajeCompletoList = 0

	messeges.forEach {
		if (it.size > largoMensajeCompletoList) {
			largoMensajeCompletoList = it.size
		}
	}

	repeat(largoMensajeCompletoList) {
		mensajeCompletoList.add("")
	}

	for (i in mensajeCompletoList.indices) {
		if (mensajeCompletoList[i] == "") {
			if (mensajeKenobi[i] != "") {
				mensajeCompletoList[i] = mensajeKenobi[i]
			} else if (mensajeSkywalker[i] != "") {
				mensajeCompletoList[i] = mensajeSkywalker[i]
			} else {
				mensajeCompletoList[i] = mensajeSato[i]
			}
		}
	}

	return mensajeCompletoList.joinToString(" ")
}