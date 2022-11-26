Global GrX=800 
global GrY=600 
 
Global BrN=15 
Global BrM=20 
 
 
Global BrickDX=GrX/BrN 
Global BrickDY=GrY/BrM 
 
graphics GrX,GrY 
SetBuffer Backbuffer() 
 
Type Ball 
field img 
field x,y 
field dx,dy 
Field ax#,ay# 
Field BrickX,BrickY 
Field stikX 
Field stik 
Field mode 
end type 
 
Type Brick 
Field img 
Field count 
end type 
 
Type Bita 
Field typ 
Field img 
field x,y 
field Dx,Dy 
field mode 
end type 
 
Type Bonus 
Field Typ 
Field img 
field x,y 
end type 
 
Global MY 
 
Global KolBall 
 
dim Brick.brick(BrN,BrM) 
 
Global KolBricks=0 
 
LoadLevel() 
 
global bita.bita=CreateBita(128,16) 
 
PerFPS=1000/60 
fps=millisecs() 
While not keydown(1) 
CLS 
DrawBricks() 
 
DrawBall() 
 
DrawBonus() 
 
DrawBita() 
while millisecs()-fps<PerFPS 
wend 
text 0,0,1000/(millisecs()-fps) 
fps=millisecs() 
flip 
wend 
 
end 
 
Function CatchBonus(b.bonus) 
select b\typ 
case 1 
BallCount=0 
for ball.ball=each ball 
BallCount=BallCount+1 
next 
ball=first ball 
for i=1 to BallCount 
bl.ball=createball(Ball\x,ball\y,16,16,0) 
bl.ball=createball(Ball\x,ball\y,16,16,0) 
ball=after Ball 
next 
 
case 2 
freelevel() 
Loadlevel() 
return 
 
case 3 
for ball.ball=each ball 
bf=graphicsbuffer() 
setbuffer imagebuffer(ball\img) 
color 200,0,0 
oval 0,0,16,16 
color 255,255,255 
oval 2,2,12,12 
setbuffer bf 
color 255,255,255 
ball\mode=1 
next 
 
case 4 
for ball.ball=each ball 
bf=graphicsbuffer() 
setbuffer imagebuffer(ball\img) 
oval 0,0,16,16 
setbuffer bf 
color 255,255,255 
ball\mode=2 
next 
 
Case 5 
Bita\mode=1 
 
end select 
 
freeimage b\img 
delete b 
 
end function 
 
function DrawBonus() 
for b.bonus=each bonus 
y=b\y+2 
if (Bita\y<=y)and(Bita\y>=b\y) 
if abs(Bita\x-b\x)*2<bita\dx 
CatchBonus(b) 
endif 
endif 
 
if b<>null 
if y>GrY 
freeimage b\img 
delete b 
else 
b\y=y 
drawimage b\img,b\x,b\y 
endif 
endif 
next 
end function 
 
Function DrawBita() 
MY=mouseyspeed() 
x=Bita\x+mousexspeed() 
y=Bita\y+MY 
if x-Bita\dx/2<0 then x=Bita\Dx/2 
if x+Bita\dx/2>GrX then x=GrX-Bita\Dx/2 
if y<GrY*.6 then y=GrY*.6 
if y>GrY-bita\dy then y=GrY-bita\dy 
drawimage bita\img,x,y,bita\typ 
mh=mousehit(1) 
for b.Ball=each Ball 
if mh and b\stik 
b\ax=float(b\stikX)/float(bita\Dx/2)*8 
b\ay=-4 
b\stik=0 
endif 
if abs(b\x-bita\x)*2<=bita\dx 
if (B\y>=y)and(B\y<=Bita\y) 
b\y=y 
b\ay=b\ay;*1.1 
endif 
endif 
next 
bita\x=x 
bita\y=y 
movemouse x,y 
Bita\typ=Bita\typ+mousezspeed() 
if Bita\typ>1 
Bita\typ=0 
elseif Bita\typ<0 
Bita\typ=1 
endif 
end function 
 
function DeleteBall(b.ball) 
freeimage b\img 
delete b 
kolball=kolball-1 
end function 
 
function DrawBall() 
for b.Ball=each Ball 
 
if abs(b\ax)<1 
b\ax=rnd(-4,4) 
endif 
 
if abs(b\ay)<1 
b\ay=rnd(-4,4) 
endif 
 
if b\stik 
y=bita\y-b\dy 
x=bita\x+b\stikX 
 
else 
y=b\y+b\ay 
x=b\x+b\ax 
 
if (y<0) 
y=0 
B\ay=-B\ay 
endif 
if (x>GrX) 
x=GrX 
B\aX=-B\aX 
endif 
if (X<0) 
X=0 
B\aX=-B\aX 
endif 
 
If (bita\y>=b\y)and(bita\y<=y) 
if abs(x-bita\x)*2<bita\dx 
y=bita\y 
b\ay=-b\ay 
ax#=float(x-bita\x)/float(bita\Dx/2)*8 
if ax 
b\ax=ax 
endif 
if bita\typ=1 
b\ax=-b\ax 
endif 
if mY>0 then B\ay=B\ay*0.9 
if bita\mode 
b\stikX=x-bita\x 
b\stik=1 
endif 
endif 
endif 
 
BrX=x/BrickDX 
BrY=Y/BrickDY 
if Brick(BrX,BrY)<>Null 
 
