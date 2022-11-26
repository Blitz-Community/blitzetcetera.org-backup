;------- Design-time definition (static)

     Const Screen_SizeX = 640
     Const Screen_SizeY = 480

     Const Key_ArrowUp    = 200  ; Rotate block right (clockwise)
     Const Key_ArrowDown  = 208  ; Move block down
     Const Key_ArrowLeft  = 203  ; Move block left
     Const Key_ArrowRight = 205  ; Move block right
     Const Key_RightCtrl  = 157  ; Drop block
     Const Key_AlphaZ     =  44  ; Rotate block left (counter clockwise)
     Const Key_AlphaX     =  45  ; Rotate block right (clockwise)
     Const Key_Escape     =   1  ; Exit

     ; Block to rotate/place

     Const Block_SizeX = 5
     Const Block_SizeY = 5
     Const Block_Count = 8

     Const Block_Current = 0
     Const Block_Rotate  = 1

     Const Dir_Left  = 1
     Const Dir_Right = 2
     Const Dir_Down  = 3

.BlockData
     Data "     "
     Data "     "
     Data "  X  "
     Data "     "
     Data "     "

     Data "     "
     Data "     "
     Data " XX  "
     Data "     "
     Data "     "

     Data "     "
     Data "     "
     Data " XXX "
     Data "     "
     Data "     "

     Data "     "
     Data "     "
     Data "  XX "
     Data "  X  "
     Data "     "

     Data "     "
     Data "     "
     Data " XXX "
     Data " X   "
     Data "     "

     Data "     "
     Data " X   "
     Data " XXX "
     Data "     "
     Data "     "

     Data "     "
     Data "     "
     Data " XXX "
     Data "  X  "
     Data "     "

     Data "     "
     Data "     "
     Data "XXXX "
     Data "     "
     Data "     "

     ; Playing field

     Const Field_SizeX = 11
     Const Field_SizeY = 20

     ; Visual sizes

     Const VisualBrick_SizeX = 12*2
     Const VisualBrick_SizeY = 10*2

     ; Time

     Const Block_MoveDelay_Default = 1000  ; Initial block auto-move delay (in millisecs)
     Const Block_MoveDelay_Factor# = 0.05  ; Generic increment/decrement game move delay factor

     Const Block_AvgBrickCount = 3  ; Average number of bricks in a block
     Const Block_CountPerLine = Field_SizeX / Block_AvgBrickCount  ; Rounded number of blocks to make a line

     Const Block_MoveDelay_IncFactor# = 1 + Block_MoveDelay_Factor  ; Multiplier for increasing delay
     Const Block_MoveDelay_DecFactor# = 1 + Block_MoveDelay_Factor / Block_CountPerLine  ; Divisor for decreasing delay

     ; Notes:
     ;  Game goes faster with each block placed. Game goes slower with each line filled.
     ;  The game speed increase for each block placed should be equal to the game speed decrease for each line filled
     ;   divided by the rounded number of bricks in each block required to make a straight line (Block_CountPerLine).

     ; Collisions

     Const Collide_None  = 0
     Const Collide_Wall  = 1
     Const Collide_Brick = 2

;------- Run-time definition (variable)

     Dim PlayField( Field_SizeX , Field_SizeY )

     Dim Block( 1 , Block_SizeX , Block_SizeY )  ; Current block and temporary space to store rotated block

     Dim BlockBank( Block_Count , Block_SizeX , Block_SizeY )  ; All unique blocks

     Global Block_PosX  ; Current block position
     Global Block_PosY

     Global Block_MoveDelay  ; Current delay of block moving down auto
     Global Block_LastMoved  ; Remember last time block moved automatically (down)

     Global Game_Score

