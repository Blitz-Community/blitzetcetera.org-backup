Const fw=150,fw2=fw/2,fw4=fw2/2,fh=20,maxsiz=512
Global kr$=Chr$(10)

Dim fld(maxsiz-1,maxsiz-1)
Dim com(255,15,2)

Global win=CreateWindow("",0,0,640,480,0,5)
Global lastfile$="untitled.prg"
seltitle 
MaximizeWindow win

filemenu=CreateMenu("���������",0,WindowMenu(win))
helpmenu=CreateMenu("������",1,WindowMenu(win))
CreateMenu("�����",0,filemenu)
CreateMenu("���������...",1,filemenu)
CreateMenu("���������...",2,filemenu)
CreateMenu("��������� ���...",3,filemenu)
CreateMenu("",-1,filemenu)
CreateMenu("���������...",4,filemenu)
CreateMenu("",-1,filemenu)
CreateMenu("�����",5,filemenu)
CreateMenu("�������",10,helpmenu)
CreateMenu("� ���������",11,helpmenu)
UpdateWindowMenu Win

Global runslowb=CreateButton("��������� ��������",0,0,fw,fh,win)
Global runfastb=CreateButton("��������� ������",0,fh,fw,fh,win)
Global clrb=CreateButton("�����",0,fh*2,fw,fh,win)
Global txta=CreateTextArea(0,fh*3,fw,ClientHeight(win)-fh*4,win)
Global current=CreateLabel("",0,ClientHeight(win)-fh,fw,fh,win,3)
Global xres=64,yres=64,skip=9,sqs,mdelay=200,reset=0,begpos=0
Global cnv,tx,ty,tdir,change,sym,col

createfield

Repeat
 id=WaitEvent()
 If id=$803 Then exitprogram
 If id=$1001 Then
  Select EventData()
   Case 0:SetTextAreaText txta,""
   Case 1:loadfile RequestFile("�������� �����...","prg",0)
   Case 2:savefile lastfile$
   Case 3:savefile RequestFile("���������� �����...","prg",1)
   Case 4:params
   Case 5:exitprogram
   Case 10    
    file$=SystemProperty$("appdir")+"temp.htm"
    Restore
    f=WriteFile (file$)
     Read m$
     Repeat
      i=Instr(m$,"^")
      If i=0 Then Exit
      m$=Left$(m$,i-1)+Chr$(34)+Mid$(m$,i+1) 
     Forever
     WriteLine f,m$
    CloseFile f
    x=ClientWidth(Desktop())/2-300
    y=ClientHeight(Desktop())/2-200
    w=CreateWindow ("�������",x,y,600,400,Desktop(),1)
    h=CreateHtmlView(0,0,ClientWidth(w),ClientHeight(w),w)
    ;Stop
    HtmlViewGo h,"file://"+file$
    Repeat
     If ActiveWindow()=win Then ActivateWindow w
    Until WaitEvent()=$803 And EventSource()=w
    FreeGadget w
    DeleteFile file$
   Case 11:Notify "BlitzPlus Turmit"+kr$+"����� ���������: ������ ��������"+kr$+"MattMerk@mail.ru"
  End Select
 End If
 If id=$401 Then
  Select EventSource()
   Case txta
    change=1
   Case runslowb,runfastb
    runprog EventSource()=runslowb
   Case clrb
    tdir=0
    clearfield
  End Select
 End If
 If id=$201 Then
  x=Floor(EventX()/sqs)
  y=Floor(EventY()/sqs)
  If x>=0 And x<xres And y>=0 And y<yres Then
   paintsq
   tx=x
   ty=y
   showturmit
   FlipCanvas cnv
  End If
 End If
Forever
End

