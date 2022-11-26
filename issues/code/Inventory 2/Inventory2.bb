Global DataDir$=SystemProperty$("appdir")+"Data\"
DataDir$=".\Data\"

Global AplicationWidth,AplicationHeight,KolCell,SizeCellW,SizeCellH,KolCol,KolRow,InventarPozX,InventarPozY,MouseID

AplicationWidth=800 ;Ширина Экрана
AplicationHeight=600 ;Высота экрана

Graphics3D AplicationWidth,AplicationHeight, 32,2
SetBuffer BackBuffer()

MouseID=0  ;Обнуляем переменные
MouseStatus=0 ;Переменная для хранения статуса мыши, служит для определения была ли нажата кнопка мыши...
MouseImage=LoadImage(DataDir$+"Inventory2-Mouse.png") ;Загрузка курсора мыши...
MaskImage MouseImage,0,0,0 ;маска для мыши...

Type TCell ; Тип клетки для инвенторя
 Field X ;Координа "Х" клетки
 Field Y ;Координа "У" клетки
 Field Status; Здесь хранится ID объекта, если его нет - значение = 0
End Type

KolCol=3               ;Количество столбцов
KolRow=3               ;Количество строк
KolCell=KolCol*KolRow  ;Количество ячеек

SizeCellW=80           ;Ширина ячейки
SizeCellH=80           ;Висота ячейки

InventarPozX=AplicationWidth-(KolCol*SizeCellW) ;Указываем откуда начинать рисовать инвентарь, координата "X"
InventarPozY=0                                  ;Указываем откуда начинать рисовать инвентарь, координата "Y"

Dim Inventar.TCell(KolCell-1)

Type TObject    ; Тип для описания предметов инвентаря
            Field ID    ;  Идентификатор предмета
            Field Image ;  Переменная для картинки предмета
  Field Name$ ;  Название предмета
  Field Description$ ; Описание объекта
  Field Damage       ; Повреждения наносимые объектом (Только у оружия)
  Field Guard        ; Защита (только у щитов и у брони)
End Type

;------------------------ Мечи----1-99-----------------------
Sword.TObject=New TObject
Sword\Damage=10
Sword\ID=1
Sword\Image=LoadImage(DataDir$+"Inventory2-Item1.png")
MaskImage Sword\Image,255,0,255
Sword\Name$="Меч"
Sword\Description$="Меч был выкован неизвестным кузнецом. В общем, ничего особого он не стоит..."

Sword.TObject=New TObject
Sword\Damage=15
Sword\ID=2
Sword\Image=LoadImage(DataDir$+"Inventory2-Item2.png")
MaskImage Sword\Image,255,0,255
Sword\Name$="Меч-2"
Sword\Description$="Меч-2 был выкован Великим Мастером."
;------------------------ Щиты--100-199-------------------------
Shield.TObject=New TObject
Shield\Guard=3
Shield\ID=100
Shield\Image=LoadImage(DataDir$+"Inventory2-Item3.png")
MaskImage Shield\Image,255,0,255
Shield\Name$="Щит титана"
Shield\Description$="Щит принадлежал одному из титанов, не каждому воину под силу унести его..."

Shield.TObject=New TObject
Shield\Guard=5
Shield\ID=101
Shield\Image=LoadImage(DataDir$+"Inventory2-Item4.png")
MaskImage Shield\Image,255,0,255
Shield\Name$="Щит Дракона"
Shield\Description$="Щит, созданный из драконьей чешуи, один из лучших способов защиты от огня..."

FntArial=LoadFont("Arial",14)
SetFont FntArial

InitInventar()

