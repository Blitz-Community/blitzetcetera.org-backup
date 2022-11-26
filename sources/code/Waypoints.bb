Graphics3D 640,480,16,2


Dim waypoints(3)

For x = 1 To 3
waypoints(x) = CreateCube()
EntityAlpha waypoints(x),.4
Next

PositionEntity waypoints(1),0,0,0
PositionEntity waypoints(2),-5,0,10
PositionEntity waypoints(3),10,0,15

Type player
Field entity
Field currentpoint
End Type

p.player = New player
p\entity= CreateSphere()
p\currentpoint = 2

camera = CreateCamera()
MoveEntity camera,0,20,8
RotateEntity camera,90,0,0


While KeyHit(1) = False

Goto movestufferyea
.aftermovestufferyea

RenderWorld
Flip
Wend



.movestufferyea
               
               If EntityDistance(p\entity,waypoints(1)) < 2 Then p\currentpoint = 2

               If EntityDistance(p\entity,waypoints(2)) < 2 Then p\currentpoint = 3
               
               If EntityDistance(p\entity,waypoints(3)) < 2 Then p\currentpoint = 1
               
               dx#=EntityX(waypoints(p\currentpoint))-EntityX(p\entity)
               dy#=EntityY(waypoints(p\currentpoint))-EntityY(p\entity)
               dz#=EntityZ(waypoints(p\currentpoint))-EntityZ(p\entity)
               ln#=Sqr(dx^2+dy^2+dz^2)
               dx=dx/ln
               dy=dy/ln
               dz=dz/ln
               ddy#=EntityYaw(p\entity)
               AlignToVector p\entity,dx,dy,dz,y,.1
               dy2#=EntityYaw(p\entity)

               MoveEntity p\entity,0,0,.03
               
Goto aftermovestufferyea