SeedRnd MilliSecs()

Graphics3D 800,600

;Размер текстуры, толщина отрезка: на текстуре (в пикселах), в пространстве (в ед.)
; Y-приращение (для ДНК), приращение угла (шар, ДНК)
Const tsiz=4,csiz=1,thick#=.1,ystp#=.2,astp#=20
Const pix#=1.0/tsiz,tend#=1.0*(csiz+2)/tsiz

;Массив адресов и счетчик отрезков
Dim cut(1000)
Global cutq=-1

;Инициализация текстуры (белый прямоугольник csiz x 2)
Global ltex=CreateTexture(tsiz,tsiz,2)
For x=0 To tsiz
 For y=0 To 3
  If x>0 And x<=csiz And y>0 And y<3 Then k=-1 Else k=0
  WritePixel x,y,k,TextureBuffer(ltex)
 Next
Next

;Камера и пивот, чтобы можно было вращать камеру вокруг объекта
p=CreatePivot()
cam=CreateCamera(p)
PositionEntity cam,0,0,-3
RotateEntity CreateLight(),45,45,45

;Единичные вектора, коллинеарные плоскости эквивалентной экрану и параллельные
; осям координатной системы экрана соответственно
Global vecx=CreatePivot(cam)
Global vecy=CreatePivot(cam)
PositionEntity vecx,1,0,0
PositionEntity vecy,0,1,0

Select 4
  
 Case 1; Случайным образом заданные отрезки
  For n=1 To 20
   newcut Rnd(-1,1),Rnd(-1,1),Rnd(-1,1),Rnd(-1,1),Rnd(-1,1),Rnd(-1,1)
  Next
 Case 2; Проекция тессеракта
  dt#=1-.5*thick#: dt1#=.25*thick#+.5: dt2#=1-.25*thick#
  ;dt#=1: dt1#=.5: dt2#=1

  For x=-1 To 1 Step 2
   For y=-1 To 1 Step 2
    ;Два кубических каркаса - внешний и внутренний
    For d#=.5 To 1 Step .5
     dd#=dt2#*d#
     newcut d#*x,d#*y,-dd#,d#*x,d#*y,dd#
     newcut -dd#,d#*x,d#*y,dd#,d#*x,d#*y
     newcut d#*x,-dd#,d#*y,d#*x,dd#,d#*y
     q=q+3
    Next
    ;Соединение каркасов
    newcut dt1#*x,dt1#*y,dt1#,dt2#*x,dt2#*y,dt2#
    newcut dt1#*x,dt1#*y,-dt1#,dt2#*x,dt2#*y,-dt2#
   Next
  Next
 Case 3; Шар
  For a#=0 To 359 Step astp#
   newcut Cos(a#),Sin(a#),0,Cos(a#+astp#),Sin(a#+astp#),0
   newcut Cos(a#),0,Sin(a#),Cos(a#+astp#),0,Sin(a#+astp#)
   newcut 0,Cos(a#),Sin(a#),0,Cos(a#+astp#),Sin(a#+astp#)
  Next
 Case 4; ДНК
  For yy#=-2 To 2 Step ystp#
   newcut Cos(a#),yy#,Sin(a#),-Cos(a#),yy#,-Sin(a#)
   newcut Cos(a#),yy#,Sin(a#),Cos(a#+astp#),yy#+ystp#,Sin(a#+astp#)
   newcut -Cos(a#),yy#,-Sin(a#),-Cos(a#+astp#),yy#+ystp#,-Sin(a#+astp#)
   a#=a#+astp#
  Next
End Select

Repeat
 TurnEntity p,.2,.5,.7

 ;Эту процедуру нужно вызывать для каждого отрезка перед RenderWorld, если
 ; положение камеры или отрезка изменилось
 For n=0 To cutq
  cutupdate n,thick#,cam
 Next

 RenderWorld
 Flip
Until KeyHit(1)


Function newcut(x1#,y1#,z1#,x2#,y2#,z2#)
cutq=cutq+1
m=CreateMesh(parent)
cut(cutq)=m

;Параметры меша отрезка: пояная яркость, двусторонний, тип отображения - сложение
EntityFX m,17
EntityBlend m,3
EntityTexture m,ltex

;Создание меша отрзка
s=CreateSurface(m)
AddVertex s,x1#,y1#,z1#
AddVertex s,x2#,y2#,z2#
AddVertex s,0,0,0,0,1.5*pix#
AddVertex s,0,0,0,.5*tend#,0
AddVertex s,0,0,0,tend#,1.5*pix#
AddVertex s,0,0,0,tend#,2.5*pix#
AddVertex s,0,0,0,.5*tend#,4*pix#
AddVertex s,0,0,0,0,2.5*pix#
AddTriangle s,2,3,4
AddTriangle s,2,4,5
AddTriangle s,2,5,7
AddTriangle s,7,6,5
End Function

Function cutmove(n,x1#,y1#,z1#,x2#,y2#,z2#)
s=GetSurface(cut(n),0)
VertexCoords s,0,x1#,y1#,z1#
VertexCoords s,1,x2#,y2#,z2#
End Function

Function cutupdate(n,thick#,cam)

s=GetSurface(cut(n),1)

;Оределение угла между точками отрезка, спроецированными на экран
CameraProject(cam,VertexX(s,1),VertexY(s,1),VertexZ(s,1))
vx#=ProjectedX()
vy#=ProjectedY()
CameraProject(cam,VertexX(s,0),VertexY(s,0),VertexZ(s,0))
ang#=ATan2(ProjectedY()-vy#,ProjectedX()-vx#)

;Задание координат вертексов меша отрезка через еддиничные вектора (по окружности)
For n=2 To 7
 k=(n>4)
 a#=90.0*(n+(n<=4))-ang#

 dx#=Cos(a#)*thick#
 dy#=Sin(a#)*thick#
 x#=(EntityX(vecx,True)-EntityX(cam,True))*dx#+(EntityX(vecy,True)-EntityX(cam,True))*dy#
 y#=(EntityY(vecx,True)-EntityY(cam,True))*dx#+(EntityY(vecy,True)-EntityY(cam,True))*dy#
 z#=(EntityZ(vecx,True)-EntityZ(cam,True))*dx#+(EntityZ(vecy,True)-EntityZ(cam,True))*dy#

 x1#=EntityX(vecx,True):x2=EntityX(vecy,True)
 y1#=EntityX(vecx,True):y2=EntityX(vecy,True)

 VertexCoords s,n,VertexX(s,k)+x#,VertexY(s,k)+y#,VertexZ(s,k)+z#
Next
End Function