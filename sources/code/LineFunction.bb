Function FastLine(x,y, x2,y2, rgb=$FFFFFF)
	Local yl = False
	Local incval, finval

	Local short = y2-y
	Local long = x2-x
	If (Abs(short)>Abs(long))
		temp=short
		short=long
		long=temp
		yl = True
	EndIf
	
	finval = long
	If (long<0)
		incval = -1
		long = -long
	Else
		incval = 1
	EndIf

	Local dec#
	If (long=0)
		dec = Float(short)
	Else
		dec = (Float(short)/Float(long))
	EndIf
	
	Local j# = 0.0
	If (yl)
		i=0
		While i <> finval
			If x+j >= 0
				If x+j < GraphicsWidth()
					If y+i >= 0
						If y+i < GraphicsHeight()
							WritePixel x+j, y+i,rgb	
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	Else
		i=0
		While i <> finval
			If x+i >= 0
				If x+i < GraphicsWidth()
					If y+j >= 0
						If y+j < GraphicsHeight()
							WritePixel x+i, y+j,rgb
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	EndIf
End Function

Function FastLine2(x,y, x2,y2, rgb=$FFFFFF,buffer)
		
	Local yl = False
	Local incval, finval

	Local short = y2-y
	Local long = x2-x
	Local gw=GraphicsWidth()
	Local gh=GraphicsHeight()
	If (Abs(short)>Abs(long))
		temp=short
		short=long
		long=temp
		yl = True
	EndIf
	
	finval = long
	If (long<0)
		incval = -1
		long = -long
	Else
		incval = 1
	EndIf

	Local dec#
	If (long=0)
		dec = Float(short)
	Else
		dec = (Float(short)/Float(long))
	EndIf
	
	Local j# = 0.0
	
	LockBuffer (buffer)
		
	If (yl)
		i=0
		While i <> finval
			xj=x+j
			yi=y+i
			If xj >= 0
				If xj < gw
					If yi >= 0
						If yi < gh
							WritePixelFast xj, yi,rgb,buffer	
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	Else
		i=0
		While i <> finval
		xi=x+i
		yj=y+j
			If xi >= 0
				If xi < gw
					If yj >= 0
						If yj < gh
							WritePixelFast xi, yj,rgb,buffer
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	EndIf
	UnlockBuffer(buffer)
End Function

