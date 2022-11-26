<code bb>
Type element
 Field name$,flag$
 Field root.element,prev.element,nxt.element,sub.element
End Type

Type eref
 Field e.element
End Type

Type verb
 Field name$,n[1],adverb$
End Type

Type cmd
 Field e.element, v.verb, typ
End Type

Const tysiz=14,xsiz=590,ysiz=600

Global root.element=New element
Global loc.element,hands.element,body.element,all.element,shared.element
Global ty

Dim try.cmd(2), fcmd.cmd(2)

Graphics 800,600
SetBuffer FrontBuffer()
SetFont LoadFont("Arial Cyr",16)

;Загрузка окончаний
Dim en$(20,6)
Repeat
 n1=n1+1
 Read m$
 If m$="" Then Exit
 q$=""
 For n2=1 To 6
  en$(n1,n2)=rustrim$(Mid$(m$,n2*3-2,3))
 Next
Forever

;Загрузка глаголов
Repeat
 Read m$
 If m$="" Then Exit
 v.verb=New verb
 i=Instr(m$,"#")
 v\name$=Left$(m$,i-2)
 v\n[0]=Mid$(m$,i+1,1)
 ;Определение второго глагола (если присутствует)
 ii=Instr(m$,"#",i+1)
 If ii>0 Then
  v\n[1]=Mid$(m$,ii+1,1)
  v\adverb$=Mid$(m$,i+3,ii-i-3)
 End If
 ;Эта строка - для отладки
 ;Print v\name$+" "+v\n[0]+" "+v\adverb$+" "+v\n[1]
Forever
;WaitKey

;Формирование базы данных
e.element=root.element
oldsp=-1
Repeat
 Read m$
 If m$="" Then Exit
 ;Определение уровня (в зависимости от пробелов в начале)
 sp=Len(m$)-Len(rustrim$(m$))
 While sp<oldsp
  e=e\root
  oldsp=oldsp-1
 Wend
 If sp>oldsp Then
  e=einsertin(e,Null)
  oldsp=oldsp+1
 Else
  e=einsertafter(e,Null)
 End If
 ;Флаги
 i=Instr(m$,":")
 If i Then
  e\flag$=Mid$(m$,i+1)+","
  e\name$=rustrim$(Left$(m$,i-1))
 Else
  e\name$=rustrim$(m$) 
 End If
 ;Эта строка - для отладки
 ;viewpad e
Forever

shared=root\sub
all=After shared
hands=After all
body=After hands

moveplayer "комната"

Color 255,255,0
tprint "Переключите клавиатуру на русский регистр и отключите CapsLock."
tprint "Используйте кл. PgUp и PgDn для выбора подходящего слова."

look

Repeat
 inputphrase

 k=-1
 For c.cmd=Each cmd
  If c\e=all Then 
   ;Вставка объектов вместо слова "все"
   For r.eref=Each eref
    e=r\e
    If e<>all And e<>hands And e<>body Then
     cc.cmd=New cmd
     Insert cc After c
     cc\e=e
     cc\typ=c\typ
    End If
   Next   
   Delete c
  Else
   ;Определение первой команды каждого типа
   If k<>c\typ Then
    k=c\typ
    try(k)=c
    fcmd(k)=c
   End If
  End If
 Next

 ;Разбивка фразы на элементарные
 If try(0)\v\n[0]=0 Then
  processcmd
 Else
  Repeat
   processcmd
   If try(0)=Null Then Exit
   try(0)=After try(0)
   If try(0)=fcmd(1) Then
    try(0)=fcmd(0)
    try(1)=After try(1)
    If try(1)=fcmd(2) Then
     try(1)=fcmd(1)     
     If try(2)=Null Then Exit
     try(2)=After try(2)
     If try(2)=Null Then Exit
    End If 
   End If
  Forever
 End If
Forever 

;наборы окончаний для каждого из 6 падежей (по 3 символа)
Data "а  и  е  у  ой е  ";01
Data "а  ы  е  у  ой е  ";02
Data "я  и  е  ю  ей е  ";03
Data "о  а  у  о  ом е  ";04
Data "ь  и  и  ь  ью и  ";05
Data "   а  у     ом е  ";06
Data "   а  у     ем е  ";07
Data "ая ей ей ую ей ей ";08
Data "ая ой ой ую ой ой ";09
Data "ь  я  ю  ь  ем е  ";10
Data "ый огоомуый ым ом ";11
Data "ий егоемуий им ем ";12
Data "и     ам и  амиах ";13
Data "е  егоемуе  ем ем ";14
Data "ое огоомуое ым ом ";15
Data "и  ов ам и  амиах ";16
Data ""

