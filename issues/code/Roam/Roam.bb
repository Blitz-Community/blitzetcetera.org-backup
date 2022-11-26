Graphics3D 640,480,0,2

Global RoamMaxHeightError#=5.0
Global MinimumTileSize=64.0

AppTitle "�������� ����� �����"

;������������� ������������ ������
RoamTerrainMaxHeight#=40.96
;������ ��������
HmapImage=LoadImage("roam_hmap.jpg")
;������� �� �������� ������ ��������
Global RoamTerrainWidth=ImageWidth(HmapImage)-1
;������� ������ � ������ �����
Dim RoamTerrainHeight#(RoamTerrainWidth,RoamTerrainWidth)
;��������� ������ �� ��������
SetBuffer ImageBuffer(HmapImage)
LockBuffer ImageBuffer(HmapImage)
For PixX=0 To RoamTerrainWidth
For PixY=0 To RoamTerrainWidth
	Pixel=ReadPixelFast(PixX,PixY)
	RedColor=(Pixel Shr 16) And ($000000FF)
	GreenColor=(Pixel Shr 8) And ($000000FF)
	RoamTerrainHeight#(PixX,PixY)=(RedColor/255.0)*RoamTerrainMaxHeight-(GreenColor/255.0)*RoamTerrainMaxHeight
Next
Next
UnlockBuffer ImageBuffer(HmapImage)
SetBuffer BackBuffer()
;������� ��������, ��� ��� ����� �� �����
FreeImage HmapImage

AppTitle "����� ������� ���������"

;������������ ���������� ������������� � ��������
RoamMaxTriangles=RoamTerrainWidth*RoamTerrainWidth*2-1

;������� ���������� �������� ������ � ��� ����
Dim RoamBaseNeighbor%(RoamMaxTriangles)
Dim RoamBaseNeighborFlag%(RoamMaxTriangles)

Function  FindTriBaseNeighbor(Number)
;���� ��� ������� ������������ ��� ����� �����, �� �� ����������
If RoamBaseNeighbor(number)>0 : Return : EndIf
;���� ��� ����������� 1,2 ��� 3
If Number<4
	Select Number
		Case 1
			RoamBaseNeighbor(Number)=1
			RoamBaseNeighborFlag(Number)=1 ;����� � ������ ���������
		Case 2
			RoamBaseNeighbor(Number)=0	;��� ������
			RoamBaseNeighborFlag(Number)=0
		Case 3
			RoamBaseNeighbor(Number)=0	;��� ������
			RoamBaseNeighborFlag(Number)=0
	End Select
Return
EndIf
;����� �������� � ��� ����� �����
Parent=Number Shr 1
ParentLeftChild=(Parent Shl 1)
ParentRightChild=(Parent Shl 1)+1

;����� ����������� � ��� ����� �����
PraParent=Parent Shr 1
PraParentLeftChild=(PraParent Shl 1)
PraParentRightChild=(PraParent Shl 1)+1

;������ �3 � ������� "��� ���������� � ����� " 
If Number=ParentLeftChild And Parent=PraParentLeftChild
	;������� ������� ������� ������� �������  �����������,
	PraParentRightChildRightChild=(PraParentRightChild Shl 1)+1
	;������� �������� ������� ��������� �������� ������������
	RoamBaseNeighbor(Number)=PraParentRightChildRightChild
	RoamBaseNeighborFlag(Number)=0
	;������� ���������� �������� ����� ������� ���������
	;������� ������� ������� ������� �����������
	RoamBaseNeighbor(PraParentRightChildRightChild)=Number
	RoamBaseNeighborFlag(PraParentRightChildRightChild)=0
Return
EndIf

;��� �� ������ �3 , �� � �������� �������
If Number=ParentRightChild And Parent=PraParentRightChild
	;������� ������ ������� ������ �������  �����������,
	PraParentLeftChildLeftChild=(PraParentLeftChild Shl 1)
	;������� �������� ������� ��������� �������� ������������
	RoamBaseNeighbor(Number)=PraParentLeftChildLeftChild
	RoamBaseNeighborFlag(Number)=0
	;������� ���������� �������� ����� ������� ���������
	;������ ������� ������ ������� �����������
	RoamBaseNeighbor(PraParentLeftChildLeftChild)=Number
	RoamBaseNeighborFlag(PraParentLeftChildLeftChild)=0
