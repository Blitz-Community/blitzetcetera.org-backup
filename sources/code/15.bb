; ================================================================== 
; Project Title: BarleyBreakLibray (Пятнашки) 
; Author:  Акулинин Александр (Frank) 
; Email:    aculinin@mail.ru 
; Version:   1.0 
; Date:       26.09.05 
; Notes:      Эта библиотека реализует игру пятнашки 
;     
;     
; ================================================================== 
 
 
Type BarleyBreak 
 Field X,Y,Width,Height 
 Field enabled     ; Доступный или нет 
 Field Xc,Yc     ; Координаты пустой клетки 
 Field b[15]     ; Хранться номер пятнашки 
 Field img[15]    ; Порезанное на куски изображение 
 Field ShowTXT,ShowIMG  ; Показывать ли изображение и текст на пятнашке 
 Field rc[3],gc[3],bc[3]  ; Цвет пятнашки не на своем месте, на своем месте, цвет линий, текса 
End Type 

Graphics 800,600,32,2 
SetBuffer BackBuffer() 
SeedRnd(MilliSecs()) 
 
fnt=LoadFont("Arial Cyr",30,1) 
b.BarleyBreak=CreateBarleyBreak(250,100,300,300,0,0,0,0,255,128,0) ; Создаем пятнашки 
 
Repeat 
 Cls 
 UpdateBarleyBreak(b)  ; Обновляем пятнашки 
 RenderBarleyBreak(b,fnt) ; Рисуем особым шрифтом 
 
 If BarleyBreakComplete(b) Then ; Если пятнашки сложены 
  Color 0,128,255 
  Text 400,500,"Пятнашки сложены. Нажми пробел.",1,1 
 End If 
 If KeyDown(57) Then MixBarleyBreak(b,10) ; Перемешиваем пятнашки 
 Flip 
Until KeyDown(1)
 
Function CreateBarleyBreak.BarleyBreak(x,y,width,height,img=0,r1=-1,g1=-1,b1=-1,r2=-1,g2=-1,b2=-1,r3=-1,g3=-1,b3=-1,r4=-1,g4=-1,b4=-1) 
 b.BarleyBreak=New BarleyBreak 
 b\x=x:b\y=y:b\width=width:b\height=height 
 b\enabled=True 
 b\ShowTxt=True:b\ShowImg=False  
 ResetBarleyBreak(b) 
  
 b\rc[0]=r1:b\gc[0]=g1:b\bc[0]=b1  ; Присваеваем цвета 
 b\rc[1]=r2:b\gc[1]=g2:b\bc[1]=b2 
 b\rc[2]=r3:b\gc[2]=g3:b\bc[2]=b3 
 b\rc[3]=r4:b\gc[3]=g4:b\bc[3]=b4 
 
 If r1<0 Then b\rc[0]=0:b\gc[0]=0:b\bc[0]=0  ; Умолчания по цвету 
 If r2<0 Then b\rc[1]=b\rc[0]:b\gc[1]=b\gc[0]:b\bc[1]=b\bc[0] 
 If r3<0 Then b\rc[2]=255:b\gc[2]=255:b\bc[2]=255 
 If r4<0 Then b\rc[3]=b\rc[2]:b\gc[3]=b\gc[2]:b\bc[3]=b\bc[2] 
 
 If img<>0 Then         ; Если есть изображение 
  b\ShowImg=True         ; То мы его будем показывать 
  b\width=ImageWidth(img)      ; Ширина и выстоа по изображению 
  b\height=ImageHeight(img) 
  lx=b\width/4:ly=b\height/4     ; Ширина и высота одной пятнашки 
  b1=ImageBuffer(img)       ; Запомним буфер изображения 
  i=1 
  For m=1 To 4 
   For n=1 To 4 
    If i<16 Then       ; Дробим изображение на пятнадцать частей 
     b\img[i]=CreateImage(lx,ly)  ; И помещаим их в массив 
     b2=ImageBuffer(b\img[i]) 
     CopyRect (n-1)*lx,(m-1)*ly,lx,ly,0,0,b1,b2 
    End If  
    i=i+1  
   Next 
  Next 
 End If 
Return b 
End Function 
 
Function UpdateBarleyBreak(e.BarleyBreak)   ; Обновить пятнашки 
 If MouseDown(1) And e\enabled Then    ; Нажали мышь 
  X=(MouseX()-e\x)/(e\width/4)+1    ; Ищем координаты нажатой пятнашки 
  Y=(MouseY()-e\y)/(e\height/4)+1 
  If MouseX()>e\x And MouseY()>e\y And x>0 And x<5 And y>0 And y<5 Then ; Отсеиваем неправильные координаты 
   If x-1=e\xc And y=e\yc Then RightBB(e)  ; Ищем и передвигаем пустую клетку 
   If x+1=e\xc And y=e\yc Then LeftBB(e) 
   If y-1=e\yc And x=e\xc Then DownBB(e) 
   If y+1=e\yc And x=e\xc Then UpBB(e) 
  End If 
 End If  
End Function 
 
