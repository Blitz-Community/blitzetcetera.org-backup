
;----- Code To Create a Labox --

;CreateLabox( b_text$, b_x, b_y, b_width, b_height, lab_width, lab_style, b_parent)

;---- To Auto Ajust
;SetGadgetLayout labox,1,1,1,1
;-------------------------------
;b_text$     is for the "Label" text
;b_x         its X Location
;b_y         its Y Location
;b_width     its width
;b_height    its height
;b_parent    its parent
;lab_width   its the label Text width in this direction ->>
;lab_style   0 - No border (default) 1 - Flat border 2 - No border? 3 - 3d sunken border 

;--------------------------------
;Heres an example:
;------------------
; Create a window and some labels 
win=CreateWindow("Labox Test",100,100,300,250);,0),17)
 
labox = CreateLabox(" Label",5,5,160,100,30,0,win) ; Labox Function.

SetGadgetLayout labox,1,1,1,1

Repeat 
Until WaitEvent()=$803 
End ; bye!  

Function CreateLabox(b_text$,b_x,b_y,b_width,b_height,lab_width,lab_style,b_parent)
	; Labox stand for Labeled Box :D
	Local pan = CreatePanel(b_x, b_y, b_width,b_height, b_parent)
	temp = CreatePanel( 0,5, b_width, 4, pan, 1)             ; Top
	SetGadgetLayout temp,1,1,1,0
	temp = CreatePanel( 0, 5+2, 4, b_height-3, pan, 1)        ; Left
	SetGadgetLayout temp,1,0,1,1
	temp = CreatePanel( 0, b_height-2, b_width,4, pan, 1)   ; Bottom
	SetGadgetLayout temp,1,1,0,1
	temp = CreatePanel( b_width-3, 5, 4, b_height, pan, 1)  ; Right
	SetGadgetLayout temp,0,1,1,1
	temp = CreateLabel( b_text$, 10, 0, lab_width, 16, pan, lab_style)   ; Text
	SetGadgetLayout temp,1,0,1,0
	Return pan
End Function
;-----------------------
;I hope this is helpful for you, until BlitzPlus can do it on its own.