SeedRnd MilliSecs()

Const vfuncnames=1,vchunks=2,vprocessing=4,vvars=8
Const maxparam=2,debug=0;vchunks+vprocessing;+vfuncnames+vchunks

;����� �������
Type chunk
 Field txt$,f.func,v.var,value,vfrom.var
End Type

;�������
Type cmd
 Field chunk.chunk[9],chunkq[9]
End Type

;����������
Type var
 Field name$,value
End Type

;�������
Type func
 Field firstcmd.cmd,cmdq,name$,param.var,snum.var
End Type

;�������� ����������
Repeat
 Read name$
 If name$="" Then Exit
 addvar name$
Forever

;������ ������ ��� ������� (������� ��������)
Repeat
 Read m$
 If m$="" Then Exit
 If Left$(m$,1)="%" Then
  brc1=Instr(m$,"{")
  param.var=Null
  snum.var=Null
  If brc1=0 Then
   fname$=Mid$(m$,2)
  Else
   fname$=fromto$(m$,2,brc1-1)
   comma=Instr(m$,",",brc1)
   brc2=Instr(m$,"}")
   If comma>0 And brc2>comma Then
    vname$=fromto$(m$,comma+1,brc2-1)
    snum.var=findvar(vname$)
   End If
   If comma>brc1+1 Then
    vname$=fromto$(m$,brc1+1,comma-1)
    param.var=findvar(vname$)
   ElseIf comma=0 Then
    vname$=fromto$(m$,brc1+1,brc2-1)
    param.var=findvar(vname$)
   End If
  End If
  f.func=addfunc(fname$,param,snum)
 End If
Forever

;������ ������ ��� ������� (������� ������ ������)
Restore func
f=Null
Repeat
 Read m$
 If m$="" Then Exit
 If Left$(m$,1)="%" Then
  If f=Null Then f=First func Else f=After f
  If debug And vfuncnames Then
   If f\param<>Null Then mm$="("+f\param\name$+")" Else mm$=""
   If f\snum<>Null Then mm$=mm$+"{"+f\snum\name$+"}"
   DebugLog "------------"+f\name$+mm$+"------------"
  End If
 Else
  addcmd(f,m$)
 End If
Forever

Graphics 800,600
SetFont LoadFont("Arial Cyr",14)

;������ ������
gender=ask("��� ���","����� ����",0)
fam=ask("���� �������","�������",gender)
If gender=1 Then 
 ask "���� ���","�����������",1
Else
 ask "���� ���","��� (�)",1
End If
ask "���� ��������","�����������",2+(gender=2)

Cls
If debug Then DebugLog "----------------------------------------"
Repeat 
 Print m$+phrase$(findfunc("���������"))
Until WaitKey()=27

End

;����������
Data "�����","���","���","�����"
Data "���� ���", "���� �������", "���� ��������", "��� ���",""

.func
;������� � ���������
Data "%���������"
Data  "����������, [��{�����=1}]."
Data  "�����������, [��{�����=1}]."
Data  "������ ����, [��{�����=1}] [��]."
Data  "������, [��{�����=1}]."
Data  "��� �����, [��{�����=1}]?"
Data  "��� �� ��� [��{�����=1}] [��] [��]?"
Data  "����� �� ���� ����� [���{�����=4;�����=0}] [��������]."
Data  "[��{�����=1}], ������ � [���{�����=3;�����=0}]."
Data  "�� � [��{�����=5}] ������ ������."
Data  "��������� � [�������{�����=6;�����=0}] [���] [��������]"

Data "%����� ����"
Data  "�������","�������"

Data "%���{���}"
Data  "1[�����������{1}]2[��� (�)]"
Data "%��������{���}"
Data  "1[�����������{2}]2[�����������{3}]"
Data "%������. ���{���}"
Data  "1[������. ��� (�)]2[������. ��� (�)]"
Data "%������. ���{���}"
Data  "1[������. ��� (�)]2[������. ��� (�)]"

