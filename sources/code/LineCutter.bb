Data "'� ���� (������) ���� ������� ��� ���������, ��������� �� ���������� ����� ##. � �� "
Data "������, ��� ��� ��������� ���� ���������� ������� �� ����, � ��������� �� ���� "
Data "��������� ��� ��� ��������, ��� ��������� �� ������������� �� ������ �� ������ "
Data "������.' ���� ��� ������ ����� ����� �� ������ 1, �� �� �����, ��� �� ���� �������. � "
Data "������� ������ ����������� �������������� ������ �� ���� � ���������. "
Data "����������� �������� ����: '�� ���-�� � ��� ������ ������ �������������, ������ ��� "
Data "������.' ��������������� �� �����. �� ����� � �������� ��������� ����� ������� "
Data "��������� ������ � ������� �������������� ��� �������� �� �����-���� ������ "
Data "��������. ��� ���� ���� �� � ��� ���������, � �������� ������ ������� ����� ������, "
Data "������� ������-�������� ������."
;===========================
Graphics 800,600,32,2
SetBuffer BackBuffer()
font=LoadFont("Arial cyr",20)
SetFont font
;������������ ������
Local InputString$=""
temp$=""
For i=1 To 10
Read temp
InputString$=InputString$+temp
Next
DebugLog InputString$
;============================
Local SEPARATOR$=" ";<= �����������
Local MAXLENPIX%=400+FontWidth();<= ����� �������
LNINSTR%=Len(InputString$)
Dim MAP$(LNINSTR%);<= �������
Local ST%=1
Local LINDX%=1
Local CINDX%=1
Local MapPtr%=0;<=������ ���������� ��������

While True
CINDX=Instr(InputString$,SEPARATOR$,LINDX)
If CINDX=0
CINDX=LNINSTR
temp=Mid(InputString$,ST,CINDX-ST-1)
If StringWidth(temp)>MAXLENPIX
MAP(MapPtr)=Mid(InputString$,ST,LINDX-ST-1)
MapPtr=MapPtr+1
ST=LINDX
EndIf
MAP(MapPtr)=Mid(InputString$,ST,CINDX-ST)
MapPtr=MapPtr+1
Exit
EndIf
temp=Mid(InputString$,ST,CINDX-ST-1)
If StringWidth(temp)>MAXLENPIX
MAP(MapPtr)=Mid(InputString$,ST,LINDX-ST-1)
MapPtr=MapPtr+1
ST=LINDX
EndIf
LINDX=CINDX+1
Wend
;======================
;�����
Line MAXLENPIX,0,MAXLENPIX,GraphicsHeight()
For i=0 To MapPtr
Text 0,i*FontHeight(),MAP(i)
Next
Flip
WaitKey()
End