Function RenderBarleyBreak(e.BarleyBreak,fnt=0)  ; Рисуем пятнашки 
Local lx=e\width/4,ly=e\height/4     ; Ширина и высота пятнашки 
Local i=0 
 If fnt<>0 Then SetFont fnt      ; Шрифт для вывода текста 
 For m=1 To 4 
  For n=1 To 4 
     ; Устанавливаем нуджный цвет пятнашки (если она на месте и т.д.) 
   If e\b[i]=i+1 Then Color e\rc[1],e\gc[1],e\bc[1] Else Color e\rc[0],e\gc[0],e\bc[0] 
   Rect e\x+lx*(n-1),e\y+ly*(m-1),lx,ly ; Закрашиваем пятнашку 
    
   If e\b[i]<>0 Then      ; Нулевую пятнашку не надо рисовать 
    Color e\rc[3],e\gc[3],e\bc[3]   ; Цвет текста 
             ; Если надо показать картинку, то покажем, и текст можем показать 
    If e\ShowImg Then DrawImage e\img[e\b[i]],e\x+lx*(n-1),e\y+ly*(m-1) 
    If e\ShowTxt Then Text e\x+lx*(n-1)+lx/2,e\y+ly*(m-1)+ly/2,e\b[i],1,1 
   End If 
   ; Обводим пятнашку линиями 
   Color e\rc[2],e\gc[2],e\bc[2] 
   Rect e\x+lx*(n-1),e\y+ly*(m-1),lx,ly,0 
   i=i+1 
  Next 
 Next  
End Function  
 
Function MixBarleyBreak(e.BarleyBreak,N=1000)  ; Перемешать пятнашки 
 For i=1 To n 
  a=Rand(4) 
  Select A 
   Case 1: DownBB(e) 
   Case 2: UpBB(e) 
   Case 3: LeftBB(e) 
   Case 4: RightBB(e) 
  End Select 
 Next  
End Function 
 
Function ResetBarleyBreak(e.BarleyBreak)   ; Расставить пятнашки 
 For i=0 To 14    ; Начальная расстановка пятнашек 
  e\b[i]=i+1 
 Next 
 e\b[15]=0     ; Нулевая пятнашка в конце 
 e\xc=4:e\yc=4 
End Function  
 
Function BarleyBreakComplete(e.BarleyBreak)   ; Возвращает True, если пятнашки сложены 
 a=True 
 For i=0 To 14 
  If e\b[i]=i+1 Then a=True Else Return False 
 Next 
 Return a  
End Function  
 
Function SetBarleyBreakTxtShow(e.BarleyBreak,show) ; Показывать/не показывать текст 
 e\ShowTXT=show 
End Function 
 
Function SetBarleyBreakImgShow(e.BarleyBreak,show) ; Показывать/не показывать картинку 
 e\ShowImg=show 
End Function 
 
Function SetBarleyBreakEnabled(e.BarleyBreak,enabled) ; Пятнашки доступны/не доступны 
 e\enabled=enabled 
End Function  
 
Function DownBB(e.BarleyBreak)   ; Пустая клетка движется вниз 
 C1=(e\yc-1)*4+e\xc-1    ; Узнаем пустую клетку 
 C2=(e\yc)*4+e\xc-1     ; Узнаем куда эта клетка переместится 
 If c1<0 Or c1>15 Or c2<0 Or c2>15 Or e\yc+1>4 Then Return  ; Перемещение невозможно 
 e\yc=e\yc+1 
 m=e\b[c1]:e\b[c1]=e\b[c2]:e\b[c2]=m 
End Function 
 
Function UpBB(e.BarleyBreak)   ; Пустая клетка движется вниз 
 C1=(e\yc-1)*4+e\xc-1    ; Узнаем пустую клетку 
 C2=(e\yc-2)*4+e\xc-1    ; Узнаем куда эта клетка переместится 
 If c1<0 Or c1>15 Or c2<0 Or c2>15 Or e\yc-1<0 Then Return  ; Перемещение невозможно 
 e\yc=e\yc-1 
 m=e\b[c1]:e\b[c1]=e\b[c2]:e\b[c2]=m 
End Function 
 
Function LeftBB(e.BarleyBreak)   ; Пустая клетка движется вниз 
 C1=(e\yc-1)*4+e\xc-1    ; Узнаем пустую клетку 
 C2=(e\yc-1)*4+e\xc-2    ; Узнаем куда эта клетка переместится 
 If c1<0 Or c1>15 Or c2<0 Or c2>15 Or e\xc-1<1 Then Return  ; Перемещение невозможно 
 e\xc=e\xc-1 
 m=e\b[c1]:e\b[c1]=e\b[c2]:e\b[c2]=m 
End Function 
 
Function RightBB(e.BarleyBreak)   ; Пустая клетка движется вниз 
 C1=(e\yc-1)*4+e\xc-1    ; Узнаем пустую клетку 
 C2=(e\yc-1)*4+e\xc      ; Узнаем куда эта клетка переместится 
 If c1<0 Or c1>15 Or c2<0 Or c2>15 Or e\xc+1>4 Then Return  ; Перемещение невозможно 
 e\xc=e\xc+1 
 m=e\b[c1]:e\b[c1]=e\b[c2]:e\b[c2]=m 
End Function