;глаголы
Data "взять #4","брать #4","бросить #4"
Data "положить #4 на #4","класть #4 на #4"
Data "повесить #4 на #4"
Data "идти в #4","идти по #3","прыгнуть в #4","войти в #4"
Data "открыть #4","закрыть #4","отпереть #4 #5","запереть #4 #5"
Data "сломать #4 #5","разбить #4 #5"
Data "одеть #4","надеть #4","снять #4"
Data "положить #4 в #4","положить #4 под #4"
Data "осмотреть #4","обследовать #4","осмотреться #0","инвентарь #0"
Data "бросить #4 в #4"
Data "резать #4 #5"
Data ""

;иерархия объектов
Data "всегда присутствующие объекты"
Data " вс#14:С"
Data " рук#13:М"
Data " тел#04:С"
Data "  майк#01:Рук,Одеть,Ж,Сух,Рез"
Data "комнат#02:Ж"
Data " двер#05:Откр,Ж,>прихожая"
Data " шкаф#06:Закр,Кл_на,Кл_в"
Data "  дверц#02:Скр,Ж"
Data "  вешалк#01:Скр,Рук,Ж,В,Пов_на"
Data "   пиджак#06:Скр,Рук,Одеть,Вис,Сух,Рез,Карман"
Data "   брюк#13:Скр,Рук,Одеть,Вис,М,Сух,Рез,Карман"
Data "   рубашк#01:Скр,Рук,Одеть,Вис,Ж,Сух,Рез"
Data "  галстук#06:Скр,Рук,Одеть,На,Сух,Рез"
Data " окн#04:Закр,С"
Data "  стекл#04:Скр,С"
Data "  рам#02:Скр,Ж"
Data " кроват#05:Кл_на,Ж,Кл_под"
Data "  матрац#06:Скр,Рук,Кл_на,На,Сух,Кл_под,Рез"
Data " стул#06:Рук,Кл_на,Сух"
Data "прихож#08:Ж"
Data " двер#05 комнаты:Откр,Ж,>комната"
Data " двер#05 ванной:Закр,Ж,>ванная"
Data " двер#05 туалета:Закр,Ж,>туалет"
Data " двер#05 кухни:Откр,Ж,>кухня"
Data " входн#09 двер#05:Закр,Зап,Ж,>лестничная клетка"
Data " коврик#06:Рук,Кл_на,Кл_под,Сух"
Data " настенн#09 вешалк#01:Пов_на,Ж"
Data "  ключ:Скр,Вис,Кл_на,Взять"
Data "ванн#09:Ж"
Data " двер#05:Откр,Ж,>прихожая"
Data " ванн#02:Кл_в,Ж"
Data "  красн#11 вентил#10 ванны:Скр,Закр"
Data "  син#12 вентил#10 ванны:Скр,Закр"
Data "  кран#06 ванны:Скр"
Data "  вентил#10 душа:Скр,Откр"
Data "  душ#07:Скр,Пов_на"
Data "  слив#06 ванны:Скр"
Data " раковин#02:Кл_в,Ж"
Data "  красн#11 вентил#10 раковины:Скр,Закр"
Data "  син#12 вентил#10 раковины:Скр,Закр"
Data "  кран#06 раковины:Скр"
Data "  слив#06 раковины:Скр"
Data "туалет#06"
Data " двер#05:Откр,Ж,>прихожая"
Data " унитаз#06:Кл_в,Вода"
Data " бач^ок#06:Кл_на"
Data "кухн#03:Ж"
Data " двер#05:Откр,>прихожая"
Data " плит#02:Кл_на,Ж"
Data " шкафчик#06:Закр,Кл_на"
Data " окн#04:Закр,С"
Data "  стекл#04:Скр,С"
Data "  рам#02:Скр,Ж"
Data "лестничн#09 клетк#01:Ж"
Data " входн#09 двер#05:Откр,Ж,Отп,>прихожая"
Data " красн#09 двер#05:Зап,Ж"
Data " черн#09 двер#05:Зап,Ж"
Data " лестниц#02 вниз:Кл_на,Ж"
Data " лестниц#02 наверх:Кл_на,Ж"
Data ""

