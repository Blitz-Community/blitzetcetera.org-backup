;Basic BASIC Interpreter
;Version 0.7
;Written by Michael Denathorn 2005 (www.dabzware.co.uk)
;
;Not the neatest of code, but not bad for a plasterer!!!
;See the Test.txt file for source code example (Which will run :) )
;
;Note:	If anyone finds this useful, all I ask is a mention somewhere
; 		in your credits... Thanks!!! ;)
;Note 2: At the moment, you cannot assign two strings together e.g.
;
;			a$ = "One string" + " Two string"
;
; I did have a little bother with it (it seemed to miss the '+' operator
; To fix it, find the left string operand token first (Just like in the INTEGER & FLOAT_
; IF statements). Then use the following before calling Get_Token() to check if it's a 
; DELIMITER token (Or a operator to you or me!!!):-
;
;			currentToken = currentToken + 1
;
; This should pick up the '+' operator when you call Get_Token(), then you can 
; safely move on to checking the right String operand!!! That should work!!!:)
Const INVALID_FILE = 0
Const UNEXPECT_END_OF_LINE = 1
Const UNDEFINED_VARIABLE = 2
Const TO_MANY_LINES_IN_CODE = 3
Const SYNTAX_ERROR = 4
Const VARIABLE_NOT_DEFINED = 5
Const UNEXPECTED_EOF = 6

Const LINES = 100, TOKENS = 255
Const USER_VARIABLE_AMOUNT = 100
Const USER_VARIABLE_TYPES = 2
Const COMMANDS_AMOUNT = 6
Dim command$(COMMANDS_AMOUNT)
command$(0) = "integer"	:Const INTEGER 		= 0
command$(1) = "print"	:Const PRINT_		= 1
command$(2) = "input"	:Const INPUT_		= 2
command$(3) = "string" 	:Const CHARSTRING 	= 3
command$(4) = "float"	:Const FLOAT_		= 4
command$(5) = "end"		:Const END_			= 5
command$(6) = "write"   :Const WRITE_		= 6

Const operators$ = "+-*^/%=()><"
Const alpha$ = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ"

Const DELIMITER = 1
Const VARIABLE = 2
Const NUMBER  = 3
Const ACOMMAND = 4
Const STRING_ = 5
Const QUOTE   = 6

Const ADD = 1
Const SUBTRACT = 2
Const MULTIPLY = 3
Const DIVIDE = 4

;Token stuff
Dim tokenBank$(LINES,TOKENS)
Global tok$
Global currentLine,currentToken

;arrays to hold user defined variables
Dim userVarName$(USER_VARIABLE_AMOUNT)
Dim userVarType(USER_VARIABLE_AMOUNT)
Dim userVarInt(USER_VARIABLE_AMOUNT)
Dim userVarFloat#(USER_VARIABLE_AMOUNT)
Dim userVarString$(USER_VARIABLE_AMOUNT)
Global variableCount,currentVarPointer

;Load source code
Load_Source()

main()


;Main Function
Function main()
currentToken = -1
currentLine = 0

Repeat
token_type = Get_Token()

	If token_type = VARIABLE
		token_type = Assignment()
	End If
	If token_type = ACOMMAND
		Select tok$
		Case INTEGER
			SetVar(INTEGER)
		Case PRINT_
			DoOutput(PRINT_)
		Case INPUT_
			DoInput()
		Case CHARSTRING
			SetVar(CHARSTRING)
		Case FLOAT_
			SetVar(FLOAT_)
		Case END_ 
			Notify "Program Finished"
			End
		Case WRITE_
			DoOutput(WRITE_)
		End Select
	End If
Forever
End Function

;Input function
Function DoInput()
inputPrompt$ = ">"
return_type = get_token()
If return_type = QUOTE
	tok$ = Left(tok$,Len(tok$)-1)
	tok$ = Right$(tok$,Len(tok$)-1)
	inputPrompt$ = tok$
	currentToken = currentToken + 1
	get_Token()
End If

If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
assignmentVar = currentVarPointer
tempInput$ = Input$(inputPrompt$)

If userVarType(assignmentVar) = INTEGER Then userVarInt(assignmentVar) = Int(tempInput$):Return
If userVarType(assignmentVar) = FLOAT_ Then userVarFloat(assignmentVar) = Float(tempInput$):Return
If userVarType(assignmentVar) = CHARSTRING Then userVarString$(assignmentVar) = tempInput$:Return	
error(SYNTAX_ERROR,"Illegal function parameter")
End Function

