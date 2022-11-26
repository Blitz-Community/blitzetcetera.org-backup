Graphics 800,600,0,2

SetBuffer BackBuffer()

Type rocket
     Field x,y,angle,iangle,speed,turnrate
End Type

Dim rocketimage(500)

TFormFilter 1

Global rocketpic = CreateImage(24,24)

SetBuffer ImageBuffer(rocketpic)
Rect 4,10,16,4
SetBuffer FrontBuffer()

For a = 0 To 360
     rr = CopyImage(rocketpic)
     RotateImage(rr,a)
     rocketimage(a) = rr
     MidHandle rocketimage(a)
Next


While Not KeyDown(1)
Cls
     If MouseDown (1)
     
          r.rocket = New rocket
     
          r\x = Rand(1,800)
          r\y = Rand(1,600)
          r\angle = 0
          r\speed = Rand (1,10)
          r\turnrate = Rand (1,10)
     
     End If
     

     
     For h.rocket = Each rocket
     

          h\iangle = ATan2((MouseY()-h\y),((MouseX()-h\x)))
          
          xmov# = Cos(h\angle) * h\speed
          ymov# = Sin(h\angle) * h\speed
          
          h\x = h\x + xmov#
          h\y = h\y + ymov#          
          
          
          realangle_current = h\angle          
          realangle_intend = h\iangle
          
          If h\angle < 1
                 realangle_current = 180+(180 - Abs(h\angle))
          End If
          If h\iangle < 1
               realangle_intend = 180+(180 - Abs(h\iangle))
          EndIf
          
          
          
          If realangle_intend > realangle_current And h\iangle > 0 Then h\angle = h\angle + h\turnrate
          If realangle_intend > realangle_current And h\iangle < 1 Then h\angle = h\angle + h\turnrate
          If realangle_intend < realangle_current And h\iangle > 0 Then h\angle = h\angle - h\turnrate
          If realangle_intend < realangle_current And h\iangle < 1 Then h\angle = h\angle - h\turnrate
          
          If realangle_current> 360 Then realangle_current = 1
          If realangle_current< 1 Then realangle_current = 360

          
          DrawImage rocketimage(realangle_current),h\x,h\y
          

     Next
     
     
     
     Flip


Wend