Graphics 800, 600
SetFont LoadFont("Arial Cyr", 14)

Global Health = 100, AskBeer, Money = 10, BarReputation = 5, Sleepness = 10

Dim QuestionCommands$(100)

;Stop
CurrentDialog$ = "������"
Repeat
	CurrentDialog$ = Dialog$(CurrentDialog$)
Until CurrentDialog$ = ""

Function Dialog$(BoxName$)
	;Stop
	Cls
	Restore
		Repeat
			Read DataLine$
			If DataLine$ = "" Then  Print "�� ������ ���� ��� ��������� '" + BoxName$ + "'": finish
			StartingSymbol$ = Left$(DataLine$, 1)

			If StartingSymbol$ = "[" Then
				ThisBoxName$ = Mid$(DataLine$, 2, Instr(DataLine$, "]") - 2)
				If ThisBoxName$ = BoxName$ Then
					Processing = True
				ElseIf Processing = True Then
					Exit
				End If
			ElseIf Processing Then
				BeginningOfCondition = Instr(DataLine$, "{")
				If BeginningOfCondition Then
					EndOfCondition = Instr(DataLine$, "}")
					If Condition(Mid$(DataLine$, BeginningOfCondition + 1,  EndOfCondition - BeginningOfCondition - 1)) Then
						DataLine$ = Left$(DataLine$, BeginningOfCondition - 1) + Mid$(DataLine$, EndOfCondition + 1)
					Else
						DataLine$ = ""
					End If
				End If
			
				If DataLine$ <> "" Then
					Select StartingSymbol$
						Case "|"
							ExecuteCommands Mid$(DataLine$, 2)
						Case "#"
							QuestionNumber = QuestionNumber + 1
							BeginningOfCommands = Instr(DataLine$, "|")
							Print QuestionNumber + ". " + Mid$(DataLine$, 2, BeginningOfCommands - 2)
							QuestionCommands$(QuestionNumber) = Mid$(DataLine$, BeginningOfCommands + 1)
						Default
							Print DataLine$
					End Select
				End If
			End If
		Forever
		
	PrintStats
	;Flip
	Repeat
		Num = WaitKey() - 48
		If Num >= 1 And Num <= QuestionNumber Then Exit
	Forever
	Return ExecuteCommands$(QuestionCommands$(Num))
End Function

Function Condition(ConditionText$)
	

	For Position = 1 To Len(Conditiontext$)
		Symbol$ = Mid$(Conditiontext$, Position, 1)
		Select Symbol$
			Case ">" More = True: SecondItem = True
			Case "<" Less = True: SecondItem = True
			Case "=" Equal = True: SecondItem = True
			Default If SecondItem Then Item2$ = Item2$ + Symbol$ Else Item1$ = Item1$ + Symbol$
		End Select
	Next

	Value1# = GetVariable(Item1$)
	Value2# = GetVariable(Item2$)
	If Equal Then
		If More Then Return Value1# >= Value2#
		If Less Then Return Value1# <= Value2#
		Return Value1# = Value2#
	Else
		If More Then  Return Value1# > Value2#
		If Less Then  Return Value1# < Value2#
		Print "��� ������� ��� ��������� '" + ConditionText$ +"'"
		Finish
	End If
End Function

Function GetVariable#(Name$)
	Name$ = RusTrim$(Name$)
	If Asc(Left$(Name$, 1)) <= 57 Then Return Name$
	Select Name$
		Case "��������" Return Health
		Case "��������������������" Return AskBeer
		Case "������" Return Money
		Case "��������������" Return BarReputation
		Case "����������" Return Sleepness
		Default Print "��� ���������� � ������ '" + Name$ + "'": Finish
	End Select
End Function

