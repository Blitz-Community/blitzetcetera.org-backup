
Type sn
 Field id%
 Field h%
 Field x#
 Field y#
 Field z#
 Field ax#
 Field ay#
 Field az#
End Type 

Type snm
 Field id%
 Field s%
 Field x#
 Field y#
 Field z#
 Field ax#
 Field ay#
 Field az#
End Type

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

camera=CreateCamera()

PositionEntity camera,0,10,-20
RotateEntity camera,20,0,0
RotateEntity CreateLight(1),0,50,0


t%=CreateSphere()
ScaleMesh t%,1.3,1.3,1.8
EntityColor t%,10,250,30

t1%=CreateSphere(8,t%)
PositionEntity t1%,-0.5,1,1.2
ScaleMesh t1%,0.3,0.3,0.3
EntityColor t1%,255,50,30

t2%=CreateSphere(8,t%)
PositionEntity t2%,0.5,1,1.2
ScaleMesh t2%,0.3,0.3,0.3
EntityColor t2%,255,50,30



a%=SnakeCreate(t%,10,3,0,0)

j1#=1

For i=10 To 1 Step -1
 t%=CreateSphere()
 EntityColor t%,10,250,30
 ScaleMesh t%,1*j1#,1*j1#,1.5*j1#
 PositionMesh t%,0,(1*j1#)-1.2,0
 SnakeAddSegment(a%,t%)
 j1#=j1#-0.07
Next


time%=MilliSecs()

Repeat

 speed#=0.1


 If KeyDown(200) Then SnakeMove(a,0,0,speed#):SnakeSetAngle(a,0,0,0)
 If KeyDown(208) Then SnakeMove(a,0,0,-speed#):SnakeSetAngle(a,0,180,0)
 If KeyDown(203) Then SnakeMove(a,-speed#,0,0):SnakeSetAngle(a,0,90,0)
 If KeyDown(205) Then SnakeMove(a,speed#,0,0):SnakeSetAngle(a,0,270,0)


 SnakeUpdate()

 While MilliSecs()-time<=20
 Wend


 RenderWorld

 Text 0,0,Str(SnakeTime%)

 Flip
 Until KeyHit(1)
End

Function SnakeSetAngle(id%,ax#,ay#,az#)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   SnakeManager\ax#=ax#
   SnakeManager\ay#=ay#
   SnakeManager\az#=az#
   Exit
  EndIf
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
   ax#=SnakeManager\ax#
   ay#=SnakeManager\ay#
   az#=SnakeManager\az#
   Exit
  EndIf
 Next
 For Snake.sn=Each sn
  If id%=Snake\id% Then
   x1#=Snake\x
   y1#=Snake\y
   z1#=Snake\z
   ax1#=Snake\ax
   ay1#=Snake\ay
   az1#=Snake\az
   Snake\x=x#
   Snake\y=y#
   Snake\z=z#
   Snake\ax=ax#
   Snake\ay=ay#
   Snake\az=az#
   x#=x1#
   y#=y1#
   z#=z1#
   ax#=ax1#
   ay#=ay1#
   az#=az1#
  EndIf
 Next
End Function

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
 SnakeManager\ax#=EntityPitch(h%)
 SnakeManager\ay#=EntityYaw(h%)
 SnakeManager\az#=EntityRoll(h%)
 Snake.sn=New sn
 Snake\id%=id%
 Snake\h%=h%
 Snake\x#=x#
 Snake\y#=y#
 Snake\z#=z#
 Snake\ax#=EntityPitch(h%)
 Snake\ay#=EntityYaw(h%)
 Snake\az#=EntityRoll(h%)
 Return id%
End Function


Function SnakeAddSegment(id%,h%)
 For SnakeManager.snm = Each snm
  If id%=SnakeManager\id% Then s%=SnakeManager\s%
  x#=SnakeManager\x#
  y#=SnakeManager\y#
  z#=SnakeManager\z#
  ax#=SnakeManager\ax#
  ay#=SnakeManager\ay#
  az#=SnakeManager\az#
 Next 
 For i=1 To s
  Snake.sn = New sn
  Snake\id%=id%
  Snake\x#=x#
  Snake\y#=y#
  Snake\z#=z#
  Snake\ax#=ax#
  Snake\ay#=ay#
  Snake\az#=az#
  If i=s Then Snake\h%=h% 
 Next
End Function 

Function SnakeUpdate()
 For Snake.sn = Each sn
  If Snake\h<>0 Then 
   PositionEntity Snake\h%,Snake\x#,Snake\y#,Snake\z#
   RotateEntity Snake\h%,Snake\ax#,Snake\ay#,Snake\az#
  EndIf
 Next
End Function 