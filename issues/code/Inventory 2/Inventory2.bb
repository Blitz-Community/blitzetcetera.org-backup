Global DataDir$=SystemProperty$("appdir")+"Data\"
DataDir$=".\Data\"

Global AplicationWidth,AplicationHeight,KolCell,SizeCellW,SizeCellH,KolCol,KolRow,InventarPozX,InventarPozY,MouseID

AplicationWidth=800 ;������ ������
AplicationHeight=600 ;������ ������

Graphics3D AplicationWidth,AplicationHeight, 32,2
SetBuffer BackBuffer()

MouseID=0  ;�������� ����������
MouseStatus=0 ;���������� ��� �������� ������� ����, ������ ��� ����������� ���� �� ������ ������ ����...
MouseImage=LoadImage(DataDir$+"Inventory2-Mouse.png") ;�������� ������� ����...
MaskImage MouseImage,0,0,0 ;����� ��� ����...

Type TCell ; ��� ������ ��� ���������
 Field X ;�������� "�" ������
 Field Y ;�������� "�" ������
 Field Status; ����� �������� ID �������, ���� ��� ��� - �������� = 0
End Type

KolCol=3               ;���������� ��������
KolRow=3               ;���������� �����
KolCell=KolCol*KolRow  ;���������� �����

SizeCellW=80           ;������ ������
SizeCellH=80           ;������ ������

InventarPozX=AplicationWidth-(KolCol*SizeCellW) ;��������� ������ �������� �������� ���������, ���������� "X"
InventarPozY=0                                  ;��������� ������ �������� �������� ���������, ���������� "Y"

Dim Inventar.TCell(KolCell-1)

Type TObject    ; ��� ��� �������� ��������� ���������
            Field ID    ;  ������������� ��������
            Field Image ;  ���������� ��� �������� ��������
  Field Name$ ;  �������� ��������
  Field Description$ ; �������� �������
  Field Damage       ; ����������� ��������� �������� (������ � ������)
  Field Guard        ; ������ (������ � ����� � � �����)
End Type

;------------------------ ����----1-99-----------------------
Sword.TObject=New TObject
Sword\Damage=10
Sword\ID=1
Sword\Image=LoadImage(DataDir$+"Inventory2-Item1.png")
MaskImage Sword\Image,255,0,255
Sword\Name$="���"
Sword\Description$="��� ��� ������� ����������� ��������. � �����, ������ ������� �� �� �����..."

Sword.TObject=New TObject
Sword\Damage=15
Sword\ID=2
Sword\Image=LoadImage(DataDir$+"Inventory2-Item2.png")
MaskImage Sword\Image,255,0,255
Sword\Name$="���-2"
Sword\Description$="���-2 ��� ������� ������� ��������."
;------------------------ ����--100-199-------------------------
Shield.TObject=New TObject
Shield\Guard=3
Shield\ID=100
Shield\Image=LoadImage(DataDir$+"Inventory2-Item3.png")
MaskImage Shield\Image,255,0,255
Shield\Name$="��� ������"
Shield\Description$="��� ����������� ������ �� �������, �� ������� ����� ��� ���� ������ ���..."

Shield.TObject=New TObject
Shield\Guard=5
Shield\ID=101
Shield\Image=LoadImage(DataDir$+"Inventory2-Item4.png")
MaskImage Shield\Image,255,0,255
Shield\Name$="��� �������"
Shield\Description$="���, ��������� �� ��������� �����, ���� �� ������ �������� ������ �� ����..."

FntArial=LoadFont("Arial",14)
SetFont FntArial

InitInventar()

