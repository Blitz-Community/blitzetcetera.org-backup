Graphics 640,480

SetBuffer BackBuffer()

Global Mouse_X#=320
Global Mouse_Y#=200

Global X#,Y#;Not needed


AX1#=90
AY1#=100
AX2#=70
AY2#=60

BX1#=290
BY1#=200
BX2#=270
BY2#=260

While Not KeyDown(1)
Cls

;------Start of loop-------

If LinesCross(AX1#,AY1#,AX2#,AY2#,BX1#,BY1#,BX2#,BY2#) Then

Color 255,255,255

Oval X#-5,Y#-5,10,10


Color 255,0,0
Else
Color 0,255,0
End If

Line AX1#,AY1#,AX2#,AY2#

Line BX1#,BY1#,BX2#,BY2#

If MouseDown(1) Then

Mouse_X#=MouseX()
Mouse_Y#=MouseY()

If Dis(Mouse_X#,Mouse_Y#,AX1#,AY1#)<25 Then
AX1#=Mouse_X#
AY1#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,AX2#,AY2#)<25 Then
AX2#=Mouse_X#
AY2#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,BX1#,BY1#)<25 Then
BX1#=Mouse_X#
BY1#=Mouse_Y#
ElseIf Dis(Mouse_X#,Mouse_Y#,BX2#,BY2#)<25 Then
BX2#=Mouse_X#
BY2#=Mouse_Y#
End If

End If

Color 255,255,255
Text 10,10,"Click and hold the mouse to drag the lines"


Flip
Wend
End

Function LinesCross(x0#,y0#, x1#,y1#, x2#,y2#, x3#,y3# )
  
n# = (y0#-y2#)*(x3#-x2#) - (x0#-x2#)*(y3#-y2#)
d# = (x1#-x0#)*(y3#-y2#) - (y1#-y0#)*(x3#-x2#)

If Abs(d#) < 0.0001 
; Lines are parallel!
Return False
Else
; Lines might cross!
Sn# = (y0#-y2#)*(x1#-x0#) - (x0#-x2#)*(y1#-y0#)

AB# = n# / d#
If AB#>0.0 And AB#<1.0
CD# = Sn# / d#
If CD#>0.0 And CD#<1.0
; Intersection Point
X# = x0# + AB#*(x1#-x0#)
       Y# = y0# + AB#*(y1#-y0#)
Return True
End If
End If

; Lines didn't cross, because the intersection was beyond the end points of the lines
EndIf

; Lines do not cross!
Return False

End Function

Function Dis#(X#,Y#,TX#,TY#)
Return Abs(((TX#-X#)*(TX#-X#)+(TY#-Y#)*(TY#-Y#))^0.5)
End Function
