;Save Image DDS, on 13/9/06

Graphics3D 640,480,0,2
AppTitle "Save Image DDS"
SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()
RotateEntity light,45,45,0

image=MakeTestImage(128,128) ;new image

;FreeImage image
;image=LoadImage("test.bmp") ;load bmp
;image=LoadImage("test.png") ;load png
image=LoadImage("wcrate.jpg") ;load jpg

alpha=MakeTestAlpha(ImageWidth(image),ImageHeight(image))

filename$="test.dds" : format=5 ;: alpha=0
ok=SaveImageDDS(image,filename$,format,alpha)

cube=CreateCube()
PositionEntity cube,0,0,4
If format=1 Then flags=1+4 Else flags=1+2 ;masked/alpha flags
tex=LoadTexture(filename$,flags) ;load new dds file
If format=1 Or format=3 Or format=5 EntityTexture cube,tex,0,0

cube2=CreateCube()
PositionEntity cube2,0,2,8

While Not KeyHit(1)
 RenderWorld()

 TurnEntity cube,0.4,0.3,0.2
 TurnEntity cube2,-0.4,-0.3,-0.2

 If space
  DrawImage image,50,50
  If alpha DrawImage alpha,ImageWidth(image)+100,50
 EndIf
 If KeyHit(57) Then space=Not space ;space key

 Text 0,0,"Hit space to show image and alpha maps"
 Text 0,20,"ok="+ok+" file="+filename+" format="+format+" alpha="+alpha

 Flip
Wend

Function MakeTestImage(width,height)

 Local image,x,y,rgb
 image=CreateImage(width,height)
 LockBuffer(ImageBuffer(image))
 For y=0 To ImageHeight(image)-1
  For x=0 To ImageWidth(image)-1
   rgb=y+(y*256)+(x*256^2) ;gradient color
   WritePixelFast x,y,rgb,ImageBuffer(image)
  Next
 Next
 UnlockBuffer(ImageBuffer(image))
 SetBuffer ImageBuffer(image)
 Color 255,255,255 : Oval 40,40,30,30
 Color 0,0,0 : Text 50,50,"DXTC" : Color 255,255,255
 SetBuffer BackBuffer()
 Return image

End Function

Function MakeTestAlpha(width,height)

 Local alpha,x,y,rgb
 alpha=CreateImage(width,height)
 LockBuffer(ImageBuffer(alpha))
 For y=0 To ImageHeight(alpha)-1
  For x=0 To ImageWidth(alpha)-1
   rgb=(y*2)+((y*2)*256)+((y*2)*256^2) ;grayscale
   If Not x Mod 8 Then rgb=$7F7F7F ;grid lines
   If Not y Mod 8 Then rgb=$7F7F7F
   If Not x Mod 16 Then rgb=$FFFFFF
   If Not y Mod 16 Then rgb=$FFFFFF
   WritePixel x,y,rgb,ImageBuffer(alpha)
  Next
 Next
 UnlockBuffer(ImageBuffer(alpha))
 Return alpha

End Function

Function SaveImageDDS(image,filename$,format=0,alpha=0)
 ;Top-level function to save a given image as a DDS file
 ;image=image handle, filename$, format=optional compression format 0..5
 ;alpha=optional alpha image handle, zero if no alpha required

 Local imagebuf,alphabuf,width,height
 imagebuf=ImageBuffer(image)
 width=ImageWidth(image)
 height=ImageHeight(image)
 If alpha Then alphabuf=ImageBuffer(alpha) Else alphabuf=imagebuf
 Return SaveDDS(imagebuf,alphabuf,width,height,filename$,format,alpha)

End Function

