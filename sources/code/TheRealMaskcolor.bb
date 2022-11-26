; A program to find the maskcolor when in doubt

Graphics 640,480,16,2 ; Change 16 to your bitmode if you use 24 or 32
sprite=LoadImage("spotlight2.png")
DrawImage sprite,0,0
rgb=ReadPixel(0,0); The coords on the sprite where the maskcolor is represented
b=rgb And $ff
g=rgb Shr 8 And $ff
r=rgb Shr 16 And $ff
Text 0,300,"The mask-color is: "+r+","+g+","+b
WaitKey()
End