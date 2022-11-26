;=======================================================================  
; * SOME LIMITATIONS : 
; 
; - strings must be <250 characters long... 
; - type range - 0 - 64000 
; - maximum packed size 0-64000 
; 
; * SEND EXAMPLE 
; 
; NB_Create(1) 
; NB_AddByte(10) 
; NB_AddString("hello") 
; NB_Send(stream) 
; -you can resend the same message to any other stream 
; NB_Send(clietn1) 
; NB_Send(client2) 
; 
; * RECIEVE EXAMPLE: 
; 
; msg.NB_msg = NB_Recieve(stream) 
; if msg <> null 
;  select msg\typ 
;   case 1 
;    i% = NB_Get_Int(msg) 
;    bank% = NB_Get_Bytes(msg) 
;    s$ = NB_Get_String(msg);     
;  end select 
;  NB_FreeMsg(msg) 
; endif 
; 
; 
; Author : pprogs@mail.ru 
; Homepage: http://blitzgames.vov.ru 
;=======================================================================  
 
Const NB_MAXBUFFERSIZE% = 1024 
 
;=======================================================================  
; STATUS INFORMATION 
;=======================================================================  
 
Global NB_TotalSent%=0 
Global NB_TotalRecieve%=0 
Global NB_TotalPacketsSent%=0 
Global NB_TotalPacketsRecieve%=0 
 
;=======================================================================  
; NET BANK MAIN FUNCTIONS AND VARIBLES 
;=======================================================================  
 
Type NB_msg 
 Field typ% ;2 byte 
 Field sz% ;2 byte 
 Field dat% ;databank 
 Field rp% ;readpos 
End Type 
 
Global NB_Bank%=CreateBank(NB_MAXBUFFERSIZE) 
Global NB_Type%=0 
Global NB_Size%=0 
 
;creates a new message 
Function NB_Create(msg_type%) 
 NB_Type% = msg_type% 
 NB_Size% = 4 
End Function 
 
;recieve message if any 
;dont forget to NB_FreeMsg() each recieved message!! 
Function NB_Recieve.NB_msg(stream%) 
 If ReadAvail(stream)<4 Return Null 
 Local m.NB_msg = New NB_msg 
    
 m\typ% = ReadShort(stream%)     
 m\sz% = ReadShort(stream%) - 4 ;not include header data 
 DebugLog "Recieve (size: "+(m\sz+4)+") (Type: "+m\typ+")" 
 
 If m\sz > 0 
  m\dat = CreateBank(m\sz) 
  res% = ReadBytes(m\dat,stream,0,m\sz) 
  If res <> m\sz 
   DebugLog "Error ! readed bytes is not equal msg size !" 
  End If        
 Else 
  m\dat = 0 
 End If 
 m\rp=0  
 NB_TotalRecieve = NB_TotalRecieve + m\sz + 4 
 NB_ToaltPacketsRecieve = NB_TotalPacketsRecieve + 1 
 Return m 
End Function 
 
;send current message 
Function NB_Send(stream%)  
 DebugLog "Sending (size: "+NB_Size+") (Type: "+NB_Type+")"     
 PokeShort(NB_Bank,0,NB_Type%) 
 PokeShort(NB_Bank,2,NB_Size%)         
 WriteBytes(NB_Bank, stream, 0, NB_Size%) 
 NB_TotalSent = NB_TotalSent + NB_Size 
 NB_TotalPacketsSent = NB_TotalPacketsSent + 1 
End Function 
 
;frees message 
Function NB_Free(msg.NB_msg)     
 If msg = Null Return     
 If msg\dat <> 0 FreeBank msg\dat   
 Delete msg 
End Function 
 
;reset all NB data and set new buffer size if needed 
Function NB_Reset(newbuffersize%=NB_MAXBUFFERSIZE) 
 NB_Type = 0 
 NB_Size = 0  
 NB_TotalSent%=0 
 NB_TotalRecieve%=0 
 NB_TotalPacketsSent%=0 
 NB_TotalPacketsRecieve%=0  
 If newbuffersize <> BankSize(NB_Bank) ResizeBank(NB_Bank,newbuffersize)  
End Function 
 
;=======================================================================  
; PACKET READING FUNCTIONS 
;=======================================================================  
 
Function NB_Getbyte%(msg.NB_msg)   
 If msg = Null Return 0 
 If msg\dat = 0 Return 0    
 Local tb% = PeekByte(msg\dat,msg\rp) 
 msg\rp = msg\rp + 1 
 Return tb 
