;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;                                        Powerbar example                                    ;;;
;;;                                        By : Nacho Daddy                                    ;;;
;;;                                          July 16,2004                                      ;;;
;;;                                                                                            ;;;
;;;          Feel free to use the option I included for floating point variables, this will enable  ;;;
;;; the writer to expand the choice from 100 power options to 400(if you use .25 for example). ;;;
;;;                                                                                            ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;initialization of graphics and variables 
Graphics 800,600,16,2

;;;;;;;;;;;
;COPY HERE;
;;;;;;;;;;;
Global lvl_power# = 0           ;this is the power variable
Global lvl_x# = 300             ;u dont need to mess with this
;this is the variable that controls height of power level, it has height of 1 right now
;because power level is 1
Global lvl_y# = 350            ;starting point of bar
Global lvl_width# = 25             ;dont mess with this
Global lvl_height# = -1        ;starting height of bar
Global border_x# = lvl_x# - 1   ;dont mess with this
Global border_y# = lvl_y# - 100 ;or this
Global lvl_up = True           ;or this
Global lvl_down                ;lol....or this
Global lvl_interval# = 1       ;this is somethin u can definetaly mess with :)
;;;;;;;;;;
;END COPY;
;;;;;;;;;;



;1st game loop (because the next loop will be when u run the game: Ex.the ball going into net)
SetBuffer BackBuffer()

While Not KeyHit(1) 
powerbar()
If MouseHit(1) Then Goto actual_game ;if u click the mouse, your power level is recorded and u go to the actual game
Flip : Cls 
Wend


;2nd game loop (the ball going into the net)
.actual_game
SetBuffer FrontBuffer()
Cls 
Color 255,255,255
Text 400,290,"Your game will go right here.",True,False 
Text 400,310,"Power: " + lvl_power,True,False 
;you will insert the variable lvl_power into your physics calculations to determine power of the ball being kicked
WaitKey 


;;;;;;;;;;;
;COPY HERE;
;;;;;;;;;;;
Function powerbar()
Color 255,255,255
Rect border_x#,border_y# - 1,lvl_width# + 2,101,0
Color 255,0,0
Rect lvl_x#,lvl_y#,lvl_width#,lvl_height#,1
If lvl_up = True Then 
     If lvl_y# = border_y# + 1 Then
          lvl_up = False 
          lvl_down = True 
     EndIf 
     lvl_y# = lvl_y# - lvl_interval#
     lvl_height# = lvl_height# + lvl_interval#
     lvl_power# = lvl_power# + lvl_interval#
Else If lvl_down = True Then 
     If lvl_y# = border_y# + 98 Then 
          lvl_up = True 
          lvl_down = False 
     EndIf 
     lvl_y# = lvl_y# + lvl_interval#
     lvl_height# = lvl_height# - lvl_interval#
     lvl_power# = lvl_power# - lvl_interval#
EndIf 
End Function
;;;;;;;;;;
;END COPY;
;;;;;;;;;; 
