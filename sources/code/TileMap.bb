;  ,---( Tiles Tutorial )---------------------.    ,-----------------------------------------------.
;  |                                          |\   | This baby is stuffed with little math tricks, |\
;  |  Questions?  Email: TheChange@yahoo.com  | |  | like 0/1 based maths, but overall a practical | |
;  |                                          | |  | thingy giving a technical view on tilemaps.   | |
;  |------------------------( 25 Feb 2002 )---' |  |-----------------------------------------------' |
;    \_________________________________________/     \______________________________________________/

;-[ Definition ]-------------------------------------------------------------------------------------------------1-2-0--

AppTitle ( "Tiles'n Tilemaps" )

Const KeyEscape% = 1          ; It's smart putting stuff like this here instead of looking it up every sec
Const KeyCursorUp% = 200      ;
Const KeyCursorDown% = 208    ;
Const KeyCursorLeft% = 203    ;
Const KeyCursorRight% = 205   ;
Const KeyControlRight% = 157  ;

Const ScreenWidth% = 800    ; Keeping our nose clean, if you get what I'm sayin'
Const ScreenHeight% = 600   ;
Const ColorDepthBits% = 16  ;

Const TileCount% = 4        ; How many different tiles we wanna have
Const TileWidth% = 32       ; How many pixels wide every tile is
Const TileHeight% = 32      ; How many pixels high every tile is
Const TileMapWidth% = 016   ; How many tiles wide our map is
Const TileMapHeight% = 016  ; How many tiles high our map is

;-[ Initialization ]---------------------------------------------------------------------------------------------1-2-0--

Graphics ( ScreenWidth , ScreenHeight , ColorDepthBits , 0 )

SeedRnd ( MilliSecs () )  ; If we use random, we wanna do it right, right?

Global CounterX% = 0  ; Some temporary counters to be used in loops and stuff
Global CounterY% = 0  ;

Global AimX% = 0  ; Where we are aiming at
Global AimY% = 0  ;

Global TargetBitmap = CreateImage ( 6 , 6 )  ; This will represent our butt, target or crosshair, in case we become violent
MidHandle ( TargetBitmap )  ; Center the hotspot in order to draw the thing centered, without worries
SetBuffer ( ImageBuffer ( TargetBitmap ) )
Color ( 255 , 000 , 000 )
Oval ( 0 , 0 , ImageWidth ( TargetBitmap ) , ImageHeight ( TargetBitmap ) , 1 )  ; A red filled oval just like laser guidance

Global TileData = CreateImage ( TileWidth , TileHeight , TileCount )  ; Making space for our tiles-to-be

SetBuffer ( ImageBuffer ( TileData , 0 ) )  ; Tile #0 is totally transparent
Color ( 000 , 000 , 000 )
Rect ( 0 , 0 , TileWidth , TileHeight , 1 )  ; We don't actually have to draw this, it's empty by default

SetBuffer ( ImageBuffer ( TileData , 1 ) )  ; Tile #1 is a greenish-blueish square
Color ( 000 , 095 , 063 )
Rect ( 0 , 0 , TileWidth - 1 , TileHeight - 1 , 1 )  ; Leaving some space to see where exactly the tile ends

SetBuffer ( ImageBuffer ( TileData , 2 ) )  ; Tile #2 is a blueish-purpleish square
Color ( 063 , 000 , 127 )
Rect ( 0 , 0 , TileWidth - 1 , TileHeight - 1 , 1 )

SetBuffer ( ImageBuffer ( TileData , 3 ) )  ; Tile #3 is a grey hollow square
Color ( 079 , 079 , 079 )
Rect ( 0 , 0 , TileWidth - 1 , TileHeight - 1 , 0 )

Dim TileMap% ( TileMapWidth - 1 , TileMapHeight - 1 )  ; And we finally create our tilemap
For CounterY = 0 To TileMapHeight - 1
  For CounterX = 0 To TileMapWidth - 1
    TileMap ( CounterX , CounterY ) = Rnd ( TileCount - 1 )  ; Placing randomly selected tiles in it
  Next
Next

Global BackLineX% = 0  ; Some graphical nonsense to show tile transparency
Global BackLineY% = 0  ; It's actually two lines moving from side to side, then warping

ClsColor ( 047 , 031 , 015 )  ; Our transparent background color

SetBuffer ( BackBuffer () )  ; Remember to set the drawing cursor right

;-[ Main ]-------------------------------------------------------------------------------------------------------1-2-0--

