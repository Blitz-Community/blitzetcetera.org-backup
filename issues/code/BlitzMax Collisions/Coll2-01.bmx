  Graphics 640,480
  Local Player:TImage=LoadImage("BMax-BlobShip.png")
  Local Alien:TImage=LoadAnimImage("BMax-Explosion.png",64,64,0,16)
  Local Frame:Int=0
  Local AnimDelay:int=10
  Local PlayerSize:Int=1
  Local AlienSize:Int=2
  
  While Not KeyHit(key_escape) Or AppTerminate()
    Cls
    ResetCollisions()
    SetScale AlienSize,AlienSize
    DrawImage Alien,150,100,Frame
    CollideImage(Alien,150,100,Frame,0,2)
  
    SetScale PlayerSize,PlayerSize
    DrawImage Player,MouseX(),MouseY()
    If CollideImage(Player,MouseX(),MouseY(),0,2,0)
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
