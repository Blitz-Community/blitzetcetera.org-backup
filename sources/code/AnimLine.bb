Graphics 800, 600, 32, 1
SeedRnd MilliSecs()

x1# = 50
y1# = 50
x2# = 500
y2# = 500

sx1# = Rnd(-1, 1)
sy1# = Rnd(-1, 1)
sx2# = Rnd(-1, 1)
sy2# = Rnd(-1, 1)

While Not KeyHit(1)
	AnimatingLine(x1, y1, x2, y2)
	
	x1 = x1 + sx1
	y1 = y1 + sy1
	x2 = x2 + sx2
	y2 = y2 + sy2
	
	If x1 < 0 Or x1 > GraphicsWidth() Then sx1 = -sx1
	If y1 < 0 Or y1 > GraphicsHeight() Then sy1 = -sy1
	If x2 < 0 Or x2 > GraphicsWidth() Then sx2 = -sx2
	If y2 < 0 Or y2 > GraphicsHeight() Then sy2 = -sy2
	
	Flip
Wend

Function AnimatingLine(x1, y1, x2, y2)
	Local dis = Sqr((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
	
	For i = 1 To dis
		flt# = Float i / dis
		x = x1 + (x2 - x1) * flt#
		y = y1 + (y2 - y1) * flt#
		a =  i * 10 + MilliSecs() / 2
			red = (Cos(a) + 1) * .5 * 255
			green = (Cos(a) + 1) * .5 * 255
			blue = (Cos(a) + 1) * .5 * 255
		WritePixel x, y, (blue Or (green Shl 8) Or (red Shl 16) Or ($FF000000)), GraphicsBuffer()
	Next
End Function
