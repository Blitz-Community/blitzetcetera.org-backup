SeedRnd MilliSecs()

Dim ptx#(81)
Dim pty#(81)

Graphics 800,600

;Создается цепь из точек
x#=0
y#=300
Color 255,0,0
Repeat
 q=q+1
 ptx#(q)=x#
 pty#(q)=y#
 Oval x#-4,y#-4,9,9
 x#=x#+Rnd(30,100)
 y#=y#+Rnd(-100,100) 
Until x#>=800

Color 255,255,255
;Цикл по всем отрезкам (для крайней правой точки отрезка нет)
For n=1 To q-1
 d#=pty#(n)
 If n=1 Then
  ;Если берется начальный отрезок, то производная равна 0 (т.к. смежная точка
  ; слева, необходимая для определения коэффициента отсутствует
  c#=0
 Else
  ;Вычисление коэффициента, равного производной N1 по формуле
  c#=(pty#(n+1)-pty#(n-1))/(ptx#(n+1)-ptx#(n-1))
 End If
 ;Аналогично вычисляется производная N2
 If n=q Then
  dy2#=0
 Else
  dy2#=(pty#(n+2)-pty#(n))/(ptx#(n+2)-ptx#(n))
 End If
 ;Вычисление остальных коэффициентов многочлена
 x3#=ptx#(n+1)-ptx#(n)
 xx3#=x3#*x3#
 b#=(3*pty#(n+1)-dy2#*x3#-2*c#*x3#-3*d#)/xx3#
 a#=(dy2#-2*b#*x3#-c#)/(3*xx3#)
 ;Построение отрезка кривой
 For x#=0 To x3#
  xx#=x#*x#
  y#=a#*xx#*x#+b#*xx#+c#*x#+d#
  x1#=x#+ptx#(n)  
  If x1#>0 Then
   y1#=y#
   If y1#<-3 Then y1#=-3 ElseIf y1#>602 Then y1#=602
   If y2#<-3 Then y2#=-3 ElseIf y2#>602 Then y2#=602
   If y2#<y1# Then z#=y1#:y1#=y2#:y2#=z#
   For yy#=y1# To y2#
    Rect x1#-1,yy#-1,3,3
   Next
  End If
  y2#=y#
 Next
Next
WaitKey