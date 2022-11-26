Graphics 800,600,32,2
SetBuffer BackBuffer()

Global ButCol_r = 240
Global ButCol_g = 192
Global ButCol_b = 130

; Example code
ClsColor 200,180,160
Repeat
	Cls
	For n=1 To 10
		modus=n-1:If modus>4 Then Modus=Mode
		If Button(n*50,n*32,100,20,4,"Button "+n,modus)
			Color 255,0,0:Text 0,0,"Button "+n+" clicked!!"
		EndIf
	Next
	If MouseHit(2)
		ButCol_r=Rand(0,200)
		ButCol_g=Rand(0,200)
		ButCol_b=Rand(0,200)
		mode=Rand(0,4)
	EndIf
	Text 0,400,ButCol_r+" "+ButCol_g+" "+ButCol_b
	Text 0,416,"Mode: "+mode
	Flip
Until KeyHit(1)


Function Button(x,y,Width,Height,Border,Label$,mode=0)
	Local Over = False
	Local r,g,b

	If RectsOverlap (x,y,width,height,MouseX(),MouseY(),1,1) Then over = True

	If over Then
		If mode=0 r=(ButCol_r+$80) And $ff : g=(ButCol_g+$80) And $ff : b=(ButCol_b+$80) And $ff
		If mode=1 b=ButCol_r Xor $ff And $ff : g=ButCol_g Xor $ff And $ff : r=ButCol_b Xor $ff And $ff
		If mode=2
			r=(ButCol_r+(255-ButCol_r)/2);+$10 : If r>255 Then r=255
			g=(ButCol_g+(255-ButCol_g)/2);+$10 : If g>255 Then g=255
			b=(ButCol_b+(255-ButCol_b)/2);+$10 : If b>255 Then b=255
		EndIf
		If mode=3 r=$ff : g=$ff : b=$ff
		If mode=4 r=$20 : g=$20 : b=$20
		tr=ButCol_r : tg=ButCol_g : tb=ButCol_b
	Else
		r=ButCol_r : g=ButCol_g : b=ButCol_b
		tr=ButCol_r : tg=ButCol_g : tb=ButCol_b
	EndIf
	tr=(tr+(255-tr)/2)+$40 : If tr>255 Then tr=255
	tg=(tg+(255-tg)/2)+$40 : If tg>255 Then tg=255
	tb=(tb+(255-tb)/2)+$40 : If tb>255 Then tb=255

	If Not (over And MouseDown(1)) x=x+1:y=y+1

	Color r,g,b
	Rect x,y,width,height

	If over And MouseDown(1)
		Color ButCol_r-ButCol_r/3,ButCol_g-ButCol_g/3,ButCol_b-ButCol_b/3
	Else
		Color ButCol_r+(255-ButCol_r)/3,ButCol_g+(255-ButCol_g)/3,ButCol_b+(255-ButCol_b)/3
	EndIf
	Rect x-border,y-border,width+border*2,border
	Rect x-border,y-border,border,height+border*2

	If over And MouseDown(1)
		Color ButCol_r+(255-ButCol_r)/3,ButCol_g+(255-ButCol_g)/3,ButCol_b+(255-ButCol_b)/3
	Else
		Color ButCol_r-ButCol_r/3,ButCol_g-ButCol_g/3,ButCol_b-ButCol_b/3
	EndIf
	Rect x+width,y,border,height+border
	Rect x,y+height,width+border,border
	For i=0 To border-1
		Line x+width,y,x+width+border-1,y-i
		Line x,y+height,x-i,y+height+border-1
	Next

	Color 16,16,16
	Text x+(width/2)+3,y+(height/2)+2,label,True,True
	Text x+(width/2)+2,y+(height/2)+1,label,True,True
	Color tr,tg,tb
	Text x+(width/2)+1,y+(height/2),label,True,True

	Color 0,0,0
	
	If over And MouseDown(1) Return True Else Return False
End Function
