.StartCode
	AppTitle "Bellcurve conversion by LuCiFeR[SD] V.2"
	;
	;code converted from Amiblitz by LuCiFeR[SD]
	;
	;mailto:lucifer-sd AT satanicdreams.com
	;
	;This little conversion has gone a bit further than I intended. It started out life as a
	;straightforward conversion, It has now exceeded that title and has now got an identity of its own.
	;please feel free to modify this code however you like. it's not really of much use for anything,
	;but it was kind of fun to mess around with. Plus it taught me a few things LOL.
	;The original code is from AndrewsDemos directory that came with Blitz Basic 2.1 for the Amiga. 
	;I have botched in Vwait code that will probs not work on all systems... I had to do this because
	;of a few complaints about this drawing too fast. There is probably a better way to implement it,
	;but right now, (its 04:30) and I cannot be bothered :). 
	
	;;-=-=-=-=-=-=-=-=-=-=-=Globals &amp; Constants-=-=-=-=-=-=-=-=-=-=-=-
	;
	Const width=1024,height=768,bits=16,disptype=1 ;setup Display
	Global switch,x#,u1#,u2#,mean#,std# ;a few important global variables
	Global blood = False ;if blood = false it gives a Amiga type representation LOL
	numlines=2 ;set the number of lines drawn before Vwait can do crap
	
	;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Display-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	Graphics width,height,bits,disptype ;open our display
	SetBuffer FrontBuffer() 
	
	If blood ;If blood=true
		Color 255,0,0 ;set the drawing colour to red
	Else ;if it isn't....
		ClsColor 180,180,180 ;set screen colour to miggy grey LOL
		Cls ;clear the screen
		Color 0,0,0 ;set the drawing colour to black
	EndIf ;Exit the If thang :)
	
	;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Arrays-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	Dim size(width) ;set the array up to hold some crap (bellcurve stuff)
	Dim col(100) ;set up another array to hold mor crap (colour stuff)
	
	;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Misc-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	If blood ;If blood=true
		For c=0 To 99 ;loop 100 times
		col(c)=128+Rand(128) ;hold our colour info in the array
		Next ;exit the loop
	EndIf ;Exit the If thang :)
	
.Main ;label here just to point crap out in code 
	;-=-=-=-=-=-=-=-=-=-=-=-=-=-Main Loop-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	While Not KeyHit(1) Or MouseHit (1) ;loop until escape is pressed
		count=count+1 ;increment variable by 1 each loop.
		If count=width/numlines ;if count = the same value as width variable
			VWait:count=0 ;do a vwait and reset count variable to zero
		EndIf ;exit the if thang. 
		x=normal(width/2,width/10) ;enter function 
		If x > 0 And x < width ;are we are within limits?
			If blood ;If blood=true 
				If Rand(600)=95 ;nice effect :)
					Color col(Rand(100)),0,0 ;set the drawing colour
				EndIf ;Exit the blood If thang :)
			EndIf ;Exit the If thang :) 
			If size(x) < height ;check that we do not draw beyond height limits
				Plot x#,size(x) ;draw shit to our display
			EndIf ;end the if thang 
		size(x)=size(x)+1 ;increment by 1 
		EndIf ;Exit the If thang :)
	Wend ;do it all over again
	;-=-=-=-=-=-=-=-=-=-=-=-=-Program End-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	End


.BlitzPC_Functions ;label here just to point crap out in code 
	;-=-=-=-=-=-=-=-=-=-=-=-=-Functions-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	;
	;Worked out the damn problem I was having...
	;It was with the math. I forgot that blitzPC works angles in degrees
	;and AmiBlitz uses Radians! 180 degrees different...
	;no wonder it was so fudged :))))).
	;
	; "normal" distribution with Mean and Standard Deviation
	;
	Function normal(mean,std) ;This calculates the bellcurve:
		switch=Not switch ;quick 0/1 toggle
		If switch Then Return u1#*Cos(u2)+mean ;if 1 enter
		u1#=std*Sqr(-2*Log(Rnd(1)) ) ;math
		u2#=Rnd(182*Pi) ;
		Return u1#*Sin(u2)+mean ;
	End Function ;return to main loop



.AmiBlitzSource ;label here just to point crap out in code 
	;-=-=-=-=-=-=-=-=-=-=-original Amiblitz source-=-=-=-=-=-=-=-=-=-=-
	;
	;The original source is from "Blitz2:EXAMPLES\ANDREWSDEMOS\bellcurve.bb"
	;This is all commented out for obvious reasons... ie. it won't work LOL
	
	
	; "normal" distribution with Mean and Standard Deviation
	;Function.q normal{mean,std}
	; SHARED switch.w,u1,u2
	; switch=Not switch
	; If switch Then Function Return u1*Cos(u2)+mean
	; u1=std*Sqr(-2*Log(Rnd(1)) )
	; u2=Rnd(2*Pi)
	; Function Return u1*Sin(u2)+mean
	;End Function
	;
	;Screen 0,4
	;ScreensBitMap 0,0
	;Dim size(320)
	;
	;Repeat
	; x=normal{160,30}
	; If x>0 And x<320
	; Plot x,size(x),1
	; size(x)+1
	; EndIf
	;Until Joyb(0)<>0
	;End

;-=-=-=-=-=-=-=-=-=-END original Amiblitz source-=-=-=-=-=-=-=-=-=-=-
