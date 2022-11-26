SuperStrict

Global ScrWidth : Int  = 640
Global ScrHeight : Int = 480

Global TileList:TList = CreateList()

Const TILESIZE:Int   = 32 

Type TTile
  Field x:Int
  Field y:Int

  Field xTile:Int
  Field yTile:Int
  
  Field Width:Byte
  Field Height:Byte

  Field Walkable:Byte
  
  Function Create(sx:Int, sy:Int, WB:Byte) 
    Local TT:TTile = New TTile
      TT.width  = TILESIZE
      TT.height = TILESIZE
      TT.xTile = sx
      TT.yTile = sy
      TT.x = TT.xTile * TT.width
      TT.y = TT.yTile * TT.height
      TT.Walkable = WB

    ListAddLast(TileList , TT)
  End Function  

  Method update()
    If    walkable         'walkable
      SetColor(255 , 0 , 0)  'red
    Else             'NOT walkable
      SetColor(0 , 255 , 0) 'green
    End If  
  End Method      

  Method Draw()
        DrawRect(x,y,Width,Height)  
  End Method

End Type

Type TLevel
  Field Width : Byte 
  Field Height : Byte

  Field Map:Int[,]
  
  Function Create:TLevel(MyMap:Int[],map_width:Int,map_height:Int)
      Local TM : TLevel = New TLevel  
    TM.Width  = map_width
    TM.Height = map_height

    TM.map = New Int [TM.Width, TM.Height]
    TM.load(myMap)
    
    Return TM               
  End Function  
  
  Method Load(arrMap:Int[])
    For Local i:Int = 0 Until Height
      For Local j:Int = 0 Until Width
        Map[j , i] = arrMap[j + (i * Width)]
        If Map[j , i]
             TTile.Create(j , i , True)
        Else
             TTile.Create(j , i , False)
        End If
      Next
    Next    
  End Method  

  Method Render()
    For Local CurTile:TTile = EachIn TileList
      CurTile.Update()
      CurTile.Draw()
    Next  
  End Method
  
End Type

Global LevMap:Int[] = [1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , ..
         1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 1 , ..
         1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , ..
         1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
         1 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
         1 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , ..
         1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , ..
         1 , 0 , 1 , 1 , 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 1 , ..
         1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 , ..
         1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1]

Global MyLevel : TLevel = New TLevel.Create(LevMap , 20 , 10)

Graphics(ScrWidth, ScrHeight) 

Repeat
   Cls

    MyLevel.Render()
    
   Flip 
Until KeyDown(KEY_ESCAPE) Or AppTerminate()

End
