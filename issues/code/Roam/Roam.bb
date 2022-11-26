Graphics3D 640,480,0,2

Global RoamMaxHeightError#=5.0
Global MinimumTileSize=64.0

AppTitle "Загрузка карты высот"

;Устанавливаем максимальную высоту
RoamTerrainMaxHeight#=40.96
;Грузим картинку
HmapImage=LoadImage("roam_hmap.jpg")
;Достаем из картинки раземр террайна
Global RoamTerrainWidth=ImageWidth(HmapImage)-1
;создаем массив с картой высот
Dim RoamTerrainHeight#(RoamTerrainWidth,RoamTerrainWidth)
;считываем массив из картинки
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
;Удаляем картинку, она нам более не нужна
FreeImage HmapImage

AppTitle "Поиск соседей основания"

;маскимальное количество треугольников в террайне
RoamMaxTriangles=RoamTerrainWidth*RoamTerrainWidth*2-1

;массивы содержащие базового соседа и его флаг
Dim RoamBaseNeighbor%(RoamMaxTriangles)
Dim RoamBaseNeighborFlag%(RoamMaxTriangles)

Function  FindTriBaseNeighbor(Number)
;если для данного треугольника уже задан сосед, то мы пропускаем
If RoamBaseNeighbor(number)>0 : Return : EndIf
;если это треугольник 1,2 или 3
If Number<4
	Select Number
		Case 1
			RoamBaseNeighbor(Number)=1
			RoamBaseNeighborFlag(Number)=1 ;сосед в другой структуре
		Case 2
			RoamBaseNeighbor(Number)=0	;нет соседа
			RoamBaseNeighborFlag(Number)=0
		Case 3
			RoamBaseNeighbor(Number)=0	;нет соседа
			RoamBaseNeighborFlag(Number)=0
	End Select
Return
EndIf
;поиск родителя и его обоих детей
Parent=Number Shr 1
ParentLeftChild=(Parent Shl 1)
ParentRightChild=(Parent Shl 1)+1

;поиск прародителя и его обоих детей
PraParent=Parent Shr 1
PraParentLeftChild=(PraParent Shl 1)
PraParentRightChild=(PraParent Shl 1)+1

;случай №3 в разделе "Мои добавления в метод " 
If Number=ParentLeftChild And Parent=PraParentLeftChild
	;находим правого потомка правого потомка  прародителя,
	PraParentRightChildRightChild=(PraParentRightChild Shl 1)+1
	;который является соседом основания искомого треугольника
	RoamBaseNeighbor(Number)=PraParentRightChildRightChild
	RoamBaseNeighborFlag(Number)=0
	;искомый треуольник является также соседом основания
	;правого потомка правого потомка прародителя
	RoamBaseNeighbor(PraParentRightChildRightChild)=Number
	RoamBaseNeighborFlag(PraParentRightChildRightChild)=0
Return
EndIf

;тот же случай №3 , но в обратную сторону
If Number=ParentRightChild And Parent=PraParentRightChild
	;находим левого потомка левого потомка  прародителя,
	PraParentLeftChildLeftChild=(PraParentLeftChild Shl 1)
	;который является соседом основания искомого треугольника
	RoamBaseNeighbor(Number)=PraParentLeftChildLeftChild
	RoamBaseNeighborFlag(Number)=0
	;искомый треуольник является также соседом основания
	;левого потомка левого потомка прародителя
	RoamBaseNeighbor(PraParentLeftChildLeftChild)=Number
	RoamBaseNeighborFlag(PraParentLeftChildLeftChild)=0
Return
EndIf

;Теперь проверяем есть ли у прародителя сосед основания

PraParentBaseNeighbor=RoamBaseNeighbor(PraParent)
PraParentBaseNeighborFlag=RoamBaseNeighborFlag(PraParent)

;если нет, то и у искомого быть не может
If PraParentBaseNeighbor=0
	RoamBaseNeighbor(Number)=0
	RoamBaseNeighborFlag(Number)=0
	Return
EndIf