Return
EndIf

;������ ��������� ���� �� � ����������� ����� ���������

PraParentBaseNeighbor=RoamBaseNeighbor(PraParent)
PraParentBaseNeighborFlag=RoamBaseNeighborFlag(PraParent)

;���� ���, �� � � �������� ���� �� �����
If PraParentBaseNeighbor=0
	RoamBaseNeighbor(Number)=0
	RoamBaseNeighborFlag(Number)=0
	Return
EndIf

;�������� ������ �4
;���� ������� ����������� ����� �������, � �������� - �����
If Number=ParentRightChild And Parent=PraParentLeftChild
	;������� ����� ����� ������� ������� ������� ������ ��������� �����������
    PraParentBaseNeighborRightChild=(PraParentBaseNeighbor Shl 1)+1
    PraParentBaseNeighborRightChildLeftChild=(PraParentBaseNeighborRightChild Shl 1)
	;������� � ������ � �������� ���� �� ����� �����������
    RoamBaseNeighbor(Number)=PraParentBaseNeighborRightChildLeftChild
    RoamBaseNeighborFlag(Number)=PraParentBaseNeighborFlag
	; � ��������
    RoamBaseNeighbor(PraParentBaseNeighborRightChildLeftChild)=Number
    RoamBaseNeighborFlag(PraParentBaseNeighborRightChildLeftChild)=PraParentBaseNeighborFlag
    Return
EndIf

;������ �������� ��������� ������ �4 ��������
;����� ������� ����������� ����� �������, � �������� - ������
If Number=ParentLeftChild And Parent=PraParentRightChild
	;������� ����� ����� ������� ������� ������� ������ ��������� �����������
    PraParentBaseNeighborLeftChild=(PraParentBaseNeighbor Shl 1)
    PraParentBaseNeighborLeftChildRightChild=(PraParentBaseNeighborLeftChild Shl 1)+1
	;������� � ������ � �������� ���� �� ����� �����������
    RoamBaseNeighbor(Number)=PraParentBaseNeighborLeftChildRightChild
    RoamBaseNeighborFlag(Number)=PraParentBaseNeighborFlag
	; � ��������
    RoamBaseNeighbor(PraParentBaseNeighborLeftChildRightChild)=Number
    RoamBaseNeighborFlag(PraParentBaseNeighborLeftChildRightChild)=PraParentBaseNeighborFlag
EndIf

End Function

;��������������� ������� ���� ������������� � ����� �� ������� ���������
For CurrentNumber=1 To RoamMaxTriangles
  FindTriBaseNeighbor(CurrentNumber)
Next

;�������� ��������� ������������� �� ������ �� ��������
For i=1 To 31
DebugLog "Tri# "+Str(i)+" Base neighbor: "+Str(RoamBaseNeighbor(i)) +" Flag: "+Str(RoamBaseNeighborFlag(i))
Next

;��� ����������� ����� ����� ����� �� ����� ��������
Global RoamCriticalTriLevel=RoamTerrainWidth*RoamTerrainWidth-1
;������� ����� ������ ��� �������� ���������� ������ �������������
Dim RoamTriangle(1)
RoamTriangle(0)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)	;����� 0 - ������ ������� �����������
RoamTriangle(1)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)	;����� 1 - ����� ������ �����������
;����������� �����������
Global MinTileSizeLevel=2^(RoamTerrainWidth/MinimumTileSize)

; �������, ������������ c���� �� ���� ����������� ��� ���
Function RoamBreakTriangle(x0,z0,x1,z1,x2,z2,Number,Branch)

;���� ����������� ������ � ����� �������� ����������������
If Number>RoamCriticalTriLevel
	;�������� ��� �� ����� � �������
	PokeByte(RoamTriangle(Branch),Number,0)
	Return
EndIf

