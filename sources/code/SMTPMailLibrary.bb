;********************************************************************************************
;***                                Blitz Mail Library
;***                                   Version 2.0
;********************************************************************************************

;Version 1.0
;Release...Err...2001 sometime :o/

;Version 1.1
;Release 03/12/04 - Fixed multi Line response bug.
;                   Added Base64 decoder and SASL 'LOGIN' authorisation.

;Version 2.0
;Release 05/05/05 - Rewrote send_mail$(), it should be easier to follow and add stuff now.
;                   Added SASL 'CRAM-MD5' authorisation.
;                   Added SASL 'PLAIN' authorisation.
;                   Added the option of supplying a server port.

Const AUTH_NONE = 0
Const AUTH_PLAIN = 1
Const AUTH_LOGIN = 2
Const AUTH_CRAMMD5 = 4
Const AUTH_ALL = AUTH_PLAIN Or AUTH_LOGIN Or AUTH_CRAMMD5

Const MAIL_DEBUG = False

Dim MD5_k(63), MD5_r(15), MD5_data(1)

init_MD5()

SeedRnd MilliSecs()

Type html_image
    Field filename$, cid$, idata$
End Type

Type inc_data
    Field filename$, idata$
End Type

; Sends the processed mail.
; mailfrom$   = A string holding the senders mail address
; fromname$   = A string holding the senders name
; mailto$     = A string holding the recipients mail address
; toname$     = A string holding the recipients name - optional
; subject$    = A string holding the subject of the mail
; body$       = A string holding the processed MIME mail
; mailserver$ = A string holding the ESMTP/SMTP servers IP address or FQDN.
;               You can add an optional port number (default = 25).
;               example >>> "mail.myisp.net" Or "mail.myisp.net:25"
; username$   = A string holding the account username
; userpass$   = A string holding the account password
; This function returns a string containing ;success; if successful or an error message if not
Function send_mail$(mailfrom$, fromname$, mailto$, toname$ = "", subject$, body$, mailserver$, username$="", userpass$="", auth_type = AUTH_NONE)
    Local server, code, resp$, client_ip$, ip_count, port
    
    If toname$ = "" Then toname$ = mailto$
    
    ip_count = CountHostIPs("")
    If ip_count
        client_ip$ = "[" + DottedIP$(HostIP(1)) + "]"
    Else
        client_ip$ = "[127.0.0.1]"  
    EndIf
    
    If Instr(mailserver$, ":")
        port = Right$(maileserver$, (Len(mailserver$) - (Instr(mailserver$, ":") + 1)))
    Else
        port = 25
    EndIf
    
    server = OpenTCPStream(mailserver$, port)

    ;Is there a connection?
    If Not server Then Return "Failed to connect to `" + mailserver$ + "`"
    
    resp$ = get(server) 
    
    ;Is the mail server available?
    Select get_code(resp$)
        Case 421
            CloseTCPStream server
            Return "Service not available"; >>>>>> ERROR EXIT
        
        Case 220 ; Server available, lets get logged on.
            put(server, "EHLO " + client_ip$)           
            resp$ = get(server)
            
            ;An ESMTP server?
            If get_code(resp$) = 250
                ; If a username and password supplied, do SASL stuff.
                If (username$ <> "") And (userpass$ <> "") And (auth_type <> AUTH_NONE)                 
                    Repeat
                        Select True
                            Case (auth_type And AUTH_CRAMMD5) ; Do CRAM-MD5
                                auth_type = auth_type And ~AUTH_CRAMMD5
                                
                                put(server, "AUTH CRAM-MD5") ; CRAM-MD5?        
                                resp$ = get(server)
                                
                                If get_code(resp$) = 334
                                    put(server, base64_enc(username$ + " " + cram_md5(userpass$, base64_dec(get_message(resp$)))))
                                    resp$ = get(server)
                                    
                                    If get_code(resp$) <> 235
                                        CloseTCPStream server
                                        Return resp$ ; >>>>>> ERROR EXIT
                                    EndIf
                                EndIf

                            Case (auth_type And AUTH_LOGIN) ; Do LOGIN
                                auth_type = auth_type And ~AUTH_LOGIN
                                
                                put(server, "AUTH LOGIN") ; LOGIN?      
                                resp$ = get(server)

                                If get_code(resp$) = 334
                                    While get_code(resp$) = "334"
                                        Select Lower$(base64_dec(get_message(resp$)))
                                            Case "username:"
                                                put(server, base64_enc(username$, 0))
                                                resp$ = get(Server)
                                                
                                            Case "password:"
                                                put(server, base64_enc(userpass$, 0))
                                                resp$ = get(Server)                                     
                                        End Select
                                    Wend    
                                    
                                    If get_code(resp$) <> 235
                                        CloseTCPStream server
                                        Return resp$ ; >>>>>> ERROR EXIT
                                    EndIf
                                EndIf
                                
                            Case (auth_type And AUTH_PLAIN) ; Do PLAIN
                                auth_type = auth_type And ~AUTH_PLAIN
                                
                                put(server, "AUTH PLAIN") ; PLAIN?
                                resp$ = get(server)
                                
                                If get_code(resp$) = 334
                                    put(server, base64_enc(Chr$(0) + username$ + Chr$(0) + userpass$ + Chr$(0), 0))
                                    resp$ = get(Server)

                                    If get_code(resp$) <> 235
                                        CloseTCPStream server
                                        Return resp$ ; >>>>>> ERROR EXIT
                                    EndIf
                                EndIf
                                
                            Case auth_type = AUTH_NONE
                                CloseTCPStream server
                                Return "Unsupported SASL Authorisation type" ; >>>>>> ERROR EXIT
                        End Select
                    Until get_code(resp$) = 235                                     
                EndIf
            Else
                put(server, "HELO " + client_ip$)
                
                resp$ = get(server)
                If get_code(resp$) <> 250
                    CloseTCPStream server
                    Return resp$ ; >>>>>> ERROR EXIT
                EndIf
            EndIf
            
        Default
            CloseTCPStream server
            Return "Time Out" ; >>>>>> ERROR EXIT
    End Select
    
    ;Now we`re logged in, we can send mail
    put(server, "MAIL FROM: <" + mailfrom$ + ">")
    resp$ = get(server)
    
    If get_code(resp$) <> 250
        CloseTCPStream server
        
        If  get_code(resp$) = 501
            Return "Email sender not specified (or invalid address)" ; >>>>>> ERROR EXIT
        Else
            Return resp$ ; >>>>>> ERROR EXIT
        EndIf
    EndIf
    
    put(server, "RCPT TO: <" + mailto$ + ">")
    resp$ = get(server)
    
    If get_code(resp$) <> 250
        CloseTCPStream server
        
        If  get_code(resp$) = 501
            Return "Email recipient not specified (or invalid address)" ; >>>>>> ERROR EXIT
        Else
            Return resp$ ; >>>>>> ERROR EXIT
        EndIf
    EndIf
    
    put(server, "DATA")
    resp$ = get(server)
    
    If get_code(resp$) <> 354
        CloseTCPStream server
        Return resp$ ; >>>>>> ERROR EXIT
    EndIf
    
    put(server, "From: " + qt$ + mailname$ + qt$ + " <" + mailfrom$ + ">")
    put(server, "To: " + qt$ + toname$ + qt$ + " <" + mailto$ + ">")
    put(server, "Subject: " + subject$)
    put(server, "X-Mailer: Blitz Mail Library v2.0")
    put(server, body$)
    put(server, ".")
    resp$ = get(server)
    
    If get_code(resp$) <> 250
        CloseTCPStream server
        Return resp$ ; >>>>>> ERROR EXIT
    EndIf

    put(server, "QUIT")
    resp$ = get(server)
    
    If get_code(resp$) = 221
        Return "success" ; >>>>>> SUCCESS EXIT
    Else
        Return resp$ ; >>>>>> ERROR EXIT
    EndIf
End Function    

; Encodes the processed HTML text and/or plain text into MIME format
; html$ = A string holding the processed HTML text - optional
; plain$= A string holding the plain text - optional
; This function returns a string containing the MIME mail body text
;
; You`re probably wondering why BOTH of the parameters are optional?
; If you wanted to send a blank mail with just an attachment.
; You still need to run this function to MIME encode the attachment - build_mime$()
Function build_mime$(html$="", plain$="")
    Local out$, nl$ = Chr$(13) + Chr$(10), qt$ = Chr$(34)
    Local bound$ = "=_PART_00_" + unique()
    Local bound1$ = "=_PART_01_" + unique()
    Local bound2$ = "=_PART_02_" + unique()
    If plain$ = "" And html$ <> ""
        plain$ = "This is a HTML mail. Your mail app may not be able to display the main body of this mail."
    EndIf
    
    out$ = out$ + "MIME-Version: 1.0" + nl$
    out$ = out$ + "Content-Type: multipart/mixed;" + nl$ + Chr$(9)
    out$ = out$ + "boundary=" + qt$ + bound$ + qt$ + nl$ + nl$
    out$ = out$ + "This is a MIME encoded message" + nl$ + nl$
    out$ = out$ + "--" + bound$ + nl$
    If html$ <> ""
        If First html_image = Null
            out$ = out$ + "Content-Type: multipart/alternative;" + nl$ + Chr$(9)
            out$ = out$ + "boundary=" + qt$ + bound1$ + qt$ + nl$ + nl$ + nl$
            out$ = out$ + "--" + bound1$ + nl$
            out$ = out$ + "Content-Type: text/plain; charset=" + qt$ + "iso-8859-1" + qt$ + nl$
            out$ = out$ + "Content-Transfer-Encoding: quoted-printable" + nl$ + nl$
            out$ = out$ + qp_enc$(plain$) + nl$
            out$ = out$ + "--" + bound1$ + nl$ 
            out$ = out$ + "Content-Type: text/html; charset=" + qt$ + "iso-8859-1" + qt$ + nl$
            out$ = out$ + "Content-Transfer-Encoding: quoted-printable" + nl$ + nl$
            out$ = out$ + html$ + nl$
        Else
            out$ = out$ + "Content-Type: multipart/related;" + nl$ + Chr$(9)
            out$ = out$ + "boundary=" + qt$ + bound1$ + qt$ + nl$ + nl$ + nl$
            out$ = out$ + "--" + bound1$ + nl$
            out$ = out$ + "Content-Type: multipart/alternative;" + nl$ + Chr$(9)
            out$ = out$ + "boundary=" + qt$ + bound2$ + qt$ + nl$ + nl$ + nl$
            out$ = out$ + "--" + bound2$ + nl$
            out$ = out$ + "Content-Type: text/plain; charset=" + qt$ + "iso-8859-1" + qt$ + nl$
            out$ = out$ + "Content-Transfer-Encoding: quoted-printable" + nl$ + nl$
            out$ = out$ + qp_enc$(plain$) + nl$
            out$ = out$ + "--" + bound2$ + nl$
            out$ = out$ + "Content-Type: text/html; charset=" + qt$ + "iso-8859-1" + qt$ + nl$
            out$ = out$ + "Content-Transfer-Encoding: quoted-printable" + nl$ + nl$
            out$ = out$ + html$ + nl$
            out$ = out$ + "--" + bound2$ + "--" + nl$ + nl$
            For image.html_image=Each html_image
                out$ = out$ + "--" + bound1$ + nl$
                out$ = out$ + "Content-Type: image/" + Right$(image.html_image\filename$, 3)
                out$ = out$ + "; name=" + qt$ + image.html_image\filename$ + qt$ + nl$
                out$ = out$ + "Content-Transfer-Encoding: base64" + nl$
                out$ = out$ + "Content-ID: <" + image.html_image\cid$ + ">" + nl$ + nl$
                out$ = out$ + image.html_image\idata$ + nl$
            Next
        EndIf
        out$ = out$ + "--" + bound1$ + "--" + nl$ + nl$
    Else
        out$ = out$ + "Content-Type: text/plain;" + nl$ + nl$
        out$ = out$ + "Content-Transfer-Encoding: quoted-printable" + nl$
        out$ = out$ + qp_enc$(plain$) + nl$
    EndIf
        
    If First inc_data <> Null
        For inc.inc_data=Each inc_data
            out$ = out$ + "--" + bound$ + nl$
            out$ = out$ + "Content-Type: application/octete-stream"
            out$ = out$ + "; name=" + qt$ + inc.inc_data\filename$ + qt$ + nl$
            out$ = out$ + "Content-Transfer-Encoding: base64" + nl$
            out$ = out$ + "Content-Disposition: attachment; filename=" + qt$ + inc.inc_data\filename$ + qt$ + nl$ + nl$
            out$ = out$ + inc.inc_data\idata$ + nl$
        Next
    EndIf
    
    out$ = out$ + "--" + bound$ + "--" + nl$
    
    Delete Each html_image
    Delete Each inc_data
    
    Return out$
