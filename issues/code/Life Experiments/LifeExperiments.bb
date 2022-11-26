SeedRnd MilliSecs()

Const xres=800, yres=600, fsiz=10, cell=30
;dst - ���� ��������� ������������, dstperiod - ���������� �������������� ������
; ������������, dstpasses - ���-�� ��������, rep - ���� ����������, bnd - ����
; ����������� ����, visible - ����������� ��������� �� ������, explo - �����
; �������������� "������", xper - ������� "������" � ����������, loadorg$ -
; - �������� ��������� �� �����, search - ����� ������ ������������ (0 - ����,
; ����� ������������� ����� - ����������� ���-�� ��������� ��� ������ � ����)

Const dstperiod=3, dstpasses=5, xper=256, bnd=1, visible=1

Const explo=1, xrect=150, yrect=100
;Const dst=1, search=2000, xrect=7, yrect=7
;Const dst=1, loadorg$="Life-Locomotive.png", xrect=0, yrect=0
;Const dst=1, loadorg$="Life-Virus.png", xrect=0, yrect=0

Const fsiz0=1 Shl fsiz,fsiz1=fsiz0-1,fsiz2=(fsiz0 Shl fsiz),fsiz3=fsiz2-1
Const xc=(fsiz0-xres) Shr 1,yc=(fsiz0-yres) Shr 1
Const x1=(xres-xrect) Shr 1,x2=((xres+xrect) Shr 1)-1
Const y1=(yres-yrect) Shr 1,y2=((yres+yrect) Shr 1)-1

Global ib, bnk, dbnk, dend, cellq

Graphics xres,yres,32
SetFont LoadFont ("Arial cyr",14)

buf=CreateImage(xres,yres)
ib=ImageBuffer(buf)

bnk=CreateBank(fsiz2)
dbnk=CreateBank(fsiz2 Shl 2)

Dim neig(8)
k=-fsiz0-1
For n=0 To 7
 If n=3 Then k=-1
 If n=5 Then k=fsiz0-1
 neig(n)=k
 k=k+1+(n=3)
Next

Dim change(64)
For n=0 To 3
 Read m$
 For nn=0 To 8
  change(n*16+nn)=Sgn(Instr(m$,nn))
 Next
Next
Data "3","0145678";�������� �������
Data "23","0245678";������� ��� "������"

;������ ��� �������� ���������������� ���������
Dim org(xrect,yrect)

If search Then f=WriteFile("longlife.txt")

