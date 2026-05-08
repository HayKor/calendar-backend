package common

import com.haykor.plugins.configureSerialization
import com.haykor.plugins.configureStatusPages
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import org.koin.test.KoinTest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

abstract class BaseRouteTest : KoinTest {

    abstract fun testModules(): List<Module>

    fun ApplicationTestBuilder.baseSetup(vararg extraModules: Module) {
        application {
            this@application.configureSerialization()
            this@application.configureStatusPages()
            this@application.install(Koin) {
                modules(testModules() + extraModules.toList())
            }
            authentication {
                jwt("auth-jwt") {
                    verifier { verifier }
                    validate { JWTPrincipal(it.payload) }
                }
            }
            this@application.routing {
                configureRoutes()
            }
        }
        client = createClient {
            install(ClientContentNegotiation) { json() }
        }
    }

    abstract fun Routing.configureRoutes()
}
