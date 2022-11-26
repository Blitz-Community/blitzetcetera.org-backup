Include "BList.bb" 
Type Ball 
 Field id 
 Field x# 
 Field y# 
End Type 
 
; ������� 2 ������ 
list1.BList = New BList 
list2.BList = New BList 
 
;������� 6 �������� � ������ ������ 
For i = 0 To 5 
 b.Ball = Object.Ball(AddFront(list1,Handle(New Ball))) 
 b\id = i 
Next 
 
Print "������ 1:" : printlist(list1) 
 
;������������ ������ ������ 
ResetList(list1) 
While NextItem(list1) 
 b.Ball = Object.Ball(CurrentItem(list1)) 
 ;���������� ��������� ������ �� ������ ������ 
 If b\id = 4 Then MoveItem(list1,list2) 
 ; ������� 3-� ������, ������ �����, ����� ��������� ������ 
 If b\id = 3 Then tmp.BNode = CurrentNode(list1) 
 ; ������� ������ ������ 
 If b\id = 2 Then Delete Object.Ball(KillItem(list1,Null)) 
Wend 
Delete Object.Ball(KillItem(list1,tmp)) 
 
Print "������ 1: (������� - 2,3, ����������- 4)" : printlist(list1) 
Print "������ 2:" : printlist(list2) 
 
;������� ���� ������ ������ 
While FirstItem(list1) 
 Delete Object.Ball(KillItem(list1,Null)) 
Wend 
Print "������ 1:" : printlist(list1) 
 
WaitKey 
 
Function printlist(list.BList) 
 ResetList(list) 
 While NextItem(list) 
  b.Ball = Object.Ball(CurrentItem(list)) 
  Print b\id 
 Wend 
End Function 