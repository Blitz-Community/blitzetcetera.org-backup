Graphics 800, 600
SetFont LoadFont("Arial Cyr", 14)

Global Health = 100, AskBeer, Money = 10, BarReputation = 5, Sleepness = 10

Dim QuestionCommands$(100)

;Stop
CurrentDialog$ = "Начало"
Repeat
	CurrentDialog$ = Dialog$(CurrentDialog$)
Until CurrentDialog$ = ""

Function Dialog$(BoxName$)
	;Stop
	Cls
	Restore
		Repeat
			Read DataLine$
			If DataLine$ = "" Then  Print "Не найден бокс под названием '" + BoxName$ + "'": finish
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
		Print "Нет команды под названием '" + ConditionText$ +"'"
		Finish
	End If
End Function

Function GetVariable#(Name$)
	Name$ = RusTrim$(Name$)
	If Asc(Left$(Name$, 1)) <= 57 Then Return Name$
	Select Name$
		Case "Здоровье" Return Health
		Case "СколькоРазПросилПиво" Return AskBeer
		Case "Деньги" Return Money
		Case "РепутацияВБаре" Return BarReputation
		Case "Сонливость" Return Sleepness
		Default Print "Нет переменной с именем '" + Name$ + "'": Finish
	End Select
End Function

Function SetVariable(Name$, Value#)
	Select RusTrim$(Name$)
		Case "Здоровье" Health = Value#
		Case "СколькоРазПросилПиво" AskBeer = Value#
		Case "Деньги" Money = Value#
		Case "РепутацияВБаре" BarReputation = Value#
		Case "Сонливость" Sleepness = Value#
		Default Print "Нет переменной с именем '" + Name$ + "'": Finish
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
			Case "" Print "Строка команд не заканчивается переходом: " + Command$: Finish
			Case "Битва" Print "Приготовьтесь! Сражение " + Parameter$ + "!": Finish
			Case "Выход" Print "Разговор закончен.": Finish
			Case "ВыходНаУлицу" Print "Вы оказались на улице.": Finish
			Case "ВыходИзГорода" Print "Вы оказались за пределами города.": Finish
			Case "Перейти" Return Parameter$
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
	Print "Здоровье: " + Health + ", деньги: " + Money + ", сонливость: " + Sleepness + ", репутация в баре: " + BarReputation
End Function

Data "[Начало]"
Data "Вы стоите у барной стойки. Бармен обращает свое внимание на вас."
Data "#Мне пива {СколькоРазПросилПиво = 0} | Перейти(ПроситьПиво)"
Data "#Пива мне, я сказал, быстро! {СколькоРазПросилПиво = 1} | Перейти(ПроситьПиво2)"
Data "#Так, если ты мне сию же минуту не принесешь пива, я разобью вот эту бутылку тебе о голову. {СколькоРазПросилПиво = 2} | Перейти(ВасИзбили)"
Data "#Мне чашечку кофе | Перейти(ПроситьКофе)"
Data "#Уйти | Выход"

Data "[ПроситьПиво]"
Data "- Пиво, к сожалению, закончилось. Может, хотите что-нибудь еще"
Data "#Дальше | СколькоРазПросилПиво = 1; Перейти(Начало)"

Data "[ПроситьПиво2]"
Data "- Ты что, на голову больной? Сказали же тебе - пива нет!"
Data "#Дальше | СколькоРазПросилПиво = 2; Перейти(Начало)"

Data "[ВасИзбили]"
Data "'Хорошо' - говорит бармен и удаляется. Через некоторое время вас внезапно бьют сзади по голове."
Data "Вы очухиваетесь на улице, голова просто раскалывается, на затылке - здоровенная шишка."
Data "#Дальше | Здоровье : -5; Модификация(ШишкаНаГолове); Выход"

Data "[ПроситьКофе]"
Data "Бармен наливает вам чашку крепкого, горячего кофе."
Data "#(Выпить кофе) | Перейти(КофеВыпит)"
Data "#Благодарю вас, я передумал | Перейти(КофеНеВыпит)"

Data "[КофеВыпит]"
Data "- С вас 3 монеты"
Data "| Сонливость : -6"
Data "#У меня нет денег | Перейти(ВасИзбили)"
Data "#Вот, держите {Деньги > 3} | Деньги : -3; Перейти(Начало)"
Data "#(Приготовиться к сражению) | Битва(в баре)"
Data "#(Убежать) | Перейти(Побег)"

Data "[КофеНеВыпит]"
Data "- В таком случае, попрошу вас покинуть наше заведение"
Data "#Я пошутил, извините. Конечно, я выпью кофе. | Перейти(КофеВыпит)"
Data "#Я буду оставаться здесь столько, сколько пожелаю и плевал я на ваши просьбы | Перейти(ВасИзбили)"
Data "#(Глупо улыбаясь, пройти к выходу) | РепутацияВБаре : -1; ВыходНаУлицу"

Data "[Побег]"
Data "Вы поняли, что ничего хорошего вам такое поведение не сулит, поэтому взяли ноги в руки и пустились бегом из бара."
Data "Охрана бара, пробежав некоторое расстояние, решила не париться и вернулась обратно."
Data "#Дальше | РепутацияВБаре = -10; ВыходИзгорода"

Data "[Конец]", ""