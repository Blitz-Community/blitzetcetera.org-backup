Type TShip Extends TSpaceObject

 Global List:TList
 Global Image:Timage

 Field TurnSpeed#
  
 Function Create()
  Local Ship:TShip = New TShip  
  If List = Null List = CreateList()
  List.AddLast Ship ' Добавляем Ship в конец списка List
  
  If Not Image 
   Image = LoadImage("Ast-Ship.png")
   MidHandleImage( Image )'Центруем
  EndIf
 EndFunction
 
 Method New()'Начальные значения
  X = 300
  Y = 400
 EndMethod
 
 Method Draw()
  SetRotation( Direction )
  DrawImage( Image,X,Y )
 EndMethod

 Method Update()
  Draw()

  'Возможные действия
  Local Up,Down,Left,Right,Fire
  
  ' Действиями управляет пользователь, Игрок 1
  If KeyDown(Key_Up)     Up   = True
  If KeyDown(Key_Down)   Down  = True
  If KeyDown(Key_Left)   Left  = True
  If KeyDown(Key_Right)  Right  = True 
  If KeyDown(Key_Space)  Fire = True
 
 
  ' ФИЗИЧЕСКАЯ МОДЕЛЬ 
  '---------------------------------------
  'Добавь это, чтобы добавить физическую модель
   
   Local Acceleration#   = 0.04
   Local Friction#    = 0.014
   Local TopSpeed#    = 2.0
   
   Local TurnAcceleration# = 0.18
   Local TurnFriction#   = 0.03  
   Local TurnMax#     = 5
 
  '--------------------------------------- 
  
        
  'ФИЗИКА ДВИЖЕНИЯ
  '--------------------------------------
  '         
  If Up

   ' Создаем и вычитаем вектор ускорения из
   ' вектора скорости
   XSpeed:+ Cos(Direction)*Acceleration
   YSpeed:+ Sin(Direction)*Acceleration 
  EndIf
  
  If Down
   ' Создаем и вычитаем вектор ускорения из
   ' вектора скорости
   XSpeed:- Cos(Direction)*Acceleration
   YSpeed:- Sin(Direction)*Acceleration
  EndIf
  
  'Считаем длину вектора скорости
  Local SpeedVectorLength# = Sqr(XSpeed*XSpeed + YSpeed*YSpeed)
  
  If SpeedVectorLength > 0
   'Если движемся, то уменьшаем скорость на величину трения
   XSpeed:- (XSpeed/SpeedVectorLength)*Friction
   YSpeed:- (YSpeed/SpeedVectorLength)*Friction
  EndIf
  
  If SpeedVectorLength > TopSpeed
   'Если скорость больше максимальной, то уменьшаем
   'её до максимальной, хранящейся в TopSpeed
   XSpeed:+ (XSpeed/SpeedVectorLength)*(TopSpeed - SpeedVectorLength)
   YSpeed:+ (YSpeed/SpeedVectorLength)*(TopSpeed - SpeedVectorLength)
  EndIf
  
  'Двигаем
  X:+ XSpeed
  Y:+ YSpeed
    
  'Rem 'Рисуем векторы  
  SetRotation 0
  SetColor 255,0,0 'Красный 
   DrawLine X,Y,X,Y + YSpeed*50
  SetColor 0,255,0 'Зеленый
   DrawLine X,Y,X+XSpeed*50,Y
  SetColor 0,0,255 'Синий
   'Это длина вектора скорости
   'Он построен сложением красного и зеленого векторов
   DrawLine X,Y,X+XSpeed*50,Y+YSpeed*50
  SetColor 255,255,255
  'EndRem 
    
  'ФИЗИКА ПОВОРОТА
  '--------------------------------------
  '         
  If Left TurnSpeed:-TurnAcceleration
  If Right TurnSpeed:+TurnAcceleration   
  
  'Предел для TurnSpeed
  If TurnSpeed >  TurnMax TurnSpeed =  TurnMax
  If TurnSpeed < -TurnMax TurnSpeed = -TurnMax
    
  Direction:+TurnSpeed
  
  If Direction > 360  Direction:- 360
  If Direction < 0  Direction:+ 360
  
  'Применяем силу трения для поворота
  If TurnSpeed >  TurnFriction TurnSpeed:- TurnFriction
  If TurnSpeed < -TurnFriction TurnSpeed:+ TurnFriction
  
  'Если сила трения больше скорости поворота, то прекращаем поворот
  If TurnSpeed < TurnFriction And TurnSpeed > -TurnFriction TurnSpeed = 0

  If Fire TShot.Fire( X, Y, Direction, XSpeed, YSpeed )
  
 EndMethod

 Function UpdateAll()
  Local Ship:TShip
  For Ship = EachIn List
   Ship.Update()
  Next
 EndFunction

End Type