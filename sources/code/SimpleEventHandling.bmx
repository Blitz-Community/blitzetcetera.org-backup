Strict


Type Tevent
  Field t:Int
  Field arg1:Int, arg2:Int
EndType


Type EventHandler

  ' Overload this
  Method EventHandler( e:Tevent ) 
  EndMethod


  Field _nexthandler:EventHandler

  Method init()
    EventDispatcher.RegisterHandler(Self)
  EndMethod
  

  Method GetNextHandler:EventHandler()
    Return _nexthandler
  EndMethod
  

  Method SetNextHandler( n:EventHandler )
    _nexthandler = n
  EndMethod
  
  
  Method SendEvent( event_type:Int, arg1:Int = 0, arg2:Int = 0 )
    EventDispatcher.SendEvent( event_type, arg1, arg2 )
  EndMethod

EndType




Type EventDispatcher

  Global _devicelist:EventHandler

  Function RegisterHandler( device:EventHandler )
    device.SetNextHandler( _devicelist )
    _devicelist = device
  EndFunction
  
  Function SendEvent( event_type:Int, arg1:Int = 0, arg2:Int = 0 )
    Local e:Tevent = New Tevent
    e.t = event_type
    e.arg1 = arg1
    e.arg2 = arg2
    Local curDevice:EventHandler = _deviceList
    While curDevice
      curDevice.EventHandler(e)
      curDevice = CurDevice.GetNextHandler()
    Wend
  EndFunction


EndType


' Some events
Const E_NEWGAME = 0
Const E_PAUSEGAME = 1
Const E_RESUMEGAME = 2
Const E_STOPGAME = 3
Const E_APPCLOSE = 4
Const E_INCSCORE = 5
Const E_POWERUP = 6




Type A Extends EventHandler

  Method EventHandler( e:Tevent )
    Select e.t
    
      Case E_NEWGAME
        Print "A: New game started"
      Case E_PAUSEGAME
        Print "A: Tea time"
      Case E_RESUMEGAME
        Print "A: No tea for you!"
      Case E_STOPGAME
        Print "A: *Chicken noises*"
      Case E_APPCLOSE
        Print "A: Shutdown"       
              
    EndSelect 
  EndMethod

EndType


Type B Extends EventHandler

  Method EventHandler( e:Tevent )
    Select e.t
    
      Case E_PAUSEGAME
        Print "B: Tea time"
      Case E_RESUMEGAME
        Print "B: No tea for you!"
      Case E_INCSCORE             
        Print "B: Yay! Score! Got "+e.arg1+" points!"
      Case E_POWERUP
        Print "B: Powerup taken"
    EndSelect 
  EndMethod

EndType


Type C Extends EventHandler

  Method EventHandler( e:Tevent )
    Select e.t
    
      Case E_PAUSEGAME
        Print "C: Tea time"
      Case E_RESUMEGAME
        Print "C: No tea for you!"
    EndSelect 
  EndMethod

EndType



Print "- Creating A and B"
Local a1:A = New A ; a1.init()
Local b1:B = New B ; b1.init()

Print "- Sending events"
EventDispatcher.SendEvent( E_NEWGAME )
EventDispatcher.SendEvent( E_INCSCORE, 1000 )
EventDispatcher.SendEvent( E_PAUSEGAME )

Print "- Creating C"
Local c1:C = New C ; c1.init()

Print "- Sending some more events"
EventDispatcher.SendEvent( E_RESUMEGAME )
EventDispatcher.SendEvent( E_POWERUP )
