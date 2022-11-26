; If you look at the bottom of the code you will see what each mask looks like (0=transparent).
; Every mask can be applied to any specified image by calling the supplied function.
; The only backdraw is that the dithered copies would have to be prebuffered.
;
; The code will display a continuously updated 'game screen' with a static image to be 'transparently' drawn on top of it.
; If you have any questions, you can always email me <TheChange@yahoo.com> :)
;

Const ScreenSizeX = 800
Const ScreenSizeY = 600

Graphics ScreenSizeX , ScreenSizeY

  ; The original image to fade to/from
  BlendImage = CreateImage ( ScreenSizeX , ScreenSizeY )
  ; Creating some interesting image
  SetBuffer ImageBuffer ( BlendImage )
  For X = 0 To ScreenSizeX / 2
    ColorValue = X * 255 / ScreenSizeX * 2
    Color ColorValue , 0 , 0
    Y = X * ScreenSizeY / ScreenSizeX
    Oval X , Y , ScreenSizeX - X*2 , ScreenSizeY - Y*2 , True
  Next

  ; Prebuffer dithered copies of the original image
  Dim BlendImages( 6 )
  For Blend = 0 To 6
    BlendImages( Blend ) = CreateMaskImage ( BlendImage , Blend )
  Next

  ; Set demo blend values
  Blend = 0
  BlendDir = 1

  Color 0 , 255 , 0
  SetBuffer BackBuffer ()
  Repeat

    ; Game screen
    For Count = 1 To 100
      Text Rnd ( ScreenSizeX ) , Rnd ( ScreenSizeY ) , Blend , True , True
    Next

    ; Blend overlay
    DrawImage BlendImages( Blend ) , 0 , 0

    ; Alter blend value
    Blend = Blend + BlendDir
    If Blend = 6 Or Blend = 0 Then BlendDir = -BlendDir

    ; Display
    Flip
    Cls

  Until KeyHit ( 1 )

End

;-->

  ; *---------------------------------------------------*
  ; * Create masked image from an image                 *
  ; *---------------------------------------------------*
  ; * Parameters                                        *
  ; *   OriginalImage: Handle of original image to mask *
  ; *   MaskType: Which mask to apply;                  *
  ; *     0: 100% original image                        *
  ; *     1: 25% transparent                            *
  ; *     2: 33% transparent                            *
  ; *     3: 50% transparent                            *
  ; *     4: 66% transparent                            *
  ; *     5: 75% transparent                            *
  ; *     6: 100% transparent                           *
  ; * Returns: Masked image                             *
  ; *---------------------------------------------------*

  Function CreateMaskImage ( OriginalImage , MaskType )

    ; Temporary mask image for data
    Mask = CreateImage ( 4 , 4 )
    ; White is transparent
    MaskImage Mask , 255 , 255 , 255

    ; Data mask select
    Select MaskType
      Case 0 : Restore Mask0
      Case 1 : Restore Mask25
      Case 2 : Restore Mask33
      Case 3 : Restore Mask50
      Case 4 : Restore Mask66
      Case 5 : Restore Mask75
      Case 6 : Restore Mask100
      Default : Restore Mask50
    End Select

    ; Create mask
    For Y = 0 To 3
      For X = 0 To 3
        Read Value
        If Value
          WritePixel X , Y , 0 , ImageBuffer ( Mask )
        Else
          WritePixel X , Y , 16777215 , ImageBuffer ( Mask )
        End If
      Next
    Next

    ; Apply mask to new image
    Image = CopyImage ( OriginalImage )
    SetBuffer ImageBuffer ( Image )
    TileImage Mask
    FreeImage Mask

    Return Image
  End Function

  .Mask0
  Data 0,0,0,0
  Data 0,0,0,0
  Data 0,0,0,0
  Data 0,0,0,0

  .Mask25
  Data 0,1,0,1
  Data 0,0,0,0
  Data 0,1,0,1
  Data 0,0,0,0

  .Mask33
  Data 0,1,0,1
  Data 0,0,1,0
  Data 0,1,0,1
  Data 1,0,0,0

  .Mask50
  Data 0,1,0,1
  Data 1,0,1,0
  Data 0,1,0,1
  Data 1,0,1,0

  .Mask66
  Data 0,1,0,1
  Data 1,1,1,0
  Data 0,1,0,1
  Data 1,0,1,1

  .Mask75
  Data 0,1,0,1
  Data 1,1,1,1
  Data 0,1,0,1
  Data 1,1,1,1

  .Mask100
  Data 1,1,1,1
  Data 1,1,1,1
  Data 1,1,1,1
  Data 1,1,1,1

;
;-->