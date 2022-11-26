Const FPS:Int=75 
Global period:Float=1000.0/FPS,elapsed:Int,Ticks:Int,tween:Float, time:Int


	time=MilliSecs()-period
	Repeat		
		Repeat 
			elapsed=MilliSecs()-time 
		Until elapsed 
		 
		Ticks=elapsed/period
		tween=Float(elapsed mod period)/Float(period) 
		  
		For k=1 To Ticks 
			time=time+period 
			'############# C A L C U L A T I N G ###############
			'logic code
		Next 
		'###################### D R A W I N G ######################
		'graphics code
	Forever