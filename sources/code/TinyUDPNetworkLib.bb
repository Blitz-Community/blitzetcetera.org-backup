; graphics
Graphics 640, 480, 0, 2
SetBuffer BackBuffer(  )


; open udp stream
Const port% = 80
Local host.net = Open_Net.net( port% )
If host.net <> Null Then Print "Open Stream: OK" Else Print "Open Stream: Fail" : Goto net_end


; send message
If Send_Msg%( host.net, "Hello!" ) Then Print "Message send: OK" Else Print "Message send: Fail"

; receive message
Print "Message Received: " + Recv_Msg$( host.net )
Print ""


; end program
Print "Program End...."
Close_NET%( host.net )
.net_end
WaitKey
End

; declare net type
Type net
	Field net%, ip%
End Type

; open net
Function Open_Net.net( net_port% )
Local net.net, net_net%, net_hosts%
net_hosts% = CountHostIPs( "" )
If net_hosts% > 0 Then
	net_net% = CreateUDPStream( net_port% )
		If net_net% <> 0 Then
			net.net = New net
				net\net% = net_net%
				net\ip% = HostIP( 1 )
			Return net.net
		Else
			Return Null
		EndIf
Else
	Return Null
EndIf
End Function

; close net
Function Close_Net%( net.net )
If net.net <> Null Then
	CloseUDPStream net\net%
		Delete net.net
	Return True
Else
	Return False
EndIf
End Function

; send message
Function Send_Msg%( msg.net, msg_data$, msg_ip% = 0, msg_port% = 0 )
If msg.net <> Null Then
	If msg_iip% = 0 Then msg_ip% = msg\ip%
	If msg_port% = 0 Then msg_port% = UDPStreamPort( msg\net% )
		WriteString( msg\net%, msg_data$ )
			SendUDPMsg( msg\net%, msg_ip%, msg_port% )
		Return True
Else
	Return False
EndIf
End Function

; receive message
Function Recv_Msg$( msg.net )
Local msg_ip%, msg_data$
If msg.net <> Null Then
	msg_ip% = RecvUDPMsg( msg\net% )
		If msg_ip% <> 0 Then
			msg_data$ = ReadString$( msg\net% )
			Return msg_data$
		Else
			Return False
		EndIf
Else
	Return False
EndIf
End Function

; get ip
Function Get_IP%( net.net )
If net.net <> Null Then
	Return net\ip%
Else
	Return False
EndIf
End Function

; message ip
Function Msg_IP%( msg.net )
Local msg_ip_ret%
If msg.net <> Null Then
	msg_ip_ret% = UDPMsgIP( msg\net% )
	Return msg_ip_ret%
Else
	Return False
EndIf
End Function

; message port
Function Msg_Port%( msg.net )
Local msg_port_ret%
If msg.net <> Null Then
	msg_port_ret% = UDPMsgPort( msg\net% )
	Return msg_port_ret%
Else
	Return False
EndIf
End Function