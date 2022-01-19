import top.hellholestudios.xgn.jmj.Tile
import top.hellholestudios.xgn.jmj.Tiles

fun Tiles.toImageLocation(red:Boolean=false):String{
    return if(red){
        "/static/tiles/${ordinal+1}r.png"
    }else{
        "/static/tiles/${ordinal+1}.png"
    }
}

const val back="/static/tiles/back.png"
const val db="/static/tiles/db.png"

fun Tile.toImageLocation():String{
    return if(red){
        "/static/tiles/${id+1}r.png"
    }else{
        "/static/tiles/${id+1}.png"
    }
}