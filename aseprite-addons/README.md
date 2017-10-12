# Aseprite Addons

Class to load Aseprite export Json file and
help to create animation from the aseprite definition

Example:


```
val assets = AssetManager()

assets.setLoader(AsepriteJson::class.java, AsepriteJsonLoader(InternalFileHandleResolver()))
assets.setLoader(Aseprite::class.java, AsepriteLoader(InternalFileHandleResolver()))

// will load player.png and player.json
assets.load("player", Aseprite::class.java)
  
// ...

val player: Aseprite = assets.get("player", Aseprite::class.java)
// will get the jump animation
val jump = player["jump"]         
```