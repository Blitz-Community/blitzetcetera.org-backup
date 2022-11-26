Graphics3D 640, 480, 32
SetBuffer BackBuffer()
HidePointer

Global God = CreateCamera()

MoveEntity god,0,2,0

Global Red=0      ; ������������
Global Green=150  ; �����
Global Blue=200   ; ����

Global Day=1.0    ; ������ � ��� ��������� ����
CameraClsColor God,red,green,Blue; ����

Global DayTime=100000 ; �������� �����
Global DayBegin=0   ; ����� ��������� ����� ��� � ����


Pl= CreatePlane()             ;�����, ������� ����
EntityAlpha Pl,0.5
tx=LoadTexture("media\water.jpg")   ;
EntityTexture Pl,tx           ;
FreeTexture tx
EntityPickMode pl,2
NameEntity pl,"Water"

sh= CreatePlane()             ; ��� - �����

Land=LoadTerrain("media\land.png")    ; �������

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

AmbientLight 100,100,100                ; ��� �������� �������� �����
Global l = CreateLight()             ; ����, ������� ������
TurnEntity l,30,20,0

Dim Flora(2)

Flora(1)=LoadMesh("media\tree\tree01.b3d")
HideEntity Flora(1)
Flora(2)=LoadMesh("media\tree\tree01.b3d")
ScaleEntity Flora(2),1,0.5,1
HideEntity Flora(2)

For i=1 To 3000 ; ���������� ��������
en=0
While en=0
 x#=Rnd(-2500,2500)
 z#=Rnd(-2500,2500)
 LinePick x,200,z,0,-200,0
 en=PickedEntity()
 If en
  If EntityName(en)="Land"         ; � ���� �������� �� ������.
   tr=CopyEntity(flora(Rnd(1,2)))
   PositionEntity tr,x,PickedY#(),z
  Else
   en=0
  EndIf
 EndIf
Wend
Next

Global Center=CreatePivot() ; ����� �������, ��� ��������� ��������� � ������� (� ���� �.�.)
Global star=CreateSphere(8,center) ; ������
EntityFX star,1
ScaleEntity star,50,50,50
FlipMesh star
tx=LoadTexture("Media\space\stars.png",4)
EntityTexture star,tx
FreeTexture tx
EntityOrder star,1.0
Global solCen=CreatePivot(Center)  ; ����� ������ �������� ��������� ������
sol=LoadSprite("Media\space\sol.png",1,SolCen); ������
MoveEntity sol,0,0,15
EntityOrder sol,0.9

Global MoonCen=CreatePivot(Center) ; ����� ������ �������� ��������� ����
Moon=LoadSprite("Media\space\moon.png",1,MoonCen); ����
MoveEntity moon,0,0,-15
EntityOrder moon,0.8



While Not KeyDown(1)

GodMovement()   ; ������� ��� ����� � ��� ���

DayControl()  ;����� ��� � ����

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
TurnEntity God,MouseYSpeed(),-MouseXSpeed(),0 ; ������� ������ ������ ��� FPS
TurnEntity god,0,0,-EntityRoll(god) ; ����� �� ���������������� ������
MoveEntity God,KeyDown(32)-KeyDown(30),0,KeyDown(17)-KeyDown(31) ; �������� ������ wasd
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 ; ��� ����� ����� ����� �� ������� �� �����
End Function

Function DayControl()
If DayBegin=0 Then DayBegin=MilliSecs()  ; ���� ������ �������
delta#=Float(MilliSecs()-DayBegin)/DayTime ; ����� ����� ��� ������?
If delta>=1                                ; ����/���� ��������
 delta=1
 DayBegin=MilliSecs()                       ;������� ����� ����/����
EndIf
If day=-1                                  ;����� ����� ���������� ������
 delta=1-delta
EndIf
LightColor l,255*delta,255*delta,255*delta
CameraClsColor God,red*delta,green*delta,Blue*delta  ; ���� ���� ������� �� ������� �����
EntityAlpha star,1-delta
SpaceControl(delta,day)
If delta=1 Or delta=0 Then day=-day                  ; � �������/������� ������� ������� �������� ���
End Function