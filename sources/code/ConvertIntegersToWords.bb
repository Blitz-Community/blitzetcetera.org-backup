;INITIALISE ---------- These globals are required



Global check

Global thousands
Global hundreds
Global tens
Global units



;-----------------------------------TEST ROUTINE-----------------------
.l



check=Input("Value:")

Print "Check: "+check

total$=num_to_text$(check)

Print thousands+" Thousands"
Print hundreds+" Hundreds"
Print tens+" Tens"
Print units+" Units"

Print total$


WaitKey()

thousands=0
hundreds=0
tens=0
units=0

Cls

Goto l

;--------------------------------------FUNCTIONS-----------------------

Function Num_to_Text$(number)


	If number>=1000 

		thousands=(number-(number Mod 1000))
		
		number=number-thousands

		thousands=(thousands/1000)

	EndIf



	If number>=100 

		hundreds=(number-(number Mod 100))

		number=(number-hundreds)

		hundreds=(hundreds/100)	
		
	EndIf



	If number>=10

		tens=(number-(number Mod 10))

		number=(number-tens)
		
		tens=(tens/10)

	EndIf




	units=number



	If thousands>0

		value=thousands

		a$=(convert$(value))

		Text_out$=a$+"-THOUSAND"

	EndIf


	If hundreds>0 And thousands>0

		value=hundreds

		a$=(convert$(value))

		Text_out$=Text_out$+","+a$+"-HUNDRED"

	EndIf
	
	If hundreds>0 And thousands=0

		value=hundreds

		a$=(convert$(value))

		Text_out$=a$+"-HUNDRED"

	EndIf


	If tens=1 And check>99 And units>3 And units<>5
		value=units

		a$=(convert$(value))

		Text_out$=Text_out$+" AND "+a$+"TEEN"

	EndIf


	If tens=1 And check<100 And units<>5
			If units=0 Then Text_out$=Text_out$+" AND TEN"
			If units=1 Then Text_out$="ELEVEN"
			If units=2 Then Text_out$="TWELVE"
			If units=3 Then Text_out$="THIRTEEN"
	EndIf
	
	
		If tens=1 And check<100 And units>3 And units<>5
		value=units

		a$=(convert$(value))

		Text_out$=a$+"TEEN"

	EndIf


	If tens=1 And check>99 And units<>5
			If units=0 Then Text_out$=Text_out$+" AND TEN"
			If units=1 Then Text_out$=Text_out$+" AND ELEVEN"
			If units=2 Then Text_out$=Text_out$+" AND TWELVE"
			If units=3 Then Text_out$=Text_out$+" AND THIRTEEN"
	EndIf
	
If tens=1 And check>99 And units=5 Then Text_out$=Text_out$+" AND FIFTEEN"
If tens=1 And check<100 And units=5 Then Text_out$=Text_out$+"FIFTEEN"

	If check>99 And tens=0 And units>0

		value=units

		a$=(convert$(value))

		Text_out$=Text_out$+" AND "+a$
		
	EndIf

	If check<100 And tens=0 And units>0

		value=units

		a$=(convert$(value))

		Text_out$=a$
		
	EndIf

	If tens>1 And check>99

		value=tens

		a$=(convert_ten$(value))

		Text_out$=Text_out$+" AND "+a$

			If units>0

				value=units
				a$=(convert$(value))
				Text_out$=Text_out$+"-"+a$

			EndIf
	EndIf


	If check<99 And tens>1
	
		value=tens

		a$=(convert_ten$(value))

		Text_out$=a$

			If units>0

				value=units
				a$=(convert$(value))
				Text_out$=Text_out$+"-"+a$

			EndIf
	EndIf

	If check=99 Then Text_Out$="NINETY-NINE"

	If check=0 Then Text_out$="NONE"
	
	Result$=Text_out$
	Return Result$

End Function





Function convert$(integer)

		Select integer
			Case 0
				integer_text$="NIL"
			Case 1
				integer_text$="ONE"
			Case 2
				integer_text$="TWO"
			Case 3
				integer_text$="THREE"
			Case 4
				integer_text$="FOUR"
			Case 5
				integer_text$="FIVE"
			Case 6
				integer_text$="SIX"
			Case 7
				integer_text$="SEVEN"
			Case 8
				integer_text$="EIGHT"
			Case 9
				integer_text$="NINE"

		End Select
		
	result$=integer_text$
	Return Result$

End Function




Function convert_ten$(integer_ten)

		Select integer_ten
			Case 0
				integer_ten_text$=""
			Case 1
				integer_ten_text$=""
			Case 2
				integer_ten_text$="TWENTY"
			Case 3
				integer_ten_text$="THIRTY"
			Case 4
				integer_ten_text$="FORTY"
			Case 5
				integer_ten_text$="FIFTY"
			Case 6
				integer_ten_text$="SIXTY"
			Case 7
				integer_ten_text$="SEVENTY"
			Case 8
				integer_ten_text$="EIGHTY"
			Case 9
				integer_ten_text$="NINETY"

		End Select


	result$=integer_ten_text$
	Return Result$
	
	

End Function
