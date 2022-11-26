;
; ������ 1: ���������, ������������ ���� ������� � �������� �������� �����.
;            

; [ ���������� ]_____________________________________________

Global IMGGrisu  ; ���������� �������� ����� �����������
Global angle#,r# ; ���� � ������, ������������ ��� �������� ������


; [ Loading Data ]__________________________________________
Function LoadData()

	IMGGrisu=LoadImage("DATA\grisu.png") ; ��������� ��������
 	MaskImage IMGGrisu,255,255,255       ; ������� �����

End Function 

; [ Main Loop ]_____________________________________________

Graphics 640,480,16,2  ; ������������� ����������� �����
SetBuffer BackBuffer() ; ������������� �����
ClsColor 33,57,73      ; ���� ����
angle#=0:r#=0          ; �������� ���� � ������

LoadData()             ; ��������� ��� ������

While Not KeyHit(1)    ; ���� �� ������ ������� ESC, �
	Cls

  If IMGGrisu > 0 Then TileImage IMGGrisu,0,0 ; ������ �������� ���� ��� ����            

	angle=(angle+1) Mod 360                     ; ������������ ��������
	r=(r+0.5) Mod 300
	Text GraphicsWidth()/2+Cos(angle)*r,GraphicsHeight()/2-Sin(angle)*r,"Grisu's Tutorial No. 1",1,1 ; ������� ������� ������

	Flip
Wend                  ; ����� ��������� �����
 
End                   ; ����� �� ���������