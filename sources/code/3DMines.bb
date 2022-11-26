;3DMineSweeper Demo (c) august 2005 SMax 
; 
;This program is free software 
; 
;This program is distributed in the hope that it will be 
;useful, but WITHOUT ANY WARRANTY 
 
; 
;установка видеорижима 
; 
SetBuffer BackBuffer()  
Graphics3D 640,480,0,0  
 
; 
;объ€вление переменных 
; 
Type tPole ; €чейка пол€  
  Field eModel ; модель пол€ 
  Field fMine ; признак наличи€ мины 
  Field fMark ; признак маркировки флажка 
  Field fClear ; признак открытого пол€ 
  Field nAroundMine ; число мин окружающих поле 
End Type  
 
Global nFieldSize=10 ; размерность минного пол€ 
Global nHalfField=Floor(nFieldSize*3/2)+1 
Global nMines=15 ; число мин 
Global nMarkedMines=0 ; число помеченных мин 
 
Global bEndGame=0 ; окончание игры 1 - проигрыш, 2 - выигрыш 
 
Dim oField.tPole(nFieldSize,nFieldSize,nFieldSize) ; поле 
 
Global eCube=CreateCube() ;  модель скрытой €чейки 
HideEntity eCube 
 
;;;;; 
Global eMine=CreateSphere()  ;  модель мины 
ScaleMesh(eMine,0.7,0.7,0.7) 
eCyl=CreateCylinder() 
ScaleMesh(eCyl,0.2,1,0.2) 
AddMesh eCyl,eMine 
RotateMesh eCyl,90,0,0 
AddMesh eCyl,eMine 
RotateMesh eCyl,0,90,0 
AddMesh eCyl,eMine 
FreeEntity eCyl 
HideEntity eMine 
;;;;;; 
 
Global eMark=CreateSphere() ;  модель маркировки 
HideEntity eMark 
 
EntityPickMode eCube,1 
EntityPickMode eMine,1 
EntityPickMode eMark,1 
 
; 
; √енераци€ пол€  
; 
For x=1 To nFieldSize  
  For y=1 To nFieldSize 
    For z=1 To nFieldSize 
 oField(x,y,z) = New tPole 
 oField(x,y,z)\eModel=CopyEntity( eCube ) 
 EntityColor oField(x,y,z)\eModel,255,255,255 
 MoveEntity oField(x,y,z)\eModel,x*3-nHalfField,y*3-nHalfField,z*3-nHalfField 
 oField(x,y,z)\fMine = 0 
 oField(x,y,z)\fMark = 0 
 oField(x,y,z)\fClear = 0 
 oField(x,y,z)\nAroundMine = 0 
    Next 
  Next 
Next 
 
; 
; √енераци€ мин 
; 
SeedRnd MilliSecs() 
For i=1 To nMines  
  x = Floor(Rnd(nFieldSize))+1 
  y = Floor(Rnd(nFieldSize))+1 
  z = Floor(Rnd(nFieldSize))+1 
  If oField(x,y,z)\fMine<>1  
    oField(x,y,z)\fMine = 1 
  Else i=i-1 
  End If 
Next 
 
; 
; ѕересчЄт мин, окружающих каждую €чейку пол€  
; 
For x=1 To nFieldSize  
  For y=1 To nFieldSize 
    For z=1 To nFieldSize 
 oField(x,y,z)\nAroundMine = fIsMine(x-1,y-1,z-1) + fIsMine(x-1,y,z-1) + fIsMine(x-1,y+1,z-1) + fIsMine(x,y-1,z-1) + fIsMine(x,y+1,z-1) + fIsMine(x+1,y-1,z-1) + fIsMine(x+1,y,z-1) + fIsMine(x+1,y+1,z-1) + fIsMine(x,y,z-1)     +  fIsMine(x-1,y-1,z+1) + fIsMine(x-1,y,z+1) + fIsMine(x-1,y+1,z+1) + fIsMine(x,y-1,z+1) + fIsMine(x,y+1,z+1) + fIsMine(x+1,y-1,z+1) + fIsMine(x+1,y,z+1) + fIsMine(x+1,y+1,z+1) + fIsMine(x,y,z+1) + fIsMine(x-1,y-1,z) + fIsMine(x-1,y,z) + fIsMine(x-1,y+1,z) + fIsMine(x,y-1,z) + fIsMine(x,y+1,z) + fIsMine(x+1,y-1,z) + fIsMine(x+1,y,z) + fIsMine(x+1,y+1,z) 
    Next 
  Next 
Next 
 
 
; 
;ќпределение источников света и камеры 
; 
lLight=CreateLight() 
TurnEntity lLight,32,32,0 
 