Data "%��"
Data  "[�������{��� ���;�����=���� �������}]"
Data "%��"
Data  "[���{��� ���;�����=���� ���}]"
Data "%��"
Data  "[������. ���{��� ���;�����=���� ���}]"
Data "%��"
Data  "[������. ���{��� ���;�����=���� ���}]"
Data "%��"
Data  "[��������{��� ���;�����=���� ��������}]"

Data "%�����������{���,�����}"
Data  "������1[�]2����[����]3���[����]","�����1[�]2����[����]3���[����]"
Data  "�������1[_]2����[����]3���[����]","����1[_]2����[����]3���[����]"
Data  "����1[_]2����[����]3���[����]","�����1[_]2����[����]3���[����]"
Data  "���1��[_]2������[����]3�����[����]","������1[�]2����[����]3���[����]"
Data  "�������1[_]2����[����]3���[����]","�����1[_]2����[����]3���[����]"
Data  "������1[�]2����[����]3���[����]","����1��[_]2������[����]3�����[����]"
Data  "���1[�]2��[��]3����[���]","���1[��]2�����[����]3����[����]"
Data  "����1[_]2����[����]3���[����]","����1[_]2�����[����]3����[����]"
Data  "�����1[_]2����[����]3���[����]","�����1[_]2����[����]3���[����]"
Data  "����1[_]2����[����]3���[����]","�����1[_]2�����[����]3����[����]"

Data "%������. ��� (�){,�����}"
Data  "���[�3]","������[�3]","�������","������[�3]"
Data  "���[�]","���[�]","�����[_]","�����[�3]"
Data  "�������[�3]","�����[�3]","����[�]","���[�3]"
Data  "����[�3]","���[�3]","���[�]","��[�3]"
Data  "���[�]","��[�]","����[_]","�����[_]"

Data "%������. ��� (�){,�����}"
Data  "���[�3]","������[�3]","�������","������[�3]"
Data  "����","���[�]","�����[_]","�����[�3]"
Data  "�������[�3]","�����[�3]","����[�]","���[�3]"
Data  "����[�3]","���[�3]","���[�]","��[�3]"
Data  "���[�]","��[�]","����[_]","�����[_]"

Data "%��� (�){,�����}"
Data  "����[�]","���[�2]","�������[�2]","�����[�2]","������[�2]","��[�2]"

Data "%������. ��� (�),������. ��� (�){,�����}"
Data  "��[�]","��[�]","���[�2]","�����[�2]","���[�2]","��[�2]"

Data "%�������{���,�����}"
Data  "�����1[��]2[���]","������1[��]2[���]","����������1[��]2[���]"
Data  "��������1[��]2[���]","�������1[��]2[���]","�����������1[��]2[���]"
Data  "������1[��]2[���]","�������1[��]2[���]","�������1[��]2[���]"
Data  "�������1[��]2[���]","��������1[��]2[���]","������1[��]2[���]"
Data  "�����1[�]2�","�������1[��]2[���]","�����1[��]2[���]","�������1[��]2[���]"
Data  "��������","��������1[��]2[���]","��������1[��]2[���]"
Data  "��������1[��]2[���]","�����1[��]2[���]","��������1[��]2[���]"
Data  "���������1[��]2[���]","������1[��]2[���]","������1[��]2[���]"
Data  "������1[��]2[���]","�����1[��]2[���]","������1[��]2[���]"
Data  "��������1[��]2[���]","���������1[��]2[���]","��������"
Data  "�������1[��]2[���]","�������1[��]2[��]","�����1[��]2[���]"
Data  "��������1[��]2[���]","����������1[��]2[��]","�������1[��]2[���]"
Data  "�����1[��]2[���]","��������1[��]2[���]","���������1[��]2[���]"
Data  "�������1[��]2[���]","�����1[��]2[���]","�������1[��]2[���]"
Data  "������1[��]2[��]","���������1[��]2[���]","������1[��]2[���]"
Data  "�����1[��]2[���]","��������1[��]2[���]","�������1[��]2[���]"
Data  "��������1[��]2[���]","��������1[��]2[���]"

