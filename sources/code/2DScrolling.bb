AppTitle "scrolling demo by MSW"


;Use arrows to move around, space bar to swap between controllable players
; Press 'G' to turn on/off gravity (defaults to OFF)

; Const declarations
Const ScreenX = 640   ;screen X resolution
Const ScreenY = 480   ;screen Y resolution


Const Tsize = 16      ;size of Tiles

Const MapTX=100       ;Number of tiles horizontal
Const MapTY=100       ;Number of tiles vertical

Const Maxplayers = 2  ;Number of player 'sprites' onscreen




Dim Map%(MapTX,MapTY) ;set up the map then load it with random tiles

For x = 0 To MapTX
For Y = 0 To MapTY

If X = 0 Or x = MapTX Or Y = 0 Or Y = MapTY Then
 Map(x,y) = 5
Else 
 Map(x,y) = Rand(0,3)
 If Rand(0,50)=50 Then map(x,y)=4
 If Rand(0,100) = 100 Then map(x,y)=5
 
End If

Next
Next



;create players

Dim PlayerX%(Maxplayers)
Dim PlayerY%(Maxplayers)
Dim PMoveX%(Maxplayers)
Dim PMoveY%(Maxplayers)

For i = 0 To Maxplayers

PlayerX(i) = Rand(2*Tsize,(MapTX-1)*Tsize) ;randomly place the player on one of the tiles
PlayerY(i) = Rand(2*Tsize,(MapTY-1)*Tsize)

;Map(PlayerX(i),PlayerY(i)) = 0 ;Make this tile walkable
 
;PlayerX(i) = (PlayerX(i) * Tsize) - (Tsize/2) ;then convert the player to pixel cords
;PlayerY(i) = (PlayerY(i) * Tsize) - (Tsize/2) ;placeing them at the center of this map tile

PMoveX(i) = 0     ;set up the movement variables...0 means the player isn't moveing
PMoveY(i) = 0

Next

Pset%=1 ;variable to indicate which player is under user control


CamX% = 0 ;set up the camera cords
CamY% = 0


Graphics ScreenX, ScreenY, 16,2
SetBuffer BackBuffer()










While Not KeyHit(1)


;align the camera

CamX = CamX + (((PlayerX(Pset) - (ScreenX/2)) - CamX) * .09)
CamY = CamY + (((PlayerY(Pset) - (ScreenY/2)) - CamY) * .09)


;this is a counter to make the non-walkable tiles flash
Kount% = Kount% + 1

If Kount > 8 Then Kount = 0
;this is a variable to allow the selected player to flash
K%=1-K%



;draw map

TX% = CamX / Tsize
TY% = CamY / Tsize

CamOffsetX% = CamX - (TX * Tsize)
CamOffsetY% = CamY - (Ty * Tsize)


For X = 0 To (ScreenX / Tsize + 1)
For Y = 0 To (ScreenY / Tsize + 1)


 If (X+TX) >= 0 And (X+TX) <= MapTX And (Y+TY) >= 0 And (Y+TY) <= MapTY Then
    Tile% = Map(X+TX,Y+TY)
 Else
    Tile% = -1
 End If

 

 Select Tile
   Case -1
     Color 0,0,0
   Case 0
     Color 0,60,0
   Case 1
     Color 0,80,0
   Case 2
     Color 0,100,0
   Case 3
     Color 0,120,0
   Case 4
     Color 0,0,Kount * 8
   Case 5
     Color Kount * 8,0,0
 End Select



Rect (X * Tsize)-CamOffsetX, (Y * Tsize) - CamOffsetY, Tsize, Tsize, 1


Next
Next




;now we draw the players
; for this I'm just going to draw an empty grey rectange 
; and Plot a white pixel to show the "real" player location
For i = 0 To Maxplayers

;first calculate the onscreen location of the player
PX% = PlayerX(i) - CamX
PY% = PlayerY(i) - CamY   
  
;now show this location
Color 255,255,255
 Plot PX,PY

;now show the rectangle holding the player
; for this the player will be Tsize tall and half Tsize wide 
; with the actual player location representing his feet

;first I calc where the upper left corner of this rect will be
SpriteX% = PX - (Tsize/4)
SpriteY% = PY - (Tsize) + (Tsize/4)

;now show this location
Color 128,128,128
If i = Pset Then
 Rect SpriteX,SpriteY,(Tsize/2),Tsize,k
Else
 Rect SpriteX,SpriteY,(Tsize/2),Tsize,0
End If

Next


;just a small text message to indicate that gravity is turned on
If gravity = 1 Then
 Color 200, 255,200
 Text (20,20, "GRAVITY: ON")
End If


;a small delay then flip the backbuffer onscreen
Delay 10
Flip 





;Very basic point basied collision detection and movement for all players
; this can be a bit buggy when gravity is turned on
  
For i = 0 To Maxplayers

TempX% = PlayerX(i) + PMoveX(i)
TempY% = PlayerY(i) + PMoveY(i) 

 
TestX = TempX / Tsize
TestY = TempY / Tsize


If map(TestX,TestY)>3 Then
 
 If PMoveX(i) < 0 Then FlagX# = -1 Else FlagX# = 1
 If PMoveY(i) < 0 Then FlagY# = -1 Else FlagY# = 1

 OffsetX% = TempX - (TestX * Tsize) + FlagX 
 OffsetY% = TempY - (TestY * Tsize) + FlagY 

If FlagX>0 Then FlagX = 1/Tsize
If FlagY>0 Then FlagY = 1/Tsize

If PMoveX(i) <>0 Then TempX = PlayerX(i) + (PMoveX(i) - (OffsetX + (Tsize * FlagX))):PMoveX(i)=0
If PMoveY(i) <>0 Then TempY = PlayerY(i) + (PMoveY(i) - (OffsetY + (Tsize * FlagY))):PMoveY(i)=0 

End If


PlayerX(i) = TempX
PlayerY(i) = TempY


PMoveX(i) = 0

; this is where gravity is performed if switched on
If gravity = 1 Then 

 PMoveY(i) = PMoveY(i) + 1
 If PMoveY(i) > Tsize Then PMoveY(i) = Tsize -1

Else

 PMoveY(i) = 0

End If

Next



; User input first the movement keys
;   movement speed is 40% of the size of the tiles


If KeyDown(203) Then PMoveX(Pset) = -1 * (Tsize * .4)
If KeyDown(205) Then PMoveX(Pset) =  (Tsize * .4)
If KeyDown(200) Then PMoveY(Pset) = -1 * (Tsize * .4)
If KeyDown(208) Then PMoveY(Pset) = (Tsize * .4)


; User input to check if switching between players
If KeyDown(57) Then Pset=Pset+1
 If Pset > Maxplayers Then Pset = 0

; User input to turn gravity on/off
If KeyDown(34) Then gravity = 1 - gravity

Wend






