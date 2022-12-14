Graphics 640,480,0,2
SeedRnd MilliSecs()

;you only need this line and the funtion
Dim maze(0,0);dummy


;examples:

sx=55
sy=35
makemaze(sx,sy,0,2)

;draw the maze
For fy=1 To sy
	For fx=1 To sx
		If maze(fx,fy)>=1 Then Rect fx*10,fy*10,10,10
	Next
Next
WaitKey

sx=15
sy=45
makemaze(sx,sy,1,1)

Cls
;draw the maze
For fy=1 To sy
	For fx=1 To sx
		If maze(fx,fy)>=1 Then Rect fx*10,fy*10,10,10
	Next
Next

WaitKey


sx=301
sy=201
makemaze(sx,sy,4,9)

Cls
;draw the maze
For fy=1 To sy
	For fx=1 To sx
		If maze(fx,fy)>=1 Then Rect fx*2,fy*2,2,2
	Next
Next

WaitKey

sx=301
sy=201
makemaze(sx,sy,0,10)

Cls
;draw the maze
For fy=1 To sy
	For fx=1 To sx
		If maze(fx,fy)>=1 Then Rect fx*2,fy*2,2,2
	Next
Next

WaitKey

sx=301
sy=201
makemaze(sx,sy,1,1)

Cls
;draw the maze
For fy=1 To sy
	For fx=1 To sx
		If maze(fx,fy)>=1 Then Rect fx*2,fy*2,2,2
	Next
Next

WaitKey
End









;MAKEMAZE()
;open tells how wide the space between walls are
;if open is large the mazes tend to look the same
;when exact is 1 a maze wall can only make a turn on an odd number cell
;exact=1
;#
;###
;  #
;  #
;exact=0
;# 
;## 
; ##
;  #
;ex. when exact is 5, a wall can only be put on every fifth cell
Function MakeMaze(mx,my,exact=0,open=1)
	Local fx,fy,allx,ally
	Local go,farx,fary,alldir
	Local dir,ok,f,f2
	Local x,y,x2,y2
	Local movex[3],movey[3]
	Local cango[4]
	Local did,didmax
	Local mx2,my2

	If exact=1 Then If (my And 1)=0 Or (mx And 1)=0 Then RuntimeError "maze size must be odd for exact mazes"
	If open<1 Then RuntimeError "open must be greater than 0"
	
	movex[0]=+1
	movey[1]=+1
	movex[2]=-1
	movey[3]=-1

	Dim maze(mx+1,my+1)
	For fx=1 To mx
		maze(fx,1)=1
		maze(fx,my)=1
	Next
	For fy=1 To my
		maze(1,fy)=1
		maze(mx,fy)=1
	Next
	farx=mx
	fary=my
	ally=1
	allx=1
	exact=exact+1
	didmax=mx*my
	mx2=mx+exact+1
	my2=my+exact+1
	Repeat
		Repeat
			allx=Rand(1,mx2)
			ally=Rand(1,my2)
			allx=allx/exact*exact-1
			ally=ally/exact*exact-1
			If allx<1 Then allx=1
			If ally<1 Then ally=1
			If allx>mx Then allx=mx
			If ally>my Then ally=my
			If maze(allx,ally)=1 Then Exit
		Forever
		did=did+1
		If did>didmax Then Exit
		;If allx>=mx And ally>=my Then Exit
		x=allx
		y=ally
		dir=Rand(0,3)
		Repeat
			ok=0
			For candir=0 To 3
				For f=1 To exact+open
					x2=x+movex[candir]*f
					y2=y+movey[candir]*f
					If x2<=0 Or y2<=0 Or x2>mx Or y2>my Then f=9999:Exit
					If maze(x2,y2)=1 Then f=9999:Exit
					For f2=1 To open
						If maze(x2+movex[(candir+1) And 3]*f2,y2+movey[(candir+1) And 3]*f2)=1 Then f=9999:Exit
						If maze(x2+movex[(candir-1) And 3]*f2,y2+movey[(candir-1) And 3]*f2)=1 Then f=9999:Exit
					Next
				Next
				cango[candir]=(f<9999)
				If cango[candir] Then ok=1
			Next
			If Rand(didmax)=1 Then Exit
			If ok=0 Then Exit
			dir=(dir+Rand(-1,+1)) And 3
			If cango[dir]=1 Then 
				For f=1 To exact
					x=x+movex[dir]
					y=y+movey[dir]
					maze(x,y)=1
				Next
			EndIf
		Forever
	Forever
End Function
