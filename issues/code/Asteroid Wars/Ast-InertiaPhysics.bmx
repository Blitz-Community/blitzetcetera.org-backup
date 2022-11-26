'-----------------------------------------------------------------------------
'	Inertia Example - Richard R Betson 10/20/04
'	www.redeyeware.50megs.com
'	Use Granted :)
'-----------------------------------------------------------------------------

Strict

' -----------------------------------------------------------------------------
'
' Zodiacs inertia engine - last updated 21 September 2000
' Email: fooligan@gmx.de
'
'
'BMAX PORT - Richard R Betson - 10/17/04
'www.redeyeware.50megs.com
'Author permission granted
'
' -----------------------------------------------------------------------------



Const screenw				= 1024			' Screen width
Const screenh				= 768			' Screen height

Graphics screenw, screenh, 0,80	' Open display
'Image variables

' -------------------------------------------------------------------------
' These variables define the movement of the player ship
'--------------------------------------------------------------------------

Global	X#					= screenw/2		' X position
Global	Y#					= screenh/2		' Y position
Global	dX#					= 0				' X speed
Global	dY#					= 0				' Y speed
Global	Dir					= 0				' Current Frame (= Direction)
Global	gravity#			= 0.0			' Gravity applied 
Global	maxspeed#			= 5				' Max Speed
Global	friction#			= 0.04			' Friction applied to speed
Global	Acc#				= 0.11			' How fast it boosts in the x direction (mixed with Sin/Cos though)
Global	turnacc#			= 0.18			' acc when turning
Global	turnfriction#		= 0.08			' The friction applied when turning
Global	turnmax#			= 3				' Max Turnspeed
Global	turnspeed#			= 0				' Current Speed in turning
Global	turnangle#			= 0				' Current angle of ship
Global   speed#
Const totalvehicles = 7		'Number of different vehicle dynamics available

' -----------------------------------------------------------------------------
' Ship Image
' -----------------------------------------------------------------------------

' Define the starting behavior of the player
Global aeroplane$		'Name of current vehicle
Global vehicle			'Type of current dynamic set
setdynamics (0)			'Set all movement variables accordingly


' ----------------------------------------
' Sketch the images 
' ----------------------------------------
AutoMidHandle True					' Centre future image handles

Global ship=LoadImage("ship.png") 



' -----------------------------------------------------------------------------
' main loop...
' -----------------------------------------------------------------------------


'Repeat  


HideMouse  
While Not KeyHit(KEY_escape) 
	RocketUpdate()									' Check Input device
	DrawRocket ()								' Draw rocket + background
	SetBlend Alphablend
	SetAlpha(.6)
	SetColor 255,0,0
	DrawText (aeroplane$,10,10)
	DrawText ("Change Vehicle with <space>, steer with <cursor keys>",10,30)
	DrawText "FPS: "+FPS.Calc(),50,50
	SetColor 255,255,255
	SetAlpha(1)
	' Show result
	
	Flip	
	Cls
	FlushMem()
Wend							' [ESC] key quits game

End  





Type FPS

	Global Counter, Time, TFPS

	Function Calc%()
	'	FPS_Counter    <> Runs And displays the FPS
	'	--------------------------------------------
			Counter:+1
		
			If Time < MilliSecs()
				TFPS = Counter-1
				Time = MilliSecs() + 1000'Update
				' <- Frames/Sec
				Counter = 0
			EndIf
			
		Return TFPS
	'	;--------------------------------------------
	EndFunction
	
EndType

' -----------------------------------------------------------------------------
' functions used
' -----------------------------------------------------------------------------

' -----------------------------------------------------------------------------
' Check player keys
' -----------------------------------------------------------------------------

Function RocketUpdate()
		' Space, change vehicle
		
		If KeyHit (32)  
		
			vehicle = vehicle + 1
			If vehicle = totalvehicles Then vehicle = 0
			setdynamics (vehicle)
			dX = 0
			dY = 0
			turnspeed = 0
				
		End If
		
