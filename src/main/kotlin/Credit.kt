import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.html.*

fun Route.makeCredit() {
    get("/credit"){
        call.respondHtml {
            head{
                title("喝喝水麻将屋")
            }
            body{
                style = "text-align: center; font-family: sans-serif"
                heading()
                + "Project by XGN from HHS 2022"
                br{}
                + "This project is a demonstration of the JMahjong library. It is powered by Ktor."
                br{}
                a("https://github.com/XiaoGeNintendo/JMahjong"){+"JMahjong"}
                br{}
                a("https://ktor.io/"){+"Ktor"}
                br{}
                a("https://blog.hellholestudios.top/"){+"HellHoleStudios"}
                br{}
                a("https://github.com/HellHoleStudios/HHS-Mahjong-Site"){+"Source Code"}
            }
        }
    }
}