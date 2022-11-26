;-------------
; GENERAL INFO
;-------------

; This codelib contains functions for working with data strings. Data strings are basically flat databases consisting of structured strings which can optionally contain multiple named records. Each record can optionally contain multiple value fields which are used to store data. The data is stored and returned as a string, which can also be an integer or float value if you transfer the data into an int or float variable (or use the 'Int()' or 'Float()' commands to convert it).

; Data strings can also optionally have a header string at the start which can be used for any purpose you wish, but which must not contain any record name delimiter characters.


;------
; USAGE
;------

; The structure of a data string is an optional header string at the start of the data string, optionally followed by any number of records. The structure of a record is a name delimiter character, followed by the record name, followed by another name delimiter character, optionally followed by any number of value fields.  The structure of a value field is a string value (which can be an integer or float value, depending on how you use it) followed by a value delimiter character. Example: (uses '#' for the name delimiter, and '^' for the value delimiter.) header string#record 1#value field 1^value field 2^value field etc^record etc#value field 1^value field 2^value field etc^

; The name delimiter character is stored in the global variable 'G_DS_name_delimiter$', and the value field delimiter is stored in the global variable 'G_DS_value_delimiter$'. You can change these characters to whatever you like at any time. Only a single character should be used for each, though, and the same character must not be used for both the name and value delimiters. It's also recommended that you don't use a zero ascii character value for these to avoid conflicts when using zero delimited strings (when passing a data string to a DLL, for example).

; All the data string commands work on a string stored in the global string variable 'G_DS_data_string$'. When you wish to work with a data string, transfer it into this variable. When you are finished working with a data string, transfer it from this variable back to its original storage.

; Use 'F_DS_CreateRecord( L_record_name$, L_num_fields )' to create a new record with the name specified in 'L_record_name$' and with the number of empty fields specified in 'L_num_fields'. The new record is added to the end of the data string. The newly created record becomes the current record and the first field of that record becomes the current field for setting and getting values. Record names are case sensitive and can contain any character other than the one used for the record name delimiter. An empty string should not be used as a record name.

; Use 'F_DS_RemoveRecord( L_record_name$, L_num_fields )' to remove a record with the name specified in 'L_record_name$' and with the number of fields specified in 'L_num_fields'. If the record doesn't exist, no changes will be made to either the data string or its internal pointers, so you can call this function blind. After the record has been removed the internal pointers may not be pointing at any valid record, so you should use 'F_DS_FindRecord()' or 'F_DS_GetNextRecord$( 1 )' when you want to obain a new record to work with.

; Use 'F_DS_FindRecord( L_record_name$ )' to search for a record with the name specified in 'L_record_name$' and set it as the current record and its first field as the current field for setting and getting values. If the record is not found the function will return a zero, otherwise it will return a non-zero value. You can use the value returned by this function to test if the record actually exists.

; Use 'F_DS_SetValue( L_value$, [L_index] )' to set the value of the current field in the current record. After this function is called, the next field in the record (if any) becomes the current field. If the optional 'L_index' parameter is present, then the field indexed by 'L_index' will be the one that the value is set for, and the field following it will become the current field on exit from the function. Field indexes start at one for the first field, and increment by one for each successive field. You should be aware of how many fields a record contains, as there is no limit checking for the number of record fields.

; Use 'F_DS_GetValue( L_value$, [L_index] )' to get the value of the current field in the current record. After this function is called, the next field in the record (if any) becomes the current field. If the optional 'L_index' parameter is present, then the field indexed by 'L_index' will be the one that the value is obtained from, and the field following it will become the current field on exit from the function. Field indexes start at one for the first field, and increment by one for each successive field. You should be aware of how many fields a record contains, as there is no limit checking for the number of record fields. The value returned by this command is a string, so you will need to use the 'Int()' or 'Float()' commands as appropriate if you wish to use the returned value inline in a numeric expression.

; Use 'F_DS_GetNextRecord$( [L_search_from_start_flag] )' to get the name of the next record in the data string, or the first record if a True value is specified in the optional parameter. If no next (or first) record is found, then an empty string will be returned. If a record is found, then that record will be set as the current record and its first field as the current field.

; Use 'F_DS_SetToFirstRecordField()' to set the first field in the current record as the current field.

