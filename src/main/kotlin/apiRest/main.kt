package apiRest

import apiRest.controller.RunApplication
import org.springframework.boot.runApplication

/**
 * Application entry point. Start the Spring Boot application with the [RunApplication] class.
 *
 * @param args Arguments passed by command line.
 */
fun main(args: Array<String>) {
	runApplication<RunApplication>(*args)
}
