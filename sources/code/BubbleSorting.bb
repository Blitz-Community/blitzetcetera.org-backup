; Note this is made in BlitzPlus, the print commands may not work the same in Blitz3D
length%=5
Dim Array(length%)
Print "==Creating Random List=="
For i%=0 To length%
	Array(i%)=Rand(1,300)
	Print Array(i%)
Next
Print "==Bubble Sorting List=="
For i%=length% To 1 Step -1
    swap=0
	For j%=0 To i%-1
		If Array(j%)>Array(j%+1)
			Temp%=Array(j%)
			Array(j%)=Array(j%+1)
			Array(j%+1)=Temp%
			swap=1
		EndIf
	Next
	If swap=0 Then Exit
Next
Time%=MilliSecs()-Start%
Print "Took " +Time%+ " ms to calculate."
Print "==Sort Result=="
For i%=0 To length%
	Print Array(i%)
Next
Print "===="
Repeat
Forever