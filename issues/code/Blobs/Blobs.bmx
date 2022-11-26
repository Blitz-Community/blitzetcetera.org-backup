'�������-����� � BlitzMax, ��������� ������ Max2D 

'������� ���� � ��� ��������
Local ballsize:Int=512
Local ballsizehalf:Int=ballsize/2 

'������������� ����������� �����
Graphics 800,600,0

Cls 

'��������� �������� ��� ������ ��������� ����
Local balldivider:Float
If ballsize=128 Then balldivider=64 '8x8
If ballsize=256 Then balldivider=256 '16x16
If ballsize=512 Then balldivider=1024 '32x32
Local lineardivider:Float
If ballsize=128 Then lineardivider=0.5
If ballsize=256 Then lineardivider=1
If ballsize=512 Then lineardivider=2

'��������� �������� � �����
For Local r:Float=1 To ballsize-1 Step 0.5
	Local level:Float=r
	level:*level
	level=level/balldivider
	SetColor level,level,level '������ ��������
	'SetColor r/lineardivider,r/lineardivider,r/lineardivider '��� �������� ����������
	
DrawOval r/2,r/2,ballsize-r,ballsize-r  
Next

'�������� � ��������
AutoMidHandle True
Local img:TImage=CreateImage(ballsize,ballsize,1,FILTEREDIMAGE)
GrabImage(img,0,0,0)


'������������� ����� ���������
SetBlend LIGHTBLEND

'������ ��������, ���� �� ������ �� escape
Repeat
	Cls
	DrawImage img,400,300
	DrawImage img,MouseX(),MouseY()
	Flip
Until KeyHit(KEY_ESCAPE)