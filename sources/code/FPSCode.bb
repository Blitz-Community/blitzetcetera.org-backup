; Main loop start

Start = MilliSecs()

; Your main loop code goes here
Print "Current FPS: " + CurFPS#
; Your main loop code goes here

CurFPS# = 1000.0 / (MilliSecs() - Start)

; Main loop end