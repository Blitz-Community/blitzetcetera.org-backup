Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer ()
HidePointer


; == Declarations ==


; -- World.
;Global gravity# = 9.8
;^^^^^^

; -- Viewport.
Global viewport_center_x = GraphicsWidth () / 2
Global viewport_center_y = GraphicsHeight () / 2
;^^^^^^


; -- Mouselook.
Global mouselook_x_inc# = 0.4 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.4 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
Global mouse_left_limit = 250 ; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_right_limit = GraphicsWidth () - 250 ; As above.
Global mouse_top_limit = 250 ; As above.
Global mouse_bottom_limit = GraphicsHeight () - 250 ; As above.
;^^^^^^

; -- Mouse smoothing que.
Global mouse_x_speed_1#
Global mouse_x_speed_2#
Global mouse_x_speed_3#
Global mouse_x_speed_4#
Global mouse_x_speed_5#
Global mouse_y_speed_1#
Global mouse_y_speed_2#
Global mouse_y_speed_3#
Global mouse_y_speed_4#
Global mouse_y_speed_5#
;^^^^^^

; -- User.
Global user_movement_speed# = 4.0 ; The user's movement speed. This produces a movement speed of 4 metres per second.
Global user_camera_pitch#
Const USER_CENTERPOINT_HEIGHT# = 0.94
Const USER_CAMERA_HEIGHT# = USER_CENTERPOINT_HEIGHT# * 2.0 - 0.1
;^^^^^^

; -- Timing.
Global milli_secs ; Holds the value of the Millisecs() timer. Set at the start of the main loop.
Global old_time ; This must be set to the current 'MilliSecs()' time at the start of a new game and when returning from a pause.
Global game_time ; This holds a relative game time value which is used with timeouts so that they can be paused.
Global Delta_Time# ; Use this as a multiplier for all continuous game world events, to regulate game speed.
;^^^^^^

; -- Collision.
Const COLLTYPE_geometry = 1
Const COLLTYPE_user = 2
;^^^^^^


; == Setup ==


; -- Create a light;
RotateEntity CreateLight (), 45.0, 45.0, 0.0, True
;^^^^^^

; -- Create user entity.
Global user = CreatePivot()
PositionEntity user, 0.0, USER_CENTERPOINT_HEIGHT#, 0.0, True
EntityRadius user, USER_CENTERPOINT_HEIGHT#
EntityType user, COLLTYPE_user
;^^^^^^

; -- Create user's camera.
Global user_camera = CreateCamera( user )
CameraRange user_camera, 0.5, 1000.0
CameraClsColor user_camera, 0.0, 106.0, 213.0
CameraZoom user_camera, 1.6 ; Set the camera focus to the correct value for the human eye.
PositionEntity user_camera, 0.0, USER_CAMERA_HEIGHT#, 0.0, True
;^^^^^^

; -- Create some meshes to use as a movement reference.
Global sphere = CreateSphere ()
UpdateNormals sphere
PositionEntity sphere, 15.0, 1.0, 0.0, True
EntityType sphere, COLLTYPE_geometry
Global cone = CreateCone ()
UpdateNormals cone
PositionEntity cone, -15.0, 1.0, 0.0, True
EntityType cone, COLLTYPE_geometry
Global cube = CreateCube ()
UpdateNormals cube
PositionEntity cube, 0.0, 1.0, 15.0, True
EntityType cube, COLLTYPE_geometry
Global cylinder = CreateCylinder ()
UpdateNormals cylinder
PositionEntity cylinder, 0.0, 1.0, -15.0, True
EntityType cylinder, COLLTYPE_geometry
	; -- Create grid texture.
	Global grid_2d_tex = CreateTexture ( 256, 256, 11 )
	SetBuffer TextureBuffer ( grid_2d_tex )
	For i = 0 To 4
		Rect i, i, 256 - i - i, 256 - i - i, False
	Next
	For y = 5 To 250
		For x = 5 To 250
			WritePixel x, y, 0
		Next
	Next
	SetBuffer BackBuffer ()
	;^^^^^^
	; -- Create 2D grid.
	Global grid_2D = CreatePlane ()
	EntityType grid_2D, COLLTYPE_geometry
	EntityTexture grid_2D, grid_2d_tex
	EntityFX grid_2D, 9
	;^^^^^^
;^^^^^^

InitializeNewMap()

; == Main Code ==


; -- Initialize the loop.
old_time = MilliSecs ()
Collisions COLLTYPE_user, COLLTYPE_geometry, 2, 2
;^^^^^^

; -- Main loop.
While Not KeyHit ( 1 ) ; Exit the main loop if the escape key is pressed.
	CalculateDeltatimeAndGametime()
	MouseLook()
	MoveUser()
	UpdateWorld
	RenderWorld

	; -- Frames Per Second counter.
	; Place into the main program loop where it will be executed each frame. The variables used here do not need to be declared globlly as this will occur automatically if the routine is placed in the main program. The value of 'milli_secs' should be set from the 'MilliSecs()' Blitz function at the start of the main program loop.
	FPS_framecounter = FPS_framecounter + 1
	If milli_secs >= FPS_timeout
		FPS_timeout = milli_secs + 1000
		FPS_framecount = FPS_framecounter
		FPS_framecounter = 0
	EndIf
	Text 10, 10, "FPS"
	Text 40, 10, FPS_framecount
	;^^^^^^

	Text 10, 30, "Use W, A, S, D, F, C to move and ESCAPE to exit."

	VWait ; Use this along with 'Flip False' so that the user can't disable the wait for vertical blank.
	Flip False

	Delay ( 1 ) ; Lets Windows do whatever housekeeping it needs to. Useful if the user is downloading something at the same time as playing a game, etc. Not sure if it's required for a windowed game.

