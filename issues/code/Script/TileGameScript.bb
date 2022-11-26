Graphics 640,480,32

;Загрузка изображений тайлов (последняя - спрайт)
Global tiles=LoadAnimImage("Script-Tiles.png",20,20,0,12)
;Цвет маски - пурпурный
MaskImage tiles,255,0,255

;Буфер для тайловой карты
Global buf=CreateImage(640,480)
Global bufb=ImageBuffer(buf)

;Координаты игрока, номер экрана
Global px=16,py=12,sc=0

;Занесение в память карт тайлов экранов
Dim scr$(1,19)
For n1=0 To 1
 For n2=0 To 19
  Read scr$(n1,n2)
 Next
Next

SetBuffer BackBuffer()
SetFont LoadFont("Arial Cyr",20,True)
;Установка экрана номер 0
setscr 0

Repeat
 ;Приращения, зависящие от нажатых клавиш
 dx=KeyDown(205)-KeyDown(203)
 If dx=0 Then dy=KeyDown(208)-KeyDown(200) Else dy=0
 ;Перемещение
 If dx<>0 Or dy<>0 Then move dx,dy
 ;Пуск скрипта при нажатии пробела перед стражником
 If py>0 And KeyHit(57) Then If land$(px,py-1)="J" Then runscript

Until KeyHit(1)

WaitKey

Function setscr(n1)
;Рисование тайлов в буфер тайловой карты
SetBuffer bufb
For n2=0 To 19
 For n3=0 To 31
  DrawBlock tiles,n3*20,n2*20,Asc(Mid$(scr$(n1,n2),n3+1,1))-65
 Next
Next
SetBuffer BackBuffer()
sc=n1
drawbuf
End Function

Function drawbuf()
;Реинициализация
DrawBlock buf,0,0
DrawImage tiles,px*20,py*20,11
Flip
End Function

Function move(dx,dy)
;Пресечение возможности выхода за границу экрана
If dx+px>=0 And dx+px=<31 And dy+py>=0 And dy+py<=19 Then
 Select land$(px+dx,py+dy)
  ;Можно ходить только по траве, полу и дороге
  Case "E","H","I"
   px=px+dx
   py=py+dy
   ;Перемещение между экранами
   If py=0 And sc=0 Then
    py=18
    setscr 1
   ElseIf py=19 And sc=1 Then
    py=1
    setscr 0
   End If
   drawbuf
   Delay 100
 End Select
End If
End Function

;Функция возвращающая номер тайла по координатам
Function land$(x,y)
Return Mid$(scr$(sc,y),x+1,1)
End Function

;
Function runscript()
Restore script
Repeat
 Read m$
 If m$="" Then Exit
 ;Адреса разделителей в строке
 i1=Instr(m$," ")
 i2=Instr(m$,"/",i1+1)
 i3=Instr(m$+"/","/",i2+1)
 ;Выделение имени команды
 cmd$=Mid$(m$,1,i1-1)
 ;Разбиение строки на параметры
 par1=Mid$(m$,i1+1,i2-i1-1)
 par2$=Mid$(m$,i2+1,i3-i2-1)
 par3$=Mid$(m$,i3+1)
 x=par1:y=par2$
 ;Stop
 Select cmd$
  Case "SPEAK"
   ;Команда "говорить" выдает текст в окне
   Textwindow par1,par2$
  Case "MOVE"
   ;Команда "переместиться" перемещает игрока в заданную точку
   drawbuf
   ;Сначала по горизонтали
   x=x-px
   For dx=1 To Abs(x)
    move Sgn(x),0
   Next
   ;Затем по вертикали.
   y=y-py
   For dy=1 To Abs(y)
    move 0,Sgn(y)
   Next
  Case "CHANGE"
   ;Команда "изменить" изменяет тайл с заданными координатами
   ;Рисование тайла в буфере тайловой карты
   SetBuffer bufb
   DrawBlock tiles,x*20,y*20,Asc(par3$)-65
   SetBuffer BackBuffer()
   ;Изменение карты тайлов - замена символа в строке
   scr$(sc,y)=Mid$(scr$(sc,y),1,x)+par3$+Mid$(scr$(sc,y),x+2)
  Default
   ;Сообщение об ошибке (при неизвестной команде)
   DebugLog "Unknow command '"+cmd$+"'"
   Stop
 End Select
Forever
drawbuf
End Function

Function Textwindow(v,txt$)
;Окно текста
FlushKeys
drawbuf
;Рисование окна
Color 255,255,255
Rect 0,400,640,80
Color 128,128,128
Rect 3,403,637,77
Color 192,192,192
Rect 2,402,636,76
;Рисование персонажа, в данный момент говорящего
DrawImage tiles,10,410,9+v
Color 0,0,0
y=410

For n=1 To Len(txt$)
 k$=Mid$(txt$,n,1)
 ;Пробел - начало нового слова
 If k$=" " Then
  ;Если длина куска фразы с новым словом больше допустимой - переход на новую строку
  If StringWidth(m$+w$)>490 Then 
   Text 40,y,m$
   y=y+20
   m$=w$
  Else
   ;Иначе - прибавить слово к фразе
   m$=m$+" "+w$
  End If
  w$=""
 Else
  w$=w$+k$
 End If
Next
;Печать остатка
Text 40,y,m$+" "+w$
Flip
WaitKey
End Function

;Карты
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"
Data "FFFFFFFFFFFFFFBDDBFFFFFFFFFFFFFF"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data "HHHHHHHHHHHHHHJJJJHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHJHHJHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"

Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BCBBBCBBBCBBBCBEEBCBBBCBBBCBBBCB"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data "BBABBDBBABBDBBAEEABBDBBABBDBBAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "CBABCBCBABCBCBAEEABCBCBABCBBCABC"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEKEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data "AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data "BBCBBDBBCBBDBCBEEBCBDBBCBBDBBCBB"
Data "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data "AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data "BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"

;Данные скрипта
.script
Data "SPEAK 0/Стой, кто идет!"
Data "SPEAK 2/Мое имя Леонард, я пришел из города Сэндворм сражаться с гигантскими пауками!"
Data "SPEAK 0/Леонард? Я слышал о тебе. Проходи к капитану дворцовой стражи, налево и по коридору 2-я дверь."
Data "CHANGE 15/7/H"
Data "CHANGE 16/7/H"
Data "CHANGE 13/7/J"
Data "CHANGE 18/7/J"
Data "CHANGE 15/2/E"
Data "CHANGE 16/2/E"
Data "MOVE 16/1"
Data "CHANGE 15/2/D"
Data "CHANGE 16/2/D"
Data "MOVE 16/0"
Data "MOVE 16/17"
Data "MOVE 5/17"
Data "CHANGE 5/16/E"
Data "MOVE 5/13"
Data "SPEAK 2/Здравствуйте."
Data "SPEAK 1/Здравствуй. Тебя зовут Леонард, не так ли?"
Data "SPEAK 2/Да, я Леонард."
Data "SPEAK 1/Мое имя Жан. Я слушаю тебя."
Data "SPEAK 2/Я слышал, вы набираете людей для похода в горы, уничтожить гигантских пауков."
Data "SPEAK 1/Да, эти пауки уже представляют угрозу."
Data "SPEAK 2/Я хочу присоединиться к вам."
Data "SPEAK 1/Хорошо, Леонард. Я наслышан о твоих подвигах, думаю, обойдемся без экзаменов. А пока, иди подзаправься. Завтра утром выходим."
Data "MOVE 5/17"
Data "CHANGE 5/16/D"
;Пустая строка - конец скрипта
Data ""