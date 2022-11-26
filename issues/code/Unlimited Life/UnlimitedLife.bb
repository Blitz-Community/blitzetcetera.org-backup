;��������� � ������������ ������
Type ptr
 Field nxt.ptr[3] ;��������� ��������� � ��������
 Field prev.ptr ;���������� ��������� � ��������
 Field prevpos ;������������ ����������� ���������
 Field neig.ptr[7] ;������ ������� (��� ������)
 Field x,y,nq; ���������� � ���������� �������
End Type

;��������� �� ������, ��� ������� �������� ��������� ���������
Type chang
 Field p.ptr
End Type

Const loadorg$="Life-Locomotive.png"
;Const loadorg$="Life-Virus.png"
Const xres=800,yres=600

Global cellq, scrx, scry, ib

;�������� ������� ���������� �� 3 ������
Dim pmark.ptr(3)
For n=0 To 3
 pmark(n)=New ptr
Next

Graphics xres,yres
SetFont LoadFont ("Arial cyr",14)

Dim change(24)
For n=0 To 1
 Read m$
 For nn=0 To 8
  change(n*16+nn)=Sgn(Instr(m$,nn))
 Next
Next
Data "3","0145678"

i=CreateImage(xres,yres)
ib=ImageBuffer(i)

i2=LoadImage(loadorg$)
ib2=ImageBuffer(i2)
xsiz=ImageWidth(i2)
ysiz=ImageHeight(i2)
For x=0 To xsiz-1
 For y=0 To ysiz-1
  If ReadPixel(x,y,ib2) And 255 Then
   ;������ ����������� ������ - ��������� ����� ��� �������� ���������
   If cellq=0 Then
    cell.ptr=New ptr
    ptrq=ptrq+1
    cell\x=400
    cell\y=300
    xx=x
    yy=y
   End If
   newborn findcell(cell,x-xx,y-yy)
  End If
 Next
Next
FreeImage i2

Repeat
 
 DrawBlock i,0,0

 gen=gen+1
 ;������ ���� ������������ � ����� ������ (����� �� ��������� �� ����)
 MoveMouse xres Sar 1,yres Sar 1

 If cellq=0 Then Exit

 ;��� ������, ������������ ����������, ����������� � ������ �2
 For ch.chang=Each chang
  If change(ch\p\nq) Then Insert ch\p After pmark(2)
 Next
 Delete Each chang

 ;�������� ��������� ���� ������ �� ������ �2
 cell=pmark(2)
 Repeat
  cell=After cell
  If cell=pmark(3) Then Exit
  If cell\nq<16 Then
   newborn cell
  Else
   ;��� ���� ������� - ���������� �� �������� �������
   For n=0 To 7
    cell2.ptr=cell\neig[n]
    cell2\nq=cell2\nq-1
    ;��������� ������ � ������ �����, �������, ��������, ������� ���� ���������
    If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
   Next
   ;���������� ���� �������������
   cell\nq=cell\nq And 15
   WritePixel scrx+cell\x,scry+cell\y,0,ib
   cellq=cellq-1
   ;��������� ������������ ������ � ������ ������������ ������������ �����
   If change(cell\nq) Then ch.chang=New chang: ch\p=cell
  End If
 Forever

 ;��� ������ �� ������ �2 ��������� � ������ �1 (���������������)
 Insert pmark(2) Before pmark(3)

 ;������������ �������� ������� ����
 dx=MouseX()-xres Sar 1
 dy=MouseY()-yres Sar 1
 ;���� ������ ���������, �� ���������� ����������� ������ (���� 1-� ������)
 If dx<>0 Or dy<>0 Then
  scrx=scrx+dx
  scry=scry+dy

  SetBuffer ib
  Cls
  SetBuffer FrontBuffer()

  cell=pmark(1)
  Repeat
   cell=After cell
   If cell=pmark(2) Then Exit
   If cell\nq And 16 Then WritePixel scrx+cell\x,scry+cell\y,-1,ib
  Forever

 End If

 SetBuffer ib
 Color 0,0,0
 Rect 0,0,100,36
 Color 255,255,255
 Text 0,0,"FPS: "+1000.0/(MilliSecs()-fps)
 Text 0,12,"������: "+cellq
 Text 0,24,"���������: "+gen
 fps=MilliSecs()
 SetBuffer FrontBuffer()
 
Until KeyHit(1)
End

;������� ������ ������ � �������� �� ��������� ����� � ��������
;���� ������ �� ����������, ��� ��������� ������ � �������� ����������
Function findcell.ptr(cell.ptr,x,y)
;���� �������� �������, �� ��������� - ��������� �����
If x=0 And y=0 Then Return cell
;���������� ���������� ������� ������ (�� ������, ���� �� �������� �������)
xx=x+cell\x
yy=y+cell\y
pmax=1 ;������� ������ � ��������
;������ ���� - ������ �����
Repeat
 ;���������� ������ ��������� ������, ���� ���������� ������� ��������
 If cell\prev=Null Then
  p.ptr=New ptr
  Insert p After pmark(0)
  ;������� ������������ ���������� ������� ������
  pos=(x<0)+(y<0) Shl 1
  p\nxt[pos]=cell
  cell\prev=p
  cell\prevpos=pos
 Else
  ;����� - ������� �� ����� ������� ������� � ��������
  pos=cell\prevpos
  p.ptr=cell\prev
 End If
 ;��������� ��������� � ������������ � ������������
 If pos And 1 Then x=x+pmax
 If pos And 2 Then y=y+pmax
 ;��������� ������
 pmax=pmax Shl 1
 cell=p
 ;�����, ���� ���������� �����, ������ ����� ���������� �� �������
Until x>=0 And y>=0 And x<pmax And y<pmax

;������ ���� - �����
Repeat
 ;��������� ������
 pmax=pmax Shr 1
 ;����������� �����������
 pos=((x And pmax)=pmax)+((y And pmax)=pmax) Shl 1

 ;�������� ������ ���������, ���� ����� �����������
 If cell\nxt[pos]=Null Then
  p.ptr=New ptr
  Insert p After pmark(0)
  cell\nxt[pos]=p
  p\prev=cell
  p\prevpos=pos
  ;���� ������� ������ (��������� 1-�� ������), �� ���������� �� � ������ �1 �
  ; ����������� ����������� ����������
  If pmax=1 Then
   Insert p After pmark(1)
   p\x=xx
   p\y=yy
  End If
 End If
 cell=cell\nxt[pos]
 ;���� ��������� ��� �������� (������� ������) - �����
Until pmax=1
Return cell
End Function

;������� �������� ����� ������
Function newborn(cell.ptr)
;�����, ����������� ������� � ���������� �� �������� ���������� �������
For xx=-1 To 1
 For yy=-1 To 1
  If xx Or yy Then
   cell2.ptr=findcell(cell,xx,yy)
   cell2\nq=cell2\nq+1
   ;��������� ������ � ������ �����, �������, ��������, ������� ���� ���������
   If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
   cell\neig[n]=cell2
   n=n+1
  End If
 Next
Next
;��������� ���� �������������
cell\nq=cell\nq Or 16
;��������� ������������ ������ � ������ ������������ ������������ �����
If change(cell\nq) Then ch.chang=New chang: ch\p=cell
WritePixel cell\x+scrx,cell\y+scry,-1,ib
cellq=cellq+1
End Function