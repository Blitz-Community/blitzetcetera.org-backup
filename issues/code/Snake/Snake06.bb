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
 Field t%
 Field mt%
 Field c%
 Field s%
 Field x#
 Field y#
 Field z#
 Field ax#
 Field ay#
 Field az#
End Type


Graphics3D 800,600,16,2
;Graphics3D 1024,768,16,1

SeedRnd MilliSecs()

SetBuffer BackBuffer()

camera=CreateCamera()

PositionEntity camera,75,100,0
RotateEntity camera,60,0,0

RotateEntity CreateLight(1),0,50,0


Dim Map%(15,15,1)

For i=0 To 15
 For j=0 To 15
  t%=CreateSphere ()
  PositionEntity t%,i*10-5,0,j*10-5
  ScaleMesh t%,0.5,0.5,0.5
 Next
Next

For i=0 To 14
 For j=0 To 14
  map(i,j,0)=0
  map(i,j,1)=0
 Next
Next



For i=2 To 10
 t%=CreateCube()
 PositionEntity t%,i*10,1,i*10
 ScaleEntity t%,5,2,5
 Map(i,i,0)=1
 Map(i,i,1)=t%
Next



t%=CreateSphere()
ScaleMesh t%,1.3,1.3,1.8
EntityColor t%,10,250,30
PositionMesh t%,0,1,0

t1%=CreateSphere(8,t%)
PositionEntity t1%,-0.5,2,1.2
ScaleMesh t1%,0.3,0.3,0.3
EntityColor t1%,255,50,30

t2%=CreateSphere(8,t%)
PositionEntity t2%,0.5,2,1.2
ScaleMesh t2%,0.3,0.3,0.3
EntityColor t2%,255,50,30



ScaleEntity t%,2.5,2.5,2.5



a%=SnakeCreate(t%,0,5,0,0,0)

j1#=2.5

