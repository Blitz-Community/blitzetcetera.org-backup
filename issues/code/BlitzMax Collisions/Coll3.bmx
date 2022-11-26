  Strict
  Graphics 640,480
  AutoMidHandle True
  
  Local SpaceShip:TImage=LoadImage("BMax-BlobShip.png")
  Local AlienShip1:TImage=LoadImage("BMax-UFO.png")
  Local AlienShip2:TImage=LoadImage("BMax-UFO.png")
  
  Local w:Int=ImageWidth(SpaceShip)
  Local h:Int=ImageHeight(SpaceShip)

  
  Repeat
   Cls
   Local R:Int=0
   Local G:Int=0
   Local B:Int=255
   ResetCollisions()
   SetColor 255,255,255
   DrawImage AlienShip1, 250,100
   CollideImage(AlienShip1,250,100,0,0,1,AlienShip1)
  
   DrawImage AlienShip2, 400,100
   CollideImage(AlienShip2,400,100,0,0,1,AlienShip2)
   Local p:Object[]=CollideRect(MouseX()-w,MouseY()-h,100,100,1,0)
   For Local i:TImage=EachIn p
      Select i
      Case AlienShip1
          R=255
      Case AlienShip2
          G=255
      End Select
   Next
   SetColor R,G,B 
   DrawRect MouseX()-w,MouseY()-h,100,100

   DrawImage   SpaceShip,MouseX(),MouseY()
   Flip
  
  Until KeyDown(KEY_ESCAPE) Or AppTerminate()
