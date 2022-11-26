Graphics 640,480
SetFont LoadFont("Arial",16)

;������������ ���������� ��������
Const maxdbf=100
;��������� ����������
Global dbfile,fleng,stpos,rowq,colq
;������ ��� ���������� � ��������
Dim dbc(maxdbf,1)

;�������� ���� ������
opendb "storage.dbf"
;����� ���� ������ �� �����
For n=0 To rowq-1
 Print trimdbf$(n,0)+": "+trimdbf$(n,1)+"$, "+trimdbf$(n,2)+"pcs."
Next
;�������� ���� ������
closedb

WaitKey

;������� �������� ����� ���� ������
Function opendb(file$)
;��� ���������� ����� ������� ������������� �����
dbfile=OpenFile(file$)
SeekFile dbfile,8
;������ ����� ������
stpos=ReadShort(dbfile)
;����� ������ � ������� (����� ������)
fleng=ReadShort(dbfile)
;������ ���������� �����
rowq=(FileSize(file$)-stpos)/fleng

;������� ���������� ��������
colq=0
;������� ������ ��������� - 1
dbc(0,0)=1
;���� �� ���� ���������� �����
Repeat
 ;����� ������ ���������
 fp=32*(colq+1)
 ;�����, ���� �������� ������ � ����� 13
 SeekFile dbfile,fp
 If ReadByte(dbfile)=13 Then Exit 
 ;���������� ����� ���� (� ��������)
 SeekFile dbfile,fp+16
 dbc(colq,1)=ReadShort(dbfile)
 ;������ ������� ����
 If colq>0 Then dbc(colq,0)=dbc(colq-1,0)+dbc(colq-1,1)
 colq=colq+1
Forever
End Function

;������� �������� ����� ���� ������
Function closedb()
CloseFile dbfile
End Function

;������� ���������� ������ �� ������
Function dbf$(row,col)
;����������� ������ ������ � �����
SeekFile dbfile,stpos+fleng*row+dbc(col,0)
;������������ ���������� ���������� � ������� ������
For n=1 To dbc(col,1)
 m$=m$+Chr$(ReadByte(dbfile))
Next
Return m$
End Function

;�������, ��������� � ������ ������� ����� � ������
Function trimdbf$(row,col)
Return Trim$(dbf$(row,col))
End Function