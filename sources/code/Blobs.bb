AppTitle "Blobs" 
 
Const ScreenWidth=320 
Const ScreenHeight=240 
 
Graphics ScreenWidth, ScreenHeight 
 
SetBuffer BackBuffer() 
 
Flip 
 
Dim Palette(255) 
Dim table(ScreenHeight,ScreenWidth) 
 
 
For i=0 To 63 
      Color 0,0,i*4 
      Line i,0,i,200       
Next 
 
For i=0 To 127 
      Color 0,(i/2)*4,63*4 
      Line i+64,0,i+64,200 
Next 
 
For i=0 To 63 
      Color i*4,63*4,63*4 
      Line i+192,0,i+192,200 
Next 
 
For x=0 To 255 
      Palette(x)=ReadPixel (x,0) 
Next 
 
Cls 
 
 
 
For y=0 To ScreenHeight 
      For x=0 To ScreenWidth 
            If (x=0 And y=0) 
                  table(y,x)=255 
            Else  
                  table(y,x)=(9000000000.0 / (Sqr(x*x + y*y) * 7000000.0)) 
            EndIf 
      Next 
Next 
 
 
FlushKeys 
FlushMouse 
MoveMouse 0,0 
 
 
Repeat 
 
      x1 = 60 * Cos (alfa)    + 30 * Sin (-alfa)  + ScreenWidth/2 
      y1 = 30 * Cos (-alfa*2) + 60 * Sin (alfa)   + ScreenHeight/2 
      x2 = 30 * Cos (alfa)    + 60 * Sin (alfa*2) + ScreenWidth/2 
    y2 = 60 * Cos (alfa)    + 30 * Sin (alfa)   + ScreenHeight/2 
    x3 = 45 * Cos (-alfa)   + 45 * Sin (alfa)   + ScreenWidth/2 
    y3 = 45 * Cos (alfa*2)  + 45 * Sin (-alfa)  + ScreenHeight/2 
    x4 = 75 * Cos (alfa)    + 15 * Sin (alfa*2) + ScreenWidth/2 
    y4 = 15 * Cos (-alfa)   + 75 * Sin (alfa*2) + ScreenHeight/2 
    x5 = 35 * Cos (alfa)    + 10 * Sin (alfa)   + ScreenWidth/2 
    y5 = 10 * Cos (alfa*2)  + 35 * Sin (-alfa)  + ScreenHeight/2 
    x6 = 40 * Cos (-alfa)   + 30 * Sin (alfa*2) + ScreenWidth/2 
    y6 = 40 * Cos (alfa)    + 10 * Sin (alfa)   + ScreenHeight/2 
      alfa=alfa+3 
       
      LockBuffer 
      For y=0 To ScreenHeight 
            For x=0 To ScreenWidth 
                  pixel=table(Abs(y1-y),Abs(x1-x))+table(Abs(y2-y),Abs(x2-x))+table(Abs(y3-y),Abs(x3-x))+table(Abs(y4-y),Abs(x4-x))+table(Abs(y5-y),Abs(x5-x))+table(Abs(y6-y),Abs(x6-x)) 
                  If pixel>255 Then pixel=255  
                  WritePixelFast x,y,palette(pixel) 
            Next 
      Next 
      UnlockBuffer 
       
      Flip 
       
      Cls 
 
 
Until MouseX()<>0 Or MouseY()<>0 Or GetKey()<>0 Or GetMouse()<>0 
 