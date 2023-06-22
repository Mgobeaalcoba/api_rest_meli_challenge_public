package apiRest

import apiRest.controller.RunApplication
import org.springframework.boot.runApplication

/**
 * Punto de entrada de la aplicación. Inicia la aplicación Spring Boot con la clase [RunApplication].
 *
 * @param args Argumentos pasados por línea de comandos.
 */
fun main(args: Array<String>) {
	runApplication<RunApplication>(*args)
}
