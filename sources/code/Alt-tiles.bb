;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Alternating terrain-tile thing
;;By Michael Scherer          
;;          [RadiumGames]     
;;Can be extended to include other things beside trees like
;;  bushes, rocks, or even the starting locations of animals
;;  or beasts/enemies or allies.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;these hold what each of the tile type numbers are, i didn't end up needing them, so
;i may as well not have them at all, but i might need it for the finished program
Const dirt = 0, grass = 1, trees = 2, water = 3

Graphics 800,600

;seed the rand so the map isn't identical
SeedRnd(MilliSecs())

;each row is the type of tile, column is which tile
Dim tile(4,2)

;dirt
tile(0,0) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(0,0))
Color 255,150,0
Rect 0,0,64,64,True

tile(0,1) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(0,1))
Color 255,70,0
Rect 0,0,64,64,True


;grass
tile(1,0) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(1,0))
Color 0,255,0
Rect 0,0,64,64,True

tile(1,1) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(1,1))
Color 150,170,0
Rect 0,0,64,64,True


;trees
tile(2,0) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(2,0))
Color 150,170,0
Rect 0,0,64,64,True

tile(2,1) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(2,1))
Color 0,170,0
Rect 0,0,64,64,True


;water
tile(3,0) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(3,0))
Color 0,0,255
Rect 0,0,64,64,True

tile(3,1) = CreateImage(64,64)
SetBuffer ImageBuffer(tile(3,1))
Color 0,0,150
Rect 0,0,64,64,True


;trees (the actual trees)
Type scene
     Field x,y
     Field style
End Type

Dim scenery(2)

scenery(0) = CreateImage(15,15)
SetBuffer ImageBuffer(scenery(0))
Color 0,100,0
Oval 0,0,15,15,True

scenery(1) = CreateImage(15,15)
SetBuffer ImageBuffer(scenery(1))
Color 0,255,0
Oval 0,0,15,15,True


SetBuffer BackBuffer()


;the number of rows&columns that the map will have, if you alter data
;statements, be sure to change this to corespond with the other numbers
Global rows = 10, columns = 15

;the array for the map
Dim map(rows,columns,2)

;offset so you can move around
Global offsetx, offsety

;load the tiles form the data statements below
readtiles()

;----------------------------------------Mainloop---------------

While Not KeyDown(1)
     Cls
     
     checkkeys()
     
     drawtiles()
     
     UpdateScenery()
     
     Delay 50
     Flip
Wend


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;Funcs;;;;;;;;;;;;;;;;;;;;;
;(i think the names are pretty strait foward)



Function checkkeys()
     ;left
     If KeyDown(203)
          offsetx = offsetx - 5
     EndIf

     ;right
     If KeyDown(205)
          offsetx = offsetx + 5
     EndIf

     ;up
     If KeyDown(200)
          offsety = offsety - 5
     EndIf
     
     ;down
     If KeyDown(208)
          offsety = offsety + 5
     EndIf
End Function



Function readtiles()
     Restore mapdata
     
     For h = 1 To rows
          For i = 1 To columns
               Read TileType
               map(h,i,0) = TileType
               map(h,i,1) = Rand(0,1)
               
               If TileType = 2
                    For j = 1 To Rand(3,4)
                         t.scene = New scene
                         t\x = i*64 + Rand(-16,16)
                         t\y = h*64 + Rand(-16,16)
                         t\style = Rand(0,1)
                    Next
               EndIf
          Next
     Next
End Function




Function drawtiles()
     For h = 1 To rows
          For i = 1 To columns
               If h*64-offsety <= GraphicsHeight() + 32 And h*64-offsety >= -64                              ;--
                    If i*64-offsetx <= GraphicsWidth() + 32 And i*64-offsetx >= -64                              ;these two if statements are so that it doesn't draw tiles that don't need to be drawn
                         DrawImage tile(map(h,i,0),map(h,i,1)),(i*64)-offsetx,(h*64)-offsety
                    EndIf
               EndIf
          Next
     Next
End Function




Function UpdateScenery()
     For t.scene = Each scene
          If t\x-offsetx <= GraphicsWidth() + 32 And t\x-offsetx >= -64
               If t\y-offsety <= GraphicsHeight() + 32 And t\y-offsety >= -64
                    DrawImage scenery(t\style),t\x-offsetx,t\y-offsety
               EndIf
          EndIf
     Next
End Function




;-------------------------------------Mapdata------------------

.mapdata
Data 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
Data 3,3,0,0,0,0,0,0,0,0,0,0,0,3,3
Data 3,0,1,0,0,0,1,1,0,0,0,1,0,0,3
Data 3,0,1,1,1,1,1,1,1,1,1,0,0,0,3
Data 3,0,0,1,1,2,2,2,1,2,1,1,0,0,3
Data 3,3,3,3,1,1,1,1,1,1,1,1,1,0,3
Data 3,3,2,3,3,1,2,1,1,2,1,2,0,0,3
Data 3,1,2,1,3,3,3,3,3,3,3,3,3,3,3
Data 3,0,1,1,1,2,2,2,2,1,2,2,1,0,3
Data 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3