;directory view of hierarchy
;check your DebugLog After calling - by Rob Cummings
Global tab ; makes debuglog output more readible (tabbing).

Graphics3D 640,480,16,2
mesh=LoadAnimMesh("mak_running.3ds")

xtree(mesh)

WaitKey:End
Function xtree(ent)
	tab=tab+4
	For i=1 To CountChildren(ent)	
		child=GetChild(ent,i)
		name$=EntityName(child)
		DebugLog String(" ",tab)+child+" "+name
		xtree(child)
	Next
	tab=tab-4
End Function