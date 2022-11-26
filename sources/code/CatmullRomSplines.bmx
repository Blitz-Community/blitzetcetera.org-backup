'This is just a little type to keep track of individual control points
Type point
	Field x#,y#
	
	Function create:point(x#,y#)
		p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
End Type

'Spline type - the points list is a list of the control points to use in the spline
Type spline

	Field points:TList
	
	Method New() '(This is called when the spline object is created, it just initialises the points list)
		points=New TList
	End Method
	
	Method addpoint(p:point) 'Call this to add a point to the end of the list
		points.addlast p
	End Method
	
	Method draw() 'Do the actual drawing!
	
		'Just marking out the control points here
		For p:point=EachIn points
			DrawRect p.x-1,p.y-1,2,2
		Next
		
		num=points.count()
		If num<4 Then Return 'Check there are enough points to draw a spline

		'Get the first four TLinks in the list of points. This algorithm is going to work by crawling along the list, then getting the point objects from the TLinks. Yet more irrelevant stuff to the actual Catmull Rom.
		pl0:TLink=points.firstlink()
		pl1:TLink=pl0.nextlink()
		pl2:TLink=pl1.nextlink()
		pl3:TLink=pl2.nextlink()
		
		While pl3<>Null 'pl3 will be null when we've reached the end of the list
			'get the point objects from the TLinks
			p0:point=point(pl0.value())
			p1:point=point(pl1.value())
			p2:point=point(pl2.value())
			p3:point=point(pl3.value())
			For t#=0 To 1 Step .01 'THE MEAT AND BONES! Oddly, there isn't much to explain here, just copy the code.
				x#=.5*((2*p1.x)+(p2.x-p0.x)*t+(2*p0.x-5*p1.x+4*p2.x-p3.x)*t*t+(3*p1.x-p0.x-3*p2.x+p3.x)*t*t*t)
				y#=.5*((2*p1.y)+(p2.y-p0.y)*t+(2*p0.y-5*p1.y+4*p2.y-p3.y)*t*t+(3*p1.y-p0.y-3*p2.y+p3.y)*t*t*t)
				DrawRect x,y,1,1
			Next
			
			'Move one place along the list
			pl0=pl1
			pl1=pl2
			pl2=pl3
			pl3=pl3.nextlink()
		Wend
	End Method
End Type

'Demo - create a spline and add points when the mouse is clicked

Graphics 800,800,0

s:spline=New spline

While Not KeyHit(KEY_ESCAPE)

	If MouseHit(1)
		s.addpoint(point.create(MouseX(),MouseY()))
	EndIf
	
	s.draw()
	Flip
	Cls
Wend

Rem
THE ORIGINAL FUNCTION:

q(t) = 0.5 *(
   				(2 * P1) +
  				(-P0 + P2) * t +
				(2*P0 - 5*P1 + 4*P2 - P3) * t2 +
				(-P0 + 3*P1- 3*P2 + P3) * t3
			)
			
Where P0/1/2/3 are the 4 control points on the spline, and q(t) is the point on the curve at position t, where t=0 is the start of the curve, and t=1 is the end of the curve.
To actually use this, just split it into x and y parts, so:

qx(t) = 0.5 *(
   				(2 * P1x) +
  				(-P0x + P2x) * t +
				(2*P0x - 5*P1x + 4*P2x - P3x) * t2 +
				(-P0x + 3*P1x- 3*P2x + P3x) * t3
			)

qy(t) = 0.5 *(
   				(2 * P1y) +
  				(-P0y + P2y) * t +
				(2*P0y - 5*P1y + 4*P2y - P3y) * t2 +
				(-P0y + 3*P1y- 3*P2y + P3y) * t3
			)
			
Where each point PN has components PNx and PNy.

endrem
