; ���������: ��� ������� �����������.
; �����: Grisu
; �������: MANIAK_dobrii
; ������: 1.00


; ��� ��� �������� ������������.
;
; 1. ������ ������ ���� ��� � ������� "Configure()" � "Start()".
;
; 2. ����������� ����� � ������� ���������� � ".exe" �� ".scr".
;
; 3. ������ ���� ������(& ����� ���� ����) � �������� ����� Windows(C:\Windows\ ��������)


; ��������� �������� �� ��������, ����������� � blitz-�:
;
; - ���������� �������� ����������� ������ � ���� ������������ ������������ Windows.
;
; - ��� ������� ������, ������� �������� ��������� ���������� ����.


Const SCREENX=800 ; 2 ��������� ��� ��������� ����������
Const SCREENY=600 

; �����
; ����� ����� ��������� ���� ������ ��� Windows!!!.
; ��� ������ ���������� ���������� �� ��, ��� ����� �����������.
; ����������� ��� ������ �� ����� ������, �.�. "appdir" Blitz-� ��� "\bin".
; ����� ���� ������ ������ �������!!! :)

;ChangeDir SystemProperty$("appdir")

; �������, ����� ��������� ��� ���� Windows 

If CommandLine$() <> "" Then ; ���� �������� ���� ��: 
  If Upper(Left$(CommandLine$(),2)) = "/C" Then Configure() ; ���� �������� ���� ���������
  If Upper(Left$(CommandLine$(),2)) = "/S" Then Start() ; ��� ���� �������� ��� �����������? 
EndIf ; <> ""

End ; ����� �����, bye bye(����. ������:)(����. ���.))


Function Configure()

; �������� ���� ��� ���� ���������.
; ���� �����, �� ����� ������� ������� �����. �������� ���������� �� VB, Delphi ��� ������.

 ; ������ ����� ��������, ��� ���� ��������� ����������!

 AppTitle "Screensaver Options" ; ������ ��������� ����
 Graphics 640,480,16,2 ; ������ GFX �����
 SetBuffer BackBuffer()

 Repeat 
  Text 10,10, "��������� Grisu �� �������� �������������" 
  Text 10,20, "������� MANIAK_dobrii"
  Text 10,30, "-----------------------------------------"
  Text 10,50, "��� ������ ������ ����������� ������������." 
  Text 10,70, "��� ��� ��� ������ ���������������!" 
  Text 10,450,"����� ������ ��� �������� �����... :)" 
  Flip
  Delay 1 ; �� ����� ������������ ��� 100% CPU!
 Until GetKey() Or WaitMouse() 

End Function ; ����� Configure()


Function Start()

; �������� ���� ��� ������ ������������.

 AppTitle "Screensaver Tutorial1" ; The Name of your Screensaver
 Graphics SCREENX,SCREENY,16,1 ; set GFX Mode
 SetBuffer BackBuffer() 

 FlushKeys() ; ������ ����� �����
 FlushMouse() ; ������ ����� ���� 
 MoveMouse 0,0 ; ������� ���� � 0,0 ��� ����������� ��������

 Textout$="��������� Grisu �� �������� �������������" ; ���� ����� ����� �������

 Repeat ; ������ �������� ����� 

  Color Rand(255),Rand(255),Rand(255) 
  Rect 0,0,SCREENX,SCREENY,1

  Color 0,0,0 
  For y=0 To SCREENY Step 2 
   Rect 0,y,SCREENX,1 
  Next 

  Rect 50,50,SCREENX-100,SCREENY-100,1 ; ������ ������ ������� � ������

  Color 255,255,255 ; ����� �����
  Text (SCREENX-StringWidth(Textout$))/2,SCREENY/2, Textout$ 

  Flip ; flip-��� �������
  Delay 1 ; �� ����� ������������ ��� 100% CPU!
 Until GetMouse() <> 0 Or MouseX() <> 0 Or MouseY() <> 0 Or GetKey() <> 0 ; ���������, ������� �� ������������ ��������� ������, ��� ���� �� ����� :) 

End Function ; ����� Start() 