Graphics3D 1024,768,16,1 
 
SeedRnd MilliSecs() 
 
Const UPS=60 
Global k1,MAX=250 
Global f1=CreateSphere (8) 
HideEntity f1 
Type Mol1 
 Field Model 
 Field Life1 
 Field Life2 
 Field HP# 
 Field Sost 
 Field Cel 
 Field Par 
End Type  
 
cam=CreateCamera() 
PositionEntity cam,0,300,0 
RotateEntity cam,90,0,0 
l=CreateLight() 
RotateEntity l,90,0,0 
 
CreateM1(Rnd(-100,100),Rnd(-100,100),1) 
CreateM1(Rnd(-100,100),Rnd(-100,100),2) 
CreateM1(Rnd(-100,100),Rnd(-100,100),3) 
 
period=1000/UPS 
time=MilliSecs()-period 
 
Repeat 
 Repeat 
  elapsed=MilliSecs()-time 
 Until elapsed 
 ticks=elapsed/period 
 tween#=Float(elapsed Mod period)/Float(period) 
  
 For k=1 To ticks 
  time=time+period  
  If KeyHit(1) End 
         UpdateM1() 
  UpdateWorld  
 Next 
  
 RenderWorld tween 
 Flip 
 
Forever 
 
Function CreateM1(x,z,par) 
m1.Mol1 = New Mol1 
 m1\Model=CopyEntity (f1) 
 If Par =1 Then  
 EntityColor m1\Model,255,0,0 
 End If 
 If Par =2 Then  
 EntityColor m1\Model,0,255,0 
 End If 
 If Par =3 Then  
 EntityColor m1\Model,0,0,255 
 End If 
 PositionEntity m1\Model,x,0,z 
 k1=k1+1 
 M1\Sost=0 
 m1\Cel=CreatePivot () 
 M1\Life1=0 
 M1\Life2=Rnd(100,200) 
 M1\Par = Par 
 M1\HP=1  
End Function 
 
Function UpdateM1() 
For M1.Mol1=Each Mol1 
If M1\Sost=0 Then 
 
 PositionEntity M1\Cel,Rnd(-200,200),0,Rnd(-200,200) 
 PointEntity M1\Model,M1\Cel 
 M1\Sost=1 
 Else 
  If Abs(EntityX(M1\Model)-EntityX(M1\Cel))<1 And Abs(EntityZ(M1\Model)-EntityZ(M1\Cel))<1 Then M1\Sost=0 
  
End If  
 MoveEntity M1\Model,0,0,0.3 
 If M1\Life1<M1\Life2+1 Then M1\Life1=M1\Life1+1 
 
 If M1\Life1>M1\Life2 Then 
 
 If k1<MAX Then  
CreateM1(EntityX(M1\Model),EntityZ(M1\Model),M1\Par) 
M1\Life1=0 
 End If 
 
M1\Sost=0 
 End If  
 
For M2.Mol1=Each Mol1 
 If EntityDistance (M2\Model,M1\Model)<M2\HP+M1\HP Then 
  If M1\Par=1 And M2\Par=2 Then 
   M2\HP=M2\HP-0.1 
   If M1\Hp<5 Then M1\Hp=M1\HP+0.1 
  End If 
 
  If M1\Par=2 And M2\Par=3 Then 
   M2\HP=M2\HP-0.1 
   If M1\Hp<5 Then M1\Hp=M1\HP+0.1 
  End If 
 
  If M1\Par=3 And M2\Par=1 Then 
   M2\HP=M2\HP-0.1 
   If M1\Hp<5 Then M1\Hp=M1\HP+0.1 
  End If 
 End If  
 
Next  
 
 If M1\HP<5 Then 
 ScaleEntity M1\Model,M1\HP,M1\HP,M1\HP 
 
 EntityRadius M1\Model,M1\HP 
 End If  
 
 If M1\HP<1 Then 
  FreeEntity M1\Cel 
  FreeEntity M1\Model 
  Delete M1 
  K1=k1-1 
 End If  
Next 
 
End Function