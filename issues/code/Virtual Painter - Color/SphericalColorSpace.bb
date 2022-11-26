Graphics 640,480,32

i=LoadImage("Painter1.jpg")
DrawBlock i,0,0

;Заменяемый цвет
r1=224
g1=224
b1=0
;Цвет замены
r2=64
g2=64
b2=255
;Радиус
rad#=128

For y=0 To 479
 For x=0 To 639
  ;Разложение цвета на составляющие
  p=ReadPixel(x,y)
  b=p And 255
  g=(p Shr 8) And 255
  r=(p Shr 16) And 255
  ;Расстояние между исходным цветом и заменяемым
  d#=Sqr((r-r1)*(r-r1)+(g-g1)*(g-g1)+(b-b1)*(b-b1))
  ;Проверка нахождения цвета внутри сферы
  If d#<=rad# Then
   ;Вычисление коэффициентов
   d1#=d#/rad#
   d2#=1-d1#
   ;Значения интенсивностей составляющих
   r=Int(d1#*r+d2#*r2)
   g=Int(d1#*g+d2#*g2)
   b=Int(d1#*b+d2#*b2)
  End If
  ;Изображение пиксела
  WritePixel x,y,b+(g Shl 8)+(r Shl 16)
 Next
Next

WaitKey