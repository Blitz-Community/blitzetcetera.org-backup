 
Graphics3D 640,480  
SetBuffer BackBuffer()  
 
camera=CreateCamera() 
PositionEntity camera,0,10,0 
RotateEntity camera,30,0,0 
 
light=CreateLight()  
RotateEntity light,0,0,90  
 
cube_pol=CreateCube () 
PositionEntity cube_pol,0,-1.5,20 
ScaleEntity cube_pol,10,1,10 
EntityColor cube_pol,255,0,0 
EntityPickMode cube_pol,2 
 
For i=0 To 5 
   cube=CreateCube()  
   PositionEntity cube,5+1.5*i,i,20 
   ScaleEntity cube,1,0.5,10 
   EntityColor cube,10*i+50,100*i,100*i 
   EntityPickMode cube,2 
Next   
 
player = CreateSphere () 
PositionEntity player,0,1.5,15 
ScaleEntity player,2,2,2 
EntityColor player,0,255,0 
 
 
While Not KeyDown( 1 ) 
 
If KeyDown (200) MoveEntity player,0,0,0.1 
If KeyDown (208) MoveEntity player,0,0,-0.1 
If KeyDown (203) MoveEntity player,-0.1,0,0 
If KeyDown (205) MoveEntity player,0.1,0,0 
 
;-------------------------------идём по ступенькам--------------------------- 
height#=EntityY#(player)-PickedY#() 
If height#<1.9 MoveEntity player,0,0.1,0 
If height#>2.1 MoveEntity player,0,-0.1,0 
If LinePick (EntityX (player),EntityY (player),EntityZ (player),0,-10,0,1)=0 MoveEntity player,0,-0.5,0 
;----------------------------------------------------------------------- -------------- 
 
RenderWorld 
Flip 
Wend  
 
End