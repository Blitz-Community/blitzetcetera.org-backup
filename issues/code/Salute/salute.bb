
Graphics3D 800,600,0,2

SetBuffer BackBuffer()

Include "bah.bb" ; подключаем нашу библиотеку

cam=CreateCamera()
MoveEntity cam,0,0,-20
lit=CreateLight()

While Not KeyHit(1)
If KeyHit(57) Then create_bah(0,0,0,300) ; При нажатии наа
;«Пробел» создать салют

update_bah() ; Просчитать новое положение частиц

UpdateWorld
RenderWorld
Flip

Wend
End