;���������
Data "%��,��{�����}"
Data  "2�3�4�5��6�"
Data "%����,����{�����}"
Data  "2�3�4�5��6�"
Data "%_,��,��,��,��,��{�����}"
Data  "2�3�4�5��6�"
Data "%��{�����}"
Data  "2�3�4�5��6�"
Data "%��{�����}"
Data  "1��2��3��4��5��6��"
Data "%�{�����}"
Data  "1�2�3�4�5��6�"
Data "%�{�����}"
Data  "1�2�3�4�5��6�"
Data "%�2{�����}"
Data  "1�2�3�4�5��6�"
Data "%�3{�����}"
Data  "1�2�3�4�5��6�"
Data "%�{�����}"
Data  "1�2�3�4�5��6�"
Data "%����,����{�����}"
Data  "1�2�3�4�5��6�"
Data "%���,���,���{�����}"
Data  "1�2��3��4�5��6��"
Data "%��{�����}"
Data  "1��2���3���4���5��6��"
Data "%��{�����}"
Data  "1��2��3��4��5��6��"
Data "%�{�����}"
Data  "1�2�3�4�5��6�"
Data "%��{�����}"
Data  "1��2��3��4��5���6��"

Data ""

;������� ���������� ����� �������� ������ � ������� pfrom �� pto
Function fromto$(v$,pfrom,pto)
Return Mid$(v$,pfrom,pto-pfrom+1)
End Function

;���������� ����� ���������� (���)
Function addvar.var(name$)
v.var=New var
v\name$=name$
v\value=1
Return v
End Function

