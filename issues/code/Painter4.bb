<code bb>
;=============================================================================
;========================== "����������" =====================================
;=============================================================================
Const MaxFilters=10
Global CountFilters=-1
Dim Filter_core# ( MaxFilters, 2, 2 )
Dim Filter_D ( MaxFilters )

Dim Filter_matrixR(0, 0)
Dim Filter_matrixG(0, 0)
Dim Filter_matrixB(0, 0)
;=======================================
Function CreateFilter ( d, c1#, c2#, c3#, c4#, c5#, c6#, c7#, c8#, c9# ) ; ������� ������, ����������� ������������ ���������
      CountFilters = CountFilters+1
      Filter_D ( CountFilters ) = d
      Filter_core ( CountFilters, 0, 0 ) = c1#
      Filter_core ( CountFilters, 0, 1 ) = c2#
      Filter_core ( CountFilters, 0, 2 ) = c3#
      Filter_core ( CountFilters, 1, 0 ) = c4#
      Filter_core ( CountFilters, 1, 1 ) = c5#
      Filter_core ( CountFilters, 1, 2 ) = c6#
      Filter_core ( CountFilters, 2, 0 ) = c7#
      Filter_core ( CountFilters, 2, 1 ) = c8#
      Filter_core ( CountFilters, 2, 2 ) = c9#
      Return CountFilters
End Function

Function ApplyFilter ( img, id ) ; ��������� ������
      width = ImageWidth ( img )
      height = ImageHeight ( img )
      buffer = ImageBuffer ( img )
      PrepareMatrixRGB ( img ) ; ��������� �������� � ������� ������������� ( r, g, b )
      For k=1 To Width-2
            For j=1 To Height-2
                  ; �������������� ���������� ������ �����
                  r = Filter_D ( id )
                  g = Filter_D ( id )
                  b = Filter_D ( id )
                  ; ����� �� ������������� �� ��������� ��� �������� (k-1) � (j-1) ������� �������������� ����������
                  xx=k-1
                  yy=j-1
                  ; ��������������� ��������� ������
                  For x = 0 To 2
                        For y = 0 To 2
                              r = r + Filter_matrixR(xx+x, yy+y)*Filter_core(id, x, y)
                              g = g + Filter_matrixG(xx+x, yy+y)*Filter_core(id, x, y)
                              b = b + Filter_matrixB(xx+x, yy+y)*Filter_core(id, x, y)
                              Next
                  Next
                  ; ������������ ���� �������
                  WritePixelFast k, j, RGB(r, g, b), buffer
            Next
      Next
      UnlockBuffer buffer
      CorrectDefect ( img ) ; ������� "�����"
End Function

Function RGB(r,g,b)
     	If r<0  r=0
      If r>255  r=255
      If g<0  g=0
      If g>255  g=255
      If b<0  b=0
      If b>255  b=255
      Return (r Shl 16) Or (g Shl 8) Or b  Or (255 Shl 24)
End Function

Function GetR(val)
      Return (val Shr 16) And ($000000FF)
End Function

Function GetG(val)
      Return (val Shr 8) And ($000000FF)
End Function

Function GetB(val)
      Return (val And $000000FF)
End Function

Function PrepareMatrixRGB ( img ) ; ��������� �������� � ������� ������������� ( r, g, b )
      width = ImageWidth ( img )
      height = ImageHeight ( img )
      Dim Filter_matrixR( width, height )
      Dim Filter_matrixG( width, height )
      Dim Filter_matrixB( width, height )
      LockBuffer ImageBuffer(img)
      For k=0 To width-1
            For j=0 To Height-1
                  clr=ReadPixelFast(k, j, ImageBuffer(img))
                  Filter_matrixR(k, j)=GetR(clr)
                  Filter_matrixG(k, j)=GetG(clr)
                  Filter_matrixB(k, j)=GetB(clr)
            Next
      Next
End Function

Function CorrectDefect ( img ) ; ������������ ����������� ( ������� "�����" )...
      ; ..."�����" ��������� ���������� ����, ��� �� �� ����������� ������� �������.
      buffer = ImageBuffer ( img )
      LockBuffer ( buffer )
      height = ImageHeight ( img )
      width = ImageWidth ( img )
      For k = 0 To width-1
            WritePixelFast k, 0, ReadPixelFast ( k, 1, buffer ), buffer
            WritePixelFast k, height-1, ReadPixelFast ( k, height-2, buffer ), buffer
      Next
      For k = 0 To height-1
            WritePixelFast 0, k, ReadPixelFast ( 1, k, buffer ), buffer
            WritePixelFast width-1, k, ReadPixelFast ( width-2, k, buffer ), buffer
      Next
      UnlockBuffer ( buffer )
End Function

;==============================================================================
;=================== �������� ����� ===========================================
;==============================================================================
;=========================== �������������
Graphics 640, 480, 16, 2
SetFont LoadFont ( "arial cyr", 30, 1 )

ClsColor 150, 150, 150
Cls

;=========================== �������� �������� � ��������
template = CreateImage ( 300, 40 )
      SetBuffer ImageBuffer ( template )
      ClsColor 50, 50, 50
      Cls
      Color 255, 255, 255
      Text 150, 20, "Filters  Example", 1, 1
SetBuffer BackBuffer ( )

DrawBlock template, 10, 10
Flip ( )

;=========================== �������� ��������
emboss = CreateFilter ( 128, -1, 0, 0, 0, 1, 0, 0, 0, 0 ) ; ������� ������ "��������"
blur = CreateFilter ( 0, 0, .2, 0, .2, .2, .2, 0, .2, 0 ) ; ������� ������ "�����������"
contur = CreateFilter ( 0, 0, 1, 0, 1, -4, 1, 0, 1, 0 ) ; ������� ������ "�������������"

;=========================== ���������� ��������
img1 = CopyImage ( template )
ApplyFilter ( img1, blur ) ; ����������
ApplyFilter ( img1, blur ) ; ����������
ApplyFilter ( img1, emboss ) ; �����������
DrawBlock img1, 10, 60
Flip ( )

img2 = CopyImage ( template )
ApplyFilter ( img2, contur ) ; ������������
ApplyFilter ( img2, blur ) ; ����������
ApplyFilter ( img2, emboss ) ; ����������
DrawBlock img2, 10, 110
Flip ( )

img3 = CopyImage ( template )
ApplyFilter ( img3, emboss ) ; ����������
ApplyFilter ( img3, blur ) ; ����������
ApplyFilter ( img3, blur ) ; ����������
ApplyFilter ( img3, blur ) ; ����������
DrawBlock img3, 10, 160

Text 10, 210, "Press any key..."
Flip ( )

FlushKeys ()
WaitKey ( )
End

</code><noinclude>[[���������:���]]</noinclude>