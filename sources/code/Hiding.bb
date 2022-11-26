Graphics3D 800,600,16,2 
SetBuffer BackBuffer() 
 
cam=CreateCamera() 
 
MoveEntity cam,0,5,-5 
RotateEntity cam,45,0,0 
light=CreateLight() 
 
Global obj1=CreateCylinder() 
EntityColor obj1,255,255,0 
 
bot=CreateSphere() 
MoveEntity bot,-3,0,0 
EntityColor bot,255,0,0 
 
player=CreateSphere() 
MoveEntity player,3,0,0 
EntityColor player,0,255,0 
 
While Not KeyHit(1) 
 
PositionEntity bot,EntityX(obj1,1),EntityY(obj1,1),EntityZ(obj1,1) 
PointEntity bot,player 
TurnEntity bot,0,180,0 
MoveEntity bot,0,0,3 
 
If KeyDown(203) Then MoveEntity player,-0.1,0,0 
If KeyDown(205) Then MoveEntity player,0.1,0,0 
If KeyDown(200) Then MoveEntity player,0,0,0.1 
If KeyDown(208) Then MoveEntity player,0,0,-0.1 
If MouseDown(1) Then MoveEntity player,0,0.1,0 
If MouseDown(2) Then MoveEntity player,0,-0.1,0 
 
 
If KeyDown(30) Then MoveEntity obj1,-0.1,0,0 
If KeyDown(32) Then MoveEntity obj1,0.1,0,0 
If KeyDown(17) Then MoveEntity obj1,0,0,0.1 
If KeyDown(31) Then MoveEntity obj1,0,0,-0.1 
 
UpdateWorld 
RenderWorld 
 
Flip 
Wend