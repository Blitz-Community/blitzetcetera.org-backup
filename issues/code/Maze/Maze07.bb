SeedRnd MilliSecs()
Graphics 640,480
Color 255,255,255
For m=1 To 30
 a=Rand(3,12)
 x=Rand(0,32-a)
 y=Rand(0,22-a)
 For n=0 To a
  Rect x*20,(y+n)*20,20,20
  Rect (x+a)*20,(y+n)*20,20,20
  Rect (x+n)*20,(y+a)*20,20,20
  Rect (x+n)*20,y*20,20,20
 Next
Next

WaitKey:End