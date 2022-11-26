Data "'В теме (ссылка) было удалено мое сообщение, следующее за сообщением номер ##. Я же "
Data "считаю, что мое сообщение было конкретным ответом по теме, и модератор не имел "
Data "оснований для его удаления, ибо сообщение не противоречило ни одному из правил "
Data "форума.' Если Ваш вопрос будет похож на пример 1, то не ждите, что на него ответят. В "
Data "вопросе должна ОБЯЗАТЕЛЬНО присутствовать ссылка на тему с проблемой. "
Data "Абстрактные проблемы типа: 'ну что-то у вас модеры совсем разбаловались, чистят все "
Data "подряд.' рассматриваться НЕ будут. НЕ стоит в качестве мотивации своей правоты "
Data "приводить случаи с другими пользователями или намекать на какие-либо другие "
Data "проблемы. Ибо речь идет не о тех проблемах, и описание других проблем будет флудом, "
Data "который просто-напросто удалят."
;===========================
Graphics 800,600,32,2
SetBuffer BackBuffer()
font=LoadFont("Arial cyr",20)
SetFont font
;формирование строки
Local InputString$=""
temp$=""
For i=1 To 10
Read temp
InputString$=InputString$+temp
Next
DebugLog InputString$
;============================
Local SEPARATOR$=" ";<= разделитель
Local MAXLENPIX%=400+FontWidth();<= длина нарезки
LNINSTR%=Len(InputString$)
Dim MAP$(LNINSTR%);<= ресивер
Local ST%=1
Local LINDX%=1
Local CINDX%=1
Local MapPtr%=0;<=индекс заполнения ресивера

While True
CINDX=Instr(InputString$,SEPARATOR$,LINDX)
If CINDX=0
CINDX=LNINSTR
temp=Mid(InputString$,ST,CINDX-ST-1)
If StringWidth(temp)>MAXLENPIX
MAP(MapPtr)=Mid(InputString$,ST,LINDX-ST-1)
MapPtr=MapPtr+1
ST=LINDX
EndIf
MAP(MapPtr)=Mid(InputString$,ST,CINDX-ST)
MapPtr=MapPtr+1
Exit
EndIf
temp=Mid(InputString$,ST,CINDX-ST-1)
If StringWidth(temp)>MAXLENPIX
MAP(MapPtr)=Mid(InputString$,ST,LINDX-ST-1)
MapPtr=MapPtr+1
ST=LINDX
EndIf
LINDX=CINDX+1
Wend
;======================
;вывод
Line MAXLENPIX,0,MAXLENPIX,GraphicsHeight()
For i=0 To MapPtr
Text 0,i*FontHeight(),MAP(i)
Next
Flip
WaitKey()
End