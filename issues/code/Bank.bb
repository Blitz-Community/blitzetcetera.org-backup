<code bb>
;Maxrec - максимальное количество записей, reclen - длина записи в байтах (в
; данном примере запись содержит координаты: x(int), y(float), приращения:
; dx(Float), dy(Float), цвет с(byte) - 17 байт)
Const maxrec=120000, reclen=17
;Создаем банк
bnk=CreateBank(maxrec*reclen)
;Адрес последнего элемента в банке
addr=-reclen

Graphics 800,600,32

;Массив для хранения значений цветов (градаций серого)
Dim col(255)
For n=0 To 255
 col(n)=n*65793
Next

SetBuffer BackBuffer()
Repeat
 ;Добавляем 400 снежинок
 For n=1 To 400
  addr=addr+reclen
  PokeFloat bnk,addr,Rand(800);координата x
  PokeFloat bnk,addr+4,0;координата y
  PokeFloat bnk,addr+8,Rnd(-1,1);приращение по x
  PokeFloat bnk,addr+12,Rnd(2,10);приращение по y
  PokeByte bnk,addr+16,Rnd(64,255);номер цвета
 Next

 Cls
 LockBuffer BackBuffer()

 ;Цикл по всем снежинкам в банке
 n=0
 While n<=addr
  ;Добавляем приращения к координатам
  x#=PeekFloat(bnk,n)+PeekFloat(bnk,n+8)
  y#=PeekFloat(bnk,n+4)+PeekFloat(bnk,n+12)
  If x<0 Or x>799 Or y#>599 Then
   ;Если снежинка вышла за край экрана - удаляем ее из банка
   CopyBank bnk,addr,bnk,n,reclen
   addr=addr-reclen
   ;Так как на ее место стала последняя, то адрес не увеличивается 
  Else
   ;Иначе - отображаем на экране и обновляем координаты
   WritePixelFast x#,y#,col(PeekByte(bnk,n+12))
   PokeFloat bnk,n,x#
   PokeFloat bnk,n+4,y#
   ;Переходим к следующей снежинке
   n=n+reclen
  End If
 Wend

 UnlockBuffer BackBuffer()
 Flip

Until KeyHit(1)
</code><noinclude>[[Категория:Код]]</noinclude>