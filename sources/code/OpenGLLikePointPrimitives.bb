; OpenGL-Like Point Primitives - By: Todd Riggins - Free for all! No $$$ required!
; Version Ooo.err.4 (D. Woodgate)  Fixes the nasty graphic glitches by another means and added a 
; camerazoom option and chasecam to the example. Is it any faster than before - err, no not really :(
 
; Use arrows to move around
; right mouse click to rotate camera
; - Notice how the square-ish points remain the same size no matter how close they
;   are to the camera! 
; - Unfortunitly this system is not fast enough for big "group vertice selections" like
;   you would want in moddeling editors. It's definitely fast enough for level editors like Maplet!
; - This system is all in 3D. Didn't want to use 2D graphics here. Doing this in 3D help'd me
;   mimic OpenGl point primitives pretty much To the T.
; !!!! If anybody has any ideas to speed this up, please let me know. Moving the vertices around all
; the time is what slows this system down the more point primitives you have.

; ------------------------------------------------------------------------
;  Superior Documentation...
; ------------------------------------------------------------------------
; thisPoint.POINTPRIMITIVE=AddPoint.POINTPRIMITIVE( x#, y#, z#)
;  		AddPoint returns a type handle to a newly created point primitive.
;        x#, y#, z# parameters = initial 3d location for point primitive.
; ------------------------------------------------------------------------
; PositionPoint(thisPoint.POINTPRIMITIVE, x#, y#, z#)
;		Position's an already created point primitive in a new 3d location.
; ------------------------------------------------------------------------
; PointSize(thisPoint.POINTPRIMITIVE,size)
; 		Make the point primitive smaller or bigger. Default = 2
; ------------------------------------------------------------------------
; PointBias(thisPoint.POINTPRIMITIVE,bias#)
;       Move the point closer to or further from the camera. Helps in 
;       z-buffering fighting issues. Default = 0.25
; ------------------------------------------------------------------------
; ColorPoint(thisPoint.POINTPRIMITIVE,red,green,blue)
; 		Be artistic! Color your point your favorite color! :P
; ------------------------------------------------------------------------
; DeletePoint(thisPoint.POINTPRIMITIVE)
;		Delete Point.
; ------------------------------------------------------------------------
; UpdatePoints(mycamera)
; 		Need this in the main loop! Pass your camera's handle in the parameter!
; ------------------------------------------------------------------------


; NOTE: xCameraControls is there just for example sakes...

Graphics3D 800, 600;, 0, 2
SetBuffer BackBuffer(  )
Global grwidth = GraphicsWidth(), grheight=GraphicsHeight()
Global grhalfwidth = grwidth/2, grhalfheight = grheight/2

;camera
camzoom# = 1
camera=CreateCamera()
CameraRange camera,0.1,1000
CameraClsColor camera,40,100,60	
PositionEntity camera, 0, 10, -25
chasecam=CreateCamera(camera)
PositionEntity chasecam,0,0,-5
;RotateEntity overcam,90,0,0
CameraViewport chasecam,600,0,200,200
CameraClsColor chasecam,30,80,50
HideEntity chasecam

;light
Global light
light=CreateLight()
LightColor light,32,32,32
TurnEntity light,45,45,0

; various camera control variable helpers
Global dest_cam_yaw#
Global dest_cam_pitch#
Global mfb=0

; the point primitive structure 
Type POINTPRIMITIVE
	Field Px#
	Field Py#
	Field Pz#
	Field size
	Field bias#
	Field red
	Field green
	Field blue
	Field vi0
	Field vi1
	Field vi2
	Field vi3
	Field Visible
	Field Deleted
End Type

; Single Surface Partical System For Point Primitives
; - All points use the same surface of the mesh, but they
; - own their own vertices each...

Global PointQuads=CreateMesh()
Global PQsSurf=CreateSurface(PointQuads)
EntityFX PointQuads,3 ; Points are vertex colored and fullbright

; !!!!!!!!!!!!!!!!!
; change 'number' to make more or less points
; !!!!!!!!!!!!!!!!!

pointsize=2
number=25
For i=-number To number
	For j=-number To number
		thisPoint.POINTPRIMITIVE=AddPoint(i,0,j, pointsize)
		ColorPoint(thisPoint,Rnd(127)+128,Rnd(127)+128,Rnd(127)+128)
	Next
Next

; ------------
; MAIN Example
; ------------
newsize=pointsize
Ftime=MilliSecs()+1000
Repeat
	If KeyHit(57) Then chaseview=Not chaseview ; spacebar
	If chaseview Then ShowEntity chasecam Else HideEntity chasecam
	If KeyDown(201) Then camzoom=camzoom+0.1 ; Pageup
	If camzoom>10 Then camzoom=10
	If KeyDown(209) Then camzoom=camzoom-0.1 ; Pagedown
	If camzoom<0.1 Then camzoom=0.1
	CameraZoom camera,camzoom

	If KeyHit(78) Then newsize=pointsize+1 ; numpad +
	If KeyHit(74) Then newsize=pointsize-1 ; numpad -
	If newsize>50 Then newsize=50
	If newsize<1 Then newsize=1
	If newsize<>pointsize Then pointsize=newsize : pointsize(Null,pointsize)
	

	xCameraControls(camera)
	time=MilliSecs()
	UpdatePoints(camera,camzoom)
	time=MilliSecs()-time
	RenderWorld

	Text 0,0,TrisRendered()+" "+time+" "+fps
	frame=frame+1 : If MilliSecs()>=ftime Then fps=frame : frame=0 : Ftime=ftime+1000
	Flip
Until KeyHit(1) = True

Function AddPoint.POINTPRIMITIVE(px#,py#,pz#, pointsize=2)
	Local newpoint.POINTPRIMITIVE = Last POINTPRIMITIVE
	; deleted points are stuck at the end of the list, so
	If newpoint<>Null
		If newpoint\deleted ; if last entry was deleted then reuse it
			newpoint\deleted=False 
			Insert newpoint Before First POINTPRIMITIVE
		Else
			newpoint = Null ; no deleted points to reuse
		EndIf
	EndIf
	; there is no deleted point to reuse so make a new one
	If newpoint=Null 
		newpoint.POINTPRIMITIVE = New POINTPRIMITIVE
		Insert newpoint Before First POINTPRIMITIVE
		newpoint\vi0=AddVertex(PQsSurf, 1000000, 1000000, 1000000);,0,0 ; 0 left top
		newpoint\vi1=AddVertex(PQsSurf, 1000000, 1000000, 1000000);,1,0 ; 1 right top
		newpoint\vi2=AddVertex(PQsSurf, 1000000, 1000000, 1000000);,0,1 ; 2 left bottom
		newpoint\vi3=AddVertex(PQsSurf, 1000000, 1000000, 1000000);,1,1 ; 3 right bottom
		AddTriangle PQsSurf,newpoint\vi2,newpoint\vi0,newpoint\vi1 ; and 2 triangles...
		AddTriangle PQsSurf,newpoint\vi2,newpoint\vi1,newpoint\vi3
	EndIf
	PositionPoint(newpoint,px,py,pz)
	ColorPoint(newpoint,255,0,0)
	Pointsize(newpoint,pointsize)
	PointBias(newpoint,0.25)
	Return newpoint	
End Function


Function PositionPoint(thisPoint.POINTPRIMITIVE,px#,py#,pz#)
	thisPoint\px = px : thispoint\py = py : thispoint\pz = pz
End Function

Function PointSize(thisPoint.POINTPRIMITIVE,size)
	If thispoint=Null Then
		For thispoint = Each POINTPRIMITIVE 
			thispoint\size=size
		Next
	Else
		thisPoint\size=size
	EndIf
End Function

Function PointBias(thisPoint.POINTPRIMITIVE,bias#)
	thisPoint\bias#=bias#
End Function

Function ColorPoint(thisPoint.POINTPRIMITIVE,red,green,blue)
	thisPoint\red=red
	thisPoint\green=green
	thisPoint\blue=blue
	VertexColor PQsSurf,thisPoint\vi0,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi1,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi2,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi3,thisPoint\red,thisPoint\green,thisPoint\blue
End Function

Function DeletePoint(thisPoint.POINTPRIMITIVE)
	VertexCoords PQsSurf,thisPoint\vi0,1000000,1000000,1000000
	VertexCoords PQsSurf,thisPoint\vi1,1000000,1000000,1000000
	VertexCoords PQsSurf,thisPoint\vi2,1000000,1000000,1000000
	VertexCoords PQsSurf,thisPoint\vi3,1000000,1000000,1000000
	thisPoint\deleted=True
	Insert thisPoint After Last POINTPRIMITIVE
End Function

Function UpdatePoints(mycamera,zoom#)
	Local TLX#, TLY#, TLZ#, TRX#, TRY#, TRZ#, BLX#, BLY#, BLZ#, BRX#, BRY#, BRZ#
	Local Zdist#, ProjX#, ProjY#

	; loop through and update points...
	For thisPoint.POINTPRIMITIVE = Each POINTPRIMITIVE
		If thispoint\deleted Then Exit 	; All deleted points are put at end of type list
		; Project point position to screen
		CameraProject mycamera,thispoint\px,thispoint\py,thispoint\pz
		projx = ProjectedX() : projy = ProjectedY() : onscreen = False

		If ProjectedZ()>0 	; if in front of camera then check if on screen
			If projx+thispoint\size>=0 And projx-thispoint\size<=grwidth 
				If projy+thispoint\size>=0 And projy-thispoint\size<=grheight
					thispoint\visible=True : onscreen = True
					; Get distance of point from the camera viewplane
					TFormPoint thispoint\px,thispoint\py,thispoint\pz, 0,mycamera
					zdist = TFormedZ()-thispoint\bias
					; project our square back into worldspace
					Reverseproject(mycamera,projx-thispoint\size,projy-thispoint\size,Zdist,zoom)
						TLX = TFormedX()
						TLY = TFormedY()
						TLZ = TFormedZ()
					Reverseproject(mycamera,projx+thisPoint\size,projy+thisPoint\size,Zdist,zoom)
						BRX = TFormedX()
						BRY = TFormedY()
						BRZ = TFormedZ()
					Reverseproject(mycamera,projx+thisPoint\size,projy-thisPoint\size,Zdist,zoom)
						TRX = TFormedX()
						TRY = TFormedY()
						TRZ = TFormedZ()
					Reverseproject(mycamera,projx-thisPoint\size,projy+thisPoint\size,Zdist,zoom)
						BLX = TFormedX()
						BLY = TFormedY()
						BLZ = TFormedZ()
					; Update the point in worldspace
					VertexCoords PQsSurf,thisPoint\vi0,TLX#,TLY#,TLZ#
					VertexCoords PQsSurf,thisPoint\vi1,TRX#,TRY#,TRZ#
					VertexCoords PQsSurf,thisPoint\vi2,BLX#,BLY#,BLZ#
					VertexCoords PQsSurf,thisPoint\vi3,BRX#,BRY#,BRZ#
				EndIf
			EndIf
		EndIf
		If Not onscreen	; if point not onscreen this frame...
			If thispoint\visible Then ; but it was onscreen last time hide it away and...
				thispoint\visible=False	; record that it is no longer visible
				VertexCoords PQsSurf,thisPoint\vi0,1000000,1000000,1000000
				VertexCoords PQsSurf,thisPoint\vi1,1000000,1000000,1000000
				VertexCoords PQsSurf,thisPoint\vi2,1000000,1000000,1000000
				VertexCoords PQsSurf,thisPoint\vi3,1000000,1000000,1000000
			EndIf						
		EndIf
	Next							
End Function

; Reverse project a point. Need to specify camzoom and Z
; will not deal with scaled cameras so don't!
Function Reverseproject(cam,sx#,sy#,z#,zoom#=1,dest=0)
	Local f#,x#,y#
	f# = Zoom * grhalfwidth
	x = ((sx-grhalfwidth)/f)  * z
	y = ((grhalfheight-sy)/f) * z
	TFormPoint x,y,z,cam,dest ; camera to dest (0 for world)
End Function


Function xCameraControls(mycamera)

	Local thisspeed#=0.25
	Local thisUnitSqr#=1.0

	If MouseDown(1)=0 And MouseDown(2)=0 Then mfb=0

		;zoom
	   	If KeyDown(208) Then MoveEntity mycamera,0,0,-thisspeed#
	   	If KeyDown(200) Then MoveEntity mycamera,0,0,thisspeed#
	
		;straff left/right
		If KeyDown(203) Then MoveEntity mycamera,-thisspeed#,0,0
		If KeyDown(205) Then MoveEntity mycamera,thisspeed#,0,0
   
		;elevate up/down
		If KeyDown(157) Then MoveEntity mycamera,0,-thisspeed#,0
		If KeyDown(54) Then MoveEntity mycamera,0,thisspeed#,0

		If MouseDown(2)=True And mfb=0
			mfb=1
			MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		EndIf

		If MouseDown(2)=False And mfb=1
			mfb=0
		EndIf	
	
		If mfb>0
			mxs#=MouseXSpeed()
			mys#=MouseYSpeed()
	
			dest_cam_yaw#=dest_cam_yaw#-mxs#
			dest_cam_pitch#=dest_cam_pitch#+mys#
			RotateEntity mycamera,dest_cam_pitch#,dest_cam_yaw#,0
			MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		EndIf
	
End Function

