<code bb>
;Maxrec - ������������ ���������� �������, reclen - ����� ������ � ������ (�
; ������ ������� ������ �������� ����������: x(int), y(float), ����������:
; dx(Float), dy(Float), ���� �(byte) - 17 ����)
Const maxrec=120000, reclen=17
;������� ����
bnk=CreateBank(maxrec*reclen)
;����� ���������� �������� � �����
addr=-reclen

Graphics 800,600,32

;������ ��� �������� �������� ������ (�������� ������)
Dim col(255)
For n=0 To 255
 col(n)=n*65793
Next

SetBuffer BackBuffer()
Repeat
 ;��������� 400 ��������
 For n=1 To 400
  addr=addr+reclen
  PokeFloat bnk,addr,Rand(800);���������� x
  PokeFloat bnk,addr+4,0;���������� y
  PokeFloat bnk,addr+8,Rnd(-1,1);���������� �� x
  PokeFloat bnk,addr+12,Rnd(2,10);���������� �� y
  PokeByte bnk,addr+16,Rnd(64,255);����� �����
 Next

 Cls
 LockBuffer BackBuffer()

 ;���� �� ���� ��������� � �����
 n=0
 While n<=addr
  ;��������� ���������� � �����������
  x#=PeekFloat(bnk,n)+PeekFloat(bnk,n+8)
  y#=PeekFloat(bnk,n+4)+PeekFloat(bnk,n+12)
  If x<0 Or x>799 Or y#>599 Then
   ;���� �������� ����� �� ���� ������ - ������� �� �� �����
   CopyBank bnk,addr,bnk,n,reclen
   addr=addr-reclen
   ;��� ��� �� �� ����� ����� ���������, �� ����� �� ������������� 
  Else
   ;����� - ���������� �� ������ � ��������� ����������
   WritePixelFast x#,y#,col(PeekByte(bnk,n+12))
   PokeFloat bnk,n,x#
   PokeFloat bnk,n+4,y#
   ;��������� � ��������� ��������
   n=n+reclen
  End If
 Wend

 UnlockBuffer BackBuffer()
 Flip

Until KeyHit(1)
</code><noinclude>[[���������:���]]</noinclude>