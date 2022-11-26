Graphics3D 640,480,32
PositionEntity CreateCamera(),0,0,-5
RotateEntity CreateLight(),45,45,0
p=CreatePivot()
;�������� ������ � �����������
m=CreateMesh(p)
s=CreateSurface(m)
;������� ������: ���������� "��������������" ������ (16) + ��������� (1)
EntityFX m,17

;�������� �������� ��� ������
tex=CreateTexture(8,8,14)
SetBuffer TextureBuffer(tex)
Rect 0,0,8,8,0
;��������� ��������� �������� (�����=0)
For x=1 To 6
 For y=1 To 6
  WritePixel x,y,0
 Next
Next
EntityTexture m,tex

;������ ��� ��������� ������ ������
Dim v#(99,4)

Dim r(5,1)
;�������� ���� ���������
For n1=0 To 2
 For n2=n1+1 To 3
  ;��� ����� ������������ ��� ����������� ���������� ��������
  r(n,0)=n1
  r(n,1)=n2
  n=n+1
  ;����������� ������� ���� ���������� ���������
  n3=(n1*n2=0)+(n1+n2=1)
  n4=6-n1-n2-n3
  ;����� ��� �������� ���� ��������� �������� ���� �����������
  For sn1=-1 To 1 Step 2
   For sn2=-1 To 1 Step 2
    ;����� ��� ������ ������
    For sn3=-1 To 1 Step 2
     For sn4=-1 To 1 Step 2
      v#(nn,n1)=sn1
      v#(nn,n2)=sn2
      v#(nn,n3)=sn3
      v#(nn,n4)=sn4
      AddVertex s,0,0,0,sn3=1,sn4=1
      nn=nn+1
     Next
    Next
    ;���������� ����� � ������
    AddTriangle s,nn-4,nn-3,nn-2
    AddTriangle s,nn-1,nn-3,nn-2
   Next
  Next  
 Next
Next

ang#=1
sina#=Sin(ang#)
cosa#=Cos(ang#)
Repeat

 ;������� ���� (� ������-������ ������) ������ ���������� ��� ������� ������ 1-6
 For n3=0 To 5
  If KeyDown(n3+2) Then
   n1=r(n3,0)
   n2=r(n3,1)
   For n=0 To nn-1
    c1#=v(n,n1)*cosa#-v(n,n2)*sina#
    c2#=v(n,n1)*sina#+v(n,n2)*cosa#
    v(n,n1)=c1#
    v(n,n2)=c2#
   Next
  End If
 Next

 ;������� ���������� �������� ��� ������� ������ 7-9
 For n3=0 To 2
  If KeyDown(n3+8) Then
   TurnEntity p,ang#*(n3=0),ang#*(n3=1),ang#*(n3=2)
  End If
 Next

 ;��������� ��������� ������ ������
 For n=0 To nn-1
  VertexCoords s,n,v(n,0),v(n,1),v(n,2)
 Next

 RenderWorld
 Flip
Until KeyHit(1)