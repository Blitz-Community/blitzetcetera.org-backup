;TBSDK][ by Frankie Techlord Taylor 11/12/04
;Tournament Blitz Service Module v3 
;TBScoreBoard Proxy Client 

Const TBSDK_TOURNAMENT%=1
Const TBSDK_COINOP%=2
Const TBSDK_TOURNAMENT_MAX%=256
Const TBSDK_COINOP_MAX%=256
Const TBSDK_PLAYER_MAX%=256

;TBSDK Objects
Type TBSDK
	Field Session_TimeOut%
	Field Session_Timer%
	Field Session_userName$
	Field Session_userPassword$
	Field Session_Active%
	Field Session_Language$="en-us"
	Field Game_ID%, Application_ID%
	Field Game_Type%	
	Field Game_Play%
	Field Game_Score%
	Field Players%
	Field Player.TBSDK_Player_Item[TBSDK_PLAYER_MAX%]
	Field Player_Tournaments%
	Field Player_Credits%
	Field Player_PlayCredits%
	Field Player_Top_Score
	Field Player_Top_userName$
	Field Tournaments%
	Field Tournament.TBSDK_Tournament_Item[TBSDK_TOURNAMENT_MAX%]
	;Field Tournament_PS_Key ;playerstat id key	
	Field Tournament_Message$
	Field Tournament_Final_Score%
	Field Tournament_Leader% 
	Field Tournament_Selected.TBSDK_Tournament_Item
	Field CoinOps%
	Field CoinOp.TBSDK_Coinop_Item[TBSDK_COINOP_MAX%]
	Field CoinOp_Selected.TBSDK_CoinOp_Item
	Field Server_URL$   
	Field Server_Folder$	
	Field Server_Port   
	Field Server_TCP_Stream
	Field Server_File$
	Field Server_Advertisement$
End Type

Type TBSDK_Player_Item
	Field Score
	Field Username$
	Field Bonus
    Field Rank
End Type

Type TBSDK_Tournament_Item
	Field ID
	Field IP$
	Field Types
	Field SkillLevel
	Field EntryFee
	Field MaxPlayer
	Field CurrentPlayer
    Field Prize
End Type

Type TBSDK_CoinOp_Item
	Field ID
	Field IP$
	Field EntryFee
	Field MaxPlayer
	Field CurrentPlayer
	Field Value$
End Type

Function TBSDK_New.TBSDK(tbsdkgameid%,tbsdkServer$="dev",tbsdkGameType%=1)
	;Purpose: Creates new instance of TBSDK Object
	;Parameters: 
	;	tbsdkGameID - Game_ID% or Application_ID% acquired from TournamentBlitz.com
	;	tbsdkServer -  TournamentBlitz Database Server
	;	tbsdkGameType- TournamentBlitz or CoinOp Service 
	;Returns TBSDK Object
	this.TBSDK = First TBSDK
	If this = Null
		this.TBSDK = New TBSDK
		this\Game_ID%=tbsdkgameid%
		If tbsdkGameType<>TBSDK_TOURNAMENT% Or tbsdkGameType<> TBSDK_COINOP% RuntimeError("TBSDK_Start Game Type must equal 1 or 2")
		this\Game_Type%=tbsdkGameType%
		Select Lower(tbsdkServer$)
			Case "localhost"
				this\Server_URL$="localhost"
				this\Server_Folder$="/tournamentblitz/"
				this\Server_Port%=8500		
			Case "dev"
				this\Server_URL$="www.tournamentblitz.com"
				this\Server_Folder$="/"
				this\Server_Port%=80		
			Case "live" ;production
				this\Server_URL$="www.tournamentblitz.com"
				this\Server_Folder$="/"
				this\Server_Port%=80	
		End Select
	End If
	Return this	
End Function

Function TBSDK_Delete(this.TBSDK)
	;Purpose: Removes TBSDK Object
	;Parameters: TBSDK Object
	;Returns : None
	For loop% =  1 To this\Tournaments%
		Delete this\Player[loop%]
	Next	
	For loop% =  1 To this\Tournaments%
		Delete this\Tournament[loop%]
	Next
	For loop% =  1 To this\Tournaments%
		Delete this\CoinOp[loop%]
	Next	
	Delete this
End Function

Function TBSDK_Request(this.TBSDK,tbsdkfile$,tbsdkpoststring$)
	;Purpose: Opens HTTP connection to Tournament BLITZ Server Page and Posts Variables Values.
    this\Server_TCP_Stream=OpenTCPStream(this\Server_URL$,this\Server_Port)
    If Not this\Server_TCP_Stream RuntimeError "Failed To Connect to Tournament BLITZ Server"
    WriteLine this\Server_TCP_Stream, "POST "+this\Server_Folder+tbsdkfile$+" HTTP/1.0"
    WriteLine this\Server_TCP_Stream, "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*"
    WriteLine this\Server_TCP_Stream, "Accept-Language: "+this\Session_Language$
    WriteLine this\Server_TCP_Stream, "Accept-Encoding: gzip, deflate"
    WriteLine this\Server_TCP_Stream, "User-Agent: TBSDK_Client"
    WriteLine this\Server_TCP_Stream, "Content-Length: "+Str(Len(tbsdkpoststring$))
    WriteLine this\Server_TCP_Stream, "Host: "+this\Server_URL$
    WriteLine this\Server_TCP_Stream, "Content-Type: application/x-www-form-urlencoded"
    WriteLine this\Server_TCP_Stream, ""
    WriteLine this\Server_TCP_Stream, tbsdkpoststring$
End Function

Function TBSDK_Response(this.TBSDK)
	;Purpose: Returns and Parses Server Reponse after Page Request. Closes HTTP connection.
	TBSDK_Parse(this)
	CloseTCPStream this\Server_TCP_Stream
	this\Server_TCP_Stream=0
End Function

Function TBSDK_Parse(this.TBSDK)
	;Purpose: Parse HTML Assigning Encoded Values to TBSDK Variables
	;Parameter: TBSDK Object
	;Returns: None
	While Not Eof(this\Server_TCP_Stream)
			Select ReadLine(this\Server_TCP_Stream)
					Case "<TBSDK>" Color 0,255,255
					Case "Session_Timeout" this\Session_Timeout=ReadLine(this\Server_TCP_Stream)
					Case "Session_Timer" this\Session_Timer=ReadLine(this\Server_TCP_Stream)
					Case "Session_userName" this\Session_userName=ReadLine(this\Server_TCP_Stream)
					Case "Session_userPassword" this\Session_userPassword=ReadLine(this\Server_TCP_Stream)
					Case "Session_Active" this\Session_Active=ReadLine(this\Server_TCP_Stream)
					Case "Session_Language" this\Session_Language=ReadLine(this\Server_TCP_Stream)
					Case "Game_Play" this\Game_Play=ReadLine(this\Server_TCP_Stream)
					Case "Game_Score" this\Game_Score=ReadLine(this\Server_TCP_Stream)
					Case "Player_Tournaments" this\Player_Tournaments=ReadLine(this\Server_TCP_Stream)
					Case "Player_Credits" this\Player_Credits=ReadLine(this\Server_TCP_Stream)
					Case "Player_PlayCredits" this\Player_Credits=ReadLine(this\Server_TCP_Stream)
					Case "Player_Top_Score" this\Player_Top_Score=ReadLine(this\Server_TCP_Stream)
					Case "Player_Top_userName" this\Player_Top_userName$=ReadLine(this\Server_TCP_Stream)
					Case "Player_List"
						For tbsdkloop=1 To this\Players%
							Delete this\Player[tbsdkloop]
						Next 
						this\Players%=0
						;Delete Each TBSDK_Player_Item;delete playerlist
					Case "Player_Item" 
						this\Players%=this\Players%+1
						this\Player.TBSDK_Player_Item[this\Players%]= New TBSDK_Player_Item
						;TBSDK_Player.TBSDK_Player_Item = New TBSDK_Player_Item ;Item
					Case "Player_Score" this\Player[this\Players%]\Score=ReadLine(this\Server_TCP_Stream)
					Case "Player_Username" this\Player[this\Players%]\Username$=ReadLine(this\Server_TCP_Stream)						
					Case "Player_Bonus" this\Player[this\Players%]\Bonus=ReadLine(this\Server_TCP_Stream)
					Case "Player_Rank" 	this\Player[this\Players%]\Rank=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_Message" this\Tournament_Message=ReadLine(this\Server_TCP_Stream)	
					Case "Tournament_Final_Score" this\Tournament_Final_Score=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_Leader" this\Tournament_Leader=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_List"
						For tbsdkloop=1 To this\Tournaments%
							Delete this\Tournament[tbsdkloop]
						Next
						this\Tournaments%=0 
						;Delete Each TBSDK_Tournament_Item ;delete tournamentlist
					Case "Tournament_Item"
						this\Tournaments%=this\Tournaments%+1
						this\Tournament.TBSDK_Tournament_Item[this\Tournaments%]= New TBSDK_Tournament_Item	
						;TBSDK_Tournament.TBSDK_Tournament_Item = New TBSDK_Tournament_Item ;Item
					Case "Tournament_ID" this\Tournament[this\Tournaments%]\ID=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_HostIP"  this\Tournament[this\Tournaments%]\IP$=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_Type" this\Tournament[this\Tournaments%]\Types=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_SkillLevel" this\Tournament[this\Tournaments%]\SkillLevel=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_EntryFee" this\Tournament[this\Tournaments%]\EntryFee=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_MaxPlayer" this\Tournament[this\Tournaments%]\MaxPlayer=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_CurrentPlayer" this\Tournament[this\Tournaments%]\CurrentPlayer=ReadLine(this\Server_TCP_Stream)
					Case "Tournament_Prize" this\Tournament[this\Tournaments%]\Prize=ReadLine(this\Server_TCP_Stream)
					Case "Server_URL"  this\Server_URL=ReadLine(this\Server_TCP_Stream)
 					Case "Server_Folder" this\Server_Folder=ReadLine(this\Server_TCP_Stream)
					Case "Server_Port" this\Server_Port=ReadLine(this\Server_TCP_Stream)   
					Case "Server_File" this\Server_File=ReadLine(this\Server_TCP_Stream)
					Case "Server_Advertisement" this\Server_Advertisement=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_List" 
						For tbsdkloop=1 To this\CoinOps%
							Delete this\CoinOp[tbsdkloop]
						Next 
						this\CoinOps%=0
						;Delete Each TBSDK_CoinOp_Item					
					Case "CoinOp_Item"
						this\CoinOps%=this\CoinOps%+1
						this\CoinOp.TBSDK_CoinOp_Item[this\CoinOps%]= New TBSDK_CoinOp_Item					 
						;TBSDK_CoinOp.TBSDK_CoinOp_Item = New TBSDK_CoinOp_Item
					Case "CoinOp_ID" this\CoinOp[this\CoinOps%]\ID=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_IP" this\CoinOp[this\CoinOps%]\IP$=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_EntryFee" this\CoinOp[this\CoinOps%]\EntryFee=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_MaxPlayer" this\CoinOp[this\CoinOps%]\MaxPlayer=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_CurrentPlayer" this\CoinOp[this\CoinOps%]\CurrentPlayer=ReadLine(this\Server_TCP_Stream)
					Case "CoinOp_Value" this\CoinOp[this\CoinOps%]\Value$=ReadLine(this\Server_TCP_Stream)
					Case "</TBSDK>" Color 255,255,255
				End Select
				;Close
	Wend
End Function

Function TBSDK_Login(this.TBSDK)
	;Purpose: Logins into TournamentBlitz Server and Retrieves Tournament List stored in TBSDK\Tournament.TBSDK_Tournament_Item[n]
	;	Login Username and Password is persistant until logout. If Login all fails, Player should be presented with a Fail Alert Dialog 
	;	and retuned to Login Dialog.
	;Parameters:  TBSDK Object
	;Return:  1:Pass, 0:Fail 
	;Note: 
	Select this\Game_Type%
		Case TBSDK_TOURNAMENT% TBSDK_Request(this,"TBSDK_access.cfm","?userName="+this\Session_userName$+"&amp;userPassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%))
		Case TBSDK_COINOP% TBSDK_Request(this,"TBSDK_coinop_access.cfm","?userName="+this\Session_userName$+"&amp;userPassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%))
	End Select
	TBSDK_Response(this)
	Return this\Session_Active%
End Function

Function TBSDK_Entry(this.TBSDK)
	;Purpose: Submits Entry into Tournament. Prior to Calling this Function the Player a presented a dialog to confirm entry. 
	;	The dialog should  display tournament fee, available credits, and balance: Player_Credits - Tournament[id%]\EntryFee
	;Parameters: TBSDK Object
	;Return: 1:Start Game  -1:Not Enough Credits, -2: Tournament Closed
	Select this\Game_Type%
		Case TBSDK_TOURNAMENT% TBSDK_Request(this,"TBSDK_entry.cfm","?userName="+this\Session_userName$+"&amp;userpassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%)+"&amp;tournamentID="+Str(this\Tournament_Selected\ID%))
		Case TBSDK_COINOP% TBSDK_Request(this,"TBSDK_coinop_entry.cfm","?userName="+this\Session_userName$+"&amp;userpassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%)+"&amp;coinopID="+Str(this\Tournament_Selected\ID%))
	End Select
	TBSDK_Response(this)
	Return this\Game_Play%
End Function

Function TBSDK_Final(this.TBSDK)
	;Purpose: Submits  Score and Retrieves Tournament Game Results storing Player results in stored in TBSDK\Tournament.TBSDK_Player_Item[n]
	;Parameters: TBSDK Object
	;Return: None
	Select this\Game_Type%
		Case TBSDK_TOURNAMENT% TBSDK_Request(this,"TBSDK_final.cfm","?userName="+this\Session_userName$+"&amp;userpassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%)+"&amp;tournamentID="+Str(this\Tournament_Selected\ID%)+"&amp;score"+Str(this\Game_Score%))
		Case TBSDK_COINOP% TBSDK_Request(this,"TBSDK_coinop_final.cfm","?userName="+this\Session_userName$+"&amp;userpassword="+this\Session_userPassword$+"&amp;gameID="+Str(this\Game_ID%)+"&amp;coinopID="+Str(this\Tournament_Selected\ID%)+"&amp;score"+Str(this\Game_Score%))
	End Select
	TBSDK_Response(this)
End Function

Function TBSDK_Logout(this.TBSDK)
	;Purpose:  Resets Username and Password to nil. Player should be returned to Login Dialog
	;Parameters:  TBSDK Object
	;Return:  None
	this\Session_userName$=""
	this\Session_userPassword$=""
	this\Session_Active=False
End Function
