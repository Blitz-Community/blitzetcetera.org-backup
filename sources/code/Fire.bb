Type element
 Field x#,y#,z#
End Type

Type particle
 Field x#,y#,t#,h,ang#
End Type

Const framq=32,siz=32,siz2=siz/2
Const dd#=.01,drad#=.3,dy#=.1

Graphics3D 800,600

fire=CreateTexture(siz,siz,6,framq)
For n=0 To framq-1
 firebuf=TextureBuffer(fire,n)
 LockBuffer firebuf
 t#=1.0*n/framq
 dt=Floor(1024*t#) Mod 256
 r=255
 g=255
 b=255
 If t#>=.25 Then
  b=255-dt
  If t#>=.5 Then
   b=0
   g=255-dt
   If t#>=.75 Then g=0
  End If 
 End If 
 
 Dim q#(siz,siz)
 For nn=0 To 5
  For x=0 To siz-1
   For y=0 To siz-1
    Select nn
     Case 0
      rad#=1.0-1.0*Sqr((x-siz2)*(x-siz2)+(y-siz2)*(y-siz2))/siz2
      If rad#<0 Then rad#=0
      q(x,y)=Rnd(0,1)*rad#
     Case 5
      a=Floor((255-255*n/framq)*q(x,y))
      WritePixelFast x,y,a Shl 24+r Shl 16+g Shl 8+b,firebuf:q(x,y)=0
     Default
      If x>0 And y>0 And x<siz-1 And y<siz-1 Then
       q(x,y)=.2*(q(x-1,y)+q(x+1,y)+q(x,y-1)+q(x,y+1)+q(x,y))
      End If
    End Select
   Next
  Next
 Next
 UnlockBuffer firebuf
Next

SetBuffer BackBuffer()

piv=CreatePivot()
PositionEntity CreateCamera(piv),0,10,-30
RotateEntity CreateLight(),45,0,45

SetBuffer BackBuffer()
Repeat
 For n=1 To 6
  p.particle=New particle
  p\ang=Rnd(-180,180)
  p\h=CreateSprite()
  EntityFX p\h,5+32
 Next
 n=0
 For p=Each particle

  p\x=p\x+.3*Sin(p\ang)
  p\y=p\y+.3*Cos(p\ang)
  p\t=p\t+.0003*(30+Abs(p\ang))+.0005*Abs(p\x)
  p\ang=p\ang-Sgn(p\ang)*2
  TurnEntity p\h,0,0,5
  sc#=3.5*(1.0-Abs(p\t-.45))
  n=n+1

  If p\t>=1 Then
   FreeEntity p\h
   Delete p
  Else
  PositionEntity p\h,p\x+.5*Cos(p\y*30),p\y,0
  ScaleSprite p\h,sc#,sc#
  EntityTexture p\h,fire,Floor(p\t*framq)
  End If
 Next
 
 RenderWorld
 Text 0,0,"Particle quantity:"+n
 Flip
 Delay 50
Until KeyHit(1)