While Not KeyHit(1) ;Выход из программы если нажали Esc
SetBuffer BackBuffer()
Cls

    DrawInventar() ;Функция рисования инвентаря
    If KeyHit(2) Then AddElementToInventar(1,1) ;Если нажать на кнопку 1 то в инвентарь добавится меч
    If KeyHit(3) Then AddElementToInventar(2,2) ;Если нажать на кнопку 2 то в инвентарь добавится 2-ой меч
    If KeyHit(4) Then AddElementToInventar(1,100) ;Если нажать на кнопку 3 то в инвентарь добавится Щит титана
    If KeyHit(5) Then AddElementToInventar(2,101) ;Если нажать на кнопку 4 то в инвентарь добавится Щит Дракона

 If MouseDown(2) Then GetInfoInventar() ; По нажатию правой кнопки мыши на объекте, в инвентаре выводится информация об этом предмете

 If MouseDown(1)  Then MoveElementToInventar():MouseStatus=1 ; По нажатию левой - перемещение предмета
 If MouseStatus=2 Then AddElementToInventar(MouseDownInventar(),MouseID):MouseStatus=0 ;Если отпустили мышь с предметом, то он добавляется в инвентарь
 If MouseStatus<>0 Then MouseStatus=2 ; необходимо для того, чтобы отпущенный предмет добавился только один раз

 DrawImage MouseImage,MouseX(),MouseY() ;Рисуем курсор мыши

 Flip;
Wend

End

Function InitInventar();Создаем инвентарь
K=0;
 For I=0 To KolRow-1
 For J=0 To KolCol-1
   Inventar.TCell(K)= New TCell
   Inventar.TCell(K)\X=InventarPozX+((SizeCellW-1)*J)
   Inventar.TCell(K)\Y=InventarPozY+((SizeCellH-1)*I)
   Inventar.TCell(K)\Status=0
   K=K+1
 Next
 Next
End Function

Function DrawInventar();Функция рисования инвентаря
 For I=0 To KolCell-1
   Rect(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,SizeCellW,SizeCellH,0); Рисуем квадрат для инвентаря (здесь можно квадрат заменить на картинку)
   DrawElementInventar(Inventar.TCell(I)\Status,I);Функция рисования предметов инвентаря
 Next
End Function

Function DrawElementInventar(ID,Element);Функция рисования предметов инвентаря
  If ID<>0 Then ;Если ячейка не пустая, то
    Temp.TObject=FindObjectInInventar(ID) ;Функция возвращает данные об объекте, который находится в ячейке
    DrawImage Temp\image,Inventar.TCell(Element)\X,Inventar.TCell(Element)\Y ;Рисуем предмет из инвентаря
  EndIf
End Function

Function FindObjectInInventar.TObject(ID) ;Функция возвращает данные об объекте, который находится в ячейке.
If (ID>=1) And (ID<=99) Then ;Если ID (идентификатор объекта) попал в интервал от 1 до 99 то это меч
  Sword.TObject = First TObject ; Устанавливаем список мечей на первый меч
   For Sword.TObject= Each TObject ;Перебираем все мечи в поисках нужного
       Temp=Sword\ID ;Присваиваем переменной Temp ID перебираемого объекта
       If Temp =ID Then Return Sword: Exit ; Смотрим: если ID (Temp) = ID искомого предмета, то возвращаем о нем данные и выходим из цикла
    Next
  EndIf
  If (ID>=100) And (ID<=199) Then ;Если ID (идентификатор объекта) попал в интервал от 100 до 199, то это щит
  Shield.TObject = First TObject ;Устанавливаем список щитов на первый щит
   For Shield.TObject= Each TObject;Перебираем все щиты в поисках нужного
       Temp=Shield\ID  ;Присваеваем переменной Temp ID перебираемого объекта
       If Temp =ID Then Return Shield: Exit ;Смотрим, если ID (Temp) = ID искомого предмета, то возвращаем о нем данные и выходим из цикла
    Next
  EndIf
  ; Можно добавить и еще подобные проверки, если у вас больше предметов...
End Function

Function AddElementToInventar(Poz,ID); Функция добавляет предмет в инвентарь, Poz-в какую ячейку, ID-какой предмет
    TempEmptyCell=FindEmptyCellInInventar(); Поиск свободной клетки
  If Poz=-1   Then Poz=TempEmptyCell; Если предмет вы бросаете вне клеток инвентаря, то предмет попадет в первую свободную клетку
    Temp=Inventar.TCell(Poz)\Status ;Здесь запоминается идентификатор (ID) предмета в клетке
    If TempEmptyCell>=0 Then ; смотрим, есть ли пустые клетки, если есть, то идем дальше
      If Temp<>0 Then ; Если клетка не пустая, то
        Inventar.TCell(TempEmptyCell)\Status=ID ; присваиваем предмет найденной пустой клетке
        MouseID=0 ; Указываем, что предмет опущен в клетку и мышь пустая
      EndIf
      If Temp=0 Then  ; Если клетка пустая,
        Inventar.TCell(Poz)\Status=ID ;то присваиваем предмет текущей клетке
        MouseID=0 ; Указываем, что предмет опущен в клетку и мышь пустая
      EndIf
    Else Text 10,10,"Инвентарь переполнен"
    EndIf
