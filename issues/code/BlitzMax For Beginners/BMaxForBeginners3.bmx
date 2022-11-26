width=640
height=480
depth=16
Graphics width,height,depth

AutoMidHandle True

logo=LoadImage("BMax-Logo.png")

xmove=1
ymove=1

SetAlpha 0.1
SetBlend LIGHTBLEND

While Not KeyDown(KEY_ESCAPE)

 ang=ang+1
 SetRotation ang

 xpos=xpos+xmove
 ypos=ypos+ymove

 If xpos>width-(ImageWidth(logo)/2) Then xmove=-1
 If xpos<0+(ImageWidth(logo)/2) Then xmove=1
 If ypos>height-(ImageHeight(logo)/2) Then ymove=-1
 If ypos<0+(ImageWidth(logo)/2) Then ymove=1

 DrawImage logo,xpos,ypos

 Flip
 'Cls

Wend
