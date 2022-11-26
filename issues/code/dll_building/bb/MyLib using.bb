Graphics3D 400,300,0,2 
AppTitle "Dll functions sample by MadMedic"
Print "5+10"
Print FunkciaSlojeniya(5,10)
Print ""
Print "(2+5)/4"
Print FunkciaDlyaFloatov(2,5,4)
Print ""
Print FunkciaStokaPlusChislo("Random number from 0 to 16 is ",16)
Print FunkciaStokaPlusChislo("Random number from 0 to 16 is ",16)
Print FunkciaStokaPlusChislo("Random number from 0 to 16 is ",16)
Print FunkciaStokaPlusChislo("Random number from 0 to 16 is ",16)
Print FunkciaStokaPlusChislo("Random number from 0 to 16 is ",16)
Print ""

bank=CreateBank(12)
PokeFloat(bank,0,0.128)
PokeFloat(bank,4,32.9)
PokeFloat(bank,8,213.75)

Print "Peek floats from bank "
Print SamplePeekF(bank,0)
Print SamplePeekF(bank,4)
Print SamplePeekF(bank,8)
Print ""

pivot=CreatePivot()
PositionEntity pivot,20.125,0,0
Print "FunkciyaEntityX "
Print FunkciyaEntityX(pivot)
Print ""


Print "Press any key to exit"
WaitKey()
FreeBank bank
End
