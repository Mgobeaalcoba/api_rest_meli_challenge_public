package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
