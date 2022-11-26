; Yes, doing collisions for a pong game can be very cumbersome.
; However, the program below will not use any complicated maths.
; Just a logic algorithm.
;
; When running the program, press <space> to toggle debug 'mode'.
; In debug 'mode' additional visuals and a delay is introduced
;  to demonstrate the problem areas in collisions.
; Kinda like a what-you-see-is-what-you-get approach.

  Global User_Debug = True

  Graphics 640 , 480 , 0 , 1  ; Force full screen (read below)

  ; Size of the tile map (from data)

  Const TileMapSizeX = 20
  Const TileMapSizeY = 15

  Global Block = CreateBlockImage ( 32 , 32 )

  ; Detect (grab) the tile size from image

  Global TileSizeX = ImageWidth ( Block )
  Global TileSizeY = ImageHeight ( Block )

  Global Ball = CreateBallImage ( 16 , 16 )

  ; Center the hotspot, so drawing/collision/positioning is from center of image

  MidHandle Ball  ; Center hotspot

  ; Grab ball sizes from image

  Global BallSizeX = ImageWidth ( Ball )
  Global BallSizeY = ImageHeight ( Ball )

  ; Starting position and direction

  Global BallPosX = 64
  Global BallPosY = 72
  Global BallDirX = 1
  Global BallDirY = 1

  ; Actual tilemap

  Dim Tiles( TileMapSizeY , TileMapSizeX )

  Restore TileMap
  For TileY = 0 To TileMapSizeY-1
    For TileX = 0 To TileMapSizeX-1
      Read Tiles( TileY , TileX )
    Next
  Next

  SetBuffer BackBuffer ()

  Repeat

    ; To get perfect collisions in a Pong game you'll need 1 pixel step movement for the ball
    ; or more complicated maths :P The idea is to update the ball (or all logics) more times
    ; than showing it on the screen, making it more independent. This way you can also increase
    ; the ball speed (e.g. by increasing the number of times to update the ball/collision).

    ; It is possible to convert the ball position and direction to floating point values with
    ; a max value of 1 for direction to get that Arkanoid style bouncing. Ofcourse you'll also
    ; need those complicated maths for accurate bouncing, like having special objects instead
    ; of a tilemap.

    ; Update logic
    For GameSpeed = 1 To 3
      Collision
      UpdateBall
    Next
    If KeyHit ( 57 ) Then User_Debug = Not User_Debug  ; Spacebar toggles debug 'mode'

    ; Update screen
    DrawTiles
    DrawBall
    Flip
    Cls

  Until KeyHit ( 1 )

