Graphics3D 800,600,32,1
SetBuffer BackBuffer()

Global fntArial,fntArialB,fntArialI
;Load fonts to a file handle variables
fntArial=LoadFont("Arial",54,True,True,False)
fntArialB=LoadFont("Arial",34,True,True,False)
fntArialI=LoadFont("Arial",34,True,False,False)


Repeat

Cls 

mx=MouseX() 
my=MouseY() 

SetFont fntArial
Color 0,0,255
	Text 300,27,"3D Action"

SetFont fntArialB
Color 255,0,0
	Text 330,207,"New game"
	
	SetFont fntArialB
Color 255,0,0
	Text 370,257,"Exit"

SetFont fntArialI
Color 0,255,0
	Text mx,my,"[+]"
	
If  MouseX()>290 And MouseX()<490 And MouseY()>190 And MouseY()<230
SetFont fntArialB
Color 246,255,0
	Text 330,207,"New game"

EndIf 

If  MouseX()>290 And MouseX()<490 And MouseY()>190 And MouseY()<230 And MouseDown(1)

Goto game
EndIf 

If  MouseX()>320 And MouseX()<430 And MouseY()>240 And MouseY()<280

SetFont fntArialB
Color 246,255,0
	Text 370,257,"Exit"
	
EndIf 

If  MouseX()>320 And MouseX()<430 And MouseY()>240 And MouseY()<280 And MouseDown(1)
End
EndIf



UpdateWorld
RenderWorld
Flip
Until KeyDown(1) 
End 

..game

Graphics3D 640,480,32,1

SetBuffer BackBuffer()

Global fntArialr
;Load fonts to a file handle variables
fntArialr=LoadFont("Arial",24,False,False,False)


Type Dude
	Field entity,speed#
End Type

Type Dudet
	Field entity2
	End Type

 Const TYPE_PLAYER=1,Typelevel=2,enti=3,botar=4,enti2=5
Collisions TYPE_PLAYER,TYPE_PLAYER,1,1

Healthb=100
Healthig=100

SchetB=0
SchetP=0
 campiv = CreatePivot() 
 
cam = CreateCamera(campiv)
CameraRange cam, 0.1,200000
EntityType campiv,TYPE_PLAYER

lit=CreateLight()

PositionEntity lit,0,20,0
RotateEntity lit,10,90,0

gun1=CreateCube()

ScaleEntity gun1,0.04,0.04,0.1
 PointEntity gun1,campiv
EntityParent gun1,campiv
EntityParent gun1,cam 
PositionEntity gun1,0,-0.15,+0.3
EntityColor gun1,99,99,103

gunmu=CreateCube(gun1)
ScaleEntity gunmu,0.1,0.7,0.7
 PointEntity gunmu,campiv
EntityParent gunmu,campiv
EntityParent gunmu,cam 
PositionEntity gunmu,0,-0.15,+0.40

bot=CreateCube()
ScaleEntity bot,0.5,0.8,0.5
PositionEntity bot,0,0,20
EntityColor bot,0,160,252
EntityType bot,botar

gol=CreateSphere(8,bot)
ScaleEntity gol,0.7,0.5,0.7
PositionEntity gol,0,1.5,0
EntityColor gol,231,109,0
EntityType gol,botar

pln=CreatePlane()

EntityColor pln,205,88,249

PositionEntity pln,0,-1,0


 cub1=CreateCube()
EntityColor cub1,42,255,0
PositionEntity cub1,-3,0,0
ScaleEntity cub1,0.3,4,20
EntityType cub1,Typelevel

 cub2=CreateCube()
EntityColor cub2,42,255,0
PositionEntity cub2,3,0,0
ScaleEntity cub2,0.3,4,20
EntityType cub2,Typelevel

 cub3=CreateCube()
EntityColor cub3,255,0,0
PositionEntity cub3,8,0,20
ScaleEntity cub3,5,4,0.3
EntityType cub3,Typelevel

 cub4=CreateCube()
EntityColor cub4,255,0,0
PositionEntity cub4,-8,0,20
ScaleEntity cub4,5,4,0.3
EntityType cub4,Typelevel

cub2=CreateCube()
EntityColor cub2,255,0,0
PositionEntity cub2,20,0,0
ScaleEntity cub2,0.3,4,40
EntityType cub2,Typelevel


cub2=CreateCube()
EntityColor cub2,255,0,0
PositionEntity cub2,-20,0,0
ScaleEntity cub2,1,4,40
EntityType cub2,Typelevel

pot=CreateCube()
ScaleEntity pot,100,0.01,100

EntityColor pot,255,255,255
PositionEntity pot,0,4,0

Collisions TYPE_PLAYER,typelevel,2,2
 
Collisions botar,typelevel,2,2


