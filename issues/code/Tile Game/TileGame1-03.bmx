SuperStrict

Global ScrWidth : Int  = 640
Global ScrHeight : Int = 480

Global TileList:TList = CreateList()
Global BonusList:TList = CreateList()
Global MovingXList:TList = CreateList()

Const TILESIZE:Int	 = 32 

Type TTile
	Field x:Int
	Field y:Int

	Field xTile:Int
	Field yTile:Int
	
	Field Width:Byte
	Field Height:Byte

	Field Walkable:Byte
	
	Function Create(sx:Int, sy:Int, WB:Byte) 
		Local TT:TTile = New TTile
			TT.width  = TiLESIZE		
			TT.height = TiLESIZE		
			TT.xTile = sx				
			TT.yTile = sy         		
			TT.x = TT.xTile * TT.width		
			TT.y = TT.yTile * TT.height		
			TT.Walkable = WB				

		ListAddLast(TileList , TT)	
	End Function	

	Method update()
		If 	  walkable 			 'walkable
			SetColor(255 , 0 , 0)	 'red
		Else 						 'NOT walkable
		 	SetColor(0 , 255 , 0) 	'green
		End If	
	End Method			

	Method Draw()
   	    DrawRect(x,y,Width,Height)	
	End Method

End Type

Type TLevel
	Field Width : Byte 
	Field Height : Byte

	Field Map:Int[,]
	
	Function Create:TLevel(MyMap:Int[],map_width:Int,map_height:Int)
	    Local TM : TLevel = New TLevel	
		TM.Width  = map_width
		TM.Height = map_height

		TM.map = New Int [TM.Width, TM.Height]
		TM.load(myMap)
		
		Return TM								
	End Function	
	
	Method Load(arrMap:Int[])
		For Local i:Int = 0 Until Height
			For Local j:Int = 0 Until Width
				Map[j , i] = arrMap[j + (i * Width)]
				If Map[j , i]
				     TTile.Create(j , i , True)
				Else
				     TTile.Create(j , i , False)
				End If
			Next
		Next		
	End Method	

 	Method Render()
		
		For Local CurTile:TTile = EachIn TileList
			CurTile.Update()
			CurTile.Draw()
		Next	
		
		For Local CurBonus:TBonus = EachIn BonusList
			CurBonus.Update()
			CurBonus.Draw()
		Next			
		
		For Local CurMovingX:TMovingX = EachIn MovingXList
			CurMovingX.Update()
			CurMovingX.Draw()
		Next				
		
	End Method
	
End Type

Type TBonus 
	Field x:Int
	Field y:Int
	Field width:Int
	Field height:Int
	
	Field points:Int
	Field miny:Int
	Field maxy:Int
	Field diry:Int
	Field speed:Int
	Field AnimDelay:Int
	
	Function Create(sx:Int,sy:Int,pts:Int)
		Local TB : TBonus = New TBonus
			TB.Width = 6
			TB.Height = 16
			TB.x = sx * TiLESIZE + ((TiLESIZE - TB.width) / 2)	'center it
			TB.y = sy * TiLESIZE + ((TiLESIZE - TB.height) / 2)	'center it
			TB.points = pts
			TB.miny = TB.y - 4
			TB.maxy = TB.y + TB.Height + 4
			TB.diry = -1
			TB.speed = 1
			TB.AnimDelay = 5
		ListAddLast(BonusList ,TB)
	End Function	
	
	Method Update()	
		If(Animdelay < 0)

			   If ( miny >= (y) Or maxy <= (y + height) ) 
				     diry = -diry
			   End If
		
			   y = y + speed * diry
		
			   AnimDelay = 5

		End If 	
		
		AnimDelay :- 1
		
	End Method
	
	Method Draw()	
		SetColor(255 , 255 , 0)
		DrawRect(x,y,Width,Height)
	End Method	
	
End Type

Type TMovingX 
	Field x:Int
	Field y:Int
	Field width:Int
	Field height:Int
	
	Field mindir:Int
	Field maxdir:Int

	Field dirx:Int
	Field speed:Int	
	
	Function Create(xt:Int , yt:Int , mn:Int , mx:Int , d:Int)
		Local TMX:TMovingX = New TMovingX

			TMX.x = xt * TILESIZE
			TMX.y = yt * TILESIZE

			TMX.width = TILESIZE 
			TMX.height = TILESIZE

			TMX.speed = 1

			TMX.dirx = d

			TMX.mindir = (mn + xt) * TILESIZE	
			TMX.maxdir = (mx + xt + 1) * TILESIZE 	

		ListAddLast(MovingXList, TMX)	
	End Function

	Method Update()
		
		If( mindir >= x Or maxdir <= (x + width) )	
				dirx = - dirx
		EndIf
		
		 	x :+ speed * dirx
	
	End Method	
	
	Method Draw()	
		SetColor(128 , 128 , 128)	 ' gray
		DrawRect(x , y , Width , Height)	
		SetColor(255 , 255 , 255)	 ' white line
		DrawLine(x , y , x + width - 1 , y)	
	End Method

End Type		
	
		
Global LevMap:Int[] = [1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , ..
		 		 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 1 , ..
				 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , ..
				 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
				 1 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
				 1 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , ..
				 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
				 1 , 0 , 1 , 1 , 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 1 , ..
				 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 , ..
				 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1]

Global MyLevel : TLevel = New TLevel.Create(LevMap , 20 , 10)

Function Init_All_Bonuses()
	TBonus.Create(1 , 1 , 25) 
	TBonus.Create(3 , 8 , 25) 
	TBonus.Create(4 , 8 , 25) 
	TBonus.Create(5 , 8 , 25) 
	TBonus.Create(15 , 1 , 100) 
	TBonus.Create(7 , 4 , 100) 
	TBonus.Create(9 , 7 , 10) 
	TBonus.Create(4 , 6 , 10) 
	TBonus.Create(9 , 2 , 10) 
	TBonus.Create(14 , 7 , 10) 
	TBonus.Create(17 , 6 , 10) 
	TBonus.Create(17 , 4 , 10) 
	TBonus.Create(18 , 4 , 10) 
	TBonus.Create(3 , 3 , 10) 
End Function	

TMovingX.Create(13 , 3 , - 2 , 5 , 1)

'----------------------------- main loop -----------------------------
Graphics(ScrWidth, ScrHeight) 

Init_All_Bonuses()

Repeat
	 Cls

		MyLevel.Render()
		
	 Flip 
Until KeyDown(KEY_ESCAPE) Or AppTerminate()

End
