Function PickedVertex()

	mesh = PickedEntity() 
	surf = PickedSurface()
	tri = PickedTriangle()

	px# = PickedX()
	py# = PickedY()
	pz# = PickedZ()
	
	v0 = TriangleVertex(surf,tri,0)
	v1 = TriangleVertex(surf,tri,1)
	v2 = TriangleVertex(surf,tri,2)
	
	TFormPoint VertexX(surf,v0),VertexY(surf,v0),VertexZ(surf,v0),mesh,0
	dx# = TFormedX - px
	dy# = TFormedY - py
	dz# = TFormedZ - pz
	v0d# = dx*dx + dy*dy + dz*dz

	TFormPoint VertexX(surf,v1),VertexY(surf,v1),VertexZ(surf,v1),mesh,0
	dx# = TFormedX - px
	dy# = TFormedY - py
	dz# = TFormedZ - pz
	v1d# = dx*dx + dy*dy + dz*dz

	TFormPoint VertexX(surf,v2),VertexY(surf,v2),VertexZ(surf,v2),mesh,0
	dx# = TFormedX - px
	dy# = TFormedY - py
	dz# = TFormedZ - pz
	v2d# = dx*dx + dy*dy + dz*dz
	
	If (v0d < v1d) And (v0d < v2d) Then Return v0
	If (v1d < v0d) And (v1d < v2d) Then Return v1

	Return v2

End Function