Graphics 640,480
SetBuffer BackBuffer()
done=False
While Not KeyHit(1)
	Cls
	Text 320,10,"RANDOM VERSION NUMBER GENERATOR",True,True
	If done Then
		Text 320,240,"Your random version number is",True,True
		Text 320,260,"v"+Rand(0,9)+"."+Rand(0,9)+Rand(0,9),True,True
		Text 320,460,"Press any key to generate a new version number",True,True
	Else
		Text 320,460,"Press any key to generate a version number",True,True
	EndIf
	WaitKey
	done=True
	Flip
Wend