;���� ���������� ������ � ����� ������������� ���������� �����������
If Number<MinTileSizeLevel
	;������� ���������� ����������� �����
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	;������� ��������
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	;�������� ����������� ��� ��������
	PokeByte(RoamTriangle(Branch),Number,1)
	;�������� ������� ���������� ��� ��������
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	;�������, ����� �� ������ ������ ���������
	Return
EndIf

;���� ����������� ��� ������� ��� ������� (� �������� ForceSplit)
;�� ���� ��������� ��� ��������
 If PeekByte(RoamTriangle(Branch),Number)=1
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	Return
EndIf

;����� ���������� �������� �������� ���� ����������� ��������� ���
;�� ��������� ����������� - ��� �������� ����������� ������
xC=(x0+x2)/2
zC=(z0+z2)/2
;����������� ������
DeltaHeight#=Abs(RoamTerrainHeight(xC,zC)-(RoamTerrainHeight(x0,z0)+RoamTerrainHeight(x2,z2))*0.5)
;���� ��� ������ ������������ ������ �� ���������
If DeltaHeight>=RoamMaxHeightError
	; - " -
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	PokeByte(RoamTriangle(Branch),Number,1)
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	; ���� � ������������ ���� ����� ��������� �� ���������
	; ��� ����������� �������� (�� ����)
	If RoamBaseNeighbor(Number)>0
		;�������� �������� ��� ������������� ����� ��������� ����� ����� Xor
		RoamForceSplitTri(RoamBaseNeighbor(Number),Branch Xor RoamBaseNeighborFlag(Number))
	EndIf

EndIf
End Function 
;�������, ����������� ������ ��������� ������������
Function RoamForceSplitTri(Number,Branch)
;���� ����������� ��� ������ �� �������
If PeekByte(RoamTriangle(Branch),Number)=1
	Return
EndIf
;�������� ��� ��������
PokeByte(RoamTriangle(Branch),Number,1)
;������� ������� ��� ������ ���������
If RoamBaseNeighbor(Number)>0
	RoamForceSplitTri(RoamBaseNeighbor(Number),Branch Xor RoamBaseNeighborFlag(Number))
EndIf
;� ��������� � ��������
Parent=Number Shr 1
RoamForceSplitTri(Parent,Branch)
End Function

;������� ���
Global RoamMesh=CreateMesh()
;�������� ��� � ����� ����
PositionEntity RoamMesh,-0.5*RoamTerrainWidth,0,-0.5*RoamTerrainWidth
;����� �������� � ���������
tex=LoadTexture("sand.jpg")
ScaleTexture tex,4,4
EntityTexture RoamMesh,tex
;������� ������� � ������ ���������
Global RoamSurface=CreateSurface(RoamMesh)
Dim RoamVertex(RoamTerrainWidth,RoamTerrainWidth)

;������� ��������� ����������� �� ����� ������
Function RoamCreateTriangle(x0,z0,x1,z1,x2,z2,Number,Branch)
;���� ����������� ������, �� �������� � ��� ��������
 If PeekByte(RoamTriangle(Branch),Number)=1
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	RoamCreateTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamCreateTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	Return
Else
	;��� ������� ����������

	;��������� ������ �� ������� 0
	If RoamVertex(x0,z0)=0
		RoamVertex(x0,z0)=AddVertex(RoamSurface,x0,RoamTerrainHeight(x0,z0),z0,x0,z0)
	EndIf

	;��������� ������ �� ������� 1
	If RoamVertex(x1,z1)=0
		RoamVertex(x1,z1)=AddVertex(RoamSurface,x1,RoamTerrainHeight(x1,z1),z1,x1,z1)
	EndIf

	;��������� ������ �� ������� 2
	If RoamVertex(x2,z2)=0
		RoamVertex(x2,z2)=AddVertex(RoamSurface,x2,RoamTerrainHeight(x2,z2),z2,x2,z2)
	EndIf

	;������� 
	AddTriangle (RoamSurface,RoamVertex(x0,z0),RoamVertex(x1,z1),RoamVertex(x2,z2))
	
EndIf

End Function

