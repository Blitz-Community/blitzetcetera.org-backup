Graphics 800,600,16,2
SeedRnd MilliSecs()
x = 399
y = 299
w = 3
l = 3
f = 1

While Not KeyHit(1)
If l > 1000 Then 
x = 399
y = 299
w = 3 
l = 3
EndIf 
Color Rand(0,255),Rand(0,255),Rand(0,255)
Oval x,y,w,l,f
f = 0
x = x - 1
y = y - 1
w = w + 2
l = l + 2
Wend