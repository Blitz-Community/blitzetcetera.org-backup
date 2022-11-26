width=640
height=480
depth=16
Graphics width,height,depth 

font=LoadImageFont("Arial.ttf",1)
SetImageFont font

While Not KeyDown(KEY_ESCAPE)

 DrawText "Hello World",0,0

 Flip
 Cls

Wend 