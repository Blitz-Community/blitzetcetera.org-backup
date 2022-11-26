Type TShot Extends TSpaceObject

  Global List:TList
  Global Image:TImage

  Function Fire( X, Y, Direction, XSpeed, YSpeed  )
    Local Shot:TShot = New TShot   
    If List = Null List = CreateList()
    List.AddLast Shot
    
    If Not Image'First Time
      Image = LoadImage("Ast-Missile.png")
      MidHandleImage( Image )
    EndIf

    Local ShotSpeed#= 8
    Shot.X      = X
    Shot.Y      = Y
    Shot.Direction  = Direction
    
    'Add Shot Start Speed
    Shot.XSpeed= Cos(Direction)*ShotSpeed + XSpeed
    Shot.YSpeed= Sin(Direction)*ShotSpeed + YSpeed
  EndFunction
  
  Method Draw()
    SetRotation( Direction )
    DrawImage( Image,X,Y )
  EndMethod
  
  Method Update()
    Draw()
    X:+ XSpeed
    Y:+ YSpeed          
    If X > 800 Or Y > 600 Or X < 0 Or Y < 0 Destroy()
  EndMethod

  Function UpdateAll()
    If Not List Return 
    
    Local Shot:TShot
    For Shot = EachIn List
      Shot.Update()
    Next
  EndFunction

  Method Destroy() 
    List.Remove( Self )
  EndMethod
  
EndType 