;Userlib Declaration:
;.lib "user32.dll"
;SetWindowPos%(hwnd%,hWndInsertAfter%,x%,y%,cx%,cy%,wFlags%):"SetWindowPos"

wndMain = CreateWindow("Hello",100,100,400,300,0,5)
MakeWindowOnTop(wndMain)

Repeat

If WaitEvent() = $803 Then End

Forever

;+++++++++++++++++++++++++++++++++++++
;FUNCTION
;+++++++++++++++++++++++++++++++++++++

Function MakeWindowOnTop(wndBlitzHandle,ontop=1)	;Windows gadget handle and switch to be set. Defaults to 1. E.g. make ontop.

wndHandle = QueryObject(wndBlitzHandle,1)		;Gets the actual handle of the window to be used with API call.
SetWindowPos(wndHandle,ontop-2,0,0,0,0,1+2+40)		;Applies setting to window with constants at end (see below). 

;1: Keep Original Window Size
;2: Keep Orginal Pos.
;40: Keep Original Z-Order

End Function