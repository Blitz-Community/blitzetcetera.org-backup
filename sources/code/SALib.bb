;created by impersonalis (b00lean team)
;impersonalis@boolean.name
;
;http://community.boolean.name/index.php
;http://boolean.name/index.php

;��������� ������������ �������� ���������������� ��������, � ������������ ���������� ��
;� �������, ������� � ����� � �.�.

;��������:

;���������:

;Const SA_MAX_DIM_CONST=5
;������������ ����������� �������. �������� 5, ����������������: ����� �������� ������
;� ���������� ����������� =5, �.�. SA_CREATE(s1+","+s2+","+s3+","+s4+","+s5,"I")

;Const SA_Divider$=","
;����-����������� - ������������ ������ ����������� ��������� ���������-�����������,
;���������� ���� �� �����, ���������� ����� ������ ��������� �������.

;�������:

;Function SA_Create(index$,t$)
;������� ������������ ������ (������� � index$ ����� ����-�����������)
;���� t$
;t$ - ������������� �������� � ������� ����������, ����� ��������� ��������:
;"B" - ����
;"S" - ���������
;"I" - ����� (4 �����)
;"F" - � ��������� ������
;
;���������� ����� �������
;
;temp.SpecialArray=SA_CREATE(10+","+15,"I")
;������� ������ 10 �� 15 ��� �������� ����� �����

;Function SA_OUT$(S.SpecialArray,index$)
;��������� �������� ������� � ������� S
;�� ������, ��������� � index$
;��������: ���������� ������������� ��������������� �������� ������ � ������� ����
;�� ��������� ������������ ������

;Function SA_IN$(S.SpecialArray,index$,inputZ$)
;�������� �������� inputZ$ � ������ � ������� S
;� ������, ��������� � index$

;Function SA_Delete(S.SpecialArray)
;����������
;������� ������ ���������� ������������ �������� � ������� S

Const SA_MAX_DIM_CONST=5
Const SA_Divider$=","

Graphics 800,600
SetFont LoadFont("Arial",20)

Function OUTPUTA(S.SpecialArray,ss,c)
	For is=1 To ss
		For ic=1 To c
			Text is*15,ic*15,SA_OUT(S,is+","+ic)
		Next
	Next
End Function

Function INPUTA(S.SpecialArray,ss,c)
	For is=1 To ss
		For ic=1 To c
			SA_IN(S,is+","+ic,Rand(0,9))
		Next
	Next
End Function

sC=2
cC=3
temp.SpecialArray=SA_CREATE(sC+","+cC,"I")
InputA(temp,sC,cC)

OUTPUTA(temp,sC,cC)

Flip
SA_DELETE(temp)

WaitKey()
End

Type SpecialArray
	Field PrivateArray%
	Field PrivateINIT_STR%[SA_MAX_DIM_CONST%]
	Field PrivateMN%
	Field PrivateType%
End Type

Function SA_Create.SpecialArray(index$,t$)
	S.SpecialArray=New SpecialArray
	Select(t$)
		Case "B" S\PrivateMN=1:S\PrivateType%=1
		Case "S" S\PrivateMN=2:S\PrivateType%=2
		Case "I" S\PrivateMN=4:S\PrivateType%=3
		Case "F" S\PrivateMN=4:S\PrivateType%=4
	End Select

	Local Re%=1
	Local c=1
	For i=1 To Len(index$)
		ip=Instr(index$,SA_Divider$,i)
		If ip=0 ip=Len(index$)+1
		S\PrivateINIT_STR[c]=Int(Mid$(index$,i,ip-i))
		RE=RE*S\PrivateINIT_STR[c]
		i=ip
		c=c+1
	Next
	S\PrivateArray=CreateBank(Re%*S\PrivateMN)
	Return S
End Function

Function SA_Private_ReturnIndex(S.SpecialArray,index$)
	Local c=1
	Local ptr=0
	For i=1 To Len(index$)
		ip=Instr(index$,SA_Divider$,i)
		If ip=0 
			ip=Len(index$)+1
			ptr=ptr+Int(Mid$(index$,i,ip-i))-1
			Exit
		EndIf
		ptr=ptr+(S\PrivateINIT_STR[c]-1)*(Int(Mid$(index$,i,ip-i))-1)
		i=ip
		c=c+1
	Next
	ptr=ptr*S\PrivateMN
	Return ptr
End Function

Function SA_OUT$(S.SpecialArray,index$)
	Local ptr=SA_Private_ReturnIndex(S,index$)
	Local ReturnC$
	Select(S\PrivateType%)
		Case 1 ReturnC$=PeekByte(S\PrivateArray,ptr)
		Case 2 ReturnC$=PeekShort(S\PrivateArray,ptr)
		Case 3 ReturnC$=PeekInt(S\PrivateArray,ptr)
		Case 4 ReturnC$=PeekFloat(S\PrivateArray,ptr)
	End Select
	Return ReturnC$
End Function

Function SA_IN$(S.SpecialArray,index$,inputZ$)
	Local ptr=SA_Private_ReturnIndex(S,index$)
	Select(S\PrivateType%)
		Case 1 PokeByte(S\PrivateArray,ptr,Left(inputZ$,1))
		Case 2 PokeShort(S\PrivateArray,ptr,Int(inputZ$))
		Case 3 PokeInt(S\PrivateArray,ptr,Int(inputZ$))
		Case 4 PokeFloat(S\PrivateArray,ptr,Float(inputZ$))
	End Select
End Function

Function SA_Delete(S.SpecialArray)
	FreeBank S\PrivateArray
	Delete S
End Function