;���������� ����� ������� (���,��������,���������� ��� ������
Function addfunc.func(name$,param.var,snum.var)
f.func=New func
f\param=param
f\snum=snum
f\name$=","+name$+","
If debug And vfuncnames Then DebugLog "{"+name$+"}"
Return f
End Function

;���������� ����� ������� (�������,������ ������)
Function addcmd.cmd(f.func,m$)
v.var=Null
vfrom.var=Null
f2.func=Null
c.cmd=New cmd
If f\firstcmd=Null Then f\firstcmd=c
m$=m$+"["
;������
For n=1 To Len(m$)
 mm$=Mid$(m$,n,1)
 Select mm$
  Case "["
   atcmd=1
   If txt$<>"" Then addchunk c,cn,Null,Null,0,txt$,Null
   txt$=""
  Case "]"
   If f2=Null Then f2=findfunc(txt$)
   addchunk c,cn,f2,Null,0,"",Null
   atcmd=0
   f2=Null
   txt$=""
  Case "{"
   f2=findfunc(txt$)
   v=f2\param
   txt$=""
  Case "="
   v=findvar(txt$)
   txt$=""
  Case "}",";"
   If Instr("0123456789",Left$(txt$,1)) Then
    addchunk c,cn,Null,v,txt$,"",Null
    If debug And vvars Then DebugLog f\name$+"("+v\name$+"="+txt$+")"
   Else
    vfrom=findvar(txt$)
    addchunk c,cn,Null,v,0,"",vfrom
    If debug And vvars Then DebugLog f\name$+"("+v\name$+"="+vfrom\name$+")"
    vfrom=Null
   End If
   txt$=""
   v=Null
  Default
   If Asc(mm$)>=49 And Asc(mm$)<=57 And atcmd=0 Then
    If txt$<>"" Then addchunk c,cn,Null,Null,0,txt$,Null
    cn=mm$
    txt$=""
   Else
    txt$=txt$+mm$
   End If
 End Select
 
Next
f\cmdq=f\cmdq+1
Return c
End Function

;���������� ����� �������
Function addchunk(c.cmd,n,f.func,v.var,value,txt$,vfrom.var)
ch.chunk=New chunk
ch\f=f
ch\v=v
ch\value=value
ch\txt$=txt$
ch\vfrom=vfrom
If c\chunk[n]=Null Then c\chunk[n]=ch
c\chunkq[n]=c\chunkq[n]+1
If debug And vchunks Then
 m$=n+","+c\chunkq[n]+":"
 If f<>Null Then m$=m$+"func "+ch\f\name$+";"
 If vfrom<>Null Then
  m$=m$+"var "+ch\v\name$+"="+ch\vfrom\name$+"; "
 ElseIf v<>Null Then
  m$=m$+"var "+ch\v\name$+"="+ch\value+"; "
 End If
 DebugLog m$+"'"+txt$+"'"
End If
End Function

;����� ������� �� ��������
Function findfunc.func(funcname$)
funcname$=","+funcname$+","
For f.func=Each func
 If Instr(f\name$,funcname$) Then Return f
Next
If f=Null Then DebugLog "Function ["+funcname$+"] not found":Stop
End Function

;����� ���������� �� ��������
Function findvar.var(vname$)
;DebugLog vname$
For v.var=Each var
 If v\name$=vname$ Then Return v
Next
If v=Null Then DebugLog "Variable {"+vname$+"} not found":Stop
End Function

;����� ����� (�������, �������� ���������, ����� �������)
Function phrase$(f.func,pvalue=0,num=0)
If f\snum<>Null And num=0 Then num=f\snum\value
If num=0 Then cn=Rand(1,f\cmdq) Else cn=num

cmd.cmd=f\Firstcmd
For n=2 To cn
 cmd=After cmd
Next

If f\param<>Null And pvalue>0 Then f\param\value=pvalue
For n=0 To 1
 If f\param=Null Then chn=n Else chn=n*f\param\value
 ch.chunk=cmd\chunk[chn]
 If debug And vprocessing And f\param<>Null Then
  DebugLog f\name$+" param {"+f\param\name$+"="+f\param\value+"}"
 End If
 For nn=1 To cmd\chunkq[chn]
  If debug And vprocessing Then
   m$=f\name$+":"+chn+"("+nn+" of "+cmd\chunkq[chn]+"):"
   If ch\f<>Null Then m$=m$+"func "+ch\f\name$
   If ch\vfrom<>Null Then
    m$=m$+"("+ch\v\name$+"="+ch\vfrom\name$+")"
   ElseIf ch\v<>Null
    m$=m$+"("+ch\v\name$+"="+ch\value+")"
   End If
   DebugLog m$+"'"+txt$+"'"
  End If
  If ch\vfrom<>Null Then
   ch\v\value=ch\vfrom\value
  ElseIf ch\v<>Null Then
   ch\v\value=ch\value
  End If
  If ch\f<>Null Then p$=p$+phrase$(ch\f,0)
  p$=p$+ch\txt$
  ch=After ch
 Next
Next
Return p$
End Function

;��������� �������� ���������� �� ��������
Function setvar(name$,value)
v.var=findvar(name$)
v\value=value
End Function

;������� ��� ����� �������
Function testpad()
f.func=First func
c.cmd=First cmd
pad.var=findvar("�����")
Repeat
 For n=1 To f\cmdq
  Cls
  pad\value=1
  Text 0,0,phrase$(f,n)
  pad\value=2
  Text 0,14,"����� ��� "+phrase$(f,n)
  pad\value=3
  Text 0,28,"����� � "+phrase$(f,n)
  pad\value=4
  Text 0,42,"���������� �� "+phrase$(f,n)
  pad\value=5
  Text 0,56,"������ ����� "+phrase$(f,n)
  pad\value=6
  Text 0,70,"�������� � "+phrase$(f,n)
  WaitKey
 Next
 f=After f
Until f=Null
End Function

;������ ������ ������� (��� ����������, ��� �������, �������� ���������)
; ��� ���������� - ���, ������� ����� ��������
Function ask(vname$,fname$,pvalue)
v.var=findvar(vname$)
f.func=findfunc(fname$)
Cls
Text 0,0,fname$
For n=1 To f\cmdq
 Text 0,10*n,n+") "+phrase$(f,pvalue,n)
Next
Repeat
 If MouseHit(1) Then i=Floor(MouseY()/10)
 If i>0 And i<n Then Exit
Forever
v\value=i
Return i
End Function