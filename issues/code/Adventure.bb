<code bb>
Type element
 Field name$,flag$
 Field root.element,prev.element,nxt.element,sub.element
End Type

Type eref
 Field e.element
End Type

Type verb
 Field name$,n[1],adverb$
End Type

Type cmd
 Field e.element, v.verb, typ
End Type

Const tysiz=14,xsiz=590,ysiz=600

Global root.element=New element
Global loc.element,hands.element,body.element,all.element,shared.element
Global ty

Dim try.cmd(2), fcmd.cmd(2)

Graphics 800,600
SetBuffer FrontBuffer()
SetFont LoadFont("Arial Cyr",16)

;�������� ���������
Dim en$(20,6)
Repeat
 n1=n1+1
 Read m$
 If m$="" Then Exit
 q$=""
 For n2=1 To 6
  en$(n1,n2)=rustrim$(Mid$(m$,n2*3-2,3))
 Next
Forever

;�������� ��������
Repeat
 Read m$
 If m$="" Then Exit
 v.verb=New verb
 i=Instr(m$,"#")
 v\name$=Left$(m$,i-2)
 v\n[0]=Mid$(m$,i+1,1)
 ;����������� ������� ������� (���� ������������)
 ii=Instr(m$,"#",i+1)
 If ii>0 Then
  v\n[1]=Mid$(m$,ii+1,1)
  v\adverb$=Mid$(m$,i+3,ii-i-3)
 End If
 ;��� ������ - ��� �������
 ;Print v\name$+" "+v\n[0]+" "+v\adverb$+" "+v\n[1]
Forever
;WaitKey

;������������ ���� ������
e.element=root.element
oldsp=-1
Repeat
 Read m$
 If m$="" Then Exit
 ;����������� ������ (� ����������� �� �������� � ������)
 sp=Len(m$)-Len(rustrim$(m$))
 While sp<oldsp
  e=e\root
  oldsp=oldsp-1
 Wend
 If sp>oldsp Then
  e=einsertin(e,Null)
  oldsp=oldsp+1
 Else
  e=einsertafter(e,Null)
 End If
 ;�����
 i=Instr(m$,":")
 If i Then
  e\flag$=Mid$(m$,i+1)+","
  e\name$=rustrim$(Left$(m$,i-1))
 Else
  e\name$=rustrim$(m$) 
 End If
 ;��� ������ - ��� �������
 ;viewpad e
Forever

shared=root\sub
all=After shared
hands=After all
body=After hands

moveplayer "�������"

Color 255,255,0
tprint "����������� ���������� �� ������� ������� � ��������� CapsLock."
tprint "����������� ��. PgUp � PgDn ��� ������ ����������� �����."

look

Repeat
 inputphrase

 k=-1
 For c.cmd=Each cmd
  If c\e=all Then 
   ;������� �������� ������ ����� "���"
   For r.eref=Each eref
    e=r\e
    If e<>all And e<>hands And e<>body Then
     cc.cmd=New cmd
     Insert cc After c
     cc\e=e
     cc\typ=c\typ
    End If
   Next   
   Delete c
  Else
   ;����������� ������ ������� ������� ����
   If k<>c\typ Then
    k=c\typ
    try(k)=c
    fcmd(k)=c
   End If
  End If
 Next

 ;�������� ����� �� ������������
 If try(0)\v\n[0]=0 Then
  processcmd
 Else
  Repeat
   processcmd
   If try(0)=Null Then Exit
   try(0)=After try(0)
   If try(0)=fcmd(1) Then
    try(0)=fcmd(0)
    try(1)=After try(1)
    If try(1)=fcmd(2) Then
     try(1)=fcmd(1)     
     If try(2)=Null Then Exit
     try(2)=After try(2)
     If try(2)=Null Then Exit
    End If 
   End If
  Forever
 End If
Forever 

;������ ��������� ��� ������� �� 6 ������� (�� 3 �������)
Data "�  �  �  �  �� �  ";01
Data "�  �  �  �  �� �  ";02
Data "�  �  �  �  �� �  ";03
Data "�  �  �  �  �� �  ";04
Data "�  �  �  �  �� �  ";05
Data "   �  �     �� �  ";06
Data "   �  �     �� �  ";07
Data "�� �� �� �� �� �� ";08
Data "�� �� �� �� �� �� ";09
Data "�  �  �  �  �� �  ";10
Data "�� �������� �� �� ";11
Data "�� �������� �� �� ";12
Data "�     �� �  ����� ";13
Data "�  �������  �� �� ";14
Data "�� �������� �� �� ";15
Data "�  �� �� �  ����� ";16
Data ""

