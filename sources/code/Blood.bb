Graphics3D 800, 600, 16 
HidePointer() 
Global p=CreateTexture(2, 2);Текстура дырки от пули 
 SetBuffer TextureBuffer(p) 
 ClsColor 0, 0, 0 
 Cls 
Global blood=CreateTexture(6, 6);Текстура крови 
 SetBuffer TextureBuffer(blood) 
 ClsColor 200, 200, 200 
 Cls 
 Color 255, 0, 0 
 Oval 3, 3, 5, 5 
SetBuffer BackBuffer() 
 
cam=CreateCamera();Камера 
TranslateEntity cam, 0, .1, 0 
CameraRange cam, .001, 20 
 
c1.TSquare=createSquare(0, 100, 1000):ScaleEntity c1\mesh, 1, 1, 10;Пол 
EntityType c1\mesh, 2 
EntityPickMode c1\mesh, 2 
c2.TSquare=createSquare(0, 100, 1000):TurnEntity c2\mesh, 0, 0, -90:MoveEntity c2\mesh, -.5, -.5, 0:ScaleEntity c2\mesh, 1, 1, 10;Стена 
EntityType c2\mesh, 2 
EntityPickMode c2\mesh, 2 
c3.TSquare=createSquare(0, 100, 1000):TurnEntity c3\mesh, 0, 0, 90:MoveEntity c3\mesh, .5, -.5, 0:ScaleEntity c3\mesh, 1, 1, 10;Стена 
EntityType c3\mesh, 2 
EntityPickMode c3\mesh, 2 
c4.TSquare=createSquare(0, 100, 100):TurnEntity c4\mesh, 90, 0, 0:MoveEntity c4\mesh, 0, -5, -.5;Стена 
EntityType c4\mesh, 2 
EntityPickMode c4\mesh, 2 
c5.TSquare=createSquare(0, 100, 100):TurnEntity c5\mesh, -90, 0, 0:MoveEntity c5\mesh, 0, -5, .5;Стена 
EntityType c5\mesh, 2 
EntityPickMode c5\mesh, 2 
 
l1=CreateLight(2):LightColor l1, 0, 0, 255:LightRange l1, 1:PositionEntity l1, 0, 0, 4;Синий свет 
l2=CreateLight(2):LightColor l2, 255, 0, 0:LightRange l2, 1:PositionEntity l2, 0, 0, -4;Красный свет 
 
Collisions 1, 2, 2, 2;Коллизия кровь-стена 
Color 255, 255, 255 
SetBuffer BackBuffer() 
 
kol=CreateSphere();Тестовый зеленый шарик 
 ScaleEntity kol, .05, .05, .05 
 EntityColor kol, 0, 255, 0 
 EntityPickMode kol, 1 
 EntityRadius kol, .05 
 PositionEntity kol, Rnd(-.5, .5), .04, Rnd(-1, 1)*5 
 
While Not KeyHit(1) 
 
If KeyDown(200) MoveEntity cam, 0, 0, .1 
If KeyDown(208) MoveEntity cam, 0, 0, -.1 
If KeyDown(203) TurnEntity cam, 0, 4, 0 
If KeyDown(205) TurnEntity cam, 0, -4, 0 
 
If MouseDown(1) 
 pick=CameraPick(cam, MouseX(), MouseY()) 
 If (pick=kol);Если попали в тестовый шарик 
  createblood(pick) 
  PositionEntity pick, Rnd(-.5, .5), .04, Rnd(-1, 1)*5 
 Else;Если попали в стену 
 If (pick>0) PaintToSquare(PickedX(), PickedY(), PickedZ(), pick, p)  
 End If 
End If 
 
updateblood();Движение крови 
UpdateWorld() 
RenderWorld() 
 
Text MouseX(), MouseY(), "Х", 1, 1 
 
Flip() 
Wend 
End 
 
;============================== 
;======= ОСНОВА ============== 
;============================== 
Type TSquare 
Field mesh, texture 
End Type 
 
Function PaintToSquare(x#, y#, z#, dest_ent, src_tex) 
For dest.TSquare=Each TSquare 
If (dest\mesh=dest_ent) And (dest\texture>0) 
 TFormPoint x#, y#, z#, 0, dest\mesh 
 w=TextureWidth(src_tex) 
 h=TextureHeight(src_tex) 
 ew#=MeshWidth(dest\mesh) 
 eh#=MeshDepth(dest\mesh) 
 dest_tex=dest\texture 
 CopyRect 1, 1, w, h, (TFormedX#()+ew/2)*(TextureWidth(dest_tex)/ew) - w/2, (-TFormedZ#()+eh/2)*(TextureHeight(dest_tex)/eh) - h/2, TextureBuffer(src_tex), TextureBuffer(dest_tex) 
End If 
Next 
End Function 
 
Function CreateSquare.TSquare(tex=0, size_x=50, size_y=50, r=200, g=200, b=200) 
mesh=CreateMesh() 
surf=CreateSurface(mesh) 
v1=AddVertex(surf, .5, 0, .5, 1, 0) 
v2=AddVertex(surf, -.5, 0, .5, 0, 0) 
v3=AddVertex(surf, -.5, 0, -.5, 0, 1) 
v4=AddVertex(surf, .5, 0, -.5, 1, 1) 
AddTriangle(surf, 1, 0, 3) 
AddTriangle(surf, 1, 3, 2) 
UpdateNormals mesh 
If tex<1 
tex=CreateTexture(size_x, size_y) 
 SetBuffer TextureBuffer(tex) 
 ClsColor r, g, b:Cls 
End If 
EntityTexture mesh, tex 
s.TSquare= New TSquare 
s\mesh=mesh 
s\texture=tex 
Return s 
End Function 
;============================== 
;============================== 
;============================== 
Type TBlood 
Field dy#, ent 
End Type 
 
Function CreateBlood(ent) 
For k=1 To 20 
b.TBlood=New TBlood 
b\dy=.01-Rnd(0, .01) 
b\ent=CreateSphere(8, ent) 
EntityParent b\ent, 0 
TurnEntity b\ent, Rnd(-180, 180), Rnd(-180, 180), Rnd(-180, 180) 
EntityType b\ent, 1 
EntityColor b\ent, 255, 0, 0 
ScaleEntity b\ent, .02, .02, .02 
EntityRadius b\ent, .02, .02 
Next 
End Function 
 
Function UpdateBlood() 
For b.TBlood = Each TBlood 
 MoveEntity b\ent, 0, 0, .02 
 TranslateEntity b\ent, 0, b\dy, 0 
 b\dy=b\dy-.001 
 coll=EntityCollided(b\ent, 2) 
 If (coll>0) Or (EntityY(b\ent)<0) 
  PaintToSquare(EntityX(b\ent), EntityY(b\ent), EntityZ(b\ent), coll, blood) 
  FreeEntity b\ent 
  Delete b 
 End If 
Next 
End Function