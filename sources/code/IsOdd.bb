;
;	Simple function to return 1 if the value is odd otherwise it will return 0
;	Coded by Ed Upton
;

Print IsOdd( 55 )
WaitKey()
End

Function IsOdd( value )
	If value =0 Then Return 0
	
	If Float( value Mod 2 ) <>0 Then Return 1 Else Return 0
End Function
