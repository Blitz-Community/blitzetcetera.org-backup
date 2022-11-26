;                                             [AU.sft-2005]
;                               Сокобанчик на 52 задачки от Yoshio Murase
;                       Рекордов не сохраняет, звуков не имеет - лепите сами :-)
global win=CreateWindow("BPlus-sokoban",100,100,340,289,0,1)
global btn=CreateButton("Next",265,10,60,25,win,1)
btn2=createbutton("Previous",265,50,60,25,win,1)
btn3=createbutton("Reset",265,90,60,25,win,1)
btn4=createbutton("Undo",265,130,60,25,win,1)
fnt=LoadFont("Arial",32,True):tmp=CreateImage(32,32)
pnl=createpanel(0,0,256,256,win)
Global cnv=CreateCanvas(0,0,512,256,pnl)
global krp=CreateImage(32,32,6),rab=CreateImage(32,32,4)
global xg,yg,dx,dy,x,y,ok,z,ut$,lv$
dim m(7,7)

maskimage rab,1,0,0 maskimage krp,1,0,0 ;----------------------------------------;
SetBuffer ImageBuffer(krp,0) color 100,100,100 rect 0,0,32,32                    ;
color 200,200,200 rect 0,1,15,14 rect 17,1,15,14 rect 1,17,30,14 ;Художественная ;
color 150,150,150 rect 0,2,15,13 rect 18,2,14,13 rect 2,18,29,13 ;    часть      ;
SetBuffer CanvasBuffer(cnv) SetFont fnt TileBlock krp,0,0,0                      ;
Color 80,80,80 Text 129,105,"SuKaBaN",1,1                     ; Ежели пользовать ;
Color 230,230,230 Text 127,103,"SuKaBaN",1,1 flipcanvas cnv   ; ресурсы, вся эта ;
SetBuffer ImageBuffer(krp,2) color 90,90,255 rect 11,11,10,10; лабуда сократится ;
color 0,0,200 rect 11,11,9,9 SetBuffer ImageBuffer(krp,3)       ; до двух строк  ;
color 150,130,0 rect 0,0,32,32 color 250,230,150 rect 0,0,31,31                  ;
color 250,200,0 rect 1,1,30,30 color 250,230,150 rect 5,5,22,22                  ;
color 160,120,0 rect 5,5,21,21 color 220,170,0 rect 6,6,20,20                    ;
SetBuffer ImageBuffer(krp,4) drawimage krp,0,0,3                                 ;
color 250,0,0 rect 10,10,12,12 setbuffer imagebuffer(tmp)                        ;
color 210,130,100 Oval 5,4,6,4 oval 21,4,6,4 color 115,115,0                     ;
rect 5,8,6,12  rect 21,8,6,12 color 150,150,0 oval 5,12,22,14                    ;
color 200,100,0 oval 8,11,16,14 color 180,80,0 oval 8,11,16,14,0                 ;
oval 9,12,14,12,0 color 250,150,150 rect 15,10,2,3 oval 11,12,10,3               ;
SetBuffer ImageBuffer(krp,5) drawimage tmp,0,0 MidHandle tmp                     ;
for t=0 to 3 setbuffer imagebuffer(rab,t) drawimage tmp,16,16                    ;
rotateimage tmp,90 next freeimage tmp ;------------------------------------------;

for k=1 to 8 read a$ for t=1 to 78        ; Разжимаем данные с задачами
b=asc(mid(a,t,1)):b=b-48:c1=b/36:c2=(b-c1*36)/6 ; и пишем их в строку lv$
c3=b-(c1*36+c2*6):lv$=lv+c1+c2+c3 next next
SetBuffer CanvasBuffer(cnv):spim():sled()                ;Рисуем задачу

Repeat
id = WaitEvent()
If id=$803 End
If id=$101 hod()                       ; Нажата клава
If id=$401 then                       ; Нажата кнопка
 select eventsource()
  case btn:sled()                  ; Следующая задача
  case btn2:pred()                ; Предыдущая задача
  case btn3:z=z-1:sled()                ; Тажа задача
  case btn4:und()                       ; Отмена хода
 end select
 Activategadget cnv     ; Возвращаем управление клаве
endif
Forever

function hod()                                ; Ходим
k=eventdata()-200:krd(k)        ; Какая клава нажата?
if dx=0 and dy=0 return
select m(x,y)                    ; Что у нас на пути?
 case 1,2:gul():ut=ut+(8-k)           ; Ничего-гуляем
 case 3,4:tr=m(x+dx,y+dy)                      ; Ящик
  if tr=1 or tr=2 then             ; А за ящиком что?
   m(x+dx,y+dy)=tr+2:m(x,y)=m(x,y)-2
   drawimage krp,(x+dx)*32,(y+dy)*32,tr+2   ; Толкаем
   gul():ok=ok+(tr=2)-(m(x,y)=2):ut=ut+(k+1)	
   if ok=3 ura()             ; Сколь ящиков на месте?
  endif
