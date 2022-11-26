;(c) Agnislav Lipcan
;      inventar
;avelnet@yandex.ru

Graphics3D 800,700,32,2
SetBuffer BackBuffer()

AppTitle "inventar"

font=LoadFont(courier ,20)
SetFont font

camera=CreateCamera()

Global ImH=30    ;<= высота одной клетки
Global ImW=30    ;<= ширина одной клетки
Global nachx=70 ;<= начальное положение поля по x минус ImW
Global nachy=70 ;<= начальное положение поля по y минус ImH
Global vertic=6  ;<= клеток вниз
Global goriz=8   ;<= клеток вправо
Global kolvovesh=2   ;общее количество вещей
Global mouse          ;мыша
Global orugnom=0     ;первое выбранное оружие

;создаем массив клеток инвентаря
Dim pole(goriz+1,vertic+1)

;картинка пустоты
Global noneim=LoadImage("Inventory1-SmallItem0.png")

Type predmet
	Field kolvokletok,posx,posy
	Field gde ;0-не активно; 1-в инвентаре; 2-выбрано
	Field image ;картинка
	;Field cena, sila, lovkost ;параметры
	;Field name$ ;название
	;field model ;3d модель
End Type

Global tempimage
Global temp1=0
Global temp2=0

;создаем массив предметов
Dim pr.predmet(kolvovesh+1)

;temp
Global num=1
newvesh(1,1,6,"Inventory1-Item1.png")
newvesh(4,2,8,"Inventory1-Item2.png")


;*************************  main cikles  ****************************
Repeat

	UpdateWorld()
	RenderWorld()
	refresh()
	draw()
	Flip

Until KeyHit(1)
End
;*************************        end         ***************************

;*************************     functions     ***************************

Function draw()
For i=1 To vertic
	For j=1 To goriz
		DrawImage noneim,nachx+j*ImW,nachy+i*ImH
		;Text nachx+j*ImW+5,nachy+i*ImH+5,pole(j,i)
	Next
Next
For i=0 To 3
	For j=0 To 1
		DrawImage noneim,507+j*ImW,368+i*ImH
	Next
Next
For i=1 To  kolvovesh
	If pr(i)\gde=1 Then
		DrawImage pr(i)\image,nachx+pr(i)\posx*ImW,nachx+pr(i)\posy*ImH
	EndIf
	If pr(i)\gde=2 Then
		DrawImage pr(i)\image,505,367
	EndIf
Next
If mouse=1 Then DrawImage tempimage,MouseX(),MouseY()
End Function

Function refresh()
For i=0 To vertic
	For j=0 To goriz
		pole(j,i)=0
	Next
Next
For i=1 To  kolvovesh
	If pr(i)\gde=1 Then
		For k=0 To pr(i)\kolvokletok*0.5-1
			For m=0 To 1
				pole(pr(i)\posx+m,pr(i)\posy+k)=i
			Next
		Next
	EndIf
Next
If MouseX() > nachx+ImW And MouseX() < nachx+ImW*(goriz) And MouseY() > nachy+ImH And MouseY() < nachy+ImH*(vertic) Then
	If MouseHit(1) Then
		If mouse=0 Then
			If pole((MouseX()-nachx)/ImW,(MouseY()-nachy)/ImH) <> 0 Then
				mouse=1
				i=pole((MouseX()-nachx)/ImW,(MouseY()-nachy)/ImH)
				pr(i)\gde=0
				temp1=i
				tempimage=pr(i)\image
			EndIf
		Else
			temp2=0
			For k=0 To pr(temp1)\kolvokletok*0.5-1
				For m=0 To 1
					If pole((MouseX()-nachx)/ImW+m,(MouseY()-nachy)/ImH+k) <> 0 Then temp2=pole((MouseX()-nachx)/ImW+m,(MouseY()-nachy)/ImH+k)
				Next
			Next
			If  temp2=0 Then
				pr(temp1)\posx=(MouseX()-nachx)/ImW
				pr(temp1)\posy=(MouseY()-nachy)/ImH
				pr(temp1)\gde=1
				temp1=0
				mouse=0
			Else
				If pr(temp2)\kolvokletok >= pr(temp1)\kolvokletok Then
					pr(temp1)\posx=pr(temp2)\posx
					pr(temp1)\posy=pr(temp2)\posy
					pr(temp1)\gde=1
					pr(temp2)\gde=0
					tempimage=pr(temp2)\image
					temp1=temp2
				EndIf
			EndIf
		EndIf
	EndIf
EndIf
If MouseX() > 505 And MouseX() < 565 And MouseY() > 367 And MouseY() < 427 Then
	If MouseHit(1) Then
		If mouse=1 Then
			If orugnom <> 0 Then
				pr(orugnom)\gde=0
				pr(temp1)\gde=2
				temp=temp1
				temp1=orugnom
				tempimage=pr(orugnom)\image
				orugnom=temp
			Else
				pr(temp1)\gde=2
				orugnom=temp1
				temp1=0
				mouse=0
			EndIf
		Else
			If orugnom <> 0 Then
				mouse=1
				pr(orugnom)\gde=0
				temp1=orugnom
				tempimage=pr(orugnom)\image
				orugnom=0
			EndIf
		EndIf
	EndIf
EndIf
End Function

Function newvesh(x,y,kl,im$)
pr(num) = New predmet
pr(num)\posx=x
pr(num)\posy=y
pr(num)\kolvokletok=kl
pr(num)\image=LoadImage(im$)
pr(num)\gde=1
num=num+1
End Function