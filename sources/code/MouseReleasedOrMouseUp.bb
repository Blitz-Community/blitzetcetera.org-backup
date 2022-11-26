; For inner - how to do a "mouse released" or "mouse up"
; By Rob Cummings (rob@redflame net)

Global oldbutton,newbutton

While Not KeyHit(1)
	If MouseUp() Then Print "MOUSEUP!"
	UpdateMouse()
Wend
End

Function updatemouse()
	oldbutton=newbutton
	newbutton=MouseDown(1)
End Function

Function MouseUp()
	If oldbutton=1 And newbutton=0 Return 1
End Function
