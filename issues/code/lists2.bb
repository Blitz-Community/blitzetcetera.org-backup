<code bb>
Type TList
Field Start.TItem,Item.TItem
End Type
Type TItem
Field c% 
Field N.TItem
End Type 

; Переменная списка - может быть сколько угодно разных
Global MyList.TList = New TList
Global MyList2.TList = New TList

For i=1 To 10
Add(MyList,i) ; Добавляет i в список List
Next

For i=1 To 10
Add(MyList2,i+15) ; Добавляет i в список List
Next

Start(MyList) ; Устанавливает список на начало
While Not EofList(MyList) ; Пока список не закончился
NextItem(MyList) ; Следующий элемент
Print Item(MyList)
Wend 

Function Add(List.TList,i)
Local a.TItem
a.TItem = New TItem
a\c=i
If List\Item=Null Then List\Item=New TItem:List\Start=List\Item
List\Item\n=a
List\Item=a
End Function

Function Start(List.TList)
List\Item=List\Start
End Function

Function NextItem(List.TList)
List\Item=List\Item\n
End Function

Function EofList(List.TList)
If List\Item\n=Null Then Return True Else Return False
End Function

Function Item(List.TList)
Return List\Item\c
End Function 
</code><noinclude>[[Категория:Код]]</noinclude>