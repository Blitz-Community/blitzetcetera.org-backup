Function createcircle(segs=8,parent=0,twosided=False)

  mesh=CreateMesh( parent ) 
  surf=CreateSurface( mesh ) 

  AddVertex surf,0,0,0,.5,.5
  
  vc=0
  inc#=360/segs
  ii#=0

  While ii<= 360

    vc = vc+1
    ii  = ii +inc
    xc# = Sin(ii)
    zc# = Cos(ii)
    u#=xc/2+.5
    v#=zc/2+.5
    AddVertex surf,xc,0,zc,u,v

    AddTriangle (surf,0,vc-1,vc)
    If twosided Then AddTriangle (surf,0,vc,vc-1)

  Wend 
  AddTriangle (surf,0,vc,1)
  If twosided Then AddTriangle (surf,0,vc-1,vc)
  UpdateNormals mesh

  Return mesh

End Function