End Function

; Converts an external file into a string
; inp$ = A string containing the filepath to the required file
; This function returns a string containing the files data
Function file_to_string$(inp$)
    Local file, out$
    
    file = ReadFile(inp$)
    If (Not file) Then RuntimeError "Couldn't open '" + inp$ + "' for encoding."
    
    Repeat
        out$ = out$ + Chr$(ReadByte(file))
    Until Eof(file)
    
    CloseFile(file)
    
    Return out$
End Function

; Encodes files as attachments
; file$ = A string containing the filepath to the required file
Function process_include$(file$)
    inc.inc_data = New inc_data
    inc.inc_data\filename$ = file$
    inc.inc_data\idata$ = base64_enc$(file_to_string$(file$))
End Function

; Scans the HTML string loads and encodes any images and replaces their filename with a ContentIDentifier
; inp$  = A string containing the HTML text
; dir$  = A string containing the path to the original HTML file - optional (if the HTML file is in the same directory
;                                                                           as your code)
; This function returns a string containing the processed HTML text
Function process_html$(inp$, dir$="")
    Local out$ = inp$, img$, ifrom, ito = 1, fnd
    If dir$ <> "" And Right$(dir$, 1) <> "\" Then dir$ = dir$ + "\"
    
    Repeat
        ifrom = Instr(Lower$(inp$), "<img", ito)
        If ifrom
            ifrom = Instr(Lower$(inp$), "src", ifrom + 4) + 3
            ifrom = Instr(Lower$(inp$), Chr$(34), ifrom) + 1
            ito = Instr(Lower$(inp$), Chr$(34), ifrom)
            fnd = False
            For image.html_image=Each html_image
                If image.html_image\filename$ = Mid$(inp$, ifrom, ito - ifrom) Then fnd = True
            Next
            If (Not fnd)
                image.html_image = New html_image
                image.html_image\filename$ = Mid$(inp$, ifrom, ito - ifrom)      
                image.html_image\cid$ = unique$()
                image.html_image\idata$ = base64_enc$(file_to_string$(dir$ + image.html_image\filename$))
                out$ = Replace$(out$, image.html_image\filename$, "cid:" + image.html_image\cid$)
            EndIf 
        EndIf
    Until (Not ifrom)
    
    Return qp_enc$(out$)
