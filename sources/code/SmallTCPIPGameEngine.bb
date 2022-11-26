Global max_player=8,nunbers_of_players=0

Type user
	Field username$
	Field stream
	Field xpos
	Field ypos
	Field Xmouse
	Field Ymouse
	Field packetdelay
	Field TTS ;Time to send value. each 25 millisec minimum.
	Field RTS ;Ready to send.
End Type



server=CreateTCPServer(81)




If Not server Then Print "Failed to create server at port 81" : WaitKey: End
Print "Main core mode.. waiting for a users"




Graphics 320,240,32,2
SetBuffer BackBuffer()
ClsColor 0,0,255
Cls



While Not KeyHit(1)

upd=upd+1
If upd=50
upd=0
	Cls

	Text 0,0,"Server online"
	Text 0,10,"-------------"
	Text 0,20,"Users logged in "+nunbers_of_players
	Text 0,30,"Max users "+max_player
	Text 0,40,"Packets recived "+packets
	Text 0,50,"Packets sended "+packets
	Text 0,60,"Total Logins sens start "+logins

	Locate 0,30
	Flip True
End If


	;************* New Stream handing ************** 
	stream=AcceptTCPStream(server)
	
	If nunbers_of_players<max_player 
		
		If stream
	
			logit "New stream detected"
	
			message$=ReadLine$(stream)
			If message$="Logint to network Game of Point to mouse"
				logit "User logins"
				message$=ReadLine$(stream)
				failed=False
				For users.user=Each user
					If message$=users\username$ Then failed=True
				Next
			
				If failed=True
					WriteLine(stream,"Username already taken")
					logit "user login failed"
				Else ;Login OK
					logit "user login ok"
					WriteLine(stream,"You have a connection to server")
					users.user = New user
					users\username$=message$
					users\stream=stream
					logins=logins+1
					nunbers_of_players=nunbers_of_players+1
				End If
			End If
		End If
	End If
	;**** End of New stream handling ****





	;General stream handling
	For users.user = Each user
		
		users\TTS=users\TTS-1 ;Decrese time to send.
		
		If Not Eof(users\stream) ;Check to see so user stream is up.
		
			
		
				If ReadAvail(users\stream)>1 ;Check if data has arrived.
					message$=ReadLine$(users\stream) ;read some
					If message$="Player Cords" ;
				
						logit "reading user cordinates"
						users\xpos=ReadLine(users\stream) ;Read xpos
						users\ypos=ReadLine(users\stream) ;Read ypos
						users\XMouse=ReadLine(users\stream) ;read xmouse
						users\YMouse=ReadLine(users\stream) ;Read ymouse
						packets=packets+1
						
						;Now reply all user cordinates to player.
						
					
						users\RTS=True
					 	
					End If
				End If
				
			
		Else ;User disconected .. ditch that user.
			logit users\username$+" has ben lost connection to"
			Delete users
			nunbers_of_players=nunbers_of_players-1:Exit ;Exits loop so it wont be a MAV error.
		End If

		If users\TTS<=0 And users\RTS=True
			users\rts=False
			users\TTS=25
			For others.user=Each user
				
				logit "sending user cordinates"
				WriteLine(users\stream,"user")
				WriteLine(users\stream,others\username$)
				WriteLine(users\stream,others\xpos)
				WriteLine(users\stream,others\ypos)
				WriteLine(users\stream,others\xmouse)
				WriteLine(users\stream,others\ymouse)
			
			Next
		End If

	Next



	Delay 1

	



Wend






Function logit(a$)

Text 0,100,a$

End Function

;Client code
Global server_ip$="localhost"

Global xpos=100,ypos=100 ;position of player

Global max_player=8,nunbers_of_players=0

Type user
	Field username$
	Field stream
	Field xpos
	Field ypos
	Field Xmouse
	Field Ymouse
End Type




While  Len(username$)<4
	username$=Input$("Enter your username")
Wend


stream=OpenTCPStream(server_ip$,81)

If Not stream Then Print "Failed to connect to server "+server_ip$ : WaitKey:End


;Send network game name as verification :)
WriteLine(stream,"Logint to network Game of Point to mouse")


WriteLine(stream,username$)

message$=ReadLine$(stream)

If message$<>"You have a connection to server" Then Print message$ : WaitKey :End ;somthing went wrong.


Print "Login ok starting the game"


WriteLine(stream,"Player Cords")
		
WriteLine(stream,xpos)
WriteLine(stream,ypos)
WriteLine(stream,MouseX())
WriteLine(stream,MouseY())


		
Graphics 640,480,32,2
SetBuffer BackBuffer()



While Not KeyHit(1)
	
	If hitt=True
	ClsColor 255,255,255
	
	Else
	ClsColor 0,0,0
	End If
	Cls
	
	
	.more	
	hitt=False
	;Time to check for new players
	If ReadAvail(stream)
		hitt=True ;a variable to be set each time packets reading
		message$=ReadLine$(stream)
		If message$="user"
		
			;read username
			newuser=True
			userN$=ReadLine$(stream)
			If userN$<>username$ 
			For users.user=Each user
				 If users\username$=userN$ Then newuser=False : Exit ;exit and read in cords
			Next
			
			If newuser=True
				users.user = New user
				users\username$=userN$
			End If
			users\xpos=ReadLine(stream)
			users\ypos=ReadLine(stream)
			users\Xmouse=ReadLine(stream)
			users\Ymouse=ReadLine(stream)
			nunbers_of_players=nunbers_of_players+1
			End If
			
			send=True
			;Done reading :)
		End If
			
	End If
	
	If hitt=True Then Goto more
	
	If send=True
		;Well time to send cords to server.
		WriteLine(stream,"Player Cords")
		
		WriteLine(stream,xpos)
		WriteLine(stream,ypos)
		WriteLine(stream,MouseX())
		WriteLine(stream,MouseY())	
		send=False
		sended=True
	End If
	;Draw players on screen including player self
	Rect MouseX(),MouseY(),10,10
	
	For users.user = Each user
		
		Rect  users\xmouse,users\ymouse,20,20
		Rect users\xpos,users\ypos,15,5
	Next

	;Draw a rect on top left coner to show if packets drawn.
	If sended=True
		sended=False
		Rect 0,0,30,10
	End If


	handle_player()


	Flip False
	Delay 2
	
	


Wend





Function handle_player()

If KeyDown(205) Then xpos=xpos+1
If KeyDown(203) Then xpos=xpos-1
If KeyDown(208) Then ypos=ypos+1
If KeyDown(200) Then ypos=ypos-1

Rect xpos,ypos,15,5

End Function
