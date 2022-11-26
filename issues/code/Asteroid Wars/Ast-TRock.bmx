Type TRock Extends TSpaceObject

	Global List:TList
	Global Image:TImage
	Field Size
	Field Rotation#

	Const LARGE  = 3
	Const MEDIUM = 2
	Const SMALL  = 1

	Function Spawn( X, Y ,Size )
		Local Rock:TRock = New TRock	 
		If Not List List = CreateList()
		List.AddLast Rock
		
		If Not Image'First Time
			Image = LoadImage("Ast-Rock.png")
			MidHandleImage( Image )
		EndIf
				
		Local RockSpeed# = 0
		Select Size
			Case LARGE'Large		
				RockSpeed = 0.5 + Rnd( 0, 0.5 )
			Case MEDIUM'Medium
				RockSpeed = 1 + Rnd( 0, 1.0 )
			Case SMALL'Small	
				RockSpeed = 1   + Rnd( 0, 1.5 )
		EndSelect
		
		Rock.X 			 = X
		Rock.Y 			 = Y
		Rock.Direction 	 = Rand(360)
		Rock.Rotation 	 = Rock.Direction
		Rock.Size 		 = Size
		
		'Add Rock Start Speed
		Rock.XSpeed= Cos(Rock.Direction)*RockSpeed
		Rock.YSpeed= Sin(Rock.Direction)*RockSpeed
	EndFunction
	
	Method Draw()

		Select Self.Size
			Case LARGE		
				SetScale 2,2
				Rotation:+ 0.1
			Case MEDIUM'Medium
				SetScale 1,1
				Rotation:+ 0.2
			Case SMALL'Small	
				SetScale 0.5, 0.5
				Rotation:+ 0.7
		EndSelect
			
		SetRotation( Rotation )
		
		SetBlend LIGHTBLEND
		DrawImage( Image,X,Y )
		SetScale 1,1
		SetRotation 0
	EndMethod
	
	Method Update()
		Draw()
		X:+ XSpeed
		Y:+ YSpeed			
		
		Collision()
				
		If X > 800+50 X = -50
		If Y > 600+50 Y = -50
		If X < -50 X = 800+50
		If Y < -50 Y = 600+50
			
	EndMethod

	Function UpdateAll()
		If Not List Return 
		
		For Local Rock:TRock = EachIn List
			Rock.Update()
		Next
	EndFunction

	Method Destroy() 
		List.Remove( Self )
	EndMethod
	
	Method Collision()
		'Rock vs Ship
		'Rock vs Shot
		Local Radius
		Select Size
			Case LARGE		
				Radius = 50
			Case MEDIUM'Medium
				Radius = 50
			Case SMALL
				Radius = 20
		EndSelect
	
		'If a rock hit a ship
		If TShip.List
			For Local Ship:TShip = EachIn TShip.List
				If Distance( X, Y, Ship.X, Ship.Y ) < Radius
					Ship.Destroy()'Ship = Dead		
				EndIf
			Next
		EndIf

		'If a shot hit a rock
		If Not TShot.List Return
		For Local Shot:TShot = EachIn TShot.List
			If Distance( X, Y, Shot.X, Shot.Y ) < Radius
				
				Shot.Destroy()
				
				Select Size
					Case LARGE	
						Destroy()'Destory the rock	
						Spawn( X+Rand(-20,20), Y+Rand(-20,20) , MEDIUM )
						Spawn( X+Rand(-20,20), Y+Rand(-20,20) , MEDIUM )
						Spawn( X+Rand(-20,20), Y+Rand(-20,20) , MEDIUM )
					Case MEDIUM
						Destroy()	
						Spawn( X, Y , SMALL )
						Spawn( X, Y , SMALL )
					Case SMALL
						Destroy()'
						'Spawn( X, Y , LARGE )
						Score:+1 'Global from Main
				EndSelect
	
			EndIf
		Next	
	EndMethod
	
EndType	

'Give the distance between two points
Function Distance#( X1, Y1, X2, Y2 )
	Local DX# = X2 - X1
	Local DY# = Y2 - Y1
	Return Sqr(Dx*Dx + Dy*Dy)
EndFunction

