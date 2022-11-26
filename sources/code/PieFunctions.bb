;
; If you find any better and/or other way, optimized, unoptimized, allowing negative angles, some bananas and/or any other more flexible
;   parameters, please please pretty please with sugar on top, let me know! :)
;
; Have fun! =)
;

;
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;  Pie function
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;
; Parameters:
;
;   PosX , PosY - Absolute center of circle on screen.
;   Radius# - Circle/pie radius, the larger, the slower.
;   Angle# - Relative angle of pie. Allowed values between 0 and 360.
;   AngleOffset# - (Optional) absolute angle offset of pie. Allowed value in range of 0 to 360. By default an angle of 0 (top).
;   RGB - (Optional) RGB color components. By default white.
;   RangeChecking - If the square radius of the entire circle is off-screen and RangeChecking is True, the Pie will not be drawn. This
;     parameter is optional and by default set to True.
;   PixelRangeChecking - Draw only those pixels which are within screen boundaries if PixelRangeChecking is True. This parameter is also
;     optional and set to False by default.
;
; Note:
;
;   The function assumes that an angle of 0 degrees means up and a positive angle increment is clockwise.
;   This function uses WritePixelFast on the current drawing buffer while being locked.
;   Range checking is not performed by Blitz, but by the function.
;

  Function Pie ( PosX , PosY , Radius# , Angle# , AngleOffset# = 0 , RGB = $FFFFFFFF , RangeChecking = True , PixelRangeChecking = False )

    If PixelRangeChecking Or RangeChecking
      PieScreenSizeX = GraphicsWidth ()
      PieScreenSizeY = GraphicsHeight ()
    End If

    If RangeChecking
      If PosX - Radius < 0 Or PosX + Radius >= PieScreenSizeX Or PosY - Radius < 0 Or PosY + Radius >= PieScreenSizeY
        Return False
      End If
    End If

    LockBuffer

    ;= Within square radius
    SquareRadius = Radius * Radius
    For OffsetX = -Radius To Radius
      SquareOffsetX = OffsetX * OffsetX
      For OffsetY = -Radius To Radius
        SquareOffsetY = OffsetY * OffsetY

        ;= Within circle radius
        If SquareOffsetX + SquareOffsetY <= SquareRadius

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
              PixelX = OffsetX + PosX
              PixelY = OffsetY + PosY
              If PixelRangeChecking
                If PixelX >= 0
                  If PixelX < PieScreenSizeX
                    If PixelY >= 0
                      If PixelY < PieScreenSizeY
                        WritePixelFast PixelX , PixelY , RGB
                      End If
                    End If
                  End If
                End If
              Else
                WritePixelFast PixelX , PixelY , RGB
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
;  Pie Outline function
; --------------------------------------------------------------------------------------------------------------------------------------- ;
;
; Parameters:
;
;   PosX , PosY - Absolute center of circle on screen.
;   Radius# - Circle/pie radius, the larger, the slower.
;   Angle# - Relative angle of pie. Allowed values between 0 and 360.
;   AngleOffset# - (Optional) absolute angle offset of pie. Allowed value in range of 0 to 360. By default an angle of 0 (top).
;
; Note:
;
;   The function assumes that an angle of 0 degrees means up and a positive angle increment is clockwise.
;   This function uses Line on the current drawing buffer.
;   Range checking is performed by Blitz.
;

  Function PieOutline ( PosX , PosY , Radius# , Angle# , OffsetAngle# = 0 )

    VectorX# = 0
    VectorY# = 0

    ;= Starting point of pie is center of circle
    LastVectorX# = 0
    LastVectorY# = 0

    Repeat
      CurrentAngle = CurrentAngle + 1

      ;= Calculate next position/pixel along the outside of the circle
      PixelAngle = OffsetAngle + CurrentAngle
      VectorX# = Sin ( PixelAngle ) * Radius
      VectorY# = Sin ( PixelAngle - 90 ) * Radius

      ;= Previous position to new position
      Line PosX + LastVectorX , PosY + LastVectorY , PosX + VectorX , PosY + VectorY

      ;= Update previous position
      LastVectorX = VectorX
      LastVectorY = VectorY
    Until CurrentAngle >= Angle

    ;= Close the pie
    Line PosX + LastVectorX , PosY + LastVectorY , PosX , PosY

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
  AngleIncrement# =   1.1
  OffsetAngle#    =   0.0
  PieAngle#       =  11.1
  Radius#         = 101.1

  RangeChecking = False  ; Fast (and imprecise) range checking
  PixelRangeChecking = False  ; In case you want to overlap sides of the screen
  Outline = False  ; Demonstrate either Pie or PieOutline

  SeedRnd MilliSecs ()
  SetBuffer BackBuffer ()

  Repeat

    ;= Parameters
    If KeyHit ( 19 )  ; R
      RangeChecking = Not RangeChecking
    End If
    If KeyHit ( 25 )  ; P
      PixelRangeChecking = Not PixelRangeChecking
    End If
    If KeyHit ( 57 )  ; Space
      Outline = Not Outline
    End If

    Text 0 ,  0 , "Press space to toggle outline"
    Text 0 , 20 , "Pie (R)ange checking: " + RangeChecking
    Text 0 , 30 , "Pie (P)ixel range checking: " + PixelRangeChecking

    ;= Pie
    OffsetAngle = OffsetAngle + AngleIncrement
    If OffsetAngle >= 360 Then OffsetAngle = OffsetAngle - 360

    If Outline
      Color Rand ( 0 , 255 ) , Rand ( 0 , 255 ) , Rand ( 0 , 255 )
      PieOutline ScreenMidX , ScreenMidY , Radius , PieAngle , OffsetAngle
    Else
      ColorRGB = Rand ( 0 , $FFFFFF )
      Pie ScreenMidX , ScreenMidY , Radius , PieAngle , OffsetAngle , ColorRGB , RangeChecking , PixelRangeChecking
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
    Text 0 , 50 , "Frames per second: " + FPS

    ;= Display
    Flip False
    Cls

  Until KeyHit ( 1 )  ; Escape

End

;-->