width=640
height=480
depth=16
Graphics width,height,depth

AutoMidHandle True

logo=LoadImage("BMax-Logo.png")

While Not KeyDown(KEY_ESCAPE)

ang=ang+1
SetRotation ang
DrawImage logo,width/2,height/2

Flip
Cls

Wend 