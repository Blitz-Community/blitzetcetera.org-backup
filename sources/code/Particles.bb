; Particle Functions     ; 
;                        ;
;       by               ;
;                        ;
;                        ;
;Rafael_the_GameCreator! ;
;                        ;
;                        ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




Type blows    ; the type of the particles

Field image    
Field frm       
Field dir      
Field speed     
Field jumppower#  
Field gravity#  
Field x,y
Field chooser

End Type 



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;  Function set_blows  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;arguments:
; 
;  X - it's X start position on the screen 
;  Y - it's Y start position on the screen 
;  animimage - the image containing all the particle types that you want (you load it with LoadAnimImage()command)                                   
;  amount - the number of particles you want 
;  frames - the max number of frames in the image that you've loaded
;
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                                                                                             
Function set_blows(x,y,animimage,amount,frames)

For t = 0 To amount-1

blow.blows = New blows

blow\frm = Rnd(0,(frames-1))  

blow\chooser = Rnd(0,1) 

Select blow\chooser

Case 0 
blow\dir = 1
Case 1
blow\dir = -1

End Select

blow\jumppower = Rnd(2,4)
blow\x = x
blow\y = y
blow\gravity = Rnd(0.1,0.10)

blow\speed = Rnd(1.1,3.1)

blow\image = animimage

Next 

End Function 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;  Function update_blows()   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; Call this function in your main loop and it'll update all the 
; particles.
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


Function update_blows()

For blow.blows = Each blows
 can_delete = 0

 blow\jumppower = blow\jumppower - blow\gravity
 blow\y = blow\y - blow\jumppower

If blow\y > (GraphicsHeight+6) Then 
 can_delete = 1
EndIf 

 If blow\dir = 1 Then  
  blow\x = blow\x + blow\speed
   If blow\x > (GraphicsWidth+6) Then can_delete = 1
 EndIf 


If blow\dir = -1 Then  
  blow\x = blow\x - blow\speed
   If blow\x < -6 Then can_delete = 1
 EndIf 


If can_delete = 1 Then
    Delete blow 
Else
    DrawImage blow\image,blow\x,blow\y,blow\frm 
EndIf

Next 


End Function 

; test
