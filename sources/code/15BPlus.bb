;                                         [AU.sft-2005]
;                            Может кто найдет что-нибудь полезное :-)
win=CreateWindow("BPlus-15",100,100,340,288,0,1)
btn=createbutton("Мешать",265,10,60,30,win,1)
fnt=LoadFont("Arial",32,true)
Global cnv=CreateCanvas(0,0,256,256,win)
Global g=3,v=3,pt=CreateImage(64,64,3)
Dim m(3,3) SeedRnd Millisecs()
For t=0 to 14 m(t And 3,t Shr 2)=t+1 Next      ; заполняем массив 4х4
For t=0 to 1 ;------------------------------------------------------;
 SetBuffer ImageBuffer(pt,t)                  ; рисуем две пятнашки ;
 Color 50+100*t,100,0 Rect 0,0,64,64,1 Color 150+100*t,200,0        ;
 For k=0 to 3 Line k,k,63-k,k Line k,k,k,63-k Next                  ;
 Rect 5,5,55,55 Color 100+100*t,150,0 Rect 5,5,54,54,1              ;
Next ;--------------------------------------------------------------;
SetBuffer CanvasBuffer(cnv) SetFont fnt
MaskImage pt,255,255,255:start()                           ; стартуем

Repeat
id = WaitEvent()
If id=$803 End
If id=$201 And EventSource()=cnv hod()
If id=$401 mix()
Forever

Function start()                               ; расставляем пятнашки
 For t=0 to 14                      ; согласно массиву (или массива?)
  x=t And 3:y=t Shr 2	
  drw(x*64,y*64,m(x,y),m(x,y)=t+1)
 Next
End Function

Function drw(x,y,n,i)          ; выводим на канву пятнашку и ее нумер
DrawImage pt,x,y,i                        ;--n-номер пятнашки, i-цвет
Color 150,100,0 Text x+32,y+34,n,1,1
Color 255,245,0 Text x+31,y+33,n,1,1
FlipCanvas cnv
End Function

Function hod()                                     ; двигаем пятнашку
x=EventX() Shr 6:y=EventY() Shr 6                 ;--координаты клика
 If Abs(g-x)+Abs(v-y)=1       ;--если клик на соседней с дырой плашке
  m(g,v)=m(x,y) DrawImage pt,x*64,y*64,2   ;--на ее месте рисуем дыру
  drw(g*64,v*64,m(g,v),m(g,v)=g+v*4+1) ;--на месте дыры рисуем плашку
  g=x:v=y                                ;--обновляем координаты дыры
 Endif		
End Function

Function mix()                    ; тасуем пятнашки по нажатию кнопки
If g*v<9 m(g,v)=m(3,3) DrawImage pt,192,192,2
 For t=0 to 14                      ;--меняем местами числа в массиве
  x=t And 3:y=t Shr 2:a=Rnd(0,3):b=Rnd(0,3)	 	
  if a*b<9 f=m(x,y):m(x,y)=m(a,b):m(a,b)=f
 Next
g=3:v=3:start()                                           ;--стартуем
End Function
