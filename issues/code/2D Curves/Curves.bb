Type dot
 Field x,y,dx1,dy1,dx2,dy2
End Type

Const xres=800, yres=600, stp#=.01

Global seldot.dot, bi, sel
Dim a#(1),b#(1),c#(1),d#(1),oc(1)

Graphics xres,yres,32

;Задание начальных точек
dt.dot=New dot
dt\x=.5*xres
dt\y=.25*yres
dt\dx2=50
dt\dy2=50
dt.dot=New dot
dt\x=.5*xres
dt\y=.75*yres
dt\dx1=40
dt\dy1=-60


drawcurve First dot
WaitKey

Function drawcurve(dt1.dot)
dt2.dot=After dt1

;Вычисление расстояния между вершинами кривой для вычисления производных
r#=.05*Sqr((dt1\x-dt2\x)*(dt1\x-dt2\x)+(dt1\y-dt2\y)*(dt1\y-dt2\y))

;Вычисление коэффициентов кривой
For nn=0 To 1
 If nn Then
  v1=dt1\y
  v2=dt2\y
  c#(nn)=dt1\dy2*r#
  dy2#=dt2\dy1*r#
 Else
  v1=dt1\x
  v2=dt2\x
  c#(nn)=dt1\dx2*r#
  dy2#=dt2\dx1*r#
 End If
 d#(nn)=v1
 b#(nn)=3.0*v2-dy2#-2.0*c#(nn)-3.0*d#(nn)
 a#(nn)=(dy2#-2*b#(nn)-c#(nn))/3.0
Next

;Построение кривой
For t#=0 To 1 Step stp
 tt#=t#*t#
 For nn=0 To 1
  oc(nn)=a#(nn)*tt#*t#+b#(nn)*tt#+c#(nn)*t#+d#(nn)
 Next
 If t#>0 Then Line oc(0),oc(1),x,y
 x=oc(0)
 y=oc(1)
Next

;Отображение направляющих отрезков, относящихся к кривой, и ее вершин
Color 0,255,255
If dt1\dx2<>0 Or dt1\dy2<>0 Then
 Line dt1\x,dt1\y,dt1\x+dt1\dx2,dt1\y+dt1\dy2
 Oval dt1\x+dt1\dx2-1,dt1\y+dt1\dy2-1,3,3
End If
If dt2\dx1<>0 Or dt2\dy1<>0 Then
 Line dt2\x,dt2\y,dt2\x-dt2\dx1,dt2\y-dt2\dy1
 Oval dt2\x-dt2\dx1-1,dt2\y-dt2\dy1-1,3,3
End If
Color 255,255,255
End Function