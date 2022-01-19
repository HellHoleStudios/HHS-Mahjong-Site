import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.html.*
import top.hellholestudios.xgn.jmj.Tiles

fun BODY.heading() {
    img(src = Tiles.Red.toImageLocation())
    h1 { +"喝喝水麻将屋" }
    hr { }
    i{+"本网站不保存您的任何信息。提交的请求和记录也可能在任何时候被删除。"}
    p {
        a("/") { +"主页" }
        +" "
        a("/credit") { +"Credits" }
    }
    hr {}
}

fun Route.makeMainPage() {
    get("/") {
        call.respondHtml {
            head {
                title("喝喝水麻将屋")
            }
            body {
                style = "text-align: center; font-family: sans-serif"

                heading()
                //the form
                table {
                    h2 { +"牌理查询" }
                    +"在下面输入牌，就可以获取到向听数、牌理、胡牌点数、役种等信息啦！"
                    br {}
                    form(action = "ticket", method = FormMethod.post) {
                        abbr {
                            title = "最后一张是进张/和牌"
                            +"手牌"
                        }
                        +"："
                        input {
                            name = "hand"
                            placeholder = "形如123m406p789s12345z"
                            width = "300"
                        }
                        br {}
                        abbr {
                            title = "使用#分隔暗杠（先）和副露（后）。用英文逗号(,)分隔每个面子。没有#默认只有副露。"
                            +"副露"
                        }
                        +"："
                        input {
                            name = "fuuro"
                            placeholder = "形如1111m#1111p,234p,444s"
                            width = "300"
                        }
                        br {}
                        +"宝牌："
                        input {
                            name = "dora"
                            placeholder = "形如1m2z3s"
                            width = "85"
                        }
                        br {}
                        +"进张来源："
                        select {
                            name = "source"
                            option {
                                value = "tsumo"
                                +"自摸"
                            }
                            option {
                                value = "ron"
                                +"荣和"
                            }
                            option {
                                value = "rinshan"
                                +"岭上"
                            }
                            option {
                                value = "haitei"
                                +"海底（最后一张自摸）"
                            }
                            option {
                                value = "houtei"
                                +"河底（最后一张荣和）"
                            }
                            option {
                                value = "kan"
                                +"抢杠"
                            }
                            option {
                                value = "firstTsumo"
                                +"首发自摸（天和地和）"
                            }
                        }
                        br {}
                        +"立直情况："
                        select {
                            name = "riichi"
                            option {
                                value = "no"
                                +"无"
                            }
                            option {
                                value = "one"
                                +"立直"
                            }
                            option {
                                value = "two"
                                +"双立直"
                            }
                        }
                        input {
                            name = "ippatsu"
                            type = InputType.checkBox
                            +"一发"
                        }
                        br {}
                        +"场风："
                        select {
                            name = "pwind"
                            option {
                                value = "east"
                                +"东"
                            }
                            option {
                                value = "south"
                                +"南"
                            }
                            option {
                                value = "west"
                                +"西"
                            }
                            option {
                                value = "north"
                                +"北"
                            }
                        }
                        +"自风："
                        select {
                            name = "swind"
                            option {
                                value = "east"
                                +"东"
                            }
                            option {
                                value = "south"
                                +"南"
                            }
                            option {
                                value = "west"
                                +"西"
                            }
                            option {
                                value = "north"
                                +"北"
                            }
                        }
                        br {}
                        input {
                            type = InputType.submit
                        }
                    }
                }

                hr {}
                h1 { +"最近成功查询" }
                for (x in 1..20) {
                    val step = requests.size - x
                    if (step < 0) {
                        break
                    }
                    val req = requests[step]
                    if (req.respondStatus == "success") {
                        a("view/$step") { h2 { +"查询$step" } }
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
                                img(src = Tiles.from(i.tile).toImageLocation())
                                img(src = Tiles.from(i.tile).toImageLocation())
                                img(src = back)
                            }
                            +" "
                        }
                        if (req.gFuuro!!.isNotEmpty()) {
                            +"副露"
                            for (i in req.gFuuro!!) {
                                for (t in i.toTiles()) {
                                    img(src = t.toImageLocation())
                                }
                            }
                            +" "
                        }
                        if (req.gDora!!.isNotEmpty()) {
                            +"宝牌"
                            for (i in req.gDora!!) {
                                img(src = Tiles.from(i).toImageLocation())
                            }
                        }
                        br{}
                        pre{+req.respondText}


                        hr{}
                    }

                }
            }
        }
    }
}