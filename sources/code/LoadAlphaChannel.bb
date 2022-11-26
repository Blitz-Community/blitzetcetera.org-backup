; example , texture flags have to be used individually, but
; of course the Flag 2 (alpha) must be set.
; Both textures need to have the same size.

Graphics3D 800,600
PositionEntity CreateCamera(),0,0,-4
RotateEntity CreateLight(),45,0,45

; loading the RGB channels from a lossy compressed JPG:
tex=LoadTexture("mground.jpg",256 Or 16 Or 32 Or 2)
; Assigning the Alpha Channel form a 8- Bit PNG:
LoadAlphaChannel(tex,"heightmap.jpg")
c=CreateCube()
EntityTexture c,tex

Repeat
 TurnEntity c,.2,.5,.7
 RenderWorld
 Flip
Until KeyHit(1)


Function LoadAlphaChannel(id,file$)
 tex4=LoadTexture(file$,2)
 If tex4<>0
  If TextureWidth(id)=TextureWidth(tex4)
   If TextureHeight(id)=TextureHeight(tex4)
    SetBuffer TextureBuffer(id)
    LockBuffer(TextureBuffer(tex4))
    LockBuffer(TextureBuffer(id))
    For j=0 To TextureHeight(tex4)-1
     For i=0 To TextureWidth(tex4)-1
      argb=ReadPixelFast(i,j,TextureBuffer(tex4)) And $ffffff
      r=(argb Shr 16)And $FF
      g=(argb Shr 8)And $FF
      b=argb And $FF
      grey= ((r+g+b)/3)
      If grey > 255 Then grey=255
      rgb=(ReadPixelFast(i,j,TextureBuffer(id))) And $FFFFFF
      WritePixelFast i,j,rgb Or (grey Shl 24),TextureBuffer(id)
     Next
    Next
    UnlockBuffer(TextureBuffer(tex4))
    UnlockBuffer(TextureBuffer(id))
    SetBuffer BackBuffer()
   EndIf
  EndIf
  FreeTexture tex4
 EndIf
End Function