End

  Function DrawTiles ()  ; Draw tiles as usual, but using array
    For TileY = 0 To TileMapSizeY-1
      For TileX = 0 To TileMapSizeX-1
        If Tiles( TileY , TileX ) = 1
          DrawImage Block , TileX * TileSizeX , TileY * TileSizeY
        End If
      Next
    Next
  End Function

  Function DrawBall ()
    DrawImage Ball , BallPosX , BallPosY
  End Function

  Function UpdateBall ()  ; Update ball position using direction
    BallPosX = BallPosX + BallDirX
    BallPosY = BallPosY + BallDirY
  End Function


  Function Collision ()

    ; This function has become huge. Also to easily show which tiles are collided with, I've temporary
    ; set the drawing buffer to the front buffer, which is set back to the back buffer after the function
    ; is done. This means that the result is only visible in full screen (Graphics 640 , 480 , 0 , 1).

    ; Precalculate future position, to prevent the ball from overlapping tiles.

    BallNewX = BallPosX + BallDirX
    BallNewY = BallPosY + BallDirY

    SetBuffer FrontBuffer ()

    ; Precalculate the tile under the ball.

    BallTileX = BallNewX / TileSizeX
    BallTileY = BallNewY / TileSizeY

    ; Loop only through the 8 tiles next to the ball and the tile under the ball.
    ; This ofcourse will crash if the ball gets too close to the edge (but the map is closed on all sides
    ; so this won't happen).

    For TileY = BallTileY-1 To BallTileY+1
      For TileX = BallTileX-1 To BallTileX+1

        ; Draw the tile currently being checked for collisions.

        If User_Debug
          Color 0 , 0 , 255
          Rect TileX * TileSizeX , TileY * TileSizeY , TileSizeX , TileSizeY , False
        End If

        If Tiles( TileY , TileX ) = 1

          If ImagesCollide ( Block , TileX * TileSizeX , TileY * TileSizeY , 0 , Ball , BallNewX , BallNewY , 0 )
            Collision = True

            ; Set a flag to know which tile has collided with the ball.

            If TileY = BallTileY - 1 And TileX = BallTileX     Then TileAbove = True  ; Above
            If TileY = BallTileY + 1 And TileX = BallTileX     Then TileBelow = True  ; Below
            If TileY = BallTileY     And TileX = BallTileX - 1 Then TileLeft  = True  ; Left
            If TileY = BallTileY     And TileX = BallTileX + 1 Then TileRight = True  ; Right
            If TileY = BallTileY - 1 And TileX = BallTileX - 1 Then TileUpLt  = True  ; Upper left
            If TileY = BallTileY - 1 And TileX = BallTileX + 1 Then TileUpRt  = True  ; Upper right
            If TileY = BallTileY + 1 And TileX = BallTileX - 1 Then TileLowLt = True  ; Lower left
            If TileY = BallTileY + 1 And TileX = BallTileX + 1 Then TileLowRt = True  ; Lower right

            ; Visual collision display.

            If User_Debug
              Color 255 , 0 , 0
              Rect TileX * TileSizeX , TileY * TileSizeY , TileSizeX , TileSizeY , True
            End If
          End If

        End If

      Next
    Next

    ; For the exceptions, knowing the absolute heading of the ball is required.

    If BallDirX < 0 And BallDirY < 0 Then HeadingUpLt  = True  ; Upper left
    If BallDirX < 0 And BallDirY > 0 Then HeadingLowLt = True  ; Lower left
    If BallDirX > 0 And BallDirY < 0 Then HeadingUpRt  = True  ; Upper right
    If BallDirX > 0 And BallDirY > 0 Then HeadingLowRt = True  ; Lower right

    ; In case of any collisions, reverse the direction in question.

    If Collision
      ; Any corner (when the ball is pixel-perfectly cornered)
      If TileLeft And TileAbove Or TileLeft And TileBelow Or TileRight And TileAbove Or TileRight And TileBelow
        ; Reverse all directions
        BallDirX = -BallDirX
        BallDirY = -BallDirY
      Else
        ; Only left or right side
        If TileLeft Or TileRight
          BallDirX = -BallDirX
        ; Only upper or lower side
        ElseIf TileAbove Or TileBelow
          BallDirY = -BallDirY
        ; Exception: Vertical single edges
        ElseIf ( HeadingUpLt And TileLowLt ) Or ( HeadingLowLt And TileUpLt ) Or ( HeadingUpRt And TileLowRt ) Or ( HeadingLowRt And TileUpRt )
          BallDirX = -BallDirX
        ; Exception: Horizontal single edges
        ElseIf ( HeadingUpLt And TileUpRt ) Or ( HeadingUpRt And TileUpLt ) Or ( HeadingLowLt And TileLowRt ) Or ( HeadingLowRt And TileLowLt )
          BallDirY = -BallDirY
        ; Exception: Sharp edges (complete bounce)
        ElseIf TileUpLt Or TileUpRt Or TileLowLt Or TileLowRt
          BallDirX = -BallDirX
          BallDirY = -BallDirY
        ; Unknown exception
        Else
          If User_Debug
            Color 255 , 255 , 0
            Oval BallTileX * TileSizeX , BallTileY * TileSizeY , TileSizeX , TileSizeY
            WaitKey
          End If
        End If
      End If
      If User_Debug Then Delay 100
    End If

    SetBuffer BackBuffer ()

  End Function

  .TileMap
  Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
  Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
  Data 1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1
  Data 1,0,1,1,0,0,0,1,0,1,1,0,0,1,1,0,0,0,0,1
  Data 1,0,1,1,0,0,0,1,0,1,1,0,0,0,0,0,1,1,0,1
  Data 1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,1
  Data 1,0,0,0,0,0,0,1,0,0,0,0,1,1,0,0,0,0,0,1
  Data 1,0,1,1,0,0,0,1,0,0,0,0,1,1,0,1,1,0,0,1
  Data 1,0,1,1,0,0,0,1,0,1,1,0,0,0,0,1,1,0,0,1
  Data 1,0,0,0,0,0,0,1,0,1,1,0,0,0,0,0,0,0,0,1
  Data 1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1
  Data 1,0,0,1,1,0,0,1,0,0,0,1,1,0,0,1,1,0,0,1
  Data 1,0,0,1,1,0,0,1,0,0,0,1,1,0,0,1,1,0,0,1
  Data 1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1
  Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1

    ; Functions for image independence
    Function CreateBlockImage ( SizeX , SizeY )
        Image = CreateImage ( SizeX , SizeY )
        SetBuffer ImageBuffer ( Image )
        Color 64 , 96 , 128
        Rect 0 , 0 , SizeX , SizeY , True
        Color 128 , 192 , 255
        Line 0 , 0 , SizeX-2 , 0
        Line 0 , 0 , 0 , SizeY-2
        Color 32 , 48 , 64
        Line 0 , SizeY-1 , SizeX-1 , SizeY-1
        Line SizeX-1 , 0 , SizeX-1 , SizeY-1
        Color 255 , 255 , 255
        Plot 0 , 0
        Return Image
    End Function

    Function CreateBallImage ( SizeX , SizeY )
        Image = CreateImage ( SizeX , SizeY )
        SetBuffer ImageBuffer ( Image )
        For x = 0 To SizeX/2-1
            c = x*191/(SizeX/2)+64
            Color c*2/3 , c*3/4 , c
            y = Float x*SizeY/SizeX
            Oval x , y , (SizeX-x*2) , (SizeY-y*2) , True
        Next
        Return Image
    End Function
