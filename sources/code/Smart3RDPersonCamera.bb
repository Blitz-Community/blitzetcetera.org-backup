; ID: 1083
; Author: Otacon
; Date: 2004-06-13 04:37:46
; Title: Smart 3rd person camera
; Description: Follows a entity and avoids geo.

Type CSys

   Field cx#, cy#, cz#
   Field mx#, my#, sps%
   
End Type

Function ChaseCam(cam, Entity, XOff#, YOff# = 2, ZOff# = -5, Spd# = 0.8)

	Local sys.csys = First csys
	Local nx#, ny#, nz#
	Local dx#, dy#, dz#
	Local ex#, ey#, ez#
	Local hit%
	
	If sys = Null 
		sys = New cSys
		sys\sps = CreatePivot()
	EndIf
	
	sys\mx = MouseX()
	sys\my = MouseY()
	sps = sys\sps
	PositionEntity sps, EntityX(entity), EntityY(entity), EntityZ(entity)
	TFormVector xOff, yOff, zOff, entity, 0
	ex# = EntityX(entity)
	ey# = EntityY(entity)
	ez# = EntityZ(entity)
	nx# = ex+TFormedX()
	ny# = ey+TFormedY()
	nz# = ez+TFormedZ()
	dx# = nx-ex
	dy# = ny-ey
	dz# = nz-ez
	hit = LinePick(ex, ey, ez, dx, dy, dz, 0.2)
	
	If hit
		nx = PickedX()
		ny = PickedY()
		nz = PickedZ()
	EndIf
	
	sys\cx = sys\cx+(nx-sys\cx)*spd
	sys\cy = sys\cy+(ny-sys\cy)*spd
	sys\cz = sys\cz+(nz-sys\cz)*Spd
	
	PositionEntity cam, sys\cx, sys\cy, sys\cz
	PointEntity Cam, Entity, 0

End Function


Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()

Global cube = CreateCube()
Global cam = CreateCamera()
Global ops = CreateCube()
ScaleEntity ops, 10, 10, 1
	MoveEntity ops, 0, 0, -5
	EntityPickMode ops, 2

While Not KeyHit(1)
	
	Cls
	
	ChaseCam(cam, cube, 0, 2, -20, .1)
	
	MoveEntity ops, KeyDown(205) - KeyDown(203), 0, 0
	
	UpdateWorld
	RenderWorld
	
	Flip
	
Wend