end select
end function

function gul()                               ; Гуляем
 drawimage krp,xg*32,yg*32,m(xg,yg)
 drawimage rab,x*32,y*32,2-dx-2*(dy=-1)
 xg=x:yg=y flipcanvas cnv	 	
end function

function und()                          ; Отмена хода
if ut<>"" then            ; Есть ли записи в истории?
 un=Right(ut,1)                       ; Последний ход
 select un
  case 0,3,5,8:krd(un):gul()           ; Гуляем назад
  case 1,4,6,9:krd(un-1)              ; Толкаем назад
   ok=ok+(m(xg,yg)=2)-(m(x,y)=4)
   m(xg,yg)=m(xg,yg)+2:m(x,y)=m(x,y)-2
   drawimage krp,x*32,y*32,m(x,y)
   krd(10-un-1):gul()	 			 	
 end select
 ut=left(ut,len(ut)-1)	      ; Урезаем историю ходов
endif                     ; (один символ на один ход)
end function

function krd(k)                ; Направление движения
dx=(k=5)-(k=3):dy=(k=8)-(k=0)
x=xg+dx:y=yg+dy
end function

function sled()                    ; Следующая задача
z=z*(z<52)+1:ris(0,-1)
CopyRect 256,0,256,256,0,0
SetGadgetShape cnv,0,0,512,256
end function

function pred()                   ; Предыдущая задача
CopyRect 0,0,256,256,256,0
SetGadgetShape cnv,-256,0,512,256
z=z-1+52*(z<2):ris(-256,1)
end function

function ris(xi,p)                    ; Рисуем задачу
drawimage krp,xi+256,0,0
Color 80,80,80 text xi+257,1,z         ; Номер задачи
Color 230,230,230 text xi+256,0,z
b=z*36-35:ut="":ok=0
 for t=1 to 6 for k=1 to 6           ; Читаем условие
 f=mid(lv,b,1)
 drawimage krp,k*32+256+xi,t*32,f  ; Выводим на канву
 m(k,t)=f
  if f=5 then m(k,t)=1:xg=k:yg=t
  if f=4 then ok=ok+1
 b=b+1 next next FlipCanvas cnv
 for t=1 to 256:xi=xi+p              ; Сдвигаем канву
  SetGadgetShape cnv,xi,0,512,256;(чиста понтов ради)
  delay 2 next
end function

function ura()             ; Радуемся решенной задаче
Color 0,80,0 text 128,104,"Мат на "+len(ut)+" ходу!",1,1
Color 0,170,0 text 127,103,"Мат на "+len(ut)+" ходу!",1,1
FlipCanvas cnv:spim():sled()
end function

function spim()            ; Ждем нажатия кнопки Next
repeat
 id = WaitEvent()
 if id=$803 End
 If id=$401 and eventsource()=btn exit
forever
Activategadget cnv      ; Возвращаем управление клаве
end function

;  Задачи (52 штуки) в сжатом виде (в три раза). В разжатом: 0-стена,1-пусто,2-метка
;  3-ящик,4-ящик на метке,5-мужик. Карта 6х6 на одну задачу.
;  Какой смысл в сжатии? А никакого...
Data "1a:U8Ј7B1к1Z7—6y7g?ў70000r^ў[Z80C080<26\9§7Ј77700r0[~Ї[Zњ=Tb0g[Zќs‹00001`g6oZ"
Data "`к[T[000р]6U©[ќ0[Ђ0{ў[кfZ7T000UZa6jўO07000aU7чh0907Z0Z0d[C[6њ=д;T8[Ѓ‹BT7T00"
Data "7TfTaдmZ6ў=T0070‡\Wgл7077–7h0Б7[]Z[0O070=0iЂ]T1T00=T6T:Ј7©7д001Z1<1`нў\ў[дfTmЈ"
Data "[1=a1Z7r77=g9ћ0\00002TaT[Ї8k00PZEЖZZZ\0U0[070D1a1Vgg[O\0h~ZB77C_701T1T1…]7\Ї0s"
Data "[0UЮ^[3[1\001G=]6^Z761Z000070[ZZі\nO[~[iЁ`ZZ000[[U4Yg‹0707OZ3[1gBa`0Z[0]ZE`"
Data "O~161`[0i[`Z6ZBs`0000aT`TuЈh[t7caZЈ]Z1T1T[agkЊ0Z0Z0Z0aTZд]TBZ[6‚Z000aqag7Ug[0"
Data "00001Tbh]§0[000O08gҐ[1[7VҐ[Ј0=1_1\0к1b9B[©Z[[T[дi0g6yU[[0Z0fuT[[1[И0s0[9f"
Data "9Z6…Z000ѓ\ЈUTg¤0[00\;UЈhC8[70007[<8Ik7C77~0f0Z0cscU1[[[W[gжB7\00\T[TW©1ч11["

