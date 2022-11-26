<code bb>
SeedRnd MilliSecs()

;���������� ���������: xres,yres - ���������� ������, xrect, yrect -
; - ������� ��������������, ������������ �������� � ��������� �������,
; cell - ��������� ������ ������ ����� ��������������(%), fsiz - ��������,
; �������� �� ������ ����. ������ - (2^fsiz)x(2^fsiz)

Const xres=800,yres=600,xrect=600,yrect=400,cell=30,fsiz=10

;����������� ���������: fsiz0 - ������ ����, fsiz1 - ������� �������
; ���������� (X,Y=0..fsiz1), fsiz2 - ������ ������ ��� ���� (fsiz0*fsiz),
; fsiz3 - ������� ������ ������ � ������ (X+Y*fsiz0=0..fsiz3),
; xc,yc - ����� ������ �� ��������� � ���� (���� ������������ ����� ����),
; x1,y1,x2,y2 - ���������� ����� ������������ �������� ��������������

Const fsiz0=1 Shl fsiz,fsiz1=fsiz0-1,fsiz2=(fsiz0 Shl fsiz),fsiz3=fsiz2-1
Const xc=(fsiz0-xres) Shr 1,yc=(fsiz0-yres) Shr 1
Const x1=(xres-xrect) Shr 1,x2=((xres+xrect) Shr 1)-1
Const y1=(yres-yrect) Shr 1,y2=((yres+yrect) Shr 1)-1

;����������, ������������ ��� � ���������, ��� � � ���������.
Global ib, bnk, dbnk, dend

Graphics xres,yres,32

;������� �������� �����
buf=CreateImage(xres,yres)
ib=ImageBuffer(buf)
LockBuffer ib

;���� ��� �������� �����
bnk=CreateBank(fsiz2)
;���� ��� ���������� ������
dbnk=CreateBank(fsiz2 Shl 2)

;���������� ������� ������ ������ ������ ��� ����������� �������
Dim neig(8)
k=-fsiz0-1
For n=0 To 7
 If n=3 Then k=-1
 If n=5 Then k=fsiz0-1
 neig(n)=k
 k=k+1+(n=3)
Next

;���������� ������� ������
Dim change(24)
change(3)=1
For n=16 To 24
 If n<>18 And n<>19 Then change(n)=1
Next

;������� ����� ��� "����������" ������
dend=-4
;���������� �������� ��������������
For x=x1 To x2
 For y=y1 To y2
  ;��������� �����: ��������� �� ������ � ������ �����?
  If Rand(0,99)<cell Then putcell x,y
 Next
Next

Repeat

 ;����������� ������ �� �����
 UnlockBuffer ib
 DrawBlock buf,0,0

 ;������� ���������� ��������� � �������, ����� �� � ���-�� "����������"
 ; ����� �� ����� 
 Color 0,0,0
 Rect 0,0,50,10
 Color 255,255,255
 Text 0,0,1000.0/(MilliSecs()-fps)
 Text 0,8,dend Shr 2
 fps=MilliSecs()

 LockBuffer ib

 ;���� �� ���� "����������" �������
 n=0
 While n<=dend
  ;����������� ������ ������ � �� ���������
  pos=PeekInt(dbnk,n)
  k=PeekByte(bnk,pos)
  ;���� ��� ���������� ���������� (������ ����������� ��� ����������) -
  ; ���������� ������� ���������
  If change(k And 31) Then PokeByte bnk,pos,k Or 32
  ;���� ������ ������ ����� 0 ������� - ��� ��������� �� ������
  ; � ��������� �� �������� ������������
  If (k And 31)=0 Then
   PokeInt dbnk,n,PeekInt(dbnk,dend)
   PokeByte bnk,pos,0
   dend=dend-4
  Else
   n=n+4
  End If
 Wend

 n=0
 ;�������������� ����������, ����� �� ��������� ������������� ������
 dend2=dend
 ;������ ���� �� ���� "����������" �������
 While n<=dend2
  ;����������� ������ ������ � �� ���������
  pos=PeekInt(dbnk,n)
  k=PeekByte(bnk,pos)

  ;�������� �� ������� ���������
  If k And 32
   ;����������� �������� �������������
   v=(k And 16) Shr 4
   ;����������� ��������� ������
   x=(pos And fsiz1)-xc
   y=(pos Shr fsiz)-yc
   ;����� ����������� ������: ���� ��� ���� ��������� - �������� (���� 0 -
   ; ������), ����� - ��������� (���� -1 - �����)
   If x>=0 And x<xres And y>=0 And y<yres Then WritePixelFast x,y,v-1,ib
   ;���������� ���������� �������� ���-�� ������� ��� ������� ������:
   ; ���� ������ �����������, �� +1, ���������� - -1
   v=1-(v Shl 1)
   ;���� �� ���� �������
   For nn=0 To 7
    ;����������� ������ �������� ������ � �� ���������
    addr=(neig(nn)+pos) And fsiz3
    p=PeekByte(bnk,addr)
    ;���� ������������ ������� ���-�� ������� � ������, � ������� �������
    ; ������������ ����� 0 - ������� �� � ���� "����������" �
    ; ������������� �������� ����� �������� ������ 1
    If p=0 Then
     dend=dend+4
     PokeInt dbnk,dend,addr
     PokeByte bnk,addr,65
    Else
     ;����� - ������ ��������� �������� ���-�� �������
     PokeByte bnk,addr,p+v
    End If
   Next
   ;����� �������� ���������� � �������� ��������� (48=16+32)
   PokeByte bnk,pos,k Xor 48   
  End If  
  n=n+4
 Wend

 ;���� ������ ������� "Esc" - �������
Until KeyHit(1)

;�������, ����������� ������ � ������������ (x,y) � ������������
; �������������� ��������.
Function putcell(x,y)

;���������� ������ ������ (� ������������ � ������� ������ ���. ����)
pos=x+xc+((y+yc) Shl fsiz)
;��������� � ���� ������� � ������
For nn=0 To 8
 ;����������� ������ ������ (������� AND ����������� ����, ��������
 ; ����� � �������� 0..fsiz3)
 addr=(neig(nn)+pos) And fsiz3
 p=PeekByte(bnk,addr)
 ;���������� �������� ���-�� ������� ������ �� 1. ���� ������� �������������
 ; ����� 0, ������ ������ �� �������� � ���� "����������", ������� �������.
 If p=0 Then
  ;������ ����� �������� � ����� 4 �����
  dend=dend+4
  PokeInt dbnk,dend,addr
  PokeByte bnk,addr,65
 Else
  PokeByte bnk,addr,p+1
 End If
Next
;������� � ���� ��������� �� ����� ������ ������� "�������������"
; ����� 1 (��� ��� � ���������� ����� ��� �������� ������� "���-��
; �������")
PokeByte bnk,pos,PeekByte(bnk,pos) + 15
;����������� ������ � �������� ������
WritePixel x,y,-1,ib
End Function
</code><noinclude>[[���������:���]]</noinclude>