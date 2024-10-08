package ht.eyfout.example

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.http.ControllerAPI
import ht.eyfout.example.http.ExampleStateScopeProvider
import ht.eyfout.junit.jupiter.gherkin.api.GherkinDynamicTest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory

@MicronautTest
class GroupedHttpRequestsTests {

    @MockBean(DMVClient::class)
    val dmvClient: DMVClient = mockk()

    @Inject
    lateinit var provider: ExampleStateScopeProvider

    @TestFactory
    fun grouped() = GherkinDynamicTest
        .dynamicTest(provider)
        .given("an unauthorized request") {
            it.GETManufacturerAnswer(null) {
                HttpResponse.unauthorized()
            }
            it.GETVehiclesAnswer(null, null) {
                HttpResponse.unauthorized()
            }
        }.`when`("calling service api") {
            it.httpRequest(ControllerAPI.VehiclesByManufacturerName)
                .queryParam("make", "Nissan")
                .header("Authorization", "other")

            it.httpRequest(ControllerAPI.VehiclesByManufacturerID)
                .header("Authorization", "other")
                .pathParam("manufacturerID", "Nissan-#1")

            it.httpRequest(ControllerAPI.Manufacturers)
                .header("Authorization", "other")
        }.then("Http 401") {
            assertEquals(HttpStatus.UNAUTHORIZED.code, it.httpResponse().statusCode())
        }

}