www=OpenTCPStream( "www.yahoo.com",80 )
If Not www RuntimeError "Failed to connect"
WriteLine www,"GET / HTTP/1.1"
WriteLine www,"Host: www.yahoo.com"
WriteLine www,"User-Agent: BlitzBrowser"
WriteLine www,"Accept: */*"
WriteLine www,""
While Not Eof(www)
    Print "RECV: " + ReadLine(www)
Wend
CloseTCPStream www

; OpenTCPStream/CloseTCPStream Example

location$ = "www.cnn.com"

Print "Connecting..."
tcp=OpenTCPStream( location$,80 )

If Not tcp Print "Failed.":WaitKey:End

Print "Connected! Sending request..."

WriteLine tcp,"GET <a href="http:///"+location$+"" target="_blank">"+location$+"</a> HTTP/1.0"
WriteLine tcp,Chr$(10)

If Eof(tcp) Print "Failed.":WaitKey:End

Print "Request sent! Waiting for reply..."

mainwin = CreateWindow("HTML",0,0,400,400)
SetMinWindowSize mainwin,100,100
maintext = CreateTextArea(0,0,370,300,mainwin)

While Not Eof(tcp)
AddTextAreaText maintext, (ReadLine$( tcp ) + Chr$(10))
Wend

If Eof(tcp)=1 Then Print "Success!" Else Print "Error!"

CloseTCPStream tcp

Repeat

Select WaitEvent()
Case $802
	SetGadgetShape maintext,0,0,GadgetWidth(mainwin)-30,GadgetHeight(mainwin)-100
Case $803
	End
End Select

Forever
End