End Function

; Encodes a string using the base64 algorithm
; inp$ = A string containing the data to be encoded
; add_nl = 1 - adds a newline seguence at the end of the encoded string, 0 - no newline (if string is < 76 chars)
; This function returns a string containing the encoded data
; You shouldn`t need to call this directly. But feel free.
Function base64_enc$(inp$, add_nl=1)
    Local b64_enc$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    Local nl$ = Chr$(13) + Chr$(10)
    Local out$, trp$, char, i = 1
        
    Repeat
        trp$ = Mid$(inp$, i, 3)
    
        Select Len(trp$)
            Case 3
                out$ = out$ + Mid$(b64_enc$, (Asc(Mid$(trp$, 1, 1)) Shr 2) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, (((Asc(Mid$(trp$, 1, 1)) Shl 4) Or (Asc(Mid$(trp$, 2, 1)) Shr 4)) And $3f) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, (((Asc(Mid$(trp$, 2, 1)) Shl 2) Or (Asc(Mid$(trp$, 3, 1)) Shr 6)) And $3f) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, (Asc(Mid$(trp$, 3, 1)) And $3f) + 1, 1)
            Case 2
                out$ = out$ + Mid$(b64_enc$, (Asc(Mid$(trp$, 1, 1)) Shr 2) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, (((Asc(Mid$(trp$, 1, 1)) Shl 4) Or (Asc(Mid$(trp$, 2, 1)) Shr 4)) And $3f) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, ((Asc(Mid$(trp$, 2, 1)) Shl 2) And $3f) + 1, 1)
                out$ = out$ + "="
            Case 1
                out$ = out$ + Mid$(b64_enc$, (Asc(Mid$(trp$, 1, 1)) Shr 2) + 1, 1)
                out$ = out$ + Mid$(b64_enc$, ((Asc(Mid$(trp$, 1, 1)) Shl 4) And $3f) + 1, 1)
                out$ = out$ + "=="
        End Select
    
        i = i + 3
        char = char + 4
        If char = 76
            out$ = out$ + nl$
            char = 0
        EndIf
    Until i > Len(inp$)
    If char And add_nl Then out$ = out$ + nl$
    
    Return out$
End Function

; Decodes a string that`s been encoded with the base64 algorithm
; inp$ = A string containing the encoded data
; This function returns a string containing the decoded data
; You shouldn`t need to call this directly. But feel free.
Function base64_dec$(inp$)
    Local b64_enc$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
    Local out$, oct, i = 1, qc, char 

    Repeat
        char = Instr(b64_enc$, Mid$(inp$, i, 1))
        If char > 0
            If char = 65 Then char = 1
            oct = (oct Shl 6) Or ((char - 1) And $3f)
            qc = qc + 1
        EndIf
        
        If qc = 4
            out$ = out$ + Chr$((oct Shr 16) And $ff) + Chr$((oct Shr 8) And $ff) + Chr$(oct And $ff)
            
            oct = 0
            qc = 0
        EndIf
    
        i = i + 1
    Until i > Len(inp$)
    
    Return out$
End Function
        
; Encodes a string using the quoted printable algorithm
; inp$ = A string containing the text to be encoded
; This function returns a string containing the encoded text
; You shouldn`t need to call this directly. But feel free.
Function qp_enc$(inp$)
    Local qp_asc$ = Chr$(9) + " !#$%&'()*+,-./0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
    Local nl$ = Chr$(13) + Chr$(10)
    Local out$, cnt, char$, i
    
    Repeat
        i = i + 1
        char$ = Mid$(inp$, i, 1)
        If Instr(qp_asc$, char$)
            out$ = out$ + char$
            cnt = cnt + 1
        Else
            If Mid$(inp$, i, 2) <> nl$
                If cnt > 72
                    out$ = out$ + "=" + nl$
                    cnt = 0
                EndIf
                out$ = out$ + "=" + Right$(Hex$(Asc(char$)), 2)
                cnt = cnt + 3
            Else
                out$ = out$ + nl$
                i = i + 1
                cnt = 0
            EndIf
        EndIf
        If cnt = 75
            out$ = out$ + "=" + nl$
            cnt = 0
        EndIf
    Until i >= Len(inp$)
    
    If cnt Then out$ = out$ + nl$
    
    Return out$
