Type point2
	Field x#, y#
End Type

;Calculates the distance between 2 2D points.
Function point2_CalcDistance#( A.point2, B.point2 )
	Local dX# = Abs( A\x - B\x )
	Local dY# = Abs( A\y - B\y )
	Return Sqr( (dX*dX) + (dY*dY) )
End Function

;Calculates the angle between 2 2D points.
Function point2_CalcAngle#( A.point2, B.point2 )
			Local dX# = A\x - B\x
			Local dY# = A\y - B\y
			Return ATan2( dX#, dY# ) +180
End Function

Local Pos.point2 = New point2; Current position.
Local Target.point2 = New point2; Target position.

;Move position and target to the centre of the screen.
Pos\x = 320: Pos\y = 240
Target\x = 320: Target\y = 240

Graphics 640,480, 0, 2
SetBuffer BackBuffer()
Repeat:Flip:Cls

	If MouseHit( 1 )
		;Update target with new mouse coords.
		Target\x = MouseX()
		Target\y = MouseY()
	EndIf

	;Check to see if the sprite is not already 'at' Target.
	If point2_CalcDistance( Pos, Target ) > 1
		Angle# = point2_CalcAngle( Pos, Target ); Calculate the angle needed to go to Target.
		Pos\x = Pos\x + Sin( Angle# );Move along x axis.
		Pos\y = Pos\y + Cos( Angle# );Move along y axis.
	EndIf

	Oval( pos\x-5, pos\y-5, 10, 10 ); Draw sprite

	Text 0,0,"Distance to target: "+ point2_CalcDistance( Pos, Target )
	Text 0,15,"Angle to target: "+ point2_CalcAngle( Pos, Target )

Until KeyHit(1)
