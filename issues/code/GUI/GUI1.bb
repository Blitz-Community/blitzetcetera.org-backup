;(c) Agnislav Lipcan
;e-mail: avelnet[@]yandex.ru

;���������� = 1
Global game=1

; ������
Repeat 
Select game
Case 1
; ������ ���� ����
  Cls();<-������� �����
  Graphics3D 1024,768,32,1;<-�������������� �������
  SetBuffer BackBuffer()

  ekran=LoadImage("GUI-Screen.png");<-��������� ��������

  Repeat;<-������ �������� �����

      If KeyHit(1) game=2

      UpdateWorld
      RenderWorld

      DrawImage ekran,0,0

      Flip

   Until game=2;<-����� �������� �����

Case 2
; ����� ����
   Cls();<-������� �����
   Graphics 1024,768,32,1;<-�������������� �������
   font=LoadFont("Arial Cyr",24)
   SetFont font
   FlushKeys();<-������� ���������� (������� ������� �������)
   Text 430,300,"����� ����� ����"
   WaitKey()
   End ;<-�����, �������
End Select
Forever 