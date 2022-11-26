Graphics 800,600

Global scratch%=CreateImage(640,480)

i=CreateImage(256,256)
SetBuffer ImageBuffer(i)
For n=0 To 200
 Color Rand(256),Rand(256),Rand(256)
 Oval Rand(256),Rand(256),32+Rand(64),32+Rand(64)
Next

SetBuffer BackBuffer()
Repeat
 Cls
 drawsizeimage i,0,0,256+128*Cos(ang#),256+128*Cos(1.7*ang#)
 ang#=ang#+3
 Flip
Until KeyHit(1)

Function DrawSizeImage(image,x%,y%,w%,h%)
     Local ih%=ImageHeight(image)
     Local iw%=ImageWidth(image)

     Local sw%=Abs(w)
     Local sh%=Abs(h)
     
     Local xr#=(Float(iw)/Float(sw))
     Local yr#=(Float(ih)/Float(sh))
     
     fromimg=ImageBuffer(image)
     toimg=ImageBuffer(scratch)
     
     Local vf=-1+((h>0)*2)
     
     Local fw=(w<0)*w
     Local fh=(h<0)*h
     
     If w>=0
          For ix=0 To sw
               CopyRect ix*xr,0,1,ih,ix,0,fromimg,toimg
          Next
     Else
          For ix=0 To sw
               CopyRect ix*xr,0,1,ih,sw-ix,0,fromimg,toimg
          Next
     EndIf
     

     For iy=0 To sh
          CopyRect 0,iy*yr,sw,1,x+fw,y+(iy*vf),toimg
     Next

End Function