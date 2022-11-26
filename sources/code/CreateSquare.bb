Function CreateSquare(x,side,Central=True)
;X is width and depth of square
;Side is # of vertices per side eg 7 means total of 49 verts
;central is if you want handle in the centre of mesh

     mesh = CreateMesh()
     surface = CreateSurface(mesh)

     ;Position Vertices
     For xpoint=0 To side-1
          For zpoint=0 To side-1
               AddVertex (surface,xpoint,0,zpoint)
          Next
     Next

     ;Add Triangles
     For tri = 0 To (side^2)-side-1
          If tri Mod side<>side-1
               AddTriangle (surface,tri,tri+1,tri+side)
               AddTriangle (surface,tri+1,tri+side+1,tri+side)
          EndIf
     Next

     ScaleMesh mesh,(x/MeshWidth(mesh)),0,(x/MeshWidth(mesh))

     ;Set UV coords
     For index=0 To CountVertices(surface)-1
          VertexTexCoords surface,index,(VertexX(surface,index)/(x)),(x)-(VertexZ(surface,index)/(x))
     Next

     ;centre handle
     If central=True Then PositionMesh mesh,-x/2,0,-x/2
     
     Return mesh

End Function