;Print & Write function
Function DoOutput(output_type)
return_type = Get_Token()
If return_type = NUMBER
	If output_type = PRINT_ Then Print Int(tok$)
	If output_type = WRITE_ Then Write Int(tok$)
	Return
End If

If return_type = VARIABLE
	If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
	If userVarType(currentVarPointer) = INTEGER
		If output_type = PRINT_ Then Print Int(userVarInt(currentVarPointer))
		If output_type = WRITE_ Then Write Int(userVarInt(currentVarPointer))
		Return
	End If
	If userVarType(currentVarPointer) = FLOAT_
		If output_type = PRINT_ Then Print Float(userVarFloat(currentVarPointer))
		If output_type = WRITE_ Then Write Float(userVarFloat(currentVarPointer))
		Return
	End If
	If userVarType(currentVarPointer) = CHARSTRING
		If output_type = PRINT_ Then Print userVarString$(userVarPointer)
		If output_type = WRITE_ Then Write userVarString$(userVarPointer)
		Return
	End If
End If

If return_type = QUOTE
	tok$ = Left(tok$,Len(tok$)-1)
	tok$ = Right$(tok$,Len(tok$)-1)
	If output_type = PRINT_ Then Print tok$
	If output_type = WRITE_ Then Write tok$
	Return
End If
error(SYNTAX_ERROR,"Illegal function parameter")
End Function


;Massive function. which can easily be re-coded, but at this present time, I cannot be arsed!!!
Function Assignment()

If DoesVariableExist() = False
	error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
End If

assignmentVar = currentVarPointer

If userVarType(assignmentVar) = INTEGER
	Local lIntOperand%
	Local rIntOperand%
	return_type = Get_Token()
	If return_type = DELIMITER
		If tok$ = "="
			;
		Else
			error(SYNTAX_ERROR,"Invalid assignment operator")
		End If
	Else
		error(SYNTAX_ERROR,"Missing operator")
	End If
	
	return_type = Get_Token()
	Select(return_type)
	Case NUMBER
		lIntOperand = Int(tok$)
	Case VARIABLE
		If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
		lIntOperand = userVarInt(currentVarPointer)
	Default
		error(SYNTAX_ERROR,"Invalid assignment,see left operand")
	End Select 
	return_type = Get_Token()
	If return_type <> DELIMITER
		userVarInt(assignmentVar) = lIntOperand
		Return return_type
	End If
	
	If tok$ = "+" Then mathsOperation = ADD
	If tok$ = "-" Then mathsOperation = SUBTRACT
	If tok$ = "*" Then mathsOperation = MULTIPLY
	If tok$ = "/" Then mathsOperation = DIVIDE
	
	return_type = Get_Token()
	Select(return_type)
	Case NUMBER
		rIntOperand = Int(tok$)
	Case VARIABLE
		If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
		rIntOperand = userVarInt(currentVarPointer)
	Default
		error(SYNTAX_ERROR,"Invalid assignment,see right operand")
	End Select 
	
	Select mathsOperation
	Case ADD
		userVarInt(assignmentVar) = lIntOperand + rIntOperand
	Case SUBTRACT
	 	userVarInt(assignmentVar) = lIntOperand - rIntOperand
	Case MULTIPLY
		userVarInt(assignmentVar) = lIntOperand * rIntOperand
	Case DIVIDE
		userVarInt(assignmentVar) = lIntOperand + rIntOperand
	Default
		error(SYNTAX_ERROR,"Unknown mathmatical operator")
	End Select
	Return 

End If



