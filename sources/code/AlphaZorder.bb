; code by Beaker 2002 - please credit prominently (or arrange otherwise)
; beaks@playerfactory.co.uk

; Alpha Z-Ordering Mumbo Jumbo
;
; By Beaker and tweaked by Fredborg
;

;
; Zorder type
Type zorder
	Field mesh
	Field surf
	Field x,y,z			; Banks: size = tricnt * 6 bytes (3 shorts)
	Field n_tris		; Counttriangles(surf)-1
	Field include_y		; Include y axis sorting
	Field prev			; Last sorting axis
End Type

Function ZOrder_SortBank(bankA,bankB,l,r)
	Local p,q,h,x#
	p=l
	q=r
	x=PeekFloat(bankA,((l+r)/2)*4)
	Repeat
		While PeekFloat(bankA,p*4)>x
			p=p+1
		Wend
		While x>PeekFloat(bankA,q*4)
			q=q-1
		Wend
		If p>q Then Exit
		
		;SWAP------------------
		v0 = PeekShort(bankB,q*6 + 0)
		v1 = PeekShort(bankB,q*6 + 2)
		v2 = PeekShort(bankB,q*6 + 4)

		PokeShort bankB,q*6 + 0,PeekShort(bankB,p*6 + 0)
		PokeShort bankB,q*6 + 2,PeekShort(bankB,p*6 + 2)
		PokeShort bankB,q*6 + 4,PeekShort(bankB,p*6 + 4)
				
		PokeShort bankB,p*6 + 0,v0
		PokeShort bankB,p*6 + 2,v1
		PokeShort bankB,p*6 + 4,v2

		f# = PeekFloat(bankA,q*4)
		PokeFloat bankA,q*4,PeekFloat(bankA,p*4)
		PokeFloat bankA,p*4,f#	
		;----------------------
		
		p=p+1
		q=q-1
		If q<0 Then Exit
	Forever 
	If l<q Then ZOrder_SortBank(bankA,bankB,l,q)
	If p<r Then ZOrder_SortBank(bankA,bankB,p,r)

End Function

Function ZOrder_Init.zorder(mesh, include_y = False)

	zo.zorder = New zorder
	zo\mesh = mesh
	zo\surf = GetSurface(zo\mesh,1)

	If zo\surf = 0
		RuntimeError "Zorderize(): No surface found!"
	EndIf

	zo\include_y = include_y

	zo\n_tris = CountTriangles(zo\surf)-1

	tempbank = CreateBank((zo\n_tris+1)*4)

	; X axis sorting
	zo\x = CreateBank((zo\n_tris+1)*6)
	For t = 0 To zo\n_tris
		ft = t*6
		PokeShort zo\x,ft+0,TriangleVertex(zo\surf,t,0)
		PokeShort zo\x,ft+2,TriangleVertex(zo\surf,t,1)
		PokeShort zo\x,ft+4,TriangleVertex(zo\surf,t,2)
		PokeFloat tempbank,t*4,VertexX(zo\surf,TriangleVertex(zo\surf,t,0))+VertexX(zo\surf,TriangleVertex(zo\surf,t,1))+VertexX(zo\surf,TriangleVertex(zo\surf,t,2))
	Next
	ZOrder_SortBank(tempbank,zo\x,0,zo\n_tris)

	If include_y
		; Y axis sorting
		zo\y = CreateBank((zo\n_tris+1)*6)
		For t = 0 To zo\n_tris
			ft = t*6
			PokeShort zo\y,ft+0,TriangleVertex(zo\surf,t,0)
			PokeShort zo\y,ft+2,TriangleVertex(zo\surf,t,1)
			PokeShort zo\y,ft+4,TriangleVertex(zo\surf,t,2)
			PokeFloat tempbank,t*4,VertexY(zo\surf,TriangleVertex(zo\surf,t,0))+VertexY(zo\surf,TriangleVertex(zo\surf,t,1))+VertexY(zo\surf,TriangleVertex(zo\surf,t,2))
		Next
		ZOrder_SortBank(tempbank,zo\y,0,zo\n_tris)
	EndIf
	
	; Z axis sorting
	zo\z = CreateBank((zo\n_tris+1)*6)
	For t = 0 To zo\n_tris
		ft = t*6
		PokeShort zo\z,ft+0,TriangleVertex(zo\surf,t,0)
		PokeShort zo\z,ft+2,TriangleVertex(zo\surf,t,1)
		PokeShort zo\z,ft+4,TriangleVertex(zo\surf,t,2)
		PokeFloat tempbank,t*4,VertexZ(zo\surf,TriangleVertex(zo\surf,t,0))+VertexZ(zo\surf,TriangleVertex(zo\surf,t,1))+VertexZ(zo\surf,TriangleVertex(zo\surf,t,2))
	Next
	ZOrder_SortBank(tempbank,zo\z,0,zo\n_tris)
	
	FreeBank tempbank
	
	Return zo
	
