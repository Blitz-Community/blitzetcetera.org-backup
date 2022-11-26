Graphics 640,480
Local Player:TImage=LoadImage("BMax-BlobShip.png")
Local Alien:TImage=LoadImage("BMax-UFO.png")

While Not (KeyHit(key_escape) Or AppTerminate())
Cls
DrawImage Alien,150,200
DrawImage Player,MouseX(),MouseY()
If ImagesCollide(Alien,150,200,0,Player,MouseX(),MouseY(),0)
SetClsColor 255,0,0
Else
SetClsColor 0,0,0
EndIf
Flip
Wend
End
