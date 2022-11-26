
Const maxdbf=100
Global dbfile,fleng,stpos,rowq,colq,dbbank
Dim dbc(maxdbf,1)

CopyFile "storage.dbf","storage2.dbf"
opendb "storage2.dbf"
For n=0 To rowq-1
 readrow n
 v=readdbf(1)
 writedbf 1,.88*v
 v=readdbf(1)
 writedbf 2,2*v
 writerow n
Next
closedb

opendb "storage2.dbf"
For n=0 To rowq-1
 readrow n
 Print trimdbf$(0)+": "+trimdbf$(1)+"$, "+trimdbf$(2)+"pcs."
Next
closedb

WaitKey

Function opendb(file$)
dbfile=OpenFile(file$)
SeekFile dbfile,8
stpos=ReadShort(dbfile)
fleng=ReadShort(dbfile)
rowq=(FileSize(file$)-stpos)/fleng

;��������������� ����� ��� �������� ������
dbbank=CreateBank(fleng)

dbc(0,0)=1
colq=0
Repeat
 fp=32*(colq+1)
 SeekFile dbfile,fp
 If ReadByte(dbfile)=13 Then Exit 
 SeekFile dbfile,fp+16
 dbc(colq,1)=ReadShort(dbfile)
 If colq>0 Then dbc(colq,0)=dbc(colq-1,0)+dbc(colq-1,1)
 colq=colq+1
Forever
End Function

Function closedb()
CloseFile dbfile
FreeBank dbbank
FreeBank dbbankc
End Function

;������� ������ ������ �� ����� � �����
Function readrow(row)
;����������� ������ ������ � �����
SeekFile dbfile,stpos+fleng*row
;������ ������ �� ����� � ����� ������
ReadBytes dbbank,dbfile,0,fleng
End Function

;������� ������ ������ �� ������ ������ � ����
Function writerow(row)
;����������� ������ ������ � �����
SeekFile dbfile,stpos+fleng*row
;������ ������ �� ������ ������ � ����
WriteBytes dbbank,dbfile,0,fleng
End Function

;������� ���������� ������ �� ������ ������ ������
Function readdbf$(col)
;������������ ���������� ���������� � ������� ������
For n=0 To dbc(col,1)-1
 m$=m$+Chr$(PeekByte(dbbank,dbc(col,0)+n))
Next
Return m$
End Function

;������� ������ ������ � ������ ������ ������
Function writedbf(col,m$)
;���������� ������ �� ��������� ���������� � �����
l=Len(m$)
For n=0 To dbc(col,1)-1
 If n<l Then v=Asc(Mid$(m$,n+1,1)) Else v=32
 PokeByte dbbank,n+dbc(col,0),v
Next
End Function

;������� �������� ������ � �����
Function trimdbf$(col)
Return Trim$(readdbf$(col))
End Function