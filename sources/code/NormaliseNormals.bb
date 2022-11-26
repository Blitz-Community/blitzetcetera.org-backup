Function NormaliseNormals(mesh)

	Local s
	
	For s=1 To CountSurfaces(mesh)
	
		surf=GetSurface(mesh,s)
	
		For v=0 To CountVertices(surf)-1
	
			nx#=VertexNX#(surf,v)
			ny#=VertexNY#(surf,v)
			nz#=VertexNZ#(surf,v)
			
			uv#=Sqr(nx#^2+ny#^2+nz#^2)
	
			nx#=nx#/uv#
			ny#=ny#/uv#
			nz#=nz#/uv#
		
			VertexNormal surf,v,nx#,ny#,nz#
	
		Next
	
	Next

End Function