End Function

; Spits out a 24 digit (96 bit) random(ish) hexadecimal string
Function unique$()
    Local out$, i
    
    For i= 1 To 12
        out$ = out$ + Right$(Hex$(Rand(1, 255)), 2)
    Next
    
    Return out$
End Function

; Write a string to the given stream with debug log output
Function put(stream, out$)  
    WriteLine stream, out$
    
    If MAIL_DEBUG
        DebugLog "BLITZ  : " + out$
    EndIf
End Function

; Read a string from the given stream with debug log output
Function get$(stream)
    Local in$
    
    in$ = ReadLine(stream)
    While Mid$(in$, 4, 1) = "-"
        If MAIL_DEBUG
            DebugLog "SERVER : " + in$
        EndIf
        
        in$ = ReadLine(stream)
    Wend

    If MAIL_DEBUG
        DebugLog "SERVER : " + in$
    EndIf
        
    Return in$
End Function

; The next two are pretty pointless, they just make the send_mail$() code easier to read.
Function get_code(inp$)
    Return Left(inp$, 3)
End Function

Function get_message$(inp$)
    Return Right$(inp$, Len(inp$) - 4)
End Function

; CRAM-MD5 and associated functions
Function cram_md5$(key$, tag$)
    Local ikey$
    
    If Len(key$) > 64
        ikey$ = hex2char$(md5$(key$))
    Else
        ikey$ = key$ + String(Chr$(0), 64 - Len(key$))
    EndIf
        
    Return md5$(xor_string$(ikey$, $5C) + hex2char$(md5$(xor_string$(ikey$, $36) + tag$)))