Repeat
 LockBuffer ib
 dend=-4

 If loadorg$="" Then
  For x=x1 To x2
   For y=y1 To y2
    If Rand(0,99)<cell Then putcell x,y: org(x-x1,y-y1)=1 Else org(x-x1,y-y1)=0
   Next
  Next
 Else
  i=LoadImage(loadorg$)
  ii=ImageBuffer(i)
  xsiz=ImageWidth(i)
  ysiz=ImageHeight(i)
  xx=(xres-xsiz) Shr 1
  yy=(yres-ysiz) Shr 1
  For x=0 To xsiz-1
   For y=0 To ysiz-1
    If ReadPixel(x,y,ii)=-1 Then putcell x+xx,y+yy
   Next
  Next
  FreeImage i
 End If

 ;��������������� ������ ��������� ������������: ������ �������� - ���-�� ������
 ; �� ���������� �����, ������ - ������� ���������� ���-�� ������
 Dim dstcq(dstperiod,1)

 Repeat

  UnlockBuffer ib

  If visible Then
   ;����� ��������� ��������, �������� ����� �� �� �����, � � �����
   SetBuffer ib
   Color 0,0,0
   Rect 0,0,100,36
   Color 255,255,255
   Text 0,0,"FPS: "+1000.0/(MilliSecs()-fps)
   ;����� ���-�� ������ � ��������� �� �����
   Text 0,12,"������: "+cellq
   Text 0,24,"���������: "+gen

   SetBuffer FrontBuffer()
   DrawBlock buf,0,0
  End If
 
  ;������� ���������
  gen=gen+1
  ;"�����"
  If explo And (gen Mod xper)=0 Then add=32 Else add=0

  ;�������� ������������
  If dst Then
   ;���� �� ���� ��������
   For n=2 To dstperiod
    If gen Mod n=0 Then
     If cellq=dstcq(n,0) Then
      ;���� ���-�� ������ ��������� � ������� - ������������� ������� ��������
      dstcq(n,1)=dstcq(n,1)+1
      ;���� ���-�� �������� �������� ������� - �������� ����������������,
      ; ���������� ���� ������
      If dstcq(n,1)=dstpasses Then ex=1
     Else
      ;���� ���-�� ������ ���������� - ������� ����������
      dstcq(n,0)=cellq
      dstcq(n,1)=0
     End If
    End If
   Next
  End If

  ;���� ���� ������ ������� ��� ������ ������� ������� - �������� ��� � �������
  ; �� ����� ��������
  If ex Or KeyHit(57) Then ex=0:Exit
  fps=MilliSecs()

  LockBuffer ib

  n=0
  While n<=dend
   pos=PeekInt(dbnk,n)
   k=PeekByte(bnk,pos)
   If change((k And 31)+add) Then PokeByte bnk,pos,k Or 32
   If (k And 31)=0 Then
    PokeInt dbnk,n,PeekInt(dbnk,dend)
    PokeByte bnk,pos,0
    dend=dend-4
   Else
    n=n+4
   End If
  Wend

  n=0
  dend2=dend
  While n<=dend2
   pos=PeekInt(dbnk,n)
   k=PeekByte(bnk,pos)
 
   If k And 32 Then
    If bnd=0 Or (pos>fsiz1 And (pos And fsiz1)>0) Then
     v=(k And 16) Shr 4
     If visible Then
      x=(pos And fsiz1)-xc
      y=(pos Shr fsiz)-yc
      If x>=0 And x<xres And y>=0 And y<yres Then WritePixelFast x,y,v-1,ib
     End If
     v=1-(v Shl 1)
     ;���� ������ ����������, ������� ������ ����������� �� 1, ���� ����������� -
     ; ������������� �� 1
     cellq=cellq+v
     For nn=0 To 7
      addr=(neig(nn)+pos) And fsiz3
      p=PeekByte(bnk,addr)
      If p=0 Then
       dend=dend+4
       PokeInt dbnk,dend,addr
       PokeByte bnk,addr,65
      Else
       PokeByte bnk,addr,p+v
      End If
     Next
     PokeByte bnk,pos,k Xor 48
    End If
   End If
   n=n+4
  Wend 

  If KeyHit(1) Then End
 Forever

 ;������ ����������� � ���� 
 If search>0 And search<=gen Then
  WriteLine f,gen
  For x=0 To xrect-1
   m$=""
   For y=0 To yrect-1
    If org(x,y) Then m$=m$+"0" Else m$=m$+"-"
   Next
   WriteLine f,m$
  Next
  WriteLine f,""
 End If

 ;����� ���������� ������ ��������� ��������� ����� � ����� ���������, � �����
 ; ���������� �������� ���������, ������ � ��������� ������� "����������"
 ; �����
 SetBuffer ib
 Cls
 SetBuffer FrontBuffer()
 FreeBank bnk
 bnk=CreateBank(fsiz2)
 gen=0
 cellq=0
Forever

Function putcell(x,y)
pos=x+xc+((y+yc) Shl fsiz)
For nn=0 To 8
 addr=(neig(nn)+pos) And fsiz3
 p=PeekByte(bnk,addr)
 If p=0 Then
  dend=dend+4
  PokeInt dbnk,dend,addr
  PokeByte bnk,addr,65
 Else
  PokeByte bnk,addr,p+1
 End If
Next
PokeByte bnk,pos,PeekByte(bnk,pos) + 15
If visible Then WritePixel x,y,-1,ib
;���������� �������� ������ �� �������
cellq=cellq+1
End Function