otarydir#(Asource#,Adest#,smooth#)
	If Asource#>Adest#
		Diff1#=Asource-Adest
		diff2#=(360.0-Asource)+Adest
		If diff2<diff1
			dir#=diff2/smooth
		Else
			dir#=diff1/smooth*-1
		EndIf
	Else
		If Asource#<Adest#
			diff1=Adest-Asource
			diff2=(360.0-Adest)+Asource
			If diff2<diff1
				dir#=diff2/smooth*-1
			Else
				dir#=diff1/smooth
			EndIf
		Else
			dir=0
		EndIf
	EndIf
	Return dir
End Function	