;проверим случай №4
;если искомый треугольник првый потомок, а родитель - левый
If Number=ParentRightChild And Parent=PraParentLeftChild
	;соседом будет левый потомок правого потомка соседа основания прародителя
    PraParentBaseNeighborRightChild=(PraParentBaseNeighbor Shl 1)+1
    PraParentBaseNeighborRightChildLeftChild=(PraParentBaseNeighborRightChild Shl 1)
	;заносим в массив и копируем флаг от флага прародителя
    RoamBaseNeighbor(Number)=PraParentBaseNeighborRightChildLeftChild
    RoamBaseNeighborFlag(Number)=PraParentBaseNeighborFlag
	; и наоборот
    RoamBaseNeighbor(PraParentBaseNeighborRightChildLeftChild)=Number
    RoamBaseNeighborFlag(PraParentBaseNeighborRightChildLeftChild)=PraParentBaseNeighborFlag
    Return
EndIf

;Теперь остается проверить случай №4 наоборот
;когда искомый треугольник левый потомок, а родитель - правый
If Number=ParentLeftChild And Parent=PraParentRightChild
	;соседом будет левый потомок правого потомка соседа основания прародителя
    PraParentBaseNeighborLeftChild=(PraParentBaseNeighbor Shl 1)
    PraParentBaseNeighborLeftChildRightChild=(PraParentBaseNeighborLeftChild Shl 1)+1
	;заносим в массив и копируем флаг от флага прародителя
    RoamBaseNeighbor(Number)=PraParentBaseNeighborLeftChildRightChild
    RoamBaseNeighborFlag(Number)=PraParentBaseNeighborFlag
	; и наоборот
    RoamBaseNeighbor(PraParentBaseNeighborLeftChildRightChild)=Number
    RoamBaseNeighborFlag(PraParentBaseNeighborLeftChildRightChild)=PraParentBaseNeighborFlag
EndIf

End Function

;непосредственно перебор всех треугольников и поиск их соседей оснований
For CurrentNumber=1 To RoamMaxTriangles
  FindTriBaseNeighbor(CurrentNumber)
Next

;проверим несколько треугольников из дебага по картинке
For i=1 To 31
DebugLog "Tri# "+Str(i)+" Base neighbor: "+Str(RoamBaseNeighbor(i)) +" Flag: "+Str(RoamBaseNeighborFlag(i))
Next

;Все трегольники после этого числа не будут делиться
Global RoamCriticalTriLevel=RoamTerrainWidth*RoamTerrainWidth-1
;создаем банки памяти для хранения бинароного дерева треугольников
Dim RoamTriangle(1)
RoamTriangle(0)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)	;ветка 0 - правый верхний треугольник
RoamTriangle(1)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)	;ветка 1 - левый нижний треугольник
;Минимальная детализация
Global MinTileSizeLevel=2^(RoamTerrainWidth/MinimumTileSize)

; Функция, определяющая cтоит ли бить треугольник или нет
Function RoamBreakTriangle(x0,z0,x1,z1,x2,z2,Number,Branch)

;если треугольник входит в число наиболее детализированных
If Number>RoamCriticalTriLevel
	;помечаем как не битый и выходим
	PokeByte(RoamTriangle(Branch),Number,0)
	Return
EndIf

;Если треуольник входит в число треугольников наименьшей детализации
If Number<MinTileSizeLevel
	;находим координаты центральной точки
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	;находим потомков
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	;помечаем треугольник как разбитый
	PokeByte(RoamTriangle(Branch),Number,1)
	;вызываем функцию разбивания для потомков
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	;возврат, чтобы не делать лишних просчетов
	Return
EndIf

;если треугольник уже помечен как рзбитый (в процессе ForceSplit)
;то тоже проверяем его потомков
 If PeekByte(RoamTriangle(Branch),Number)=1
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	Return
EndIf

;после предыдущих проверок остается одна возможность разбивать или
;не разбивать треугольник - это проверка погрешности высоты
xC=(x0+x2)/2
zC=(z0+z2)/2
;погрешность высоты
DeltaHeight#=Abs(RoamTerrainHeight(xC,zC)-(RoamTerrainHeight(x0,z0)+RoamTerrainHeight(x2,z2))*0.5)
;если она больше максимальной ошибки то разбиваем
If DeltaHeight>=RoamMaxHeightError
	; - " -
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	PokeByte(RoamTriangle(Branch),Number,1)
	RoamBreakTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamBreakTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	; если у треугольника есть сосед основания то разбиваем
	; его специальной функцией (см ниже)
	If RoamBaseNeighbor(Number)>0
		;Обратите внимание что результирущая ветка находится через маску Xor
		RoamForceSplitTri(RoamBaseNeighbor(Number),Branch Xor RoamBaseNeighborFlag(Number))
	EndIf

