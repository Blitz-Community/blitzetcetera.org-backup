Graphics3D 640,480,16,2 
 
Const UPS=60 
 
Const ParazitMax=50 
Dim Parazit(ParazitMax) 
Dim ParazitSost(ParazitMax) 
Global S 
Const Type_Parazit=1,Type_Target=2 
 
Collisions Type_Parazit,Type_Target,2,3 
CreateDemo() 
 
cam=CreateCamera() 
CameraClsColor cam,56,203,31 
PositionEntity cam,0,30,-30 
RotateEntity cam,45,0,0 
l=CreateLight() 
RotateEntity l,60,30,0 
period=1000/UPS 
time=MilliSecs()-period 
 
Repeat 
      Repeat 
            elapsed=MilliSecs()-time 
      Until elapsed 
      ticks=elapsed/period 
      tween#=Float(elapsed Mod period)/Float(period) 
       
      For k=1 To ticks 
            time=time+period       
            If KeyHit(1) End 
 
                                                UpdateParazit() 
             
            UpdateWorld       
      Next 
       
      RenderWorld tween 
      Flip 
 
Forever 
 
 
Function CreateDemo() 
S=CreateSphere (16) 
      ScaleEntity S,10,10,10 
      EntityColor S,253,173,94 
      EntityType S,Type_Target 
 
Parazit(1)=CreateCube() 
      PositionEntity Parazit(1),0,12,0 
      ScaleEntity Parazit(1),0.3,0.2,0.4 
      EntityColor Parazit(1),128,0,64 
      EntityType Parazit(1),Type_Parazit 
      EntityRadius Parazit(1),0.2 
 
For i=2 To ParazitMax 
      Parazit(i)=CopyEntity (Parazit(1)) 
      PositionEntity Parazit(i),0,12,0 
      ScaleEntity Parazit(1),0.3,0.2,0.4 
      EntityColor Parazit(i),128,0,64 
      EntityType Parazit(i),Type_Parazit 
      EntityRadius Parazit(i),0.2 
Next  
 
End Function 
 
 
Function UpdateParazit() 
For i=1 To ParazitMax 
 
ParazitSost(i)=ParazitSost(i)-1 
If ParazitSost(i)<=0 Then 
TurnEntity Parazit(i),0,Rnd(-180,180),0 
ParazitSost(i)=Rnd(10,100) 
End If  
 
 
MoveEntity  Parazit(i),0,-1,0.1 
If EntityCollided( Parazit(i),Type_Target ) 
                  For k=1 To CountCollisions(  Parazit(i) ) 
                        If GetEntityType( CollisionEntity(  Parazit(i),k ) )=Type_Target 
                        tri=CollisionTriangle ( Parazit(i),k) 
                        ;DeformTarget(k) 
 
                        e#=-0.001 
 
 
                   
                              nx#=CollisionNX(  Parazit(i),k ) 
                              ny#=CollisionNY( Parazit(i),k ) 
                              nz#=CollisionNZ(  Parazit(i),k ) 
                              ;th.Hole=New Hole 
                              ;th\alpha=1 
                              ;th\sprite=CopyEntity( hole_sprite ) 
                              ;PositionEntity th\sprite,cx,cy,cz 
                              AlignToVector  Parazit(i),nx,ny,nz,2 
 
                              tri=CollisionTriangle ( Parazit(i),k) 
surf=CollisionSurface ( Parazit(i),k) 
                  vert=TriangleVertex ( Surf,tri,0 ) 
                  ;VertexCoords surf,vert,0,2,0 
                  VertexCoords surf,vert,VertexX(surf,vert)+(VertexNX(surf,vert)*e),VertexY(surf,vert)+ (VertexNY(surf,vert)*e),VertexZ(surf,vert)+(VertexNZ(surf,vert)*e) 
                  vert=TriangleVertex ( Surf,tri,1 ) 
                  VertexCoords surf,vert,VertexX(surf,vert)+(VertexNX(surf,vert)*e),VertexY(surf,vert)+ (VertexNY(surf,vert)*e),VertexZ(surf,vert)+(VertexNZ(surf,vert)*e) 
                  vert=TriangleVertex ( Surf,tri,2 ) 
                  VertexCoords surf,vert,VertexX(surf,vert)+(VertexNX(surf,vert)*e),VertexY(surf,vert)+ (VertexNY(surf,vert)*e),VertexZ(surf,vert)+(VertexNZ(surf,vert)*e) 
                               
                              Exit 
                        EndIf 
                  Next 
                   
            EndIf 
 
Next  
 
End Function