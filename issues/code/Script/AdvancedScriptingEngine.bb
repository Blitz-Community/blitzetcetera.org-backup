SeedRnd MilliSecs()

Const vfuncnames=1,vchunks=2,vprocessing=4,vvars=8
Const maxparam=2,debug=0;vchunks+vprocessing;+vfuncnames+vchunks

;Кусок команды
Type chunk
 Field txt$,f.func,v.var,value,vfrom.var
End Type

;Команда
Type cmd
 Field chunk.chunk[9],chunkq[9]
End Type

;Переменная
Type var
 Field name$,value
End Type

;Функция
Type func
 Field firstcmd.cmd,cmdq,name$,param.var,snum.var
End Type

;Создание переменных
Repeat
 Read name$
 If name$="" Then Exit
 addvar name$
Forever

;Первый проход для функций (создает элементы)
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

;Второй проход для функций (создает наборы команд)
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

;Запрос данных
gender=ask("ваш пол","выбор пола",0)
fam=ask("ваша фамилия","фамилия",gender)
If gender=1 Then 
 ask "ваше имя","имяотчество",1
Else
 ask "ваше имя","имя (ж)",1
End If
ask "ваше отчество","имяотчество",2+(gender=2)

Cls
If debug Then DebugLog "----------------------------------------"
Repeat 
 Print m$+phrase$(findfunc("обращение"))
Until WaitKey()=27

End

;Переменные
Data "падеж","тип","пол","номер"
Data "ваше имя", "ваша фамилия", "ваше отчество", "ваш пол",""

.func
;Функции с командами
Data "%обращение"
Data  "Здравствуй, [ву{падеж=1}]."
Data  "Приветствую, [вф{падеж=1}]."
Data  "Добрый день, [ви{падеж=1}] [во]."
Data  "Привет, [ви{падеж=1}]."
Data  "Как жизнь, [вп{падеж=1}]?"
Data  "Кто из вас [вф{падеж=1}] [ви] [во]?"
Data  "Нигде не могу найти [имя{падеж=4;номер=0}] [отчество]."
Data  "[вп{падеж=1}], пойдем к [имя{падеж=3;номер=0}]."
Data  "Мы с [вф{падеж=5}] сейчас придем."
Data  "Поговорим о [фамилия{падеж=6;номер=0}] [имя] [отчество]"

Data "%выбор пола"
Data  "Мужской","Женский"

Data "%имя{пол}"
Data  "1[имяотчество{1}]2[имя (ж)]"
Data "%отчество{пол}"
Data  "1[имяотчество{2}]2[имяотчество{3}]"
Data "%уменьш. имя{пол}"
Data  "1[уменьш. имя (м)]2[уменьш. имя (ж)]"
Data "%панибр. имя{пол}"
Data  "1[панибр. имя (м)]2[панибр. имя (ж)]"

Data "%вф"
Data  "[фамилия{ваш пол;номер=ваша фамилия}]"
Data "%ви"
Data  "[имя{ваш пол;номер=ваше имя}]"
Data "%ву"
Data  "[уменьш. имя{ваш пол;номер=ваше имя}]"
Data "%вп"
Data  "[панибр. имя{ваш пол;номер=ваше имя}]"
Data "%во"
Data  "[отчество{ваш пол;номер=ваше отчество}]"

Data "%имяотчество{тип,номер}"
Data  "Алексе1[й]2евич[евич]3евн[евна]","Андре1[й]2евич[евич]3евн[евна]"
Data  "Евдоким1[_]2ович[ович]3овн[овна]","Карл1[_]2ович[ович]3овн[овна]"
Data  "Иван1[_]2ович[ович]3овн[овна]","Федор1[_]2ович[ович]3овн[овна]"
Data  "Дав1ид[_]2ыдович[ович]3ыдовн[овна]","Тимофе1[й]2евич[евич]3евн[евна]"
Data  "Харитон1[_]2ович[ович]3овн[овна]","Антон1[_]2ович[ович]3овн[овна]"
Data  "Гаврил1[а]2ович[ович]3овн[овна]","Миха1ил[_]2йлович[ович]3йловн[овна]"
Data  "Иль1[я]2ич[ич]3инич[ина]","Пав1[ел]2лович[ович]3ловн[овна]"
Data  "Петр1[_]2ович[ович]3овн[овна]","Яков1[_]2левич[евич]3левн[евна]"
Data  "Семен1[_]2ович[ович]3овн[овна]","Исаак1[_]2ович[ович]3овн[овна]"
Data  "Марк1[_]2ович[ович]3овн[овна]","Игнат1[_]2ьевич[евич]3ьевн[евна]"

