Global ZOMBI=1,DOM=2,SCENE=3,  PART=4, BLOOD=5 
Global anglex#=0, angley#=0;<-- Переменные для поврота камеры 
;Тип - живой человек 
Type TMan 
Field body, head, foot1, foot2, main1, main2, piv 
Field speed#, timer;timer - время, в теч. кот. чел не меняет направление 
End Type 
;Тип - оторванная голова 
Type TMem 
Field ent 
Field dy#, maxy#, speed#;dy-скорость по оси OY, maxy-макс. высота подъема 
End Type 
;Тип - труп без головы 
Type TTrup 
Field ent  
Field dy#, speed# 
End Type 
;Тип - кровь в воздухе(кровь на полу - это тектстура) 
Type TBlood 
Field s, speed#, dy# 
End Type 
;Пятна крови, которые не выведены на текстуру пола 
Dim pyatna_x(100) 
Dim pyatna_y(100) 
Global pyatna_count 
 
init() 
;Текстура пола 
Global tex=CreateTexture(1000, 1000):SetBuffer TextureBuffer(tex):ClsColor 100, 200, 100:Cls:SetBuffer BackBuffer() 
;Пол 
Global pol 
World() 
;Камера 
Global cam1_p=CreatePivot():PositionEntity cam1_p, 0, 7, -15:EntityType cam1_p, ZOMBI:EntityRadius cam1_p, 1.5, 7 
Global cam1=CreateCamera(cam1_p):CameraRange cam1, 1 ,10000 
;Свет 
light1=CreateLight() 
;!!!!!!!!!!!!Создание челов ??ПОСМОТРИТЕ ЭТУ ФУНКЦИЮ(кол-во челов почемуто на один меньше)?? 
CreateMans(5) 
 
Collisions ZOMBI, SCENE, 2, 2 
Collisions ZOMBI, DOM, 2, 2 
Collisions ZOMBI, ZOMBI, 1, 1 
Collisions BLOOD, SCENE, 2, 1 
Collisions BLOOD, DOM, 2, 1 
Collisions BLOOD, ZOMBI, 1, 2 
 
While Not KeyHit(1) 
 
mousecontrol() 
 
UpdateMans();<-- Передвижение челов 
UpdateMems();<-- Передвижение оторванных голов 
UpdateTrups();<-- Передвижение безголовых тел 
UpdateBlood();<-- Передвижение капелек крови в воздухе 
paintblood();<-- Прорисовка на текстуре пола упавших капель крови 
UpdateWorld() 
RenderWorld() 
;Курсор 
Color 255, 0, 255 
Text GraphicsWidth()/2, GraphicsHeight()/2, "*", 1, 1 
Flip() 
Wend 
 
