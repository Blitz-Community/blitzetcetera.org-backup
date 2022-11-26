; prevent windows screensaver

; the following code should be executed in the mainloop, or 
; may be in a function when the variable is global.

if millisecs()>prevent_screensaver
 prevent_screensaver=millisecs()+30000
 movemouse mousex(),mousey()
endif