While Not KeyHit(1) ;����� �� ��������� ���� ������ Esc
SetBuffer BackBuffer()
Cls

    DrawInventar() ;������� ��������� ���������
    If KeyHit(2) Then AddElementToInventar(1,1) ;���� ������ �� ������ 1 �� � ��������� ��������� ���
    If KeyHit(3) Then AddElementToInventar(2,2) ;���� ������ �� ������ 2 �� � ��������� ��������� 2-�� ���
    If KeyHit(4) Then AddElementToInventar(1,100) ;���� ������ �� ������ 3 �� � ��������� ��������� ��� ������
    If KeyHit(5) Then AddElementToInventar(2,101) ;���� ������ �� ������ 4 �� � ��������� ��������� ��� �������

 If MouseDown(2) Then GetInfoInventar() ; �� ������� ������ ������ ���� �� �������, � ��������� ��������� ���������� �� ���� ��������

 If MouseDown(1)  Then MoveElementToInventar():MouseStatus=1 ; �� ������� ����� - ����������� ��������
 If MouseStatus=2 Then AddElementToInventar(MouseDownInventar(),MouseID):MouseStatus=0 ;���� ��������� ���� � ���������, �� �� ����������� � ���������
 If MouseStatus<>0 Then MouseStatus=2 ; ���������� ��� ����, ����� ���������� ������� ��������� ������ ���� ���

 DrawImage MouseImage,MouseX(),MouseY() ;������ ������ ����

 Flip;
Wend

End

Function InitInventar();������� ���������
K=0;
 For I=0 To KolRow-1
 For J=0 To KolCol-1
   Inventar.TCell(K)= New TCell
   Inventar.TCell(K)\X=InventarPozX+((SizeCellW-1)*J)
   Inventar.TCell(K)\Y=InventarPozY+((SizeCellH-1)*I)
   Inventar.TCell(K)\Status=0
   K=K+1
 Next
 Next
End Function

Function DrawInventar();������� ��������� ���������
 For I=0 To KolCell-1
   Rect(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,SizeCellW,SizeCellH,0); ������ ������� ��� ��������� (����� ����� ������� �������� �� ��������)
   DrawElementInventar(Inventar.TCell(I)\Status,I);������� ��������� ��������� ���������
 Next
End Function

Function DrawElementInventar(ID,Element);������� ��������� ��������� ���������
  If ID<>0 Then ;���� ������ �� ������, ��
    Temp.TObject=FindObjectInInventar(ID) ;������� ���������� ������ �� �������, ������� ��������� � ������
    DrawImage Temp\image,Inventar.TCell(Element)\X,Inventar.TCell(Element)\Y ;������ ������� �� ���������
  EndIf
End Function

Function FindObjectInInventar.TObject(ID) ;������� ���������� ������ �� �������, ������� ��������� � ������.
If (ID>=1) And (ID<=99) Then ;���� ID (������������� �������) ����� � �������� �� 1 �� 99 �� ��� ���
  Sword.TObject = First TObject ; ������������� ������ ����� �� ������ ���
   For Sword.TObject= Each TObject ;���������� ��� ���� � ������� �������
       Temp=Sword\ID ;����������� ���������� Temp ID ������������� �������
       If Temp =ID Then Return Sword: Exit ; �������: ���� ID (Temp) = ID �������� ��������, �� ���������� � ��� ������ � ������� �� �����
    Next
  EndIf
  If (ID>=100) And (ID<=199) Then ;���� ID (������������� �������) ����� � �������� �� 100 �� 199, �� ��� ���
  Shield.TObject = First TObject ;������������� ������ ����� �� ������ ���
   For Shield.TObject= Each TObject;���������� ��� ���� � ������� �������
       Temp=Shield\ID  ;����������� ���������� Temp ID ������������� �������
       If Temp =ID Then Return Shield: Exit ;�������, ���� ID (Temp) = ID �������� ��������, �� ���������� � ��� ������ � ������� �� �����
    Next
  EndIf
  ; ����� �������� � ��� �������� ��������, ���� � ��� ������ ���������...
End Function

Function AddElementToInventar(Poz,ID); ������� ��������� ������� � ���������, Poz-� ����� ������, ID-����� �������
    TempEmptyCell=FindEmptyCellInInventar(); ����� ��������� ������
  If Poz=-1   Then Poz=TempEmptyCell; ���� ������� �� �������� ��� ������ ���������, �� ������� ������� � ������ ��������� ������
    Temp=Inventar.TCell(Poz)\Status ;����� ������������ ������������� (ID) �������� � ������
    If TempEmptyCell>=0 Then ; �������, ���� �� ������ ������, ���� ����, �� ���� ������
      If Temp<>0 Then ; ���� ������ �� ������, ��
        Inventar.TCell(TempEmptyCell)\Status=ID ; ����������� ������� ��������� ������ ������
        MouseID=0 ; ���������, ��� ������� ������ � ������ � ���� ������
      EndIf
      If Temp=0 Then  ; ���� ������ ������,
        Inventar.TCell(Poz)\Status=ID ;�� ����������� ������� ������� ������
        MouseID=0 ; ���������, ��� ������� ������ � ������ � ���� ������
      EndIf
    Else Text 10,10,"��������� ����������"
    EndIf