EndIf
End Function 
;функция, разбивающая соседа основания треугольника
Function RoamForceSplitTri(Number,Branch)
;если треугольник уже разбит то выходим
If PeekByte(RoamTriangle(Branch),Number)=1
	Return
EndIf
;помечаем как разбитый
PokeByte(RoamTriangle(Branch),Number,1)
;пробуем разбить его соседа основания
If RoamBaseNeighbor(Number)>0
	RoamForceSplitTri(RoamBaseNeighbor(Number),Branch Xor RoamBaseNeighborFlag(Number))
EndIf
;и переходим к родителю
Parent=Number Shr 1
RoamForceSplitTri(Parent,Branch)
End Function

;создаем меш
Global RoamMesh=CreateMesh()
;помещаем меш в центр мира
PositionEntity RoamMesh,-0.5*RoamTerrainWidth,0,-0.5*RoamTerrainWidth
;грзим текстуру и текстурим
tex=LoadTexture("sand.jpg")
ScaleTexture tex,4,4
EntityTexture RoamMesh,tex
;создаем сурфейс и массив вертексов
Global RoamSurface=CreateSurface(RoamMesh)
Dim RoamVertex(RoamTerrainWidth,RoamTerrainWidth)

;функция создающая треугольник па банку памяти
Function RoamCreateTriangle(x0,z0,x1,z1,x2,z2,Number,Branch)
;если треугольник разбит, то переходм к его потомкам
 If PeekByte(RoamTriangle(Branch),Number)=1
	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	RoamCreateTriangle(x1,z1,xC,zC,x0,z0,LeftChild,Branch)
	RoamCreateTriangle(x2,z2,xC,zC,x1,z1,RightChild,Branch)
	Return
Else
	;или создаем тругольник

	;проверяем создан ли вертекс 0
	If RoamVertex(x0,z0)=0
		RoamVertex(x0,z0)=AddVertex(RoamSurface,x0,RoamTerrainHeight(x0,z0),z0,x0,z0)
	EndIf

	;проверяем создан ли вертекс 1
	If RoamVertex(x1,z1)=0
		RoamVertex(x1,z1)=AddVertex(RoamSurface,x1,RoamTerrainHeight(x1,z1),z1,x1,z1)
	EndIf

	;проверяем создан ли вертекс 2
	If RoamVertex(x2,z2)=0
		RoamVertex(x2,z2)=AddVertex(RoamSurface,x2,RoamTerrainHeight(x2,z2),z2,x2,z2)
	EndIf

	;создаем 
	AddTriangle (RoamSurface,RoamVertex(x0,z0),RoamVertex(x1,z1),RoamVertex(x2,z2))
	
EndIf

End Function

;функция очищающая память, разбивающая ландшафт и создающая треугольники
Function CreateLand()
time=MilliSecs()
AppTitle "Создается ландшафт..."
;очищаем банки памяти обеих ветвей треугольников
FreeBank RoamTriangle(0)
FreeBank RoamTriangle(1)
;и создаем заново пустые
RoamTriangle(0)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
RoamTriangle(1)=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
;пересоздаем массив вертексов
Dim RoamVertex(RoamTerrainWidth,RoamTerrainWidth)
;очищаем сурфейс и создаем вертекс 0
ClearSurface RoamSurface,1,1
AddVertex RoamSurface,0,0,0
;разбиваем треугольники
RoamBreakTriangle(0,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0)
RoamBreakTriangle(RoamTerrainWidth,0,0,0,0,RoamTerrainWidth,1,1)
;создаем треугольники
RoamCreateTriangle(0,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0)
RoamCreateTriangle(RoamTerrainWidth,0,0,0,0,RoamTerrainWidth,1,1)
;обновляем нормали
UpdateNormals(RoamMesh)
AppTitle "Ландшафт ("+RoamTerrainWidth+"x"+RoamTerrainWidth+") создан за "+Str(MilliSecs()-time)+" мс. hError: "+Str(RoamMaxHeightError)
End Function 

;создаем террайн
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
;Управляющие переменные
	wf=0			;WireFrame

;сброс данных мыша
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
