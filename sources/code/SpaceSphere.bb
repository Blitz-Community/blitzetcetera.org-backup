;Spacesphere
Graphics3D 640,480,0,2
camera=CreateCamera()
light=CreateLight()
RotateEntity light,45,45,0

;Create the Texture
width=1024
sptex = CreateTexture(width,width,1+8)
SetBuffer TextureBuffer(sptex)
For a = 1 To 200
	Plot Rand(0,width-1),Rand(0,width-1)
Next
SetBuffer BackBuffer()
TextureBlend sptex,5

;Create the Sphere
spbox = CreateSphere(5)
ScaleEntity spbox,1000,1000,1000
EntityTexture spbox,sptex
ScaleTexture sptex,.25,.5
EntityFX spbox,1
FlipMesh spbox
EntityOrder spbox,99999

ball=CreateSphere(5)

While Not KeyHit(1)
 RenderWorld

 sincos#=sincos#+0.2
 PositionEntity ball,50.0*Sin(sincos#),25.0*Cos(sincos#),200.0*Cos(sincos#)

 If KeyDown(200) Then TurnEntity camera,-0.25,0,0 ;up key
 If KeyDown(208) Then TurnEntity camera,0.25,0,0 ;down key
 If KeyDown(203) Then TurnEntity camera,0,0.25,0 ;left key
 If KeyDown(205) Then TurnEntity camera,0,-0.25,0 ;right key
 If KeyHit(17) Then wf=Not wf : WireFrame wf ;W key
 Text 0,0,"Arrow keys=move camera, W=wireframe"

 Flip
Wend
