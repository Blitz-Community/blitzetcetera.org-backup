ProcedureDLL FunkciaSlojeniya(a,b) 
c=a+b
ProcedureReturn c
EndProcedure

ProcedureDLL.f FunkciaDlyaFloatov(a.f, b.f, c.f) 
d.f=(a*b)/c
ProcedureReturn d
EndProcedure

ProcedureDLL.s FunkciaStokaPlusChislo(stroka.s, chislo)
stroka.s=stroka.s+Str(Random(chislo))
ProcedureReturn stroka
EndProcedure

ProcedureDLL.f SamplePeekF(*bank,offset)
ProcedureReturn PeekF(*bank+offset)
EndProcedure

ProcedureDLL.f FunkciyaEntityX(*entity)
ProcedureReturn PeekF(*entity+64)
EndProcedure
; IDE Options = PureBasic v4.01 (Windows - x86)
; ExecutableFormat = Shared Dll
; CursorPosition = 20
; Folding = -
; Executable = ..\..\Blitz3D\userlibs\mylib.dll