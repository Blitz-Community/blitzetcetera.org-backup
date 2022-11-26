Type menuitem
 Field name$
End Type

Function Menu_Create(w)
wmenu=WindowMenu(w)
parent=wmenu
Repeat
 Read m$
 Select m$
  Case "{": parent=old
  Case "}": parent=wmenu
  Case "===Конец===": Exit
  Default
   If parent<>wmenu Then
    i=i+1
    mi.menuitem=New menuitem
    mi\name$=m$
   End If
   old=CreateMenu(m$,i,parent)
 End Select
Forever
UpdateWindowMenu(w)
End Function

Function menuitem_name$(n)
mi.menuitem=First menuitem
For nn=2 To n
 If mi=Null Then Notify "Menuitem N"+n+" doesn't exist":Return
 mi=After mi
Next
Return mi\name$
End Function