End Function 
 
Function NB_GetShort%(msg.NB_msg)     
 If msg = Null Return 0     
 If msg\dat = 0 Return 0 
 Local tb% = PeekShort(msg\dat,msg\rp) 
 msg\rp = msg\rp + 2 
 Return tb% 
End Function 
 
Function NB_GetInt%(msg.NB_msg)     
 If msg = Null Return 0     
  If msg\dat = 0 Return 0 
  Local tb% = PeekInt(msg\dat,msg\rp) 
 msg\rp = msg\rp + 4 
 Return tb% 
End Function 
 
Function NB_Getfloat#(msg.NB_msg)     
 If msg = Null Return 0    
 If msg\dat = 0 Return 0 
 Local tb# = PeekFloat#(msg\dat,msg\rp) 
 msg\rp = msg\rp + 4 
 Return tb# 
End Function 
 
Function NB_GetString$(msg.NB_msg)     
 If msg = Null Return ""     
 If msg\dat = 0 Return "" 
 Local lng = NB_Getbyte(msg) 
 Local tmp$ = "" 
 For i = 0 To lng-1 
  tmp = tmp + Chr( PeekByte(msg\dat,msg\rp+i)) 
 Next 
 msg\rp = msg\rp + lng 
 Return tmp$ 
End Function 
 
Function NB_GetBytes%(msg.NB_msg) 
 If msg=Null Return 0 
 If msg\dat=0 Return 0 
 Local bsz% = NB_GetShort(msg) 
 Local bnk% = CreateBank(bsz) 
 CopyBank msg\dat,msg\rp,bnk,0,bsz% 
 msg\rp% = msg\rp% + bsz% 
 Return bnk 
End Function

;=======================================================================  
; PACKET DATA ADD FUNCTIONS 
;=======================================================================  
 
Function NB_AddByte(nb_byte%)     
 PokeByte(NB_Bank, NB_Size, nb_byte%) 
 NB_Size = NB_Size + 1 
End Function 
 
Function NB_AddShort(nb_short%)     
 PokeShort(NB_Bank, NB_Size, nb_short%) 
 NB_Size = NB_Size + 2 
End Function 
 
Function NB_AddInt(nb_int%)    
 PokeInt(NB_Bank, NB_Size, nb_int%) 
 NB_Size = NB_Size + 4 
End Function 
 
