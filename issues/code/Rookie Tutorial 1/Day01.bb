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

Global DayTime=10000 ; �������� �����
Global DayBegin=0   ; ����� ��������� ����� ��� � ����


Pl= CreatePlane()             ;�����, ������� ����
tx=LoadTexture("media\water.jpg")   ;
EntityTexture Pl,tx           ;

AmbientLight 50,50,50                ; ��� �������� �������� �����
Global l = CreateLight()             ; ����, ������� ������
TurnEntity l,30,20,0

While Not KeyDown(1)

GodMovement()   ; ������� ��� ����� � ��� ���

DayControl()  ;����� ��� � ����

RenderWorld
Flip

Wend

End

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
If delta=1 Or delta=0 Then day=-day                  ; � �������/������� ������� ������� �������� ���
End Function
