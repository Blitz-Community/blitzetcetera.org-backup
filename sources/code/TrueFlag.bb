Graphics 320,512,16,2


    ReturnedColours = 1 + 2 + 8 + 64

    Global k%
    Global I%
    
    Dim value(16)

    k = 1
    value(0) = ReturnedColours And 1              ; Get lowest 8 bits  - Red
	Print Str(I) + "  flag: " + Str(k) + " = " + Str(value(I))
    For I = 1 To 16

        k = k + k
        value(I) = Int(ReturnedColours / k) And 1
    	Print Str(I) + "  flag: " + Str(k) + " = " + Str(value(I))
		
    Next
