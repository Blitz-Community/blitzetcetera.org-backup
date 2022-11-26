; Glass FX by norc of CSP
Graphics3D 640,480,16,2
SetBuffer BackBuffer()

; -----------------create a scene---------------------
Global camera=CreateCamera()
MoveEntity camera,0,5,-50
li=CreateLight()
RotateEntity li,45,45,45
ter=CreateCube()
ScaleEntity ter,50,50,50
tertex=CreateTexture(128,128)
Color 0,255,0
SetBuffer TextureBuffer(tertex)
For j=0 To 15 
 For i=0 To 15 
  Rect i*16,0,5,128
  Rect 0,j*16,128,5
 Next
Next
Color 255,0,0
Text 0,64,"Hello world",0,1
SetBuffer BackBuffer()
FlipMesh ter
EntityTexture ter,tertex

For i=0 To 15
 cube=CreateCube()
 If Rand(100)>50
  PositionEntity cube,49,Rand(-49,49),Rand(-49,49)
 Else
  PositionEntity cube,Rand(-49,49),Rand(-49,49),49
 EndIf
 EntityColor cube,Rand(255),Rand(255),Rand(255)
 ScaleEntity cube,Rnd(2,10),Rnd(2,10),Rnd(2,10)
 RotateEntity cube,Rand(360),Rand(360),Rand(360),1
 EntityParent cube,ter
Next
TurnEntity ter,0,45,0
; ----------------eo scene----------------



Global glass_o=CreateSphere(12)
Global glass_i=CreateSphere(12)
ScaleEntity glass_o,15,15,15
ScaleEntity glass_i,13.5,13.5,13.5
FlipMesh glass_o
;EntityFX glass_i,1
;EntityFX glass_o,1


Global texs=256 ; glass texture size
Global tex2=CreateTexture(texs,texs,9) ; use Bit 256 here - something's wrong with my machine...
EntityTexture glass_i,tex2
EntityTexture glass_o,tex2 
;probably add enviroment mapping on the outter hull (using texture index 1 and FX 16 or as a seperate Mesh)
EntityParent glass_o,glass_i

;-------------
While KeyDown(1)=0
 a#=a#+2 Mod 360
 PositionEntity glass_i,Cos(a#)*15,Sin(a#)*15,0,1
 mapglass()
 RenderWorld()
 Flip
Wend
End
; -----------

Function mapglass()
 oro#=EntityRoll(camera,1)
 oya#=EntityYaw(camera,1)
 opi#=EntityPitch(camera,1)
 ox#=EntityX(camera,1)
 oy#=EntityY(camera,1)
 oz#=EntityZ(camera,1)
 CameraZoom camera,1.1 ; check it out
 PointEntity camera,glass_i
 PositionEntity camera,EntityX(glass_i,1),EntityY(glass_i,1),EntityZ(glass_i,1),1
 CameraViewport camera,0,0,texs,texs
 HideEntity glass_i
 HideEntity glass_o
 RenderWorld()
 CopyRect 0,0,texs,texs,0,0,BackBuffer(),TextureBuffer(tex2)
 ShowEntity glass_i
 ShowEntity glass_o
 CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
 PositionEntity camera,ox,oy,oz,1
 RotateEntity camera,opi,oya,oro,1
 CameraZoom camera,1.0
End Function
; Glass FX by norc modified by filax
Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Type Effect_Glass
	Field Camera
	Field EntityIn
	Field EntityOut
	Field Scale#
	Field Border#
	Field Glass
	Field Reflect
End Type

Function Proc_CreateGlassSphere(Camera,Scale#,Border#,ReflectMap$)
	F.Effect_Glass=New Effect_Glass
	F\Camera=Camera
	F\EntityIn=CreateSphere(30)
	F\EntityOut=CreateSphere(20)
	F\Scale#=Scale#
	F\Border#=Border#

	F\Glass=CreateTexture(128,128)
	F\Reflect=LoadTexture(ReflectMap$,64)
	
	ScaleEntity F\EntityIn,F\Scale#-F\Border#,F\Scale#-F\Border#,F\Scale#-F\Border#
	ScaleEntity F\EntityOut,F\Scale#,F\Scale#,F\Scale#

	EntityTexture F\EntityIn,F\Glass,0,0
	EntityTexture F\EntityOut,F\Glass,0,0

	EntityTexture F\EntityIn,F\Reflect,0,1
	EntityTexture F\EntityOut,F\Reflect,0,1

	TextureBlend F\Reflect,3

	FlipMesh F\EntityOut

	EntityShininess F\EntityIn,1
	EntityShininess F\EntityOut,1

	EntityParent F\EntityOut,F\EntityIn
	
	Return F\EntityIn
End Function

Function Proc_UpdateGlassSphere()
	For F.Effect_Glass=Each Effect_Glass
		Obj_Roll#=EntityRoll(F\Camera,1)
		Obj_Yaw#=EntityYaw(F\Camera,1)
 		Obj_Pitch#=EntityPitch(F\Camera,1)
 		Obj_Px#=EntityX(F\Camera,1)
 		Obj_Py#=EntityY(F\Camera,1)
 		Obj_Pz#=EntityZ(F\Camera,1)

		CameraZoom F\Camera,1.3 ; check it out
 		PointEntity F\Camera,F\EntityIn
 		PositionEntity F\Camera,EntityX(F\EntityIn,1),EntityY(F\EntityIn,1),EntityZ(F\EntityIn,1),1
 
 		CameraViewport camera,0,0,TextureWidth(F\Glass),TextureHeight(F\Glass)
 		HideEntity F\EntityIn
 		HideEntity F\EntityOut
		
 		RenderWorld()

		CopyRect 0,0,TextureWidth(F\Glass),TextureHeight(F\Glass),0,0,BackBuffer(),TextureBuffer(F\Glass)

		ShowEntity F\EntityIn
 		ShowEntity F\EntityOut
 		CameraViewport F\Camera,0,0,GraphicsWidth(),GraphicsHeight()
 		PositionEntity F\Camera,Obj_Px,Obj_Py,Obj_Pz,1
 		RotateEntity F\Camera,Obj_Pitch,Obj_Yaw,Obj_Roll,1
 		CameraZoom F\Camera,1.0
	Next 
End Function 
