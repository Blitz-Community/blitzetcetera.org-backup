Const NUMIT = 128000; 
;var  a, b, c, d, e, f : array (1..4) of real; 
 
;(*********************************) 
 
Dim a#(4) 
Dim b#(4) 
Dim c#(4) 
Dim d#(4) 
Dim e#(4) 
Dim f#(4) 
Dim p#(4) 
 
  a(1)=-0 : b(1)=0: c(1)=0: d(1)= 0.16: e(1)=0: f(1)=0: p(1)=0.01 
  a(2)=0.85: b(2)=0.04: c(2)=-0.04: d(2)=0.85: e(2)=0: f(2)=1.6: p(2)=0.85 
  a(3)=0.20: b(3)=-0.26: c(3)=0.23: d(3)=0.22: e(3)=0: f(3)=1.6: p(3)=0.07 
  a(4)=-0.15: b(4)=0.28: c(4)=0.26: d(4)=0.24: e(4)=0: f(4)=0.44: p(4)=0.07 
 
;(*********************************) 
Function Chaos() 
Local  currit, rndnum%, longint 
Local  x#, y#, plotx#, ploty# 
Local pick% 
 
; begin 
 x=0 
 y=0 
   ;randomize; 
   For currit= 1 To 128000  
;   begin 
     pick = Rand(101) - 84; 
blue=0 
 
If pick<=15 Then   
  rndnum=4  
 Else  
  rndnum=1  
EndIf  
If  pick<=7 Then  rndnum=3  
If  pick <= 0 Then rndnum=2 
 
 
     plotx= ((a(rndnum)*x + b(rndnum)*y + e(rndnum))); 
     ploty= ((c(rndnum)*x + d(rndnum)*y + f(rndnum))); 
     x= plotx; 
     y= ploty; 
 
r=0 
g=255 
;lue=0 
 
rezalt=(r Shl 16) Or (g Shl 8) Or blue 
WritePixel((plotx*20)+400, (ploty*20)+320, rezalt); 
WritePixel(400-(plotx*20), 320-(ploty*20), rezalt); 
 
WritePixel((ploty*20)+400, (plotx*20)+320, rezalt); 
WritePixel(400-(ploty*20), 320-(plotx*20), rezalt); 
 
 
;WritePixel((ploty*20)+620, (plotx*20), rezalt); 
;WritePixel((plotx*20)+620, (ploty*20), rezalt); 
 
   Next 
 End Function; 
;(*********************************) 
 
Graphics 800,600,16,1 
SetBuffer BackBuffer() 
ClsColor 0,0,0 
Cls 
 
Chaos() 
Flip 
WaitKey() 
  
 End