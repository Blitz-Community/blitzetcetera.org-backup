Graphics 1024, 768
SetBuffer BackBuffer()

degree# = 45
speed# = 0

While Not KeyHit(1)
     
     Cls
     
     
     y# = y# + speed*Sin(degree) ; The Sin movement
     x# = x# + speed*Cos(degree) ; The Cos movement
     
     If KeyDown(200) ; Move
          speed = 5
     Else
          speed = 0
     EndIf
     
     
     If KeyDown(205) Then ; Turn left
          degree = degree +5
     EndIf
     
     If KeyDown(203) Then ; Turn right
          degree = degree -5
     EndIf
     
     Line x,y,x +50*Sin(degree+90),y -50*Cos(degree+90) ; Draw the line and make it face forward


Flip
Wend
End