End Function

Function FindEmptyCellInInventar();Ищет первую свободную ячейку в инвентаре
  For I=0 To KolCell-1
    If Inventar.TCell(I)\Status=0 Then  Return I:Exit ;Перебираем все клетки в поисках пустой, если нашли, то возвращаем её номер и выходим...
  Next
   Return -1 ;Если нет пустых клеток то, возвращаем -1
End Function

Function GetInfoInventar(); По нажатию правой кнопки мыши на объекте, в инвентаре выводится информация об этом предмете
  For I=0 To KolCell-1
    ID=Inventar.TCell(I)\Status ; Перебираем все клетки и смотрим ID объектов клетки
    If (ID<>0) And (MouseOverlap(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,Inventar.TCell(I)\X+SizeCellW,Inventar.TCell(I)\Y+SizeCellH)=True) Then  ; Смотрим: если клетка не пустая и мышь находится над этой клеткой, то
     TempDan.TObject=FindObjectInInventar(ID)         ; Ищем данные об объекте
     Text 0,10, TempDan\Name$    ; Выводим название объекта
     Text 0,30, TempDan\Description$ ;Описание объекта
     If TempDan\ID<=99 Then Text 0,50,"Урон:"+TempDan\Damage ;Если это оружие, значит выводим значение урона
     If TempDan\ID>=100 And TempDan\ID<=199 Then Text 0,50,"Защита:"+TempDan\Guard   ;Если это щит, значит выводим значение защиты
    EndIf
  Next
End Function

Function MouseOverlap(X,Y,X1,Y1) ; Функция возвращает TRUE, если мышка попадает в квадрат и False, если не попадает
  If (MouseX()>X) And (MouseX()<X1) And  (MouseY()>Y) And (MouseY()<Y1) Then Return True Else  Return False
End Function

Function MoveElementToInventar();перемещение предмета
  Poz=MouseDownInventar(); Смотрим, где взяли предмет (в какой ячейке)
  If (Poz>=0) Or (MouseID<>0)Then ;Проверяем, чтобы предмет брался из инвентаря или мышь была не пустая
    If MouseID=0 Then   ID=Inventar.TCell(Poz)\Status ; Если мышь пустая, то берем ID предмета в текущей клетке
    If  ID<>0 Then MouseID=ID: DeleteElementToInventar(Poz) ; Если предмет есть в клетке, то мышь берет его и клетка очищается
    If (MouseID<>0) Then     ; Если мышь не пустая, то
      Temp.TObject=FindObjectInInventar(MouseID) ; ищем данные объекта по ID, который, у нас в мыши
      DrawImage Temp\image,MouseX()-SizeCellW/2,MouseY()-SizeCellH/2 ; Рисуем картинку объекта вместе с мышью...
    EndIf
  EndIf
End Function

Function DeleteElementToInventar(Poz); Удаление  предмета из инвентаря
   Temp=Inventar.TCell(Poz)\Status ; Сохраняем в переменную значение клетки
   If Temp<>0 Then Inventar.TCell(Poz)\Status=0; Смотрим, если клетка не пустая, то делаем ее пустой
End Function

Function MouseDownInventar(); смотрим в какой ячейке находится мышь
  For I=0 To KolCell-1
    If (MouseOverlap(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,Inventar.TCell(I)\X+SizeCellW,Inventar.TCell(I)\Y+SizeCellH)=True) Then; Смотрим где находится мышь и если она попадает в клетку инвентаря, возвращаем его номер и выходим
     Return I:Exit
    EndIf
  Next
  Return -1 ; Если мышь не попадает ни в одну клетку
End Function