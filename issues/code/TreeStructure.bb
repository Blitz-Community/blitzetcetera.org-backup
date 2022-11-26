<code bb>
Type element
 ;���������������� ���� ��������
 Field r,g,b
 ;��������������� ���� ��������
 Field root.element,prev.element,nxt.element,sub.element
End Type

;�������� �������
Global root.element=New element

Global ex,ey,sel.element

Graphics 800,600
branchcreate root,9
sel=root\sub
SetBuffer BackBuffer()
Repeat
 Cls
 ex=0:ey=0
 branchview root
 Flip
 Select WaitKey()
  Case 3;Ins
   sel=einsertin(Null,sel)
  Case 4;Del
   sel2.element=Null
   If sel\prev<>Null Then
    sel2=sel\prev
   ElseIf sel\nxt<>Null Then
    sel2=sel\nxt
   ElseIf sel\root<>root
    sel2=sel\root
   End If
   If sel2<>Null Then eremove sel:sel=sel2
  Case 5;Page Up
   sel=einsertbefore(Null,sel)
  Case 6;Page Down
   sel=einsertafter(Null,sel)
  Case 27;Esc
   Exit
  Case 28;������� �����
   If sel\prev<>Null Then sel=sel\prev
  Case 29;������� ����
   If sel\nxt<>Null Then sel=sel\nxt
  Case 30;������� ������
   If sel\sub<>Null Then sel=sel\sub
  Case 31;������� �����
   If sel\root<>root Then sel=sel\root
 End Select
Forever

;�������, ��������� ����� � ���������� �� �������� e (k-�������� ���������)
Function branchcreate(e.element,k)
q=Rand(1,k)
;������� n ���������
For n=1 To q
 e2.element=einsertin(Null,e)
 ;� ����� ������� ������� ����������� �� ������� ��������, �������� ��������
 ; �������� �� 2
 If Rand(1,3)=1 Then branchcreate e2,k-2
Next
End Function

;����������� ����� (������������ ��������)
Function branchview(e.element)
ex=ex+35
e=e\sub
ey1=ey-6
ey2=ey1
While e<>Null
 Line ex-20,ey+10,ex+15,ey+10
 ;��������� �������� ��������
 If e=sel Then c=127 Else c=0
 ;���� ���� �������� �� ����� - ������ ��������� �������
 If e\r=0 Then
  e\r=Rand(1,128)
  e\g=Rand(1,128)
  e\b=Rand(1,128)
 End If
 Color c+e\r,c+e\g,c+e\b
 Rect ex,ey,30,20
 Color 255,255,255
 Rect ex,ey,30,20,False
 ey2=ey+10
 ey=ey+25
 branchview e
 e=e\nxt
Wend
Line ex-20,ey1,ex-20,ey2
ex=ex-35
End Function

;������� �������� ����� ��������
Function einsertafter.element(what.element,afterwhat.element)
;���� ������� �� ����� - ������� ���, ����� ��������� ������� �� ��� ������
If what=Null Then what=New element Else epush what
;��������� ����� ������� � ������� � ������ � ������
what\prev=afterwhat
what\nxt=afterwhat\nxt
If afterwhat\nxt<>Null Then afterwhat\nxt\prev=what
afterwhat\nxt=what
;������ ������ � ���������� ��������� �� �������
what\root=afterwhat\root
Return what
End Function

;������� �������� ����� ���������
Function einsertbefore.element(what.element,beforewhat.element)
If what=Null Then what=New element Else epush what
what\prev=beforewhat\prev
what\nxt=beforewhat
If beforewhat\prev<>Null Then beforewhat\prev\nxt=what
what\root=beforewhat\root
;���� ������� ���������� ����� ������ � ������ - ��������� � ��� ������
If what\prev=Null Then what\root\sub=what
beforewhat\prev=what
Return what
End Function

;������� �������� � ������ ���������
Function einsertin.element(what.element,inwhat.element)
If what=Null Then what=New element Else epush what
;�������� ������� � ������ ������
what\prev=Null
If inwhat\sub=Null Then
 what\nxt=Null
Else
 ;���� ������ �� ����� - ������� ������ ������� ����, �������� ��� � �����
 what\nxt=inwhat\sub
 inwhat\sub\prev=what
End If
;��������� ������ ������ � ����� ��������� (������ ������ � ������)
inwhat\sub=what
what\root=inwhat
Return what
End Function

;�������� �������� ������ � ��� �������������
Function eremove(what.element,care=True)
;��������� ������� ������� �� ��� ������
If care Then epush what
e.element=what\sub
;���� ������� �������� ��������� - ������� ��, ��������� ��������
While e<>Null
 e2.element=e
 e=e\nxt
 ;��������� �������� �� ��������� � ���������� �������� �� ������
 eremove e2,False
Wend
Delete what
End Function

;��������������� ������� - ���������� �������� ��������� �� ������
Function epush(what.element)
;��������� ������� � ������ �������� ���� � ������
;���� ������� ��������� � ����� ������ - ��������� � ��� ������ ������
If what\prev<>Null Then what\prev\nxt=what\nxt Else what\root\sub=what\nxt
If what\nxt<>Null Then what\nxt\prev=what\prev
End Function
</code><noinclude>[[���������:���]]</noinclude>