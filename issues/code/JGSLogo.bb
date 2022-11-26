<code bb>
Graphics 640,640,32,2

tex = CreateImage(640,640) 

SetBuffer ImageBuffer(tex)

Color 87,96,251
For b# = 0 To 360 Step 10
For a# = 0 To 360 Step 0.2
Plot 320+Cos(b)*Cos(a)*60-Sin(b)*Sin(a)*300,320 + Sin(b)*Cos(a)*60+Cos(b)*Sin(a)*300
Next
Next

DebugLog "generate base image ok"

Flip
SetBuffer BackBuffer()

SetBuffer ImageBuffer(tex)

Dim buf(ImageWidth(tex),ImageHeight(tex),3)

LockBuffer ImageBuffer(tex)
For x=0 To ImageWidth(tex)-1
For y=0 To ImageHeight(tex)-1
ki = ReadPixelFast (x,y,ImageBuffer(tex))
buf(x,y,0)=ki Shr 16 Shl 24 Shr 24
buf(x,y,1)=ki Shr 8 Shl 24 Shr 24
buf(x,y,2)=ki Shl 24 Shr 24
Next
Next
UnlockBuffer ImageBuffer(tex)

DebugLog "get color value ok"

For z = 0 To 3
For x=1 To ImageWidth(tex)-1
For y=1 To ImageHeight(tex)-1

;обычный блюр
r#=(buf(x,y,0)+buf(x-1,y,0)+buf(x+1,y,0)+buf(x,y-1,0)+ buf(x-1,y-1,0)+buf(x+1,y-1,0)+buf(x,y+1,0)+buf(x-1,y+1,0)+buf(x+1,y+1,0))/9
g#=(buf(x,y,1)+buf(x-1,y,1)+buf(x+1,y,1)+buf(x,y-1,1)+ buf(x-1,y-1,1)+buf(x+1,y-1,1)+buf(x,y+1,1)+buf(x-1,y+1,1)+buf(x+1,y+1,1))/9
b#=(buf(x,y,2)+buf(x-1,y,2)+buf(x+1,y,2)+buf(x,y-1,2)+ buf(x-1,y-1,2)+buf(x+1,y-1,2)+buf(x,y+1,2)+buf(x-1,y+1,2)+buf(x+1,y+1,2))/9
;а весь секрет вот в этом числе  bloom effect при blur
l# = 1.35
r = r*l
g = g*l
b = b*l

If r>255 Then r = 255
If g>255 Then g = 255
If b>255 Then b = 255

buf(x,y,0) = r
buf(x,y,1) = g
buf(x,y,2) = b

Next
Next
DebugLog "blur "+z
Next

LockBuffer ImageBuffer(tex)
For x=0 To ImageWidth(tex)-1
For y=0 To ImageHeight(tex)-1
WritePixelFast x,y,buf(x,y,0) Shl 16+buf(x,y,1) Shl 8+buf(x,y,2)
Next
Next
UnlockBuffer ImageBuffer(tex)
DebugLog "write image pixels ok"

SetBuffer BackBuffer()
Cls
DrawImage tex,0,0
Flip
WaitKey
End
</code><noinclude>[[Категория:Код]]</noinclude>