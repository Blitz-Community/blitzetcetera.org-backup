
Graphics 640,480,0

foo$ = gl_input$(10,10,"what is your name? ")

SetColor 255,255,0 ; SetScale(2,2) 

DrawText "hello "+foo$+", how are you?",100,100

Flip
WaitMouse

End 


'-------------------------------------
Function gl_input$(x,y,prompt$ = "?")

	Repeat
	
		Cls
		DrawText prompt$+m$,10,10
		DrawText key,10,50
		hit_key = 0
		For key = 1 To 226
		
			hit_key = KeyHit(key)
			If hit_key 
				m$ = m$ + Chr(key)
			
				If key = KEY_ENTER
					Return m$
				EndIf
				If key = KEY_BACKSPACE
					l = Len(m$)
					m = m[..l-2]
				EndIf
			EndIf 
		Next
	
 	Flip
	Until KeyHit(KEY_ESCAPE)

End Function