Graphics 640,480
Local Player:TImage=LoadImage("BMax-BlobShip.png")
Local Alien:TImage=LoadAnimImage("BMax-Explosion.png",64,64,0,16)
Local Frame:Int=0
Local AnimDelay:Int=10

While Not (KeyHit(key_escape) Or AppTerminate())
Cls
DrawImage Alien,150,200, Frame
DrawImage Player,MouseX(),MouseY()
If ImagesCollide(Alien,150,200,Frame,Player,MouseX(),MouseY(),0)
SetClsColor 255,0,0
Else
SetClsColor 0,0,0
EndIf
Flip
If AnimDelay<0 Then
Frame :+ 1
If Frame>15 Then Frame=0
AnimDelay=10
EndIf
AnimDelay :- 1

Wend
End
