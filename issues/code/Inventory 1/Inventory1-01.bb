;(c) Agnislav Lipcan
;      инвентарь
;avelnet@yandex.ru

Global ImH=30    ;<= высота одной клетки
Global ImW=30     ;<= ширина одной клетки
Global nachx=100 ;<= начальное положение поля по x
Global nachy=100 ;<= начальное положение поля по y
Global vertic=4     ;<= клеток вниз
Global goriz=8     ;<= клеток вправо

Graphics3D 500,400,32,2
SetBuffer BackBuffer()

camera=CreateCamera()
CameraClsColor camera,100,100,100

Dim image(goriz*vertic+1)
Dim imageyn(goriz*vertic+1)
i=1
While i < goriz*vertic+1
   image(i)=LoadImage("Inventory1-SmallItem0.png")
   imageyn(i)=0
   i=i+1
Wend

Global vlog=0

image(3)=LoadImage("Inventory1-SmallItem1.png")
imageyn(3)=1
image(11)=LoadImage("Inventory1-SmallItem2.png")
imageyn(11)=1

Global tempimage1=CreateImage(ImW,ImH)
Global tempimage2=CreateImage(ImW,ImH)
Global tempimage3=CreateImage(ImW,ImH)

;main cikles
While Not exitprogram=True

If KeyHit(1)=True Then exitprogram=True

refresh()

If KeyHit(57) Then addim("Inventory1-SmallItem1.png")

UpdateWorld()
RenderWorld()

draw()

Flip

Wend
End
;functions
Function draw()
k=1
i=1
j=1
While i < vertic+1
    j=1
    While j < goriz+1
       DrawImage image(k),nachx+j*ImW,nachy+i*ImH
       j=j+1
       k=k+1
    Wend
    i=i+1
Wend
If vlog=1 Then DrawImage tempimage1,MouseX()-ImW/2,MouseY()-ImH/2
End Function


Function refresh()
If MouseX() > nachx+ImW And MouseX() < nachx+ImW*(goriz+1) And MouseY() > nachy+ImH And MouseY() < nachy+ImH*(vertic+1) Then
If MouseHit(1) Then
  If vlog=0 Then
  	nom=(((MouseY()-nachy)/ImH)*goriz)-(goriz-((MouseX()-nachx)/ImW))
  	tempimage1=CopyImage(image(nom))
  	image(nom)=LoadImage("Inventory1-SmallItem0.png")
  	vlog=1
  	imageyn(nom)=0
  Else
  	nom=(((MouseY()-nachy)/ImH)*goriz)-(goriz-((MouseX()-nachx)/ImW))
  	If imageyn(nom)=0 Then
     	tempimage2=CopyImage(image(nom))
     	image(nom)=CopyImage(tempimage1)
     	vlog=0
     	imageyn(nom)=1
  	Else
       tempimage3=CopyImage(image(nom))
       image(nom)=CopyImage(tempimage1)
       tempimage1=CopyImage(tempimage3)
       vlog=1
  	EndIf
  EndIf
EndIf
EndIf
End Function

Function addim(imstr$)
i=1
l=0
While i < goriz*vertic+1
  If imageyn(i)=0 Then
     imageyn(i)=1
     image(i)=LoadImage(imstr$)
     i=goriz*vertic+1
    l=1
  Else
     i=i+1
    l=0
  EndIf
Wend
If l=0 Then RuntimeError "нет места!"
End Function