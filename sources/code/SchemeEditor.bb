Type obj
 Field x#, y#, xsiz#, ysiz#, txt$, style
End Type

Type dot
 Field x#, y#, attobj.obj, txt$, style
End Type

Type lin
 Field dot1.dot, dot2.dot, txt$, style
End Type

Type selobj
 Field o.obj
End Type

Type seldot
 Field d.dot
 Field o.obj
 Field l.lin
 Field proc
 Field num
End Type

Global mx#, my#, mb, mh, mcur, sel, selected, tool, lbmode, fbnk
Global gsiz#=16, grid=1, sen=6, cnvsen#=sen, sellin.lin, rsd.seldot
Global scrx#, scry#, ft, ax1#, ay1#, ax2#, ay2#, curmz
Global mgn#=1, dmgn#=1.5, minmgn#=.1,maxmgn#=10

Global xres, yres, cnv, bi, slb, win, cm, fnt, fnthgt, lastfile$
Global lwid=100, lbhei=20, kr$=Chr$(10)

Const ext$="sch",smax=100, maxmes=30

Dim scol(smax),sbg(smax)

win=CreateWindow("",20,40,600,400,0,7)
SetMinWindowSize win,lwid+100,220
MaximizeWindow win
slb=CreateListBox(0,0,10,10,win)
cm=CreateTextField(0,0,10,10,win)

wmenu=WindowMenu(win)
parent=wmenu
Restore messages
Dim mtxt$(maxmes)

For n=0 To 2
 n1=0
 Repeat
  Read m$
  If Left$(m$,3)="***" Then Exit
  Select n
   Case 0
    mtxt$(n1)=Mid$(m$,Instr(m$,"-")+1)
    n1=n1+1
   Case 1
    Select m$
     Case "{": parent=old
     Case "}": parent=wmenu
     Default
      old=CreateMenu(m$,i,parent)
      ;old=CreateMenu(m$+" ("+i+")",i,parent)
      If m$<>"" And parent<>wmenu Then i=i+1;: m$=m$+" ("+i+")"
    End Select 
   Case 2
    AddGadgetItem(slb,m$)
    Read scol(n1),sbg(n1)
    n1=n1+1
  End Select
 Forever
Next
CloseFile f
UpdateWindowMenu win
SelectGadgetItem slb,0
SetGadgetText win,mtxt$(2)+"[без имени]"

Restore cursors
Const curq=4
Dim cl(curq,5,3)
Dim clq(curq)
For n1=1 To curq
 Read clq(n1)
 For n2=0 To clq(n1)
  For n3=0 To 3
   Read cl(n1,n2,n3)
  Next
 Next
Next

Restore fnt
Dim fnttop(18)
Dim fntsiz(18)
For n=1 To 18
 Read fnttop(n),fntsiz(n)
Next

updatemgn
updatewindow

tool=1
Color 0,0,0

