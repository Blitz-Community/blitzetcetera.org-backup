<code bb>
Graphics 800,600,32,2
SetBuffer BackBuffer()

Const R=60

Type Sphere2D
	Field x
	Field y
End Type

Function createSphere2D.Sphere2D(x,y)
	S.Sphere2D=New Sphere2D
	S\x=x
	S\y=y
	Return S
End Function

Function DrawALLSphere2D()
	For S.Sphere2D=Each Sphere2D
		Oval S\x-R,S\y-R,R*2,R*2
	Next
End Function

Function SaveLevel(name$)
	File=WriteFile(name$+".txt")
	For S.Sphere2D=Each Sphere2D
		WriteInt(File,S\x)
		WriteInt(File,S\y)
	Next
	CloseFile(File)
End Function

Function LoadLevel(name$)
	Delete Each Sphere2D
	file=ReadFile(name$+".txt")
	While Not Eof(File)
		a=ReadInt(file)
		b=ReadInt(file)
		CreateSphere2D(a,b)
	Wend
	CloseFile(file)
End Function

While Not KeyHit(1)
	If MouseHit(1)
		CreateSphere2D(MouseX(),MouseY())
	EndIf
	If KeyHit(64) Savelevel("1")
	If KeyHit(65) Loadlevel("1"):Cls
	DrawALLSphere2D()
	Flip
Wend
End
</code><noinclude>[[Категория:Код]]</noinclude>