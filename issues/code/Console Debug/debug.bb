Graphics3D 800,600,0,2


SetBuffer BackBuffer()

Include "bah.bb"

Global X#=0
Global Y#=0
Global Z#=0
Global Col=300

cam=CreateCamera()
MoveEntity cam,0,0,-20
lit=CreateLight()

While Not KeyHit(1)

If KeyHit(57) Then create_bah(X,Y,Z,Col)

update_bah()

If KeyHit(88) Then Consol()

UpdateWorld
RenderWorld
Flip

Wend
End

Function consol()

Print "X"

Print "Y"
Print "Z"
Print "Col"

comand$=Input("Enter comand > ")
If comand$="X" Then X=Input("New X = ")
If comand$="Y" Then Y=Input("New Y = ")
If comand$="Z" Then Z=Input("New Z = ")
If comand$="Col" Then Col=Input("New Col = ")

End Function