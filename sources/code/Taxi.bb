;====================================================== 
 
Graphics3D 640,480,16,2 
SetBuffer BackBuffer() 
 
Global fntArial1=LoadFont("Verdana",24,1,0,0) 
Global fntArial2=LoadFont("Verdana",16,1,0,0) 
Global x#,y#,z#,xt#,yt#,zt#,xc#,yc#,zc#,xl#,yl#,zl# 
Global taxi,police,flash1,flash2 
 
Const CAR=1,WALL=2,TARG=3 
Collisions CAR,WALL,2,0  
Collisions TARG,WALL,2,3 
Collisions CAR,TARG,2,1 
 
 CreateWorld()    ;Создаём "мир" (см. функцию ниже) 
 CreatePolice()    ;Создаём "Мента" (см. функцию ниже) 
 CreateTaxi()     ;Создаём "Такси" (см. функцию ниже) 
 PositionEntity taxi,10,1.3,0  ; Устанавливаем начальную позицию Такси 
 
;================================================= 
;==           MAIN PROGRAMM            == 
;================================================= 
  
While Not KeyHit(1) 
 Cls 
 
If KeyDown( 205 ) x=.1 Else If KeyDown( 203 ) x=-.1 Else x=0 ; Перемещение "Вперёд-Назад" 
If KeyDown( 208 ) z=-.1 Else If KeyDown( 200 ) z=.1 Else z=0  ; Вращение "Влево-Вправо" 
If KeyHit( 57 ) m=1-m  ; Старт-Стоп 
 
 xt#=EntityX#(taxi)  ; смотрим текущие координаты Такси по оси Х 
 zt#=EntityZ#(taxi)  ; смотрим текущие координаты Такси по оси Z 
 
 xl#=EntityX#(police)  ; смотрим текущие координаты Мента по оси X 
 zl#=EntityZ#(police)  ; смотрим текущие координаты Мента по оси Z 
 
 cx#=xt-xl 
 cz#=zt-zl 
 cn#=Sqr(cx*cx+cz*cz) 
 cx=cx/cn    ; вычисляем промежуточную координату по оси X  между Ментом и Такси для ориентации Мента 
 cz=cz/cn    ; вычисляем промежуточную координату по оси Z  между Ментом и Такси  для ориентации Мента 
 
 
If colid=0 And m=1 AlignToVector police,cx,0,cz,0,.04  ; Если Мент не упёрся в "Стену" и не "догнал" Такси выравниваем Мента относительно промежуточных координат 
If m=1 TurnEntity flash1,0,6,0 : TurnEntity flash2,0,-6,0  ; Если Мент не "догнал" Такси, "вращаем" Мигалки Мента 
If m=1 ScaleEntity flash1,.4,.4,.4 : ScaleEntity flash2,.4,.4,.4 Else ScaleEntity flash1,.2,.2,.2 : ScaleEntity flash2,.2,.2,.2 ; Масштабируем Мигалки Мента 
 
 
If EntityCollided (police,WALL) colid=1 TurnEntity police,0,1,0 Else colid=0  ; Если Мент "упёрся" в стену, поворачиваем влево, пока не перестанем "упираться" 
If EntityCollided (police,TARG) m=0 ; Если Мент "догнал" такси, останавливаем "погоню" 
If EntityDistance (police,taxi)>2 MoveEntity police,0,0,m*.25 Else m=0  ; Если дистанция между Ментом и Такси больше 2, двигаем Мента, в ином случае - останавливаем "погоню" 
 
 
MoveEntity taxi,0,0,z*4  ; передвигаем Такси 
TurnEntity taxi,0,-x*20,0  ; поворачиваем Такси 
 
 
 UpdateWorld 
 RenderWorld 
 
 SetFont fntArial2 
 Color 0,255,0 
 Text 180,10,"Press SPACEBAR for Start or Stop CHASE" 
 Text 180,30,"CURSOR KEYS for driving Taxi" 
 SetFont fntArial1 
 If m=1 Color 0,255,0 : Text 260,400,"START CHASE" 
 If m=0 Color 255,0,0 : Text 260,400,"STOP CHASE" 
 
  Flip 
Wend

;================================================= 
;==            FUNCTIONS           == 
;================================================= 
 