Data "%уменьш. имя (м){,номер}"
Data  "Леш[а3]","Андрюш[а3]","Евдоким","Карлуш[а3]"
Data  "Ван[я]","Фед[я]","Давид[_]","Тимош[а3]"
Data  "Харитош[а3]","Антош[а3]","Гавр[я]","Миш[а3]"
Data  "Илюш[а3]","Паш[а3]","Пет[я]","Яш[а3]"
Data  "Сен[я]","Из[я]","Марк[_]","Игнат[_]"

Data "%панибр. имя (м){,номер}"
Data  "Лех[а3]","Андрюх[а3]","Евдоким","Карлух[а3]"
Data  "Вано","Фед[я]","Давид[_]","Тимох[а3]"
Data  "Харитох[а3]","Антох[а3]","Гавр[я]","Мих[а3]"
Data  "Илюх[а3]","Пах[а3]","Пет[я]","Яш[а3]"
Data  "Сен[я]","Из[я]","Марк[_]","Игнат[_]"

Data "%имя (ж){,номер}"
Data  "Ольг[а]","Анн[а2]","Серафим[а2]","Марин[а2]","Зинаид[а2]","Ид[а2]"

Data "%уменьш. имя (ж),панибр. имя (ж){,номер}"
Data  "Ол[я]","Ан[я]","Фим[а2]","Марин[а2]","Зин[а2]","Ид[а2]"

Data "%фамилия{пол,номер}"
Data  "Орлов1[ов]2[ова]","Крылов1[ов]2[ова]","Спиридонов1[ов]2[ова]"
Data  "Михайлов1[ов]2[ова]","Круглов1[ов]2[ова]","Перехрестов1[ов]2[ова]"
Data  "Петров1[ов]2[ова]","Камаров1[ов]2[ова]","Семенов1[ов]2[ова]"
Data  "Кушаков1[ов]2[ова]","Петраков1[ов]2[ова]","Пушкин1[ин]2[ина]"
Data  "Гогол1[ь]2ь","Калугин1[ин]2[ина]","Мясов1[ов]2[ова]","Макаров1[ов]2[ова]"
Data  "Петерсен","Горбунов1[ов]2[ова]","Притыкин1[ин]2[ина]"
Data  "Серпухов1[ов]2[ова]","Куров1[ов]2[ова]","Тикакеев1[ев]2[ева]"
Data  "Коратыгин1[ин]2[ина]","Машкин1[ин]2[ина]","Кошкин1[ин]2[ина]"
Data  "Марков1[ов]2[ова]","Окнов1[ов]2[ова]","Козлов1[ов]2[ова]"
Data  "Стрючков1[ов]2[ова]","Мотыльков1[ов]2[ова]","Ковшегуб"
Data  "Сусанин1[ин]2[ина]","Жуковск1[ий]2[ая]","Жуков1[ов]2[ова]"
Data  "Захарьин1[ин]2[ина]","Петрушевск1[ий]2[ая]","Комаров1[ов]2[ова]"
Data  "Зубов1[ов]2[ова]","Ромашкин1[ин]2[ина]","Фетелюшин1[ин]2[ина]"
Data  "Комаров1[ов]2[ова]","Пакин1[ин]2[ина]","Ракукин1[ин]2[ина]"
Data  "Немецк1[ий]2[ая]","Григорьев1[ев]2[ева]","Шашкин1[ин]2[ина]"
Data  "Мышин1[ин]2[ина]","Коршунов1[ов]2[ова]","Кулыгин1[ин]2[ина]"
Data  "Селезнев1[ев]2[ева]","Кузнецов1[ов]2[ова]"