Function SaveDDS(imagebuf,alphabuf,width,height,filename$,format,alpha)
 ;Saves a given image as a DDS file
 ;imagebuf/alphabuf=image/alpha buffer handles, width/height=image size
 ;filename$, format=compression format, alpha=alpha flag

 Local dwwidth,dwheight,flags1,flags2,caps1,caps2,bpp,pitch,sizebytes
 Local bsize,bindex,fourcc,hdds,i,x,y,offset,ix,iy,argb
 Local color0,color1,color2,color3,color4,color5,color6,color7
 Local d0,d1,d2,d3,d4,d5,d6,d7,texel,file

 ;dwFlags constants
 Local DDSD_CAPS=$00000001,DDSD_HEIGHT=$00000002,DDSD_WIDTH=$00000004
 Local DDSD_PITCH=$00000008,DDSD_PIXELFORMAT=$00001000
 Local DDSD_MIPMAPCOUNT=$00020000,DDSD_LINEARSIZE=$00080000
 Local DDSD_DEPTH=$00800000,DDPF_ALPHAPIXELS=$00000001
 Local DDPF_FOURCC=$00000004,DDPF_RGB=$00000040
 ;dwCaps1 constants
 Local DDSCAPS_COMPLEX=$00000008,DDSCAPS_TEXTURE=$00001000
 Local DDSCAPS_MIPMAP=$00400000
 ;dwCaps2 constants
 Local DDSCAPS2_CUBEMAP=$00000200,DDSCAPS2_CUBEMAP_POSITIVEX=$00000400
 Local DDSCAPS2_CUBEMAP_NEGATIVEX=$00000800
 Local DDSCAPS2_CUBEMAP_POSITIVEY=$00001000
 Local DDSCAPS2_CUBEMAP_NEGATIVEY=$00002000
 Local DDSCAPS2_CUBEMAP_POSITIVEZ=$00004000
 Local DDSCAPS2_CUBEMAP_NEGATIVEZ=$00008000,DDSCAPS2_VOLUME=$00200000

 ;calculate DWORD-aligned width and height, multiple of 4
 dwwidth=(width+3)/4*4
 dwheight=(height+3)/4*4

 ;default flags for all formats
 flags1=DDSD_CAPS Or DDSD_HEIGHT Or DDSD_WIDTH Or DDSD_PIXELFORMAT
 caps1=DDSCAPS_TEXTURE
 If format<=0 Or format>5 ;uncompressed
  flags1=flags1 Or DDSD_PITCH
  flags2=DDPF_RGB
  bpp=24
  pitch=dwwidth*(bpp/8) ;DWORD-aligned scanline
  sizebytes=pitch*dwheight
 Else ;compressed
  flags1=flags1 Or DDSD_LINEARSIZE
  flags2=DDPF_FOURCC
  sizebytes=(dwwidth/4)*(dwheight/4)*8
  If format>1 sizebytes=sizebytes*2
  pitch=sizebytes
  bsize=2 : bindex=0 : If format>1 Then bsize=4 : bindex=8 ;block values
  If format=1 Then fourcc=MakeFourCC("D","X","T","1")
  If format=2 Then fourcc=MakeFourCC("D","X","T","2")
  If format=3 Then fourcc=MakeFourCC("D","X","T","3")
  If format=4 Then fourcc=MakeFourCC("D","X","T","4")
  If format=5 Then fourcc=MakeFourCC("D","X","T","5")
 EndIf

 hdds=CreateBank(128+sizebytes) ;bank to store DDS

 ;Magic Value, DWORD
 PokeInt hdds,0,MakeFourCC("D","D","S"," ") ;dwMagic, "DDS "
 ;Surface Format Header, DDSURFACEDESC2 structure
 PokeInt hdds,4,124 ;dwSize, sizeof(DDSURFACEDESC2)
 PokeInt hdds,8,flags1 ;dwFlags, flags to indicate valid fields
 PokeInt hdds,12,dwheight ;dwHeight, image height in pixels
 PokeInt hdds,16,dwwidth ;dwWidth, image width in pixels
 PokeInt hdds,20,pitch ;dwPitchOrLinearSize, pitch or linear size
 PokeInt hdds,24,0 ;dwDepth, volume textures not supported until DX 8.0
 PokeInt hdds,28,0 ;dwMipMapCount, for items with mipmap levels
 For i=1 To 11
  PokeInt hdds,(i*4)+28,0 ;dwReserved[11]
 Next
 ;DDPIXELFORMAT structure
 PokeInt hdds,76,32 ;dwSize, sizeof(DDPIXELFORMAT)
 PokeInt hdds,80,flags2 ;dwFlags, flags to indicate valid fields
 PokeInt hdds,84,fourcc ;dwFourCC
 PokeInt hdds,88,bpp ;dwRGBBitCount
 PokeInt hdds,92,$00FF0000 ;dwRBitMask
 PokeInt hdds,96,$0000FF00 ;dwGBitMask
 PokeInt hdds,100,$000000FF ;dwBBitMask
 PokeInt hdds,104,$FF000000 ;dwRGBAlphaBitMask
 ;DDCAPS2 structure
 PokeInt hdds,108,caps1 ;dwCaps1, flags to indicate valid fields
 PokeInt hdds,112,caps2 ;dwCaps2, flags to indicate valid fields
 For i=1 To 2
  PokeInt hdds,(i*4)+112,0 ;dwReserved[2]
 Next
 PokeInt hdds,124,0 ;dwReserved2
 ;Main Surface Data, BYTE bData1[]
 LockBuffer(imagebuf)
 If format<=0 Or format>5 ;uncompressed, 24-bit
  For y=0 To height-1
   offset=(pitch*y)+128 ;next DWORD-aligned scanline
   For x=0 To width-1
    argb=ReadPixelFast(x,y,imagebuf)
    PokeByte hdds,offset+(x*3),argb And $000000FF ;b
    PokeByte hdds,offset+(x*3)+1,(argb And $0000FF00) Shr 8 ;g
    PokeByte hdds,offset+(x*3)+2,(argb And $00FF0000) Shr 16 ;r
   Next
  Next
 Else ;compressed
  For y=0 To dwheight-1 Step 4
   offset=(dwwidth/4)*(y*bsize)+128 ;next block-aligned scanline
   For x=0 To dwwidth-1 Step 4
    If format=2 Or format=3 ;DXT2,DXT3
    ;find color in alpha block and set each alpha texel
     For iy=0 To 3
      For ix=0 To 3
       If x+ix>width-1 Or y+iy>height-1 ;out of bounds
        argb=0 ;black
       Else
        argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;use alpha map
       EndIf
       i=ColorHighest(argb)/17 : If i>15 Then i=15 ;alpha color 0..15
       texel=PeekShort(hdds,offset+(x*bsize)+(iy*2)) Or (i Shl ix*4)
       PokeShort hdds,offset+(x*bsize)+(iy*2),texel ;wAlphaTexels[4]
      Next
     Next
    EndIf
    If format=4 Or format=5 ;DXT4,DXT5
     ;find highest and lowest colors in alpha block
     color0=0 : color1=$FFFFFFFF ;color0 highest
     For iy=0 To 3
      For ix=0 To 3
       If x+ix>width-1 Or y+iy>height-1 ;out of bounds
        argb=0 ;black
       Else
        argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;use alpha map
       EndIf
        If ColorHighest(argb)>ColorHighest(color0) Then color0=argb
        If ColorHighest(argb)<ColorHighest(color1) Then color1=argb
      Next
     Next
     PokeByte hdds,offset+(x*bsize),ColorHighest(color0) ;bAlpha0
     PokeByte hdds,offset+(x*bsize)+1,ColorHighest(color1) ;bAlpha1
     ;set each alpha texel in block to closest alpha
     color0=ColorHighest(color0) : color1=ColorHighest(color1)
     For iy=0 To 3
      For ix=0 To 3
       If x+ix>width-1 Or y+iy>height-1 ;out of bounds
        argb=0 ;black
       Else
        argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;use alpha map
       EndIf
       If color0>color1 ;8-alpha block
        color2=((6*color0)+color1)/7
        color3=((5*color0)+(2*color1))/7
        color4=((4*color0)+(3*color1))/7
        color5=((3*color0)+(4*color1))/7
        color6=((2*color0)+(5*color1))/7
        color7=(color0+(6*color1))/7
       Else ;6-alpha block
        color2=((4*color0)+color1)/5
        color3=((3*color0)+(2*color1))/5
        color4=((2*color0)+(3*color1))/5
        color5=(color0+(4*color1))/5
        color6=0
        color7=255
       EndIf
       d0=Abs(color0-ColorHighest(argb)) ;get differences
       d1=Abs(color1-ColorHighest(argb))
       d2=Abs(color2-ColorHighest(argb))
       d3=Abs(color3-ColorHighest(argb))
       d4=Abs(color4-ColorHighest(argb))
       d5=Abs(color5-ColorHighest(argb))
       d6=Abs(color6-ColorHighest(argb))
       d7=Abs(color7-ColorHighest(argb))
       i=0 : If d1<d0 Then d0=d1 : i=1 ;find closest color
       If d2<d0 Then d0=d2 : i=2
       If d3<d0 Then d0=d3 : i=3
       If d4<d0 Then d0=d4 : i=4
       If d5<d0 Then d0=d5 : i=5
       If d6<d0 Then d0=d6 : i=6
       If d7<d0 Then d0=d7 : i=7
       If iy<2 ;upper 24bit-block
        texel=PeekInt(hdds,offset+(x*bsize)+2) And $00FFFFFF
        If iy=0 Then texel=texel Or (i Shl (ix*3))
        If iy=1 Then texel=texel Or (i Shl ((ix*3)+12))
        PokeInt hdds,offset+(x*bsize)+2,texel And $00FFFFFF
       Else ;lower 24bit-block
        texel=PeekInt(hdds,offset+(x*bsize)+5) And $00FFFFFF
        If iy=2 Then texel=texel Or (i Shl (ix*3))
        If iy=3 Then texel=texel Or (i Shl ((ix*3)+12))
        PokeInt hdds,offset+(x*bsize)+5,texel And $00FFFFFF
       EndIf
      Next
     Next
    EndIf
    ;find highest and lowest colors in texel block
    ;better algorithm would find the most common highest/lowest colors
    color0=0 : color1=$FFFFFFFF ;color0 highest
    For iy=0 To 3
     For ix=0 To 3
      If x+ix>width-1 Or y+iy>height-1 ;out of bounds
       argb=0 ;black
      Else
       argb=ReadPixelFast(ix+x,iy+y,imagebuf)
      EndIf
      If ColorTotal(argb)>ColorTotal(color0) Then color0=argb
      If ColorTotal(argb)<ColorTotal(color1) Then color1=argb
     Next
    Next
    If format=1 And alpha<>0 ;DXT1a, using alpha bit
     i=color0 : color0=color1 : color1=i ;switch order, color1 highest
    EndIf
    PokeShort hdds,offset+(x*bsize)+bindex,Color565(color0) ;wColor0
    PokeShort hdds,offset+(x*bsize)+bindex+2,Color565(color1) ;wColor1
    ;set each texel in block to closest color
    color0=ColorTotal(color0) : color1=ColorTotal(color1)
    For iy=0 To 3
     For ix=0 To 3
      If x+ix>width-1 Or y+iy>height-1 ;out of bounds
       argb=0 ;black
      Else
       argb=ReadPixelFast(ix+x,iy+y,imagebuf)
      EndIf
      If color0>color1 ;four-color block
       color2=((2*color0)+color1)/3
       color3=(color0+(2*color1))/3
      Else ;three-color block
       color2=(color0+color1)/2
       color3=3*16 ;max transparent color
      EndIf
      d0=Abs(color0-ColorTotal(argb)) ;get differences
      d1=Abs(color1-ColorTotal(argb))
      d2=Abs(color2-ColorTotal(argb))
      d3=Abs(color3-ColorTotal(argb))
      i=0 : If d1<d0 Then d0=d1 : i=1 ;find closest color
      If d2<d0 Then d0=d2 : i=2
      If d3<d0 Then d0=d3 : i=3
      If color0>color1 And Abs(color2-color3)<8
       If i=3 Then i=2 ;close and wrong order so use color2
      EndIf
      If format=1 And alpha<>0 ;DXT1a, using alpha bit
       If i=3 Then i=2 ;no color3 so use color2
      EndIf
      If x+ix>width-1 Or y+iy>height-1 ;out of bounds
       If color0<color1 Then i=0 Else i=1 ;find lowest color
      EndIf
      texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy) Or (i Shl ix*2)
      PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel ;bTexels[4]
     Next
    Next
    ;find color in texel block and set each alpha texel
    If format=1 And alpha<>0 ;DXT1a, using alpha bit
     For iy=0 To 3
      For ix=0 To 3
       If x+ix>width-1 Or y+iy>height-1 ;out of bounds
        argb=0 ;black
       Else
        argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;use alpha map
       EndIf
       color3=3*16 ;max transparent color
       If ColorTotal(argb)<color3 ;set alpha texel
        If Not x+ix>width-1 Or y+iy>height-1 ;not out of bounds
         texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy) Or (3 Shl ix*2)
         PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel ;bTexels[4]
        EndIf
       EndIf
      Next
     Next
    EndIf
   Next
  Next
  ;Attached Surfaces Data, BYTE bData2[]
  ;complex/mipmaps stuff
 EndIf
 UnlockBuffer(imagebuf)

 ;write DDS bank to file
 file=WriteFile(filename$)
 If Not file FreeBank hdds : Return False ;fail code
 WriteBytes hdds,file,0,128+sizebytes
 CloseFile file
 FreeBank hdds
 Return True ;success code

End Function

Function MakeFourCC(c0$,c1$,c2$,c3$)

 Return (Asc(c0$)+(Asc(c1$) Shl 8)+(Asc(c2$) Shl 16)+(Asc(c3$) Shl 24))

End Function

Function ColorHighest(argb)

 Local r,g,b,a
 r=(argb And $00FF0000) Shr 16
 g=(argb And $0000FF00) Shr 8
 b=(argb And $000000FF)
 If r>g Then a=r Else a=g
 If b>a Then a=b
 Return a

End Function

Function ColorTotal(argb)

 Local r,g,b
 r=(argb And $00FF0000) Shr 16
 g=(argb And $0000FF00) Shr 8
 b=(argb And $000000FF)
 Return (r+g+b) ;0..255*3

End Function

Function Color565(argb)

 Local r,g,b
 r=(argb And $00FF0000) Shr 16 : r=(r*31/255) Shl 11 ;bits 11..15
 g=(argb And $0000FF00) Shr 8 : g=(g*63/255) Shl 5 ;bits 5..10
 b=(argb And $000000FF) : b=b*31/255 ;bits 0..4
 Return (r+g+b)

End Function