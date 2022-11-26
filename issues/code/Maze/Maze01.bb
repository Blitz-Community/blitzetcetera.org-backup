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
  Case 1: If Y <> 0  Then Y = Y - 1
  Case 2: If X <> 31 Then X = X + 1
  Case 3: If X <> 21 Then Y = Y + 1
  Case 4: If X <> 0  Then X = X - 1
 End Select
 Rect X*25,Y*25,25,25          ;"Размываем"
Next

WaitKey:End