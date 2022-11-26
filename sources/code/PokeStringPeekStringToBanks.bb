Function PokeString(bank,offset,s$)
	PokeInt bank,offset,Len(s$)
	For i = 1 To Len(s$)
		PokeByte(bank,offset+i+3, Asc(Mid$(s$,i,1)))
	Next
End Function

Function PeekString$(bank,offset,s$)
	l = PeekInt(bank,offset)
	s$ = ""
	For i = 1 To l
		s$ = s$ + Chr$(PeekByte(bank,offset+i+3))
	Next
	Return s$
End Function
