<code bb>
Graphics3D 800,600

PositionEntity CreateCamera(),0,0,-20
RotateEntity CreateLight(),45,45,45

;Тип для пушки - координаты, угол, режим стрельбы, указатели на 3D-объекты,
; переменные для AI
Type turret
 Field x#,y#,Mode,head,barrel,ang#,dir#,dx#,dy#
End Type

;Тип для выстрела - координаты, приращения, указатель на 3D-объект,
; расстояние от пушки, вспомогательные переменные для AI
Type shot
 Field x#,y#,dx#,dy#,h,r#,tx#,ty#,dir#,t.turret
End Type

Const aimax=200,aistp#=.2
Dim aiang#(aimax)

SetFont LoadFont("arial",14)
SetBuffer BackBuffer()

;Константы - период стрельбы, скорость снарядов, скорость игрока
Const frq=200, v0#=.01, vp#=.005
;Const frq=200, v0#=.2, vp#=.1
Const bs#=2

Global x#,y#

;createturret 1:createturret 2:createturret 3:createturret 4
createturret 5:createturret 4:createturret 2


;Создание игрока
p=CreateSphere(16)
h=CreateSphere(8,p)
PositionEntity h,1,0,0
ScaleEntity h,.4,.4,.4

Repeat
 tim=MilliSecs() 

 For t.turret=Each turret

  ;Вспомогательные переменные + нацеливание на игрока по умолчанию
  dx#=x#-t\x
  dy#=y#-t\y
  cang#=ATan2(dy#,dx)
  t\dx#=dx#
  t\dy#=dy#
  t\dir#=cang#
  pr#=Sqr(dx#*dx#+dy#*dy#)

  ;Режимы прицеливания пушек:
  Select t\Mode
   Case 0; стрельба в данном направлении
    cang=0
   Case 1; стрельба с вращением
    cang#=t\ang#+.05*dt#
   Case 2; точное нацеливание на объект

   Case 3; стрельба веером
    cang#=cang#+Sin(dcang#)*30
    dcang#=dcang#+2
   Case 4; стрельба на опережение
    ;Вычисление коэффициентов квадратного уравнения
    a#=v#*v#-v0#*v0#
    b#=2.0*v#*(dx#*Cos(ang#)+dy#*Sin(ang#)-bs#)
    c#=dx#*dx#+dy#*dy#-bs#*bs#
    ;Вычисление дискриминанта
    d#=b#*b#-4.0*a#*c#
    If d#>=0 Then
     ;Вычисление момента встречи снаряда и игрока
     t1#=(-b#+Sqr(d#))/2.0/a#
     t2#=(-b#-Sqr(d#))/2.0/a#
     If t2#>0 Then t1#=t2#
     If t1#>=0 Then
      ;Вычисление угла через координаты объекта в найденный момент времени
      cang#=ATan2(y#+t1#*v#*Sin(ang#)-t\y#,x#+t1#*v#*Cos(ang#)-t\x#)
     End If
    End If 
   Case 5; самообучающаяся пушка (поправка к углу берется из массива
    cang#=cang#+aiang(Int(pr#*aistp#))
  End Select

  ;Поворот пушки
  rotateturret t,cang#
  ;Возврат дула на прежнее положение
  If EntityX(t\barrel)<.75 Then MoveEntity t\barrel,0,-.25/frq*dt#,0
 
  ;Выстрел и смещение дула внутрь пушки
  If nextshot<=tim Then
   fire t
   PositionEntity t\barrel,.5,0,0
  End If
 Next

 ;Задание следующего момента стрельбы 
 If nextshot<=tim Then nextshot=tim+frq

 For s.shot=Each shot
  ;Смещение снарядов
  s\x#=s\x#+s\dx#*dt#
  s\y#=s\y#+s\dy#*dt#
  s\r#=s\r#+v0#*dt#
  PositionEntity s\h,s\x#,s\y#,0
  ;Если расстояния от пушки до снаряда и до игрока равны, то
  If pr#<=s\r# And s\dir#<999 Then
   ;Вычисляется корректирующий угол для данного расстояния
   Newang#=s\t\dir#-s\dir#
   ; втискивается в границы [-180,180)
   Newang#=Newang#-Floor((Newang#+180.0)/360.0)*360.0
   ; и заносится в массив,
   aiang(Int(pr#*aistp#))=Newang#
   ; (следующая строка - чтобы не заносить этот снаряд в массив еще раз)
   s\dir#=999
  End If
  If (s\x#-x#)^2+(s\y#-y#)^2<1 Then
   FreeEntity s\h:Delete s
   red=255
   hits#=hits#+1
  Else
   ;Удаление снарядов, вышедших за границы экрана
   If Abs(s\x#)+Abs(s\y#)>40 Then FreeEntity s\h:Delete s
  End If
 Next

 ;Индикация попадания
 If red>0 Then
  red=red-Int(dt/3)
 Else
  red=0
 End If
 EntityColor p,255,255-red,255-red

 ;Перемещение и поворот игрока (скорость вычисляется исходя из прошедшего времени)
 PositionEntity p,x#,y#,0
 RotateEntity p,0,0,ang#
 v#=(KeyDown(200)-KeyDown(208))*vp#
 ang#=ang#+.3*dt#*(KeyDown(203)-KeyDown(205))
 x#=x#+v#*Cos(ang#)*dt#
 y#=y#+v#*Sin(ang#)*dt#
 
 RenderWorld
 ;Счет
 Text 0,0,"Hits/sec:"+(hits#/sec#)
 Flip
 ;Время, затраченное этот цикл (дробное, так как умножается на дробные величины)
 dt#=MilliSecs()-tim
 sec#=sec#+0.001*dt#
Until KeyHit(1)

Function createturret(Mode)
;Создание головной части
t.turret=New turret
t\head=CreateCylinder(6)
ScaleMesh t\head,1,.5,1
RotateMesh t\head,90,0,0
;Дуло создать отдельно, но привязать к головной части
t\barrel=CreateCylinder(16,False)
RotateEntity t\barrel,0,0,90
ScaleEntity t\barrel,.2,1,.2
PositionEntity t\barrel,.75,0,0
h=CreateCylinder(16,True)
ScaleEntity h,.3,.3,.3
RotateEntity h,0,0,90
PositionEntity h,2,0,0
EntityParent t\barrel,t\head
EntityParent h,t\barrel
;Задание координат пушки случайным образом
t\x#=Rnd(-20,20)
t\y#=Rnd(-20,20)
PositionEntity t\head,t\x#,t\y#,0
t\Mode=Mode
End Function

Function rotateturret(t.turret,ang#)
t\ang#=ang#
RotateEntity t\head,0,0,ang#
End Function

Function fire(t.turret)
ang#=t\ang#
s.shot=New shot
s\x#=t\x#+Cos(ang#)*r
s\y#=t\y#+Sin(ang#)*r
s\r#=r
s\dx#=Cos(ang#)*v0#
s\dy#=Sin(ang#)*v0#
s\h=CreateSphere(2)
ScaleEntity s\h,.15,.15,.15
EntityColor s\h,255,255,0
;Вспомогательные переменные (для AI)
s\tx#=t\x#
s\ty#=t\y#
s\dir=t\dir#
s\t=t
End Function
</code><noinclude>[[Категория:Код]]</noinclude>