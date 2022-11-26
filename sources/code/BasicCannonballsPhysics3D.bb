Type cannonball
 Field x
 Field y
 Field cube
End Type 


Graphics3D 640,480,16,2
SetBuffer(BackBuffer())

cam=CreateCamera()
MoveEntity cam,35,10,-70

c.cannonball=New cannonball
c\cube=CreateSphere()


While True 
If KeyHit(57) c.cannonball=New cannonball:c\cube=CreateSphere():c\x=0:c\y=0 

For c.cannonball=Each cannonball
c\y=(-0.01347*c\x*c\x+0.9325*c\x+5.5)*2 
c\x=c\x+1 
PositionEntity c\cube,c\x,c\y,0  
Next 

RenderWorld 
Flip 
Wend 