End Function

Function FindEmptyCellInInventar();���� ������ ��������� ������ � ���������
  For I=0 To KolCell-1
    If Inventar.TCell(I)\Status=0 Then  Return I:Exit ;���������� ��� ������ � ������� ������, ���� �����, �� ���������� � ����� � �������...
  Next
   Return -1 ;���� ��� ������ ������ ��, ���������� -1
End Function

Function GetInfoInventar(); �� ������� ������ ������ ���� �� �������, � ��������� ��������� ���������� �� ���� ��������
  For I=0 To KolCell-1
    ID=Inventar.TCell(I)\Status ; ���������� ��� ������ � ������� ID �������� ������
    If (ID<>0) And (MouseOverlap(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,Inventar.TCell(I)\X+SizeCellW,Inventar.TCell(I)\Y+SizeCellH)=True) Then  ; �������: ���� ������ �� ������ � ���� ��������� ��� ���� �������, ��
     TempDan.TObject=FindObjectInInventar(ID)         ; ���� ������ �� �������
     Text 0,10, TempDan\Name$    ; ������� �������� �������
     Text 0,30, TempDan\Description$ ;�������� �������
     If TempDan\ID<=99 Then Text 0,50,"����:"+TempDan\Damage ;���� ��� ������, ������ ������� �������� �����
     If TempDan\ID>=100 And TempDan\ID<=199 Then Text 0,50,"������:"+TempDan\Guard   ;���� ��� ���, ������ ������� �������� ������
    EndIf
  Next
End Function

Function MouseOverlap(X,Y,X1,Y1) ; ������� ���������� TRUE, ���� ����� �������� � ������� � False, ���� �� ��������
  If (MouseX()>X) And (MouseX()<X1) And  (MouseY()>Y) And (MouseY()<Y1) Then Return True Else  Return False
End Function

Function MoveElementToInventar();����������� ��������
  Poz=MouseDownInventar(); �������, ��� ����� ������� (� ����� ������)
  If (Poz>=0) Or (MouseID<>0)Then ;���������, ����� ������� ������ �� ��������� ��� ���� ���� �� ������
    If MouseID=0 Then   ID=Inventar.TCell(Poz)\Status ; ���� ���� ������, �� ����� ID �������� � ������� ������
    If  ID<>0 Then MouseID=ID: DeleteElementToInventar(Poz) ; ���� ������� ���� � ������, �� ���� ����� ��� � ������ ���������
    If (MouseID<>0) Then     ; ���� ���� �� ������, ��
      Temp.TObject=FindObjectInInventar(MouseID) ; ���� ������ ������� �� ID, �������, � ��� � ����
      DrawImage Temp\image,MouseX()-SizeCellW/2,MouseY()-SizeCellH/2 ; ������ �������� ������� ������ � �����...
    EndIf
  EndIf
End Function

Function DeleteElementToInventar(Poz); ��������  �������� �� ���������
   Temp=Inventar.TCell(Poz)\Status ; ��������� � ���������� �������� ������
   If Temp<>0 Then Inventar.TCell(Poz)\Status=0; �������, ���� ������ �� ������, �� ������ �� ������
End Function

Function MouseDownInventar(); ������� � ����� ������ ��������� ����
  For I=0 To KolCell-1
    If (MouseOverlap(Inventar.TCell(I)\X,Inventar.TCell(I)\Y,Inventar.TCell(I)\X+SizeCellW,Inventar.TCell(I)\Y+SizeCellH)=True) Then; ������� ��� ��������� ���� � ���� ��� �������� � ������ ���������, ���������� ��� ����� � �������
     Return I:Exit
    EndIf
  Next
  Return -1 ; ���� ���� �� �������� �� � ���� ������
End Function