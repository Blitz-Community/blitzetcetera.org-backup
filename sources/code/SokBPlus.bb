;                                             [AU.sft-2005]
;                               ���������� �� 52 ������� �� Yoshio Murase
;                       �������� �� ���������, ������ �� ����� - ������ ���� :-)
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
color 200,200,200 rect 0,1,15,14 rect 17,1,15,14 rect 1,17,30,14 ;�������������� ;
color 150,150,150 rect 0,2,15,13 rect 18,2,14,13 rect 2,18,29,13 ;    �����      ;
SetBuffer CanvasBuffer(cnv) SetFont fnt TileBlock krp,0,0,0                      ;
Color 80,80,80 Text 129,105,"SuKaBaN",1,1                     ; ����� ���������� ;
Color 230,230,230 Text 127,103,"SuKaBaN",1,1 flipcanvas cnv   ; �������, ��� ��� ;
SetBuffer ImageBuffer(krp,2) color 90,90,255 rect 11,11,10,10; ������ ���������� ;
color 0,0,200 rect 11,11,9,9 SetBuffer ImageBuffer(krp,3)       ; �� ���� �����  ;
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

for k=1 to 8 read a$ for t=1 to 78        ; ��������� ������ � ��������
b=asc(mid(a,t,1)):b=b-48:c1=b/36:c2=(b-c1*36)/6 ; � ����� �� � ������ lv$
c3=b-(c1*36+c2*6):lv$=lv+c1+c2+c3 next next
SetBuffer CanvasBuffer(cnv):spim():sled()                ;������ ������

Repeat
id = WaitEvent()
If id=$803 End
If id=$101 hod()                       ; ������ �����
If id=$401 then                       ; ������ ������
 select eventsource()
  case btn:sled()                  ; ��������� ������
  case btn2:pred()                ; ���������� ������
  case btn3:z=z-1:sled()                ; ���� ������
  case btn4:und()                       ; ������ ����
 end select
 Activategadget cnv     ; ���������� ���������� �����
endif
Forever

function hod()                                ; �����
k=eventdata()-200:krd(k)        ; ����� ����� ������?
if dx=0 and dy=0 return
select m(x,y)                    ; ��� � ��� �� ����?
 case 1,2:gul():ut=ut+(8-k)           ; ������-������
 case 3,4:tr=m(x+dx,y+dy)                      ; ����
  if tr=1 or tr=2 then             ; � �� ������ ���?
   m(x+dx,y+dy)=tr+2:m(x,y)=m(x,y)-2
   drawimage krp,(x+dx)*32,(y+dy)*32,tr+2   ; �������
   gul():ok=ok+(tr=2)-(m(x,y)=2):ut=ut+(k+1)	
   if ok=3 ura()             ; ����� ������ �� �����?
  endif
end select
end function

function gul()                               ; ������
 drawimage krp,xg*32,yg*32,m(xg,yg)
 drawimage rab,x*32,y*32,2-dx-2*(dy=-1)
 xg=x:yg=y flipcanvas cnv	 	
end function

function und()                          ; ������ ����
if ut<>"" then            ; ���� �� ������ � �������?
 un=Right(ut,1)                       ; ��������� ���
 select un
  case 0,3,5,8:krd(un):gul()           ; ������ �����
  case 1,4,6,9:krd(un-1)              ; ������� �����
   ok=ok+(m(xg,yg)=2)-(m(x,y)=4)
   m(xg,yg)=m(xg,yg)+2:m(x,y)=m(x,y)-2
   drawimage krp,x*32,y*32,m(x,y)
   krd(10-un-1):gul()	 			 	
 end select
 ut=left(ut,len(ut)-1)	      ; ������� ������� �����
endif                     ; (���� ������ �� ���� ���)
end function

function krd(k)                ; ����������� ��������
dx=(k=5)-(k=3):dy=(k=8)-(k=0)
x=xg+dx:y=yg+dy
end function

function sled()                    ; ��������� ������
z=z*(z<52)+1:ris(0,-1)
CopyRect 256,0,256,256,0,0
SetGadgetShape cnv,0,0,512,256
end function

function pred()                   ; ���������� ������
CopyRect 0,0,256,256,256,0
SetGadgetShape cnv,-256,0,512,256
z=z-1+52*(z<2):ris(-256,1)
end function

function ris(xi,p)                    ; ������ ������
drawimage krp,xi+256,0,0
Color 80,80,80 text xi+257,1,z         ; ����� ������
Color 230,230,230 text xi+256,0,z
b=z*36-35:ut="":ok=0
 for t=1 to 6 for k=1 to 6           ; ������ �������
 f=mid(lv,b,1)
 drawimage krp,k*32+256+xi,t*32,f  ; ������� �� �����
 m(k,t)=f
  if f=5 then m(k,t)=1:xg=k:yg=t
  if f=4 then ok=ok+1
 b=b+1 next next FlipCanvas cnv
 for t=1 to 256:xi=xi+p              ; �������� �����
  SetGadgetShape cnv,xi,0,512,256;(����� ������ ����)
  delay 2 next
end function

function ura()             ; �������� �������� ������
Color 0,80,0 text 128,104,"��� �� "+len(ut)+" ����!",1,1
Color 0,170,0 text 127,103,"��� �� "+len(ut)+" ����!",1,1
FlipCanvas cnv:spim():sled()
end function

function spim()            ; ���� ������� ������ Next
repeat
 id = WaitEvent()
 if id=$803 End
 If id=$401 and eventsource()=btn exit
forever
Activategadget cnv      ; ���������� ���������� �����
end function

;  ������ (52 �����) � ������ ���� (� ��� ����). � ��������: 0-�����,1-�����,2-�����
;  3-����,4-���� �� �����,5-�����. ����� 6�6 �� ���� ������.
;  ����� ����� � ������? � ��������...
Data "1a:U8�7B1�1Z7�6y7g?�70000r^�[Z80C080<26\9�7�77700r0[~�[Z�=Tb0g[Z�s�00001`g6oZ"
Data "`�[T[000�]6U�[�0[�0{�[�fZ7T000UZa6j�O07000aU7�h0907Z0Z0d[C[6�=�;T8[��BT7T00"
Data "7TfTa�mZ6�=T0070�\Wg�7077�7h0�7[]Z[0O070=0i�]T1T00=T6T:�7�7�001Z1<1`�\�[�fTm�"
Data "[1=a1Z7r77=g9�0\00002TaT[�8k00PZE�ZZZ\0U0[070D1a1Vgg[O\0h~ZB77C_701T1T1�]7\�0s"
Data "[0U�^[3[1\001G=]6^Z761Z000070[ZZ�\nO[~[i�`ZZ000[[U4Yg�0707OZ3[1gBa`0Z[0]ZE`"
Data "O~161`[0i[`Z6ZBs`0000aT`Tu�h[t7caZ�]Z1T1T[agk�0Z0Z0Z0aTZ�]TBZ[6�Z000aqag7Ug[0"
Data "00001Tbh]�0[000O08g�[1[7V�[�0=1_1\0�1b9B[�Z[[T[�i0g6yU[[0Z0fuT[[1[�0s0[9f"
Data "9Z6�Z000�\�UTg�0[00\;U�hC8[70007[<8Ik7C77~0f0Z0cscU1[[[W[g�B7\00\T[TW�1�11["

