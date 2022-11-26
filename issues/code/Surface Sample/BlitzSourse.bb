;andreyman.ucoz.ru
;(c)ANDREYman


Graphics3D 640,480,32,2

SetBuffer BackBuffer()


camera=CreateCamera()
PositionEntity camera,0,10,-30

light=CreateLight(1) 
RotateEntity light,50,80,0

Entity1=CreateCube();
PositionEntity Entity1,15,10,0
ScaleEntity Entity1,5,5,5
 
SurfaceSrc = GetSurface(Entity1, 1)
Vcount = CountVertices(SurfaceSrc)

Entity2=CopyMesh(Entity1)
EntityColor Entity2,255,0,0
PositionEntity Entity2,-30,0,0

SurfaceDest = GetSurface(Entity2, 1)

While Not KeyHit(1)

 TurnEntity Entity1, 1, 1, 0

RenderWorld()


If KeyDown(57)
Text 5,5, "Don't ransformate"
Else
Text 5,5, "Transformating SurfaceSrc(Red color)"
TransformSurfaceToMatrix SurfaceSrc, SurfaceDest, Entity1
;ATransformSurfaceToMatrix SurfaceSrc, SurfaceDest, 0, Vcount, Pivot
EndIf

Flip 1
Wend
End


