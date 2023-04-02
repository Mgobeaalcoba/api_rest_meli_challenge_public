package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Clase que define una prueba unitaria de ejemplo para asegurar que el contexto de la aplicación se cargue correctamente.
 *
 * Para ello se utiliza la anotación @SpringBootTest que carga la aplicación y su contexto para que puedan ser utilizados en las pruebas.
 * La anotación @ExtendWith es necesaria para habilitar el soporte de Spring en JUnit 5.
 *
 * @see org.junit.jupiter.api.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.junit.jupiter.api.extension.ExtendWith
 * @see org.springframework.test.context.junit.jupiter.SpringExtension
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [apiRest.RunApplication::class])
class DemoApplicationTests {

	/**
	 * Método de prueba que simplemente verifica que el contexto de la aplicación se cargue correctamente.
	 * Si el contexto se carga sin errores, entonces la prueba es exitosa.
	 * Es una forma automatizada que realiza las mismas pruebas que podríamos realizar con un cliente y haciendo
	 * peticiones POST y GET a los endpoints de nuestra API REST.
	 */
	@Test
	fun contextLoads() {
	}

}
