Function IntToStr$(num%, strlen% = 4)
	st$ = Chr$(num And 255)
	For shiftin = 1 To (strlen - 1)
		st$ = st$ + Chr$(num Shr (8 * shiftin))
	Next
	Return st$
End Function 

Function StrToInt%(st$)
	For shiftin = 0 To (Len (st$) - 1)
		num = num Or (Asc (Mid$ (st$, shiftin + 1, 1)) Shl shiftin * 8)
	Next
	Return num
End Function
