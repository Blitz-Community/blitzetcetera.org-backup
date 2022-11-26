SeedRnd MilliSecs()
Graphics 640,480
For y=0 To 1
 For x=0 To 31 Step 2
  puttile x,y,Rand(7)
 Next
Next
For y=2 To 18
 For x=0 To 31
  puttile x,y,Rand(7)
 Next
Next
For y=19 To 20
 For x=0 To 31 Step 2
  puttile x,y,Rand(7)
 Next
Next
Line 16,16,624,16
Line 624,35,624,415
Line 35,415,624,415
Line 16,16,16,415

WaitKey:End

Function puttile(x,y,n)
xx=x*19+16
yy=y*19+16
Select n
 Case 1:Line xx,yy,xx,yy+19
 Case 2:Line xx,yy,xx+19,yy
 Case 3:Line xx+19,yy,xx+19,yy+19
 Case 4:Line xx,yy+19,xx+19,yy+19
 Case 5:Line xx,yy,xx+19,yy+19
 Case 6:Line xx+19,yy,xx,yy+19
End Select
End Function