;�������
Data "����� #4","����� #4","������� #4"
Data "�������� #4 �� #4","������ #4 �� #4"
Data "�������� #4 �� #4"
Data "���� � #4","���� �� #3","�������� � #4","����� � #4"
Data "������� #4","������� #4","�������� #4 #5","�������� #4 #5"
Data "������� #4 #5","������� #4 #5"
Data "����� #4","������ #4","����� #4"
Data "�������� #4 � #4","�������� #4 ��� #4"
Data "��������� #4","����������� #4","����������� #0","��������� #0"
Data "������� #4 � #4"
Data "������ #4 #5"
Data ""

;�������� ��������
Data "������ �������������� �������"
Data " ��#14:�"
Data " ���#13:�"
Data " ���#04:�"
Data "  ����#01:���,�����,�,���,���"
Data "������#02:�"
Data " ����#05:����,�,>��������"
Data " ����#06:����,��_��,��_�"
Data "  �����#02:���,�"
Data "  ������#01:���,���,�,�,���_��"
Data "   ������#06:���,���,�����,���,���,���,������"
Data "   ����#13:���,���,�����,���,�,���,���,������"
Data "   ������#01:���,���,�����,���,�,���,���"
Data "  �������#06:���,���,�����,��,���,���"
Data " ���#04:����,�"
Data "  �����#04:���,�"
Data "  ���#02:���,�"
Data " ������#05:��_��,�,��_���"
Data "  ������#06:���,���,��_��,��,���,��_���,���"
Data " ����#06:���,��_��,���"
Data "������#08:�"
Data " ����#05 �������:����,�,>�������"
Data " ����#05 ������:����,�,>������"
Data " ����#05 �������:����,�,>������"
Data " ����#05 �����:����,�,>�����"
Data " �����#09 ����#05:����,���,�,>���������� ������"
Data " ������#06:���,��_��,��_���,���"
Data " �������#09 ������#01:���_��,�"
Data "  ����:���,���,��_��,�����"
Data "����#09:�"
Data " ����#05:����,�,>��������"
Data " ����#02:��_�,�"
Data "  �����#11 ������#10 �����:���,����"
Data "  ���#12 ������#10 �����:���,����"
Data "  ����#06 �����:���"
Data "  ������#10 ����:���,����"
Data "  ���#07:���,���_��"
Data "  ����#06 �����:���"
Data " �������#02:��_�,�"
Data "  �����#11 ������#10 ��������:���,����"
Data "  ���#12 ������#10 ��������:���,����"
Data "  ����#06 ��������:���"
Data "  ����#06 ��������:���"
Data "������#06"
Data " ����#05:����,�,>��������"
Data " ������#06:��_�,����"
Data " ���^��#06:��_��"
Data "����#03:�"
Data " ����#05:����,>��������"
Data " ����#02:��_��,�"
Data " �������#06:����,��_��"
Data " ���#04:����,�"
Data "  �����#04:���,�"
Data "  ���#02:���,�"
Data "��������#09 �����#01:�"
Data " �����#09 ����#05:����,�,���,>��������"
Data " �����#09 ����#05:���,�"
Data " ����#09 ����#05:���,�"
Data " �������#02 ����:��_��,�"
Data " �������#02 ������:��_��,�"
Data ""

