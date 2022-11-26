
Type sn
 Field id%
 Field h%
 Field x#
 Field y#
 Field z#
End Type 

Type snm
 Field id%
 Field s%
 Field x#
 Field y#
 Field z#
End Type

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

camera=CreateCamera()

PositionEntity camera,0,10,-20
RotateEntity camera,20,0,0
RotateEntity CreateLight(1),0,50,0


a%=SnakeCreate(CreateSphere(),6,3,0,0)

For i=1 To 10
 SnakeAddSegment(a,CreateSphere())
Next

b%=SnakeCreate(CreateSphere(),6,-3,0,0)

For i=1 To 10
 SnakeAddSegment(b,CreateSphere())
Next


time%=MilliSecs()

Repeat

 speed#=0.3


 If KeyDown(200) Then SnakeMove(a,0,0,speed#):SnakeMove(b,0,0,speed#)
 If KeyDown(208) Then SnakeMove(a,0,0,-speed#):SnakeMove(b,0,0,-speed#)
 If KeyDown(203) Then SnakeMove(a,-speed#,0,0):SnakeMove(b,-speed#,0,0)
 If KeyDown(205) Then SnakeMove(a,speed#,0,0):SnakeMove(b,speed#,0,0)


 SnakeUpdate()

 While MilliSecs()-time<=20
 Wend


 RenderWorld

 Flip
 Until KeyHit(1)
End


Function SnakeCreate%(h%,s%,x#,y#,z#)
 id%=1
 For SnakeManager.snm = Each snm
  id%=id%+1
 Next
 SnakeManager.snm=New snm
 SnakeManager\id%=id%
 SnakeManager\s%=s%
 SnakeManager\x#=x#
 SnakeManager\y#=y#
 SnakeManager\z#=z#
 Snake.sn=New sn
 Snake\id%=id%
 Snake\h%=h%
 Snake\x#=x#
 Snake\y#=y#
 Snake\z#=z#
 Return id%
End Function


Function SnakeAddSegment(id%,h%)

 For SnakeManager.snm = Each snm
  If id%=SnakeManager\id% Then s%=SnakeManager\s%
  x#=SnakeManager\x#
  y#=SnakeManager\y#
  z#=SnakeManager\z#
 Next 

 For i=1 To s
  Snake.sn = New sn
  Snake\id%=id%
  Snake\x#=x#
  Snake\y#=y#
  Snake\z#=z#
  If i=s Then Snake\h%=h% 
 Next

End Function 



Function SnakeMove(id%,x#,y#,z#)
 For SnakeManager.snm = Each snm
  If id%=SnakeManager\id% Then
   SnakeManager\x#=SnakeManager\x#+x#
   SnakeManager\y#=SnakeManager\y#+y#
   SnakeManager\z#=SnakeManager\z#+z#
   x#=SnakeManager\x#
   y#=SnakeManager\y#
   z#=SnakeManager\z#
   Exit
  EndIf
 Next


 For Snake.sn=Each sn
  If id%=Snake\id% Then
   x1#=Snake\x
   y1#=Snake\y
   z1#=Snake\z
   Snake\x=x#
   Snake\y=y#
   Snake\z=z#
   x#=x1#
   y#=y1#
   z#=z1#
  EndIf
 Next

End Function



Function SnakeUpdate()
 For Snake.sn = Each sn
  If Snake\h<>0 Then PositionEntity Snake\h,Snake\x,Snake\y,Snake\z
 Next
End Function 