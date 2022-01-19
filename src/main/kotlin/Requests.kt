import top.hellholestudios.xgn.jmj.Mentsu
import top.hellholestudios.xgn.jmj.Tile
import top.hellholestudios.xgn.jmj.ValidatorResult
import top.hellholestudios.xgn.jmj.Wind
import top.hellholestudios.xgn.jmj.scoring.AgariInfo
import java.util.*

data class Request(
    val hand: String,
    val fuuro: String,
    val dora: String,
    val source: String,
    val riichi: String,
    val ippatsu: String,
    val prevalentWind: String,
    val seatWind: String,
    var respondStatus: String = "preparing",
    var respondText: String = "少女祈祷中……",
    var gTiles: Array<Tile>? = null,
    var gAnkans: Array<Mentsu>? = null,
    var gFuuro: Array<Mentsu>? = null,
    var gDora: IntArray? = null,
    var validateResult: ValidatorResult? = null
) {

    fun toWind(s: String): Wind? {
        return when (s) {
            "east" -> Wind.East
            "west" -> Wind.West
            "north" -> Wind.North
            "south" -> Wind.South
            else -> null
        }
    }

    fun toTileSource(): AgariInfo.TileSource? {
        return when (source) {
            "tsumo" -> AgariInfo.TileSource.Tsumo
            "ron" -> AgariInfo.TileSource.Ron
            "firstTsumo" -> AgariInfo.TileSource.FirstTsumo
            "haitei" -> AgariInfo.TileSource.Haitei
            "houtei" -> AgariInfo.TileSource.Houtei
            "kan" -> AgariInfo.TileSource.Kan
            "rinshan" -> AgariInfo.TileSource.Rinshan
            else -> null
        }
    }

    fun toRiichi(): AgariInfo.Riichi? {
        return when (riichi) {
            "no" -> AgariInfo.Riichi.NoRiichi
            "one" -> AgariInfo.Riichi.Riichi
            "two" -> AgariInfo.Riichi.WRiichi
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Request

        if (hand != other.hand) return false
        if (fuuro != other.fuuro) return false
        if (dora != other.dora) return false
        if (source != other.source) return false
        if (riichi != other.riichi) return false
        if (ippatsu != other.ippatsu) return false
        if (prevalentWind != other.prevalentWind) return false
        if (seatWind != other.seatWind) return false
        if (respondStatus != other.respondStatus) return false
        if (respondText != other.respondText) return false
        if (gTiles != null) {
            if (other.gTiles == null) return false
            if (!gTiles.contentEquals(other.gTiles)) return false
        } else if (other.gTiles != null) return false
        if (gAnkans != null) {
            if (other.gAnkans == null) return false
            if (!gAnkans.contentEquals(other.gAnkans)) return false
        } else if (other.gAnkans != null) return false
        if (gFuuro != null) {
            if (other.gFuuro == null) return false
            if (!gFuuro.contentEquals(other.gFuuro)) return false
        } else if (other.gFuuro != null) return false
        if (gDora != null) {
            if (other.gDora == null) return false
            if (!gDora.contentEquals(other.gDora)) return false
        } else if (other.gDora != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hand.hashCode()
        result = 31 * result + fuuro.hashCode()
        result = 31 * result + dora.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + riichi.hashCode()
        result = 31 * result + ippatsu.hashCode()
        result = 31 * result + prevalentWind.hashCode()
        result = 31 * result + seatWind.hashCode()
        result = 31 * result + respondStatus.hashCode()
        result = 31 * result + respondText.hashCode()
        result = 31 * result + (gTiles?.contentHashCode() ?: 0)
        result = 31 * result + (gAnkans?.contentHashCode() ?: 0)
        result = 31 * result + (gFuuro?.contentHashCode() ?: 0)
        result = 31 * result + (gDora?.contentHashCode() ?: 0)
        return result
    }

}

val requests = Vector<Request>()

fun addRequest(r: Request): Int {
    requests.add(r)
    return requests.size - 1
}
