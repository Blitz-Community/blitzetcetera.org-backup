Include "Menu.bb"

Global ent$=Chr$(13)+Chr$(10),filename$

Global win=CreateWindow("",ClientWidth(Desktop())/2-300,ClientHeight(Desktop())/2-200,600,400,0,7)
filename_change "untitled.txt"
Menu_Create win
Global ta=CreateTextArea(0,0,10,10,win)
ta_resize()

Repeat
 Select WaitEvent()
  Case $1001
   Select menuitem_name(EventData())
    Case "Новый"
     filename_change "untitled.txt"
     SetTextAreaText ta,""
    Case "Открыть..."
     file$=RequestFile("Открыть файл...","txt,*")
     If file$<>"" Then doc_load file$
    Case "Сохранить..."
     doc_save
    Case "Сохранить как..."
     file$=RequestFile("Сохранить файл как...","txt,*",True)
     If file$<>"" Then
      filename_change file$
      doc_save
     End If
    Case "Выход":Exit
    Case "Помощь":Notify "звони в 03 ((C) В.Н.Машков)"
    Case "О программе...":Notify "Простой текстовый редактор"+ent$+"Автор: Матвей Меркулов"
   End Select
  Case $802:ta_resize()
  Case $803:Exit
 End Select
Forever

Data "Файл","{","Новый","Открыть...","Сохранить...","Сохранить как...","","Выход","}"
Data "Справка","{","Помощь","О программе...","}"
Data "===Конец==="

Function ta_resize()
SetGadgetShape ta,0,0,ClientWidth(win),ClientHeight(win)
End Function

Function filename_change(file$)
filename$=file$
SetGadgetText win,"Простой текстовый редактор - "+filename$
End Function

Function doc_load(file$)
filename_change file$
f=ReadFile(file$)
LockTextArea ta
SetTextAreaText ta,""
While Not Eof(f)
 AddTextAreaText ta,ReadLine$(f)
 If Not Eof(f) Then AddTextAreaText ta,ent$
Wend
CloseFile f
UnlockTextArea ta
End Function

Function doc_save()
f=WriteFile(filename$)
WriteLine f,TextAreaText$(ta)
CloseFile f
End Function