Type dot
 Field x,y,dx1,dy1,dx2,dy2
End Type

Const xres=800, yres=600, stp#=.01

Global seldot.dot, sel, mx, my
Dim a#(1),b#(1),c#(1),d#(1),oc(1)

Graphics xres,yres,32,2

dt.dot=New dot
dt\x=.5*xres
dt\y=.25*yres
dt.dot=New dot
dt\x=.5*xres
dt\y=.75*yres

SetBuffer BackBuffer()
Repeat

 redraw

 mx=MouseX()
 my=MouseY()
 ;��� ���������� ����� ��������� �������� 1, ���� ������ ����� � 2, ���� ������
 ; ������ ����
 mb=MouseDown(1)+2*MouseDown(2)

 ;����� �������� � ����������� �� ������� ������ � ���������� �������
 Select mb+sel*10
  Case 11
   ;���� ������ ����� ������ ���� � ��� �������� ��������� ������� - ��������� ��
   sel=3
  Case 31
   ;���� ������ ����� ������ ���� ��� ��������������� ������� - �������������
   ; ���������
   sel=0
   redraw
   If sel=1 Then sel=3
  Case 12
   ;������ ������ ������ ���� ��� ��������� ������� - ���������� �������
   seldot\x=mx
   seldot\y=my
  Case 22
   ;������ ������ ������ ���� ��� ��������� ����� - ��������� �������
   dt.dot=New dot
   dt\x=mx
   dt\y=my
   Insert dt After seldot
   seldot=dt
   sel=0
 End Select

 ;���� ��������� ��������
 If sel=3 Then
  If KeyDown(3) Then
   ;������ ������� "2" - �������� ������ ������
   seldot\dx1=seldot\x-mx
   seldot\dy1=seldot\y-my
  End If
  If KeyDown(2) Or KeyDown(4) Then
   ;�������� ������ ������
   seldot\dx2=mx-seldot\x
   seldot\dy2=my-seldot\y
   If KeyDown(4) Then
    ;� ������, ���� ������ ������� "3" � �� "2"
    seldot\dx1=mx-seldot\x
    seldot\dy1=my-seldot\y
   End If
  End If
 End If

 ;���� ������� �������, ��...
 If sel=1 Or sel=3 Then
  ;�������� �����("Del")
  If KeyDown(211) And After First dot<>Last dot Then
   Delete seldot
   sel=0
  End If
  ;��������� ��������, ���� ����� "0"
  If KeyDown(11) Then
   seldot\dx1=0
   seldot\dy1=0
   seldot\dx2=0
   seldot\dy2=0
  End If
 End If

 ;������ ������
 If KeyHit(60) Then
  f=WriteFile("data.bb")
  For dt.dot=Each dot
   WriteLine f,"Data "+dt\x+","+dt\y+","+dt\dx1+","+dt\dy1+","+dt\dx2+","+dt\dy2   
  Next
  CloseFile f
 End If 

 ;�������� ������
 If KeyHit(61) Then
  Delete Each dot
  f=ReadFile("data.bb")
  While Not Eof(f)
   dt.dot=New dot
   m$=","+Mid$(ReadLine(f),6)
   For n=1 To 6
    m$=Mid$(m$,Instr(m$,",")+1)
    Select n
     Case 1:dt\x=m$
     Case 2:dt\y=m$
     Case 3:dt\dx1=m$
     Case 4:dt\dy1=m$
     Case 5:dt\dx2=m$
     Case 6:dt\dy2=m$
    End Select
   Next
  Wend
  CloseFile f
 End If 

Until KeyHit(1)

Function redraw()
;���� ��������� �� �������������, ��� �����������
If sel<3 Then sel=0

Cls
Color 255,255,255
;�������� �� �������� � ������
If sel=0 Then
 For dt.dot=Each dot
  If Abs(mx-dt\x)<=3 And Abs(my-dt\y)<=3 Then seldot=dt: sel=1
 Next
End If
For dt.dot=Each dot
 ;��������� �����
 Oval dt\x-1,dt\y-1,3,3
 drawcurve dt
Next
If sel Then Oval seldot\x-3,seldot\y-3,7,7

Flip
End Function

Function drawcurve(dt1.dot)
dt2.dot=After dt1
If dt2=Null Then Return

r#=.05*Sqr((dt1\x-dt2\x)*(dt1\x-dt2\x)+(dt1\y-dt2\y)*(dt1\y-dt2\y))
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

For t#=0 To 1 Step stp
 tt#=t#*t#
 For nn=0 To 1
  oc(nn)=a#(nn)*tt#*t#+b#(nn)*tt#+c#(nn)*t#+d#(nn)
 Next

 If t#>0 Then
  If sel=4 Then
   ;��������� ������� ������
   For xx=-1 To 1
    For yy=-1 To 1
     Line oc(0)+xx,oc(1)+yy,x+xx,y+yy
    Next
   Next
  Else
   Line oc(0),oc(1),x,y
  End If
 End If

 ;�������� �� �������� � �������� ������
 If sel=0 Then
  ;�������� ���������� ������� ������ ��������������� ��������������
  If mx>=min(x,oc(0))-3 And mx<=max(x,oc(0))+3 Then
   If my>=min(y,oc(1))-3 And my<=max(y,oc(1))+3 Then
    aa#=y-oc(1)
    bb#=oc(0)-x
    ;�������� ���������� �� ������
    If Abs(aa#*(mx-x)+bb#*(my-y))<=3.0*Sqr(aa#*aa#+bb#*bb#) Then
     seldot=dt1
     ;��� ������ ������ ��������� ���������� ������, ������� �� � ������
     ; ��� ���� ���������� �� ��������
     sel=4
     t#=-stp
    End If
   End If
  End If
 End If

 x=oc(0)
 y=oc(1)

Next
;��������������� �������� sel
If sel=4 Then sel=2

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

Function min(v1,v2)
If v1<v2 Then Return v1 Else Return v2
End Function

Function max(v1,v2)
If v1>v2 Then Return v1 Else Return v2
End Function