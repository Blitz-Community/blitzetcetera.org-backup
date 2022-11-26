Print Fact%(0)

WaitKey()
End

Function Fact%(Num)

Perms = Num

For a = 1 To Num - 1

Perms = Perms * (Num - a)

Next

If Num = 0 Then
	Return 1
Else
	Return Perms
EndIf
End Function
