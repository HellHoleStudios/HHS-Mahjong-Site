import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main(args: Array<String>) {
    println("Running Mahjong Server")
    embeddedServer(Netty, 9977) {
        routing {
            static("/static") {
                resources("static")
            }
            makeMainPage()
            makeTicket()
            makeView()
            makeCredit()
        }
    }.start(wait = true)

}