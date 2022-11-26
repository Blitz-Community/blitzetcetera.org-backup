;������������� ����� � ����������� ��� �����
Const screenWidth = 800, screenHeight = 600
Graphics screenWidth, screenHeight, 16, 2
centerX = screenWidth / 2
centerY = screenHeight / 2

;������ ������ ����� � ����� ��� ����������� �������
Const carpetSize = 600			;������ ������ �����
Const threshold = 4				;������ ����� ��������

;������ ����� ������� ���������� ������, �������� � �����
offset = carpetSize / 2
Rect centerX - offset, centerY - offset, carpetSize, carpetSize

;������ ���� ��������� ������ � ���������� � ��������...
Color 0, 0, 0
carpet(centerX, centerY, carpetSize, threshold)

;Ѩ, ������! ����� ������� �� ����� ������ ����� ����������� 
WaitKey
End


 
;����������� �������
Function carpet(x, y, size, threshold)
	newSize = size / 3		;1/3 ����������� �������
	offset = newSize / 2		;����� �������� �������

	;������ ������ ������� � ������(��� � ���� "��������", �����������)
	Rect x - offset, y - offset, newSize, newSize

	If threshold > 0 Then
		newThreshold = threshold - 1	;��������� ����� - �� ������ ��� ���!
		
		;����� ������� ����� 8 ��� ��� ������� ���������� ������ ������������ 
		carpet(x - newSize, y - newSize, newSize, newThreshold)
		carpet(x, y - newSize, newSize, newThreshold)
		carpet(x + newSize, y - newSize, newSize, newThreshold)
		carpet(x + newSize, y, newSize, newThreshold)
		carpet(x + newSize, y + newSize, newSize, newThreshold)
		carpet(x, y + newSize, newSize, newThreshold)
		carpet(x - newSize, y + newSize, newSize, newThreshold)
		carpet(x - newSize, y, newSize, newThreshold)
	End If
End Function