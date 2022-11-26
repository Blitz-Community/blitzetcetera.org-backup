Function ReverseBytes( p@ Ptr, sz%, rbits%=0 )
    Local szh% = Int(Floor(sz*.5))
    sz :- 1
    If rbits>0 Then
        For Local i:Int = 0 To szh-1
            szh = p[i] ' Because szh is only used once, we can reuse it here
            p[i] = ReverseBits(p[sz-i])
            p[sz-i] = ReverseBits(szh)
        Next
        sz :+ 1
        If Int(sz*.5) <> Ceil((sz*.5)-.1) Then
            sz = Int(Ceil(sz*.5))
            p[sz] = ReverseBits(p[sz])
        EndIf
    Else
        For i:Int = 0 To szh-1
            szh = p[i]
            p[i] = p[sz-i]
            p[sz-i] = szh
        Next
    EndIf
End Function

Function ReverseBits@( p@ )
    ?Debug
    Return (p & %1) Shl 7..
    | ((p&%10000000) Shr 7)..
    | (p & %10) Shl 5..
    | ((p&%1000000) Shr 5)..
    | (p & %100) Shl 3..
    | ((p&%100000) Shr 3)..
    | (p & %1000) Shl 1..
    | ((p&%10000) Shr 1)
    ?
    Local p2:Int = ((p & %11110000) Shr 4) | ((p & %1111) Shl 4)
    p =((p & %11001100) Shr 2) | ((p & %110011) Shl 2)
    Return ((p & %10101010) Shr 1) | ((p & %1010101) Shl 1)
End Function

Local p@ Ptr = "blah".ToWString( )

ReverseBytes( p, 8, False )
Print Bin( Int Ptr(p)[0] )

Print String.FromShorts( Short Ptr(p), 8 )

ReverseBytes( p, 8, False )
Print Bin( Int Ptr(p)[0] )

ReverseBytes( p, 8, True )
Print Bin( Int Ptr(p)[0] )

Print String.FromShorts( Short Ptr(p), 8 )

ReverseBytes( p, 8, True )
Print Bin( Int Ptr(p)[0] )

Print String.FromShorts( Short Ptr(p), 8 )

ReverseBytes( p, 8, True )
Print Bin( Int Ptr(p)[0] )

ReverseBytes( p, 8, False )
Print Bin( Int Ptr(p)[0] )

Print String.FromShorts( Short Ptr(p), 8 )

ReverseBytes( p, 8, False )
Print Bin( Int Ptr(p)[0] )

ReverseBytes( p, 8, True )
Print Bin( Int Ptr(p)[0] )

Print String.FromShorts( Short Ptr(p), 8 )

Local t% = MilliSecs( )
For Local i% = 0 To 99999
    ReverseBytes(p,8,True)
Next
t = MilliSecs( ) - t
Print "Time taken with rbits as True: "+t+"ms"
Print "Time taken per call with rbits as True average: "+(t*0.00001)+"ms"

t = MilliSecs( )
For i% = 0 To 199999
    ReverseBytes(p,8,False)
Next
t = MilliSecs( ) - t
Print "Time taken with rbits as False: "+t+"ms"
Print "Time taken per call with rbits as False average: "+(t*0.00001)+"ms"

'MemFree( p ) ' Ok, we're done with the memory

Print Bin(%10000000)[24..]
Print Bin(ReverseBits( %10000000 ))[24..]

Print Bin(%11000000)[24..]
Print Bin(ReverseBits( %11000000 ))[24..]

Print Bin(%10100000)[24..]
Print Bin(ReverseBits( %10100000 ))[24..]

Print Bin(%00000011)[24..]
Print Bin(ReverseBits( %00000011 ))[24..]

Input( )
