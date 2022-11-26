Type platform 
 Field mdl 
 Field x#,y#,z# 
 Field br 
 Field vp 
End Type 
 
Function createplatform() ;функция создания платформы 
  p.platform=New platform 
  p\mdl=CreateCube() 
  p\x#=Rnd(-200,200) 
  p\y#=0 
  p\z#=Rnd(-300,300) 
  p\vp#=0.1 
  p\br=CreateBrush(Rnd(255),Rnd(255),Rnd(255)) 
  PositionEntity p\mdl,p\x#,p\y#,p\z# 
  ScaleEntity p\mdl,2,0.3,2 
  PaintEntity p\mdl,p\br 
End Function 
 
Function moveplatform(rnad#) ;функция двигает платформу вниз-вврех, от rnad# зависит скорость 
 For p.platform=Each platform 
   MoveEntity p\mdl,0,p\y#,0 
     py#=EntityY(p\mdl) 
 If py#<10 Then p\y#=(p\y#+0.01+Rnd(rnad#)) Else  
 If py#>=10 Then p\y#=(p\y#-0.01-Rnd(rnad#)) 
 Next 
End Function 
 
Graphics3D 1024,768,32,1 
SetBuffer BackBuffer() 
 
camera=CreateCamera() 
AmbientLight 255,255,255 
 
;создаем 1000 платформок :-) 
For x=1 To 1000 
 createplatform() 
Next 
 
While Not KeyDown(1) 
 
;двигаем все платформы 
moveplatform(0.005) 
 
;для удобного просмотра 
   If KeyDown(200) Then MoveEntity camera,0,0,+1 
   If KeyDown(208) Then MoveEntity camera,0,0,-1 
   If KeyDown(203) Then TurnEntity camera,0,+2,0  
   If KeyDown(205) Then TurnEntity camera,0,-2,0 
 
   mxs#=-.25*MouseXSpeed() 
   mys#=.25*MouseYSpeed() 
   MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 
   TurnEntity camera,0,mxs#,0,1 
   TurnEntity camera,mys#,0,0,0 
 
UpdateWorld() 
RenderWorld() 
Flip 0 
Wend 
End