'Rem
	' Cursur right
		
		If KeyDown (key_right)
			 turnspeed# =  turnspeed# +  turnacc#
			If  turnspeed# >  turnmax# Then  turnspeed# =  turnmax#
		EndIf  

	' Cursur left
		
		If KeyDown (key_left)
			 turnspeed =  turnspeed -  turnacc
			If  turnspeed < - turnmax Then  turnspeed = - turnmax
		EndIf
		
	' Cursur up, thrust
		

		
		If KeyDown (key_up)
		'Add a vector in the direction of your ship
		'It will push you ship forward.
		
			dX:+ Cos(Dir)*Acc
			dY:+ Sin(Dir)*Acc
		EndIf

	' Cursur up, thrust
		
		If KeyDown (key_down)
			dX:- Cos(Dir)*Acc
			dY:- Sin(Dir)*Acc
		EndIf
								
		Local thrust# = Sqr(dx*dx + dy*dy)
		
		
		If Thrust > 0
			dX:- dX/thrust*Friction
			dY:- dY/thrust*Friction
		EndIf
		
		X:+ dX
		Y:+ dY 

	SetColor 255,0,0 
		DrawLine X,Y,X,Y+Dy*50
	SetColor 0,255,0
		DrawLine X,Y,X+Dx*50,Y
	SetColor 0,0,255
		DrawLine X,Y,X+Dx*50,Y+Dy*50
	SetColor 255,255,255
	
	

	'Set friction to movement

		'calculate speed & angle
'		Local speed#=Sqr((dX)*(dX)+(dY)*(dY))	'Calculate true "diagonal" speed
'		Local angle#=(ATan2(dX,dY)+270) Mod 360 			'Calcutate true angle

		'friction
'		If speed >  maxspeed Then speed =  maxspeed 	'Limit speed
'		speed = speed -  friction						'Apply friction to speed
'		If speed<0 Then speed = 0						'Lowest speed: 0
		
		'set back dx & dy
'		dX = speed*Cos(angle)							'New speed in x direction
'		dY = speed*-Sin(angle)							'New speed in y direction
						
		'Set friction to rotation
		Dir:+ turnspeed  'turnangle
		
		'apply momentum to player rotation
		If  turnspeed > 0 Then
				'turnspeed > 0, clockwise
				 turnspeed= turnspeed- turnfriction
				If  turnspeed < 0 Then  turnspeed = 0		'stop turning
			Else
				'turnspeed < 0, anti-clockwise
				 turnspeed= turnspeed+ turnfriction
				If  turnspeed > 0 Then  turnspeed = 0		'stop turning
		End If
		

		

	'EndRem
		
Rem			
	' Cursur right
		
		If KeyDown (key_right)
			 turnspeed# =  turnspeed# +  turnacc#
			If  turnspeed# >  turnmax# Then  turnspeed# =  turnmax#
		EndIf  

	' Cursur left
		
		If KeyDown (key_left)
			 turnspeed =  turnspeed -  turnacc
			If  turnspeed < - turnmax Then  turnspeed = - turnmax
		EndIf
		
	' Cursur up, thrust
		
		If KeyDown (key_up)
			dX:+ Cos(Dir)*Acc
			dY:+ Sin(Dir)*Acc
		EndIf

	' Cursur up, thrust
		
		If KeyDown (key_down)
			dX:- Cos(Dir)*Acc
			dY:- Sin(Dir)*Acc
		EndIf

	
	'Set friction to movement

		'calculate speed & angle
'		Local speed#=Sqr((dX)*(dX)+(dY)*(dY))	'Calculate true "diagonal" speed
'		Local angle#=(ATan2(dX,dY)+270) Mod 360 			'Calcutate true angle

		'friction
'		If speed >  maxspeed Then speed =  maxspeed 	'Limit speed
'		speed = speed -  friction						'Apply friction to speed
'		If speed<0 Then speed = 0						'Lowest speed: 0
		
		'set back dx & dy
'		dX = speed*Cos(angle)							'New speed in x direction
'		dY = speed*-Sin(angle)							'New speed in y direction
						
		'Set friction to rotation
		Dir:+ turnspeed  'turnangle
		
		'apply momentum to player rotation
		If  turnspeed > 0 Then
				'turnspeed > 0, clockwise
				 turnspeed= turnspeed- turnfriction
				If  turnspeed < 0 Then  turnspeed = 0		'stop turning
			Else
				'turnspeed < 0, anti-clockwise
				 turnspeed= turnspeed+ turnfriction
				If  turnspeed > 0 Then  turnspeed = 0		'stop turning
		End If
		
	X = X + dX
	Y = Y + dY
EndRem						
End Function

' ----------------------------------------------------------------------------------
' Calculate Screen position  (screen is not fixed, its "elastic" to player position)
' ----------------------------------------------------------------------------------

Function DrawRocket ()
    
	SetScale(0.5,0.5)
	SetRotation (Dir)  
	SetAlpha(1)

	SetBlend MASKBLEND 
	DrawImage ( ship, X, Y)
	SetRotation (0)
	SetScale 1,1
End Function  



' -----------------------------------------------------------------------------
' Set the movement characteristics of the different vehicles
' -----------------------------------------------------------------------------

Function setdynamics (number)

Select number

