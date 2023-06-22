package com.example.demo

import apiRest.controller.TopSecretController
import apiRest.main
import apiRest.model.dataClases.Satellite
import apiRest.model.dataClases.TopSecretRequest
import apiRest.model.dataClases.TopSecretResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class for the main function.
 */
class MainTest {

	/**
	 * Test case for the main function.
	 * It executes the main function without throwing exceptions.
	 * If the execution reaches this point without throwing exceptions, the test is considered successful.
	 */
	@Test
	fun testMain() {
		main(emptyArray())
	}
}

/**
 * Class that defines an example unit test to ensure that the application context loads correctly.
 *
 * For this, the @SpringBootTest annotation is used, which loads the application and its context so that they can be used in the tests.
 * The @ExtendWith annotation is required to enable Spring support in JUnit 5.
 *
 * @see org.junit.jupiter.api.Test
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.junit.jupiter.api.extension.ExtendWith
 * @see org.springframework.test.context.junit.jupiter.SpringExtension
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [apiRest.controller.RunApplication::class])
class DemoApplicationTests {

	/**
	 * Test method that simply verifies that the application context is loaded correctly.
	 * If the context loads without errors, then the test is successful.
	 * It is an automated way that performs the same tests that we could perform with a client and doing
	 * POST and GET requests to endpoints from our REST API.
	 */
	@Test
	fun contextLoads() {
	}

	/* TODO: Make unit test for all classes in API */
}

/**
 * Test class for the TopSecretController.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TopSecretController::class])
class TopSecretControllerTest {

	private lateinit var controller: TopSecretController

	/**
	 * Setup method executed before each test case.
	 * Initializes the controller instance.
	 */
	@BeforeEach
	fun setUp() {
		controller = TopSecretController()
	}

	/**
	 * Test case for the processTopSecret method.
	 * It tests the processing of a TopSecretRequest and verifies the response.
	 */
	@Test
	fun testProcessTopSecret() {
		val satellite1 = Satellite("Kenobi", 100.0, listOf("message1", "", "message3"))
		val satellite2 = Satellite("Skywalker", 150.0, listOf("", "message2", "message3"))
		val satellite3 = Satellite("Sato", 200.0, listOf("message1", "message2", ""))
		val request = TopSecretRequest(listOf(satellite1, satellite2, satellite3))

		val response = controller.processTopSecret(request)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)

		val responseBody = response.body as TopSecretResponse

		assertEquals(-492.1875, responseBody.position.x)
		assertEquals(1540.625, responseBody.position.y)
		assertEquals("message1 message2 message3", responseBody.message)
	}

	/**
	 * Test case for the postSplitSatellite method.
	 * It tests the processing of a split satellite data and verifies the response.
	 */
	@Test
	fun testPostSplitSatellite() {
		val satelliteName = "Kenobi"
		val body = mapOf("distance" to 100.0, "message" to listOf("message1", "", "message3"))

		val response = controller.postSplitSatellite(satelliteName, body)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertEquals("Data for satellite kenobi updated successfully.", response.body)
	}

	/**
	 * Test case for the getSplitSatellite method.
	 * It tests the retrieval of information for a split satellite and verifies the response.
	 */
	@Test
	fun testGetSplitSatellite() {
		val satelliteName = "Kenobi"

		val response = controller.getSplitSatellite(satelliteName)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)

		val responseBody = response.body as TopSecretResponse
		assertEquals(-500.0, responseBody.position.x)
		assertEquals(-200.0, responseBody.position.y)
		assertEquals("Error: no hay suficiente información para mostrar el mensaje completo", responseBody.message)
	}
}