Function SetVariable(Name$, Value#)
	Select RusTrim$(Name$)
		Case "��������" Health = Value#
		Case "��������������������" AskBeer = Value#
		Case "������" Money = Value#
		Case "��������������" BarReputation = Value#
		Case "����������" Sleepness = Value#
		Default Print "��� ���������� � ������ '" + Name$ + "'": Finish
	End Select
End Function

Function ExecuteCommands$(Commands$)
	;Stop
	Repeat
		NextCommandPos = Instr(Commands$, ";")
		If Not NextCommandPos Then NextCommandPos = Len(Commands$) + 1
		CurrentCommand$ = Left$(Commands$, NextCommandPos - 1)
		OpeningBracketPos = Instr(CurrentCommand$, "(")
		If OpeningBracketPos Then
			ClosingBracketPos = Instr(CurrentCommand$, ")")
			Parameter$ = Mid$(CurrentCommand$, OpeningBracketPos + 1, ClosingBracketPos - OpeningBracketPos - 1)
			CurrentCommand$ = Left$(CurrentCommand$, OpeningBracketPos - 1) + Mid$(CurrentCommand$, ClosingBracketPos + 1)
		End If
		Select RusTrim$(CurrentCommand$)
			Case "" Print "������ ������ �� ������������� ���������: " + Command$: Finish
			Case "�����" Print "�������������! �������� " + Parameter$ + "!": Finish
			Case "�����" Print "�������� ��������.": Finish
			Case "������������" Print "�� ��������� �� �����.": Finish
			Case "�������������" Print "�� ��������� �� ��������� ������.": Finish
			Case "�������" Return Parameter$
			Default
				EqualSignPos = Instr(CurrentCommand$, "=")
				If EqualSignPos Then SetVariable Left$(CurrentCommand$, EqualSignPos - 1), Mid$(CurrentCommand$, EqualSignPos + 1)
				OperatorSignPos = Instr(CurrentCommand$, ":")
				If OperatorSignPos Then
					VariableName$ = Left$(CurrentCommand$, OperatorSignPos - 1)
					VariableValue# = GetVariable#(VariableName$)
					ValueString$ =RusTrim$(Mid$(CurrentCommand$, OperatorSignPos + 1))
					Value# = Mid$(ValueString$, 2)
					Select Left$(ValueString$, 1)
						Case "+" VariableValue# = VariableValue# + Value#
						Case "-" VariableValue# = VariableValue# - Value#
						Case "*" VariableValue# = VariableValue# * Value#
						Case "/" VariableValue# = VariableValue# / Value#
					End Select
					SetVariable VariableName$, VariableValue#
				End If
		End Select
		Commands$ = Mid$(Commands$, NextCommandPos + 1)
	Until Commands$ = ""
End Function

Function RusTrim$(Txt$)
	For n = 1 To Len(Txt$)
		If Mid$(Txt$, n, 1) <> " " Then Exit 
	Next
	If n > Len(txt$) Then Return
	Txt$ = Mid$(Txt$, n)
	For n = Len(Txt$) To 1 Step -1
		If Mid$(Txt$, n, 1) <> " " Then Exit 
	Next
	Return Left$(Txt$, n)
End Function

Function Finish()
	PrintStats
	;Flip
	WaitKey
	End
End Function

Function PrintStats()
	Print "��������: " + Health + ", ������: " + Money + ", ����������: " + Sleepness + ", ��������� � ����: " + BarReputation
End Function

Data "[������]"
Data "�� ������ � ������ ������. ������ �������� ���� �������� �� ���."
Data "#��� ���� {�������������������� = 0} | �������(�����������)"
Data "#���� ���, � ������, ������! {�������������������� = 1} | �������(�����������2)"
Data "#���, ���� �� ��� ��� �� ������ �� ��������� ����, � ������� ��� ��� ������� ���� � ������. {�������������������� = 2} | �������(���������)"
Data "#��� ������� ���� | �������(�����������)"
Data "#���� | �����"

Data "[�����������]"
Data "- ����, � ���������, �����������. �����, ������ ���-������ ���"
Data "#������ | �������������������� = 1; �������(������)"

Data "[�����������2]"
Data "- �� ���, �� ������ �������? ������� �� ���� - ���� ���!"
Data "#������ | �������������������� = 2; �������(������)"

Data "[���������]"
Data "'������' - ������� ������ � ���������. ����� ��������� ����� ��� �������� ���� ����� �� ������."
Data "�� ������������ �� �����, ������ ������ �������������, �� ������� - ����������� �����."
Data "#������ | �������� : -5; �����������(�������������); �����"

Data "[�����������]"
Data "������ �������� ��� ����� ��������, �������� ����."
Data "#(������ ����) | �������(���������)"
Data "#��������� ���, � ��������� | �������(�����������)"

Data "[���������]"
Data "- � ��� 3 ������"
Data "| ���������� : -6"
Data "#� ���� ��� ����� | �������(���������)"
Data "#���, ������� {������ > 3} | ������ : -3; �������(������)"
Data "#(������������� � ��������) | �����(� ����)"
Data "#(�������) | �������(�����)"

Data "[�����������]"
Data "- � ����� ������, ������� ��� �������� ���� ���������"
Data "#� �������, ��������. �������, � ����� ����. | �������(���������)"
Data "#� ���� ���������� ����� �������, ������� ������� � ������ � �� ���� ������� | �������(���������)"
Data "#(����� ��������, ������ � ������) | �������������� : -1; ������������"

Data "[�����]"
Data "�� ������, ��� ������ �������� ��� ����� ��������� �� �����, ������� ����� ���� � ���� � ��������� ����� �� ����."
Data "������ ����, �������� ��������� ����������, ������ �� �������� � ��������� �������."
Data "#������ | �������������� = -10; �������������"

Data "[�����]", ""