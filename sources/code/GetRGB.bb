Function GetRed(rgb) 
	Return rgb Shr 16 And %11111111 
End Function 

Function GetGreen(rgb) 
	Return rgb Shr 8 And %11111111 
End Function 

Function GetBlue(rgb) 
	Return rgb And %11111111 
End Function

Function GetRGB(red,green,blue)
	Return blue Or (green Shl 8) Or (red Shl 16)
End Function
