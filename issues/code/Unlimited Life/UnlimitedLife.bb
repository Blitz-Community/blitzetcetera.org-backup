;Указатель и одновременно ячейка
Type ptr
 Field nxt.ptr[3] ;следующие указатели в иерархии
 Field prev.ptr ;предыдущий указатель в иерархии
 Field prevpos ;расположение предыдущего указателя
 Field neig.ptr[7] ;адреса соседей (для клетки)
 Field x,y,nq; координаты и количество соседей
End Type

;Указатели на ячейки, для которых возможно изменение состояния
Type chang
 Field p.ptr
End Type

Const loadorg$="Life-Locomotive.png"
;Const loadorg$="Life-Virus.png"
Const xres=800,yres=600

Global cellq, scrx, scry, ib

;Разбивка массива указателей на 3 списка
Dim pmark.ptr(3)
For n=0 To 3
 pmark(n)=New ptr
Next

Graphics xres,yres
SetFont LoadFont ("Arial cyr",14)

Dim change(24)
For n=0 To 1
 Read m$
 For nn=0 To 8
  change(n*16+nn)=Sgn(Instr(m$,nn))
 Next
Next
Data "3","0145678"

i=CreateImage(xres,yres)
ib=ImageBuffer(i)

i2=LoadImage(loadorg$)
ib2=ImageBuffer(i2)
xsiz=ImageWidth(i2)
ysiz=ImageHeight(i2)
For x=0 To xsiz-1
 For y=0 To ysiz-1
  If ReadPixel(x,y,ib2) And 255 Then
   ;Первая встреченная ячейка - отправная точка для создания остальных
   If cellq=0 Then
    cell.ptr=New ptr
    ptrq=ptrq+1
    cell\x=400
    cell\y=300
    xx=x
    yy=y
   End If
   newborn findcell(cell,x-xx,y-yy)
  End If
 Next
Next
FreeImage i2

Repeat
 
 DrawBlock i,0,0

 gen=gen+1
 ;Курсор мыши перемещается в центр экрана (чтобы не цепляться за края)
 MoveMouse xres Sar 1,yres Sar 1

 If cellq=0 Then Exit

 ;Все ячейки, подверженные изменениям, переещаются в список №2
 For ch.chang=Each chang
  If change(ch\p\nq) Then Insert ch\p After pmark(2)
 Next
 Delete Each chang

 ;Изменяем состояние всех клеток из списка №2
 cell=pmark(2)
 Repeat
  cell=After cell
  If cell=pmark(3) Then Exit
  If cell\nq<16 Then
   newborn cell
  Else
   ;Для всех соседей - уменьшение их счетчика соседей
   For n=0 To 7
    cell2.ptr=cell\neig[n]
    cell2\nq=cell2\nq-1
    ;Занесение соседа в список ячеек, которые, возможно, изменят свое состояние
    If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
   Next
   ;Выключение бита заполненности
   cell\nq=cell\nq And 15
   WritePixel scrx+cell\x,scry+cell\y,0,ib
   cellq=cellq-1
   ;Занесение обработанной ячейки в список потенциально изменяющихся ячеек
   If change(cell\nq) Then ch.chang=New chang: ch\p=cell
  End If
 Forever

 ;Все ячейки из списка №2 переходят в список №1 (стабилизируются)
 Insert pmark(2) Before pmark(3)

 ;Определяется смещение курсора мыши
 dx=MouseX()-xres Sar 1
 dy=MouseY()-yres Sar 1
 ;Если курсор сместился, то происходит перерисовка клеток (весь 1-й список)
 If dx<>0 Or dy<>0 Then
  scrx=scrx+dx
  scry=scry+dy

  SetBuffer ib
  Cls
  SetBuffer FrontBuffer()

  cell=pmark(1)
  Repeat
   cell=After cell
   If cell=pmark(2) Then Exit
   If cell\nq And 16 Then WritePixel scrx+cell\x,scry+cell\y,-1,ib
  Forever

 End If

 SetBuffer ib
 Color 0,0,0
 Rect 0,0,100,36
 Color 255,255,255
 Text 0,0,"FPS: "+1000.0/(MilliSecs()-fps)
 Text 0,12,"Клеток: "+cellq
 Text 0,24,"Поколение: "+gen
 fps=MilliSecs()
 SetBuffer FrontBuffer()
 
Until KeyHit(1)
End

;Функция поиска ячейки в иерархии по отправной точке и смещению
;Если ячейка не существует, она создается вместе с цепочкой указателей
Function findcell.ptr(cell.ptr,x,y)
;Если смещение нулевое, то результат - отправная точка
If x=0 And y=0 Then Return cell
;Запоминаем координаты искомой ячейки (на случай, если ее придется создать)
xx=x+cell\x
yy=y+cell\y
pmax=1 ;Счетчик уровня в иерархии
;Первый этап - подъем вверх
Repeat
 ;Добавление нового указателя сверху, если достигнута вершина иерархии
 If cell\prev=Null Then
  p.ptr=New ptr
  Insert p After pmark(0)
  ;Позиция определяется положением искомой ячейки
  pos=(x<0)+(y<0) Shl 1
  p\nxt[pos]=cell
  cell\prev=p
  cell\prevpos=pos
 Else
  ;Иначе - переход на более высокий уровень в иерархии
  pos=cell\prevpos
  p.ptr=cell\prev
 End If
 ;Изменение координат в соответствии с перемещением
 If pos And 1 Then x=x+pmax
 If pos And 2 Then y=y+pmax
 ;Повышение уровня
 pmax=pmax Shl 1
 cell=p
 ;Выход, если достигнута точка, откуда можно спуститься до искомой
Until x>=0 And y>=0 And x<pmax And y<pmax

;Второй этап - спуск
Repeat
 ;Понижение уровня
 pmax=pmax Shr 1
 ;Определение направления
 pos=((x And pmax)=pmax)+((y And pmax)=pmax) Shl 1

 ;Создание нового указателя, если ветвь отсутствует
 If cell\nxt[pos]=Null Then
  p.ptr=New ptr
  Insert p After pmark(0)
  cell\nxt[pos]=p
  p\prev=cell
  p\prevpos=pos
  ;Если создаем клетку (указатель 1-го уровня), то перемещаем ее в список №1 и
  ; присваиваем запомненные координаты
  If pmax=1 Then
   Insert p After pmark(1)
   p\x=xx
   p\y=yy
  End If
 End If
 cell=cell\nxt[pos]
 ;Если достигнут низ иерархии (уровень клеток) - выход
Until pmax=1
Return cell
End Function

;Функция рождения новой клетки
Function newborn(cell.ptr)
;Поиск, запоминание соседей и увеличение их счетчика количества соседей
For xx=-1 To 1
 For yy=-1 To 1
  If xx Or yy Then
   cell2.ptr=findcell(cell,xx,yy)
   cell2\nq=cell2\nq+1
   ;Занесение соседа в список ячеек, которые, возможно, изменят свое состояние
   If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
   cell\neig[n]=cell2
   n=n+1
  End If
 Next
Next
;Включение бита заполненности
cell\nq=cell\nq Or 16
;Занесение обработанной ячейки в список потенциально изменяющихся ячеек
If change(cell\nq) Then ch.chang=New chang: ch\p=cell
WritePixel cell\x+scrx,cell\y+scry,-1,ib
cellq=cellq+1
End Function