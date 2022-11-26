Graphics 640,480
While Not (KeyHit(key_escape) Or AppTerminate())
Cls
SetColor 0,255,0
DrawRect 150,200,100,100
If CircRectsOverlap(150,200,100,100,MouseX(),MouseY(),50)
SetColor 255,0,0
Else
SetColor 0,0,255
EndIf
DrawOval MouseX()-50,MouseY()-50,100,100
Flip
Wend
End

Function CircRectsOverlap(x0, y0, w0, h0, cx, cy, r)
testX=cX
testY=cY
If TestX < x0 Then TestX=x0
If TestX > (x0+w0) Then TestX=(x0+w0)
If TestY < y0 Then Testy=y0
If TestY > (y0+h0) Then Testy=(y0+h0)

Return ((cX-TestX)*(cX-TestX)+(cY-TestY)*(cY-TestY))<r*r
End Function