If userVarType(assignmentVar) = FLOAT_
Local lFloatOperand#
Local rFloatOperand#
return_type = Get_Token()
	If return_type = DELIMITER
		If tok$ = "="
			;
		Else
			error(SYNTAX_ERROR,"Invalid assignment operator")
		End If
	Else
		error(SYNTAX_ERROR,"Missing operator")
	End If
	
	return_type = Get_Token()
	Select(return_type)
	Case NUMBER
		lFloatOperand = Int(tok$)
	Case VARIABLE
		If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
		lFloatOperand = userVarFloat(currentVarPointer)
	Default
		error(SYNTAX_ERROR,"Invalid assignment,see left operand")
	End Select 
	return_type = Get_Token()
	If return_type <> DELIMITER
		userVarFloat(assignmentVar) = lFloatOperand
		Return return_type
	End If
	
	If tok$ = "+" Then mathsOperation = ADD
	If tok$ = "-" Then mathsOperation = SUBTRACT
	If tok$ = "*" Then mathsOperation = MULTIPLY
	If tok$ = "/" Then mathsOperation = DIVIDE
	
	return_type = Get_Token()
	Select(return_type)
	Case NUMBER
		rFloatOperand = Float(tok$)
	Case VARIABLE
		If DoesVariableExist() = False Then error(VARIABLE_NOT_DEFINED,("Variable [ "+tok$+" ]"))
		rFloatOperand = userVarFloat(currentVarPointer)
	Default
		error(SYNTAX_ERROR,"Invalid assignment,see right operand")
	End Select 
	
	Select mathsOperation
	Case ADD
		userVarFloat#(assignmentVar) = lFloatOperand + rFloatOperand
	Case SUBTRACT
	 	userVarFloat#(assignmentVar) = lFloatOperand - rFloatOperand
	Case MULTIPLY
		userVarFloat#(assignmentVar) = lFloatOperand * rFloatOperand
	Case DIVIDE
		userVarFloat#(assignmentVar) = lFloatOperand + rFloatOperand
	Default
		error(SYNTAX_ERROR,"Unknown mathmatical operator")
	End Select
	Return 

End If



error(SYNTAX_ERROR,"?????")
End Function

;Check if a variable name is already stored
Function DoesVariableExist()
currentVarPointer = -1
For loopUserVars = 0 To variableCount
	If tok$ = userVarName$(loopUserVars) 
		currentVarPointer = loopUserVars
		Return True
	End If
	Next
Return False
End Function

;Store the variable
Function SetVar(dataType)
return_type = Get_Token()
If return_type = VARIABLE
	If dataType = INTEGER
		userVarName$(variableCount) = tok$
		return_type = Get_Token()
		userVarType(variableCount) = INTEGER
		If return_type = DELIMITER
			If tok$ = "="
				Get_Token()
				userVarInt(variableCount) = Int(tok$)
			Else
				error(SYNTAX_ERROR,"Invalid assignment operator")
			End If
		variableCount = variableCount + 1
		End If
	End If

	If dataType = FLOAT_
		userVarName$(variableCount) = tok$
		return_type = Get_Token()
		userVarType(variableCount) = FLOAT_
		If return_type = DELIMITER
			If tok$ = "="
				Get_Token()
				userVarFloat(variableCount) = Float(tok$)
			Else
				error(SYNTAX_ERROR,"Invalid assignment operator")
			End If
		variableCount = variableCount + 1
		End If
	End If

	If  dataType = CHARSTRING
		userVarName$(variableCount) = tok$	
		return_type = Get_Token()
		userVarType(variableCount) = CHARSTRING
		If return_type = DELIMITER
			If tok$ = "="
				Get_Token()
				tok$ = Left(tok$,Len(tok$)-1)
				tok$ = Right$(tok$,Len(tok$)-1)
				userVarString$(variableCount) = tok$
			Else
				error(SYNTAX_ERROR,"Invalid assignment operator")
			End If
		variableCount = variableCount + 1
		End If
	End If
Else
error(SYNTAX_ERROR,"???")
End If
End Function

;Work out what the next token is
Function Get_Token()
tok$ = ""
token_word = False
;Move up a token
currentToken = currentToken + 1
;Check if token exists, if not,reset line
If tokenBank$(currentLine,currentToken) = "" 
	currentLine = currentLine + 1 
	If currentLine = LINES Then error(TO_MANY_LINES_IN_CODE,currentLine)
	currentToken = 0
	If tokenBank$(currentLine,currentToken) = "" Then error(UNEXPECTED_EOF,"")
End If

;Check if token is an operator
For checkOperator = 1 To Len(operators$)
	If Mid$(operators$,checkOperator,1) = tokenBank$(currentLine,currentToken)
		tok$ = tokenBank$(currentLine,currentToken)
		Return DELIMITER
	End If