;------- Run (init, game, exit)

     SeedRnd MilliSecs ()

     BlockBank_Load
     Game_Reset

     Graphics Screen_SizeX , Screen_SizeY
     Color 255 , 255 , 255
     ClsColor 0 , 0 , 0

     SetBuffer BackBuffer ()
     Repeat

          ;- Logic update

          If KeyHit ( Key_ArrowUp    ) Then Block_Rotate Dir_Right
          If KeyHit ( Key_ArrowDown  ) Then Block_Move Dir_Down
          If KeyHit ( Key_ArrowLeft  ) Then Block_Move Dir_Left
          If KeyHit ( Key_ArrowRight ) Then Block_Move Dir_Right
          If KeyHit ( Key_RightCtrl  ) Then Block_Drop
          If KeyHit ( Key_AlphaZ     ) Then Block_Rotate Dir_Left
          If KeyHit ( Key_AlphaX     ) Then Block_Rotate Dir_Right
          FlushKeys
          Block_AutoMove

          ;- Visual update

          ; Make (0,0) center of screen
          Origin Screen_SizeX/2 - Field_SizeX/2 * VisualBrick_SizeX , Screen_SizeY/2 - Field_SizeY/2 * VisualBrick_SizeY
          PlayField_Draw
          Block_Draw
          Origin 0 , 0
          Stats_Draw
          Flip
          Cls

     Until KeyHit ( Key_Escape )

End

