;-----------------------------------------------------------
; Function RoundText Give a text a differend back color 
; Pass X and Y position and the texts  you want to print.
; After that i used color names instead of RGB color notation
; See the function SetColor for mor detail
;
; Remark this wil not flip screens and draw on the active viewport
; setup by the user.
;-----------------------------------------------------------
Function RoundText (XPos,YPos,Label$,RoundColor$,TextColor$)
SetColor (RoundColor$) ; New function see function for detail
For x = -1 To 1
For y = -1 To 1
Text Xpos+x,Ypos+y,Label$
Next 
Next 
SetColor (TextColor$)
Text XPos,YPos,Label$
End Function


; Include file for making color by using the names insted of using RGB value
; Intial colors are in now.
; --------------------------------------------------------------
; Put this part in front of the program. It wil execute one time to read in al the data.
; KEEP IN MIND that this part may not to place in any kind of loop. 
;---------------------------------------------------------------
Dim Colors$(500)	; Text colors
Dim NumColors(500,2)	; The numbers
Global TotalColors = 18  ;<---- This number is the total of named colors. Ajust it to your needs.
Restore ColorList
For x = 0 To Totalcolors
	Read Colors$(x)
	Read NumColors(x,0)
	Read NumColors(x,1)
	Read NumColors(x,2)
Next

Graphics 800,600
SetFont LoadFont("Arial",20)
RoundText 10,10,"Hi there!","Red","Yellow"

;--------------------------------------------------------
; Function SetColor 
; Give a color name in string. Unknown color name result in White color
;--------------------------------------------------------
Function SetColor (ColorName$)
	ColorNames$ = Upper$ (ColorNames$)
	Color 255,255,255 ; Pre define color if not found the name then standard white returns
	For x = 0 To TotalColors
		If Upper$ (ColorName$) = Upper$(Colors$(x))
			Color NumColors(x,0),NumColors(x,1),NumColors(x,2)
		End If
	Next
End Function


;-----------------------------------------------------------
; For now its a short list but in the future its expand. 
; You input is also important. 
; If you add colors please repost it back to BB.
;-----------------------------------------------------------
.ColorList
; Standard color list (7)
Data "White",255,255,255
Data "Red",255,0,0
Data "Green",0,255,0
Data "Blue",0,0,255
Data "Black",0,0,0
Data "Cyan",0,255,255
Data "Magenta",255,0,255 

; Half way colors
Data "Gray",125,125,125
; Most used colors name (11)
Data "Silver",176,176,176 
Data "Dark_grey",100,100,100 
Data "Pale_green",152,251,152 
Data "Light_sky_blue",135,206,250 
Data "Orange",255,165,0 
Data "Brown",200,150,100
Data "Pale_pink",255,200,200 
Data "Light_grey",170,170,170 
Data "Mid_red",255,63,63 
Data "Light_red",255,127,127 
Data "Pink",255,191,191