Next
;Check if token is a string
If Left$(tokenBank$(currentLine,currentToken),1) = Chr$(34)
	tok$ = tokenBank$(currentLine,currentToken)
	Return QUOTE
End If
;Check if token is a number
For checkDigit = 0 To 9
	If Left$(tokenBank$(currentLine,currentToken),1) = Str(checkDigit)
		tok$ = tokenBank$(currentLine,currentToken)
		Return NUMBER
	End If
Next
;Check if token is a word
For searchAlpha = 1 To Len(alpha$) 
	If Mid$(alpha$,searchAlpha,1) = Left$(tokenBank$(currentLine,currentToken),1) 
		token_word = STRING_
	End If 
Next

;Check if word is a command or a variable
If token_word = STRING_
	For checkCommand = 0 To COMMANDS_AMOUNT
		If tokenBank$(currentLine,currentToken) = command$(checkCommand)
			tok$ = Str(checkCommand)
			Return ACOMMAND
		End If
	Next
	tok$ = tokenBank$(currentLine,currentToken)
	Return VARIABLE
End If

error(SYNTAX_ERROR,"t"+tok$)
End
End Function

;Load Source code function
;Probably a bit of over-kill too LOL
Function Load_Source()
Stop
Local inString = 1
filename$ = RequestFile$("","",False)
filein = ReadFile(filename$)
If filein = 0 Then error(INVALID_FILE,filename$)
Repeat
 .Skip
 tempLine$ = Lower$(ReadLine$(filein))
 
Select(tempLine$)
	Case ""
	;BlankLine
	Default
	If Right$(templine$,1) <> ";" Then error(UNEXPECT_END_OF_LINE,tempLine$)
	;If Left$(templine$,2) = "//" Then Goto Skip
	.SearchAgain
	
	For searchString = 1 To Len(tempLine$)
		If Mid$(tempLine$,searchString,1) = Chr$(34)
			tempString$ = Chr$(34)			
			 For getString = (searchString+1) To Len(tempLine$)
			 	tempChar$ = Mid$(tempLine$,getString,1)
			 	If tempChar$ = Chr$(34) Then Exit
			 	tempString$ = tempString$ + tempChar$
			 Next
			tempString$ = tempString$ + Chr$(34)
			tokenBank$(currentLine,currentToken)  = tempString$
			currentToken = currentToken + 1
			tempLine$ = Right$(templine$,(Len(templine$)-getString))
			Goto SearchAgain
		End If
		If Mid$(tempLine$,searchString,1) = Chr$(32) Or Mid$(tempLine$,searchString,1) = ";"
			tokenBank$(currentLine,currentToken) = Left$(tempLine$,searchString-1)
			currentToken = currentToken + 1
			tempLine$ = Right$(templine$,(Len(templine)-searchString))
			Goto SearchAgain	
		End If
		Next
		currentToken = 0
		currentLine = currentLine + 1
	End Select 
Until Eof(filein)
CloseFile filein
End Function

;If there's an error, notify the user
Function error(err,detail$)
	Select err
	Case INVALID_FILE
		Notify "Invalid source file."+Chr$(13)+Chr$(13)+"DETAIL:-"+Chr$(13)+"Cannot open file "+Chr$(13)+detail$
		End
	Case UNEXPECT_END_OF_LINE
		Notify "Unexpected end of line."+Chr$(13)+Chr$(13)+"Error occured on code line "+(currentLine+1)+Chr$(13)+"DETAIL:-"+Chr$(13)+detail$
		End
	Case TO_MANY_LINES_IN_CODE
		Notify "Source file to large."+Chr$(13)+Chr$(13)+"DETAIL:-"+Chr$(13)+"Maximum amount of lines breached" 
		End
	Case SYNTAX_ERROR
		Notify "Syntax error."+Chr$(13)+Chr$(13)+"Error occured on code line "+(currentLine+1)+Chr$(13)+"DETAIL:-"+Chr$(13)+detail$
		End 
	Case VARIABLE_NOT_DEFINED
		Notify "Variable not defined."+Chr$(13)+Chr$(13)+"Error occured on code line "+(currentline+1)+Chr$(13)+"DETAIL:-"+Chr$(13)+detail$
		End	
	Case UNEXPECTED_EOF
		Notify "Expected end of file "
		Stop
		End	
	End Select
End Function