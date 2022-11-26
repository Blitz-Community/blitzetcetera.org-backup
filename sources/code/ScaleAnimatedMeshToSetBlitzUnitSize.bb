Function loadscaledanimmesh(filename$, sx#,sy#,sz#) ;Size in blitz units not percent!!
	
	;By Dan @ D-Grafix
	;useage :  mesh=loadscaledanimmesh(file$,width,height,depth)
	
	;create scaler and mesure static mesh
	scaler=LoadMesh(filename)
	mesh_SX#		=MeshWidth(scaler)
	mesh_SY#	=MeshHeight(scaler)
	mesh_SZ#		=MeshDepth(scaler)

	;and bin it	
	FreeEntity scaler
	
	;load 'real' mesh
	mesh=LoadAnimMesh(filename)
	
	;do some maths
	Xscale# 	= ((100/mesh_SX)   * sx) / 100.0
	Yscale# 	= ((100/mesh_SY)  * sy) / 100.0
	Zscale# 	= ((100/mesh_SZ)  * sz) / 100.0

	;scale it
	ScaleEntity mesh,Xscale#,Yscale#,Zscale#

	;return it
	Return mesh
	
End Function