Data "<HTML><BODY><CENTER><h2>BlitzPlus Turmit</h2><h3>����� ���������: <A HREF=mailto:MattMerk@mail.ru>������ ��������</A></h3><h3>���� ���������: <A HREF=^http:\\blitzetc.boolean.name^ TARGET=^_blank^>http:\\blitzetc.boolean.name</A></h3><h3>������� ������</h3><P ALIGN=JUSTIFY>���������� ���� ��������� ���� NxM ������. ��� ��� ������ ��������� ����� ������ (����� 15). � ����� �� ��� ��������� Turmit, �� ������������ ����� �������� � ��������� ������. Turmit �������� �� ��������� � ����� ������������ �� ���� � ����������� ������ � ���� �� 16 ������, �������� ����. ���� ��������� (��� ���), �� ����, ������ �� ��� �������, Turmit ���������� � ������ �������.</P><h3>���������������� Turmit'�</h3><P ALIGN=JUSTIFY>��������� ��� Turmit'� ������ �� ��������� � ������� �� ������ �����. ������ ������ ������� �� 5 ���������, ������ ����� ������: ������, ������� ����, ����� ����, ��� ��������, ��������� ������. ���������� ���������� � ������ ������: ������ ��� �������� ������������, Turmit ������ ������, �� ������� ��������� � ^����� ����^, �������������� �� 90 �������� ����� (��� �������� = -1), ������ (��� �������� = 1), �� ������ ����������� (��� �������� = 0) � ������������ ������. ����� ����� � ��������� ������ ������ � ��������, ������ ^���������� �������^, � ������� ����� ^������� ����^ ����� ����� ������ ��� Turmit'��. ���� ����� ������ �������, �� ��� ����������� ����������, ���� ��� - ��������� �����������.</P><h3>�������</h3><P ALIGN=JUSTIFY>���� �� ����� ���������� ��������� ������ ������� �������, Turmit �������� � ����� ���������� ���������� - ����������� � ����� ����� ���������� ������� �������. ����� �� ����� ������ - ESC. �������� ESC ����� ����� �������� ���������� ���������.</P></CENTER></BODY></HTML>"