cCamera=CreateCamera() 
cam_xr#=0:cam_yr#=0:cam_zr#=0:cam_z#=0-(nFieldSize+1)*3 
 
 
eUnderMouse=0 ; переменна€ дл€ объектов под курсором 
 
; 
; ќсновной цикл 
; 
While Not KeyHit(1) Or bEndGame 
 
  nMButton=GetMouse()  
 
  eCP=CameraPick( cCamera,MouseX(),MouseY() ) 
 
  If eCP<>eUnderMouse 
    If eUnderMouse  
 EntityAlpha eUnderMouse,1 
    End If 
 eUnderMouse=eCP 
  End If 
 
  If eUnderMouse 
    dx=EntityX#(eUnderMouse) 
    dy=EntityY#(eUnderMouse) 
    dz=EntityZ#(eUnderMouse) 
    x=(dx+nHalfField)/3 
    y=(dy+nHalfField)/3 
    z=(dz+nHalfField)/3 
    EntityAlpha eUnderMouse,Sin( MilliSecs() )*.5+.5 
    If nMButton=1 
 If Not oField(x,y,z)\fClear Then eUnderMouse=fOpenField(x,y,z) 
    EndIf 
    If nMButton=2 
 If Not oField(x,y,z)\fClear Then eUnderMouse=fMarkField(x,y,z) 
    EndIf 
  EndIf 
 
 If KeyDown(203) 
  cam_yr=cam_yr-1 
 Else If KeyDown(205) 
  cam_yr=cam_yr+1 
 EndIf 
  
 If KeyDown(200) 
  cam_xr=cam_xr+1 
  If cam_xr>180 cam_xr=-180 
 Else If KeyDown(208) 
  cam_xr=cam_xr-1 
   If cam_xr<-180 cam_xr=180 
 EndIf 
  
 If KeyDown(26) 
  cam_zr=cam_zr+1 
 Else If KeyDown(27) 
  cam_zr=cam_zr-1 
 EndIf 
  
 If KeyDown(30) 
  cam_z=cam_z+1:If cam_z>180 cam_z=180 
 Else If KeyDown(44) 
  cam_z=cam_z-1:If cam_z<-180 cam_z=-180 
 EndIf 
  
 PositionEntity cCamera,0,0,0 
 RotateEntity cCamera,cam_xr,cam_yr,cam_zr 
 MoveEntity cCamera,0,0,cam_z 
 
  UpdateWorld 
  RenderWorld 
 
  SetBuffer BackBuffer()   
    Print "Arrow keys: Rotate" 
    Print "A/Z keys: Zoom" 
    Print "Esc: Exit" 
    Print "-=Mouse But=-" 
    Print "Left: Open Field" 
    Print "Right: Mark/Unmark Field" 
    Print "" 
    Print "Mines:"+nMines 
;    Print "bEndGame:"+bEndGame 
;  If eUnderMouse Then  ; отладочный код 
;    Print "dX:"+dx 
;    Print "dY:"+dy 
;    Print "dZ:"+dy 
;    Print "X:"+x 
;    Print "Y:"+y 
;    Print "Z:"+y 
;    Print "fMine:" + oField(x,y,z)\fMine 
;    Print "fMark:" + oField(x,y,z)\fMark 
;    Print "nAroundMine:" + oField(x,y,z)\nAroundMine 
;  End If 
  SetBuffer FrontBuffer()   
 
  Flip 
Wend 
; 
; онец основного цикла 
; 
 
EntityPickMode eCube,0 
EntityPickMode eMine,0 
EntityPickMode eMark,0 
 
fntArialI=LoadFont("Arial",80,True,False,False)  
SetFont fntArialI 
 
Select bEndGame ; ¬ывод сообщени€ окончани€ игры 
  Case 0 
    Text 320,200,"Game Break!",True,True 
    Delay(1000) 
  Case 1 
    Text 320,200,"Game Over!",True,True 
  Case 2 
    Text 320,200,"You are Win!",True,True 
End Select  
 
FreeFont fntArialI 
 
If bEndGame  
  While Not KeyHit(1) 
  Wend 
EndIf 
 
FreeEntity eCube 
FreeEntity eMine 
FreeEntity eMark 
 
End 
; 
; онец 
; 
 
; 
;;;;;;;;;;;;;;;;;;; ‘ункции 
; 
 
;fIsMine(x,y,z) 
; 
; возвращает 1 - если поле (x,y,z) содержит мину и 0 - если не содержит 
; 
Function fIsMine(x,y,z)  
  If x<1 Or x>nFieldSize Or y<1 Or y>nFieldSize Or z<1 Or z>nFieldSize Return 0 
  If oField(x,y,z)\fMine Return 1 