;-------

     Function Game_Reset ()
          PlayField_Erase
          Block_MoveDelay = Block_MoveDelay_Default
          Block_New
          Game_Score = 0
     End Function

     Function Block_New ()
          Local Rotate_Count
          Local Repeat_Number

          Block_PosX = Field_SizeX/2 - Block_SizeX/2  ; Center horizontally
          Block_PosY = 0                              ; Top of playing field
          Block_Assign Rand ( 1 , Block_Count )
          Rotate_Count = Rand ( 0 , 3 )  ; Select random direction
          For Repeat_Number = 1 To Rotate_Count
               Block_Rotate Dir_Right
          Next
          Block_MoveDelay = Block_MoveDelay / Block_MoveDelay_DecFactor  ; Faster
          Block_LastMoved = MilliSecs ()  ; Reset auto move start time
          If Block_Collide ( Block_Current , Block_PosX , Block_PosY ) Then Game_Reset  ; Game over
     End Function

     Function Block_Assign ( BlockNumber )  ; Get unique block from blockbank and replace current block
          Local BrickX
          Local BrickY

          For BrickX = 1 To Block_SizeX
               For BrickY = 1 To Block_SizeY
                    Block( Block_Current , BrickX , BrickY ) = BlockBank ( BlockNumber , BrickX , BrickY )
               Next
          Next
     End Function

     Function Block_AutoMove ()
          If MilliSecs () >= Block_LastMoved + Block_MoveDelay
               Block_LastMoved = MilliSecs ()
               Block_Move Dir_Down
          End If
     End Function

     Function Block_Move ( Direction )
          Local Future_PosX
          Local Future_PosY

          ; Test for collision with block's future position (prevention)
          Future_PosX = Block_PosX
          Future_PosY = Block_PosY
          Select Direction
               Case Dir_Left
                    Future_PosX = Future_PosX - 1
               Case Dir_Right
                    Future_PosX = Future_PosX + 1
               Case Dir_Down
                    Future_PosY = Future_PosY + 1
                    Block_LastMoved = MilliSecs ()  ; Reset automove time
          End Select
          Select Block_Collide ( Block_Current , Future_PosX , Future_PosY )
               Case Collide_None  ; Move normally
                    Block_PosX = Future_PosX
                    Block_PosY = Future_PosY
               Case Collide_Wall  ; Can't move
               Case Collide_Brick  ; Place block
                    If Direction = Dir_Down  ; Place block only if moving down
                         Block_Integrate
                         PlayField_Check
                         Block_New
                    End If
          End Select
     End Function

     Function Block_Rotate ( Direction )
          Local BrickX
          Local BrickY

          Select Direction
               Case Dir_Left
                    ; Rotate by mixing X and Y and put into temporary space to prevent overwrite
                    For BrickX = 1 To Block_SizeX
                         For BrickY = 1 To Block_SizeY
                              Block( Block_Rotate , BrickY , BrickX ) = Block( Block_Current , Block_SizeX-BrickX+1 , BrickY )
                         Next
                    Next
               Case Dir_Right
                    ; Rotate by mixing X and Y and put into temporary space to prevent overwrite
                    For BrickX = 1 To Block_SizeX
                         For BrickY = 1 To Block_SizeY
                              Block( Block_Rotate , BrickY , BrickX ) = Block( Block_Current , BrickX , Block_SizeY-BrickY+1 )
                         Next
                    Next
          End Select
          Select Block_Collide ( Block_Rotate , Block_PosX , Block_PosY )
               Case Collide_None  ; Rotate normally
                    ; Put result back in block
                    For BrickX = 1 To Block_SizeX
                         For BrickY = 1 To Block_SizeY
                              Block( Block_Current , BrickX , BrickY ) = Block( Block_Rotate , BrickX , BrickY )
                         Next
                    Next
               Case Collide_Wall , Collide_Brick  ; Can't rotate
          End Select
     End Function

     Function Block_Drop ()  ; Move block down until collided
          While Not Block_Collide ( Block_Current , Block_PosX , Block_PosY + 1 )
               Block_PosY = Block_PosY + 1
          Wend
          Block_Integrate
          PlayField_Check
          Block_New
     End Function

     Function Block_Integrate ()  ; Get rid of block by integrating into playing field
          Local BrickX
          Local BrickY

          For BrickX = 1 To Block_SizeX
               For BrickY = 1 To Block_SizeY
                    If Block( Block_Current , BrickX , BrickY )
                         PlayField( Block_PosX + BrickX , Block_PosY + BrickY ) = True
                    End If
               Next
          Next
     End Function

     Function Block_Collide ( ThisBlock , PosX , PosY )  ; Test for collision between block and playing field
          ;- Note: 'ThisBlock' can indicate either the current block or the rotated block
          Local BrickX
          Local BrickY

          ; Check for collision with left wall of playing field
          If Block_LeftMostBrick ( ThisBlock ) + PosX < 1 Then Return Collide_Wall
          ; Check for collision with right wall of playing field
          If Block_RightMostBrick ( ThisBlock ) + PosX > Field_SizeX Then Return Collide_Wall
          ; Check for collision with bottom wall of playing field (pretend it's bricks so block will be placed)
          If Block_BottomBrick ( ThisBlock ) + PosY > Field_SizeY Then Return Collide_Brick
          ; Check for collision with bricks by searching overlapping bricks
          For BrickX = 1 To Block_SizeX
               For BrickY = 1 To Block_SizeY
                    If Block( ThisBlock , BrickX , BrickY )
                         If PlayField( PosX + BrickX , PosY + BrickY )
                              Return Collide_Brick
                         End If
                    End If
               Next
          Next

          ; Otherwise not colliding with anything
          Return Collide_None
     End Function
     ; Possible optimizations:
     ;  Cache leftmost/rightmost/bottom brick after block change.
     ;  Find leftmost/rightmost/bottom brick during initialization.

     Function Block_LeftMostBrick ( ThisBlock )  ; Find position of leftmost brick in block
          Local BrickX
          Local BrickY

          For BrickX = 1 To Block_SizeX
               For BrickY = 1 To Block_SizeY
                    If Block( ThisBlock , BrickX , BrickY ) Then Return BrickX
               Next
          Next
     End Function

     Function Block_RightMostBrick ( ThisBlock )  ; Find position of rightmost brick in block
          Local BrickX
          Local BrickY

          For BrickX = Block_SizeX To 1 Step -1
               For BrickY = 1 To Block_SizeY
                    If Block( ThisBlock , BrickX , BrickY ) Then Return BrickX
               Next
          Next
     End Function

     Function Block_BottomBrick ( ThisBlock )  ; Find position of bottom brick in block
          Local BrickX
          Local BrickY

          For BrickY = Block_SizeY To 1 Step -1
               For BrickX = 1 To Block_SizeX
                    If Block( ThisBlock , BrickX , BrickY ) Then Return BrickY
               Next
          Next
     End Function

     Function Block_Draw ()
          Local BrickX
          Local BrickY
          Local PosX
          Local PosY

          For BrickX = 1 To Block_SizeX
               For BrickY = 1 To Block_SizeY
                    If Block( Block_Current , BrickX , BrickY )
                         PosX = (Block_PosX + BrickX-1) * VisualBrick_SizeX
                         PosY = (Block_PosY + BrickY-1) * VisualBrick_SizeY
                         Rect PosX , PosY , VisualBrick_SizeX , VisualBrick_SizeY , False
                         Oval PosX , PosY , VisualBrick_SizeX , VisualBrick_SizeY , False
                    End If
               Next
          Next
     End Function

     Function PlayField_Check ()  ; Check if playfield contains filled horizontal lines
          Local FieldY

          For FieldY = 1 To Field_SizeY
               If PlayField_LineFilled ( FieldY )
                    PlayField_RemoveLine FieldY
                    Block_MoveDelay = Block_MoveDelay * Block_MoveDelay_IncFactor  ; Slower
                    Game_Score = Game_Score + 1
               End If
          Next
     End Function

     Function PlayField_LineFilled ( FieldY )
          Local FieldX

          For FieldX = 1 To Field_SizeX
               If Not PlayField( FieldX , FieldY ) Return False  ; If a gap was found, then it's not filled
          Next

          Return True  ; No gap was found, so filled
     End Function

     Function PlayField_RemoveLine ( RemoveY )
          Local FieldX
          Local FieldY

          For FieldX = 1 To Field_SizeX
               For FieldY = RemoveY-1 To 1 Step -1
                    PlayField( FieldX , FieldY+1 ) = PlayField( FieldX , FieldY )
               Next
          Next
     End Function

     Function PlayField_Erase ()
          Local FieldX
          Local FieldY

          For FieldX = 1 To Field_SizeX
               For FieldY = 1 To Field_SizeY
                    PlayField( FieldX , FieldY ) = False
               Next
          Next
     End Function

     Function PlayField_Draw ()
          Local BrickX
          Local BrickY
          Local PosX
          Local PosY

          ; Frame *around* the game area
          Rect OffsetX - 1 , OffsetY - 1 , Field_SizeX * VisualBrick_SizeX + 2 , Field_SizeY * VisualBrick_SizeY + 2 , False

          For BrickX = 1 To Field_SizeX
               For BrickY = 1 To Field_SizeY
                    If PlayField( BrickX , BrickY )
                         PosX = (BrickX-1) * VisualBrick_SizeX
                         PosY = (BrickY-1) * VisualBrick_SizeY
                         Rect PosX , PosY , VisualBrick_SizeX , VisualBrick_SizeY , True
                         Color 0 , 0 , 0
                         Rect PosX , PosY , VisualBrick_SizeX , VisualBrick_SizeY , False
                         Color 255 , 255 , 255
                    End If
               Next
          Next
     End Function

     Function Stats_Draw ()
          Color 0 , 255 , 0
          Text Screen_SizeX / 2 , 10 , "Score: " + Game_Score , True , False
          Text Screen_SizeX / 2 , Screen_SizeY - 25 , "Level: " + Block_MoveDelay , True , False
          Color 255 , 255 , 255
     End Function

     Function BlockBank_Load ()  ; Restore unique blocks from data into array for easy access
          Local BlockNumber
          Local BlockLineNumber  ; (BrickY)
          Local BlockLine$
          Local BrickX

          Restore BlockData
          For BlockNumber = 1 To Block_Count
               For BlockLineNumber = 1 To Block_SizeY
                    Read BlockLine
                    For BrickX = 1 To Block_SizeX
                         If Mid ( BlockLine , BrickX , 1 ) = "X"
                              BlockBank( BlockNumber , BrickX , BlockLineNumber ) = True
                         End If
                    Next
               Next
          Next
     End Function

;-->