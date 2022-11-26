;Image to Ascii example

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

image=LoadImage("spotlight2.png") ;source

ascii=CreateImage(ImageWidth(image),ImageHeight(image)) ;target

ImageToChars(image,ascii,1)

ClsColor 0,0,0 : Color 255,255,255 ;reset colors

While Not KeyHit(1)

 Cls
 DrawImage ascii,50,50

 Text 0,0,"image="+image+" ascii="+ascii

Flip
Wend

Function ImageToChars(source%, target%, colors% = True)
 Local chars$ = " .,:`;'^" + Chr(34) + "<>\-/_!~=?)(|t+i7{j}lv]%[1cf32Jr$CuIyz9o6wTna5sk&VY40LO8mG*hexSgApqbZdUPQFDXKW#RNEHBM@"
 Local w% = 0, char$ = "", argb%, red%, green%, blue%
 
 For i = 1 To Len(chars$)
  If StringWidth(Mid$(chars$, i, 1)) > w% Then w% = StringWidth(Mid$(chars$, i, 1))
 Next
 
 ;LockBuffer ImageBuffer(source%)
 Local buffer% = GraphicsBuffer()
 SetBuffer ImageBuffer(target%)
 For y = 0 To (ImageHeight(source%) / FontHeight()) - 1
  For x = 0 To (ImageWidth(source%) / w%) - 1
   argb% = ReadPixel(x * w%, y * FontHeight(), ImageBuffer(source%))
    red% = (argb Shr 16) And $FF
    green% = (argb Shr 8) And $FF
    blue% = argb And $FF
   col# = (Float red + green + blue) / (3 * 255)
   
   If colors% Then Color red, green, blue
   
   char$ = Mid$(chars$, (Len(chars$) - 1) * col# + 1, 1)
   Text x% * w%, y% * FontHeight(), char$
  Next
 Next
 SetBuffer buffer%
 ;UnlockBuffer ImageBuffer(source%)
End Function
