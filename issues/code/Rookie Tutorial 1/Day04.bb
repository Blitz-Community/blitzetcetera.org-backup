Graphics3D 640, 480, 32
SetBuffer BackBuffer()
HidePointer

Global God = CreateCamera()

MoveEntity god,0,2,0

Global Red=0      ; Составляющие
Global Green=150  ; цвета
Global Blue=200   ; Неба

Global Day=1.0    ; Значит у нас кончилась ночь
CameraClsColor God,red,green,Blue; Небо

Global DayTime=100000 ; скорость смены
Global DayBegin=0   ; когда произошла смена дня и ночи


Pl= CreatePlane()             ;Земля, которая вода
EntityAlpha Pl,0.5
tx=LoadTexture("media\water.jpg")   ;
EntityTexture Pl,tx           ;
FreeTexture tx
EntityPickMode pl,2
NameEntity pl,"Water"

sh= CreatePlane()             ; дно - шельф

Land=LoadTerrain("media\land.png")    ; Острова

EntityPickMode Land,2
NameEntity Land,"Land"

tx=LoadTexture("media\dirt001.jpg")   ;

ScaleTexture tx,0.2,0.2
EntityTexture sh,tx             ;
EntityTexture land,tx           ;
FreeTexture tx

ScaleEntity sh,5,1,5
ScaleEntity Land,5,80,5
MoveEntity Land,-256*5,-20,-256*5
MoveEntity sh,0,-20.001,0

AmbientLight 100,100,100                ; как освещены предметы ночью
Global l = CreateLight()             ; Свет, который хорошо
TurnEntity l,30,20,0

Dim Flora(2)

Flora(1)=LoadMesh("media\tree\tree01.b3d")
HideEntity Flora(1)
Flora(2)=LoadMesh("media\tree\tree01.b3d")
ScaleEntity Flora(2),1,0.5,1
HideEntity Flora(2)

For i=1 To 3000 ; количество растений
en=0
While en=0
 x#=Rnd(-2500,2500)
 z#=Rnd(-2500,2500)
 LinePick x,200,z,0,-200,0
 en=PickedEntity()
 If en
  If EntityName(en)="Land"         ; В воду растения не сажаем.
   tr=CopyEntity(flora(Rnd(1,2)))
   PositionEntity tr,x,PickedY#(),z
  Else
   en=0
  EndIf
 EndIf
Wend
Next

Global Center=CreatePivot() ; центр системы, его постоянно совмещаем с камерой (с нами т.е.)
Global star=CreateSphere(8,center) ; ЗВЕЗДЫ
EntityFX star,1
ScaleEntity star,50,50,50
FlipMesh star
tx=LoadTexture("Media\space\stars.png",4)
EntityTexture star,tx
FreeTexture tx
EntityOrder star,1.0
Global solCen=CreatePivot(Center)  ; центр вокруг которого вращается Солнце
sol=LoadSprite("Media\space\sol.png",1,SolCen); Солнце
MoveEntity sol,0,0,15
EntityOrder sol,0.9

Global MoonCen=CreatePivot(Center) ; центр вокруг которого вращается Луна
Moon=LoadSprite("Media\space\moon.png",1,MoonCen); Луна
MoveEntity moon,0,0,-15
EntityOrder moon,0.8



While Not KeyDown(1)

GodMovement()   ; носится над водой и под ней

DayControl()  ;смена дня и ночи

RenderWorld
Flip

Wend

End

Function SpaceControl(fase#,day)
PositionEntity center,EntityX(god),EntityY(god),EntityZ(god)
If day=-1
 RotateEntity SolCen,90-(180*fase),0,0
 RotateEntity MoonCen,90-(180*fase),0,0
Else
 RotateEntity SolCen,(180*fase)+90,0,0
 RotateEntity MoonCen,(180*fase)+90,0,0
EndIf
End Function

Function GodMovement()
TurnEntity God,MouseYSpeed(),-MouseXSpeed(),0 ; поворот камеры мышкой аля FPS
TurnEntity god,0,0,-EntityRoll(god) ; чтобы не переворачивалась камера
MoveEntity God,KeyDown(32)-KeyDown(30),0,KeyDown(17)-KeyDown(31) ; Движение камеры wasd
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 ; Это нужно чтобы машка не уходила за экран
End Function

Function DayControl()
If DayBegin=0 Then DayBegin=MilliSecs()  ; день первый начался
delta#=Float(MilliSecs()-DayBegin)/DayTime ; Какая часть дня прошла?
If delta>=1                                ; День/ночь кончился
 delta=1
 DayBegin=MilliSecs()                       ;Начался новый день/ночь
EndIf
If day=-1                                  ;Ночью света становится меньше
 delta=1-delta
EndIf
LightColor l,255*delta,255*delta,255*delta
CameraClsColor God,red*delta,green*delta,Blue*delta  ; цвет неба зависит от времени суток
EntityAlpha star,1-delta
SpaceControl(delta,day)
If delta=1 Or delta=0 Then day=-day                  ; в полдень/полночь сменяем признак половины дня
End Function