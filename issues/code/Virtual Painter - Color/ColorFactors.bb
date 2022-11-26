Graphics 640,480,32

i=LoadImage("Painter1.jpg")
DrawBlock i,0,0

;Заменяемый цвет
r1#=255
g1#=64
b1#=64
;Вычисление его коэффициентов
s1=r1#+g1#+b1#
kr1#=r1#/s1
kg1#=g1#/s1
kb1#=b1#/s1
;Цвет замены
r2#=0
g2#=0
b2#=255
;Вычисление его коэффициентов
s2=r2#+g2#+b2#
r2#=r2#/s2
g2#=g2#/s2
b2#=b2#/s2
;Допустимая разность коэффициентов
skmax#=.5

For y=0 To 479
 For x=0 To 639
  ;Разложение цвета на составляющие
  p=ReadPixel(x,y)
  b#=p And 255
  g#=(p Shr 8) And 255
  r#=(p Shr 16) And 255
  ;Вычисление коэффициентов исходного цвета
  s=r#+g#+b#
  kr#=r#/s
  kg#=g#/s
  kb#=b#/s
  sk#=Abs(kr1#-kr#)+Abs(kg1#-kg#)+Abs(kb1#-kb#)
  ;Проверка на допустимую разность коэффициентов
  If sk#<=skmax# Then
   ;Вычисление множителей коэффициентов
   sk1#=sk#/skmax#
   sk2#=(1-sk1#)*s
   ;Значения интенсивностей составляющих
   rr=Int(sk1#*r+sk2#*r2#)
   If rr<0 Then rr=0 ElseIf rr>255 Then rr=255
   gg=Int(sk1#*g+sk2#*g2#)
   If gg<0 Then gg=0 ElseIf gg>255 Then gg=255
   bb=Int(sk1#*b+sk2#*b2#)
   If bb<0 Then bb=0 ElseIf bb>255 Then bb=255
   WritePixel x,y,bb+(gg Shl 8)+(rr Shl 16)
  End If
  ;Изображение пиксела
 Next
Next

WaitKey