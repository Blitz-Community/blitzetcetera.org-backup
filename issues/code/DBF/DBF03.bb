Type baserow
 Field name$
 Field price#
 Field quantity
End Type

Const maxdbf=100
Global dbfile,fleng,stpos,rowq,colq,dbbank,dbbankc
Dim dbc(maxdbf,1)

opendb "storage.dbf"
For n=0 To rowq-1
 readrow n
 r.baserow=New baserow
 r\name$=readdbf(0)
 r\price#=readdbf(1)
 r\quantity=readdbf(2)
 For r2.baserow=Each baserow
  If r2\name$>r\name$ Then Insert r Before r2:Exit
 Next
Next
closedb

createdb "storage2.dbf"
addfield "NAME",51
addfield "PRICE($)",9
addfield "QUANTITY",9
addfield "TOTAL",9
closeheader

For r=Each baserow
 clearbnk
 writedbf 0,r\name$
 writedbf 1,r\price#
 writedbf 2,r\quantity
 writedbf 3,r\quantity*r\price
 addrow
Next
closedb

opendb "storage2.dbf"
For n=0 To rowq-1
 readrow n
 Print trimdbf$(0)+": "+trimdbf$(2)+"pcs."+trimdbf$(1)+"$, total:"+trimdbf$(3)+"$"
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
;�������� � ������������� ���� ��� ������� ������
dbbankc=CreateBank(fleng)
For n=0 To fleng-1
 PokeByte dbbankc,n,32
Next

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
;������ ���������� ����� ����� ��������� ����
SeekFile dbfile,4
WriteInt dbfile,rowq
CloseFile dbfile
FreeBank dbbank
FreeBank dbbankc
End Function

Function readrow(row)
SeekFile dbfile,stpos+fleng*row
ReadBytes dbbank,dbfile,0,fleng
End Function

Function writerow(row)
SeekFile dbfile,stpos+fleng*row
WriteBytes dbbank,dbfile,0,fleng
End Function

Function readdbf$(col)
For n=0 To dbc(col,1)-1
 m$=m$+Chr$(PeekByte(dbbank,dbc(col,0)+n))
Next
Return m$
End Function

Function writedbf(col,m$)
l=Len(m$)
For n=0 To dbc(col,1)-1
 If n<l Then v=Asc(Mid$(m$,n+1,1)) Else v=32
 PokeByte dbbank,n+dbc(col,0),v
Next
End Function

Function trimdbf$(col)
Return Trim$(readdbf$(col))
End Function

;������� �������� ����� ���� ������
Function createdb(file$)
dbfile=WriteFile(file$)
;����� ������ DBase - 4, ���� - ������ ������ 2000� (����� �����)
WriteInt dbfile,3+1 Shl 8+1 Shl 16
;��������� ����� ��������� ���� �������
For n=1 To 7
 WriteInt dbfile,0
Next
stpos=32
fleng=1
rowq=0
End Function

;���������� ������ �������
Function addfield(name$,l)
;������ ����� ����
For n=1 To 11
 WriteByte dbfile,Asc(Mid$(name$,n,1))
Next
;��� ����-���������� (C)
WriteByte dbfile,67
;������ ������� ���� � ������
WriteInt dbfile,fleng
f=(stpos Shr 5)-1
dbc(f,0)=fleng
;������ ����� ����
WriteShort dbfile,l
dbc(f,1)=l
;��������� ����������� ������
For n=1 To 7
 WriteShort dbfile,0
Next
fleng=fleng+l
stpos=stpos+32
End Function

;�������, ����������� ���������
Function closeheader()
;������ ����� ���������
WriteByte dbfile,13
SeekFile dbfile,8
;������ ������� ������ ������
stpos=stpos+1
WriteShort dbfile,stpos
;������ ����� ������ � �������
WriteShort dbfile,fleng

;���������� ������
dbbank=CreateBank(fleng)
dbbankc=CreateBank(fleng)
For n=0 To fleng-1
 PokeByte dbbankc,n,32
Next
End Function

;�������, ����������� ������ � ���� ������
Function addrow()
SeekFile dbfile,stpos+fleng*rowq
WriteBytes dbbank,dbfile,0,fleng
rowq=rowq+1
End Function

;������� ������� ������ ������
Function clearbnk()
CopyBank dbbankc,0,dbbank,0,fleng
End Function