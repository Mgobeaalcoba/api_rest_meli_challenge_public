package apiRest.config

import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun apis(): List<GroupedOpenApi> {
        return listOf(
            GroupedOpenApi.builder()
                .group("api-rest")
                .pathsToMatch("/")
                .packagesToScan("apiRest.controller")
                .build()
        )
    }
}