Function NB_AddFloat(nb_float#)     
 PokeFloat(NB_Bank, NB_Size, nb_float#) 
 NB_Size = NB_Size + 4 
End Function 
 
Function NB_AddString(nb_string$) 
 Local i%     
 NB_AddByte(Len%(nb_string)) 
 For i=0 To Len(nb_string)-1 
  PokeByte(NB_Bank,NB_Size+i, Asc( Mid$(nb_string$,i+1,1))) 
 Next 
 NB_Size = NB_Size + Len(nb_string) 
End Function 
 
Function NB_AddBytes(Bank%) 
 If Bank = 0 Return 
 Local bsz% = BankSize%(Bank) 
 If bsz = 0 Return 
 NB_AddShort(bsz) 
 CopyBank(Bank,0,NB_Bank,NB_Szie,bsz) 
 NB_Size = NB_Size + bsz 
End Function

;-------------------------------------------------------------------------------
;EXAMPLE
;-------------------------------------------------------------------------------

AppTitle "Net bank test"  
  
Global net_number_generator%=0  
Const net_port_number%=9999  
Const net_timeout = 10000  
  
Const NS_CONNECTED=1  
Const NS_LOGININ=2  
Const NS_LOGGEDIN=3  
  
Type net_client  
 Field stream%  
 Field lastheard%  
 Field number%  
 Field status%    
 Field ping%  
 Field lastping%  
 Field name$  
End Type  
  
Global net_server_stream%   
Global net_server_addres$   
  
Global net_iamserver%=False  
Global net_server%  
  
Global net_name$  
Global net_pass$  
  
Global net_status=NS_CONNECTED  
  
Global users_count%=0  
  
If Input("1 - create 0 -join : ")  
 net_server = CreateTCPServer(net_port_number)  
 net_iamserver = True  
 If net_server=0  
  RuntimeError("Cannot create server")  
 End If  
End If  
  
  
If net_iamserver Then  
 net_server_addres = "localhost"  
 AppTitle "Net test : server"  
Else  
 net_server_addres = Input("Enter address (ex.192.168.5.72): ")  
 AppTitle "Net test : client"  
EndIf  
  
net_name = Input("Login: ")  
net_pass = Input("Password: ")  
  
net_server_stream = OpenTCPStream(net_server_addres,net_port_number)  
If net_server_stream = 0  
  End  
EndIf  
  
NB_Create(99)  
NB_Addstring(net_name$)  
NB_Addstring(net_pass$)  
NB_Send(net_server_stream)  
  
FlushKeys()  
  
  
While Not KeyHit(1)  
 If net_iamserver=True  
  cl% = AcceptTCPStream(net_server)  
  If cl <> 0  
    c.net_client = New net_client  
    c\stream = cl  
    c\lastheard = MilliSecs()  
    net_number_generator = net_number_generator+1  
    c\number = net_number_generator      
   c\status = NS_CONNECTED      
  End If  
  net_update_server()  
 End If  
  
 net_update_client()  
  
 If KeyHit(2) And (net_status = NS_LOGGEDIN)  
  NB_Create(1)  
  NB_Addstring("Hello !!!!")  
  NB_Send(net_server_stream)  
 End If  
Wend  
  
If net_iamserver  
 NB_Create(999)   
 For cc.net_client = Each net_client  
  NB_Send(cc\stream)     
 Next   
EndIf  
  
If net_status = NS_LOGGEDIN  
 NB_Create(50)  
 NB_Send(net_server_stream)  
End If  
  
End  
;=======================================================================  ==  
;=======================================================================  ==  
;=======================================================================  ==  
  
Function net_update_client()    
 Local msg.NB_msg = NB_Recieve(net_server_stream)  
 If msg = Null Return   
 Select msg\typ  
  Case 2  
   Print "client: Users count : "+NB_GetByte(msg)+" random float: "+NB_Getfloat(msg)  
  Case 3  
   NB_Create(3)  
   NB_Addint(NB_Getint(msg))  
   NB_Send(net_server_stream)  
  Case 100  
   res = NB_GetByte(msg)  
   If res = 1 ;deny  
    RuntimeError("access denied")  
   ElseIf res=2 ;ok  
    net_status = NS_LOGGEDIN  
    Print "Logged in !!"  
   End If  
  Case 101  
   Print "Client: new ppl here ! = "+NB_Getstring(msg)  
  Case 102  
   Print "Client: "+NB_Getstring(msg)+" timeouted !!"  
  Case 103  
   Print "Client: "+NB_Getstring(msg)+" exited !!"     
  Case 999  
   RuntimeError "Server exited"  
 End Select  
 NB_Free(msg)  
End Function  
  
Function net_update_server()  
 For c.net_client = Each net_client  
  If ReadAvail(c\stream)  
   msg.NB_msg = NB_Recieve(c\stream)  
   c\lastheard = MilliSecs()  
   Select msg\typ  
    Case 1   
     Print "** server: "+NB_Getstring(msg)+" from: "+c\number  
    Case 3  
     c\ping = MilliSecs()-NB_Getint(msg)  
     Print "** server: Client "+c\number+" ping = "+c\ping  
    Case 99  
     c\name$ = NB_Getstring(msg)  
     NB_Create(100)  
     If NB_Getstring(msg) <> "hello"   
 NB_Addbyte(2)   
     Else   
 NB_Addbyte(2)  
 c\status = NS_LOGGEDIN  
     End If  
     NB_send(c\stream)  
     If c\status = NS_LOGGEDIN  
 NB_Create(101)  
 NB_Addstring(c\name)  
 For cc.net_client = Each net_client  
  If cc <> c  
   NB_Send(cc\stream)     
  End If  
 Next  
     End If  
    Case 50  
     Print "** server : "+c\number+" exited !"  
     NB_Create(103)  
     NB_Addstring(c\name)  
     For cc.net_client = Each net_client  
 If cc <> c  
  NB_Send(cc\stream)  
 End If  
     Next  
     CloseTCPStream(c\stream)  
     Delete c  
   End Select  
   NB_Free(msg)     
  ElseIf MilliSecs()-c\lastheard>net_timeout    
   Print "** server : "+c\number+" timeouted !"  
   NB_Create(102)  
   NB_Addstring(c\name)  
   For cc.net_client = Each net_client  
    If cc <> c  
     NB_Send(cc\stream)  
    End If  
   Next  
   CloseTCPStream(c\stream)  
   Delete c  
  ElseIf MilliSecs()-c\lastping>5000  
    NB_Create(3)  
    NB_Addint(MilliSecs())  
    NB_Send(c\stream)  
    c\lastping = MilliSecs()  
  End If  
 Next
End Function