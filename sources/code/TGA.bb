;======================================================================
;=========================== TGA-библиотека ===========================
;======================================================================
;
;  Описание :
;	LoadTGAImage ( file$ ) - загрузить tga из файла ( поддерживаются как сжатые, так и несжатые файлы. реализован горизонтальный flip ( код 0x20 ).
;	SaveTGAImage ( img%, file$, compressed=1, x_origin=0, y_origin=0  ) - сохранить рисунок img в файл file$, параметр compressed = 1, если необходимо сжать рисунок.
;	SaveTGAImageWithAlpha ( img%, aimg%, file$, compressed=1, x_origin=0, y_origin=0  ) - сохранить рисунок img с альфаканалом в рисунке aimg% в файл file$, параметр compressed = 1, если необходимо сжать рисунок.
;	CreateAlphaImageFromTexture ( tex ) - создает рисунок, содержащий альфаканал текстуры tex
;	SetAlphaImageToTexture ( img, tex ) - задает текстуре tex альфаканал, содержащийся в рисунке img. Внимание : текстура должна быть загружена с флагом (2 + n)!!!
;
;======================================================================
;   сделал   Kvan ( FC :)
;======================================================================

Dim tgaRGB(0)

Const bmp$ = "tmp.bmp", tga_uncompress$ = "tmp_uncompress.tga", tga_compress$ = "tmp_compress.tga"

Graphics 800, 600

; создаем рисунок
img = CreateImage ( 128, 128 )
SetBuffer ImageBuffer ( img )
	ClsColor 100, 100, 100
	Cls
	Color 255, 0, 0
	Rect 10, 50, 100, 70
	Color 0, 255, 0
	Oval 100, 50, 30, 30
	Color 123, 144, 250
	Rect 10, 100, 60, 30
SetBuffer FrontBuffer ( )

;выводим его
SetFont LoadFont("system")
Color 255, 255, 255
Text 10, 5, "исходный рисунок :"
DrawImage img, 10, 30
; BMP
Text 10, 170, "сохряняем в BMP...     Файл : "+bmp
SaveImage ( img, bmp )
Text 10, 190, "сохранили... размер файла = "+FileSize( bmp)/1024+" кб"
; НЕзапакованный TGA
Text 10, 230, "сохряняем в НЕзапакованный TGA...     Файл : "+tga_uncompress
SaveTGAImage ( img, tga_uncompress, 0 )
Text 10, 250, "сохранили... размер файла = "+FileSize( tga_uncompress )/1024+" кб"
; запакованный TGA
Text 10, 290, "сохряняем в запакованный TGA...     Файл : "+tga_compress
SaveTGAImage ( img, tga_compress )
Text 10, 310, "сохранили... размер файла = "+FileSize( tga_compress )/1024+" кб"

WaitKey()
End

Function LoadCompressed_ ( f, img, width, height, BitPerPixel, description )
	a = 0:x = 0:y = 0
	While ( k < width*height )
		BlockInfo = ReadByte ( f )
		compressed = ( BlockInfo And %10000000 )
		NumPixels =  (BlockInfo And %01111111)+1
		If compressed
			r = ReadByte ( f )
			g = ReadByte ( f )
			b = ReadByte ( f )
			If BitPerPixel=32 a = ReadByte ( f )
			For j = 1 To NumPixels
				If description = $20
					WritePixelFast x, y,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				Else
					WritePixelFast x, height-y-1,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				End If
				x = x + 1
				If x>=width x = 0 : y = y + 1
			Next
		Else
			For j = 1 To NumPixels
				r = ReadByte ( f )
				g = ReadByte ( f )
				b = ReadByte ( f )
				If BitPerPixel=32 a = ReadByte ( f )
				If description = $20
					WritePixelFast x, y,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				Else
					WritePixelFast x, height-y-1,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				End If
				x = x + 1
				If x>=width x = 0 : y = y + 1
			Next
		End If
		k = k + NumPixels
	Wend
End Function

Function LoadUncompressed_ ( f, img, width, height, BitPerPixel, description )
	a = 0:x = 0:y = 0
	For k = 0 To height-1
		For j = 0 To width-1
			r = ReadByte ( f )
			g = ReadByte ( f )
			b = ReadByte ( f )
			If BitPerPixel=32 a = ReadByte ( f )
				If description = $20
					WritePixelFast j, k,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				Else
					WritePixelFast j, height-k-1,  r + g Shl 8 + b Shl 16 + a Shl 24, ImageBuffer ( img )
				End If
		Next
	Next
End Function

Function CreateAlphaImageFromTexture ( tex )
	width = TextureWidth ( tex )
	height = TextureHeight ( tex )
	img = CreateImage ( width, height ) 
	LockBuffer TextureBuffer (tex)
	LockBuffer ImageBuffer (img)
	For k = 0 To height-1
		For j = 0 To width-1
			argb = ReadPixelFast ( j, k, TextureBuffer ( tex ) )
			a = (argb Shr 24) And %11111111
			WritePixelFast j, k, a + a Shl 8 + a Shl 16, ImageBuffer ( img )
		Next
	Next
	UnlockBuffer ImageBuffer (img)
	UnlockBuffer TextureBuffer (tex)
	Return img
End Function

Function SetAlphaImageToTexture ( img, tex )
	width = TextureWidth ( tex )
	height = TextureHeight ( tex )
	If (ImageWidth(img)<>width) Or (ImageHeight(img)<>height) Return
	LockBuffer TextureBuffer (tex)
	LockBuffer ImageBuffer (img)
	For k = 0 To height-1
		For j = 0 To width-1
			rgb% = ReadPixelFast ( j, k, TextureBuffer ( tex ) ) And $00FFFFFF
			b% = ReadPixelFast ( j, k, ImageBuffer ( img ) ) And 255
			WritePixelFast j, k, (b Shl 24) Or rgb, TextureBuffer ( tex )
		Next
	Next
	UnlockBuffer ImageBuffer (img)
	UnlockBuffer TextureBuffer (tex)
End Function

Function LoadTGAImage ( file$ )
	f=ReadFile ( file$ )
	idLength = ReadByte ( f )
	ColorMap = ReadByte ( f )
	DataType = ReadByte ( f )
	ColorMapInfo = ReadInt ( f )
	ColorMapInfo = ReadByte ( f )
	x_origin = ReadShort ( f )
	y_Origin = ReadShort ( f )
	width = ReadShort ( f )
	height = ReadShort ( f )
	BitPerPixel = ReadByte ( f )
	description = ReadByte ( f )
	For k = 1 To idLength
		ReadByte ( f )
	Next
	img = CreateImage ( width, height )
	LockBuffer ImageBuffer ( img )
	If DataType = 10
		LoadCompressed_ ( f, img, width, height, BitPerPixel, description )
	Else
		LoadUncompressed_ ( f, img, width, height, BitPerPixel, description )
	End If
	UnlockBuffer ImageBuffer ( img )
	CloseFile f
	Return img
End Function

Function SaveTGAImage ( img%, file$, compressed=1, x_origin=0, y_origin=0  )
	SaveTGAImageWithAlpha ( img, 0, file, compressed, x_origin, y_origin  )
End Function

Function SaveTGATexture ( img%, file$, compressed=1, x_origin=0, y_origin=0  )
	If compressed
		SaveTGATextureCompressed ( img, file,  x_origin, y_origin )
	Else
		SaveTGATextureUncompressed ( img, file,  x_origin, y_origin )
	End If
End Function

Function SaveTGAImageWithAlpha ( img%, aimg%, file$, compressed=1, x_origin=0, y_origin=0  )
	If compressed
		SaveTGAImageCompressed ( img, aimg, file,  x_origin, y_origin )
	Else
		SaveTGAImageUncompressed ( img, aimg, file,  x_origin, y_origin )
	End If
End Function

Function SaveTGAImageCompressed ( img, aimg, file$, x_origin=0, y_origin=0 )
	width = ImageWidth ( img )
	height = ImageHeight ( img )
	f = WriteFile ( file$ )
	WriteShort ( f, 0 )
	WriteByte ( f, 10 )
	WriteInt ( f, 0 )
	WriteByte ( f, 0 )
	WriteShort ( f, x_origin )
	WriteShort ( f, y_origin )	
	WriteShort ( f, width )
	WriteShort ( f, height )
	bits=24 + 8*Sgn(aimg)
	WriteByte ( f, bits )
	WriteByte ( f, 0 )
	Dim tgaRGB ( width*height )
	LockBuffer ImageBuffer ( img )
	If bits=32 LockBuffer ImageBuffer ( aimg )
	i = 0
	For k = height-1 To 0 Step -1
		For j = 0 To width-1
			tgaRGB( i ) = ReadPixelFast ( j, k, ImageBuffer ( img ) )
			If bits=32
				b% = ReadPixelFast ( j, k, ImageBuffer ( aimg ) ) And 255
				tgaRGB ( i ) = ( b Shl 24) Or ( tgaRGB ( i ) And $00FFFFFF )
			End If
			i = i + 1
		Next
	Next
	If bits=32 UnlockBuffer ImageBuffer ( aimg )
	UnlockBuffer ImageBuffer ( img )
	start=0
	k=0
	compress=0
	tgaRGB(width*height) = -1
	While k<width*height
		If tgaRGB ( k ) <> tgaRGB ( k+1 )
			If (k-start>0) And (compress)
				WriteByte ( f, (k-start) Or %10000000 )
				WriteByte ( f, tgargb(k) And 255 )
				WriteByte ( f, (tgargb(k) Shr 8) And 255 )
				WriteByte ( f, (tgargb(k) Shr 16) And 255 )
				If bits=32 WriteByte  ( f, (tgargb(k) Shr 24) And 255 )
				start = k+1
				compress=0
			End If
		Else
			If (k-start>0) And (Not compress)
				WriteByte ( f, k-start-1 )
				For j = start To k-1
					WriteByte ( f, tgargb(j) And 255 )
					WriteByte ( f, (tgargb(j) Shr 8) And 255 )
					WriteByte ( f, (tgargb(j) Shr 16) And 255 )
					If bits=32 WriteByte  ( f, (tgargb(j) Shr 24) And 255 )
				Next
				start = k
			End If
			compress=1
		End If
		If compress
			If k-start=127
				WriteByte ( f, (k-start) Or %10000000 )
				WriteByte ( f, tgargb(k) And 255 )
				WriteByte ( f, (tgargb(k) Shr 8) And 255 )
				WriteByte ( f, (tgargb(k) Shr 16) And 255 )
				If bits=32 WriteByte ( f, (tgargb(k) Shr 24) And 255 )
				start = k+1
			End If
		Else
			If k-start-1=127
				WriteByte ( f, k-start-1 )
				For j = start To k-1
					WriteByte ( f, tgargb(j) And 255 )
					WriteByte ( f, (tgargb(j) Shr 8) And 255 )
					WriteByte ( f, (tgargb(j) Shr 16) And 255 )
					If bits=32 WriteByte ( f, (tgargb(j) Shr 24) And 255 )
				Next
				start = k
			End If
		End If
		k = k + 1
	Wend
	If (k-start>0) And (Not compress)
		WriteByte ( f, k-start-1 )
		For j = start To k-1
			WriteByte ( f, tgargb(j) And 255 )
			WriteByte ( f, (tgargb(j) Shr 8) And 255 )
			WriteByte ( f, (tgargb(j) Shr 16) And 255  )					
			If bits=32 WriteByte  ( f, (tgargb(j) Shr 24) And 255 )
		Next		
	End If
	CloseFile f
End Function

Function SaveTGATextureCompressed ( img, file$, x_origin=0, y_origin=0 )
	width = TextureWidth ( img )
	height = TextureHeight ( img )
	f = WriteFile ( file$ )
	WriteShort ( f, 0 )
	WriteByte ( f, 10 )
	WriteInt ( f, 0 )
	WriteByte ( f, 0 )
	WriteShort ( f, x_origin )
	WriteShort ( f, y_origin )	
	WriteShort ( f, width )
	WriteShort ( f, height )
	WriteByte ( f, 32 )
	WriteByte ( f, 0 )
	Dim tgaRGB ( width*height )
	LockBuffer TextureBuffer ( img )
	i = 0
	For k = height-1 To 0 Step -1
		For j = 0 To width-1
			tgaRGB( i ) = ReadPixelFast ( j, k, TextureBuffer ( img ) )
			i = i + 1
		Next
	Next
	UnlockBuffer TextureBuffer ( img )
	start=0
	k=0
	compress=0
	tgaRGB(width*height) = -1
	While k<width*height
		If tgaRGB ( k ) <> tgaRGB ( k+1 )
			If (k-start>0) And (compress)
				WriteByte ( f, (k-start) Or %10000000 )
				WriteInt ( f, tgargb(k) )
				start = k+1
				compress=0
			End If
		Else
			If (k-start>0) And (Not compress)
				WriteByte ( f, k-start-1 )
				For j = start To k-1
					WriteInt ( f, tgargb(j) )
				Next
				start = k
			End If
			compress=1
		End If
		If compress
			If k-start=127
				WriteByte ( f, (k-start) Or %10000000 )
				WriteInt ( f, tgargb(k) )
				start = k+1
			End If
		Else
			If k-start-1=127
				WriteByte ( f, k-start-1 )
				For j = start To k-1
					WriteInt ( f, tgargb(j) )
				Next
				start = k
			End If
		End If
		k = k + 1
	Wend
	If (k-start>0) And (Not compress)
		WriteByte ( f, k-start-1 )
		For j = start To k-1
				WriteInt ( f, tgargb(j) )
		Next		
	End If
	CloseFile f
End Function

Function SaveTGAImageUncompressed ( img, aimg, file$, x_origin=0, y_origin=0 )
	width = ImageWidth ( img )
	height = ImageHeight ( img )
	f = WriteFile ( file$ )
	WriteShort ( f, 0 )
	WriteByte ( f, 2 )
	WriteInt ( f, 0 )
	WriteByte ( f, 0 )
	WriteShort ( f, x_origin )
	WriteShort ( f, y_origin )	
	WriteShort ( f, width )
	WriteShort ( f, height )
	bits=24 + 8*Sgn(aimg)
	WriteByte ( f, bits )
	WriteByte ( f, 0 )
	LockBuffer ImageBuffer ( img )
	If bits=32 LockBuffer ImageBuffer ( aimg )
	For k = 0 To height-1
		For j = 0 To width-1
			argb = ReadPixelFast ( j, height-k-1, ImageBuffer ( img ) )
			WriteByte ( f, argb And 255 )
			WriteByte ( f, (argb Shr 8) And 255 )
			WriteByte ( f, (argb Shr 16) And 255 )
			If bits=32
				a = ReadPixelFast ( j, height-k-1, ImageBuffer ( aimg ) )
				WriteByte ( f, a And 255 )
			End If
		Next
	Next
	If bits=32 UnlockBuffer ImageBuffer ( aimg )
	UnlockBuffer ImageBuffer ( img )
	CloseFile f
End Function

Function SaveTGATextureUncompressed ( img, file$, x_origin=0, y_origin=0 )
	width = TextureWidth ( img )
	height = TextureHeight ( img )
	f = WriteFile ( file$ )
	WriteShort ( f, 0 )
	WriteByte ( f, 2 )
	WriteInt ( f, 0 )
	WriteByte ( f, 0 )
	WriteShort ( f, x_origin )
	WriteShort ( f, y_origin )	
	WriteShort ( f, width )
	WriteShort ( f, height )
	WriteByte ( f, 32 )
	WriteByte ( f, 0 )
	LockBuffer TextureBuffer ( img )
	For k = 0 To height-1
		For j = 0 To width-1
			argb = ReadPixelFast ( j, height-k-1, TextureBuffer ( img ) )
			WriteInt(f, argb)
		Next
	Next
	UnlockBuffer TextureBuffer ( img )
	CloseFile f
End Function