End 
;=== Создание капли крови ====== 
Function CreateBloodEntity(x#, y#, z#) 
mesh = CreateMesh() 
surf = CreateSurface(mesh) 
v0=AddVertex (surf, -1,-1,0) 
v1=AddVertex (surf,  1,-1,0) 
v2=AddVertex (surf,  0, .2,0) 
AddTriangle (surf,v0,v2,v1) 
AddTriangle (surf,v1, v2, v0) 
br=CreateBrush(100, 0, 0) 
PaintMesh mesh, br 
UpdateNormals mesh 
EntityFX mesh, 1 
ScaleMesh mesh, .1, .1, .1 
EntityType mesh, BLOOD 
PositionEntity mesh, x, y, z 
Return mesh 
End Function 
;----Контроль мыши  
Function mousecontrol() 
If MouseX()<10 Or MouseX()>GraphicsWidth()-10 MoveMouse GraphicsWidth()/2, GraphicsHeight()/2 
If MouseY()<10 Or MouseY()>GraphicsHeight()-10 MoveMouse GraphicsWidth()/2, GraphicsHeight()/2 
anglex=anglex+MouseYSpeed()/10 
angley=angley-MouseXSpeed()/10 
RotateEntity cam1, anglex, 0, 0 
RotateEntity cam1_p, 0, angley, 0 
If KeyDown(200) MoveEntity cam1_p, 0, 0, 1.5 
If KeyDown(208) MoveEntity cam1_p, 0, 0, -1.5 
If MouseHit(1) CreateBullet() 
End Function 
;-- Инициализация  
Function init() 
Graphics3D 800, 600,  16, 1 
HidePointer():MoveMouse GraphicsWidth()/2, GraphicsHeight()/2 
fn=LoadFont("system", 1, 0, 0, 0):SetFont(fn) 
End Function 
;--- Создание пола и стен  
Function World() 
Local dombr=CreateBrush(150, 150, 150) 
pol=CreateCube() 
 ScaleEntity pol, 100, 1, 100 
 EntityTexture pol, tex 
 EntityType pol, SCENE 
d1=CreateCube() 
 ScaleMesh d1, -100, -200, -100 
 PaintMesh d1, dombr 
 EntityColor d1, 50, 50, 50 
 EntityType d1, DOM 
End Function 
;----?? Создание челов ?? 
Function CreateMans(count) 
For k = 1 To count 
s.TMan=New TMan 
;Создание пивота 
s\piv=CreatePivot() 
EntityRadius s\piv, 2, 7 
EntityType s\piv, ZOMBI 
;Создание частей тела 
s\body=CreateSphere(8, s\piv) 
ScaleMesh s\body, 1, 2, 1 
PositionMesh s\body, 0, -2.7, 0 
EntityType s\body, ZOMBI 
EntityPickMode s\body, 3 
EntityBox s\body, -1, -4, -1,3, 3.5, 3 
 
s\Head=CreateSphere(8, s\piv) 
ScaleMesh s\Head, 1, 1, 1 
EntityType s\head, ZOMBI 
EntityPickMode s\head, 1 
 
s\main1=CreateSphere(8, s\body) 
ScaleMesh s\main1, .5, 1.5, .5:TurnEntity s\main1, -25, 0, 10 
PositionMesh s\main1, .7, -2.7, 0 
 
s\main2=CreateSphere(8, s\body) 
ScaleMesh s\main2, .5, 1.5, .5:TurnEntity s\main2, 25, 0, -10 
PositionMesh s\main2, -.7, -2.7, 0 
 
s\foot1=CreateSphere(8, s\body) 
ScaleMesh s\foot1, .5, 1.5, .5:TurnEntity s\foot1, 10, 0, 10 
PositionMesh s\foot1, -.5, -5.7, 0 
 
s\foot2=CreateSphere(8, s\body) 
ScaleMesh s\foot2, .5, 1.5, .5:TurnEntity s\foot2, -10, 0, -10 
PositionMesh s\foot2, .5, -5.7, 0 
;Создание анимации 
For frame = 1 To 7 
TurnEntity s\main1, 5, 0, 0:SetAnimKey s\main1, frame 
TurnEntity s\main2, -5, 0, 0:SetAnimKey s\main2, frame 
TurnEntity s\foot1, -2, 0, 0:SetAnimKey s\foot1, frame 
TurnEntity s\foot2, 2, 0, 0:SetAnimKey s\foot2, frame 
Next 
r=Rnd(.4, .7) 
AddAnimSeq(s\main1,frame-1):Animate s\main1, 2, r 
AddAnimSeq(s\main2,frame-1):Animate s\main2, 2, r 
AddAnimSeq(s\foot1,frame-1):Animate s\foot1, 2, r 
AddAnimSeq(s\foot2,frame-1):Animate s\foot2, 2, r 
;Начальные значения 
s\timer=10:s\speed=.1+Rnd(0, .2) 
PositionEntity s\piv, k*10, 20, 10 
;Помещение чела в World 
UpdateWorld() 
Next 
End Function

;--Движение челов  
Function UpDateMans() 
For s.TMan=Each TMan 
If GetParent(s\body)<>s\piv 
If GetParent(s\head)=s\piv CreateMem(s\head) 
Animate s\main1, 0 
Animate s\main2, 0 
Animate s\foot1, 0 
Animate s\foot2, 0 
FreeEntity s\piv 
Delete s 
Exit 
EndIf 
s\timer=s\timer-1 
If s\timer<1 TurnEntity s\piv, 0, Rnd(-15, 15), 0:s\timer=5+Rnd(0, 5) 
If EntityCollided(s\piv, DOM) Or EntityCollided(s\piv, ZOMBI) TurnEntity s\piv, 0, Rnd(-60, 60), 0:s\timer=5+Rnd(0, 5) 
MoveEntity s\piv, 0, 0, s\speed 
If Not EntityCollided(s\piv, SCENE) TranslateEntity s\piv, 0, -1, 0 
Next 
End Function 
;-- Выстрел  
Function CreateBullet() 
pick=CameraPick(cam1, GraphicsWidth()/2, GraphicsHeight()/2) 
If pick>0  
 ;Создание капель крови 
 For k=1 To 30 
  s.TBlood = New TBlood 
  s\s=CreateBloodEntity(PickedX#(), PickedY#(), PickedZ#()) 
  s\speed=Rnd(0, 1):s\dy=0 
  PointEntity s\s, cam1:TurnEntity s\s, Rnd(-90, 90),Rnd(-90, 90),Rnd(-90, 90) 
  EntityParent s\s, 0 
 Next 
 ;Если нет "детей" - то создаем оторванную голову, иначе - труп 
 If CountChildren(pick)=0 
  CreateMem(pick)  
 Else  
  CreateTrup(pick) 
 End If 
End If 
End Function 
;--- Создание оторванной головы  
Function CreateMem(ent) 
PointEntity ent, cam1 
TurnEntity ent, 0, 180+Rnd(-10, 10), 0 
For p.TMem=Each TMem 
If p\ent=ent 
p\dy=1:p\maxy=1:p\speed=2 
Return 
End If 
Next 
p.TMem = New TMem 
p\ent=ent 
EntityParent p\ent, 0 
p\dy=1.5:p\maxy=1:p\speed=3 
End Function 
;---Передвижение оторванных голов 
Function UpDateMems() 
For p.TMem = Each TMem 
 TranslateEntity p\ent, 0, p\dy, 0 
 If Not EntityCollided(p\ent, SCENE)  
  p\dy=p\dy-.1 
 Else  
  p\maxy=3*p\maxy/4 
  p\dy=p\maxy 
  p\speed=p\speed-.4 
  If p\speed<0 p\speed=0 
 End If 
 If EntityCollided(p\ent, DOM) TurnEntity p\ent, 0, 90, 180 
 If p\speed>0 MoveEntity p\ent, 0, 0, p\speed 
Next 
End Function 
;----Создание трупа без головы 
Function CreateTrup(ent) 
For p.TTrup = Each TTrup 
If p\ent=ent 
 p\speed=1:P\dy=1 
 Return 
End If 
Next 
PointEntity ent, cam1:TurnEntity ent, 0, 180+Rnd(-20, 20), 0 
p.TTrup = New TTrup 
p\ent=ent 
EntityParent p\ent, 0 
p\dy=1.5:p\speed=1.5 
End Function 
;-----Изменение угла от a1# до a2# cо скоростью s# 
Function ang#(a1#, a2#, s#) 
Local q# 
If a2#>a1# q=a1#+s# Else q=a2# 
Return q 
End Function 
;-----Движение трупов 
Function UpDateTrups() 
For p.TTrup = Each TTrup 
 TranslateEntity p\ent, 0, p\dy, 0 
 If Not EntityCollided(p\ent, SCENE) 
  p\dy=p\dy-.1 
 Else  
  p\speed=p\speed-.1 
  If p\speed<0 p\speed=-1 
  p\dy=-.5 
 End If 
 RotateEntity p\ent, ang(EntityPitch#(p\ent), 90, 3), EntityYaw#(p\ent), EntityRoll#(p\ent) 
 If p\speed>0 MoveEntity p\ent, 0, 0, p\speed Else RotateEntity p\ent, 90, EntityYaw#(p\ent), EntityRoll#(p\ent) 
Next 
End Function 
;--Движение капелек крови  
Function UpDateBlood() 
For b.TBlood = Each TBlood 
MoveEntity b\s, 0, 0, b\speed:b\speed=b\speed-.05:If b\speed<0 b\speed=0 
TranslateEntity b\s, 0, b\dy, 0 
If (Not EntityCollided(b\s, SCENE)) And (Not EntityCollided(b\s, DOM))  
 b\dy=b\dy-.1  
Else  
 pyatna_count=pyatna_count+1 
 pyatna_x(pyatna_count)=(100+EntityX(b\s))*5 
 pyatna_y(pyatna_count)=1000-(100+EntityZ(b\s))*5 
 FreeEntity b\s 
 Delete b 
End If 
Next 
End Function 
;----?? Нанесение на текстуру пола крови ?? 
Function paintBlood() 
If pyatna_count=0 Return 
SetBuffer TextureBuffer(tex) 
;LockBuffer() 
Color 150, 0, 0 
For k=0 To pyatna_count 
Plot pyatna_x(k)+Rnd(-5, 5), pyatna_y(k)+Rnd(-5, 5) 
;WritePixelFast pyatna_x(k), pyatna_y(k), 0 
Next 
pyatna_count=0 
;UnlockBuffer() 
SetBuffer BackBuffer() 
End Function