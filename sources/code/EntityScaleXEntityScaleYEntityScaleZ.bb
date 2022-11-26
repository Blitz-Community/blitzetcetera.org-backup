; -------------------------------------------------------------------------------------------------------------------
; This function returns the X axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleX#(Entity)

	Vx# = GetMatElement(Entity, 0, 0)
	Vy# = GetMatElement(Entity, 0, 1)
	Vz# = GetMatElement(Entity, 0, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Y axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleY#(Entity)

	Vx# = GetMatElement(Entity, 1, 0)
	Vy# = GetMatElement(Entity, 1, 1)
	Vz# = GetMatElement(Entity, 1, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Z axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleZ#(Entity)

	Vx# = GetMatElement(Entity, 2, 0)
	Vy# = GetMatElement(Entity, 2, 1)
	Vz# = GetMatElement(Entity, 2, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function