End Function

Function xor_string$(a$, b)
    Local c, out$
    
    For c=1 To Len(a$) 
        out$ = out$ + Chr$(Asc(Mid$(a$, c, 1)) Xor b)
    Next
    
    Return out$
End Function

Function hex2char$(hexin$)
    Local c, out$
    
    For c=1 To Len(hexin$) Step 2
        out$ = out$ + Chr$(hex2dec(Mid(hexin$, c, 2)))
    Next
    
    Return out$
End Function

Function hex2dec(hexin$)
    Local c, dec, hexval$ = "0123456789ABCDEF"
    
    For c=1 To Len(hexin$)
        dec = (dec Shl 4) Or (Instr(hexval$, Upper$(Mid$(hexin$, c, 1))) - 1)
    Next
    
    Return dec
End Function

Function init_MD5()
    Local c
    
    Restore MD5_constdata
    
    For c=0 To 63
        Read MD5_k(c)
    Next
    
    For c=0 To 15
        Read MD5_r(c)
    Next
End Function 

.MD5_constdata
Data $D76AA478, $E8C7B756, $242070DB, $C1BDCEEE, $F57C0FAF, $4787C62A
Data $A8304613, $FD469501, $698098D8, $8B44F7AF, $FFFF5BB1, $895CD7BE
Data $6B901122, $FD987193, $A679438E, $49B40821, $F61E2562, $C040B340
Data $265E5A51, $E9B6C7AA, $D62F105D, $02441453, $D8A1E681, $E7D3FBC8
Data $21E1CDE6, $C33707D6, $F4D50D87, $455A14ED, $A9E3E905, $FCEFA3F8
Data $676F02D9, $8D2A4C8A, $FFFA3942, $8771F681, $6D9D6122, $FDE5380C
Data $A4BEEA44, $4BDECFA9, $F6BB4B60, $BEBFBC70, $289B7EC6, $EAA127FA
Data $D4EF3085, $04881D05, $D9D4D039, $E6DB99E5, $1FA27CF8, $C4AC5665
Data $F4292244, $432AFF97, $AB9423A7, $FC93A039, $655B59C3, $8F0CCC92
Data $FFEFF47D, $85845DD1, $6FA87E4F, $FE2CE6E0, $A3014314, $4E0811A1
Data $F7537E82, $BD3AF235, $2AD7D2BB, $EB86D391

