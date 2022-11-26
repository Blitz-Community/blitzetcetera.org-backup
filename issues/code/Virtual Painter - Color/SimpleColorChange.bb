Const k#=180.0/255,k2#=360.0/255,fx=1
Dim col(2)

Graphics 640,480,32

i=LoadImage("Painter1.jpg")
DrawBlock i,0,0
For y=0 To 479
 For x=0 To 639
  p=ReadPixel(x,y)
  For n=0 To 2
   c=p And 255
   p=p Shr 8
   Select fx
    Case 1;негатив
     c=255-c
    Case 2;затемнение
     c=c Shr 1
    Case 3;осветление
     c=128+(c Sar 1)
    Case 4;порог
     c=255*(c>=128)
    Case 5;уменьшение насыщенности
     c=64+(c Sar 1)
    Case 6;увеличение насыщенности
     c=c Shl 1-128
     If c<0 Then c=0
     If c>255 Then c=255
    Case 7;квадратичное затемнение
     c=255-Sqr(255*255-c*c)
    Case 8;квадратичное осветление
     c=255-c
     c=Sqr(255*255-c*c)
    Case 9;уменьшение количества цветов
     c=Int(c/51)*51
    Case 10;Пила
     c=(c Shr 6)*4
    Case 11;Зубья
     If c And 64 Then c=255-(c And 63)*4 Else c=(c And 63)*4
    Case 12;Впадина
     c=255*(1-Sin(c*k#))
    Case 13;Выступ
     c=255*Sin(c*k#)
    Case 14;Изгиб
     c=Sin(c*k2#)*127+c
   End Select
   col(n)=c
  Next
  WritePixel x,y,col(0)+col(1) Shl 8+col(2) Shl 16-16777216
 Next
Next

WaitKey