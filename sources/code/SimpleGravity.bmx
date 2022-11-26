Graphics 800, 600, 0, 60

Global X# = 400
Global Y# = 525

Global GRAVITY# = 4

Global JUMP = False

While Not KeyHit(KEY_ESCAPE)

	Cls
	
		UPDATE_MOVEMENT()
	
	Flip
	
Wend
End

Function UPDATE_MOVEMENT()

	If KeyDown(KEY_SPACE) = True				' INITIATE THE JUMP
		JUMP = True
	EndIf
	If KeyDown(KEY_LEFT) = True
		X# = X# - 2
	EndIf
	If KeyDown(KEY_RIGHT) = True
		X# = X# + 2
	EndIf
	
	If JUMP = True 
		Y# = Y# - GRAVITY#
		GRAVITY# = GRAVITY# - .08		'; ADJUST THIS To CHANGE THE HEIGHT OF THE JUMP
		If Y# >= 524
			JUMP = False
			GRAVITY# = 4
		EndIf
	EndIf

	DrawOval X#, Y#, 25, 25 				'; BALL
	DrawRect 0, 550, 800, 50				'; Floor
	
	DrawText "Press L and R arrow keys to move left and right",0,0
	DrawText "Press space bar to jump",0,10

End Function
