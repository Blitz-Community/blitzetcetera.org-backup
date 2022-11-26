Graphics 800,600,16,2
SeedRnd MilliSecs()

Global fxText$ = "TypeRiter Text Demo By n8r2k " ;Always add one extra space at the end just in case
Global fxlength = Len(fxText$)

Dim fx$(fxlength)
Dim fxx(fxlength)
Dim fxy(fxlength)
Dim f(fxlength)
d = 100
t = 100
For  v = 1 To fxlength
	fxx(v) = d
	f(v) = t
	fxy(v) = 100
	fx$(v) = Left(fxText$,1)
	fxText$ = Right(fxText$,(Len(fxText$)-1))
	d = d + 10
	t = t + 10
Next 
x = 0
For n = 1 To fxlength
	Text f(n),100,fx$(n)
	Delay(50)
	Flip
Next
WaitKey()
