; ?????????: ??? ??????? ???????????.
; ?????: Grisu
; ???????: MANIAK_dobrii
; ??????: 1.00

; ???? ??? ?????? ???????? ????????????.
;
; 1. ?????? ?????? ???? ??? ? ??????? "Configure()" ? "Start()".
;
; 2. ??????????? ????? ? ??????? ?????????? ? ".exe" ?? ".scr".
;
; 3. ?????? ???? ??????(& ????? ???? ????) ? ???????? ????? Windows(C:\Windows\ ????????)

; ????????? ???????? ?? ????????, ??????????? ? blitz-?:
;
; - ?????????? ???????? ??????????? ?????? ? ???? ???????????? ???????????? Windows.
;
; - ??? ??????? ??????, ??????? ???????? ????????? ?????????? ????.

AppTitle "Screensaver Tutorial1" ; ??? ????? ??????

; ?????
; ????? ????? ????????? ???? ?????? ??? Windows!!!.
; ??? ?????? ?????????? ?????????? ?? ??, ??? ????? ???????????.
; ??????????? ??? ?????? ?? ????? ??????, ?.?. "appdir" Blitz-? ??? "\bin".
; ????? ???? ?????? ?????? ???????!!! :)

ChangeDir SystemProperty$("appdir")

; ???????, ????? ????????? ??? ???? Windows 

If CommandLine$() <> "" Then ; ???? ???????? ???? ??: 
 If Upper(Left$(CommandLine$(),2)) = "/C" Then Configure() ; ???? ???????? ???? ?????????
 If Upper(Left$(CommandLine$(),2)) = "/S" Then Start() ; ??? ???? ???????? ??? ???????????? 
EndIf

End ; ????? ?????, bye bye(????. ??????:)(????. ???.))

; ???????? ???? ??? ???? ?????????.
; ???? ?????, ?? ????? ??????? ??????? ?????. ???????? ?????????? ?? VB, Delphi ??? ??????.
Function Configure()

; ?????????? ?????, ??????? ????? ????????, ???????? ?????????? ? ??????? ???????????...

End Function ; ????? Configure()

; ???????? ???? ??? ?????? ????????????.
Function Start()

 ; ?????? GFX-????? ? ?????? ???????... (????? ??????? ??????!)

 FlushKeys() ; ?????? ????? ?????
 FlushMouse() ; ?????? ????? ???? 
 MoveMouse 0,0 ; ??????? ???? ? 0,0 ??? ??????????? ????????

 Repeat ; ?????? ???????? ?????

  ; ??? ???? ??????? ????!

  Delay 1 ; ?? ????? ???????????? ??? 100% CPU!
 Until GetMouse() <> 0 Or MouseX() <> 0 Or MouseY() <> 0 Or GetKey() <> 0 ; ?????????, ??????? ?? ???????????? ????????? ??????, ??? ???? ?? ????? :)

End Function ; ????? Start() 