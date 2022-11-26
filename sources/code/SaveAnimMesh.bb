;-------------------------------------------------------------------------------------------------------
;												SaveAnimFile()
;-------------------------------------------------------------------------------------------------------
;updated version, includes parent structure
;
;save an animated mesh to the ".x" format
;the x format doesn't support vertex weight, only parenting objects to each other
;
;SYNTAX:
;
; SaveAnimFile mesh, filename$
;
;   mesh     - mesh handle
;   filename - name of file
;

;print debug output
Const   debug = False

Global	frameindex
Global 	meshindex
Global	animindex
Global	orgmesh

;save animated mesh
Function SaveAnimFile(mesh, filename$)
	
	;reset counters
	frameindex = 0
	meshindex = 0
	animindex = 0

	;open file for writing
	ff = WriteFile(filename)
	
	;write header data
	WriteLine ff, "xof 0302txt 0064"
	WriteLine ff, " " 
	WriteLine ff, "Header {"
	WriteLine ff, " 1;"
	WriteLine ff, " 0;"
	WriteLine ff, " 1;"
	WriteLine ff, "}"
	WriteLine ff, " "
		
	;save object "mesh" to file
	SaveAnimObj(ff, mesh)
	
	;save animation data to file
	SaveAnimAnim(ff, mesh)
	
	;close file
	CloseFile ff
	
End Function

;-------------------------------------------------------------------------------------------------------
;												SaveAnimObj()
;-------------------------------------------------------------------------------------------------------
;save object and children (recursive)
Function SaveAnimObj(ff, mesh)
	
	;generate name
	name$ = "Frame" + frameindex
	frameindex = frameindex + 1
	
	;start frame block
	WriteLine ff, "Frame frm_" + name$ + " {"
	WriteLine ff, " "
	WriteLine ff, "  FrameTransformMatrix { "
	WriteLine ff, "    1.000000,0.000000,0.000000,0.000000,"
	WriteLine ff, "    0.000000,1.000000,0.000000,0.000000,"
	WriteLine ff, "    0.000000,0.000000,1.000000,0.000000,"
	WriteLine ff, "    0.000000,0.000000,0.000000,1.000000;"
	WriteLine ff, "  }"
	WriteLine ff, " "
	
	;save children
	For i = 1 To CountChildren(mesh)
		
		g = GetChild(mesh, i)
		SaveAnimObj ff, g
		
	Next

	;if entity is a mesh, then save the mesh data	
	If EntityClass$(mesh) = "Mesh" Then SaveAnimMesh(ff, mesh)
	
	;end frame block	
	WriteLine ff, "}"
	WriteLine ff, " "
		
End Function


