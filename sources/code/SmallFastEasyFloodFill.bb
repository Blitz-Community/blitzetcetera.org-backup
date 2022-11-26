;it fills over the color of the pixel at the position passed, using the color passed
;you pass a buffer (screen or image), and its size because there is no bufferwidth() in blitz
;you will have to get the color you want to fill with using the way it gets the "white" variable or it would freeze.
;just click where you want to fill

;this type is needed for fillfast, nothing else is.
Type dot
	Field x,y,nodir
End Type

scx=640: scy=480
Graphics scx,scy,16,2



;make background
;SeedRnd MilliSecs()
Color 255,0,0
Rect 0,0,640,480
Color 0,0,0
For ovals=1 To 25
	Color Rnd(255),Rnd(255),Rnd(255)
	Oval Rand(scx),Rand(scy),Rand(80,180),Rand(80,180)
Next

Color 255,255,255
Plot 0,0
white=ReadPixel(0,0)
;main loop
Repeat
	If MouseHit(1) Then
		fillfast(MouseX(),MouseY(),white,FrontBuffer(),scx,scy)
		FlushMouse
	EndIf
Until KeyHit(1)
End

;flood fill
Function fillfast(startx,starty,fillcolor,buffer,buffersizex,buffersizey)
	Local i.dot,ni.dot,dir

	If startx<0 Or starty<0 Or startx>=buffersizex Or starty>=buffersizey Then RuntimeError "Fill starting point out of bounds."
	LockBuffer buffer
	fillover=ReadPixelFast(startx,starty)
	If fillover=fillcolor Then UnlockBuffer buffer:Return

	ni=New dot
	ni\x=startx: ni\y=starty
	ni\nodir=-1
	WritePixelFast ni\x,ni\y,fillcolor
	
	For i=Each dot
		For dir=0 To 3
			If dir<>i\nodir Then
				x=i\x+(dir=0)-(dir=2)
				y=i\y+(dir=1)-(dir=3)
				If x>=0 And y>=0 And x<buffersizex And y<buffersizey Then
					If ReadPixelFast(x,y)=fillover Then
						ni=New dot
						ni\x=x: ni\y=y
						ni\nodir=(dir+2) And %11
						WritePixelFast x,y,fillcolor
					EndIf
				EndIf
			EndIf
		Next
		Delete i
	Next
	UnlockBuffer buffer
End Function
