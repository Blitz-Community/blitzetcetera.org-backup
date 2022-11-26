; Returns the directory from a specified path
Function bbGetDir$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Left(path$,a)
		EndIf
	Next
	Return ""
End Function

; Returns the file from the specifed path
Function bbGetFile$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Right(path$,Len(path$)-a)
		EndIf
	Next
	Return path$
End Function
