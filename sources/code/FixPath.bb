Function FixPath(path$)
;Creates every missing folder in a path. (Fills in gaps in a filepath)
;Handy for file saving.
;Written by Dylan McCall (Mr. Picklesworth)
	path$=extractfilepath(path$)
	Local c=1,pathTo$
	Repeat
		slash=Instr(path$,"\",c)
		If slash=0
			If c>=Len(path$)+1
				Exit
			Else
				slash=Len(path$)+1
			EndIf
		EndIf
		
		folder$=Mid(path$,c,slash-c)
		If FileType(pathTo$+folder)=0 Then CreateDir(pathTo$+folder)
		c=slash+1
		pathTo$=pathTo$+folder+"\"
	Forever
	Return 1
End Function