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

Global DayTime=10000 ; скорость смены
Global DayBegin=0   ; когда произошла смена дня и ночи


Pl= CreatePlane()             ;Земля, которая вода
EntityAlpha Pl,0.4
tx=LoadTexture("media\water.jpg")   ;
EntityTexture Pl,tx           ;
FreeTexture tx

sh= CreatePlane()             ; дно - шельф

Land=LoadTerrain("media\land.png")    ; Острова
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

While Not KeyDown(1)

GodMovement()   ; носится над водой и под ней

DayControl()  ;смена дня и ночи

RenderWorld
Flip

Wend

End

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
If delta=1 Or delta=0 Then day=-day                  ; в полдень/полночь сменяем признак половины дня
End Function