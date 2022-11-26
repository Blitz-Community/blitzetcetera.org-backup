<code bb>
SeedRnd MilliSecs()

;Задаваемые константы: xres,yres - разрешение экрана, xrect, yrect -
; - размеры прямоугольника, заполняемого клетками в случайном порядке,
; cell - плотность клеток внутри этого прямоугольника(%), fsiz - параметр,
; влияющий на размер поля. Размер - (2^fsiz)x(2^fsiz)

Const xres=800,yres=600,xrect=600,yrect=400,cell=30,fsiz=10

;Вычисляемые константы: fsiz0 - ширина поля, fsiz1 - граница индекса
; координаты (X,Y=0..fsiz1), fsiz2 - размер буфера под поле (fsiz0*fsiz),
; fsiz3 - граница адреса ячейки в буфере (X+Y*fsiz0=0..fsiz3),
; xc,yc - сдвиг экрана по отношению к полю (если показывается часть поля),
; x1,y1,x2,y2 - координаты углов заполняемого клетками прямоугольника

Const fsiz0=1 Shl fsiz,fsiz1=fsiz0-1,fsiz2=(fsiz0 Shl fsiz),fsiz3=fsiz2-1
Const xc=(fsiz0-xres) Shr 1,yc=(fsiz0-yres) Shr 1
Const x1=(xres-xrect) Shr 1,x2=((xres+xrect) Shr 1)-1
Const y1=(yres-yrect) Shr 1,y2=((yres+yrect) Shr 1)-1

;Переменные, используемые как в программе, так и в процедуре.
Global ib, bnk, dbnk, dend

Graphics xres,yres,32

;Скрытый экранный буфер
buf=CreateImage(xres,yres)
ib=ImageBuffer(buf)
LockBuffer ib

;Банк под атрибуты ячеек
bnk=CreateBank(fsiz2)
;Банк под координаты клеток
dbnk=CreateBank(fsiz2 Shl 2)

;Заполнение массива сдвига адреса ячейки для определения соседей
Dim neig(8)
k=-fsiz0-1
For n=0 To 7
 If n=3 Then k=-1
 If n=5 Then k=fsiz0-1
 neig(n)=k
 k=k+1+(n=3)
Next

;Заполнение массива правил
Dim change(24)
change(3)=1
For n=16 To 24
 If n<>18 And n<>19 Then change(n)=1
Next

;Граница банка под "интересные" ячейки
dend=-4
;Заполнение клетками прямоугольника
For x=x1 To x2
 For y=y1 To y2
  ;Случайный выбор: поставить ли клетку в данном месте?
  If Rand(0,99)<cell Then putcell x,y
 Next
Next

Repeat

 ;Отображение буфера на экран
 UnlockBuffer ib
 DrawBlock buf,0,0

 ;Подсчет количества поколений в секунду, вывод их и кол-во "интересных"
 ; ячеек на экран 
 Color 0,0,0
 Rect 0,0,50,10
 Color 255,255,255
 Text 0,0,1000.0/(MilliSecs()-fps)
 Text 0,8,dend Shr 2
 fps=MilliSecs()

 LockBuffer ib

 ;Цикл по всем "интересным" ячейкам
 n=0
 While n<=dend
  ;Определение адреса ячейки и ее атрибутов
  pos=PeekInt(dbnk,n)
  k=PeekByte(bnk,pos)
  ;Если она подвержена изменением (должна заполниться или очиститься) -
  ; включается атрибут изменения
  If change(k And 31) Then PokeByte bnk,pos,k Or 32
  ;Если пустая ячейка имеет 0 соседей - она удаляется из списка
  ; и очищается от атрибута занесенности
  If (k And 31)=0 Then
   PokeInt dbnk,n,PeekInt(dbnk,dend)
   PokeByte bnk,pos,0
   dend=dend-4
  Else
   n=n+4
  End If
 Wend

 n=0
 ;Дополнительная переменная, чтобы не проверять новорожденные клетки
 dend2=dend
 ;Второй цикл по всем "интересным" ячейкам
 While n<=dend2
  ;Определение адреса ячейки и ее атрибутов
  pos=PeekInt(dbnk,n)
  k=PeekByte(bnk,pos)

  ;Проверка на атрибут изменения
  If k And 32
   ;Определение атрибута заполненности
   v=(k And 16) Shr 4
   ;Определение координат ячейки
   x=(pos And fsiz1)-xc
   y=(pos Shr fsiz)-yc
   ;Смена изображения ячейки: если она была заполнена - очистить (цвет 0 -
   ; черный), иначе - заполнить (цвет -1 - белый)
   If x>=0 And x<xres And y>=0 And y<yres Then WritePixelFast x,y,v-1,ib
   ;Вычисление приращения атрибута кол-ва соседей для соседей ячейки:
   ; если ячейка заполнилась, то +1, очистилась - -1
   v=1-(v Shl 1)
   ;Цикл по всем соседям
   For nn=0 To 7
    ;Определение адреса соседней ячейки и ее атрибутов
    addr=(neig(nn)+pos) And fsiz3
    p=PeekByte(bnk,addr)
    ;Если прибавляется атрибут кол-ва соседей у ячейки, у которой атрибут
    ; занесенности равен 0 - заносим ее в банк "интересных" и
    ; устанавливаем значение этого атрибута равным 1
    If p=0 Then
     dend=dend+4
     PokeInt dbnk,dend,addr
     PokeByte bnk,addr,65
    Else
     ;Иначе - только изменение атрибута кол-ва соседей
     PokeByte bnk,addr,p+v
    End If
   Next
   ;Смена атрибута заполнения и атрибута изменения (48=16+32)
   PokeByte bnk,pos,k Xor 48   
  End If  
  n=n+4
 Wend

 ;Если нажата клавиша "Esc" - выходим
Until KeyHit(1)

;Функция, заполняющая ячейку с координатами (x,y) и производящая
; дополнительные действия.
Function putcell(x,y)

;Вычисление адреса клетки (в соответствии с сдвигом экрана отн. поля)
pos=x+xc+((y+yc) Shl fsiz)
;Занесение в банк соседей и клетки
For nn=0 To 8
 ;Определение адреса ячейки (команда AND зацикливает поле, сохраняя
 ; адрес в пределах 0..fsiz3)
 addr=(neig(nn)+pos) And fsiz3
 p=PeekByte(bnk,addr)
 ;Увеличение атрибута кол-ва соседей ячейки на 1. Если атрибут заполненности
 ; равен 0, значит ячейка не занесена в банк "интересных", поэтому заносим.
 If p=0 Then
  ;Каждый адрес занимает в банке 4 байта
  dend=dend+4
  PokeInt dbnk,dend,addr
  PokeByte bnk,addr,65
 Else
  PokeByte bnk,addr,p+1
 End If
Next
;Заносим в банк атрибутов на адрес ячейки атрибут "заполненности"
; минус 1 (так как в предыдущем цикле был добавлен атрибут "кол-ва
; соседей")
PokeByte bnk,pos,PeekByte(bnk,pos) + 15
;Отображение клетки в экранном буфере
WritePixel x,y,-1,ib
End Function
</code><noinclude>[[Категория:Код]]</noinclude>