AppTitle "black and white terrain destruction"

;the main terrain is white with a black border eventually. the transparent color will be 0,255,0
;each hole has a circle of color 0,255,0 and a transparent color of 255,0,255

Graphics 800,600
SetBuffer BackBuffer()

Global terrain = CreateImage(800,600)
SetBuffer ImageBuffer(terrain)
Color 255,255,255
Rect 0,0,800,600
SetBuffer BackBuffer()
MaskImage terrain,0,255,0

Global minholesize = 40
Global maxholesize = 100

Type hole
     Field image
     Field size
     Field number
End Type

Global holemask = CreateImage(maxholesize+4,maxholesize+4)
MaskImage holemask,255,255,255

id = 1

For a = minholesize To maxholesize Step 20
     h.hole = New hole
     h\size = a
     h\number = id
     id = id + 1
Next

Global hole1 = CreateImage(40,40)
SetBuffer ImageBuffer(hole1)
Color 255,0,255
Rect 0,0,40,40
Color 0,255,0
Oval 0,0,40,40
SetBuffer BackBuffer()
MaskImage hole1,255,0,255
MidHandle hole1


ClsColor 235,235,235

While Not KeyHit(1)
Cls

msx# = MouseX()
msy# = MouseY()

DrawImage terrain,0,0

If MouseDown(1)
     For h.hole = Each hole
          If h\number = curhole
               size = h\size
               halfsize = h\size/2
               
               SetBuffer ImageBuffer(terrain)
               Color 0,255,0
               Oval MsX-halfsize,MsY-halfsize,size,size
               
               GrabImage holemask,MsX-(halfsize+2),MsY-(halfsize+2)
               Color 0,0,0
               Oval MsX-(halfsize+2),MsY-(halfsize+2),size+4,size+4
               DrawImage holemask,MsX-(halfsize+2),MsY-(halfsize+2)
               
               SetBuffer BackBuffer()
          End If
     Next
End If


Color 255,0,0
Line msx-5,msy,msx+5,msy
Line msx,msy-5,msx,msy+5

For a = 0 To 3
     If KeyDown(a+2)
          curhole = a + 1
     End If
Next

Color 0,0,0
Text 10,10,"Current hole = " + curhole

Flip
Wend