Repeat

  Done = Process ()  ; Do everything except displaying stuff, like getting user input and updating objects
  Cls ()  ; Clear the backbuffer
  Display ()  ; Put everything on the backbuffer
  Flip ()  ; Flip the backbuffer to the frontbuffer (on screen)

Until ( Done )

End ()

;-[ Sub ]--------------------------------------------------------------------------------------------------------1-2-0--

Function Process ()  ; Returns True when user wanna quit

  BackLineX = BackLineX + 2  ; Moving our silly (or high-tech) lines a bit (left side to right side)
  BackLineY = BackLineY + 2  ;
  If ( BackLineX >= ScreenWidth )  BackLineX = 0  ; If our silly lines are going off the screen, put them back (warp)
  If ( BackLineY >= ScreenHeight ) BackLineY = 0  ;

  If ( KeyDown ( KeyCursorUp ) )    AimY = AimY - 1  ; Using the cursorkeys alters our aim
  If ( KeyDown ( KeyCursorDown ) )  AimY = AimY + 1  ;
  If ( KeyDown ( KeyCursorLeft ) )  AimX = AimX - 1  ;
  If ( KeyDown ( KeyCursorRight ) ) AimX = AimX + 1  ;

  Return ( KeyDown ( KeyEscape ) )  ; Program ends when Escape has been pressed

End Function

;-->

Function Display ()

  Color ( 127 , 000 , 000 )
  Line ( BackLineX , 0 , BackLineX , ScreenHeight - 1 )  ; Using the X for the vertical line
  Line ( 0 , BackLineY , ScreenWidth - 1 , BackLineY )  ; And the Y for the horizontal, a bit unlogical though

  Local BeginScreenX% = AimX - ScreenWidth / 2   ; Where the graphics begin on the screen (upper left corner)
  Local BeginScreenY% = AimY - ScreenHeight / 2  ;
  Local EndScreenX% = AimX + ScreenWidth / 2     ; Where the graphics end on the screen (lower right corner)
  Local EndScreenY% = AimY + ScreenHeight / 2    ;
  Local BeginTileX% = BeginScreenX / TileWidth     ; Which tiles need to be visible on the screen
  Local BeginTileY% = BeginScreenY / TileHeight    ;
  Local EndTileX% = EndScreenX / TileWidth         ;
  Local EndTileY% = EndScreenY / TileHeight        ;

  If ( BeginTileX <  0 )             BeginTileX = 0                  ; Making sure we don't access bogus tiles
  If ( BeginTileY <  0 )             BeginTileY = 0                  ;
  If ( BeginTileX >= TileMapWidth  ) BeginTileX = TileMapWidth  - 1  ;
  If ( BeginTileY >= TileMapHeight ) BeginTileY = TileMapHeight - 1  ;
  If ( EndTileX <  0 )             EndTileX = 0                  ; That means we don't want to display tiles which don't exist
  If ( EndTileY <  0 )             EndTileY = 0                  ;
  If ( EndTileX >= TileMapWidth  ) EndTileX = TileMapWidth  - 1  ;
  If ( EndTileY >= TileMapHeight ) EndTileY = TileMapHeight - 1  ;

  Local PositionX% = 0  ; Where each tile should be placed
  Local PositionY% = 0  ;

  For CounterY = BeginTileY To EndTileY    ; Drawing only the necessary tiles
    For CounterX = BeginTileX To EndTileX  ;
      PositionX = CounterX * TileWidth - BeginScreenX   ; This only makes sense to me when I visualize it (or sketch)
      PositionY = CounterY * TileHeight - BeginScreenY  ;
      DrawImage ( TileData , PositionX , PositionY , TileMap ( CounterX , CounterY ) )  ; Da works
    Next
  Next

  DrawImage ( TargetBitmap , ScreenWidth / 2 , ScreenHeight / 2 )  ; The butt

  Color ( 127 , 255 , 191 )  ; The essential information onscreen at runtime
  Text ( 0 , 00 , "Target ( " + AimX + " , " + AimY + " ) " )
  Text ( 0 , 16 , "Screen ( " + BeginScreenX + " , " + BeginScreenY + " ) ( " + EndScreenX + " , " + EndScreenY + " ) " )
  Text ( 0 , 32 , "Tiles  ( " + BeginTileX + " , " + BeginTileY + " ) ( " + EndTileX + " , " + EndTileY + " ) " )

End Function

;-->

; ,---( Remember that )-----------------.
; |   There's always something to learn |
; |   One is never too old To learn     |
; |   It doesn't matter what you learn  |
; |    because anything can be taught   |
; |---------------------------( T-C )---'

;-->