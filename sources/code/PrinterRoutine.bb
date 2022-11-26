; Printer-routine by Wedoe Januar 2002
Global printer = OpenFile ("LPT1")
If printer
.printerwrite
Read message$ : If message ="EOF" Then Goto endwrite
WriteString printer, message$+Chr$(13)+Chr$(10) ; Text + linefeed
Goto printerwrite
.endwrite
WriteString printer, Chr$(12)    ; Formfeed
CloseFile printer
Else
RuntimeError "Nope"
EndIf
End
Data "Hallo world !"
Data "This seems OK."
Data "Sure thing."
Data "EOF"
