;
; ArcLine uses Line to draw the arc while ArcFrame uses WritePixelFast to draw the arc.
; With an angle of 180 degrees both functions perform with similar speed.
; Decreasing the angle will make ArcLine faster while increasing the angle will make ArcFrame faster.
;
; Have fun! =)
;

;
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;  ArcLine function
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;
; Parameters:
;
;   PosX , PosY - Absolute center of arc's circle on screen.
;   Radius# - Arc/pie radius, e.g. distance from center.
;   Angle# - Relative angle of arc/pie. Allowed values between 0 and 360.
;   AngleOffset# - (Optional) absolute angle offset of arc/pie. Allowed values in range of 0 to 360. By default 0 (top).
;
; Notes:
;
;   The function assumes that an angle of 0 degrees means up and a positive angle increment is clockwise.
;   This function uses Line on the current drawing buffer.
;   Range checking is performed by Blitz.
;

  Function ArcLine ( PosX , PosY , Radius# , Angle# , OffsetAngle# = 0 )

    ;= Find first (offset) position on arc
    VectorX# = Sin ( OffsetAngle ) * Radius
    VectorY# = Sin ( OffsetAngle - 90 ) * Radius

    ;= Update previous position
    LastVectorX# = VectorX
    LastVectorY# = VectorY

    Repeat
      CurrentAngle = CurrentAngle + 1

      ;= Find next (angle) position on arc
      PixelAngle = OffsetAngle + CurrentAngle
      VectorX# = Sin ( PixelAngle ) * Radius
      VectorY# = Sin ( PixelAngle - 90 ) * Radius

      ;= Draw line from previous to next position
      Line PosX + LastVectorX , PosY + LastVectorY , PosX + VectorX , PosY + VectorY

      ;= Update previous position
      LastVectorX = VectorX
      LastVectorY = VectorY
    Until CurrentAngle >= Angle

  End Function

;
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;  ArcFrame function
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;
; Parameters:
;
;   PosX , PosY - Absolute center of arc's circle on screen.
;   Radius# - Arc/pie radius, e.g. distance from center.
;   Angle# - Relative angle of arc/pie. Allowed values between 0 and 360.
;   AngleOffset# - (Optional) absolute angle offset of arc/pie. Allowed values in range of 0 to 360. By default 0 (top).
;   RGB - (Optional) RGB color components. By default white.
;
; Notes:
;
;   The function assumes that an angle of 0 degrees means up and a positive angle increment is clockwise.
;   This function uses WritePixelFast on the current drawing buffer while being locked.
;
;   Warning: No range checking is performed.
;

  Function ArcFrame ( PosX , PosY , Radius# , Angle# , AngleOffset# = 0 , RGB = $FFFFFFFF )

    LockBuffer

    ;= Within square radius
    SquareRadius = Radius * Radius
    SquareRadiusPro = Radius * Radius - Radius*2
    For OffsetX = -Radius To Radius
      SquareOffsetX = OffsetX * OffsetX
      For OffsetY = -Radius To Radius
        SquareOffsetY = OffsetY * OffsetY

        ;= Within arc's circle radius
        If SquareOffsetX + SquareOffsetY <= SquareRadius
          If SquareOffsetX + SquareOffsetY >= SquareRadiusPro

            ;= Normalize angle to 0..360 where 0 is up
            PixelAngle = ATan2 ( OffsetY , OffsetX ) + 90
            If PixelAngle >= 180 Then PixelAngle = PixelAngle - 360
            If PixelAngle < 0 Then PixelAngle = PixelAngle + 360

            ;= Match angle to range for comparison
            If AngleOffset + Angle >= 360
              If PixelAngle < Angle
                If Angle >= 180
                  If PixelAngle < AngleOffset
                    PixelAngle = PixelAngle + 360
                  End If
                Else
                  PixelAngle = PixelAngle + 360
                End If
              End If
            End If

            ;= Range comparison; angle in range
            If PixelAngle > AngleOffset
              If PixelAngle < AngleOffset + Angle

                ;= Draw pixel
                WritePixelFast OffsetX + PosX , OffsetY + PosY , RGB

              End If
            End If

          End If
        End If

      Next
    Next

    UnlockBuffer

  End Function

;
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;  Test program
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;

  Const ScreenSizeX = 800
  Const ScreenSizeY = 600

  Const ScreenMidX = ScreenSizeX / 2
  Const ScreenMidY = ScreenSizeY / 2

  Graphics ScreenSizeX , ScreenSizeY

  ;= Demonstrational values
  AngleIncrement# =   0.3
  OffsetAngle#    =   0.0
  Angle#          = 171.1  ; Smaller is faster with Line, Larger is faster with WritePixelFast
  Radius#         = 101.1

  Manual = False  ; Manual (per pixel) drawing e.g. ArcFrame (pixel) or ArcLine (line)

  SeedRnd MilliSecs ()
  SetBuffer BackBuffer ()

  Repeat

    ;= Parameters
    If KeyHit ( 57 )  ; Space
      Manual = Not Manual
    End If

    Text 0 , 0  , "Press <Space> to change drawing modes"
    Text 0 , 20 , "Manual drawing: " + Manual

    ;= Arc
    OffsetAngle = OffsetAngle + AngleIncrement
    If OffsetAngle >= 360 Then OffsetAngle = OffsetAngle - 360

    Color Rand ( 0 , 255 ) , Rand ( 0 , 255 ) , Rand ( 0 , 255 )
    ColorRGB = Rand ( 0 , $FFFFFF )
    If Manual
      ArcFrame ScreenMidX , ScreenMidY , Radius , Angle , OffsetAngle , ColorRGB
    Else
      ArcLine ScreenMidX , ScreenMidY , Radius , Angle , OffsetAngle
    End If

    ;= FPS
    If MilliSecs () > LastTime + 999
      FPS = Frames
      Frames = 0
      LastTime = MilliSecs ()
    Else
      Frames = Frames + 1
    End If

    Color 255 , 255 , 255
    Text 0 , 40 , "Frames per second: " + FPS

    ;= Display
    Flip False
    Cls

  Until KeyHit ( 1 )  ; Escape

End

;-->