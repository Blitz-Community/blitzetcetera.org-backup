SeedRnd MilliSecs()
Graphics 640,480
m=1
For r=16 To 192 Step 16
 For n=0 To 19
  b=Rand(0,3)
  x1=r*Cos(18*n)
  y1=r*Sin(18*n)
  If b=0 And m=0 Then Goto one
  If b=0 Then Goto two
  If b=1 And (m=1 Or m=0) Then Goto one
  If b=1 And r<>16 And r<>192 Then Goto three
  .one
  x2=r*Cos(18*(n+1))
  y2=r*Sin(18*(n+1))
  Line 320+x1,240+y1,320+x2,240+y2
  Goto three
  .two
  x3=(r+16)*Cos(18*n+1)
  y3=(r+16)*Sin(18*n+1)
  If r=192 Then Goto three
  Line 320+x1,240+y1,320+x3,240+y3
  .three
  m=b
 Next
Next

WaitKey:End