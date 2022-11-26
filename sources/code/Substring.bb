;Substring Function(gets the string located at X,Y of string)
Function Substring$(s1$,x,y)
length = y - x
J$ = Mid$(s1$,x,length + 1)
Return J$
End Function

trigstring$ = "Hello my name is Zach"
Print SubString$(trigstring$,3,8)
WaitKey()

;This prints "llo my"
