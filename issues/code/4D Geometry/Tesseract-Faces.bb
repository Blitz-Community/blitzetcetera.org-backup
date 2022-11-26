Graphics3D 640,480,32
p=CreatePivot()
PositionEntity CreateCamera(p),0,0,-5
RotateEntity CreateLight(),45,45,0

;���������� ������� ������. ��� ����, ���� ����� ������ ������� ����������� �
; �������� ����������, �� ������ ��� ����� ���������� ���������������
; ����������: ���� ��� ��������, �� ���������� ����� -1, ����� - +1
Dim v#(15,4)
For n1=0 To 15
 For n2=0 To 3
  v#(n1,n2)=Sgn(n1 And (1 Shl n2))*2-1
 Next
 ;�������� �����, ������������ �������
 v(n1,4)=CreateSphere(10)
 ScaleEntity v(n1,4),.2,.2,.2
Next

;���������� ������� ���������� �������� (������������ �������� ��� ����)
Dim r(5,1)
For n1=0 To 2
 For n2=n1+1 To 3
  r(n,0)=n1
  r(n,1)=n2
  n=n+1
 Next
Next

;������ ������� ��������� - �����
Dim e(15,3)
;��������������� ������
Dim d#(2)

;��������������� ����������
ang#=1
sina#=Sin(ang#)
cosa#=Cos(ang#)
Repeat

 ;������� ���� (� ������-������) ������ ���������� ��� ������� ������ 1-6
 For n3=0 To 5
  If KeyDown(n3+2) Then
   n1=r(n3,0)
   n2=r(n3,1)
   For n=0 To 15
    c1#=v(n,n1)*cosa#-v(n,n2)*sina#
    c2#=v(n,n1)*sina#+v(n,n2)*cosa#
    v(n,n1)=c1#
    v(n,n2)=c2#
   Next
  End If
 Next

 ;�������� �����. ���� �� ���� ��������
 For n1=0 To 15
  For n=0 To 3
   ;����������� ������� ������� ����� ��������� ������ ���� (�� ���� ���������
   ; ����� �� ���������). ��� ���� ���������� �������� ����������� �����: �����
   ; ������ ���������� ������ ���� ������ ������.
   n2=n1 Or (1 Shl n)
   If n1<>n2 Then
    ;�������� ��������-����� (���� ��� �� �������)
    If e(n1,n)=0 Then e(n1,n)=CreateCylinder(8,False)
    a=e(n1,n)
    ;���������� ������ ����� � ��������� �������� � �����
    For n3=0 To 2
     d#(n3)=.5*(v(n1,n3)+v(n2,n3))
    Next
    PositionEntity a,d#(0),d#(1),d#(2)
    ;���������� ������� ����� � ������������ �������� �� ����
    dd#=0
    For n3=0 To 2
     d#(n3)=v(n1,n3)-v(n2,n3)
     dd#=dd#+d#(n3)*d#(n3)
    Next      
    AlignToVector a,d#(0),d#(1),d#(2),2
    ;��������������� �������� �� ����� �����
    ScaleEntity a,.1,.5*Sqr(dd#),.1
   End If
  Next
 Next

 For n3=0 To 2
  If KeyDown(n3+8) Then   
   TurnEntity p,ang#*(n3=0),ang#*(n3=1),ang#*(n3=2)
  End If
 Next

 ;��������� ����
 For n=0 To 15
  PositionEntity v(n,4),v(n,0),v(n,1),v(n,2)
 Next

 RenderWorld
 Flip
Until KeyHit(1)