Graphics 800, 600 
SetBuffer BackBuffer() 
HidePointer() 
Const width=180, height=120, begin_x=300, begin_y=100 
;Create images 
Global img1=CreateImage(width, height);�����������, ������� ����� ����������� 
Global img2=CreateImage(width, height);����������� img1 ����������� � 2 ����(���������� ����� ��������) 
Global result=CreateImage(40, 40);��������� �������������� 
;Global form=CreateImage(40, 40);� ��� ����� ������(��� ��� ������ ����� ����) 
Dim matrix(ImageWidth(result), ImageHeight(result));������� ��� �������� ����� red+green+blue ������ �������� ������� 
                                                                        ;(���� ����� ������ 4, �� � ��� ����� ��������� ������ �� img2) 
ClsColor 200, 200, 200 
Cls 
;�������� ��������� ���� : (begin_x, begin_y) - ���������� �������� ������ ���� ���� 
                                    ;width- ������ ����, height - ������ 
Color 0, 0, 0 
SetFont(LoadFont("arial", 0, 0, 0)) 
s$="Blitz3D rulezz!!!" 
For k=0 To 3 
      s="  "+s 
      Text 300, 100+k*16, s 
Next 
Text 300, 180, "Made by Funny Compilers" 
GrabImage img1, begin_x, begin_y;���������� ��������� ���� � img1 
GrabImage img2, begin_x, begin_y;���������� ��������� ���� � img2 
ScaleImage img2, 2, 2;���������� img2 � 2 ���� 
;�������� �������, ���� (red+green+blue)<4, �� �� ���� ����� ����� ����������� ���������� 
      Color 1, 0, 0 
      Oval 0, 0, 25, 30 
      Oval 20, 10, 20, 15 
;���������� ����� ������ ������� ������� ������� 
For k=0 To ImageWidth(result) 
      For j=0 To ImageHeight(result) 
            GetColor k, j 
            matrix(k, j)=ColorRed()+ColorGreen()+ColorBlue() 
      Next 
Next 
;CopyRect 0, 0, ImageWidth(form), ImageHeight(form), 0, 0, BackBuffer(), ImageBuffer(form);��� ���� ����� ������ 
While Not KeyHit(1) 
      Cls 
      DrawImage img1, begin_x, begin_y;���������� ��������� ���� 
      Color 0, 0, 0 
;      Text 0, 0, "Form of magnifying glass : " 
;      DrawImage form, 200, 0;��� ���� ����� ������ 
      drawcursor();���������� ���� 
      Flip() 
Wend 
End 
 
Function DrawCursor() 
      x=MouseX():y=MouseY() 
      If x<begin_x+ImageWidth(result)/2 x=begin_x+ImageWidth(result)/2;����������� ����������� ������� � �������� ��������� ���� 
      If x>begin_x-ImageWidth(result)/2+width x=begin_x-ImageWidth(result)/2+width; --:-- 
      If y<begin_y+ImageHeight(result)/2 y=begin_y+ImageHeight(result)/2; --:-- 
      If y>begin_y-ImageHeight(result)/2+height  y=begin_y-ImageHeight(result)/2+height; --:-- 
      MoveMouse x, y 
      x=x-begin_x:y=y-begin_y;��� �������� ������������ �������� ���� ��������� ���� 
      ;������ ����������� ���������� � result 
      CopyRect x*2-ImageWidth(result)/2, y*2-ImageHeight(result)/2, ImageWidth(result), ImageHeight(result), 0, 0, ImageBuffer(img2), ImageBuffer(result) 
      x=x-ImageWidth(result)/2:y=y-ImageHeight(result)/2 
      ;���� - ���� ����� ������ �������� ������� < 4, ����� ������� �� result �� �����, �����-������ 
      LockBuffer(ImageBuffer(result)) 
      LockBuffer(BackBuffer()) 
      For k=x+1 To x+ImageWidth(result)-1 
            For j=y+1 To y+ImageHeight(result)-1 
                  If matrix((k-x), (j-y))<=3 CopyPixelFast k-x, j-y, ImageBuffer(result), k+begin_x, j+begin_y 
            Next 
      Next 
      UnlockBuffer(ImageBuffer(result)) 
      UnlockBuffer(BackBuffer()) 
End Function