Function einsertafter.element(afterwhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=afterwhat
what\nxt=afterwhat\nxt
If afterwhat\nxt<>Null Then afterwhat\nxt\prev=what
afterwhat\nxt=what
what\root=afterwhat\root
Return what
End Function

Function einsertbefore.element(beforewhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=beforewhat\prev
what\nxt=beforewhat
If beforewhat\prev<>Null Then beforewhat\prev\nxt=what
what\root=beforewhat\root
If what\prev=Null Then what\root\sub=what
beforewhat\prev=what
Return what
End Function

Function einsertin.element(inwhat.element,what.element)
If what=Null Then what=New element Else epush what
what\prev=Null
If inwhat\sub=Null Then
 what\nxt=Null
Else
 what\nxt=inwhat\sub
 inwhat\sub\prev=what
End If
inwhat\sub=what
what\root=inwhat
Return what
End Function

Function eremove(what.element,care=True)
If care Then epush what
e.element=what\sub
While e<>Null
 e2.element=e
 e=e\nxt
 eremove e2,False
Wend
Delete what
End Function

Function epush(what.element)
If what\prev<>Null Then what\prev\nxt=what\nxt Else what\root\sub=what\nxt
If what\nxt<>Null Then what\nxt\prev=what\prev
End Function

;Реальное название объекта с заменой ссылок на окончания (с учетом падежа)
Function realname$(e.element,pad)
m$=e\name$
Repeat
 i=Instr(m$,"#")
 If i=0 Then Exit
 m$=Left$(m$,i-1)+en$(Mid$(m$,i+1,2),pad)+Mid$(m$,i+3)
Forever
Repeat
 i=Instr(m$,"^")
 If i=0 Then Exit
 m$=Left$(m$,i-1)+Mid$(m$,i+1+(pad>1 And pad<>5))
Forever
Return m$
End Function

;Функция удаления пробелов с краев строки, корректно работающая с русским шрифтом
Function rustrim$(m$)
For n=1 To Len(m$)
 If Mid$(m$,n,1)<>" " Then Exit
Next
m$=Mid$(m$,n)
For n=Len(m$) To 1 Step -1
 If Mid$(m$,n,1)<>" " Then Exit
Next
Return Left$(m$,n)
End Function

;Корректное перемещение в локацию
Function moveplayer(lname$)
e.element=root\sub
;Поиск локации по названию
While e<>Null
 m$=realname$(e,1)
 If m$=lname$ Then Exit
 e=e\nxt
Wend
If e=Null Then Stop
;Формирование списка доступных объектов
Delete Each eref
;Добавление всегда присутствующих объектов
addbranch shared
;Добавление всегда объектов локации
addbranch e
loc=e
End Function

;Вспомогательная функция для занесения объектов в список доступных
Function addbranch(what.element)
e.element=what\sub
While e<>Null
 ;Скрытые объекты не заносятся
 If flag(e,"Скр")=0 Then
  r.eref=New eref
  r\e=e
 End If
 e=nextobj(what,e)
Wend
End Function

;Просмотр названия объекта в разных падежах
Function viewpad(e.element)
Cls
;Просмотр названия объекта во всех падежах (отладка)
Text 0,0,realname$(e,1)
Text 0,15,"нет "+realname$(e,2)
Text 0,30,"дать "+realname$(e,3)
Text 0,45,"винить "+realname$(e,4)
Text 0,60,"творить "+realname$(e,5)
Text 0,75,"говорить о "+realname$(e,6)
WaitKey
End Function

;Функция ввода фразы
Function inputphrase$()
;Очистка списка составляющих команды
Delete Each cmd
cur.cmd=New cmd
fc.cmd=cur
Color 255,255,255
Text 0,ty,"|"
Repeat
 i=WaitKey()
 Select i
  Case 8;Backspace
   If part$<>"" Then
    part$=Left$(part$,Len(part$)-1)
   Else
    If fc<> cur Then
     cur=Before cur
     Delete Last cmd
    End If
   End If
  Case 27
   End
  Case 44, 32
   ;Проверка на возможность ввода 1-го и 2-го объектов
   If fc\v\n[0]>0 Or i=44 Then
    If cur\typ<2-(fc\v\n[1]=0) Or i=44 Then
     ;Если найдено подходящее слово - создаем новый элемент команды
     If fnd Then
      t=cur\typ
      cur=New cmd
      fnd=0
     End If
     ;Если введена запятая, а не пробел, то следующий объект будет такого же типа
     cur\typ=t+(i=32)
     part$=""
    End If
   End If
  ;Проверка на возможность ввода фразы
  Case 13
   If cur\e=Null Then i=0
   If fc<>Null Then If fc\v<>Null Then If fc\v\n[0]=0 Then i=13
  ;Изменение номера подходящего объекта
  Case 5
   q=q+1
  Case 6
   q=q-1
  Default
   part$=part$+Chr$(i)  
   q=1
 End Select

 pl=Len(part$)
 If part$<>"" Then
  n=0
  If cur\typ=0 Then
   ;Поиск подходящего глагола (тип 0)
   fv.verb=Null
   For v.verb=Each verb 
    ;Проверка на совпадение введенной части с началом глагола
    If Mid$(v\name$,1,pl)=part$ Then
     ;Проверка на совпадение формы (для дополнительных глаголов)
     If fc=cur Then
      k=1
     ElseIf v\n[0]=fc\v\n[0] And v\n[1]=fc\v\n[1] And v\adverb$=fc\v\adverb$ Then
      k=1
     Else
      k=0
     End If
     If k Then
      n=n+1
      ;Определение первого или последнего найденного объекта (в зависимости от
      ; целей
      If n=1 Or q=0 Then fv=v
      ;При встрече q-го объекта - выход (объект найден)
      If q=n Then Exit
     End If
    End If
   Next
   ;Если объекта с таким номером (q) нет, значит либо это последний объект
   ; (перескок с 1-го, q=1-1=0) либо первый (с последнего)
   If fv<>Null And v=Null Then
    v=fv
    If q=0 Then q=n Else q=1
   End If
   If v<>Null Then fnd=1
   cur\v=v
  Else
   ;Аналогичный поиск 1-го или 2-го объекта по названию
   fr.eref=Null
   For r.eref=Each eref
    If Mid$(r\e\name$,1,pl)=part$ Then
     n=n+1
     If n=1 Or q=0 Then fr=r
     If q=n Then Exit
    End If
   Next
   If fr<>Null And r=Null Then
    r=fr
    If q=0 Then q=n Else q=1
   End If
   If r<>Null Then
    fnd=1
    cur\e=r\e
   Else
    cur\e=Null
   End If
  End If
 End If

 m$=""
 ;Отображение команды на экране
 ;Очистка полосы
 Color 0,0,0
 Rect 0,ty,800,tysiz*2
 For c.cmd=Each cmd
  ;Печать пробела (между командами разных типов) или запятой
  If c<>fc Then
   If c\typ=oldtyp Then
    m$=m$+", "
   Else
    m$=m$+" "    
    If c\typ=2 Then m$=m$+fc\v\adverb$
   End If
  End If
  oldtyp=c\typ
  ;Печать последнего (вводимого) слова
  If c=cur Then
   Color 255,255,255
   ;Если слово не удалось подобрать - печать красным, выход
   If c\e=Null And c\v=Null Then
    Text 0,ty,m$
    Color 255,0,0
    pl=StringWidth(m$)
    Text pl,ty,part$+"|"
    m$=""
    Exit
   End If
   ;Печать предыдущих слов и введенной части белым
   If i<>13 Then
    pl=StringWidth(m$+part$)
    Text 0,ty,m$+part$
    m$=""
   End If
  End If
  ;Добавление в фразу последнего слова
  If c\typ=0 Then
   m$=m$+c\v\name$
  Else
   m$=m$+realname$(c\e,fc\v\n[c\typ-1])
  End If
  If i=13 And c=cur Then
   tprint m$
   Return
  End If
 Next
 ;Добавление предлога в скобках (если вводится глагол с предлогом)
 If cur\typ=0 And fc\v<>Null Then 
  If fc\v\adverb$<>"" Then m$=m$+" ("+rustrim$(fc\v\adverb$)+")"
 End If

 ;Печать зеленым оставшейся части "предсказанного" слова
 Color 0,255,0
 If m$<>"" Then Text pl,ty,Mid$(m$,Len(part$)+1)+"|"
Forever
End Function

;Процедура обработки элементарных команд
Function processcmd()
Color 255,255,0
v.verb=try(0)\v
verb$=v\name$
adv$=v\adverb$
If try(1)<>Null Then
 n1.element=try(1)\e
 n1n$=realname$(n1,1)
End If
If try(2)<>Null Then
 n2.element=try(2)\e
 n2n$=realname$(n2,1)
End If
Select verb$
 Case "взять","брать"
  k=flag(n1,"Взять")
  n2=findobjwithflag(body,"Карман")
  ;Если одет объект с карманом и предмет достаточно мал - положить его в карман
  If n2<>Null And k Then
   moveobj n2,n1,"В"
   m$=m$+"Вы положили "+realname$(n1,4)+" в "+realname$(n2,4)+"."
  ;Если предмет можно взять в руки и в руках ничего нет, взять его в руки
  ElseIf k Or flag(n1,"Рук") Then
   If hands\sub=Null Then
    moveobj hands,n1
    m$="Теперь у вас в руках "+n1n$
   Else
    m2$="У вас заняты руки."
    n2=Null
   End If
  End If
 Case "положить","бросить","класть"
  ;Если предмет в инвентаре, то ...
  If findobj(body,n1n$)<>Null Or findobj(hands,n1n$)<>Null Then
   ;Задаются флаги местоположения (в случае, если предмет может быть помещен
   ; таким образом)
   If n2<>Null Then
    If adv$="на " Then If flag(n2,"Кл_на") Then m2$="На"
    If adv$="в " Then If flag(n2,"Кл_в") Then m2$="В"
    If adv$="под " Then If flag(n2,"Кл_под") Then m2$="Под"
    ;Устранение ситуаций типа "матрас на стуле, стул на матрасе"
    If m2$<>"" Then
     e.element=n2
     While e<>Null
      If e=n1 Then m2$="":Exit
      e=e\root
     Wend
     If m2$<>"" Then 
      moveobj n2,n1,m2$
      m$="Вы избавились от "+realname$(n1,2)
      If m2$="В" Then water
     End If
    End If
   Else
    ;Предмет помещается в текущую локацию
    moveobj loc,n1
    m$="Вы избавились от "+realname$(n1,2)
   End If
   ;Объект, брошеный в открытое / разбитое окно уничтожается
   If adv$="в " And n2n$="окно" Then
    If flag(n2,"Разб") Or flag(n2,"Откр") Then
     tprint "Вы выбросили "+realname$(n1,4)+" в окно."
     eremove n1
     Return
    Else
     moveobj loc,n1
     m$=cap$(n1n$)+" отскочил"+vend$(n1)+" от стекла и упал"+vend$(n1)+" на пол."
    End If
   End If
  Else
   m2$="У вас нет "+realname$(n1,2)
  End If
 Case "повесить"
  If flag(n1,"М_вис") And flag(n2,"Пов_на") Then
   remflag n1,"Вис"
   moveobj n1,n2,"Вис"
   m$="Вы повесили "+realname$(n1,4)+" на "+realname$(n2,4)+"."
  End If
 Case "идти по"
  If Left$(n1n$,8)="лестница" Then
   tprint "Вы пошли по лестнице. Вот идете Вы, идете, а ступеньки вдруг возьми, "
   tprint " да обвались. И полетели Вы вниз, в пустоту недоделанного уровня... The End."
   WaitKey:End
  End If
 Case "идти в","прыгнуть в","войти в"
  k=Instr(n1\flag$,">")
  ;Обработка прыжка в окно
  If n1n$="окно" Then
   k2=flag(n1,"Разб") Or flag(n1,"Откр")
   If k2=0 And verb$="прыгнуть в" Then
    m$="Своим телом Вы разбили стекло и ужасно порезались."
   End If
   If k2 Or m$<>"" Then
    tprint "Вы выпрыгнули в окно."
    If m$<>"" Then tprint m$
    tprint "С дикими воплями, размахивая руками Вы летели вниз, пока не шмякнулись"
    tprint " о поверхность, испещренную надписями 'Under Construction'. The End."
    WaitKey: End
   End If
  End If
  ;Переход в другую локацию
  If k Then
   If flag(n1,"Закр")
    m2$="Закрыто."
   Else
    tprint "ОК."
    ;Вычленяется имя локации и осуществляется переход в нее
    k2=Instr(n1\flag$,",",k)
    moveplayer Mid$(n1\flag$,k+1,k2-k-1)
    ;Перейдя, осматриваемся
    look
    ;Остальные команды игнорируются (т. к. могут оказаться некорректными)
    Delete Each cmd
    Return
   End If
  End If
 Case "открыть"
  ;Если объект закрыт ...
  If flag(n1,"Закр") Then
   ;И не заперт...
   If flag(n1,"Зап") Then
    m2$="Заперто."
   Else
    ;То поменять флаг
    m$="Вы открыли "+realname$(n1,4)+"."
    remflag n1,"Закр"
    addflag n1,"Откр"

    ;Изменение наполненности водой
    If Instr(n1n$,"ванны") Then n1=findobj(root,"ванна") Else n1=findobj(root,"раковина")
    If flag(n1,"Вода")=0 Then addflag(n1,"Вода")
    water
   End If
   ;Иначе, если он открыт (то есть, вообще открывается) ...
  ElseIf flag(n1,"Откр") Then
   m$=cap$(n1n$)+" уже открыт"+vend$(n1)+"."
  End If
 Case "закрыть"
  ;Аналогично
  If flag(n1,"Закр") Then
   m$=cap$(n1n$)+" уже закрыт"+vend$(n1)+"."
  ElseIf flag(n1,"Откр") Then
   m$="Вы закрыли "+n1n$
   addflag n1,"Закр"
   remflag n1,"Откр"
   ;Изменение наполненности водой
   If Instr(n1n$,"ванны") Then
    If flag(findobj(loc,"красный вентиль ванны"),"Закр") Then
     If flag(findobj(loc,"синий вентиль ванны"),"Закр") Then
      remflag findobj(loc,"ванна"),"Вода"
     End If
    End If
   Else
    If flag(findobj(loc,"красный вентиль раковины"),"Закр") Then
     If flag(findobj(loc,"синий вентиль раковины"),"Закр") Then 
      remflag findobj(loc,"раковина"),"Вода"
     End If
    End If
   End If
  End If
 Case "сломать","разбить"
  If flag(n1,"Разб") Then
   m2$=cap$(n1n$)+"Уже разбит"+vend$(n2)+"."
  ElseIf n1n$="стекло" Or n1n$="окно" Then
   ;Предмет можно разбить только кулаком или стулом
   If n2n$="стул" Then
    m$="Вы разбили окно стулом."
   ElseIf n2=Null Then
    m$="Вы разбили окно кулаком, но при этом сильно порезались."
   Else
    m2$=cap$(n2n$)+" отскочил"+vend$(n2)+" от стекла."
   End If
   ;Добавляем новый объект - осколки стекла
   If m$<>"" Then
    n2=einsertin(loc,Null)
    n2\name$="осколк#13 стекла"
    n2\flag$="Взять,М,Остр"
    r.eref=New eref
    r\e=n2
    ;Вне зависимости от того, что били, разбиваем и окно и стекло
    addflag findobj(loc,"стекло"),"Разб"
    addflag findobj(loc,"окно"),"Разб"
   End If  
  End If
 Case "одеть","надеть"
  ;Является ли объект одеждой?
  If flag(n1,"Одеть") Then 
   ;Находится ли он в руках
   If hands\sub=n1 Then
    einsertin body,n1
    m$="На вас теперь "+n1n$+"."
   Else
    m2$=cap$(realname$(n1,4))+" нужно сначала взять в руки."
   End If
  End If
 Case "снять"
  ;Аналогично
  If hands=Null Then
   m2$="У вас заняты руки."
  ElseIf existin(body,n1n$)
   einsertin hands,n1
   m$="Вы сняли "+n1n$+"."
  Else
   m$="У вас нет "+realname$(n1,2)+"."
  End If
 Case "отпереть"
  If flag(n1,"Зап") And n2n$="ключ" Then
   If n2n$="ключ" Then
    m$="Вы отперли "+realname$(n1,4)+"."
    remflag n1,"Зап"
    addflag n1,"Отп"
   End If
  ElseIf flag(n1,"Отп") Then
   m$=cap$(n1n$)+" уже отперт"+vend$(n1)+"."
  End If
 Case "запереть"
  If flag(n1,"Отп") Then
   If n2n$="ключ" Then
    m$="Вы заперли "+realname$(n1,4)+"."
    remflag n1,"Отп"
    addflag n1,"Зап"
   End If
  ElseIf flag(n1,"Зап") Then
   m$=cap$(n1n$)+" уже заперт"+vend$(n1)+"."
  End If
 Case "осмотреть","обследовать"
  e.element=n1\sub
  ;Цикл по всем объектам в группе обследуемого
  While e<>Null
   m2$=realname$(e,1)
   ;Вывод информации в зависимости от положения
   If flag(e,"На") Then
    m$=m$+"наверху - "+m2$+", "
   ElseIf flag(e,"Под") Then
    m$=m$+"под ним - "+m2$+", "
   ElseIf flag(e,"В") Then
    If flag(n1,"Закр") Then 
     m2$=""
    Else
     m$=m$+"внутри - "+m2$+", "
    End If
   ElseIf flag(e,"Вис") Then
     m$=m$+"висит "+m2$+", "
   Else
    m$=m$+"есть "+m2$+", "
   End If
   ;Скрытые объекты становятся видимыми
   If m2$<>"" Then
    If flag(e,"Скр") Then
     r.eref=New eref
     r\e=e
    End If
    remflag (e,"Скр")
   End If
   e=e\nxt
  Wend

  m2$=""
  ;Информация о состоянии объекта
  If flag(n1,"Откр") Then m2$=m2$+", открыт"+vend$(n1)
  If flag(n1,"Закр") Then m2$=m2$+", закрыт"+vend$(n1)
  If flag(n1,"Зап") Then m2$=m2$+", заперт"+vend$(n1)
  If flag(n1,"Разб") Then m2$=m2$+", разбит"+vend$(n1)
  If flag(n1,"Мокр") Then m2$=m2$+", пропитан"+vend$(n1)+" водой"
  If flag(n1,"Вода") Then m2$=m2$+", наполнен"+vend$(n1)+" водой"
  If m$<>"" Or m2$<>"" Then
   If m$<>"" Then m$=": "+Left$(m$,Len(m$)-2) Else
   If m2$<>"" Then m$=Mid$(m2$,2)+m$
   m$=cap$(n1n$)+m$+"."
  End If

  m2$=""
  ;Если вентиль душа открыт - вода будет течь из крана ванной, иначе - из крана душа
  k=flag(findobj(root,"вентиль душа"),"Закр")
  If (n1n$="кран ванны" And k=0) Or (n1n$="душ" And k=1) Then m2$="ванны"
  If n1n$="кран раковины" Then m2$="раковины"
  ;Температура воды зависит от того, какие краны открыты (если оба закрыты, то вода
  ; не потечет)
  If m2$<>"" Then
   k=flag(findobj(loc,"синий вентиль "+m2$),"Откр")
   If flag(findobj(loc,"красный вентиль "+m2$),"Откр") Then
    If k Then
     m$="теплая вода"
    Else
     m$="горячая вода"
    End If
   ElseIf k Then
    m$="холодная вода"
   Else 
    m$=""
   End If   
   If m$<>"" Then m$="Из "+realname$(n1,2)+" течет "+m$+"."
  End If
  If m$="" Then m$="Осмотрев "+realname$(n1,4)+", Вы не обнаружили ничего особенного."
 Case "осмотреться"
  look
  Return
 Case "инвентарь"
  m$=""
  ;Осмотр своего тела на предмет одежды
  e=body\sub
  While e<>Null
   m$=m$+realname$(e,4)+", "
   e=e\nxt
  Wend
  If m$<>"" Then tprint "Вы одеты в "+Left$(m$,Len(m$)-2)+"."

  ;Осмотр рук
  If hands\sub=Null Then
   m$="ничего нет, "
  Else
   m$=realname$(hands\sub,1)+", "
  End If
  tprint "У Вас в руках "+Left$(m$,Len(m$)-2)+"."
  m$=""

  ;Осмотр карманов
  e=body\sub
  While e<>Null
   If flag(e,"Одеть")=0 Then m$=m$+realname$(e,1)+", "
   e=nextobj(body,e)
  Wend
  If m$<>"" Then tprint "В карманах у Вас "+Left$(m$,Len(m$)-2)+"."
  Return
 Case "резать","разрезать"
  If flag(n1,"Рез") Then
   If Left$(n2n$,5)="оскол" Then
    If hands\sub=n2 Or hands\sub=Null Then
     m$="Вы разрезали "+realname$(n1,4)+"."
     ;Изменение названия предмета, чтобы получить куски
     n1\name$="куск#16 "+realname$(n1,2)
     ;Удаление возможности одеть и разрезать
     remflag n1,"Рез"
     remflag n1,"Одеть"
     ;Бросить на пол
     moveobj loc,n1
    Else
     m$="У Вас заняты руки."
    End If
   Else
    m$="Чем?"
   End If
  End If
End Select

;Обработка неудавшегося действия
If m$="" Then
 m$=cap$(verb$)+" "+realname$(n1,v\n[0])
 If n2<>Null Then m$=m$+" "+v\adverb$+realname$(n2,v\n[1])
 m$=m$+" не получается. "+m2$
End If
tprint m$
End Function

;Функция, проверяющая наличие данного флага у объекта
Function flag(e.element,flag$)
If Instr(e\flag$,flag$+",") Then Return 1
End Function

;Добавление флага в строку флагов объекта
Function addflag(e.element,flag$)
e\flag$=e\flag$+flag$+","
End Function

;Удаление флага из строки флагов объекта
Function remflag(e.element,flag$)
m$=e\flag$
i=Instr(m$,flag$+",")
If i=0 Then Return
e\flag$=Left$(m$,i-1)+Mid$(m$,i+Len(flag$)+1)
End Function

;Проверка наличия объекта с данным именем в группе данного
Function existin(inwhat.element,what$)
If inwhat=Null Then Return
inwhat=inwhat\sub
While inwhat<>Null
 If realname$(inwhat,1)=what$ Then Return 1
 inwhat=After inwhat
Wend
End Function

;Функция, делающая начальную букву строки заглавной
Function cap$(m$)
m$=Chr$(Asc(Left$(m$,1))-32)+Mid$(m$,2)
Return m$
End Function

;Функция, подбирающее окончание, в зависимости от рода
Function vend$(e.element)
If flag(e,"Ж") Then Return "а"
If flag(e,"С") Then Return "о"
If flag(e,"М") Then Return "ы"
End Function

;Выполнение команды "осмотреться"
Function look()
e.element=loc\sub
;Перечисление объектов, входящих в ветвь локации (кроме скрытых)
While e<>Null
 If flag(e,"Скр")=0 Then m$=m$+realname$(e,1)+", "
 e=nextobj(loc,e)
Wend
If m$="" Then m$="ничего нет  "
tprint "Вы находитесь в "+realname$(loc,3)+"."
tprint "Перед вами "+Left$(m$,Len(m$)-2)+"."
End Function

;Корректная печать текста (с переносом и сдвигом экрана)
Function tprint(m$)
Repeat
 l=StringWidth(m$)
 If l<=xsiz Then Exit
 n$=Mid$(m$,Len(m$),1)+n$
 m$=Left$(m$,Len(m$)-1)
Forever
If ty+tysiz*2>ysiz Then
 cr=ColorRed()
 cg=ColorGreen()
 cb=ColorBlue()
 CopyRect 0,ysiz-ty,xsiz,ty,0,0
 Color 0,0,0
 Rect 0,ty,xsiz,tysiz*2
 ty=ty*2-ysiz
 Color cr,cg,cb
End If
Text 0,ty,m$
ty=ty+tysiz

If n$<>"" Then tprint n$
End Function

;Рекусривная функция поиска объекта в ветви данного по названию
Function findobj.element(inwhat.element,what$)
inwhat=inwhat\sub
While inwhat<>Null
 If realname$(inwhat,1)=what$ Then Return inwhat
 ;Проверка наличия элемента в ветви
 e.element=findobj(inwhat,what$)
 If e<>Null Then Return e
 inwhat=inwhat\nxt
Wend
End Function

;Функция нахождения следующего объекта в ветви inwhat (полезна для осуществления
; перебора элементов ветви)
Function nextobj.element(inwhat.element,current.element)
If current\sub=Null Then
 While current\nxt=Null
  If current\root=inwhat Then Return Null
  current=current\root
 Wend
 Return current\nxt
Else
 Return current\sub
End If
End Function

;Функция поиска объекта с данным флагом в даной ветви
Function findobjwithflag.element(inwhat.element,what$)
e.element=inwhat\sub
While e<>Null
 If flag(e,what$) Then Return e
 e=nextobj(inwhat,e)
Wend
End Function

;Корректное перемещение объекта
Function moveobj(inwhat.element,what.element,flag$="")
;Убираются флаги местоположения
remflag what,"На"
remflag what,"В"
remflag what,"Под"
remflag what,"Вис"
If flag$<>"" Then addflag what,flag$
einsertin inwhat,what
End Function

;Пропитывание объектов водой
Function water()
src.element=loc\sub

While src<>Null
 k=flag(src,"Вода")
 dst.element=src\sub
 While dst<>Null
  If k And flag(dst,"Сух") Then
   remflag dst,"Сух"
   addflag dst,"Мокр"
  End If
  dst=dst\nxt
 Wend 
 src=src\nxt
Wend 
End Function

</code><noinclude>[[Категория:Код]]</noinclude>