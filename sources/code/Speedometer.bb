BACK=CreateImage(64,64)
SetBuffer ImageBuffer(BACK)
Color 255,255,255
Oval 0,0,64,64

Color 0,0,255
For i=0 To 359 Step 15
	Line 32,32,32+Cos(i)*30,32+Sin(i)*30
Next

Color 255,255,255
Oval 32-20,32-20,40,40

Color 255,0,0
Oval 32-2,32-2,4,4
SaveBuffer(ImageBuffer(BACK),"back.bmp")
FreeImage BACK
;=====================================
BACK=CreateImage(64,64)
SetBuffer ImageBuffer(BACK)
Rect 31,2,3,30
SaveBuffer(ImageBuffer(BACK),"back2.bmp")
FreeImage BACK
;=====================================
Graphics3D 800,600,32,2
SetBuffer BackBuffer()
camera=CreateCamera()
light=CreateLight()

SP0=LoadSprite("back.bmp",4,camera)
SP1=LoadSprite("back2.bmp",4,SP0)

EntityOrder SP0,0
EntityFX SP0,1

EntityOrder SP1,-1
EntityFX SP1,1

PositionEntity SP0,-4,2.7,5
;=
cube=CreateCube()
PositionEntity cube,-3,2,6.5
SetBuffer BackBuffer()

While Not KeyHit(1)
	RotateSprite SP1,-MilliSecs()*0.01
	RenderWorld()
	Flip
Wend
End