;������� ��������� ������, ����������� �������� � ��������� ������������
Function CreateLand()
time=MilliSecs()
AppTitle "��������� ��������..."
;������� ����� ������ ����� ������ �������������
FreeBank RoamTriangle(0)
FreeBank RoamTriangle(1)
;� ������� ������ ������
RoamTriangle(0)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
RoamTriangle(1)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
;����������� ������ ���������
Dim RoamVertex(RoamTerrainWidth,RoamTerrainWidth)
;������� ������� � ������� ������� 0
ClearSurface RoamSurface,1,1
AddVertex RoamSurface,0,0,0
;��������� ������������
RoamBreakTriangle(0,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0)
RoamBreakTriangle(RoamTerrainWidth,0,0,0,0,RoamTerrainWidth,1,1)
;������� ������������
RoamCreateTriangle(0,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0)
RoamCreateTriangle(RoamTerrainWidth,0,0,0,0,RoamTerrainWidth,1,1)
;��������� �������
UpdateNormals(RoamMesh)
AppTitle "�������� ("+RoamTerrainWidth+"x"+RoamTerrainWidth+") ������ �� "+Str(MilliSecs()-time)+" ��. hError: "+Str(RoamMaxHeightError)
End Function 

;������� �������
CreateLand()

;Create world
	cam=CreateCamera()
	CameraRange cam,0.01,10000
	PositionEntity cam,0,0,-5
	DestCamYaw#=0
	CamYaw#=0
	DestCamPitch#=0
	CamPitch#=0

;Mouse sensivity (0-1)
	mSens#=0.9
;Mouse move inertion (0-1)
	mInert#=0.05
;����������� ����������
	wf=0			;WireFrame

;����� ������ ����
	MoveMouse 320,240
	FlushMouse()
;Delta time init
	LastFrame=MilliSecs()
;light create
	light=CreateLight()


	
;main loop
Repeat

	If KeyHit(2)
		RoamMaxHeightError#=5.0
		CreateLand()
	EndIf

	If KeyHit(3)
		RoamMaxHeightError#=2.5
		CreateLand()
	EndIf

	If KeyHit(4)
		RoamMaxHeightError#=1.5
		CreateLand()
	EndIf

	If KeyHit(5)
		RoamMaxHeightError#=1.0
		CreateLand()
	EndIf

	If KeyHit(6)
		RoamMaxHeightError#=0.5
		CreateLand()
	EndIf

;toogle wireframe
	If KeyHit(59) Then
	wf=1-wf
	WireFrame wf
	EndIf


;delta time
	Dt#=(MilliSecs()-LastFrame)*0.4
	LastFrame=MilliSecs()
;exit
	If KeyHit(1) Exit
;stop
	If KeyHit(88) Stop
;camera control
	DestCamYaw#=DestCamYaw#-MouseXSpeed()*mSens#
	DestCamPitch#=DestCamPitch#+MouseYSpeed()*mSens#
	If DestCamPitch#>90 Then DestCamPitch#=90
	If DestCamPitch#<-90 Then DestCamPitch#=-90
	CamYaw#=CamYaw#+(DestCamYaw#-CamYaw#)*mInert#
	CamPitch#=CamPitch#+(DestCamPitch#-CamPitch#)*mInert#
	If Abs(DestCamYaw#-CamYaw#)<0.1 Then CamYaw#=DestCamYaw#
	If Abs(DestCamPitch#-CamPitch#)<0.1 Then CamPitch#=DestCamPitch#
	RotateEntity cam,CamPitch#,CamYaw#,0
	MoveMouse 320,240
	MoveEntity cam,(KeyDown(32)-KeyDown(30))*Dt#,0,(KeyDown(17)-KeyDown(31))*Dt#
;global update
	UpdateWorld
	RenderWorld
;fps counter
	If FPSTimer>MilliSecs() Then
	fpsc=fpsc+1
	Else
	fps=fpsc*2
	fpsc=0
	FPSTimer=MilliSecs()+500
	EndIf
;hud
	Text 100,25,"WSAD to move, mouse to look"
	Text 100,45,"F1 To toggle WireFrame mode ("+wf+")"
	Text 100,65,"Hit 1-2-3-4-5 to change detalisation"
	Text 100,85,"FPS: "+fps+". Tris rendered:"+TrisRendered()
Flip 0
Forever
