; -- Get key name indexed by scancode (237 keys supported, numbered 1-237 including unassigned spacer keys)
; Accepts a scancode as an integer parameter and returns a string containing the name of the scancode key.
Function get_key_name$( scancode )
Restore key_names
For i = 1 To scancode
	Read temp_string$
Next
Return temp_string
End Function
;^^^^^^
; -- Data for the above function.
.key_names
Data "ESCAPE KEY","1","2","3","4","5","6","7","8","9","0","-","="
Data "BACKSPACE","TAB","Q","W","E","R","T","Y","U","I","O","P","[","]","ENTER"
Data "LEFT CONTROL","A","S","D","F","G","H","J","K","L",";","'","TILDE &amp; ACCENT GRAVE","LEFT SHIFT","\"
Data "Z","X","C","V","B","N","M",",",".","/","RIGHT SHIFT"
Data "* (Numeric Keypad)","LEFT ALT","SPACE","CAPITAL","F1","F2","F3","F4","F5","F6","F7","F8","F9"
Data "F10","PAUSE","SCROLL LOCK","7 (Numeric Keypad)","8 (Numeric Keypad)","9 (Numeric Keypad)"
Data "- (Numeric Keypad)","4 (Numeric Keypad)","5 (Numeric Keypad)","6 (Numeric Keypad)","+ (Numeric Keypad)"
Data "1 (Numeric Keypad)","2 (Numeric Keypad)","3 (Numeric Keypad)","0 (Numeric Keypad)",". (Numeric Keypad)"
Data "","","OEM_102","F11","F12","","","","","","","","","","","","F13","F14","F15","","","","","","","","",""
Data "KANA","","","ABNT_C1","","","","","","CONVERT",""
Data "NOCONVERT","","YEN","ABNT_C2 Numpad","","","","","","","","","","","","","",""
Data "= (numeric keypad)","","","PREVIOUS TRACK","AT"
Data ":","UNDERLINE","KANJI","STOP","AX Japan AX","UNLABELED",""
Data "NEXT TRACK","","","ENTER (Numeric Keypad)","RIGHT CONTROL","","","MUTE","CALCULATOR","PLAY/PAUSE","","MEDIA STOP","","","","","","","","","","VOLUME DOWN",""
Data "VOLUME UP","","WEB HOME",", (Numeric Keypad)","","/ (Numeric Keypad)","","SYSREQ","RIGHT ALT","","","","","","","","","","","","","NUMLOCK",""
Data "HOME","UP ARROW","PAGE UP","","LEFT ARROW",""
Data "RIGHT ARROW","","END","DOWN ARROW","PAGE DOWN","INSERT"
Data "DELETE","","","","","","","","LEFT WINDOWS KEY","RIGHT WINDOWS KEY","APPS MENU KEY","SYSTEM POWER","SYSTEM SLEEP","","","","SYSTEM WAKE",""
Data "WEB SEARCH","WEB FAVORITES","WEB REFRESH","WEB STOP","WEB FORWARD","WEB BACK","MY COMPUTER"
Data "MAIL","MEDIA SELECT"
;^^^^^^
