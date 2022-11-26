
Type sn
 Field h%
 Field s%
 Field x#
 Field y#
 Field z#
End Type 

x#=0
y#=0
z#=0

Graphics3D 640,480
SetBuffer BackBuffer()



camera=CreateCamera()

PositionEntity camera,0,10,-20
RotateEntity camera,20,0,0
RotateEntity CreateLight(1),0,50,0


; create snake

Snake.sn=New sn

Snake\h%=CreateSphere()
Snake\s%=5
Snake\x#=0
Snake\y#=0
Snake\z#=0


; add segment

Snake.sn=First sn
j=Snake\s%
x=Snake\x#
y=Snake\y#
z=Snake\z#

Snake.sn = Last sn

For i%=1 To j
 Snake.sn=New sn
 Snake\x#=x#
 Snake\y#=y#
 Snake\z#=z#
 If i%=j% Then Snake\h%=CreateSphere()   
 k%=k%+1
Next

Repeat

x1#=x#
y1#=y#
z1#=z#

If KeyDown(200) Then z=z+0.2
If KeyDown(208) Then z=z-0.2
If KeyDown(203) Then x=x-0.2
If KeyDown(205) Then x=x+0.2

If x1#<>x# Or y1#<>y# Or z1#<>z# Then
; rotate snake

x2#=x#
y2#=y#
z2#=z#

 For Snake.sn=Each sn

   x1#=Snake\x
   y1#=Snake\y
   z1#=Snake\z

   Snake\x=x#
   Snake\y=y#
   Snake\z=z#

   x#=x1#
   y#=y1#
   z#=z1#

 Next

x#=x2#
y#=y2#
z#=z2#



EndIf



 For Snake.sn = Each sn
  If Snake\h<>0 Then PositionEntity Snake\h,Snake\x,Snake\y,Snake\z
 Next




 RenderWorld
 Flip
 Until KeyHit(1)
End