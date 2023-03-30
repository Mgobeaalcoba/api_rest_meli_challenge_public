package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [apiRest.RunApplication::class])
class DemoApplicationTests {

	@Test
	fun contextLoads() {
	}

}