; Use 'F_DS_SetHeader( L_value$ )' to set the value of an optional header string placed at the start of the data string, before the first record. This command updates the record and field pointers so they are pointing to the same record and field after it is used as they were before it was used, so it can safely be used alongside the record and field access commands. The header must not contain any record name delimiter characters.

; Use 'F_DS_GetHeader$()' to get the value of an optional header string placed at the start of the data string, before the first record. If no header is present, an empty string is returned. This command does not interfere with the record and field pointers, so it can safely be used alongside the record and field commands. The value returned by this command is a string, so you will need to use the 'Int()' or 'Float()' commands as appropriate if you wish to use the returned value inline in a numeric expression.

; Note that none of the above commands are dependant on each other so you can safely remove any you don't need.


;---------------
; POTENTIAL USES
;---------------

; This codelib was created for a level editing program to store standard and custom properties of 3D entities in their namestrings, as well as for general scripting, undo-redo que abstraction layer scripting, map file generation and script loading, etc. I also use it for sending commands between a Blitz3D program and a Purebasic DLL.



Global G_DS_name_delimiter$ = Chr$ ( 30 ) ; Holds the character used as a delimiter at the end of both the name and value components of the INI string fields.
Global G_DS_value_delimiter$ = Chr$ ( 31 ) ; Holds the character used as a delimiter at the end of both the name and value components of the INI string fields.
Global G_DS_data_string$
Global G_DS_first_field_pos
Global G_DS_field_pos


Function F_DS_CreateRecord( L_record_name$, L_num_fields )
; Creates a record within the global string 'G_DS_data_string$', with the name specified in 'L_name$', and with the number of empty fields specified in 'L_num_fields'.
	
	G_DS_first_field_pos = Len ( G_DS_data_string$ ) + Len ( L_record_name$ ) + 3
	G_DS_field_pos = G_DS_first_field_pos
	G_DS_data_string$ = G_DS_data_string$ + G_DS_name_delimiter$ + L_record_name$ + G_DS_name_delimiter$ + String$ ( G_DS_value_delimiter$, L_num_fields )
End Function


Function F_DS_RemoveRecord( L_record_name$, L_num_fields )
; Removes an existing record within the global string 'G_DS_data_string$', with the name specified in 'L_name$', and with the number of empty fields specified in 'L_num_fields'.
; This function can be called without knowing if the record is present in the data string. The data string and its internal pointers are only modified if the record name is found.
; If the record is removed then the internal pointers may no longer be pointing to valid data, so you should use 'F_DS_FindRecord()' or 'F_DS_GetNextRecord$( 1 )' when you want to obain a new record to work with..

L_record_name$ = G_DS_name_delimiter$ + L_record_name$ + G_DS_name_delimiter$
L_start_pos = Instr ( G_DS_data_string$, L_record_name$, 1 )
If L_start_pos
	L_end_pos = Instr ( G_DS_data_string$, G_DS_name_delimiter$, L_start_pos + 1 )
	For i = 1 To L_num_fields
		L_end_pos = Instr ( G_DS_data_string$, G_DS_value_delimiter$, L_end_pos + 1 )
	Next
	G_DS_data_string$ = Left$ ( G_DS_data_string$, L_start_pos - 1 ) + Right$ ( G_DS_data_string$, Len ( G_DS_data_string$ ) - L_end_pos )
EndIf	
End Function


Function F_DS_FindRecord( L_record_name$ )
; Finds a record stored within the global string 'G_DS_data_string$' and with the name specified in 'L_name$' and sets the record (if found) as the current record to parse field values from.
; This function returns the position of the first field in the record (a non-zero value), or a zero if the record was not found. You can use the function to test if a record exists.
	
	L_record_name$ = G_DS_name_delimiter$ + L_record_name$ + G_DS_name_delimiter$
	G_DS_first_field_pos = Instr ( G_DS_data_string$, L_record_name$, 1 )
	If G_DS_first_field_pos
		G_DS_first_field_pos = G_DS_first_field_pos + Len ( L_record_name$ )
		G_DS_field_pos = G_DS_first_field_pos
		Return G_DS_first_field_pos
	EndIf
End Function


Function F_DS_SetToFirstRecordField()
; Sets the first field in the current record as the current field.

	G_DS_field_pos = G_DS_first_field_pos
End Function


