filename$="e:test\test\test\filename.bb"


Print strip_filename$(filename$)
MouseWait
End

Function strip_filename$(full_pathname$)

	; loop and sprip the filename from path.  
	For loop=1 To Len(full_pathname$) 
		old_sash_position=sash_position
		sash_position=Instr(full_pathname$,"\",loop)
					
		If sash_position=0 Then
			sash_position=sash_position
		Else
			strip_filename$=Right(full_pathname$,Len(full_pathname$)-old_sash_position)	
		EndIf					
	Next

	Return strip_filename$

End Function