End Function 
 
;fMarkField(x,y,z) 
; 
; метит €чейку пол€ 
; 
Function fMarkField(x,y,z)  
  oField(x,y,z)\fMark = 1-oField(x,y,z)\fMark 
  If oField(x,y,z)\fMark = 1 ; ставим метку 
    nMarkedMines=nMarkedMines+oField(x,y,z)\fMine 
    If nMarkedMines=nMines bEndGame = 2 
     FreeEntity oField(x,y,z)\eModel 
    oField(x,y,z)\eModel=CopyEntity( eMark ) 
    EntityColor oField(x,y,z)\eModel,0,0,255 
    MoveEntity oField(x,y,z)\eModel,x*3-nHalfField,y*3-nHalfField,z*3-nHalfField 
    Return oField(x,y,z)\eModel 
  Else ; снимаем метку 
    nMarkedMines=nMarkedMines-oField(x,y,z)\fMine 
     FreeEntity oField(x,y,z)\eModel 
    oField(x,y,z)\eModel=CopyEntity( eCube ) 
    EntityColor oField(x,y,z)\eModel,255,255,255 
    MoveEntity oField(x,y,z)\eModel,x*3-nHalfField,y*3-nHalfField,z*3-nHalfField 
    Return oField(x,y,z)\eModel 
  EndIf 
End Function 
 
;fOpenField(x,y,z)  
; 
; открывает €чейку пол€ 
; 
Function fOpenField(x,y,z)  
  If x<1 Or x>nFieldSize Or y<1 Or y>nFieldSize Or z<1 Or z>nFieldSize Return 0 
  If oField(x,y,z)\fClear = 1 Return 0 
  dx=x*3-nHalfField 
  dy=y*3-nHalfField 
  dz=z*3-nHalfField 
  oField(x,y,z)\fClear = 1 
  If oField(x,y,z)\nAroundMine = 0 And (Not oField(x,y,z)\fMine) 
     FreeEntity oField(x,y,z)\eModel 
    ;рекурсивный вызов обработки всех окружающих €чеек 
;Z-1 
    fOpenField(x-1,y-1,z-1) 
    fOpenField(x-1,y,z-1) 
    fOpenField(x-1,y+1,z-1) 
    fOpenField(x,y-1,z-1) 
    fOpenField(x,y+1,z-1) 
    fOpenField(x+1,y-1,z-1) 
    fOpenField(x+1,y,z-1) 
    fOpenField(x+1,y+1,z-1) 
    fOpenField(x,y,z-1) 
;Z+1 
    fOpenField(x-1,y-1,z+1) 
    fOpenField(x-1,y,z+1) 
    fOpenField(x-1,y+1,z+1) 
    fOpenField(x,y-1,z+1) 
    fOpenField(x,y+1,z+1) 
    fOpenField(x+1,y-1,z+1) 
    fOpenField(x+1,y,z+1) 
    fOpenField(x+1,y+1,z+1) 
    fOpenField(x,y,z+1) 
;Z 
    fOpenField(x-1,y-1,z) 
    fOpenField(x-1,y,z) 
    fOpenField(x-1,y+1,z) 
    fOpenField(x,y-1,z) 
    fOpenField(x,y+1,z) 
    fOpenField(x+1,y-1,z) 
    fOpenField(x+1,y,z) 
    fOpenField(x+1,y+1,z) 
    Return 0 
  Else If oField(x,y,z)\nAroundMine > 0 And (Not oField(x,y,z)\fMine) 
    Select oField(x,y,z)\nAroundMine ; метим €чейку в соответствии с числом окружающих мин 
  Case 1 EntityColor oField(x,y,z)\eModel,128,128,255 
  Case 2 EntityColor oField(x,y,z)\eModel,128,255,128 
  Case 3 EntityColor oField(x,y,z)\eModel,255,0,0 
  Case 4 EntityColor oField(x,y,z)\eModel,128,64,64 
  Case 5 EntityColor oField(x,y,z)\eModel,64,64,128 
  Case 6 EntityColor oField(x,y,z)\eModel,64,128,64 
  Case 7 EntityColor oField(x,y,z)\eModel,64,255,255 
  Case 8 EntityColor oField(x,y,z)\eModel,255,64,255 
     End Select 
  Else If oField(x,y,z)\fMine ; попандос! 
     FreeEntity oField(x,y,z)\eModel 
     oField(x,y,z)\eModel=CopyEntity(eMine) 
 MoveEntity oField(x,y,z)\eModel,dx,dy,dz 
     EntityColor oField(x,y,z)\eModel,255,50,50 
    bEndGame = 1 
     Return oField(x,y,z)\eModel 
  EndIf 
End Function