Repeat

For d.Dude=Each Dude
		
		MoveEntity d\entity,0,0,1

If EntityDistance(gun1,d\entity)>200
FreeEntity d\entity
	Delete d
	
					EndIf


	Next


If bull<MilliSecs()        

		bul=bul+1				
			If bul=25 Then bul=0  
		bull=MilliSecs()+1
	EndIf
	
	
	
For f.Dudet=Each Dudet
		
		MoveEntity f\entity2,0,0,1

If EntityDistance(bot,f\entity2)>200
FreeEntity f\entity2
	Delete f
	
					EndIf
			
	Next


If bull2<MilliSecs()        

		bul2=bul2+1				
			If bul2=25 Then bul2=0  
		bull2=MilliSecs()+1
	EndIf

If EntityCollided (bot,Typelevel)

AlignToVector bot,EntityX(bot)-EntityX(campiv),0,EntityZ(bot)+EntityZ(campiv),0,0.6
EndIf 




TurnEntity bot,0,Sgn(DeltaYaw(bot,campiv))*2.3,0
MoveEntity bot,0,0,0.05
 If KeyDown(17)  Then 
TFormVector 0,0,0.2,cam,campiv 
MoveEntity campiv,TFormedX(),0,TFormedZ() 
EndIf 

If KeyDown(31)  Then
TFormVector 0,0,-0.2,cam,campiv 
MoveEntity campiv,TFormedX(),0,TFormedZ() 
EndIf 

If KeyDown(30)  Then 
MoveEntity  campiv,-0.2,0,0
EndIf 

If KeyDown(32)  Then 
MoveEntity  campiv,0.2,0,0
EndIf 

If KeyDown(57)
CameraZoom cam,2
Else
CameraZoom cam,1
EndIf 

If MouseDown(1)  


For k=1 To 1
sphere=CreateSphere()
ScaleEntity sphere,0.1,0.1,0.1
RotateEntity sphere,10,0,20
If bul=0
d.Dude=New Dude
	d\entity=CopyEntity( sphere )
	EntityColor d\entity,Rnd(255),Rnd(255),Rnd(255)
	
		
	ResetEntity d\entity
	
PositionEntity d\entity,EntityX#(campiv,1),EntityY#(campiv)-0.3,EntityZ#(campiv,1) 
RotateEntity d\entity,EntityPitch#(cam,1),EntityYaw#(campiv),0 
EntityType d\entity,enti
Collisions enti,Typelevel,2,3
Collisions enti,botar,2,2

EndIf 


Next

If EntityCollided (bot,enti)
Healthb=Healthb-1
EndIf

FreeEntity sphere
EndIf


If EntityInView(bot,cam)
For g=1 To 1
sphere2=CreateSphere()
ScaleEntity sphere2,0.1,0.1,0.1
RotateEntity sphere2,10,0,20
If bul2=0
f.Dudet=New Dudet
	f\entity2=CopyEntity( sphere2 )
	EntityColor f\entity2,Rnd(255),Rnd(255),Rnd(255)
		ResetEntity f\entity2
		
	PositionEntity f\entity2,EntityX#(bot),EntityY#(bot)-0.3,EntityZ(bot) 
RotateEntity f\entity2,EntityPitch#(bot,1),EntityYaw#(bot),0 
EntityType f\entity2,enti2
Collisions enti2,Typelevel,2,3
Collisions enti2,TYPE_PLAYER,1,2
EndIf 
Next


If EntityCollided (campiv,enti2)
Healthig=Healthig-1
EndIf



FreeEntity sphere2

EndIf


mys = MouseYSpeed() 
If Abs(EntityPitch(cam)+mys) < 75
TurnEntity cam, mys,0,0 
EndIf 
TurnEntity campiv,0,-MouseXSpeed(),0 
If EntityCollided (campiv,tulevel) 
EndIf 


MoveMouse 320,240
 
If Healthb<1
PositionEntity campiv,0,0,0
PositionEntity bot,0,0,20
Healthb=100
Healthig=100
SchetB=SchetB+1
EndIf

If Healthig<1
PositionEntity campiv,0,0,0
PositionEntity bot,0,0,20
Healthb=100
Healthig=100
SchetP=SchetP+1
EndIf

 UpdateWorld

 RenderWorld

 SetFont fntArialr
	Text 70,27,"Health"
	 SetFont fntArialr
	Text 70,67,"Health-Bot="+Healthb
	Text 70,97,"Health-Player="+Healthig

	
	
	SetFont fntArialr
	Text 500,27,"Account"
	
SetFont fntArialr
	Text 500,67,"Bot="+SchetP

SetFont fntArialr
	Text 500,97,"Player="+SchetB

 Flip

Until KeyHit(1)


End

