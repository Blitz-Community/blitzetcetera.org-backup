Graphics 640,480
While Not (KeyHit(key_escape) Or AppTerminate())
Cls
SetColor 0,255,0
DrawRect 150,200,100,100
If RectsOverlap(150,200,100,100,MouseX(),MouseY(),100,100)
SetColor 255,0,0
Else
SetColor 0,0,255
EndIf
DrawRect MouseX(),MouseY(),100,100
Flip
Wend
End

Function RectsOverlap:Int(x0, y0, w0, h0, x2, y2, w2, h2)
If x0 > (x2 + w2) Or (x0 + w0) < x2 Then Return False
If y0 > (y2 + h2) Or (y0 + h0) < y2 Then Return False
Return True
End Function
