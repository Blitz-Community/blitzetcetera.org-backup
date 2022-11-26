Graphics 640,480
SetFont LoadFont("Arial",16)

;Максимальное количество столбцов
Const maxdbf=100
;Служебные переменные
Global dbfile,fleng,stpos,rowq,colq
;Массив для информации о столбцах
Dim dbc(maxdbf,1)

;Открытие базы данных
opendb "storage.dbf"
;Вывод базы данных на экран
For n=0 To rowq-1
 Print trimdbf$(n,0)+": "+trimdbf$(n,1)+"$, "+trimdbf$(n,2)+"pcs."
Next
;Закрытие базы данных
closedb

WaitKey

;Функция открытия файла базы данных
Function opendb(file$)
;Эта переменная будет хранить идентификатор файла
dbfile=OpenFile(file$)
SeekFile dbfile,8
;Начало блока данных
stpos=ReadShort(dbfile)
;Длина записи с данными (одной строки)
fleng=ReadShort(dbfile)
;Расчет количества строк
rowq=(FileSize(file$)-stpos)/fleng

;Счетчик количества столбцов
colq=0
;Позиция первой подзаписи - 1
dbc(0,0)=1
;Цикл по всем подзаписям полей
Repeat
 ;Адрес начала подзаписи
 fp=32*(colq+1)
 ;Выход, если встречен символ с кодом 13
 SeekFile dbfile,fp
 If ReadByte(dbfile)=13 Then Exit 
 ;Считывание длины поля (в символах)
 SeekFile dbfile,fp+16
 dbc(colq,1)=ReadShort(dbfile)
 ;Расчет позиции поля
 If colq>0 Then dbc(colq,0)=dbc(colq-1,0)+dbc(colq-1,1)
 colq=colq+1
Forever
End Function

;Функция закрытия файла базы данных
Function closedb()
CloseFile dbfile
End Function

;Функция считывания данных из ячейки
Function dbf$(row,col)
;Определения адреса ячейки в файле
SeekFile dbfile,stpos+fleng*row+dbc(col,0)
;Формирование символьной переменной с данными ячейки
For n=1 To dbc(col,1)
 m$=m$+Chr$(ReadByte(dbfile))
Next
Return m$
End Function

;Функция, удаляющая в строке пробелы слева и справа
Function trimdbf$(row,col)
Return Trim$(dbf$(row,col))
End Function