Data 7, 12, 17, 22,  5, 9, 14, 20,  4, 11, 16, 23,  6, 10, 15, 21

Function MD5$(in$)
    Local h0 = $67452301, h1 = $EFCDAB89, h2 = $98BADCFE, h3 = $10325476
    Local chunkStart, intCount, i, a, b, c, d, f, t
                                
    intCount = (((Len(in$) + 8) Shr 6) + 1) Shl 4
    Dim MD5_data(intCount - 1)
    
    For c=0 To (Len(in$) - 1)
        MD5_data(c Shr 2) = MD5_data(c Shr 2) Or (Asc(Mid(in$, c + 1, 1)) Shl ((c And 3) Shl 3))
    Next
    MD5_data(c Shr 2) = MD5_data(c Shr 2) Or ($80 Shl ((c And 3) Shl 3)) 
    MD5_data(intCount - 2) = Len(in$) * 8
    
    For chunkStart=0 To (intCount - 1) Step 16
        a = h0 : b = h1 : c = h2 : d = h3
                
        For i=0 To 15
            f = d Xor (b And (c Xor d))
            t = d
            
            d = c : c = b
            b = Rol((a + f + MD5_k(i) + MD5_data(chunkStart + i)), MD5_r(i And 3)) + b
            a = t
        Next
        
        For i=16 To 31
            f = c Xor (d And (b Xor c))
            t = d

            d = c : c = b
            b = Rol((a + f + MD5_k(i) + MD5_data(chunkStart + (((5 * i) + 1) And 15))), MD5_r((i And 3) + 4)) + b
            a = t
        Next
        
        For i=32 To 47
            f = b Xor c Xor d
            t = d
            
            d = c : c = b
            b = Rol((a + f + MD5_k(i) + MD5_data(chunkStart + (((3 * i) + 5) And 15))), MD5_r((i And 3) + 8)) + b
            a = t
        Next
        
        For i=48 To 63
            f = c Xor (b Or ~d)
            t = d
            
            d = c : c = b
            b = Rol((a + f + MD5_k(i) + MD5_data(chunkStart + ((7 * i) And 15))), MD5_r((i And 3) + 12)) + b
            a = t
        Next
        
        h0 = h0 + a : h1 = h1 + b
        h2 = h2 + c : h3 = h3 + d
    Next
    
    Dim MD5_data(1)
    
    Return Lower$(LEHex(h0) + LEHex(h1) + LEHex(h2) + LEHex(h3))    
