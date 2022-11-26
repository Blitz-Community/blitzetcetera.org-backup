;Blobby Objects для Blitz3D
;Портировал на Блитз: Солодовниченко Михаил ака MANIAK_dobrii
;Нужна картинка, и установленный графический режим
;В главном цикле BlobBlend(картинка,Х-координата,Y-координата)
;Картинка должна входить в экран по Y, т.е. если хоть кусочек картинки выдет за верхний/нижний край экрана, то прога вылетит


Graphics 640,480,32,2
SetBuffer BackBuffer()

BlobSize = 128
BlobDivider = 64


Global blob = CreateImage(BlobSize,BlobSize)

SetBuffer ImageBuffer(blob)
For i#=1 To BlobSize-1 Step .5
  lev# = i
  lev = lev*lev
  lev = lev/BlobDivider

  Color lev,lev,lev

  Oval i/2,i/2,BlobSize-i,BlobSize-i
  
Next
SetBuffer BackBuffer()



Repeat
Cls
msX = MouseX():msy = MouseY()

BlobBlend(blob,256,176)
BlobBlend(blob,msX-64,msY-64)

Flip
Until KeyHit(1)

End

;Рисует blob в X и Y
Function BlobBlend(Blob,x,y)
BlobBlendEx(BackBuffer(),ImageBuffer(Blob),x,y,ImageWidth(Blob),ImageHeight(blob))
End Function

;Тоже, что и BlobBlend(), но расширенная
;[буфер "под картинкой"][буфер картинки][X-коорд.][Y-коорд.][ширина картинки][высота картинки][прозрачность][маска]
;ширина и высота картинки может быть меньше реальных высоты и ширины
;Цвет, определенный в маске не будет рисоваться
Function BlobBlendEx(Back_Buffer,BlobBuffer,x,y,BlobWidth,BlobHeight,alpha=1500,mask=0)
  Local yy,xx,xn,yn,argb
  Local back_buffer_red, Blob_Buffer_red, result_red, back_buffer_green, Blob_Buffer_green, result_green, back_buffer_blue, Blob_Buffer_blue, result_blue
  LockBuffer Back_Buffer
  LockBuffer BlobBuffer
  For yy = 0 To BlobHeight-1
    For xx = 0 To BlobWidth-1
      xn = x+xx
      yn = y+yy
      
      argb = ReadPixelFast(xx,yy,BlobBuffer)
      If argb <> mask
        Blob_Buffer_red = (argb Shr 16) And $ff
        Blob_Buffer_green = (argb Shr 8) And $ff
        Blob_Buffer_blue = argb And $ff
        
        argb = ReadPixelFast(xn,yn,Back_Buffer)
        back_buffer_red = (argb Shr 16) And $ff
        back_buffer_green = (argb Shr 8) And $ff
        back_buffer_blue = argb And $ff
  
        result_red = (alpha * Blob_Buffer_red) Shr 8 + back_buffer_red
        result_green = (alpha * Blob_Buffer_green) Shr 8 + back_buffer_green
        result_blue = (alpha * Blob_Buffer_blue) Shr 8 + back_buffer_blue
        
        If result_red > 255 Then result_red = 255
        If result_green > 255 Then result_green = 255
        If result_blue > 255 Then result_blue = 255
  
        argb_pix = (((result_red Shl 8) Or result_green) Shl 8) Or result_blue
        WritePixelFast xn,yn,argb_pix,Back_Buffer
      EndIf
    Next
  Next
  UnlockBuffer Back_Buffer
  UnlockBuffer BlobBuffer
End Function