;Окончания
Data "%ов,ев{падеж}"
Data  "2а3у4а5ым6е"
Data "%ович,евич{падеж}"
Data  "2а3у4а5ем6е"
Data "%_,ен,им,уб,ан,ич{падеж}"
Data  "2а3у4а5ом6е"
Data "%ин{падеж}"
Data  "2а3у4а5ым6е"
Data "%ей{падеж}"
Data  "1ей2ея3ею4ея5ем6ее"
Data "%ь{падеж}"
Data  "1ь2я3ю4я5ем6е"
Data "%а{падеж}"
Data  "1а2и3е4у5ой6е"
Data "%а2{падеж}"
Data  "1а2ы3е4у5ой6е"
Data "%а3{падеж}"
Data  "1а2и3е4у5ей6е"
Data "%я{падеж}"
Data  "1я2и3е4ю5ей6е"
Data "%овна,евна{падеж}"
Data  "1а2ы3е4у5ой6е"
Data "%ова,ева,ина{падеж}"
Data  "1а2ой3ой4у5ой6ой"
Data "%ий{падеж}"
Data  "1ий2ого3ому4ого5им6ом"
Data "%ая{падеж}"
Data  "1ая2ой3ой4ую5ой6ой"
Data "%й{падеж}"
Data  "1й2я3ю4я5ем6е"
Data "%ел{падеж}"
Data  "1ел2ла3лу4ла5лом6ле"

Data ""

;Функция возвращает часть исходной строки с символа pfrom до pto
Function fromto$(v$,pfrom,pto)
Return Mid$(v$,pfrom,pto-pfrom+1)
End Function

;Добавление новой переменной (имя)
Function addvar.var(name$)
v.var=New var
v\name$=name$
v\value=1
Return v
End Function

;Добавление новой функции (имя,параметр,переменная для номера
Function addfunc.func(name$,param.var,snum.var)
f.func=New func
f\param=param
f\snum=snum
f\name$=","+name$+","
If debug And vfuncnames Then DebugLog "{"+name$+"}"
Return f
End Function

;Добавление новой команды (функция,строка данных)
Function addcmd.cmd(f.func,m$)
v.var=Null
vfrom.var=Null
f2.func=Null
c.cmd=New cmd
If f\firstcmd=Null Then f\firstcmd=c
m$=m$+"["
;Парсер
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

;Добавление куска команды
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

;Поиск функции по названию
Function findfunc.func(funcname$)
funcname$=","+funcname$+","
For f.func=Each func
 If Instr(f\name$,funcname$) Then Return f
Next
If f=Null Then DebugLog "Function ["+funcname$+"] not found":Stop
End Function

;Поиск переменной по названию
Function findvar.var(vname$)
;DebugLog vname$
For v.var=Each var
 If v\name$=vname$ Then Return v
Next
If v=Null Then DebugLog "Variable {"+vname$+"} not found":Stop
End Function

;Вывод фразы (функция, значение параметра, номер команды)
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

;Установка значения переменной по названию
Function setvar(name$,value)
v.var=findvar(name$)
v\value=value
End Function

;Функция для теста падежей
Function testpad()
f.func=First func
c.cmd=First cmd
pad.var=findvar("падеж")
Repeat
 For n=1 To f\cmdq
  Cls
  pad\value=1
  Text 0,0,phrase$(f,n)
  pad\value=2
  Text 0,14,"Здесь нет "+phrase$(f,n)
  pad\value=3
  Text 0,28,"Пойти к "+phrase$(f,n)
  pad\value=4
  Text 0,42,"Посмотреть на "+phrase$(f,n)
  pad\value=5
  Text 0,56,"Стоять перед "+phrase$(f,n)
  pad\value=6
  Text 0,70,"Говорить о "+phrase$(f,n)
  WaitKey
 Next
 f=After f
Until f=Null
End Function

;Запрос номера команды (имя переменной, имя функции, значение параметра)
; имя переменной - той, которая будет изменена
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