;-------------------------------------------------------------------------------------------------------
;											SaveAnimMesh()
;-------------------------------------------------------------------------------------------------------
;save mesh data to file
Function SaveAnimMesh(ff, mesh)

	;generate name
	name$ = "Mesh" + meshindex
	meshindex = meshindex + 1
	
	;debug	
	If debug Then Print "Mesh->Frame:" + name$

	;loop through all surfaces
	For surfindex = 1 To CountSurfaces(mesh)
	
		;generate name
		objectname$ = name$ + "_surface" + surfindex

		;debug
		If debug Then Print "Surface->Mesh:" + objectname$

		;get surface
		surf 		= GetSurface(mesh, surfindex)
		numVert  	= CountVertices(surf)
		numTris 	= CountTriangles(surf)
		
		;get texname
		brush 		= GetSurfaceBrush(surf)
		tex			= GetBrushTexture(brush)
		texname$	= TextureName$(tex)
		If Instr(texname$, "\") > 0 Then
			test = 1
			For i = Len(texname$) To 1 Step -1
				 If Mid$(texname$, i, 1) = "\" Then test = i: Exit
			Next
			texname$ = Mid$(texname$, test + 1, Len(texname$))
		End If
		FreeTexture tex
		FreeBrush brush
		
		WriteLine 	ff, " Mesh " + objectname$ + " { "
			
		;write vertices
		WriteLine 	ff, " " + numVert + ";"
		
		For i = 0 To numVert - 1
			xx# = VertexX(surf, i)
			yy# = VertexY(surf, i)
			zz# = VertexZ(surf, i)
			WriteLine ff, xx# + ";" + yy# + ";" + zz# + ";,"
		Next
	
		;write triangles	
		WriteLine ff, " " + numTris + ";"
		
		For i = 0 To numTris - 1
			aa = TriangleVertex(surf, i, 0)
			bb = TriangleVertex(surf, i, 1)
			cc = TriangleVertex(surf, i, 2)
			If i = numTris - 1 Then st$ = ";" Else st$ = ","
			WriteLine ff, "3;" + aa + "," + bb + "," + cc + ";" + st$
		Next
	
		;write material properties	
		WriteLine ff, " "
		WriteLine ff, "MeshMaterialList {"
		WriteLine ff, "1;"
		WriteLine ff, "1;"
		WriteLine ff, "0;;"
		WriteLine ff, " "
			
		WriteLine ff, "Material {"
		WriteLine ff, " 1.000000,1.000000,1.000000,1.000000;;"
		WriteLine ff, " 1.000000;"
		WriteLine ff, " 0.500000,0.500000,0.500000;;"
		WriteLine ff, " 0.000000,0.000000,0.000000;;"
		
		;write texture file name	
		If texname$ <> "" Then
			WriteLine ff, "      TextureFilename {"
			WriteLine ff, "        " + Chr$(34) + texname$ + Chr$(34) + "; }"
			If debug Then Print "texture:" + texname$
		End If
		
		;close material block		
		WriteLine ff, "}"
		WriteLine ff, "}"
		WriteLine ff, " "
	
		;write normals	
		WriteLine ff, "MeshNormals {"	
		WriteLine ff, " " + numVert + ";"
		
		;write normal vertices
		For i = 0 To numVert - 1
			xx# 		= VertexNX(surf, i)
			yy# 		= VertexNY(surf, i)
			zz# 		= VertexNZ(surf, i)
			WriteLine 	ff, xx# + ";" + yy# + ";" + zz# + ";,"
		Next
		
		;write normal triangles
		WriteLine ff, " " + numTris + ";"
		For i = 0 To numTris - 1
			aa 			= TriangleVertex(surf, i, 0)
			bb 			= TriangleVertex(surf, i, 1)
			cc 			= TriangleVertex(surf, i, 2)
			If ( i = numTris - 1 ) Then st$ = ";" Else st$ = ","
			WriteLine 	ff, "3;" + aa + "," + bb + "," + cc + ";" + st$
		Next
	
		;close normals block	
		WriteLine ff, 	"}"
		WriteLine ff, 	" "
		
		;write texture coordinates U,V
		WriteLine ff, 	"MeshTextureCoords {"	
		WriteLine ff, 	" " + numVert + ";"
		
		For i = 0 To numVert - 1
			If ( i = numVert - 1 ) Then st$ = ";" Else st$ = ","
			uu# 		= VertexU(surf, i)
			vv# 		= VertexV(surf, i)
			WriteLine 	ff, uu# + ";" + vv# + ";" + st$
		Next
	
		WriteLine ff, "  }"
	
		;close mesh block	
		WriteLine ff, " }"
		WriteLine ff, " "
		
	Next

End Function

;-------------------------------------------------------------------------------------------------------
;												SaveAnimAnim()
;-------------------------------------------------------------------------------------------------------
;save animation sequence data to file
Function SaveAnimAnim(ff, mesh)

	If AnimLength(mesh) < 0 Then Return
	
	;used for "Animate" command
	orgmesh = mesh
	
	;start animation sequence block
	WriteLine ff, "AnimationSet Sequence01 {"

	;save animation data	
	SaveAnimDat(ff, mesh, AnimLength(mesh) + 1)
		
	;close animation sequence block
	WriteLine ff, "}"
			
End Function
			
;-------------------------------------------------------------------------------------------------------
;												SaveAnimDat()
;-------------------------------------------------------------------------------------------------------
;save animation data
Function SaveAnimDat(ff, mesh, maxframe)
	
	animname$   = "ani_" + animindex
	objectname$ = "Frame" + animindex
	animindex = animindex + 1
	
	If debug Then Print "Animation" + animname$
	If debug Then Print "-->" + objectname$
	
	WriteLine ff, "Animation " + animname$ + " {"
	WriteLine ff, " "
	WriteLine ff, "{frm_" + objectname$ + "} "
	WriteLine ff, " "
				
		;write rotation data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "0;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, RotationToString(i, EntityPitch(mesh), EntityYaw(mesh), EntityRoll(mesh))
			Next	
		WriteLine ff, "}"
		WriteLine ff, " "
		
		;write scaling data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "1;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, ScalingToString(i, EntityWidth(mesh), EntityHeight(mesh), EntityDepth(mesh))
			Next
		WriteLine ff, "}"
		WriteLine ff, " "

		;write position data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "2;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, PositionToString(i, EntityX(mesh), EntityY(mesh), EntityZ(mesh))
			Next
		WriteLine ff, "}"
		WriteLine ff, " "
	
	;write animation options
	WriteLine ff, "AnimationOptions {"
	WriteLine ff , "1;"		;0 = closed (default)  1 = open
	WriteLine ff , "1; }"	;0 = splines  1 = linear
	
	;close animation block
	WriteLine ff, "}"
	
	;save children
	For i = 1 To CountChildren(mesh)
		
		g = GetChild(mesh, i)
		
		SaveAnimDat(ff, g, maxframe)
	
	Next			
	
End Function

;-----------------------------------------------------------------------------------------------------
; 										    RotationToString$()
;-----------------------------------------------------------------------------------------------------
;convert rotation information to string
Function RotationToString$(ptime, iPitch#, iYaw#, iRoll#)	

	;this code was written by LeadWerks
	;http://www.blitzbasic.com/Community/posts.php?topic=51579
	sp# = Sin(iYaw   / 2)
	cp# = Cos(iYaw   / 2)
	sy# = Sin(iRoll  / 2)
	cy# = Cos(iRoll  / 2)
	sr# = Sin(iPitch / 2)
	cr# = Cos(iPitch / 2)
	
	w# = + (cr * cp * cy - sr * sp * sy)
	x# = - (sr * cp * cy - cr * sp * sy)
	y# = + (cr * sp * cy + sr * cp * sy)
	z# = - (sr * sp * cy + cr * cp * sy)
	
	Return ptime + "; 4; " + w + "," + x + "," + y + "," + z + ";;;"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											 PositionToString$()
;-----------------------------------------------------------------------------------------------------
;convert position information to string
Function PositionToString$(ptime, iX#, iY#, iZ#)
	
	Return pTime + "; 3;" + iX + "," + iY + "," + iZ + ";;,"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											ScalingToString$()
;-----------------------------------------------------------------------------------------------------
;convert scaling information to string
Function ScalingToString$(pTime, iWidth#, iHeight#, iDepth#)
	
	Return pTime + "; 3;" + iWidth + "," + iHeight + "," + iDepth + ";;,"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityWidth()
;-----------------------------------------------------------------------------------------------------
;returns width of an entity
Function EntityWidth#( mesh )
	
	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshWidth(mesh) = 0 Then Return 1

	TFormPoint MeshWidth(mesh), 0, 0, mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0	
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshWidth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityHeight()
;-----------------------------------------------------------------------------------------------------
;returns height of an entity
Function EntityHeight#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshHeight(mesh) = 0 Then Return 1
	
	TFormPoint 0, MeshHeight(mesh), 0, mesh, 0
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshHeight(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityDepth()
;-----------------------------------------------------------------------------------------------------
;returns depth of an entity
Function EntityDepth#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshDepth(mesh) = 0 Then Return 1
	
	TFormPoint 0, 0, MeshDepth(mesh), mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz
	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshDepth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
		
End Function

;-----------------------------------------------------------------------------------------------------
;											iSetAnimTime()
;-----------------------------------------------------------------------------------------------------
;user defined SetAnimTime
Function iSetAnimTime( mesh, time# )
	If time >= AnimLength(mesh) Then time = AnimLength(mesh) - 0.01
	SetAnimTime mesh, time
	If debug Then If time Mod 10 = 0 Then Print "frame:" + time
End Function

;Create 3d animation example

;Set up a simple nice looking level
Graphics3D 640,480

camera=CreateCamera()
PositionEntity camera,0,14,-15
RotateEntity camera,35,0,0
light=CreateLight(2)
PositionEntity light,1000,1000,-1000
ground=CreatePlane(2)
EntityAlpha ground,0.5
EntityColor ground,0,0,255
mirror=CreateMirror()

;Lets make a bouncing ball that squashes on impact with the floor.
ball=CreateSphere(16)
EntityShininess ball,1
EntityColor ball,255,0,0

; Lets animate him and "record" the 3D animation for later playback
bloat#=0 : flatten#=0 : ypos#=10

For frame=1 To 10
;Drop the ball from height 10 to 2
ypos = ypos - spd#
spd#=spd#+.2
PositionEntity ball,0,ypos,0
ScaleEntity ball,1+bloat,1+flatten,1+bloat

;If the ball is low enough make it look increasingly squashed
If frame>8
bloat=bloat+1.5
flatten=flatten-.25
Else
flatten=flatten+.05
EndIf

;Record the frame!
SetAnimKey ball,frame
Next

;Now we need to add the frames we've just made to the sequence of "film"!
seq = AddAnimSeq(ball,frame-1) ; total number of frames

;Play it back ping-pong!
Animate ball,1

;save file
SaveAnimFile ball, "ball.x"

;free original
FreeEntity ball

;load file for testing
ball = LoadAnimMesh("ball.x")

DeleteFile "ball.x"

;Play it back ping-pong!
Animate ball,2,0.15
While Not KeyHit(1)
UpdateWorld
RenderWorld
Flip
Wend
End
