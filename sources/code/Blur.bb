Global ark_blur_image, ark_blur_texture, ark_sw, ark_sh

Function CreateBlurImage()
	;Create blur camera
	Local cam = CreateCamera()
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, 10000

	ark_sw = GraphicsWidth()
	ark_sh = GraphicsHeight()
	
	
	;Create sprite
	Local spr = CreateMesh(cam)
	Local sf = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 1024.0 / Float(ark_sw), 1024.0 / Float(ark_sw), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100000
	EntityBlend spr, 1
	ark_blur_image = spr
	
	;Create blur texture
	ark_blur_texture = CreateTexture(1024, 1024, 256)
	EntityTexture spr, ark_blur_texture
End Function

Function UpdateBlur(power#)
	EntityAlpha ark_blur_image, power#
	CopyRect  ark_sw / 2 - 512, ark_sh / 2 - 512, 1024, 1024, 0, 0, BackBuffer(), TextureBuffer(ark_blur_texture)	
End Function

;-------------------------------------------------------------------------------
; EXAMPLE
;-------------------------------------------------------------------------------

Graphics3D 800, 600, 32;, 1
SetBuffer BackBuffer()

SetFont LoadFont("Arial", 18, 1, 0, 0)

SeedRnd MilliSecs()

PV = CreatePivot()
cam = CreateCamera(PV)
PositionEntity cam, 0, 0, -7

cube = CreateCube()

;------------ CREATE BLUR IMAGE --------------
CreateBlurImage()
;---------------------------------------------

blur_power# = 0.5
lh = CreateLight(1, cam)

cb = CreateCube()
PositionEntity cb, 0, 0, 30
ScaleEntity cb, 20, 20, 1

tx = CreateTexture(64, 64)
SetBuffer TextureBuffer(tx)
ClsColor 0, 0, 50
Cls
Color 155, 155, 0
Rect 0, 0, 63, 63, 0
Rect 1, 1, 61, 61, 0

shtx = CreateTexture(64, 64)
SetBuffer TextureBuffer(shtx)
ClsColor 255, 255, 255
Cls
Color 55, 55, 0
Rect 0, 0, 63, 63, 0
Rect 1, 1, 61, 61, 0

SetBuffer BackBuffer()

EntityTexture cb, tx
ScaleTexture tx, .1, .1
ScaleTexture shtx, .5, .5

Dim sh(30), rx#(30), ry#(30), rz#(30)
For i = 1 To 30
	sh(i) = CreatePivot()
	ss = CreateSphere(8, sh(i))
	EntityTexture ss, shtx
	EntityColor ss, Rnd(255), Rnd(255), Rnd(255)
	PositionEntity ss, Rnd(2, 3), Rnd(2, 3), Rnd(2, 3)
	sc# = Rnd(0.3, 1)
	ScaleEntity ss, sc, sc, sc
	RotateEntity sh(i), Rnd(0, 360), Rnd(0, 360), Rnd(0, 360)
	rx#(i) = Rnd(-5, 5)
	ry#(i) = Rnd(-5, 5)
	rz#(i) = Rnd(-5, 5)
Next

angle# = 0

dt# = 0.01
fr = 0
fps = 0
frTime = MilliSecs()

While Not KeyHit(1)
	time = MilliSecs()
	
	TurnEntity cube, Sin(MilliSecs() / 9.987) * 3.35 * dt#, Sin(MilliSecs() / 12.345) * -4.78 * dt#, Sin(MilliSecs() / 18.90999) * 4.8765 * dt#
	angle = angle# + dt#
	RotateEntity cb, Sin(angle * 5.87) * 10.0, Sin(angle * 3.33) * 15, angle
	
	For i = 1 To 30
		TurnEntity sh(i), rx(i) * dt, rx(i) * dt, rx(i) * dt
	Next
	
	If KeyDown(74)
		blur_power# = blur_power# - dt# * .01
		If blur_power# < 0.5 Then blur_power# = 0.5
	ElseIf KeyDown(78)
		blur_power# = blur_power# + dt# * .01
		If blur_power# > 0.9 Then blur_power# = 0.9
	EndIf

;------------------------------------- UPDATE BLUR -------------------------------------------	
	UpdateBlur(blur_power# + (1.0 / (dt# * 120.0)))
;---------------------------------------------------------------------------------------------
	RenderWorld

	fr = fr + 1
	If MilliSecs() - frTime >= 1000
		fps = fr
		fr = 0
		frTime = MilliSecs()
	EndIf
	
	Color 255, 255, 255
	
	Text 5, 5, "FPS : " + fps
	Text 5, 20, "BLUR : " + Int(blur_power# * 100.0) / 100.0 + "  ('+' and '-')"
	
	Flip 1
	
	to_dt# = (MilliSecs() - time) / 60.0
	dt# = dt# + (to_dt# - dt#) * .1
Wend

End