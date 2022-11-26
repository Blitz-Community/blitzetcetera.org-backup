;(c) Agnislav Lipcan
;    redactor cart
;avelnet@yandex.ru
Graphics3D 1024,768,32,1
SetBuffer BackBuffer()

Global tim=LoadAnimImage("MapEditor-Tiles.png",10,10,1,30)
Global ukazka=LoadAnimImage("MapEditor-Cursors.png",40,40,0,8)
HandleImage ukazka,2,5

Dim ArrayGrid(65,65)

While e<64
    e=e+1
    r=0
    While r<64
        r=r+1
        ArrayGrid(e,r)=0
    Wend
Wend 

c=CreateCamera()

i=0
j=0

;main cikles

While Not KeyHit(1)=True


UpdateWorld()
RenderWorld()

ff()
ff2()

DrawImage ukazka,MouseX(),MouseY(),1
Plot MouseX(),MouseY()
If MouseHit(1) Then
   If MouseX() < 785 Then 
      ArrayGrid(MouseX()/12-1,MouseY()/11-1)=numer-1
   Else 
      numer=(MouseX()/17-47)+(10*(MouseY()/17-7))
      If numer<1 Then numer=1
      If numer>30 Then numer=30
   EndIf  
EndIf 

Text 0,0,numer
Flip 
Wend 

fileout = WriteFile("cartdata.dat")
For  n=1 To 64
    For  m=1 To 64
        stek=ArrayGrid(m,n)+1
        WriteLine(fileout, stek ) 
    Next
    WriteLine(fileout, 1 )
Next

CloseFile( fileout)

End 

;functions

Function ff()
i=0
j=0
While i<64
x=x+12 
i=i+1
y=0
While j<64
    y=y+11
     j=j+1
     DrawImage tim,10+x,12+y,ArrayGrid(i,j)
Wend
j=0
Wend
End Function 

Function ff2()
i=0
j=0
k=0
While i<3;<= кол-во рядов
y=y+17
i=i+1
x=0
While j<10
    x=x+17
     j=j+1
     DrawImage tim, 799+x,102+y, k
    k=k+1
Wend
j=0
Wend
End Function 