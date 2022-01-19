import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import top.hellholestudios.xgn.jmj.Tiles
import top.hellholestudios.xgn.jmj.ValidatorResult

fun Route.makeView() {
    get("/view/{id}") {
        try {
            val id = call.parameters["id"]!!.toInt()
            val req = requests[id]

            call.respondHtml {
                head {
                    title("喝喝水麻将屋")
                }
                body {
                    style = "text-align: center; font-family: sans-serif"
                    heading()
                    h1 { +"记录#$id" }
                    h2 { +"手牌" }
                    try {
                        if (req.riichi == "one") {
                            img(src = db)
                            br {}
                        }
                        if (req.riichi == "two") {
                            img(src = db)
                            br {}
                            img(src = db)
                            br {}
                        }

                        val tiles = req.gTiles!!
                        for (i in 0 until tiles.size - 1) {
                            img(src = tiles[i].toImageLocation())
                        }
                        +"进"
                        img(src = tiles.last().toImageLocation())

                        if (req.gAnkans!!.isNotEmpty()) {
                            +"暗杠"
                            for (i in req.gAnkans!!) {
                                img(src = back)
                                print(i.redDora)
                                if (i.redDora.count { it } >= 1) {
                                    img(src = Tiles.from(i.tile).toImageLocation(red = true))
                                } else {
                                    img(src = Tiles.from(i.tile).toImageLocation())
                                }

                                if (i.redDora.count { it } >= 2) {
                                    img(src = Tiles.from(i.tile).toImageLocation(red = true))
                                } else {
                                    img(src = Tiles.from(i.tile).toImageLocation())
                                }
                                img(src = back)
                                +" "
                            }
                            +" "
                        }
                        if (req.gFuuro!!.isNotEmpty()) {
                            +"副露"
                            for (i in req.gFuuro!!) {
                                for (t in i.toTiles()) {
                                    img(src = t.toImageLocation())
                                }
                                +" "
                            }
                            +" "
                        }
                        if (req.gDora!!.isNotEmpty()) {
                            +"宝牌"
                            for (i in req.gDora!!) {
                                img(src = Tiles.from(i).toImageLocation())
                            }
                        }
                    } catch (e: Exception) {
                        +"?"
                    } finally {
                        br {}
                        code { +"'${req.hand}'副露'${req.fuuro}'宝牌'${req.dora}'" }
                    }


                    h2 { +"手牌验证" }
                    +"验证结果:${req.validateResult}(${req.validateResult?.level})"

                    h2 { +"处理情况" }
                    +req.respondStatus

                    h2 { +"处理结果" }
                    pre { +req.respondText }

                    h2 { +"场况信息" }
                    +"场风：${req.toWind(req.prevalentWind)}"
                    br {}
                    +"自风：${req.toWind(req.seatWind)}"
                    br {}
                    +"立直情况：${req.toRiichi()}（一发:${req.ippatsu == "on"}）"
                    br {}
                    +"来源情况：${req.toTileSource()}"
                    br {}
                    +"自摸：${req.toTileSource()!!.isTsumoFamily}"

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, "遇到了不可料见的错误：" + e.stackTraceToString())
        }

    }
}