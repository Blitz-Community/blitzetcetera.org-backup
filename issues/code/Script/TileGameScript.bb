Graphics 640,480,32

;�������� ����������� ������ (��������� - ������)
Global tiles=LoadAnimImage("Script-Tiles.png",20,20,0,12)
;���� ����� - ���������
MaskImage tiles,255,0,255

;����� ��� �������� �����
Global buf=CreateImage(640,480)
Global bufb=ImageBuffer(buf)

;���������� ������, ����� ������
Global px=16,py=12,sc=0

;��������� � ������ ���� ������ �������
Dim scr$(1,19)
For n1=0 To 1
 For n2=0 To 19
  Read scr$(n1,n2)
 Next
Next

SetBuffer BackBuffer()
SetFont LoadFont("Arial Cyr",20,True)
;��������� ������ ����� 0
setscr 0

Repeat
 ;����������, ��������� �� ������� ������
 dx=KeyDown(205)-KeyDown(203)
 If dx=0 Then dy=KeyDown(208)-KeyDown(200) Else dy=0
 ;�����������
 If dx<>0 Or dy<>0 Then move dx,dy
 ;���� ������� ��� ������� ������� ����� ����������
 If py>0 And KeyHit(57) Then If land$(px,py-1)="J" Then runscript

Until KeyHit(1)

WaitKey

Function setscr(n1)
;��������� ������ � ����� �������� �����
SetBuffer bufb
For n2=0 To 19
 For n3=0 To 31
  DrawBlock tiles,n3*20,n2*20,Asc(Mid$(scr$(n1,n2),n3+1,1))-65
 Next
Next
SetBuffer BackBuffer()
sc=n1
drawbuf
End Function

Function drawbuf()
;���������������
DrawBlock buf,0,0
DrawImage tiles,px*20,py*20,11
Flip
End Function

Function move(dx,dy)
;���������� ����������� ������ �� ������� ������
If dx+px>=0 And dx+px=<31 And dy+py>=0 And dy+py<=19 Then
 Select land$(px+dx,py+dy)
  ;����� ������ ������ �� �����, ���� � ������
  Case "E","H","I"
   px=px+dx
   py=py+dy
   ;����������� ����� ��������
   If py=0 And sc=0 Then
    py=18
    setscr 1
   ElseIf py=19 And sc=1 Then
    py=1
    setscr 0
   End If
   drawbuf
   Delay 100
 End Select
End If
End Function

;������� ������������ ����� ����� �� �����������
Function land$(x,y)
Return Mid$(scr$(sc,y),x+1,1)
End Function

;
Function runscript()
Restore script
Repeat
 Read m$
 If m$="" Then Exit
 ;������ ������������ � ������
 i1=Instr(m$," ")
 i2=Instr(m$,"/",i1+1)
 i3=Instr(m$+"/","/",i2+1)
 ;��������� ����� �������
 cmd$=Mid$(m$,1,i1-1)
 ;��������� ������ �� ���������
 par1=Mid$(m$,i1+1,i2-i1-1)
 par2$=Mid$(m$,i2+1,i3-i2-1)
 par3$=Mid$(m$,i3+1)
 x=par1:y=par2$
 ;Stop
 Select cmd$
  Case "SPEAK"
   ;������� "��������" ������ ����� � ����
   Textwindow par1,par2$
  Case "MOVE"
   ;������� "�������������" ���������� ������ � �������� �����
   drawbuf
   ;������� �� �����������
   x=x-px
   For dx=1 To Abs(x)
    move Sgn(x),0
   Next
   ;����� �� ���������.
   y=y-py
   For dy=1 To Abs(y)
    move 0,Sgn(y)
   Next
  Case "CHANGE"
   ;������� "��������" �������� ���� � ��������� ������������
   ;��������� ����� � ������ �������� �����
   SetBuffer bufb
   DrawBlock tiles,x*20,y*20,Asc(par3$)-65
   SetBuffer BackBuffer()
   ;��������� ����� ������ - ������ ������� � ������
   scr$(sc,y)=Mid$(scr$(sc,y),1,x)+par3$+Mid$(scr$(sc,y),x+2)
  Default
   ;��������� �� ������ (��� ����������� �������)
   DebugLog "Unknow command '"+cmd$+"'"
   Stop
 End Select
Forever
drawbuf
End Function

Function Textwindow(v,txt$)
;���� ������
FlushKeys
drawbuf
;��������� ����
Color 255,255,255
Rect 0,400,640,80
Color 128,128,128
Rect 3,403,637,77
Color 192,192,192
Rect 2,402,636,76
;��������� ���������, � ������ ������ ����������
DrawImage tiles,10,410,9+v
Color 0,0,0
y=410

For n=1 To Len(txt$)
 k$=Mid$(txt$,n,1)
 ;������ - ������ ������ �����
 If k$=" " Then
  ;���� ����� ����� ����� � ����� ������ ������ ���������� - ������� �� ����� ������
  If StringWidth(m$+w$)>490 Then 
   Text 40,y,m$
   y=y+20
   m$=w$
  Else
   ;����� - ��������� ����� � �����
   m$=m$+" "+w$
  End If
  w$=""
 Else
  w$=w$+k$
 End If
Next
;������ �������
Text 40,y,m$+" "+w$
Flip
WaitKey
End Function

;�����
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"
Data "FFFFFFFFFFFFFFBDDBFFFFFFFFFFFFFF"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "HHHHHHHHHHHHHHJJJJHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHJHHJHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"

Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BCBBBCBBBCBBBCBEEBCBBBCBBBCBBBCB"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data "BBABBDBBABBDBBAEEABBDBBABBDBBAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "CBABCBCBABCBCBAEEABCBCBABCBBCABC"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEKEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data "BBCBBDBBCBBDBCBEEBCBDBBCBBDBBCBB"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"

;������ �������
.script
Data "SPEAK 0/����, ��� ����!"
Data "SPEAK 2/��� ��� �������, � ������ �� ������ �������� ��������� � ����������� �������!"
Data "SPEAK 0/�������? � ������ � ����. ������� � �������� ��������� ������, ������ � �� �������� 2-� �����."
Data "CHANGE 15/7/H"
Data "CHANGE 16/7/H"
Data "CHANGE 13/7/J"
Data "CHANGE 18/7/J"
Data "CHANGE 15/2/E"
Data "CHANGE 16/2/E"
Data "MOVE 16/1"
Data "CHANGE 15/2/D"
Data "CHANGE 16/2/D"
Data "MOVE 16/0"
Data "MOVE 16/17"
Data "MOVE 5/17"
Data "CHANGE 5/16/E"
Data "MOVE 5/13"
Data "SPEAK 2/������������."
Data "SPEAK 1/����������. ���� ����� �������, �� ��� ��?"
Data "SPEAK 2/��, � �������."
Data "SPEAK 1/��� ��� ���. � ������ ����."
Data "SPEAK 2/� ������, �� ��������� ����� ��� ������ � ����, ���������� ���������� ������."
Data "SPEAK 1/��, ��� ����� ��� ������������ ������."
Data "SPEAK 2/� ���� �������������� � ���."
Data "SPEAK 1/������, �������. � �������� � ����� ��������, �����, ��������� ��� ���������. � ����, ��� ������������. ������ ����� �������."
Data "MOVE 5/17"
Data "CHANGE 5/16/D"
;������ ������ - ����� �������
Data ""