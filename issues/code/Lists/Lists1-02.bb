Include "BList.bb" 
Type Ball 
 Field id 
 Field x# 
 Field y# 
End Type 
 
; создаем 2 списка 
list1.BList = New BList 
list2.BList = New BList 
 
;Создаем 6 объектов в первом списке 
For i = 0 To 5 
 b.Ball = Object.Ball(AddFront(list1,Handle(New Ball))) 
 b\id = i 
Next 
 
Print "Список 1:" : printlist(list1) 
 
;Обрабатываем первый список 
ResetList(list1) 
While NextItem(list1) 
 b.Ball = Object.Ball(CurrentItem(list1)) 
 ;Перемещаем четвертую запись во второй список 
 If b\id = 4 Then MoveItem(list1,list2) 
 ; удаляем 3-ю запись, только позже, после обработки списка 
 If b\id = 3 Then tmp.BNode = CurrentNode(list1) 
 ; удаляем вторую запись 
 If b\id = 2 Then Delete Object.Ball(KillItem(list1,Null)) 
Wend 
Delete Object.Ball(KillItem(list1,tmp)) 
 
Print "Список 1: (удалены - 2,3, перемещена- 4)" : printlist(list1) 
Print "Список 2:" : printlist(list2) 
 
;очищаем весь первый список 
While FirstItem(list1) 
 Delete Object.Ball(KillItem(list1,Null)) 
Wend 
Print "Список 1:" : printlist(list1) 
 
WaitKey 
 
Function printlist(list.BList) 
 ResetList(list) 
 While NextItem(list) 
  b.Ball = Object.Ball(CurrentItem(list)) 
  Print b\id 
 Wend 
End Function 