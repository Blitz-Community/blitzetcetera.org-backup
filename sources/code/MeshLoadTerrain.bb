Graphics3D 800,600
PositionEntity CreateCamera(),0,0,-75
RotateEntity CreateLight(),45,45,0
PositionEntity CreateCube(),0,0,0
t=LoadTerrain3("heightmap.jpg","mground.jpg","spotlight2.png")
RotateEntity t,-45,0,0
RenderWorld
Flip
FreeEntity t
WaitKey

Function LoadTerrain3(hmap$,tmap$=0,lmap$=0)

	; load the heightmap
	temp = LoadImage(hmap$)

	If temp = 0 Then RuntimeError "Heightmap image "+hmap$+" does not exist." : Return 0
	
	k#=128.0/ImageHeight(temp)
	If k#<1 Then ScaleImage temp,k#,k#
	
	; store heightmap dimensions
	x = ImageWidth(temp)
	y = ImageHeight(temp)

	; load texture and lightmaps
	tmap = LoadTexture(tmap$)
	lmap = LoadTexture(lmap$)
	
	; auto scale the textures to the right size
	If tmap ScaleTexture tmap,x,y
	If lmap	ScaleTexture lmap,x,y

	; start building the terrain
	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	; create some verts for the terrain
	For ly = 0 To y
		For lx = 0 To x
		    AddVertex surf,lx,0,ly,1.0/lx,1.0/ly
		Next
	Next
	;RenderWorld
	
	; connect the verts with faces
	For ly = 0 To y-1
		For lx = 0 To x-1
			AddTriangle surf,lx+((x+1)*ly),lx+((x+1)*ly)+(x+1),(lx+1)+((x+1)*ly)
			AddTriangle surf,(lx+1)+((x+1)*ly),lx+((x+1)*ly)+(x+1),(lx+1)+((x+1)*ly)+(x+1)
		Next
	Next
	
	; position the terrain to center 0,0,0
	PositionMesh mesh, -x/2.0,0,-y/2.0

	; alter vertice height to match the heightmap red channel
	SetBuffer ImageBuffer(temp)
	For lx = 0 To x
		For ly = 0 To y
			GetColor lx,y-ly
			index = lx + ((x+1)*ly)
			VertexCoords surf, index , VertexX(surf,index), ColorRed()/20.0,VertexZ(surf,index)
			; set the terrain texture coordinates
			VertexTexCoords surf,index,lx,-ly 
		Next
	Next
	SetBuffer BackBuffer()
	
	; update the terrain normals so lighting will look correct
	UpdateNormals mesh
	
	; apply texture map to index 0
	If tmap EntityTexture mesh,tmap,0,0
		
	; apply lightmap to index 1 with flag 2
	If lmap EntityTexture mesh,lmap,0,1 : TextureBlend lmap,2
	
	Return Mesh
End Function