End Function


Function ZOrder_Update(zo.zorder,cam)
	
	Local t,tf
	
	TFormVector 0,0,1, cam,0
	Local tx# = TFormedX()
	Local ty# = TFormedY()
	Local tz# = TFormedZ()
	
	Local temp = 0
	If zo\include_y = False Then ty = 0.0
	If (Abs(tx) > Abs(tz)) And (Abs(tx) > Abs(ty))
		temp = Sgn(tx)
		bank = zo\x
	ElseIf Abs(ty) > Abs(tz)
		temp = Sgn(ty)*2
		bank = zo\y
	Else
		temp = Sgn(tz)*3
		bank = zo\z
	EndIf
	
	If temp<>zo\prev
	
		zo\prev = temp
		ClearSurface zo\surf,0,1
					
		Select Sgn(zo\prev)
			Case 1:
				For t = 0 To zo\n_tris
					tf = t*6
					AddTriangle ( zo\surf,PeekShort(bank,tf),PeekShort(bank,tf+2),PeekShort(bank,tf+4) )
				Next
			Case -1:
				For t = zo\n_tris To 0 Step -1
					tf = t*6
					AddTriangle ( zo\surf,PeekShort(bank,tf),PeekShort(bank,tf+2),PeekShort(bank,tf+4) )
				Next
		End Select
		
	EndIf
	
End Function

Function ZOrder_Free(zo.zorder)

	If zo = Null
		For zo.zorder = Each zorder
			If zo\x Then FreeBank zo\x
			If zo\y Then FreeBank zo\y
			If zo\z Then FreeBank zo\z
			Delete zo
		Next
	Else
		If zo\x Then FreeBank zo\x
		If zo\y Then FreeBank zo\y
		If zo\z Then FreeBank zo\z
		Delete zo
	EndIf

End Function

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

;Include "zorder.bb"

b = CreateBrush()
BrushFX b,1+2+16+32

m = CreateMesh()
s = CreateSurface(m,b)

size# = 5.0
hsize# = size/2.0

For i = 0 To 1000
	x# = Rnd(-50,50)
	z# = Rnd(-50,50)
	
	a# = Rand(0,3)*90
	xx# = Sin(a)*hsize
	zz# = Cos(a)*hsize
	
	v0 = AddVertex(s,x-xx,0,z-zz)
		VertexColor s,v0,Rand(128,255),Rand(128,255),Rand(128,255),Rnd(0,1)
	v1 = AddVertex(s,x-xx,size,z-zz)
		VertexColor s,v1,Rand(128,255),Rand(128,255),Rand(128,255),Rnd(0,1)
	v2 = AddVertex(s,x+xx,size,z+zz)
		VertexColor s,v2,Rand(128,255),Rand(128,255),Rand(128,255),Rnd(0,1)
	v3 = AddVertex(s,x+xx,0,z+zz)
		VertexColor s,v3,Rand(128,255),Rand(128,255),Rand(128,255),Rnd(0,1)
		
	AddTriangle s,v0,v1,v2
	AddTriangle s,v2,v3,v0
Next

;
; Prepare zordering for mesh m
zorder.zorder = ZOrder_Init(m)

doz = False;True

c = CreateCamera()
p# = 0
y# = 0

MoveMouse 320,240
Repeat
	
	p = p+MouseYSpeed()
	y = y-MouseXSpeed()
	RotateEntity c,p,y,0
	MoveEntity c,KeyDown(205)-KeyDown(203),0,KeyDown(200)-KeyDown(208)
	
	MoveMouse 320,240

	;
	; Toggle zordering on/off
	If KeyHit(57)
		doz = Not doz
	EndIf

	;
	; Update zordering for mesh m with camera c
	If doz
		ZOrder_Update(zorder,c)
	EndIf

	RenderWorld

	If doz
		Text 0,0,"Space to disable ZOrdering"
	Else
		Text 0,0,"Space to enable ZOrdering"
	EndIf
	Flip
	
Until KeyHit(1)

End