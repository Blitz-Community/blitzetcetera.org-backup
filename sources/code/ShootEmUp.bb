     ;********************************************************************************;
     ;**
     ;**     Fundamental
     ;**
     ;********************************************************************************;
     
     Const Screen_SizeX = 640
     Const Screen_SizeY = 480
     
     Const Key_Up = 200
     Const Key_Down = 208
     Const Key_Left = 203
     Const Key_Right = 205
     Const Key_Control_Left = 29
     Const Key_Control_Right = 157
     Const Key_Escape = 1
     
     Function Fire_Button_Pressed ()
          Return KeyHit ( Key_Control_Left ) Or KeyHit ( Key_Control_Right )
     End Function
     
     SeedRnd MilliSecs ()
     
     ;********************************************************************************;
     ;**
     ;**     Player
     ;**
     ;********************************************************************************;
     
     Const Player_Default_Lives = 6
     Const Player_SizeX = Screen_SizeX / 16
     Const Player_SizeY = Screen_SizeY / 10
     Const Player_Speed = 5
     Const Player_RespawnX = Screen_SizeX / 2 - Player_SizeX / 2
     Const Player_RespawnY = Screen_SizeY - Player_SizeY - 1
     Const Player_HUD_PosX = 4
     Const Player_HUD_PosY = 4
     
     Global Player_PosX
     Global Player_PosY
     Global Player_Lives
     Global Player_Alive
     
     Function Reset_Player ()
          Player_PosX = Player_RespawnX
          Player_PosY = Player_RespawnY
          Player_Lives = Player_Default_Lives
          Player_Alive = True
     End Function

     Function Respawn_Player ()
          Player_PosX = Player_RespawnX
          Player_PosY = Player_RespawnY
          Player_Lives = Player_Lives - 1
          Player_Alive = True
     End Function
     
     Function Player_Fire_Bullet ()
          Bullet_PosX = Player_PosX + Player_SizeX / 2 - Bullet_SizeX / 2
          Bullet_PosY = Player_PosY - Bullet_SizeY
          Bullet_SpeedX = 0
          Bullet_SpeedY = -Bullet_Speed
          New_Bullet Bullet_PosX , Bullet_PosY , Bullet_SpeedX , Bullet_SpeedY
     End Function
     
     Function Explode_Player ()
          ExplosionX = Player_PosX + Player_SizeX / 2
          ExplosionY = Player_PosY + Player_SizeY / 2
          Create_Explosion ExplosionX , ExplosionY
          Kill_Player
     End Function
     
     Function Kill_Player ()
          If Player_Lives = 0
               Reset_Player
               Reset_Game
               Reset_Enemy_Difficulty
          Else
               Player_Alive = False
          End If
     End Function
     
     Function Update_Player ()
          ;-- Input from keyboard
          If Fire_Button_Pressed ()
               If Game_Playing
                    If Player_Alive
                         Player_Fire_Bullet
                    Else
                         Respawn_Player
                    End If
               Else
                    Start_Game
               End If
          End If
          If Player_Alive And Game_Playing
               If KeyDown ( Key_Up ) Then Player_PosY = Player_PosY - Player_Speed
               If KeyDown ( Key_Down ) Then Player_PosY = Player_PosY + Player_Speed
               If KeyDown ( Key_Left ) Then Player_PosX = Player_PosX - Player_Speed
               If KeyDown ( Key_Right ) Then Player_PosX = Player_PosX + Player_Speed
          End If
          ;-- Keep player within screen
          If Player_PosY < 0 Then Player_PosY = 0
          If Player_PosY >= Screen_SizeY - Player_SizeY Then Player_PosY = Screen_SizeY - Player_SizeY - 1
          If Player_PosX < 0 Then Player_PosX = 0
          If Player_PosX >= Screen_SizeX - Player_SizeX Then Player_PosX = Screen_SizeX - Player_SizeX - 1
     End Function
     
     Function Display_Player ()
          If Player_Alive And Game_Playing
               Color 0 , 128 , 255
               Rect Player_PosX , Player_PosY , Player_SizeX , Player_SizeY , False
          End If
     End Function
     
     Reset_Player
     
     ;********************************************************************************;
     ;**
     ;**     Bullet
     ;**
     ;********************************************************************************;
     
     Const Bullet_SizeX = Screen_SizeX / 32
     Const Bullet_SizeY = Screen_SizeY / 20
     Const Bullet_Speed = 7
     
     Global Bullets
     
     Type Bullet
          Field PosX
          Field PosY
          Field SpeedX
          Field SpeedY
     End Type
     
     Function New_Bullet ( PosX , PosY , SpeedX , SpeedY )
          Bullets = Bullets + 1
          Pointer.Bullet = New Bullet
          Pointer.Bullet\PosX = PosX
          Pointer.Bullet\PosY = PosY
          Pointer.Bullet\SpeedX = SpeedX
          Pointer.Bullet\SpeedY = SpeedY
     End Function
     
     Function Delete_Bullet ( Pointer.Bullet )
          Delete Pointer.Bullet
          Bullets = Bullets - 1
     End Function
     
     Function Create_Explosion ( CenterX , CenterY )
          PosX = CenterX - Bullet_SizeX / 2
          PosY = CenterY - Bullet_SizeY / 2
          New_Bullet PosX , PosY , 0 , -Bullet_Speed     ; Up
          New_Bullet PosX , PosY , Bullet_Speed , -Bullet_Speed     ; Up Right
          New_Bullet PosX , PosY , Bullet_Speed , 0     ; Right
          New_Bullet PosX , PosY , Bullet_Speed , Bullet_Speed     ; Down Right
          New_Bullet PosX , PosY , 0 , Bullet_Speed     ; Down
          New_Bullet PosX , PosY , -Bullet_Speed , Bullet_Speed     ; Down Left
          New_Bullet PosX , PosY , -Bullet_Speed , 0     ; Left
          New_Bullet PosX , PosY , -Bullet_Speed , -Bullet_Speed     ; Up Left
     End Function
     
     Function Update_Bullets ()
          For Pointer.Bullet = Each Bullet
               Pointer.Bullet\PosY = Pointer.Bullet\PosY + Pointer.Bullet\SpeedY
               Pointer.Bullet\PosX = Pointer.Bullet\PosX + Pointer.Bullet\SpeedX
               If Pointer.Bullet\PosY < -Bullet_SizeY Or Pointer.Bullet\PosY >= Screen_SizeY Or Pointer.Bullet\PosX < -Bullet_SizeX Or Pointer.Bullet\PosX >= Screen_SizeX
                    Delete_Bullet Pointer.Bullet
               End If
          Next
     End Function
     
     Function Display_Bullets ()
          Color 255 , 224 , 0
          For Pointer.Bullet = Each Bullet
               Rect Pointer.Bullet\PosX , Pointer.Bullet\PosY , Bullet_SizeX , Bullet_SizeY , False
          Next
     End Function
     
     ;********************************************************************************;
     ;**
     ;**     Enemies
     ;**
     ;********************************************************************************;
     
     Const Enemy_SizeX = Screen_SizeX / 24
     Const Enemy_SizeY = Screen_SizeY / 15
     Const Enemy_Default_Fire_Interval = 900     ; Ms
     Const Enemy_Minimum_Fire_Interval = 300     ; Ms
     Const Enemy_Default_Spawn_Interval = 1000     ; Ms
     Const Enemy_Minimum_Spawn_Interval = 100     ; Ms
     Const Enemy_Minimum_Speed = 1
     Const Enemy_Maximum_Speed = 5
     Const Enemy_Difficulty_Increment = 10
     
     Global Enemy_Spawn_LastTime
     Global Enemy_Spawn_Interval
     Global Enemy_Fire_Interval
     Global Enemies
     
     Type Enemy
          Field PosX
          Field PosY
          Field SpeedX
          Field SpeedY
          Field Fires
          Field Fire_LastTime
     End Type
     
     Function New_Enemy ( PosX , PosY , SpeedX , SpeedY , Fires = True )
          Pointer.Enemy = New Enemy
          Enemies = Enemies + 1
          Pointer.Enemy\PosX = PosX
          Pointer.Enemy\PosY = PosY
          Pointer.Enemy\SpeedX = SpeedX
          Pointer.Enemy\SpeedY = SpeedY
          Pointer.Enemy\Fires = Fires
     End Function
     
     Function Delete_Enemy ( Pointer.Enemy )
          Delete Pointer.Enemy
          Enemies = Enemies - 1
     End Function
     
     Function Spawn_Enemies ()
          If MilliSecs () >= Enemy_Spawn_LastTime + Enemy_Spawn_Interval
               Spawn_Random_Enemy
               Enemy_Spawn_LastTime = MilliSecs ()
          End If
     End Function
     
     Function Spawn_Random_Enemy ()
          If Game_Playing Then Increase_Enemy_Difficulty
          PosX = Rand ( 0 , Screen_SizeX - Enemy_SizeX - 1 )
          PosY = -Enemy_SizeY
          SpeedX = 0
          SpeedY = Rand ( Enemy_Minimum_Speed , Enemy_Maximum_Speed )
          Chance = Rand ( 1 , 5 )
          If Chance = 1 Then Enemy_Fires = True
          New_Enemy PosX , PosY , SpeedX , SpeedY , Enemy_Fires
     End Function
     
     Function Enemy_Fire_Bullet ( Pointer.Enemy )
          Bullet_PosX = Pointer.Enemy\PosX + Enemy_SizeX / 2 - Bullet_SizeX / 2
          Bullet_PosY = Pointer.Enemy\PosY + Enemy_SizeY
          Bullet_SpeedX = 0
          Bullet_SpeedY = Bullet_Speed
          New_Bullet Bullet_PosX , Bullet_PosY , Bullet_SpeedX , Bullet_SpeedY
     End Function
     
     Function Update_Enemies ()
          For Pointer.Enemy = Each Enemy
               If Pointer.Enemy\PosY < Screen_SizeY
                    Pointer.Enemy\PosX = Pointer.Enemy\PosX + Pointer.Enemy\SpeedX
                    Pointer.Enemy\PosY = Pointer.Enemy\PosY + Pointer.Enemy\SpeedY
                    If Pointer.Enemy\Fires
                         If MilliSecs () >= Pointer.Enemy\Fire_LastTime + Enemy_Fire_Interval
                              Enemy_Fire_Bullet Pointer.Enemy
                              Pointer.Enemy\Fire_LastTime = MilliSecs ()
                         End If
                    End If
               Else
                    Delete_Enemy Pointer.Enemy
               End If
          Next
     End Function
     
     Function Display_Enemies ()
          Color 128 , 255 , 0
          For Pointer.Enemy = Each Enemy
               Rect Pointer.Enemy\PosX , Pointer.Enemy\PosY , Enemy_SizeX , Enemy_SizeY , False
          Next
     End Function
     
     Function Reset_Enemy_Difficulty ()
          Enemy_Spawn_Interval = Enemy_Default_Spawn_Interval
          Enemy_Fire_Interval = Enemy_Default_Fire_Interval
     End Function
     
     Function Increase_Enemy_Difficulty ()
          If Enemy_Spawn_Interval > Enemy_Minimum_Spawn_Interval
               Enemy_Spawn_Interval = Enemy_Spawn_Interval - Enemy_Difficulty_Increment
          End If
          If Enemy_Fire_Interval > Enemy_Minimum_Fire_Interval
               Enemy_Fire_Interval = Enemy_Fire_Interval - Enemy_Difficulty_Increment
          End If
     End Function
     
     Reset_Enemy_Difficulty
     
     ;********************************************************************************;
     ;**
     ;**     Collisions
     ;**
     ;********************************************************************************;
     
     Function Collisions ()
          Local Bullet.Bullet
          Local Enemy.Enemy
          Local Enemy2.Enemy
          If Game_Playing
               ;-- Player with bullets
               If Player_Alive
                    For Bullet = Each Bullet
                         If RectsOverlap ( Player_PosX , Player_PosY , Player_SizeX , Player_SizeY , Bullet\PosX , Bullet\PosY , Bullet_SizeX , Bullet_SizeY )
                              Explode_Player
                              Delete_Bullet Bullet
                              Exit
                         End If
                    Next
               End If
               ;-- Player with enemies
               If Player_Alive
                    For Enemy = Each Enemy
                         If RectsOverlap ( Player_PosX , Player_PosY , Player_SizeX , Player_SizeY , Enemy\PosX , Enemy\PosY , Enemy_SizeX , Enemy_SizeY )
                              Explode_Player
                              Delete_Enemy Enemy
                              Exit
                         End If
                    Next
               End If
          End If
          ;-- Enemies with bullets
          For Enemy = Each Enemy
               For Bullet = Each Bullet
                    If RectsOverlap ( Enemy\PosX , Enemy\PosY , Enemy_SizeX , Enemy_SizeY , Bullet\PosX , Bullet\PosY , Bullet_SizeX , Bullet_SizeY )
                         Delete_Enemy Enemy
                         Delete_Bullet Bullet
                         Exit
                    End If
               Next
          Next
          ;-- Enemies with enemies
          For Enemy = Each Enemy
               For Enemy2 = Each Enemy
                    If Enemy = Enemy2
                         Exit
                    Else
                         If RectsOverlap ( Enemy\PosX , Enemy\PosY , Enemy_SizeX , Enemy_SizeY , Enemy2\PosX , Enemy2\PosY , Enemy_SizeX , Enemy_SizeY )
                              Delete_Enemy Enemy
                              Delete_Enemy Enemy2
                              Exit
                         End If
                    End If
               Next
          Next
     End Function
     
     ;********************************************************************************;
     ;**
     ;**     Heads up display
     ;**
     ;********************************************************************************;
     
     Function Display_HUD ()
          ;-- Frame
          Color 64 , 64 , 64
          Rect Player_HUD_PosX , Player_HUD_PosY , 70 , 97 , False
          ;-- Splitters
          Line Player_HUD_PosX , Player_HUD_PosY + 32 , Player_HUD_PosX + 69 , Player_HUD_PosY + 32
          Line Player_HUD_PosX , Player_HUD_PosY + 64 , Player_HUD_PosX + 69 , Player_HUD_PosY + 64
          ;-- Lives
          Color 0 , 96 , 192
          Text Player_HUD_PosX + 35 , Player_HUD_PosY , "Lives" , True
          Color 0 , 64 , 128
          Rect Player_HUD_PosX + 6 , Player_HUD_PosY + 12 , Player_Default_Lives * 9 + 4 , 16 , False
          Color 0 , 128 , 255
          Text Player_HUD_PosX + 35 , Player_HUD_PosY + 14 , String ( "" , Player_Lives ) , True
          ;-- Bullet count and interval
          Color 128 , 112 , 0
          Text Player_HUD_PosX + 6 , Player_HUD_PosY + 35 , "Bullets"
          Color 255 , 224 , 0
          Text Player_HUD_PosX + 52 , Player_HUD_PosY + 35 , Bullets
               ;- Interval
               MaxSize = 66
               Count = Enemy_Fire_Interval - Enemy_Minimum_Fire_Interval
               MaxCount = Enemy_Default_Fire_Interval - Enemy_Minimum_Fire_Interval
               ; Convert from MaxCount units to MaxSize units
               ; E.g. from interval range to pixel range
               RealSize = Count * MaxSize / MaxCount
               Color 128 , 96 , 0
               Rect Player_HUD_PosX + 2 , Player_HUD_PosY + 50 , RealSize , 10
               Color 96 , 64 , 0
               Rect Player_HUD_PosX + 2 , Player_HUD_PosY + 50 , MaxSize , 10 , False
          ;-- Enemy count and interval
          Color 64 , 128 , 0
          Text Player_HUD_PosX + 6 , Player_HUD_PosY + 67 , "Enemies"
          Color 128 , 255 , 0
          Text Player_HUD_PosX + 52 , Player_HUD_PosY + 67 , Enemies
               ;- Interval
               MaxSize = 66
               Count = Enemy_Spawn_Interval - Enemy_Minimum_Spawn_Interval
               MaxCount = Enemy_Default_Spawn_Interval - Enemy_Minimum_Spawn_Interval
               ; Convert from MaxCount units to MaxSize units
               ; E.g. from interval range to pixel range
               RealSize = Count * MaxSize / MaxCount
               Color 128 , 96 , 0
               Rect Player_HUD_PosX + 2 , Player_HUD_PosY + 82 , RealSize , 10
               Color 96 , 64 , 0
               Rect Player_HUD_PosX + 2 , Player_HUD_PosY + 82 , MaxSize , 10 , False
     End Function
     
     ;********************************************************************************;
     ;**
     ;**     Game
     ;**
     ;********************************************************************************;
     
     Global Game_Playing
     
     Function Reset_Game ()
          Game_Playing = False
     End Function
     
     Function Start_Game ()
          Game_Playing = True
     End Function
     
     Reset_Game
     
     ;********************************************************************************;
     ;**
     ;**     Graphics
     ;**
     ;********************************************************************************;
     
     Const Font_Size = 12
     
     Graphics Screen_SizeX , Screen_SizeY
     SetBuffer BackBuffer ()
     
     Global Font_Handle
     
     Function Setup_Font ()
          Font_Handle = LoadFont ( "Verdana" , Font_Size , True )
          SetFont Font_Handle
     End Function
     
     Setup_Font
     
     ;********************************************************************************;
     ;**
     ;**     Main
     ;**
     ;********************************************************************************;
     
     Repeat
          Update_Player
          Display_Player
          Update_Bullets
          Display_Bullets
          Update_Enemies
          Display_Enemies
          Spawn_Enemies
          Display_HUD
          Collisions
          Flip
          Cls
     Until KeyHit ( Key_Escape )
     
     End