Function seltitle() 
 filename$=lastfile$
 Repeat
  i=Instr(filename$,"\",i+1)
  If i=0 Then Exit
  ii=i
 Forever
 If ii>0 Then filename$=Mid$(filename$,ii+1)
 SetGadgetText win,"BlitzPlus Turmit: "+filename$
End Function

Function createfield()
xsiz=Floor((ClientWidth(win)-fw)/xres)*xres
ysiz=Floor(ClientHeight(win)/yres)*yres
If xsiz<ysiz Then 
 sqs=xsiz/xres
Else
 sqs=ysiz/yres
End If
If sqs<1 Then sqs=1
xsiz=sqs*xres
ysiz=sqs*yres

x=(ClientWidth(win)+fw-xsiz)/2
y=(ClientHeight(win)-ysiz)/2
If cnv<>0 Then FreeGadget cnv
cnv=CreateCanvas(x,y,xsiz+1,ysiz+1,win,2)
SetBuffer CanvasBuffer(cnv)
ActivateGadget cnv
Delay 100
clearfield
End Function

Function showturmit()
Color 0,0,128
If sqs>=3 Then
 For n=1 To sqs-1
  d=Abs(n*2-sqs)
  k=(tdir>=2) 
  d=sqs-d+(2*d-sqs)*k-1
  k=1+(sqs-1)*k
  If tdir Mod 2 Then
   x1=n
   y1=k
   x2=n
   y2=d
  Else
   x1=k
   y1=n
   x2=d
   y2=n
  End If
  Line tx*sqs+x1,ty*sqs+y1,tx*sqs+x2,ty*sqs+y2
 Next
Else
 Rect tx*sqs,ty*sqs,sqs,sqs
End If
End Function

Function loadfile(file$)
If file$<>"" Then
 SetTextAreaText txta,""
 If file$<>"" Then
  seltitle
  f=ReadFile(file$)
  Repeat
   m$=ReadLine$(f)
   If Eof(f) Then
    AddTextAreaText txta,m$
    Exit
   Else
    AddTextAreaText txta,m$+kr$
   End If
  Forever
  CloseFile f
 End If
 lastfile$=file$
 seltitle
End If
End Function

Function savefile(file$)
If file$<>"" Then
 seltitle
 f=WriteFile(file$)
 m$=TextAreaText(txta)
 i=-1
 Repeat
  i=Instr(m$,kr$,i+2)
  If i=0 Then Exit
  m$=Left$(m$,i-1)+Chr$(13)+Mid$(m$,i)
 Forever
 WriteLine f,m$
 CloseFile f
 lastfile$=file$
 change=0
 seltitle
End If
End Function

Function runprog(dl)
If reset Then tdir=0: clearfield

For n1=33 To 255
 For n2=0 To 15
  com(n1,n2,2)=0
 Next
Next

m$=TextAreaText(txta)+kr$
;Stop
Repeat
 i=Instr(m$,kr$)-1
 If i=-1 Then Exit
 m1$=Mid$(m$,1,i)+" " 
 m$=Mid$(m$,i+2)
 ii=0
 If m1$<>" " Then
  For n=1 To 5  
   i=Instr(m1$," ",ii)
   If i=0 Then Exit
   m2$=Mid$(m1$,ii,i-ii)
   Select n
    Case 1:n1=Asc(m2$):If outbounds(n1,34,255) Then ex=1:Exit
    Case 2:n2=m2$:If outbounds(n2,0,15) Then ex=1:Exit
    Case 3,4:com(n1,n2,n-3)=m2$
    Case 5:com(n1,n2,2)=Asc(m2$)
   End Select
   ii=i+1
  Next
  If i=0 Then ex=1:Exit
  If outbounds(com(n1,n2,0),0,15) Then ex=1:Exit
  If outbounds(com(n1,n2,1),-1,1) Then ex=1:Exit
  If outbounds(com(n1,n2,2),34,255) Then ex=1:Exit
  If sym=0 Then sym=n1: col=n2
  If ex Then Exit 
 End If
Forever

;Stop

Repeat
 If ex Then Notify "������ � ��������� ("+m1$+")",True:Exit

 tdir=(tdir+com(sym,col,1)+4) Mod 4
 fld(tx,ty)=com(sym,col,0)
 paintsq
 d1=(tdir Mod 2)
 d2=1-(tdir And 2)
 tx=(xres+tx+(1-d1)*d2) Mod xres
 ty=(yres+ty+d1*d2) Mod yres

 h=(h+1) Mod (skip+1)

 ActivateGadget cnv
 
 If sbs Or dl Then
  tim=MilliSecs()+mdelay
  Repeat
   If KeyDown(1) Or (dl And tim<MilliSecs() And sbs=0) Then
    If sbs Then
     sbs=0
     Repeat:Until KeyDown(1)=0
    End If
    Exit 
   End If   
   ActivateGadget cnv
   If KeyDown(57) Then sbs=1
  Until KeyDown(57)
  Repeat:Until KeyDown(57)=0
  h=0
 End If

 newsym=com(sym,col,2)
 If newsym=0 Then h=0
 If h=0 Then 
  showturmit
  SetGadgetText current,Chr$(sym)+" "+col
  FlipCanvas cnv 
 End If
 sym=newsym
 col=fld(tx,ty)
 If sym=0 Then Notify "��������� ���������":Exit
 
 If KeyDown(1) Then Exit
 If KeyDown(57) Then sbs=1


Forever
FlipCanvas cnv
End Function

Function clearfield()
sym=0
For x=0 To xres-1
 For y=0 To yres-1
  fld(x,y)=15
 Next
Next
ClsColor 255,255,255
Cls
If sqs>2 Then
 Color 0,0,0
 For y=0 To yres
  Line 0,y*sqs,ClientWidth(cnv),y*sqs
 Next
 For x=0 To xres
  Line x*sqs,0,x*sqs,ClientHeight(cnv)
 Next
End If
If begpos Then
 tx=0
 ty=0
Else
 tx=Floor(xres/2)
 ty=Floor(yres/2)
End If
showturmit
FlipCanvas cnv
End Function

Function setcol(col)
dcol=85*(col>=8)
Color dcol+170*((col And 4) Sar 2),dcol+170*((col And 2) Sar 1),dcol+170*(col And 1)
End Function

Function bounds(v,v1,v2)
If v<v1 Then Return v1
If v>v2 Then Return v2
Return v
End Function

Function paintsq()
setcol fld(tx,ty)   
If sqs<3 Then
 Rect tx*sqs,ty*sqs,sqs,sqs
Else
 Rect tx*sqs+1,ty*sqs+1,sqs-1,sqs-1
End If
End Function

Function params()
xsiz=320
ysiz=175
pwin=CreateWindow("���������",(GadgetWidth(Desktop())-xsiz)/2,(GadgetHeight(Desktop())-xsiz)/2,xsiz,ysiz,win,1)
CreateLabel("���������� ����:",5,5,95,20,pwin)
CreateLabel("��������� turmit'� ��� ������:",5,25,165,20,pwin)
CreateLabel("�������� ��� ��������� ����������:",5,45,205,20,pwin)
CreateLabel("���-�� ��������� ����� ��� ������� ����������:",5,65,265,20,pwin)
resetb=CreateButton("����� ����� �����������",5,85,250,20,pwin,2)
SetButtonState resetb,reset

xresf=CreateTextField(105,5,30,16,pwin)
yresf=CreateTextField(140,5,30,16,pwin)
SetGadgetText xresf,xres
SetGadgetText yresf,yres
incorner=CreateButton("� ����",170,25,50,16,pwin,3)
incenter=CreateButton("� ������",225,25,70,16,pwin,3)
SetButtonState incorner*(begpos=1)+incenter*(begpos=0),True
delayf=CreateTextField(215,45,30,16,pwin)
SetGadgetText delayf,mdelay
skipf=CreateTextField(275,65,30,16,pwin)
SetGadgetText skipf,skip
okbutton=CreateButton("���������",55,115,80,25,pwin)
cancelbutton=CreateButton("��������",185,115,80,25,pwin)

Repeat
 id=WaitEvent()
 If id=$803 Then ex=1:Exit
 If id=$401 Then
  e=EventSource() 
  Select e
   Case okbutton:Exit
   Case cancelbutton:ex=1:Exit
  End Select
 End If
Forever

If ex=0 Then
 newxres=bounds(TextFieldText(xresf),2,min(maxsiz,ClientWidth(cnv)-1))
 newyres=bounds(TextFieldText(yresf),2,min(maxsiz,ClientHeight(cnv)-1))
 If xres<>newxres Or yres<>newyres Then 
  xres=newxres
  yres=newyres
  createfield
 End If

 newskip=bounds(TextFieldText(skipf),0,1000)
 If newskip<>skip Then skip=newskip

 newdelay=bounds(TextFieldText(delayf),0,1000)
 If newdelay<>mdelay Then mdelay=newdelay

 If ButtonState(incorner)<>begpos Then begpos=ButtonState(incorner) 

 If ButtonState(resetb)<>reset Then reset=ButtonState(resetb)

End If
FreeGadget pwin
End Function

Function min(v1,v2)
If v1<v2 Then Return v1 Else Return v2
End Function

Function exitprogram()
If change Then 
 Select Proceed("������� ��������� �� ���������!"+kr$+"������ �� �� ��������� ��?")
  Case 0:ex=1
  Case 1:savefile lastfile$:ex=1
 End Select
Else
 ex=1
End If
If ex Then FreeGadget win:End
End Function

Function outbounds(v,v1,v2)
If v<v1 Or v>v2 Then Return 1
End Function

; 4-line RND -------------------------------------------------------------------

;A 15 2 0 B
;B 15 2 1 A
;A 2 15 -1 B
;B 2 15 0 A

; 3-Color Rnd ------------------------------------------------------------------


;A 15 9 1 A
;A 9 12 -1 A
;A 12 15 0 A

; fractal ----------------------------------------------------------------------

;Z 15 15 -1 Y
;Y 15 15 -1 X
;X 15 7 -1 W
;W 15 14 -1 V
;V 15 6 0 M

;M 15 12 0 A
;M 14 13 0 A
;M 10 10 1 N
;M 8 8 1 N
;M 7 7 1 N
;M 2 2 1 N
;N 15 15 1 N
;N 14 14 1 N
;N 10 10 1 N
;N 8 8 1 N
;N 7 7 1 N
;N 6 5 0 M
;N 5 4 0 M
;N 4 3 0 M
;N 3 2 0 K
;N 2 2 1 N

;K 15 15 0 L
;K 14 15 1 O
;K 10 2 1 O
;K 8 7 1 O
;K 7 9 1 R
;K 2 2 0 L
;L 15 15 0 K
;L 8 7 0 T
;L 7 9 0 Q
;L 2 6 0 M

;T 15 15 0 O
;T 2 2 1 O

;O 15 15 -1 P
;O 2 2 -1 P
;O 7 9 -1 Q
;P 15 14 0 L
;P 7 9 0 R
;P 2 10 0 L

;A 15 15 -1 B
;A 14 14 -1 B
;A 10 10 -1 C
;A 8 8 -1 B
;A 7 7 -1 B
;A 2 2 -1 C
;B 15 15 -1 A
;B 14 14 -1 A
;B 12 15 0 D
;B 13 14 0 D
;B 10 10 -1 A
;B 8 8 -1 A
;B 7 7 -1 A
;B 2 2 -1 A
;C 15 15 -1 C
;C 14 14 -1 C
;C 13 14 0 N
;C 12 15 0 N
;C 10 10 -1 C
;C 8 8 -1 C
;C 7 7 -1 C
;C 2 2 -1 C
;D 15 15 -1 E
;D 14 14 -1 E
;D 10 10 1 N
;D 8 8 -1 E
;D 7 7 -1 E
;D 2 2 1 N
;E 15 15 -1 F
;E 8 8 -1 F
;E 7 7 -1 F
;E 2 2 -1 F
;F 15 15 -1 G
;F 14 14 -1 G
;F 10 10 -1 G
;F 8 8 -1 G
;F 7 7 -1 G
;F 2 2 -1 G
;G 15 2 -1 N
;G 14 10 -1 N

;Q 15 15 0 Q
;Q 14 15 0 Q
;Q 10 2 0 Q
;Q 9 8 0 L
;Q 2 2 0 Q
;R 15 15 0 R
;R 14 15 0 R
;R 10 2 0 R
;R 9 8 0 K
;R 2 2 0 R