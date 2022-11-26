Graphics3D 640,480

cam=CreateCamera()
PositionEntity cam,0,0,-3
RotateEntity CreateLight(),45,0,45

;Адрес, номер и детализация фигуры
Global m,et=1,det=10
;Цвет фигуры
Global cr=255,cg=255,cb=255
;Создание фигуры
recreate

CameraProject cam,0,0,0

;Вычисление приращения по осям X и Y
d#=ProjectedX()
CameraProject cam,1,0,0
d#=1.0/(ProjectedX()-d#)
esc#=1.0

;Загрузка в память команд урока
Dim cmd$(18,1)
For n=0 To 17
 Read cmd$(n,0),cmd$(n,1)
Next

SetFont LoadFont("Arial Cyr",14)
SetBuffer BackBuffer()

Repeat
 ;Вычисление приращений значений для мыши
 dx#=MouseX()-odx#
 dy#=ody#-MouseY()
 dmz=MouseZ()-mz
 event=0

 If MouseDown(1) Then
  ;Перемещение фигуры (событие №1)
  ex#=ex#+d#*dx#
  ey#=ey#+d#*dy#
  event=1
 ElseIf MouseDown(2) Then
  ;Вращение фигуры (событие №2)
  exang#=exang#+dx#
  eyang#=eyang#+dy#
  event=2
 ElseIf MouseDown(3) Then
  ;Масштабирование фигуры (событие №3)
  esc#=esc#*1.01^dy#
  event=3
 ElseIf dmz<>0 Then
  If det+dmz>2 And det+dmz<20 Then
   ;Изменение детализации (событие №6)
   det=det+dmz
   recreate
   event=6
  End If
  mz=MouseZ()
 Else
  mb=0
 End If

 odx#=MouseX()
 ody#=MouseY()

 ;Обработка нажатий клавиш
 i=GetKey()
 If i>=49 And i<=52 Then
  ;Изменение типа фигуры (событие №4)
  et=i-48
  recreate
  If et=2 Then event=4
 End If
 If i=48 Then 
  ;Изменение цвета фигуры (событие №5)
  cr=Rand(0,255)
  cg=Rand(0,255)
  cb=Rand(0,255)
  EntityColor m,cr,cg,cb
  event=5
 End If

 PositionEntity m,ex#,ey#,0
 RotateEntity m,eyang#,exang#,0
 ScaleEntity m,esc#,esc#,esc#

 ;Обработка команд
 Select cmd$(cn,0)
  Case "TEXT" 
   ;Вывод текста (только при бездействии)
   If event=0 Then
    txt$=cmd$(cn,1)
    cn=cn+1
   End If
  Case "WAITFOR"
   ;Ожидание события
   If event=cmd$(cn,1) Then cn=cn+1
  Case "WAITKEY"
   ;Ожидание нажатия клавиши пробела
   If i=32 Then cn=cn+1
 End Select

 RenderWorld
 Text 0,0,txt$
 Flip
Until KeyHit(1)

Function recreate()
;Создание фигуры
If m<>0 Then FreeEntity m
Select et
 Case 1:m=CreateCube()
 Case 2:m=CreateSphere(det)
 Case 3:m=CreateCylinder(det)
 Case 4:m=CreateCone(det)
End Select
EntityColor m,cr,cg,cb
End Function

Data "TEXT","Это программа предназначена для просмотра трехмерных объектов. Нажмите пробел."
Data "WAITKEY",""
Data "TEXT","Данный урок поможет вам освоить ее."
Data "WAITKEY",""
Data "TEXT","Зажав левую кнопку мыши, вы можете перемещать объект. Попробуйте."
Data "WAITFOR","1"
Data "TEXT","С помощью правой кнопки, можно вращать объект. Поверните объект."
Data "WAITFOR","2"
Data "TEXT","Средняя кнопка - масштабирование объекта. Измените размер объекта."
Data "WAITFOR","3"
Data "TEXT","Цифровые клавиши 1-4 - выбор объекта. Для продолжения, выберите сферу(2)."
Data "WAITFOR","4"
Data "TEXT","Вы можете изменять цвет объекта клавишей 0. Измените цвет для подолжения."
Data "WAITFOR","5"
Data "TEXT","Смена детализации объекта - вращение колесика мыши."
Data "WAITFOR","6"
Data "TEXT","Вот и все, урок окончен."
Data "WAITKEY",""