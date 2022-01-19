import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import top.hellholestudios.xgn.jmj.*
import top.hellholestudios.xgn.jmj.scoring.AgariInfo
import top.hellholestudios.xgn.jmj.scoring.classic.DefaultRuleset
import top.hellholestudios.xgn.jmj.util.HandUtil
import java.math.BigInteger
import java.util.*

fun Route.makeTicket() {
    post("/ticket") {

        try {
            val form = call.receiveParameters()
            val pw = form["pwind"].toString()
            val sw = form["swind"].toString()
            val ippatsu = form["ippatsu"].toString()
            val hd = form["hand"].toString()
            val fuuro = form["fuuro"].toString()
            val dora = form["dora"].toString()
            val source = form["source"].toString()
            val riichi = form["riichi"].toString()

            val req = Request(hd, fuuro, dora, source, riichi, ippatsu, pw, sw)
            val id = addRequest(req)

            try {
                var tiles = HandUtil.fromNotationRaw(hd)
                val is13 = (tiles.size % 3 == 1)
                if (tiles.size % 3 == 1) { //13 tile special version
                    tiles += Tile(Tiles.Other.ordinal)
                }

                req.gTiles = tiles


                //parse hand
                val fullHand = Hand(tiles.clone())
                val hand = Hand(tiles.slice(0 until tiles.size - 1).toTypedArray())
                val lastTile = tiles.last()

                //parse fuuro
                val ankan: Array<Mentsu>
                val fuuros: Array<Mentsu>
                if ('#' in fuuro) {
                    val x = fuuro.split("#")
                    val x0 = x[0].split(",")
                    val x1 = x[1].split(",")

                    val y0 = x0.toMutableList()
                    y0.removeIf { it.trim() == "" }
                    val y1 = x1.toMutableList()
                    y1.removeIf { it.trim() == "" }

                    ankan = y0.map { HandUtil.toMentsu(HandUtil.fromNotationRaw(it.trim())) }.toTypedArray()
                    fuuros = y1.map { HandUtil.toMentsu(HandUtil.fromNotationRaw(it.trim())) }.toTypedArray()
                } else {
                    ankan = emptyArray()
                    val x = fuuro.split(",").toMutableList()
                    x.removeIf { it.trim() == "" }
                    fuuros = x.map { HandUtil.toMentsu(HandUtil.fromNotationRaw(it)) }.toTypedArray()
                }
                req.gAnkans = ankan
                req.gFuuro = fuuros

                //parse dora
                val doras = HandUtil.toIntArray(HandUtil.fromNotationRaw(dora))
                req.gDora = doras

                //make agari info
                val agariInfo = AgariInfo(
                    req.toTileSource(),
                    req.toWind(req.prevalentWind),
                    req.toWind(req.seatWind),
                    doras
                )

                agariInfo.riichi = req.toRiichi()
                agariInfo.ippatsu = (ippatsu == "on")

                //ruleset
                val ruleset = DefaultRuleset()

                //check shanten
                val shanten = ShantenCalculator.getShanten(fullHand.toCountArray(), ruleset)
                val suggest = ShantenCalculator.suggest(fullHand.toCountArray(), ruleset)

                req.respondText = when (shanten) {
                    -1 -> {
                        "胡了可能\n"
                    }
                    0 -> {
                        "听牌可能\n"
                    }
                    else -> {
                        "$shanten 向听\n"
                    }
                }

                //validate hand
                try {
                    req.validateResult = HandValidator.validate(hand, lastTile, ankan, fuuros, false)
                } catch (e: Exception) {
                    //validation failed
                }


                if (shanten != -1) {
                    req.respondText += "牌理建议：\n"
                    for (s in suggest) {
                        if (is13) {
                            if (s.throwID == Tiles.Other.ordinal) {
                                req.respondText += "待${s.waitID.map { Tiles.from(it).utf }}共${s.efficiency}张\n"
                            }
                        } else {
                            req.respondText += "打${Tiles.from(s.throwID).utf}可进${s.waitID.map { Tiles.from(it).utf }}共${s.efficiency}张\n"
                        }
                    }
                } else {
                    val desc = ruleset.describe(hand, lastTile, ankan, fuuros, emptyArray(), agariInfo)
                    req.respondText += "胡了情况：\n"
                    req.respondText += "${desc.han}翻${desc.fu}符\n"
                    req.respondText += "得点：${ruleset.score(desc, agariInfo)}\n"

                    //calculate aotenjou scoring
                    if(desc.han<=1000) {
                        val H = BigInteger.valueOf(100)
                        var bg = BigInteger("2").pow((desc.han + 2).toInt())
                        bg = bg * BigInteger.valueOf(desc.fu.toLong()) * BigInteger.valueOf(if (agariInfo.isDealer) 6 else 4)
                        if (bg % H != BigInteger.ZERO) {
                            bg = (bg / H + BigInteger.ONE) * H
                        }
                        req.respondText+="得点（青天井）：$bg\n"
                    }

                    for (i in desc.yakumans + desc.yakus) {
                        req.respondText += "${ruleset.yakus[i.key]!!.displayName} ${i.value}翻\n"
                    }

                    val breakdown = HandUtil.breakdown(fullHand.toCountArray())
                    req.respondText += "手牌拆解：\n"
                    for (i in breakdown) {
                        req.respondText += "${Arrays.toString(i.mentsus)}+${i.head}\n"
                    }
                }

                req.respondStatus = "success"
            } catch (e: Exception) {
                req.respondStatus = "error"
                req.respondText = "在完成任务的过程中发生了错误：" + e.stackTraceToString()
            }

            println(req)
            call.respondRedirect("view/${id}")
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, "出现了不可预知的异常……")
        }
    }
    get("/ticket") {

    }
}