Function CreateWorld() 
 light=CreateLight() 
 TurnEntity light,30,90,0 
  
 plane=CreatePlane() 
 EntityColor plane,50,100,0 
   
 walls=CreateCube() 
 ScaleEntity walls,6,3,2 
 PositionEntity walls,0,.5,10 
 EntityColor walls,200,100,0 
 EntityType walls,WALL 
  
 walls1=CopyEntity(walls) 
 PositionEntity walls1,-15,.5,20 
 RotateEntity walls1,0,90,0 
  
 walls2=CopyEntity(walls) 
 PositionEntity walls2,15,.5,20 
 RotateEntity walls2,0,90,0 
 
 walls3=CopyEntity(walls) 
 PositionEntity walls3,0,.5,30 
 RotateEntity walls3,0,0,0 
 
 cam1=CreateCamera() 
 CameraRange cam1,1,2500 
 RotateEntity cam1,45,0,0 
 PositionEntity cam1,0,18,-3 
End Function 
 
Function CreatePolice() 
 police=CreateCube() 
 ScaleEntity police,1.4,.75,1 
 PositionEntity police,0,1.3,0 
 part1=CreateCube(police) 
 ScaleEntity part1,1,.5,1 
 PositionEntity part1,0,-.6,2 
 EntityColor part1,30,30,30 
 part2=CreateCube(police) 
 ScaleEntity part2,1,.5,.75 
 PositionEntity part2,0,-.6,-1.75 
 EntityColor part2,30,30,30 
 part3=CreateCube(police) 
 ScaleEntity part3,.5,.2,.2 
 PositionEntity part3,0,1.2,0 
 EntityColor part3,150,150,150 
 flash1=CreateCube(police) 
 ScaleEntity flash1,.2,.2,.2 
 PositionEntity flash1,.75,1.2,0 
 EntityColor flash1,255,0,0 
 flash2=CreateCube(police) 
 ScaleEntity flash2,.2,.2,.2 
 PositionEntity flash2,-.75,1.2,0 
 EntityColor flash2,0,0,255 
 whp1=CreateCylinder(8,1,police) 
 ScaleEntity whp1,.6,1.2,.6 
 PositionEntity whp1,0,-1,2 
 RotateEntity whp1,0,0,90 
 EntityColor whp1,10,10,10 
 whp2=CreateCylinder(8,1,police) 
 ScaleEntity whp2,.6,1.2,.6 
 PositionEntity whp2,0,-1,-1.75 
 RotateEntity whp2,0,0,90 
 EntityColor whp2,10,10,10 
 EntityRadius police,3  ; Устанавливаем радиус коллизии Мента 
 EntityType police,CAR  ; Устанавливаем тип коллизии Мента 
End Function 
 
Function CreateTaxi() 
 taxi=CreateCube() 
 ScaleEntity taxi,1.4,.75,1 
 PositionEntity taxi,0,1.3,0 
 EntityColor taxi,255,200,0 
 part1=CreateCube(taxi) 
 ScaleEntity part1,1,.5,1 
 PositionEntity part1,0,-.6,2 
 EntityColor part1,255,200,0 
 part2=CreateCube(taxi) 
 ScaleEntity part2,1,.5,.75 
 PositionEntity part2,0,-.6,-1.75 
 EntityColor part2,255,200,0 
 part3=CreateCube(taxi) 
 ScaleEntity part3,.5,.2,.2 
 PositionEntity part3,0,1.2,0 
 EntityColor part3,255,100,0 
 whp1=CreateCylinder(8,1,taxi) 
 ScaleEntity whp1,.6,1.2,.6 
 PositionEntity whp1,0,-1,2 
 RotateEntity whp1,0,0,90 
 EntityColor whp1,10,10,10 
 whp2=CreateCylinder(8,1,taxi) 
 ScaleEntity whp2,.6,1.2,.6 
 PositionEntity whp2,0,-1,-1.75 
 RotateEntity whp2,0,0,90 
 EntityColor whp2,10,10,10 
 EntityRadius taxi,3  ; Устанавливаем радиус коллизии Такси 
 EntityType taxi,TARG  ; Устанавливаем тип коллизии Такси 
End Function 
 	
