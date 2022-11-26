myKey$ = "yeepeekai"

Tmp$=FBK_CryptString$("This is a test",myKey)
Print Tmp$

Tmp2$=FBK_CryptString$(Tmp$,myKey)
Print Tmp2$

WaitKey

Function FBK_CryptString$(Source$,Key$)
	Local ls = Len(Source$)
	Local lk = Len(Key)
	For C=1 To ls
		Char$=Char$+Chr$(Asc(Mid$(Source$,C,1) ) Xor Asc(Mid$(Key$,1 + (C Mod lk),1) ))
	Next

	Return Char$
End Function