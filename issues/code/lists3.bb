<code bb>
Type Type_element
Field information%
Field ptr_next%
End Type

Function CreateList()
E.Type_element=New Type_element
Return Handle(E)
End Function

Function ADD2List(ListHandle,number%)
E.Type_element=Object.Type_element(ListHandle)
ENew.Type_element=New Type_element
ENew\information=number
ENew\ptr_next=0
;=
While E\ptr_next
E=Object.Type_element(E\ptr_next)
Wend
E\ptr_next=Handle(ENew)
;=
Return Handle(ENew)
End Function

Function OutPutList(ListHandle)
E.Type_element=Object.Type_element(ListHandle)
Local count=0
While True
E=Object.Type_element(E\ptr_next)
If E=Null Exit
count=count+1
Print E\information
Wend
Return count
End Function

Function SwapInList(ListHandle,A%,B%)
E.Type_element=Object.Type_element(ListHandle)
Local X.Type_element
Local Y.Type_element
Local count=0
While E\ptr_next
E=Object.Type_element(E\ptr_next)
count=count+1
If count=A X=E
If count=B Y=E
Wend
If X=Null Or Y=Null
Print "Error!"
Return False
EndIf
;=
swap_information=X\information
X\information=Y\information
Y\information=swap_information
Return True
End Function



MyList=CreateList()
ADD2List(MyList,1)
ADD2List(MyList,2)
ADD2List(MyList,3)
ADD2List(MyList,4)
OutPutList(MyList)
Print "====="
SwapInList(MyList,4,1)
OutPutList(MyList)
Print "====="
SwapInList(MyList,1,4)
OutPutList(MyList)

WaitKey()
End

</code><noinclude>[[Категория:Код]]</noinclude>