End Function

Function Rol(val, shift)
    Return (val Shl shift) Or (val Shr (32 - shift))
End Function

Function LEHex$(val)
    Local c, out$, hexval$ = Hex$(val)
    
    For c = 7 To 1 Step -2
        out$ = out$ + Mid(hexval$, c, 2)
    Next
    
    Return out$
End Function

;************************************************************************
;***          Blitz Mail Library Demo
;************************************************************************

Graphics 640, 480, 0, 2

; Add the MIME library
; Get the required parameters
name_from$ = Input$("Enter your name : ")

Repeat
  mail_from$ = Input$("Enter your e-mail address : ")
Until mail_from$ <> "" And Instr(mail_from$, "@") > 0

Print

to_name$ = Input$("Enter the recipients name : ")

Repeat
  to_mail$ = Input$("Enter the recipients e-mail address : ")
Until to_mail$ <> "" And Instr(to_mail$, "@") > 0

Print

subject$ = Input$("Enter subject text : ")

Print

server$ = Input$("Enter SMTP server name (mail/smtp.yourisp.TLD) : ")

; Add 'text.zip' as an include
;process_include$("text.zip")

; We're using an external file for the HTML. So, lets convert it to a string
tmp_html$ = file_to_string$("htmlmail.htm");Any HTML page should do

; Lets scan the HTML string for images, load and encode them. Then insert their CID into the relevant image tags
; If the HTML was in a different directory to this code - tmp_html$ = process_html$(tmp_html$, "HTML directory\")
tmp_html$ = process_html$(tmp_html$)

; Now assemble all of the data into one MIME mail complete with relevant encoding.
html$ = build_mime$(tmp_html$, "Some Plain Text in case the receiving mail client can't cope with HTML.")

; The above could have been accomplished with...
;html$ = build_mime$(process_html$(file_to_string$("htmlmail.htm")), "Some Demo Text")

Print
Print "Sending Mail (for dial up networks you may have to connect manually first)"
Print
Delay 100
; Now send the mail

Print send_mail$(mail_from$, name_from$, to_mail$, to_name$, subject$, html$, server$)
; You may need to use...
; Print send_mail$(mail_from$, name_from$, to_mail$, to_name$, subject$, html$, server$, "account username", "account password", AUTH_ALL)

Print
Print "Any key to exit"
WaitKey()

End