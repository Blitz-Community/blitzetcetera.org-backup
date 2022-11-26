;----------------------------------------------------------------------------------------------
; Title : Function to move smoothly the camera with the mouse
; Authors : Philippe C 
; Version : V0.1
;
; This library provides functionalities to move the camera in a smooth way with the mouse.
; Whatever the speed of the mouse the rotation of the camera are always smooth.
; The effect in a game seems better.
;
;----------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------
;Version 0.1
; intial version
;----------------------------------------------------------------------------------------------

; type created in order to manage the camera mouvement in a smooth way
Type objet3dCamera
Field current_value_x#
Field current_value_y#
End Type
objet3dCamera.objet3dCamera = New objet3dCamera


Function objet3dCameraWalk(camera,clickL%,clickR%,XSpeed%,YSpeed%,Zspeed%,move=1,speed#=0.3,smooth#=6.0)
;--------------------------------------------------------------------------------------------------------------------
; The rotation of the camera are smooth
;--------------------------------------------------------------------------------------------------------------------

If speed <= 0 Then RuntimeError "objet3dCameraWalk, error program , the speed should be greater than 0 => " + speed
If smooth < 1 Then RuntimeError "objet3dCameraWalk, error program , the smooth should be greater than 1 => " + smooth

If camera <= 0 Then RuntimeError "objet3dCameraWalk, error program , the camera doesn't exist"
ycamera# = EntityY(camera)

oC.objet3dCamera = First objet3dCamera
If oC.objet3dCamera = Null Then RuntimeError "objet3dCameraWalk, error program , the type objet3dCamera doesn't set correctly"


; move forward and backward
If clickL Then
MoveEntity camera,0,0,move
EndIf
If clickR Then
MoveEntity camera,0,0,-move
EndIf

; move y camera
If Zspeed <> 0
MoveEntity camera,0,Zspeed,0
ycamera = EntityY(camera)
EndIf

; reatblish the height of the camera because of the moveentity which folow the camera angle
PositionEntity camera,EntityX(camera),ycamera,EntityZ(camera)

; the value increase smoothly 
oC\current_value_x = GetCurveValue(oC\current_value_x,YSpeed*speed,smooth)
oC\current_value_y = GetCurveValue(oC\current_value_y,-XSpeed*speed,smooth)

TurnEntity camera,oC\current_value_x,oC\current_value_y,0

; restablish the z angle because of the move entity 
RotateEntity camera,EntityPitch(camera),EntityYaw(camera),0

; center the mouse in the middle to avoid to get the cursor outside the window
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

End Function


Function objet3dCameraWalkInit()
;--------------------------------------------------------------------------------------------------------------------
; 
;--------------------------------------------------------------------------------------------------------------------

oC.objet3dCamera = First objet3dCamera
If oC.objet3dCamera = Null Then RuntimeError "objet3dCameraWalkInit, error program , the type objet3dCamera doesn't exist"

oC\current_value_x = 0.0
oC\current_value_y = 0.0

End Function



Function getCurveValue#(current#,destination#,amount#)
;--------------------------------------------------------------------------------------------------------------------
; the value is increased smoothly
;--------------------------------------------------------------------------------------------------------------------

If amount <= 0 Then RuntimeError "getCurveValue, error program , the amount should be greater than 0 => " + amount 


If Abs(destination - current ) < 1 Then Return destination

current=current+((destination-current)/amount)

If current<0.01 And current>-0.01 Then current=0

Return current

End Function



Function test()
;--------------------------------------------------------------------------------------------------------------------
; to test the program
;--------------------------------------------------------------------------------------------------------------------

Graphics3D 800,600

; cerate a simplify landscape
texture = CreateTexture(256,256)
SetBuffer TextureBuffer( texture ) 
ClsColor 0,255,0
Cls
For k = 0 To 120 Step 20
For j = 0 To 120 Step 20
cube = CreateCube()
ScaleEntity cube,2,2,2
brush = CreateBrush(Rnd(255),Rnd(255),Rnd(255))
PositionEntity cube,k,2,j
PaintEntity cube,brush
Next
Next
SetBuffer(BackBuffer())
terrain = CreateTerrain(128)
EntityTexture terrain,texture
camera=CreateCamera() 
PositionEntity camera,30,2,80
Color 255,255,255

; mangement of the mouse
HidePointer
newcam = False
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

; init of the camera parameters
objet3dCameraWalkInit()
move% = 1 ; move the camera 
speed# = 0.3 ; decrease the speed of the mouse 
smooth# = 6.0 ; smooth move of the camera


While Not KeyDown( 1 )

; management of keys
; + -
If KeyDown( 78 ) Then speed = speed + 0.01 : DebugLog "speed = " + speed
If KeyDown( 74 ) Then speed = speed - 0.01 : DebugLog "speed = " + speed
If speed <= 0 Then speed = 0.01 : DebugLog "speed = " + speed

; pg up down
If KeyDown(201) Then smooth = smooth + 0.1 : DebugLog "smooth = " + smooth
If KeyDown(209) Then smooth = smooth - 0.1 : DebugLog "smooth = " + smooth
If smooth <= 1 Then smooth = 1 : DebugLog "smooth = " + smooth
; space bar
key = KeyHit(57) 
If ( key = 1 And newcam = False) Then 
newcam = True 
Else
If key = 1 Then 
newcam = False
EndIf
EndIf
key = 0
While KeyHit(57)
Wend

If newcam 
; new function to manage the camera rotation
objet3dCameraWalk(camera,MouseDown(1),MouseDown(2),MouseXSpeed(),MouseYSpeed(),MouseZSpeed(),move,speed,smooth)
Else
; standard management of the camera 
RotateEntity camera, EntityPitch(camera) + MouseYSpeed(), EntityYaw(camera) - MouseXSpeed(), EntityRoll(camera)
EndIf

RenderWorld
UpdateWorld 

; display information
i = -20
i = i + 20 : Text 0,i,"move the mouse."
i = i + 20 : Text 0,i,"change camera , hit space bar"
If newcam
i = i + 20 : Text 0,i,"mouse speed , use key numeric pad : + & - =>" + speed
i = i + 20 : Text 0,i,"mouse smooth , use key pgup and pgdn =>" + smooth
i = i + 20 : Text 0,i,"mouse left click => move forward"
i = i + 20 : Text 0,i,"mouse right click => move backward"
EndIf

Flip

Wend


End Function



; to put in comment if you want to use as a library
test()