Repeat
 DrawBlock bi,0,0

 mx#=scr2cnvx#(MouseX(cnv))
 my#=scr2cnvy#(MouseY(cnv))

 Repeat
  e=WaitEvent() 
  Select e
   Case $101: If keypress() Then Exit
   Case $401: btpress
   Case $201,$202,$203:If EventSource()=cnv Then Exit
   Case $204:Exit
   Case $802: updatewindow
   Case $803: If askforsaving()>-1 Then End
   Case $1001: menuitem
  End Select
 Forever
 selection

 mb=mouse()
 If mb Then
  If sel=2 Then
   l.lin=findlin(mx#,my#)
   If l<>sellin Then remsel:If l<>Null Then selection
  Else
   o.obj=findobj(mx#,my#)
   If o<>Null Then
    For so.selobj=Each selobj
     If so\o=o Then Exit
    Next  
   Else
    so.selobj=Null
   End If

   If so=Null Then
    d.dot=finddot(mx#,my#)
    If d<>Null Then
     For sd.seldot=Each seldot
      If sd\d=d Then Exit
     Next
    Else
     sd.seldot=Null
    End If
    If sd=Null Then
     remsel
     selection
    End If
   End If

  End If
  x1#=scr2cnvx#(MouseX(cnv))
  y1#=scr2cnvy#(MouseY(cnv))
  If tool<3 And mb<3 Then
   x1#=move#(x1#)
   y1#=move#(y1#)
  Else
   remsel
  End If

  k=0
  Repeat

   DrawBlock bi,0,0
   hlsel   
   mx#=scr2cnvx#(MouseX(cnv))
   my#=scr2cnvy#(MouseY(cnv))
   If tool=3 Or sel+mb=1 Or mb=3 Then
    x2#=mx#
    y2#=my#
   Else
    x2#=move#(mx#)
    y2#=move#(my#)
   End If
   dx#=x2#-x1#
   dy#=y2#-y1#
   Select mb+sel*10
    Case 3,13,23,33,43
     scrx#=scrx#-dx#
     scry#=scry#-dy#
     If dx#<>0 And dy#<>0 Then k=k+1
     redraw
     ;Text 0,0,x1#+","+y1#+","+x2#+","+y2#
    Case 1
     dashrect x1#,y1#,x2#,y2#,4
    Case 2,12
     Select tool
      Case 1
       If sel=1 Then
        For so.selobj=Each selobj
         xsiz2#=so\o\xsiz+dx#
         ysiz2#=so\o\ysiz+dy#
         If xsiz2#<=0 Then xsiz2#=gsiz#:dx#=gsiz#-so\o\xsiz
         If ysiz2#<=0 Then ysiz2#=gsiz#:dy#=gsiz#-so\o\ysiz
         cnvRect so\o\x,so\o\y,xsiz2#,ysiz2#,0
        Next
       Else
        o.obj=New obj
        o\x=x1#
        o\y=y1#
        o\style=SelectedGadgetItem(slb)
        so.selobj=New selobj
        so\o=o
        sel=1
        selq=1
       End If
      Case 2
       d.dot=New dot
       d\x=x1#
       d\y=y1#
       d\style=SelectedGadgetItem(slb)
       remsel
       sd.seldot=New seldot
       sd\d=d
       sel=3
       mb=1
      Case 3
       dashrect x1#,y1#,x2#,y2#,4
     End Select    
    Case 11,21,31,41
     moveditemsshow dx#,dy# 
    Case 22
     d=New dot
     d\x=x1#
     d\y=y1#
     l.lin=New lin
     l\style=sellin\style
     l\dot1=d
     l\dot2=sellin\dot2
     l\txt$=sellin\txt$
     sellin\dot2=d
     remsel
     sd.seldot=New seldot
     sd\d=d
     sel=3
     mb=1
    Case 32
     d2.dot=finddot(x2#,y2#)
     For sd.seldot=Each seldot
      x#=sd\d\x
      y#=sd\d\y
      cnvLine x#,y#,x2#,y2#
      If d2=sd\d Then d2=Null
     Next
     If d2<>Null Then Rect cnv2scrx(d2\x)-3,cnv2scry(d2\y)-3,7,7,0     
   End Select
   drawcur
   ;Text 0,0,"Dragging!!!"
   FlipCanvas cnv
  Until mouse()=0 Or mb=4

  Select sel*10+mb
   Case 1,2,4,14,24,34,44
    ;Stop
    If x2#<x1# Then z#=x2#:x2#=x1#:x1#=z#
    If y2#<y1# Then z#=y2#:y2#=y1#:y1#=z#
    If mb=4 Then
     If MouseZ()<curmz Then
      mgn#=mgn#*dmgn#
     Else
      mgn#=mgn#/dmgn#
     End If
     updatemgn
     scrx#=x2#-MouseX(cnv)/mgn#
     scry#=y2#-MouseY(cnv)/mgn#
    ElseIf mb=2 Or tool=3
     mgn#=min#(xres/Abs(x2#-x1#),yres/Abs(y2#-y1#))
     updatemgn
     scrx#=(x1#+x2#)/2-0.5*xres/mgn#
     scry#=(y1#+y2#)/2-0.5*yres/mgn#
    Else
     remsel
     For o.obj=Each obj
      If o\x>x1# And o\x+o\xsiz<x2# Then
       If o\y>y1# And o\y+o\ysiz<y2# Then
        so.selobj=New selobj
        so\o=o
       End If
      End If
     Next
     If First selobj<>Null And First seldot=Null Then 
      sel=1
      selectalsodots 0
      selected=1
     End If 
     For d.dot=Each dot
      If d\x>=x1# And d\x=<x2# And d\y>=y1# And d\y=<y2# Then
       sd.seldot=New seldot
       sd\d=d
      End If
     Next
     If First seldot<>Null Then sel=3+sel:selected=1
    End If
   Case 12
    For so.selobj=Each selobj
     o.obj=so\o
     xsiz2#=o\xsiz+dx#
     ysiz2#=o\ysiz+dy#
     For d.dot=Each dot
      If d\attobj=o Then 
       d\x=(d\x-o\x)*xsiz2#/o\xsiz+o\x
       d\y=(d\y-o\y)*ysiz2#/o\ysiz+o\y
      End If
     Next
     o\xsiz=xsiz2#
     o\ysiz=ysiz2#
    Next
    d.dot=First dot
    setdots
    selected=1
    changed=1
   Case 11,21,31,41
    For so.selobj=Each selobj
     so\o\x=so\o\x+dx#
     so\o\y=so\o\y+dy#
     For d.dot=Each dot
      If d\attobj=so\o Then 
       d\x=d\x+dx#
       d\y=d\y+dy#
       For sd.seldot=Each seldot
        If sd\d=d Then Delete sd
       Next
      End If
     Next
    Next

    If First selobj<>Null And First seldot=Null Then
     sel=1
     selected=1
    End If

    For sd.seldot=Each seldot     
     sd\d\x=move#(sd\d\x+dx#)
     sd\d\y=move#(sd\d\y+dy#)
    Next

    For sd.seldot=Each seldot         
     For d.dot=Each dot
      If d<>sd\d Then
       If Abs(d\x-sd\d\x)<=4 And Abs(d\y-sd\d\y)<=4 Then
        For l.lin=Each lin
         rl=0
         If l\dot1=sd\d Then l\dot1=d:rl=checklinerrors(l)
         If l\dot2=sd\d Then l\dot2=d:rl=checklinerrors(l)
         If rl Then Delete l
        Next
        Delete sd\d
        sd\d=d
        For sd2.seldot=Each seldot
         If sd2<>sd And sd2\d=sd\d Then Delete sd2
        Next
        Exit
       End If
      End If
     Next
    Next

    For sd.seldot=Each seldot
     For l.lin=Each lin
      If l\dot1=sd\d Or l\dot2=sd\d Then
       For l2.lin=Each lin
        If l<>l2 Then
         If l\dot1=l2\dot1 And l\dot2=l2\dot2 Then Delete l2:Exit
         If l\dot1=l2\dot2 And l\dot2=l2\dot1 Then Delete l2:Exit
        End If
       Next
      End If
     Next
    Next

    setdots

    If sel=2 Then If sellin=Null Then sel=0 Else selected=1
    changed=1
   Case 32
    If d2.dot<>Null Then
     l.lin=Null
     For sd.seldot=Each seldot     
      l.lin=New lin
      l\txt$=""
      l\style=SelectedGadgetItem(slb)
      l\dot1=sd\d
      l\dot2=d2.dot
      changed=1
     Next
     If First seldot=Last seldot Then sellin=l:selected=1:sel=2
     If n<0 Then remsel
    End If
  End Select

  so.selobj=First selobj
  sd.seldot=First seldot
  If so<>Null Then
   SetGadgetText cm,so\o\txt$
  ElseIf sellin<>Null Then
   SetGadgetText cm,sellin\txt$
  ElseIf sd<>Null Then
   SetGadgetText cm,sd\d\txt$
  End If

  redraw
 End If
 curmz=MouseZ()
 drawcur
 hlsel 
 FlipCanvas cnv
Forever

Function drawcur()
For n1=0 To clq(mcur)
 Line MouseX(cnv)+cl(mcur,n1,0),MouseY(cnv)+cl(mcur,n1,1),MouseX(cnv)+cl(mcur,n1,2),MouseY(cnv)+cl(mcur,n1,3)
Next
If mcur=3 Then Oval MouseX(cnv)-4,MouseY(cnv)-4,9,9,0
End Function

Function min#(v1#,v2#)
If v1#<v2# Then Return v1# Else Return v2#
End Function

Function max#(v1#,v2#)
If v1#>v2# Then Return v1# Else Return v2#
End Function

Function redraw()
SetBuffer ImageBuffer(bi)
SetFont fnt
ClsColor 255,255,255
Color 0,0,0
Cls

If grid Then
 ;Color 128,128,255
 ;x#=move#(scr2cnvx(0))
 ;Repeat
 ; xx=cnv2scrx(x#)
 ; Rect xx,0,1,yres,1
 ; x#=x#+gsiz#
 ;Until xx>xres

 xrs=ImageWidth(bi)
 yrs=ImageWidth(bi)
 LockBuffer ImageBuffer(bi)
 If mgn#>.25 Then
  x#=move#(scr2cnvx(0))
  Repeat
   xx=cnv2scrx(x#)
   If xx>=xrs Then Exit
   If xx>=0 Then
    y#=move#(scr2cnvy(0))
    Repeat
     yy=cnv2scry(y#)
     If yy>=yrs Then Exit
     If yy>=0 Then WritePixel xx,yy,-8355592
     y#=y#+gsiz#
    Forever
   End If
   x#=x#+gsiz#
  Forever
 End If
 UnlockBuffer ImageBuffer(bi)

End If

For o.obj=Each obj
 setcol o\style
 x1=cnv2scrx(o\x#)
 y1=cnv2scry(o\y#)
 x2=cnv2scrsiz(o\xsiz#)
 y2=cnv2scrsiz(o\ysiz#)
 x3=x1+(x2 Sar 1)
 y3=y1+(y2 Sar 1)
 setcol o\style
 Rect x1,y1,x2,y2
 setbg o\style
 Rect x1+2,y1+2,x2-4,y2-4
 otext x3,y3-1,o\txt$,0,o\style
Next

For l.lin=Each lin
 x1=cnv2scrx(l\dot1\x)
 y1=cnv2scry(l\dot1\y)
 x2=cnv2scrx(l\dot2\x)
 y2=cnv2scry(l\dot2\y)
 x3=(x1+x2) Sar 1
 y3=(y1+y2) Sar 1
 setcol l\style
 Line x1,y1,x2,y2
 otext x3,y3-2,l\txt$,0,l\style
Next

Color 0,0,0
For d.dot=Each dot
 setcol d\style
 If d\attobj=Null Then
  Rect cnv2scrx(d\x)-1,cnv2scry(d\y)-1,3,3,0
 Else
  Oval cnv2scrx(d\x)-2,cnv2scry(d\y)-2,5,5,0
 End If
 otext x3,y3-2,d\txt$,0,d\style
Next

SetBuffer CanvasBuffer(cnv)
DrawBlock bi,0,0
End Function

Function move#(v#)
If grid Then Return gsiz#*Int(v#/gsiz#) Else Return v#
End Function

Function moveditemsshow(dx#,dy#)
If Last seldot<>Null Then
 For l.lin=Each lin
  k1=0:k2=0 
  For sd.seldot=Each seldot
   If l\dot1=sd\d Then k1=1
   If l\dot2=sd\d Then k2=1
  Next
  If k1 Or k2 Then
   cnvLine l\dot1\x+dx#*k1,l\dot1\y+dy#*k1,l\dot2\x+dx#*k2,l\dot2\y+dy#*k2
  End If
 Next

 For sd.seldot=Each seldot
  Rect cnv2scrx(sd\d\x+dx#)-1,cnv2scry(sd\d\y+dy#)-1,3,3,0
 Next
End If

For so.selobj=Each selobj
 cnvRect so\o\x+dx#,so\o\y+dy#,so\o\xsiz,so\o\ysiz,0
Next

End Function

Function findobj.obj(x#,y#)
For o.obj=Each obj
 If o\x-cnvsen#<x# And o\x+o\xsiz+cnvsen#>x# Then
  If o\y-cnvsen#<y# And o\y+o\ysiz+cnvsen#>y# Then Return o
 End If
Next
End Function

Function findlin.lin(x#,y#)
For l.lin=Each lin
 d1.dot=l\dot1
 d2.dot=l\dot2
 If x#>=min(d1\x,d2\x)-cnvsen# And x#<=max(d1\x,d2\x)+cnvsen# Then
  If y#>=min(d1\y,d2\y)-cnvsen# And y#<=max(d1\y,d2\y)+cnvsen# Then
   a#=d1\y-d2\y
   b#=d2\x-d1\x
   If Abs(a#*(x#-d1\x)+b#*(y#-d1\y))<=cnvsen#*Sqr(a#*a#+b#*b#) Then Return l
  End If
 End If
Next

End Function

Function finddot.dot(x#,y#)
For d.dot=Each dot
 If Abs(d\x-x#)<cnvsen# And Abs(d\y-y#)<cnvsen# Then Return d
Next
End Function

Function hlsel()
 If sellin<>Null Then
  x1=cnv2scrx(sellin\dot1\x)
  y1=cnv2scry(sellin\dot1\y)
  x2=cnv2scrx(sellin\dot2\x)
  y2=cnv2scry(sellin\dot2\y)

  setcol sellin\style
  For xx=-1 To 1
   For yy=Abs(xx)-1 To 1-Abs(xx)
    Line x1+xx,y1+yy,x2+xx,y2+yy
   Next
  Next
 End If

 For so.selobj=Each selobj
  setcol so\o\style
  Rect cnv2scrx(so\o\x)-2,cnv2scry(so\o\y)-2,cnv2scrsiz(so\o\xsiz)+4,cnv2scrsiz(so\o\ysiz)+4,0
 Next

 For sd.seldot=Each seldot
  setcol sd\d\style
  Rect cnv2scrx(sd\d\x)-3,cnv2scry(sd\d\y)-3,7,7,0
 Next
 Color 0,0,0
End Function

Function savefile(filename$)
If filename$<>"" Then 

 pbar mtxt$(7)

 f=WriteFile(filename$)
 Writetext f,"SCH0.2"

 s=FilePos(f)
 WriteInt f,0
 For o.obj=Each obj
  q=0
  WriteFloat f,o\x
  WriteFloat f,o\y
  WriteFloat f,o\xsiz
  WriteFloat f,o\ysiz
  WriteString f,o\txt$
  WriteByte f,o\style
  q=q+1    
 Next

 s2=FilePos(f)
 SeekFile f,s
 WriteInt f,q
 SeekFile f,s2

 s=FilePos(f)
 WriteInt f,0
 q=0
 For d.dot=Each dot
  WriteFloat f,d\x
  WriteFloat f,d\y
  WriteFloat f,d\style
  n=0
  If d\attobj<>Null Then
   For o.obj=Each obj
    n=n+1
    If o=d\attobj Then Exit
   Next
   WriteInt f,n
  End If
  q=q+1    
  Next

 s2=FilePos(f)
 SeekFile f,s
 WriteInt f,q
 SeekFile f,s2
  
 s=FilePos(f)
 WriteInt f,0
 q=0
 For l.lin=Each lin
  WriteString f,l\txt$
  WriteByte f,l\style

  For n1=0 To 1
   If n1 Then d2.dot=l\dot2 Else d2.dot=l\dot1
   n=0     
   For d.dot=Each dot
    n=n+1
    If d=d2 Then Exit
   Next
   WriteInt f,n
  Next
  q=q+1
 Next

 s2=FilePos(f)
 SeekFile f,s
 WriteInt f,q
 SeekFile f,s2

 UpdateProgBar pb,1.0

 FreeGadget pbw

 CloseFile f
 seltitle(filename$)
 changed=0
End If
End Function

Function loadfile(filename$)
If filename$<>"" Then
 
 If changed Then If askforsaving()=-1 Then Return

 f=ReadFile(filename$)
 If f=0 Then
  AppTitle mtxt$(8)
  Notify mtxt$(9),True
  Return
 End If

 txt$=Readtext(f,9)

 If txt$<>"SCH0.2" Then
  AppTitle mtxt$(8)
  Notify mtxt$(10),True
  Return
 End If

 pbar mtxt$(5)

 clearall
 pageq=ReadInt(f)
 pagen=pageq

 q=ReadInt(f)
 For n2=1 To q
  o.obj=New obj
  o\x=ReadFloat(f)
  o\y=ReadFloat(f)
  o\xsiz=ReadFloat(f)
  o\ysiz=ReadFloat(f)
  o\txt$=ReadString(f)
  o\style=ReadByte(f)
 Next
  
 q=ReadInt(f)
 For n2=1 To q
  d.dot=New dot
  d\x=ReadFloat(f)
  d\y=ReadFloat(f)
  q2=ReadInt(f)
  If q2>0 Then
   o=First obj
   For n3=2 To q2
    If o=Null Then Exit
    o=After o
   Next
   d\attobj=o
  End If
 Next

 q=ReadInt(f)
 For n2=1 To q
  l.lin=New lin
  l\txt$=ReadString(f)
  l\style=ReadByte(f)
  For n3=0 To 1
   q2=ReadInt(f)
   d.dot=First dot
   For n4=2 To q2
    d=After d
   Next
   If n3 Then l\dot2=d Else l\dot1=d
  Next
 Next

 UpdateProgBar pb,1.0*n1/pageq
 FreeGadget pbw

 checkbounds
 ;aligntoscreen 
 refresh
 remsel
 seltitle(filename$)
 changed=0
End If
End Function

Function seltitle(filename$) 
 Repeat
  i=Instr(filename$,"\",i+1)
  If i=0 Then Exit
  ii=i
 Forever
 If ii>0 Then filename$=Mid$(filename$,ii+1)
 SetGadgetText win,mtxt$(2)+filename$
 lastfile$=filename$
End Function

Function askforsaving()
If changed Then
 AppTitle mtxt$(3)
 Select Proceed(mtxt$(4),1)
  Case -1:Return -1
  Case 1:savefile lastfile$
 End Select
End If
End Function

Function aligntoscreen()
If ax1#<>ax2# And ay1#<>ay2# Then
 mgn#=min(0.95*xres/(ax2#-ax1#),0.95*yres/(ay2#-ay1#))
 updatemgn
 scrx#=(ax2#+ax1#)*0.5-0.5*xres/mgn
 scry#=(ay2#+ay1#)*0.5-0.5*yres/mgn
End If
End Function

Function cnv2scrx(x#)
Return Int((x#-scrx#)*mgn#)
End Function

Function cnv2scry(y#)
Return Int((y#-scry#)*mgn#)
End Function

Function cnv2scrsiz(siz#)
Return Int(siz#*mgn#)
End Function

Function scr2cnvx#(x)
Return 1.0/mgn#*x+scrx#
End Function

Function scr2cnvy#(y)
Return 1.0/mgn#*y+scry#
End Function

Function cnvline (x1#,y1#,x2#,y2#)
Line cnv2scrx(x1#),cnv2scry(y1#),cnv2scrx(x2#),cnv2scry(y2#)
End Function

Function cnvrect (x#,y#,xsiz#,ysiz#,fill)
Rect cnv2scrx(x#),cnv2scry(y#),cnv2scrsiz(xsiz#),cnv2scrsiz(ysiz#),fill
End Function

Function dashrect (x1#,y1#,x2#,y2#,l)
x3=cnv2scrx(min(x1#,x2#))
y3=cnv2scry(min(y1#,y2#))
x4=cnv2scrx(max(x1#,x2#))
y4=cnv2scry(max(y1#,y2#))
If x3>x4 Then z=x3:x3=x4:x4=z
If y3>y4 Then z=y3:y3=y4:y4=z

dx=1
dy=0
x=x3
y=y3
c=-1
k=Int(MilliSecs()/50)
If (k Mod (l*2))>=l Then c=-16777215

If x3<x4 And y3<y4 Then
 Repeat
  WritePixel x,y,c
  k=(k+1) Mod l
  If k=0 Then c=-1-16777215*(c=-1)
  x=x+dx
  y=y+dy
  If x=x3 And y=y3 Then Exit
  If x=x4 And y=y4 Then dx=-1:dy=0
  If (x=x3 And y=y4) Or (x=x4 And y=y3) Then dy=dx:dx=0
 Forever
End If
End Function

Function updatemgn()
If mgn#<minmgn# Then mgn#=minmgn#
If mgn#>maxmgn# Then mgn#=maxmgn#
cnvsen#=1.0/mgn#*sen
fnthgt=Int(mgn#*8)
If fnthgt<1 Then fnthgt=1 ElseIf fnthgt>18 Then fnthgt=18
If fnt<>0 Then FreeFont fnt
fnt=LoadFont("arial cyr",fntsiz(fnthgt))
End Function

Function movebounds(x#,y#)
If ft Or x#<ax1# Then ax1#=x#
If ft Or y#<ay1# Then ay1#=y#
If ft Or x#>ax2# Then ax2#=x#
If ft Or y#>ay2# Then ay2#=y#
ft=0
End Function

Function checkbounds()
ft=1
For o.obj=Each obj
 movebounds o\x,o\y
 movebounds o\x+o\xsiz,o\y+o\ysiz
Next

For d.dot=Each dot
 movebounds d\x,d\y
Next
End Function

Function updatewindow()
Repeat
 xres=ClientWidth(win)-lwid
 yres=ClientHeight(win)
Until xres>0 And yres>0

x=ClientWidth(win)-lwid

SetGadgetShape cm,x,0,lwid,lbhei
SetGadgetShape slb,x,lbhei,lwid,ClientHeight(win)-lbhei

ShowPointer cnv
If cnv<>0 Then FreeGadget cnv
cnv=CreateCanvas(0,0,xres,yres,win)
SetBuffer CanvasBuffer(cnv)
HidePointer cnv

If bi<>0 Then FreeImage bi
bi=CreateImage(xres,yres)
refresh

ActivateWindow win
End Function

Function remsel()
Delete Each selobj
sellin=Null
Delete Each seldot
sel=0
selected=0
End Function

Function btpress()
e=EventSource()
Select e
 
 Case slb,cm
  If e=slb Then
   s=SelectedGadgetItem(e)
  ElseIf EventData()=13 Then
   m$=TextFieldText$(cm)
  Else
   Return
  End If
  For so.selobj=Each selobj
   If e=slb Then so\o\style=s Else so\o\txt=m$
  Next
  If sellin<>Null Then
   If e=slb Then sellin\style=s Else sellin\txt$=m$
  End If
  For sd.seldot=Each seldot
   If e=slb Then sd\d\style=s Else sd\d\txt$=m$
  Next
  changed=1
  refresh
End Select
ActivateGadget cnv
End Function

Function selection()
 mcur=tool
 If tool<>3 And selected=0 Then
  remsel
  sel=3
  sd.seldot=New seldot
  sd\d=finddot(mx#,my#)
  If sd\d=Null Then
   Delete sd
   sel=2
   sellin=findlin(mx#,my#)
   If sellin=Null Then    
    sel=1
    so.selobj=New selobj
    so\o=findobj(mx#,my#)
    If so\o=Null Then
     Delete so
     sel=0
    End If
   Else   
    mcur=4
   End If
  End If
  selectalsodots 0
 End If
 hlsel
End Function

Function keypress()
Select EventData()
 Case 2,3,4: tool=EventData()-1:Return 1
 Case 60
  If lastfile$="" Then savefile(RequestFile(mtxt$(1),ext$,1)) Else savefile lastfile$
 Case 61
  loadfile(RequestFile(mtxt$(0),ext$,0))
 Case 1: If askforsaving()>-1 Then End
 Case 211
  If sel=2 Then
   Delete sellin
   sellin=Null
   changed=1
  Else
   For so.selobj=Each selobj
    changed=1
    k=0
    For d.dot=Each dot
     If d\attobj=so\o Then Delete d
    Next
    Delete so\o
   Next
   Delete Each selobj
   For sd.seldot=Each seldot
    changed=1
    Delete sd\d
   Next
   Delete Each seldot
  End If
  sel=0
  selected=0
  refresh
End Select
End Function

Function setcol(s)
c=scol(s)
Color c Shr 16, c Shr 8 And 255, c And 255
End Function

Function setbg(s)
c=sbg(s)
Color c Shr 16, c Shr 8 And 255, c And 255
End Function

Function Selectalsodots(n)
For so.selobj=Each selobj
 For d.dot=Each dot
  If d\attobj=so\o Then 
   sd.seldot=New seldot
   sd\d=d
   sd\o=so\o
   sd\num=n
  End If
 Next
Next

If sellin<>Null Then
 sd.seldot=New seldot
 sd\d=sellin\dot1
 sd.seldot=New seldot
 sd\d=sellin\dot2
End If
End Function

Function writetext(file,txt$)
For n=1 To Len(txt$)
 WriteByte file,Asc(Mid$(txt$,n,1))
Next
End Function

Function readtext$(file,symq)
For n=1 To symq
 txt$=txt$+Chr$(ReadByte(file))
Next
Return txt$
End Function

Function otext(x,y,m$,tb,style)
If m$="" Then Return
xs=StringWidth(m$)
x=x-(xs Sar 1)
y=y-(fnthgt-1)*(tb=0)
setbg style
Rect x-1,y-1,xs+2,fnthgt+2
setcol style
Text x,y+fnttop(fnthgt),m$
End Function

Function selectitem(list,item$)
For n=0 To CountGadgetItems(list)-1
 If GadgetItemText$(list,n)=item$ Then SelectGadgetItem list,n:Exit
Next
End Function

Function refresh()
ActivateGadget cnv
redraw
hlsel
drawcur
FlipCanvas cnv
End Function

Function checklinerrors(l.lin)
If l\dot1=l\dot2 Then Return 1
For l2.Lin=Each Lin
 If l2<>l Then
  If l2\dot1=l\dot1 And l2\dot2=l\dot2 Then Return True
  If l2\dot1=l\dot2 And l2\dot2=l\dot1 Then Return True
 End If
Next
End Function

Function pbar(title$)
x=ClientWidth(Desktop())/2-150
y=ClientHeight(Desktop())/2-25
pbw=CreateWindow(title$,x,y,300,50,0,1)
pb=CreateProgBar(5,5,284,15,pbw)
End Function

Function menuitem()
ed=EventData()
Select ed
 Case 0: clearall
 Case 1: loadfile(RequestFile(mtxt$(0),ext$,0))
 Case 2: savefile(RequestFile(mtxt$(1),ext$,1))
 Case 3: tobmp
 Case 4: If askforsaving()>-1 Then End
 Case 5
 Case 6:Notify "Scheme Editor v0.2"+kr$+"Автор программы: Матвей Меркулов"+kr$+"MattMerk@mail.ru"
End Select
End Function

Function clearall()
remsel
Delete Each obj
Delete Each lin
Delete Each dot
End Function

Function setdots()
For d.dot=Each dot
 d\attobj=findobj(d\x,d\y)
Next
End Function
 
Function tobmp()
file$=Left$(lastfile$,Instr(lastfile$,".")-1)
checkbounds
scrx#=ax1#-40.0/mgn#
scry#=ay1#-40.0/mgn#
FreeImage bi
bi=CreateImage(cnv2scrsiz(ax2#-ax1#)+80,cnv2scrsiz(ay2#-ay1#)+80)
redraw
n=n+1
SaveImage bi,file$+".bmp"
FreeImage bi
bi=CreateImage(xres,yres)
redraw
End Function

Function mouse()
If curmz<>MouseZ() Then Return 4
For n=1 To 3
 If MouseDown(n) Then Return n
Next
End Function

.messages
Data "0-Выберите файл для загрузки:"
Data "1-Выберите файл для записи:"
Data "2-Редактор схем - "
Data "3-Выход.."
Data "4-Сохранить изменения перед выходом?"
Data "5-(24)Загрузка файла..."
Data "6-(13)Вы уверены, что хотите удалить этот лист?"
Data "7-(20)Сохранение файла..."
Data "8-(21)Ошибка при открытии"
Data "9-(22)Невозможно открыть файл"
Data "10-(23)Неизвестный формат файла"
Data "*** Меню"
Data "Файл"
Data "{"
Data "Новый"
Data "Загрузить..."
Data "Сохранить..."
Data ""
Data "Вывод в BMP-файл"
Data ""
Data "Выход"
Data "}"
Data "Помощь"
Data "{"
Data "Справка"
Data "О программе"
Data "}"
Data "*** Стили"
Data "Черный",$000000,$FFFFFF
Data "Красный",$FF0000,$FFBFBF
Data "Зеленый",$00FF00,$BFFFBF
Data "Синий",$0000FF,$BFBFFF
Data "Строение",$FF8000,$FFDFBF
Data "Река",$008080,$BFFFFF
Data "Дорога",$000000,$FFFFFF
Data "Лес",$008000,$BFDFBF
Data "*** -----"

.cursors
Data 4, -8,0,-3,0, 8,0,3,0, 0,-8,0,-3, 0,8,0,3, 0,0,0,0
;data 4, -4,-8,-1,-8, 4,-8,1,-8, -4,8,-1,8, 4,8,1,8, 0,-7,0,7
Data 3, 0,0,8,4, 0,0,4,8, 4,8,5,5, 8,4,5,5
Data 3, 3,2,8,7, 2,3,7,8, -2,0,2,0, 0,-2,0,2
Data 5, 0,0,8,4, 0,0,4,8, 4,8,5,5, 8,4,5,5, -7,-4,-1,-4, -4,-7,-4,-1

.fnt
Data 0,1,0,2,0,4,-1,6,-1,8,-2,10,-2,12,-3,14,-3,15,-3,16,-3,17,-3,18
Data -4,21,-4,22,-4,23,-4,25,-4,26,-5,28