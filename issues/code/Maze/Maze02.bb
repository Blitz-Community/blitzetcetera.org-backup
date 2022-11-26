SeedRnd MilliSecs()
Graphics 800,600

ClsColor 255,255,255
Cls
Color 0,0,0

Y = Int(Rnd * 24)             ; Координаты "пробившегося источника"
X = Int(Rnd * 32)             
For N = 0 To 350              ; Длительность процесса
 B = Rand(1,4)                ; Направление "размывания"
 Select B                     ; Подсчет координат "нового туннеля"
  Case 1: If Y > 1   Then Y1 = Y - 1: Y = Y - 2: X1=X
  Case 2: If X < 30 Then X1 = X + 1: X = X + 2: Y1=Y
  Case 3: If Y < 20 Then Y1 = Y + 1: Y = Y + 2: X1=X
  Case 4: If X > 1  Then X1 = X - 1: X = X - 2: Y1=Y
 End Select
 Rect X*25,Y*25,25,25          ;"Размываем"
 Rect X1*25,Y1*25,25,25        
Next

WaitKey:End