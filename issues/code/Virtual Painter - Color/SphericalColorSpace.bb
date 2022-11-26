Graphics 640,480,32

i=LoadImage("Painter1.jpg")
DrawBlock i,0,0

;���������� ����
r1=224
g1=224
b1=0
;���� ������
r2=64
g2=64
b2=255
;������
rad#=128

For y=0 To 479
 For x=0 To 639
  ;���������� ����� �� ������������
  p=ReadPixel(x,y)
  b=p And 255
  g=(p Shr 8) And 255
  r=(p Shr 16) And 255
  ;���������� ����� �������� ������ � ����������
  d#=Sqr((r-r1)*(r-r1)+(g-g1)*(g-g1)+(b-b1)*(b-b1))
  ;�������� ���������� ����� ������ �����
  If d#<=rad# Then
   ;���������� �������������
   d1#=d#/rad#
   d2#=1-d1#
   ;�������� �������������� ������������
   r=Int(d1#*r+d2#*r2)
   g=Int(d1#*g+d2#*g2)
   b=Int(d1#*b+d2#*b2)
  End If
  ;����������� �������
  WritePixel x,y,b+(g Shl 8)+(r Shl 16)
 Next
Next

WaitKey