For i=3 To 1 Step -1
 t%=CreateSphere()
 EntityColor t%,10,250,30
 ScaleMesh t%,1*j1#,1*j1#,1.5*j1#
 PositionMesh t%,0,(1*j1#)-0.2,0
 SnakeAddSegment(a%,t%)
 j1#=j1#-0.01
Next



time%=MilliSecs()

 Direction%=0

SnakeSetSpeed(a,2)


FoodT%=0


Repeat

; ----- Create a Food --------------------------------------------

 If FoodT%>0 Then FoodT%=FoodT%-1

 If FoodT%=0 

  FoodX%=Int(Rand(0,14))
  FoodZ%=Int(Rand(0,14))
  While map(FoodX%,FoodZ%,0)=1
   FoodX%=Int(Rand(0,14))
   FoodZ%=Int(Rand(0,14))
  Wend

  map(FoodX%,FoodZ%,1)=CreateSphere()
  ScaleMesh map(FoodX%,FoodZ%,1),3,3,3
  EntityColor map(FoodX%,FoodZ%,1),255,50,50
  PositionEntity map(FoodX%,FoodZ%,1),FoodX%*10,2,FoodZ%*10

  map(FoodX%,FoodZ%,0)=2

  FoodT%=-1 

 EndIf

; ----------------------------------------------------------------



; ----- Eat a Food -----------------------------------------------

  If map(Int(SnakeGetX(a)/10),Int(SnakeGetZ(a)/10),0)=2 Or map(Int(SnakeGetX(a)+9)/10,Int(SnakeGetZ(a)+9)/10,0)=2 Then

   FoodT%=Rand(50,300)

   FreeEntity map(FoodX%,FoodZ%,1)
   map(FoodX%,FoodZ%,0)=0
   t%=CreateSphere()
   EntityColor t%,10,250,30
   ScaleMesh t%,1*j1#,1*j1#,1.5*j1#
   PositionMesh t%,0,(1*j1#)-0.2,0
   SnakeAddSegment(a%,t%)
   j1#=j1#-0.05

  EndIf

; ----------------------------------------------------------------


; ----- Check Collisions -----------------------------------------

  For i=0 To 14
   For j=0 To 14
    If map(i,j,0)>2 Then map(i,j,0)=0
   Next
  Next

  For i=1 To SnakeSegmentCount(a)
   t%=SnakeGetSegment(a,i)
   map(EntityX(t%)/10,EntityZ(t%)/10,0)=3
  Next


  If map(Int(SnakeGetX(a)/10),Int(SnakeGetZ(a)/10),0)=1 Or map(Int(SnakeGetX(a)+9)/10,Int(SnakeGetZ(a)+9)/10,0)=1 Then
   WaitKey()
   End
  EndIf

; ----------------------------------------------------------------

; ----- Check outside --------------------------------------------

 If SnakeGetX(a)<-3 Or SnakeGetX(a)>143 Or SnakeGetZ(a)<-3 Or SnakeGetZ(a)>143 Then 

  WaitKey()
  End()

 EndIf


; ----------------------------------------------------------------




 If Direction%=0 Then SnakeMove(a,0,0,speed#)
 If Direction%=2 Then SnakeMove(a,0,0,-speed#)
 If Direction%=1 Then SnakeMove(a,-speed#,0,0)
 If Direction%=3 Then SnakeMove(a,speed#,0,0)

 speed#=0.7


 If KeyDown (16) Then 
  t%=CreateSphere()
  EntityColor t%,10,250,30
  ScaleMesh t%,1*j1#,1*j1#,1.5*j1#
  PositionMesh t%,0,(1*j1#)-0.2,0
  SnakeAddSegment(a%,t%)
  j1#=j1#-0.03
 EndIf


 If KeyDown(200) Then 
  If (SnakeGetX(a) Mod 10)=0 
   Direction%=0
   If Int(SnakeGetAngleY(a))<>0
    SnakeSetAngle(a,0,0,0)
    SnakePosition (a,(Int(SnakeGetX(a))/10)*10,0,SnakeGetZ(a))
   EndIf
  EndIf
 EndIf

 If KeyDown(208) Then 
  If (SnakeGetX(a) Mod 10)=0 
   Direction%=2
   If Int(SnakeGetAngleY(a))<>180
    SnakeSetAngle(a,0,180,0)
    SnakePosition (a,(Int(SnakeGetX(a))/10)*10,0,SnakeGetZ(a))
   EndIf
  EndIf
 EndIf

 If KeyDown(203) Then 
  If (SnakeGetZ(a) Mod 10)=0 
   Direction%=1
   If Int(SnakeGetAngleY(a))<>90
    SnakeSetAngle(a,0,90,0)
    SnakePosition (a,SnakeGetX(a),0,(Int(SnakeGetZ(a))/10)*10)
   EndIf
  EndIf
 EndIf

 If KeyDown(205) Then 
  If (SnakeGetZ(a) Mod 10)=0 
   Direction%=3   
   If Int(SnakeGetAngleY(a))<>270
    SnakeSetAngle(a,0,270,0)
    SnakePosition (a,SnakeGetX(a),0,(Int(SnakeGetZ(a))/10)*10)
   EndIf
  EndIf
 EndIf



 SnakeUpdate()

 RenderWorld

 Flip


 While MilliSecs()-time<=16
 Wend

 Until KeyHit(1)
End



Function SnakeSegmentCount%(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\c%
   Exit
  EndIf
 Next
End Function


Function SnakeGetSegment%(id%,count%)
 For Snake.sn = Each sn
  If id%=Snake\id% And Snake\h%>0 Then 
   count%=count%-1
   If count%=0 Then
    Return Snake\h%
   EndIf
  EndIf
 Next
End Function

Function SnakePosition(id%,x#,y#,z#)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   SnakeManager\x#=x#
   SnakeManager\y#=y#
   SnakeManager\z#=z#
   Exit
  EndIf
 Next
End Function

Function SnakeGetX(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\x#
  EndIf
 Next
End Function

Function SnakeGetZ(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\z#
  EndIf
 Next
End Function

Function SnakeGetY(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\y#
  EndIf
 Next
End Function

Function SnakeGetAngleX#(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\ax#
  EndIf
 Next
End Function

Function SnakeGetAngleY#(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\ay#
  EndIf
 Next
End Function

Function SnakeGetAngleZ#(id%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   Return SnakeManager\az#
  EndIf
 Next
End Function


Function SnakeSetSpeed(id%,t%)
 For SnakeManager.snm = Each snm
  If id=SnakeManager\id% Then
   SnakeManager\mt%=t%
   Exit
  EndIf
 Next
End Function

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
   SnakeManager\t%=SnakeManager\t%-1
   If SnakeManager\t%<=0 Then 
    SnakeManager\t%=SnakeManager\mt%
    SnakeManager\x#=SnakeManager\x#+x#
    SnakeManager\y#=SnakeManager\y#+y#
    SnakeManager\z#=SnakeManager\z#+z#
    x#=SnakeManager\x#
    y#=SnakeManager\y#
    z#=SnakeManager\z#
    ax#=SnakeManager\ax#
    ay#=SnakeManager\ay#
    az#=SnakeManager\az#
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
   EndIf
   Exit
  EndIf
 Next

End Function

Function SnakeCreate%(h%,t%,s%,x#,y#,z#)
 id%=1
 For SnakeManager.snm = Each snm
  id%=id%+1
 Next
 SnakeManager.snm=New snm
 SnakeManager\id%=id%
 SnakeManager\mt%=t%
 SnakeManager\t%=0
 SnakeManager\s%=s%
 SnakeManager\c%=1
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
  If id%=SnakeManager\id% Then 
   s%=SnakeManager\s%
   SnakeManager\c%=SnakeManager\c%+1
   For Snake.sn=Each sn
    x#=Snake\x#
    y#=Snake\y#
    z#=Snake\z#
    ax#=Snake\ax#
    ay#=Snake\ay#
    az#=Snake\az#
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
   Return
  EndIf
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