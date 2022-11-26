Graphics 640,480,0,2 
 
Dim U#(2,129,129)  
vis#=0.005 
vis0#=2.0-vis 
vis1#=1.0-vis 
 
n=0 
p=1 
q=150 
While  Not KeyHit(1)    
 q=q+1 
 If q>200 
  q=0 
  i1=10+Rand(110) 
  j1=10+Rand(110) 
  For i=-3 To 4 
   For j=-3 To 4 
    v#=2.0-Sqr(i*i+j*j) 
    If v<0 Then v=0.0 
    U(n,i+i1+3,j+j1+3)=U(n,i+i1+3,j+j1+3)-v*0.004 
   Next 
  Next 
 EndIf 
 LockBuffer FrontBuffer()  
 For i = 1 To 128  
  For j = 1 To 128 
   WritePixelFast i+256,j+176,U(n,i,j)*30000+128 
      laplas# = ( U(n,i-1,j)+U(n,i+1,j)+U(n,i,j+1)+U(n,i,j-1) )*0.25 - U(n,i,j) 
   U(p,i,j) = U(n,i,j)*vis0 - U(p,i,j)*vis1 + laplas 
  Next  
 Next 
 UnlockBuffer FrontBuffer() 
 t=p 
 p=n 
 n=t 
 VWait 
Wend