Function F_DS_SetValue( L_value$, L_index = 0 )
; Sets the value of the current field (or the field indexed by 'L_index', with the index starting at one and incrementing by one) of the current record, and then sets the next field in the record as the current field.

	If L_index
		L_start_pos = G_DS_first_field_pos
		For i = 2 To L_index
			L_start_pos = Instr ( G_DS_data_string$, G_DS_value_delimiter$, L_start_pos ) + 1
		Next
	Else
		L_start_pos = G_DS_field_pos
	EndIf
		L_end_pos = Instr ( G_DS_data_string$, G_DS_value_delimiter$, L_start_pos )
		G_DS_field_pos = L_start_pos + Len ( L_value$ ) + 1
		G_DS_data_string$ = Left$ ( G_DS_data_string$, L_start_pos - 1 ) + L_value$ + Right$ ( G_DS_data_string$, ( Len ( G_DS_data_string$ ) - L_end_pos ) + 1 )
End Function


Function F_DS_GetValue$( L_index = 0 )
; Returns the value stored in the current field (or the field indexed by 'L_index', with the index starting at one and incrementing by one) of the current record, and then sets the next field in the record as the current field.
; The value returned is a string, so you will need to use the 'Int()' or 'Float()' commands as appropriate if you wish to use the returned value inline in a numeric expression.

	If L_index
		L_start_pos = G_DS_first_field_pos
		For i = 2 To L_index
			L_start_pos = Instr ( G_DS_data_string$, G_DS_value_delimiter$, L_start_pos ) + 1
		Next
	Else
		L_start_pos = G_DS_field_pos
	EndIf
	L_end_pos = Instr ( G_DS_data_string$, G_DS_value_delimiter$, L_start_pos )
	G_DS_field_pos = L_end_pos + 1
	Return Mid$ ( G_DS_data_string$, L_start_pos, L_end_pos - L_start_pos )
End Function


Function F_DS_GetNextRecord$( L_start_pos = 0 )
; Sets the next record in the data string as the current record and its first field as the current field, and returns the record name as a string.
; This function accepts an optional True/False parameter. True = The search begins from the start of the data string. False (the default) = The search begins from the current position in the data string, so the next record will be returned.
; If there are no more records present in the data string then an empty string will be returned by this function.

	If L_start_pos
		L_start_pos = 1
	Else
		L_start_pos = G_DS_field_pos
	EndIf
	L_start_pos = Instr ( G_DS_data_string$, G_DS_name_delimiter$, L_start_pos )
	If L_start_pos
		L_start_pos = L_start_pos + 1
		L_end_pos = Instr ( G_DS_data_string$, G_DS_name_delimiter$, L_start_pos )
		G_DS_first_field_pos = L_end_pos + 1
		G_DS_field_pos = G_DS_first_field_pos
		Return Mid$ ( G_DS_data_string$, L_start_pos, L_end_pos - L_start_pos )
	EndIf	
End Function


Function F_DS_SetHeader( L_value$ )
; Sets a string value stored at the start of the data string, before the first record.
; The header commands will also work if there are no records present in the data string.
; This command updates the data string pointers so that they are still pointing at the same record and field after the command is used, so you can safely use it in conjunction with the record and field access commands.
; The header must not contain any record name delimiter characters.

	L_end_pos = Instr ( G_DS_data_string$, G_DS_name_delimiter$, 1 )
	If L_end_pos = 0
		G_DS_data_string$ = L_value$
	Else
		G_DS_data_string$ = L_value$ + Right$ ( G_DS_data_string$, ( Len ( G_DS_data_string$ ) + 1 ) - L_end_pos )
		L_end_pos = ( Len ( L_value$ ) + 1 ) - L_end_pos
		G_DS_first_field_pos = G_DS_first_field_pos + L_end_pos
		G_DS_field_pos = G_DS_field_pos + L_end_pos
	EndIf
End Function


Function F_DS_GetHeader$()
; Gets a string value stored at the start of the data string, before the first record.
; The header commands will also work if there are no records present in the data string.
; This command does not modify any of the data string pointers, so you can safely use it in conjunction with the record and field commands.
; The value returned by this command is a string, so you will need to use the 'Int()' or 'Float()' commands as appropriate if you wish to use the returned value inline in a numeric expression.

	L_end_pos = Instr ( G_DS_data_string$, G_DS_name_delimiter$, 1 )
	Select L_end_pos
		Case 0
			Return G_DS_data_string$
		Case 1
			Return ""
	End Select
	Return Left$ ( G_DS_data_string$, L_end_pos - 1 )
End Function
