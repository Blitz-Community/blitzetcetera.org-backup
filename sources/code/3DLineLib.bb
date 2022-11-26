Graphics3D 640,480,32,2
Global FPS1,LastCheck,Frames1

; #########################################################################
; #  3D Accelerated Line Library (c)2004 Tim Fisher                       #
; #  This code is public domain, do wih it what you like. 				  #
; #########################################################################

Global screenwidth# 
Global screenheight# 

Global LineSurf
Global LineTex
Global LineCam
Global LineMesh
Global Pivot0 = CreatePivot()
Global Pivot1 = CreatePivot()

Function InitLine3d()

	LineTex = CreateTexture(16,16,2)

	SetBuffer TextureBuffer(LineTex)
	Line 7,0,7,15
	Line 8,0,8,15
	SetBuffer BackBuffer()

	LineCam = CreateCamera()
	PositionEntity LineCam,0,0,-40
	CameraProjMode LineCam,2
	CameraZoom LineCam,0.1

	LineMesh = CreateMesh()
	LineSurf = CreateSurface(LineMesh)
	EntityBlend LineMesh,3
	EntityTexture LineMesh,LineTex
	EntityFX LineMesh,3
	
	ScreenWidth = GraphicsWidth()
	ScreenHeight = GraphicsHeight()

End Function

Function ClsLines()

	ClearSurface(LineSurf)
	
End Function


Function Line3d( Mesh,x0#,y0#,x1#,y1# , r=255, g=255, b=255, w# = .25 )

	sx# = x0/ScreenWidth
	x0# = (sx * 20.0) - 10
	sy# = y0/ScreenHeight
	y0# = 7.5 - (sy * 15)
	sx# = x1/ScreenWidth
	x1# = (sx * 20.0) - 10
	sy# = y1/ScreenHeight
	y1# = 7.5 - (sy * 15)

	If Mesh = 0 
		Mesh = CreateMesh() 
		LineSurf = CreateSurface(Mesh) 
	Else 
		LineSurf = GetSurface(Mesh,1)
		If CountVertices(LineSurf)>30000
			LineSurf = CreateSurface(Mesh)
		EndIf 
	End If 

	PositionEntity Pivot0, x0,0,y0
	PositionEntity Pivot1,x1,0,y1
	PointEntity Pivot0,Pivot1
	TFormNormal 0,0,1,Pivot0,0
	dx# = TFormedX()*w
	dy# = TFormedZ()*w
	
	v0 = AddVertex( LineSurf 	, x0 - dy	, y0 + dx 	, 0	,0,0 )
	v1 = AddVertex( LineSurf	, x1 - dy	, y1 + dx	, 0 	,0,1 )
	v2 = AddVertex( LineSurf	, x1 + dy	, y1 - dx	, 0 	,1,0 )
	v3 = AddVertex( LineSurf	, x0 + dy	, y0 - dx 	, 0 	,1,1 )
	
	For v = v0 To v3
		VertexColor LineSurf, v, r,g, b
	Next
		
	AddTriangle LineSurf,v0,v1,v2 
	AddTriangle LineSurf,v2,v3,v0

	Return Mesh 

End Function



InitLine3d()

SeedRnd MilliSecs()


While Not KeyHit(1)


    For x = 0 To 1000
 		LineMesh = line3d(LineMesh,Rand(0,ScreenWidth),Rand(0,ScreenHeight),Rand(0,ScreenWidth),Rand(0,ScreenHeight),Rand(255),Rand(255),Rand(255))
	Next

	RenderWorld

	GetFPS()
	Text 400,2,"FPS : "+FPS1

	Flip False
	
	ClsLines()

Wend


Function GetFPS()
	Frames1 = Frames1 + 1
	
	If MilliSecs() > LastCheck+1000 Then
		LastCheck = MilliSecs()
		FPS1 = Frames1
		Frames1 = 0
	EndIf
	Return FPS1
End Function

