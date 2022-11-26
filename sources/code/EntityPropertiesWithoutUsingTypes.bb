;================================================
;
; Demo Code for Entity Property Library
;
; (c)2003 Ken Lynch
;
;================================================

;Include "libEntityProperty.bb"  ;<-- you need to set this up to include the library code

Graphics3D 800, 600

cam = CreateCamera()
PositionEntity cam, 0, 0, -5

cube = CreateCube()

;
; Create 3000 properties for the cube
;
For n = 1 To 3000
	Text 0,0, "Creating property P"+Str(n)
	Flip: Cls
	SetProperty cube, "P"+Str(n), Str(n)
Next

Repeat

	;
	; Hold space to view a random 10 properties
	;
	If KeyHit(57) Then
		For n=1 To 10
			i = Rand(1, 3000)
			Text 0, (n+1)*16, "P"+Str(i)+" = "+GetProperty(cube, "P"+Str(i))
		Next
		Flip

		;
		; Wait while the space bar is still pressed
		;
		While KeyDown(57)
		Wend
	End If

	;
	; Spin cube to show 3000 pivots doesn't decrease performance
	;
	TurnEntity cube, 0, 2, 0
	
	RenderWorld
	Text 0, 0, "Press space to view a random 10 properties."
	Flip
Until KeyHit(1)
;================================================
;
; Entity Property Library
;
; (c)2003 Ken Lynch
;
;================================================

;
; SetProperty entity,property$,value$
;
Function SetProperty(entity, property$, value$)
	Local props, p, v

	props = FindChild(entity, "properties")
	If props = 0 Then
		props = CreatePivot(entity)
		NameEntity props, "properties"
	End If
	p = FindChild(props, property)
	If p = 0 Then
		p = CreatePivot(props)
		NameEntity p, property
	End If
	v = GetChild(p, 1)
	If v = 0 Then v = CreatePivot(p)
	NameEntity v, value
End Function

;
; value$ = GetProperty$(entity,property$)
;
Function GetProperty$(entity, property$)
	Local props,p, v, value$

	props = FindChild(entity, "properties")
	If props > 0 Then
		p = FindChild(entity, property)
		If p > 0 Then
			v = GetChild(p, 1)
			If v > 0 Then value$ = EntityName(v)
		End If
	End If
	Return value$
End Function

;
; DeleteProperty entity,property$
;
Function DeleteProperty(entity, property$)
	Local props,p

	props = FindChild(entity, "properties")
	If props > 0 Then
		p = FindChild(entity, property)
		If p > 0 Then FreeEntity p
	End If
End Function