Case 0
 aeroplane$ ="Space ship (Mine Storm)"
 

 X#					= screenw/2		' X position
 Y#					= screenh/2		' Y position

 maxspeed#			= 8.0			' Max Speed
 friction#			= 0.01			' Friction applied to speed
 Acc#				= 0.070			' How fast it boosts
 turnacc#			= 0.6			' acc when turning
 turnfriction#		= 0.3			' The friction applied when turning
 turnmax#			= 4.0			' Max Turnspeed

Case 1
 aeroplane$ ="Helicopter"

 X#					= screenw/2		' X position
 Y#					= screenh/2		' Y position

 maxspeed#			= 5				' Max Speed
 friction#			= 0.032			' Friction applied to speed
 Acc#				= 0.10			' How fast it boosts in the x direction (mixed with Sin/Cos though)
turnacc#			= 0.18			' acc when turning
 turnfriction#		= 0.06			' The friction applied when turning
 turnmax#			= 3				' Max Turnspeed


Case 2
 aeroplane$ ="Space ship with gravity (Thurst)"


  X#					= screenw/2		' X position
  Y#					= screenh/2		' Y position
  dX#					= 0				' X speed
  dY#					= 0				' Y speed
  Dir				= 0				' Current Frame (= Direction)
  gravity#			= 0.05			' Gravity applied 
  maxspeed#			= 6				' Max Speed
  friction#			= 0.03			' Friction applied to speed
  Acc#		= 0.15			' How fast it boosts in the x direction (mixed with Sin/Cos though)
  turnfriction#		= 0.3			' The friction applied when turning
  turnspeed#			= 0				' Current Speed in turning
  turnangle#			= 0				' Current angle of ship
  turnmax#			= 3				' Max Turnspeed
  turnacc#	= 1.0			' acc when turning



Case 3
 aeroplane$ ="Tank"

 X#					= screenw/2		' X position
 Y#					= screenh/2		' Y position

  maxspeed#			= 2.5			' Max Speed
  friction#			= 0.07			' Friction applied to speed
 Acc#		= 0.1			' How fast it boosts in the x direction (mixed with Sin/Cos though)
  turnfriction#		= 0.18			' The friction applied when turning
  turnmax#			= 3.0				' Max Turnspeed
  turnacc#	= 0.26			' acc when turning


Case 4
 aeroplane$ ="Little fast spaceship"

 X#					= screenw/2	' X position
 Y#					= screenh/2	' Y position
 dX#				= 0			' X speed
 dY#				= 0			' Y speed
 Dir				= 0			' Current Frame (= Direction)
  gravity#			= 0.0		' Gravity applied 
  maxspeed#			= 15		' Max Speed
  friction#			= 0.08		' Friction applied to speed
 Acc#				= 0.5		' How fast it boosts in the x direction (mixed with Sin/Cos though)
 
  turnfriction#		= 0.5		' The friction applied when turning
  turnspeed#		= 0			' Current Speed in turning
  turnangle#		= 0			' Current angle of ship
  turnmax#			= 5			' Max Turnspeed
  turnacc#			= 1.5		' acc when turning

Case 5
 aeroplane$ ="Little fast spaceship with gravity"

 X#					= screenw/2		' X position
 Y#					= screenh/2		' Y position
 dX#					= 0				' X speed
 dY#					= 0				' Y speed
 Dir				= 0				' Current Frame (= Direction)
  gravity#			= 0.2			' Gravity applied 
  maxspeed#			= 15			' Max Speed
  friction#			= 0.08			' Friction applied to speed
 Acc#		= 0.5			' How fast it boosts in the x direction (mixed with Sin/Cos though)
  turnfriction#		= 0.5			' The friction applied when turning
  turnspeed#			= 0				' Current Speed in turning
  turnangle#			= 0				' Current angle of ship
  turnmax#			= 5				' Max Turnspeed
  turnacc#	= 1.5			' acc when turning


Case 6
 aeroplane$ ="Car"

 X#					= screenw/2		' X position
 Y#					= screenh/2		' Y position
 dX#					= 0				' X speed
 dY#					= 0				' Y speed
 Dir				= 0				' Current Frame (= Direction)
  gravity#			= 0.0			' Gravity applied 
  maxspeed#			= 5				' Max Speed
  friction#			= 0.12			' Friction applied to speed
 Acc#		= 0.3			' How fast it boosts in the x direction (mixed with Sin/Cos though)
  turnfriction#		= 0.18			' The friction applied when turning
  turnspeed#			= 0				' Current Speed in turning
  turnangle#			= 0				' Current angle of ship
  turnmax#			= 3.0				' Max Turnspeed
  turnacc#	= 0.3			' acc when turning


End Select 

' Give the player ship a random position, to let the screen slide in place....
X# = Rnd(1000)
Y# = Rnd(500)

End Function  