if b\mode<>2 
if BrX<>b\brickX 
B\ax=-B\ax 
endif 
if BrY<>b\brickY 
B\ay=-B\ay 
endif 
endif 
 
DeleteBrick(Brick(BrX,BrY)) 
typ=rnd(-30,5) ;---------------------------------------------- 
if typ>0 
CreateBonus(BrX*BrickDX+BrickDX/2,BrY*BrickDY-BrickDY/2,typ) 
Endif 
 
if b=null 
exit 
endif 
 
if b\mode=1 
BlastBricks(b\BrickX,b\BrickY) 
endif 
 
endif 
 
b\brickX=BrX 
b\brickY=BrY 
endif 
B\y=y 
b\x=x 
 
if (b\y>GrY) 
DeleteBall(b) 
if KolBall=0 
freelevel() 
Loadlevel() 
endif 
else 
drawimage b\img,b\x,b\y 
endif 
next 
end function 
 
Function BlastBricks(x,y) 
for i=x-1 to x+1 
 if (i>=0) and (i<BrN) 
   for j=y-1 to y+1 
    if (j>=0) and (j<BrM) 
     if brick(i,j)<>null 
       DeleteBrick(Brick(i,j)) 
     endif 
    endif 
   next 
 endif 
next 
end function 
 
 
Function CreateBonus(x,y,typ) 
 
select typ 
 
case 1   ;Multiball 
 
if kolball<10 
b.bonus=new bonus 
b\typ=typ 
b\img=createimage(32,32) 
b\x=x 
b\y=y 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
oval 0,0,16,16 
oval 16,0,16,16 
oval 8,16,16,16 
setbuffer bf 
midHandle b\img 
endif 
 
case 2; bomba 
 
b.bonus=new bonus 
b\typ=typ 
b\img=createimage(32,32) 
b\x=x 
b\y=y 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
oval 0,0,32,24 
oval 8,16,16,16 
color 10,10,10 
oval 5,5,10,10 
oval 17,5,10,10 
oval 12,17,7,7 
setbuffer bf 
color 255,255,255 
midHandle b\img 
 
case 3; FireBall 
b.bonus=new bonus 
b\typ=typ 
b\img=createimage(32,32) 
b\x=x 
b\y=y 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
color 200,0,0 
oval 0,0,32,32 
color 255,255,255 
oval 5,5,22,22 
setbuffer bf 
color 255,255,255 
midHandle b\img 
 
case 4; RailBall 
b.bonus=new bonus 
b\typ=typ 
b\img=createimage(32,32) 
b\x=x 
b\y=y 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
Rect 0,5,32,22 
color 250,0,0 
line 0,32,32,0 
color 255,255,255 
setbuffer bf 
color 255,255,255 
midHandle b\img 
 
Case 5; Stik 
b.bonus=new bonus 
b\typ=typ 
b\img=createimage(32,32) 
b\x=x 
b\y=y 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
rect 0,27,32,32 
oval 13,13,5,5 
setbuffer bf 
color 255,255,255 
midHandle b\img 
 
end select 
 
end Function 
 
Function DeleteBrick(Br.brick) 
freeimage Br\img 
delete Br 
KolBricks=KolBricks-1 
if KolBricks=0 
freelevel() 
Loadlevel() 
endif 
end function 
 
function DrawBricks() 
for i=0 to brN-1 
for j=0 to brM-1 
if brick(i,j)<>null 
drawimage brick(i,j)\img,i*BrickDx,j*BrickDy 
endif 
next 
next 
end function 
 
Function CreateBrick.brick(count) 
KolBricks=KolBricks+1 
b.brick= new brick 
b\img=createimage(BrickDx,BrickDy) 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
rect 0,0,BrickDx,BrickDy 
setbuffer bf 
b\count=count 
return b 
end function 
 
Function CreateBall.ball(x,y,dx,dy,stik) 
kolball=kolball+1 
b.ball=new ball 
b\img=createimage(dx,dy) 
b\Stik=Stik 
b\x=x 
b\y=y 
b\dx=dx 
b\dy=dy 
b\ax=0 
b\ay=0 
b\brickX=b\x/BrickDX 
b\brickY=b\y/BrickDY 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img) 
oval 0,0,dx,dy 
setbuffer bf 
midHandle b\img 
return b 
end function 
 
Function CreateBita.bita(dx,dy) 
b.bita=new bita 
b\img=createimage(dx,dy,2) 
b\Dx=dx 
b\Dy=dy 
bf=graphicsbuffer() 
setbuffer imagebuffer(b\img,0) 
oval 0,0,dx,dy 
setbuffer imagebuffer(b\img,1) 
rect 0,0,dx,dy 
color 0,0,0 
oval 0,-dy/2,dx,dy 
color 255,255,255 
setbuffer bf 
midHandle b\img 
return b 
end function 
 
 
function FreeLevel() 
for i=0 to brN-1 
for j=0 to brM-1 
if brick(i,j)<>null 
deletebrick(brick(i,j)) 
endif 
next 
next 
 
for b.ball=each ball 
deleteBall(b) 
next 
 
for bn.bonus=each bonus 
freeimage bn\img 
delete bn 
next 
 
end function 
 
function LoadLevel() 
for i=1 to brN-2 
for j=2 to brM-12 
color rnd(50,255),rnd(50,255),rnd(50,255) 
Brick(i,j)=CreateBrick(1) 
next 
next 
 
color 255,255,255 
 
CreateBall(GrX/2,GrY-200,16,16,1) 
end function