Function einsertafter.element(afterwhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=afterwhat
what\nxt=afterwhat\nxt
If afterwhat\nxt<>Null Then afterwhat\nxt\prev=what
afterwhat\nxt=what
what\root=afterwhat\root
Return what
End Function

Function einsertbefore.element(beforewhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=beforewhat\prev
what\nxt=beforewhat
If beforewhat\prev<>Null Then beforewhat\prev\nxt=what
what\root=beforewhat\root
If what\prev=Null Then what\root\sub=what
beforewhat\prev=what
Return what
End Function

Function einsertin.element(inwhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=Null
If inwhat\sub=Null Then
 what\nxt=Null
Else
 what\nxt=inwhat\sub
 inwhat\sub\prev=what
End If
inwhat\sub=what
what\root=inwhat
Return what
End Function

Function eremove(what.element,care=True)
If care Then epush what
e.element=what\sub
While e<>Null
 e2.element=e
 e=e\nxt
 eremove e2,False
Wend
Delete what
End Function

Function epush(what.element)
If what\prev<>Null Then what\prev\nxt=what\nxt Else what\root\sub=what\nxt
If what\nxt<>Null Then what\nxt\prev=what\prev
End Function

;�������� �������� ������� � ������� ������ �� ��������� (� ������ ������)
Function realname$(e.element,pad)
m$=e\name$
Repeat
 i=Instr(m$,"#")
 If i=0 Then Exit
 m$=Left$(m$,i-1)+en$(Mid$(m$,i+1,2),pad)+Mid$(m$,i+3)
Forever
Repeat
 i=Instr(m$,"^")
 If i=0 Then Exit
 m$=Left$(m$,i-1)+Mid$(m$,i+1+(pad>1 And pad<>5))
Forever
Return m$
End Function

;������� �������� �������� � ����� ������, ��������� ���������� � ������� �������
Function rustrim$(m$)
For n=1 To Len(m$)
 If Mid$(m$,n,1)<>" " Then Exit
Next
m$=Mid$(m$,n)
For n=Len(m$) To 1 Step -1
 If Mid$(m$,n,1)<>" " Then Exit
Next
Return Left$(m$,n)
End Function

;���������� ����������� � �������
Function moveplayer(lname$)
e.element=root\sub
;����� ������� �� ��������
While e<>Null
 m$=realname$(e,1)
 If m$=lname$ Then Exit
 e=e\nxt
Wend
If e=Null Then Stop
;������������ ������ ��������� ��������
Delete Each eref
;���������� ������ �������������� ��������
addbranch shared
;���������� ������ �������� �������
addbranch e
loc=e
End Function

;��������������� ������� ��� ��������� �������� � ������ ���������
Function addbranch(what.element)
e.element=what\sub
While e<>Null
 ;������� ������� �� ���������
 If flag(e,"���")=0 Then
  r.eref=New eref
  r\e=e
 End If
 e=nextobj(what,e)
Wend
End Function

;�������� �������� ������� � ������ �������
Function viewpad(e.element)
Cls
;�������� �������� ������� �� ���� ������� (�������)
Text 0,0,realname$(e,1)
Text 0,15,"��� "+realname$(e,2)
Text 0,30,"���� "+realname$(e,3)
Text 0,45,"������ "+realname$(e,4)
Text 0,60,"������� "+realname$(e,5)
Text 0,75,"�������� � "+realname$(e,6)
WaitKey
End Function

;������� ����� �����
Function inputphrase$()
;������� ������ ������������ �������
Delete Each cmd
cur.cmd=New cmd
fc.cmd=cur
Color 255,255,255
Text 0,ty,"|"
Repeat
 i=WaitKey()
 Select i
  Case 8;Backspace
   If part$<>"" Then
    part$=Left$(part$,Len(part$)-1)
   Else
    If fc<> cur Then
     cur=Before cur
     Delete Last cmd
    End If
   End If
  Case 27
   End
  Case 44, 32
   ;�������� �� ����������� ����� 1-�� � 2-�� ��������
   If fc\v\n[0]>0 Or i=44 Then
    If cur\typ<2-(fc\v\n[1]=0) Or i=44 Then
     ;���� ������� ���������� ����� - ������� ����� ������� �������
     If fnd Then
      t=cur\typ
      cur=New cmd
      fnd=0
     End If
     ;���� ������� �������, � �� ������, �� ��������� ������ ����� ������ �� ����
     cur\typ=t+(i=32)
     part$=""
    End If
   End If
  ;�������� �� ����������� ����� �����
  Case 13
   If cur\e=Null Then i=0
   If fc<>Null Then If fc\v<>Null Then If fc\v\n[0]=0 Then i=13
  ;��������� ������ ����������� �������
  Case 5
   q=q+1
  Case 6
   q=q-1
  Default
   part$=part$+Chr$(i)  
   q=1
 End Select

 pl=Len(part$)
 If part$<>"" Then
  n=0
  If cur\typ=0 Then
   ;����� ����������� ������� (��� 0)
   fv.verb=Null
   For v.verb=Each verb 
    ;�������� �� ���������� ��������� ����� � ������� �������
    If Mid$(v\name$,1,pl)=part$ Then
     ;�������� �� ���������� ����� (��� �������������� ��������)
     If fc=cur Then
      k=1
     ElseIf v\n[0]=fc\v\n[0] And v\n[1]=fc\v\n[1] And v\adverb$=fc\v\adverb$ Then
      k=1
     Else
      k=0
     End If
     If k Then
      n=n+1
      ;����������� ������� ��� ���������� ���������� ������� (� ����������� ��
      ; �����
      If n=1 Or q=0 Then fv=v
      ;��� ������� q-�� ������� - ����� (������ ������)
      If q=n Then Exit
     End If
    End If
   Next
   ;���� ������� � ����� ������� (q) ���, ������ ���� ��� ��������� ������
   ; (�������� � 1-��, q=1-1=0) ���� ������ (� ����������)
   If fv<>Null And v=Null Then
    v=fv
    If q=0 Then q=n Else q=1
   End If
   If v<>Null Then fnd=1
   cur\v=v
  Else
   ;����������� ����� 1-�� ��� 2-�� ������� �� ��������
   fr.eref=Null
   For r.eref=Each eref
    If Mid$(r\e\name$,1,pl)=part$ Then
     n=n+1
     If n=1 Or q=0 Then fr=r
     If q=n Then Exit
    End If
   Next
   If fr<>Null And r=Null Then
    r=fr
    If q=0 Then q=n Else q=1
   End If
   If r<>Null Then
    fnd=1
    cur\e=r\e
   Else
    cur\e=Null
   End If
  End If
 End If

 m$=""
 ;����������� ������� �� ������
 ;������� ������
 Color 0,0,0
 Rect 0,ty,800,tysiz*2
 For c.cmd=Each cmd
  ;������ ������� (����� ��������� ������ �����) ��� �������
  If c<>fc Then
   If c\typ=oldtyp Then
    m$=m$+", "
   Else
    m$=m$+" "    
    If c\typ=2 Then m$=m$+fc\v\adverb$
   End If
  End If
  oldtyp=c\typ
  ;������ ���������� (���������) �����
  If c=cur Then
   Color 255,255,255
   ;���� ����� �� ������� ��������� - ������ �������, �����
   If c\e=Null And c\v=Null Then
    Text 0,ty,m$
    Color 255,0,0
    pl=StringWidth(m$)
    Text pl,ty,part$+"|"
    m$=""
    Exit
   End If
   ;������ ���������� ���� � ��������� ����� �����
   If i<>13 Then
    pl=StringWidth(m$+part$)
    Text 0,ty,m$+part$
    m$=""
   End If
  End If
  ;���������� � ����� ���������� �����
  If c\typ=0 Then
   m$=m$+c\v\name$
  Else
   m$=m$+realname$(c\e,fc\v\n[c\typ-1])
  End If
  If i=13 And c=cur Then
   tprint m$
   Return
  End If
 Next
 ;���������� �������� � ������� (���� �������� ������ � ���������)
 If cur\typ=0 And fc\v<>Null Then 
  If fc\v\adverb$<>"" Then m$=m$+" ("+rustrim$(fc\v\adverb$)+")"
 End If

 ;������ ������� ���������� ����� "��������������" �����
 Color 0,255,0
 If m$<>"" Then Text pl,ty,Mid$(m$,Len(part$)+1)+"|"
Forever
End Function

;��������� ��������� ������������ ������
Function processcmd()
Color 255,255,0
v.verb=try(0)\v
verb$=v\name$
adv$=v\adverb$
If try(1)<>Null Then
 n1.element=try(1)\e
 n1n$=realname$(n1,1)
End If
If try(2)<>Null Then
 n2.element=try(2)\e
 n2n$=realname$(n2,1)
End If
Select verb$
 Case "�����","�����"
  k=flag(n1,"�����")
  n2=findobjwithflag(body,"������")
  ;���� ���� ������ � �������� � ������� ���������� ��� - �������� ��� � ������
  If n2<>Null And k Then
   moveobj n2,n1,"�"
   m$=m$+"�� �������� "+realname$(n1,4)+" � "+realname$(n2,4)+"."
  ;���� ������� ����� ����� � ���� � � ����� ������ ���, ����� ��� � ����
  ElseIf k Or flag(n1,"���") Then
   If hands\sub=Null Then
    moveobj hands,n1
    m$="������ � ��� � ����� "+n1n$
   Else
    m2$="� ��� ������ ����."
    n2=Null
   End If
  End If
 Case "��������","�������","������"
  ;���� ������� � ���������, �� ...
  If findobj(body,n1n$)<>Null Or findobj(hands,n1n$)<>Null Then
   ;�������� ����� �������������� (� ������, ���� ������� ����� ���� �������
   ; ����� �������)
   If n2<>Null Then
    If adv$="�� " Then If flag(n2,"��_��") Then m2$="��"
    If adv$="� " Then If flag(n2,"��_�") Then m2$="�"
    If adv$="��� " Then If flag(n2,"��_���") Then m2$="���"
    ;���������� �������� ���� "������ �� �����, ���� �� �������"
    If m2$<>"" Then
     e.element=n2
     While e<>Null
      If e=n1 Then m2$="":Exit
      e=e\root
     Wend
     If m2$<>"" Then 
      moveobj n2,n1,m2$
      m$="�� ���������� �� "+realname$(n1,2)
      If m2$="�" Then water
     End If
    End If
   Else
    ;������� ���������� � ������� �������
    moveobj loc,n1
    m$="�� ���������� �� "+realname$(n1,2)
   End If
   ;������, �������� � �������� / �������� ���� ������������
   If adv$="� " And n2n$="����" Then
    If flag(n2,"����") Or flag(n2,"����") Then
     tprint "�� ��������� "+realname$(n1,4)+" � ����."
     eremove n1
     Return
    Else
     moveobj loc,n1
     m$=cap$(n1n$)+" ��������"+vend$(n1)+" �� ������ � ����"+vend$(n1)+" �� ���."
    End If
   End If
  Else
   m2$="� ��� ��� "+realname$(n1,2)
  End If
 Case "��������"
  If flag(n1,"�_���") And flag(n2,"���_��") Then
   remflag n1,"���"
   moveobj n1,n2,"���"
   m$="�� �������� "+realname$(n1,4)+" �� "+realname$(n2,4)+"."
  End If
 Case "���� ��"
  If Left$(n1n$,8)="��������" Then
   tprint "�� ����� �� ��������. ��� ����� ��, �����, � ��������� ����� ������, "
   tprint " �� ��������. � �������� �� ����, � ������� ������������� ������... The End."
   WaitKey:End
  End If
 Case "���� �","�������� �","����� �"
  k=Instr(n1\flag$,">")
  ;��������� ������ � ����
  If n1n$="����" Then
   k2=flag(n1,"����") Or flag(n1,"����")
   If k2=0 And verb$="�������� �" Then
    m$="����� ����� �� ������� ������ � ������ ����������."
   End If
   If k2 Or m$<>"" Then
    tprint "�� ���������� � ����."
    If m$<>"" Then tprint m$
    tprint "� ������ �������, ���������� ������ �� ������ ����, ���� �� ����������"
    tprint " � �����������, ����������� ��������� 'Under Construction'. The End."
    WaitKey: End
   End If
  End If
  ;������� � ������ �������
  If k Then
   If flag(n1,"����")
    m2$="�������."
   Else
    tprint "��."
    ;����������� ��� ������� � �������������� ������� � ���
    k2=Instr(n1\flag$,",",k)
    moveplayer Mid$(n1\flag$,k+1,k2-k-1)
    ;�������, �������������
    look
    ;��������� ������� ������������ (�. �. ����� ��������� �������������)
    Delete Each cmd
    Return
   End If
  End If
 Case "�������"
  ;���� ������ ������ ...
  If flag(n1,"����") Then
   ;� �� ������...
   If flag(n1,"���") Then
    m2$="�������."
   Else
    ;�� �������� ����
    m$="�� ������� "+realname$(n1,4)+"."
    remflag n1,"����"
    addflag n1,"����"

    ;��������� ������������� �����
    If Instr(n1n$,"�����") Then n1=findobj(root,"�����") Else n1=findobj(root,"��������")
    If flag(n1,"����")=0 Then addflag(n1,"����")
    water
   End If
   ;�����, ���� �� ������ (�� ����, ������ �����������) ...
  ElseIf flag(n1,"����") Then
   m$=cap$(n1n$)+" ��� ������"+vend$(n1)+"."
  End If
 Case "�������"
  ;����������
  If flag(n1,"����") Then
   m$=cap$(n1n$)+" ��� ������"+vend$(n1)+"."
  ElseIf flag(n1,"����") Then
   m$="�� ������� "+n1n$
   addflag n1,"����"
   remflag n1,"����"
   ;��������� ������������� �����
   If Instr(n1n$,"�����") Then
    If flag(findobj(loc,"������� ������� �����"),"����") Then
     If flag(findobj(loc,"����� ������� �����"),"����") Then
      remflag findobj(loc,"�����"),"����"
     End If
    End If
   Else
    If flag(findobj(loc,"������� ������� ��������"),"����") Then
     If flag(findobj(loc,"����� ������� ��������"),"����") Then 
      remflag findobj(loc,"��������"),"����"
     End If
    End If
   End If
  End If
 Case "�������","�������"
  If flag(n1,"����") Then
   m2$=cap$(n1n$)+"��� ������"+vend$(n2)+"."
  ElseIf n1n$="������" Or n1n$="����" Then
   ;������� ����� ������� ������ ������� ��� ������
   If n2n$="����" Then
    m$="�� ������� ���� ������."
   ElseIf n2=Null Then
    m$="�� ������� ���� �������, �� ��� ���� ������ ����������."
   Else
    m2$=cap$(n2n$)+" ��������"+vend$(n2)+" �� ������."
   End If
   ;��������� ����� ������ - ������� ������
   If m$<>"" Then
    n2=einsertin(loc,Null)
    n2\name$="������#13 ������"
    n2\flag$="�����,�,����"
    r.eref=New eref
    r\e=n2
    ;��� ����������� �� ����, ��� ����, ��������� � ���� � ������
    addflag findobj(loc,"������"),"����"
    addflag findobj(loc,"����"),"����"
   End If  
  End If
 Case "�����","������"
  ;�������� �� ������ �������?
  If flag(n1,"�����") Then 
   ;��������� �� �� � �����
   If hands\sub=n1 Then
    einsertin body,n1
    m$="�� ��� ������ "+n1n$+"."
   Else
    m2$=cap$(realname$(n1,4))+" ����� ������� ����� � ����."
   End If
  End If
 Case "�����"
  ;����������
  If hands=Null Then
   m2$="� ��� ������ ����."
  ElseIf existin(body,n1n$)
   einsertin hands,n1
   m$="�� ����� "+n1n$+"."
  Else
   m$="� ��� ��� "+realname$(n1,2)+"."
  End If
 Case "��������"
  If flag(n1,"���") And n2n$="����" Then
   If n2n$="����" Then
    m$="�� ������� "+realname$(n1,4)+"."
    remflag n1,"���"
    addflag n1,"���"
   End If
  ElseIf flag(n1,"���") Then
   m$=cap$(n1n$)+" ��� ������"+vend$(n1)+"."
  End If
 Case "��������"
  If flag(n1,"���") Then
   If n2n$="����" Then
    m$="�� ������� "+realname$(n1,4)+"."
    remflag n1,"���"
    addflag n1,"���"
   End If
  ElseIf flag(n1,"���") Then
   m$=cap$(n1n$)+" ��� ������"+vend$(n1)+"."
  End If
 Case "���������","�����������"
  e.element=n1\sub
  ;���� �� ���� �������� � ������ ������������
  While e<>Null
   m2$=realname$(e,1)
   ;����� ���������� � ����������� �� ���������
   If flag(e,"��") Then
    m$=m$+"������� - "+m2$+", "
   ElseIf flag(e,"���") Then
    m$=m$+"��� ��� - "+m2$+", "
   ElseIf flag(e,"�") Then
    If flag(n1,"����") Then 
     m2$=""
    Else
     m$=m$+"������ - "+m2$+", "
    End If
   ElseIf flag(e,"���") Then
     m$=m$+"����� "+m2$+", "
   Else
    m$=m$+"���� "+m2$+", "
   End If
   ;������� ������� ���������� ��������
   If m2$<>"" Then
    If flag(e,"���") Then
     r.eref=New eref
     r\e=e
    End If
    remflag (e,"���")
   End If
   e=e\nxt
  Wend

  m2$=""
  ;���������� � ��������� �������
  If flag(n1,"����") Then m2$=m2$+", ������"+vend$(n1)
  If flag(n1,"����") Then m2$=m2$+", ������"+vend$(n1)
  If flag(n1,"���") Then m2$=m2$+", ������"+vend$(n1)
  If flag(n1,"����") Then m2$=m2$+", ������"+vend$(n1)
  If flag(n1,"����") Then m2$=m2$+", ��������"+vend$(n1)+" �����"
  If flag(n1,"����") Then m2$=m2$+", ��������"+vend$(n1)+" �����"
  If m$<>"" Or m2$<>"" Then
   If m$<>"" Then m$=": "+Left$(m$,Len(m$)-2) Else
   If m2$<>"" Then m$=Mid$(m2$,2)+m$
   m$=cap$(n1n$)+m$+"."
  End If

  m2$=""
  ;���� ������� ���� ������ - ���� ����� ���� �� ����� ������, ����� - �� ����� ����
  k=flag(findobj(root,"������� ����"),"����")
  If (n1n$="���� �����" And k=0) Or (n1n$="���" And k=1) Then m2$="�����"
  If n1n$="���� ��������" Then m2$="��������"
  ;����������� ���� ������� �� ����, ����� ����� ������� (���� ��� �������, �� ����
  ; �� �������)
  If m2$<>"" Then
   k=flag(findobj(loc,"����� ������� "+m2$),"����")
   If flag(findobj(loc,"������� ������� "+m2$),"����") Then
    If k Then
     m$="������ ����"
    Else
     m$="������� ����"
    End If
   ElseIf k Then
    m$="�������� ����"
   Else 
    m$=""
   End If   
   If m$<>"" Then m$="�� "+realname$(n1,2)+" ����� "+m$+"."
  End If
  If m$="" Then m$="�������� "+realname$(n1,4)+", �� �� ���������� ������ ����������."
 Case "�����������"
  look
  Return
 Case "���������"
  m$=""
  ;������ ������ ���� �� ������� ������
  e=body\sub
  While e<>Null
   m$=m$+realname$(e,4)+", "
   e=e\nxt
  Wend
  If m$<>"" Then tprint "�� ����� � "+Left$(m$,Len(m$)-2)+"."

  ;������ ���
  If hands\sub=Null Then
   m$="������ ���, "
  Else
   m$=realname$(hands\sub,1)+", "
  End If
  tprint "� ��� � ����� "+Left$(m$,Len(m$)-2)+"."
  m$=""

  ;������ ��������
  e=body\sub
  While e<>Null
   If flag(e,"�����")=0 Then m$=m$+realname$(e,1)+", "
   e=nextobj(body,e)
  Wend
  If m$<>"" Then tprint "� �������� � ��� "+Left$(m$,Len(m$)-2)+"."
  Return
 Case "������","���������"
  If flag(n1,"���") Then
   If Left$(n2n$,5)="�����" Then
    If hands\sub=n2 Or hands\sub=Null Then
     m$="�� ��������� "+realname$(n1,4)+"."
     ;��������� �������� ��������, ����� �������� �����
     n1\name$="����#16 "+realname$(n1,2)
     ;�������� ����������� ����� � ���������
     remflag n1,"���"
     remflag n1,"�����"
     ;������� �� ���
     moveobj loc,n1
    Else
     m$="� ��� ������ ����."
    End If
   Else
    m$="���?"
   End If
  End If
End Select

;��������� ������������ ��������
If m$="" Then
 m$=cap$(verb$)+" "+realname$(n1,v\n[0])
 If n2<>Null Then m$=m$+" "+v\adverb$+realname$(n2,v\n[1])
 m$=m$+" �� ����������. "+m2$
End If
tprint m$
End Function

;�������, ����������� ������� ������� ����� � �������
Function flag(e.element,flag$)
If Instr(e\flag$,flag$+",") Then Return 1
End Function

;���������� ����� � ������ ������ �������
Function addflag(e.element,flag$)
e\flag$=e\flag$+flag$+","
End Function

;�������� ����� �� ������ ������ �������
Function remflag(e.element,flag$)
m$=e\flag$
i=Instr(m$,flag$+",")
If i=0 Then Return
e\flag$=Left$(m$,i-1)+Mid$(m$,i+Len(flag$)+1)
End Function

;�������� ������� ������� � ������ ������ � ������ �������
Function existin(inwhat.element,what$)
If inwhat=Null Then Return
inwhat=inwhat\sub
While inwhat<>Null
 If realname$(inwhat,1)=what$ Then Return 1
 inwhat=After inwhat
Wend
End Function

;�������, �������� ��������� ����� ������ ���������
Function cap$(m$)
m$=Chr$(Asc(Left$(m$,1))-32)+Mid$(m$,2)
Return m$
End Function

;�������, ����������� ���������, � ����������� �� ����
Function vend$(e.element)
If flag(e,"�") Then Return "�"
If flag(e,"�") Then Return "�"
If flag(e,"�") Then Return "�"
End Function

;���������� ������� "�����������"
Function look()
e.element=loc\sub
;������������ ��������, �������� � ����� ������� (����� �������)
While e<>Null
 If flag(e,"���")=0 Then m$=m$+realname$(e,1)+", "
 e=nextobj(loc,e)
Wend
If m$="" Then m$="������ ���  "
tprint "�� ���������� � "+realname$(loc,3)+"."
tprint "����� ���� "+Left$(m$,Len(m$)-2)+"."
End Function

;���������� ������ ������ (� ��������� � ������� ������)
Function tprint(m$)
Repeat
 l=StringWidth(m$)
 If l<=xsiz Then Exit
 n$=Mid$(m$,Len(m$),1)+n$
 m$=Left$(m$,Len(m$)-1)
Forever
If ty+tysiz*2>ysiz Then
 cr=ColorRed()
 cg=ColorGreen()
 cb=ColorBlue()
 CopyRect 0,ysiz-ty,xsiz,ty,0,0
 Color 0,0,0
 Rect 0,ty,xsiz,tysiz*2
 ty=ty*2-ysiz
 Color cr,cg,cb
End If
Text 0,ty,m$
ty=ty+tysiz

If n$<>"" Then tprint n$
End Function

;����������� ������� ������ ������� � ����� ������� �� ��������
Function findobj.element(inwhat.element,what$)
inwhat=inwhat\sub
While inwhat<>Null
 If realname$(inwhat,1)=what$ Then Return inwhat
 ;�������� ������� �������� � �����
 e.element=findobj(inwhat,what$)
 If e<>Null Then Return e
 inwhat=inwhat\nxt
Wend
End Function

;������� ���������� ���������� ������� � ����� inwhat (������� ��� �������������
; �������� ��������� �����)
Function nextobj.element(inwhat.element,current.element)
If current\sub=Null Then
 While current\nxt=Null
  If current\root=inwhat Then Return Null
  current=current\root
 Wend
 Return current\nxt
Else
 Return current\sub
End If
End Function

;������� ������ ������� � ������ ������ � ����� �����
Function findobjwithflag.element(inwhat.element,what$)
e.element=inwhat\sub
While e<>Null
 If flag(e,what$) Then Return e
 e=nextobj(inwhat,e)
Wend
End Function

;���������� ����������� �������
Function moveobj(inwhat.element,what.element,flag$="")
;��������� ����� ��������������
remflag what,"��"
remflag what,"�"
remflag what,"���"
remflag what,"���"
If flag$<>"" Then addflag what,flag$
einsertin inwhat,what
End Function

;������������ �������� �����
Function water()
src.element=loc\sub

While src<>Null
 k=flag(src,"����")
 dst.element=src\sub
 While dst<>Null
  If k And flag(dst,"���") Then
   remflag dst,"���"
   addflag dst,"����"
  End If
  dst=dst\nxt
 Wend 
 src=src\nxt
Wend 
End Function

</code><noinclude>[[���������:���]]</noinclude>