Wend
;^^^^^^

End ; End the program.


; == Functions ==


Function MouseLook()

	; -- Update the smoothing que to smooth the movement of the mouse.
	mouse_x_speed_5# = mouse_x_speed_4#
	mouse_x_speed_4# = mouse_x_speed_3#
	mouse_x_speed_3# = mouse_x_speed_2#
	mouse_x_speed_2# = mouse_x_speed_1#
	mouse_x_speed_1# = MouseXSpeed ( )
	mouse_y_speed_5# = mouse_y_speed_4#
	mouse_y_speed_4# = mouse_y_speed_3#
	mouse_y_speed_3# = mouse_y_speed_2#
	mouse_y_speed_2# = mouse_y_speed_1#
	mouse_y_speed_1# = MouseYSpeed ( )
	the_yaw# = ( ( mouse_x_speed_1# + mouse_x_speed_2# + mouse_x_speed_3# + mouse_x_speed_4# + mouse_x_speed_5# ) / 5.0 ) * mouselook_x_inc#
	the_pitch# = ( ( mouse_y_speed_1# + mouse_y_speed_2# + mouse_y_speed_3# + mouse_y_speed_4# + mouse_y_speed_5# ) / 5.0 ) * mouselook_y_inc#
	;^^^^^^

	TurnEntity user, 0.0, -the_yaw#, 0.0 ; Turn the user on the Y (yaw) axis.
	user_camera_pitch# = user_camera_pitch# + the_pitch#
	; -- Limit the user's camera to within 180 degrees of pitch rotation. 'EntityPitch()' returns useless values so we need to use a variable to keep track of the camera pitch.
	If user_camera_pitch# > 90.0 Then user_camera_pitch# = 90.0
	If user_camera_pitch# < -90.0 Then user_camera_pitch# = -90.0
	;^^^^^^
	RotateEntity user_camera, user_camera_pitch#, 0.0, 0.0 ; Pitch the user's camera up and down.
	
	; -- Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop.
	If ( MouseX() > mouse_right_limit ) Or ( MouseX() < mouse_left_limit ) Or ( MouseY() > mouse_bottom_limit ) Or ( MouseY() < mouse_top_limit )
		MoveMouse viewport_center_x, viewport_center_y
	EndIf
	;^^^^^^

End Function


Function MoveUser()

	If KeyDown( 32 ) Or KeyDown( 205 ) ; Right. The 'D' and 'CURSOR RIGHT' keys.
		x#=1.0
	Else If KeyDown( 30 ) Or KeyDown( 203 ) ; Left. The 'A' and 'CURSOR LEFT' keys.
		x#=-1.0
	EndIf

	If KeyDown( 33 ) Or KeyDown( 54 ) ; Up. The 'F' and 'RIGHT SHIFT' keys.
		y#=1.0
	Else If KeyDown( 46 ) Or KeyDown( 157 ) ; Down. The 'C' and 'RIGHT CONTROL' keys.
		y#=-1.0
	EndIf

	If KeyDown( 17 ) Or KeyDown( 200 ) ; Forward. The 'W' and 'CURSOR UP' keys.
		z#=1.0
	Else If KeyDown( 31 ) Or KeyDown( 208 ) ; Backward. The 'S' and 'CURSOR DOWN' keys.
		z#=-1.0
	EndIf

	MoveEntity user, x# * user_movement_speed# * Delta_Time#, y# * user_movement_speed# * Delta_Time#, z# * user_movement_speed# * Delta_Time# ; Move the user.

End Function


Function CalculateDeltatimeAndGametime()
; NOTES:
; The variable 'old_time' must be set to the current millisecs time at the start of a new game and when returning from a pause.

	milli_secs = MilliSecs () ; Store the 'MilliSecs ()' time in the 'milli_secs' variable so that you can use the stored time value without calling 'MilliSecs ()' again in this loop.

	the_time_taken = milli_secs - old_time ; Calculate the time the last loop took to execute.
	If the_time_taken > 100 Then the_time_taken = 100 ; This stops disk accesses and the like from causing jumps in position.
	game_time = game_time + the_time_taken ; Calculate the value for the 'game_time' variable used with timeouts, etc.
	Delta_Time# = the_time_taken / 1000.0 ; Calculate the value for the 'Delta_Time#' variable used to regulate game speed.
	old_time = milli_secs ; Update the 'old_time' variable with the current time.

End Function


Function InitializeNewMap()
; NOTES:
 ; This function should only need to be run when a new map is first loaded. It should normally be executed by the map file loader routine.

	; -- Precache the game's 3D graphics media. This forces the media to be uploaded to the video card. This should be done before any of the media is hidden or has its alpha set to zero to turn off rendering. Note that the camera will end up pointing back to where it started, so there is no need to reset its orientation.
	For i = 1 to 4
		TurnEntity user_camera, 0.0, 90.0, 0.0, True
		RenderWorld
	Next
	;^^^^^^
End Function
