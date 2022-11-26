Type mytype
  Field xfactor
End Type

somany=1000

For some=1 To somany
  m.mytype=New mytype
  m\xfactor=Rnd(100)
Next

For m.mytype=Each mytype
  Print m\xfactor
Next

start=MilliSecs()

For a.mytype=Each mytype

  For b.mytype=Each mytype

  If Handle(a)<>Handle(b)

    If a\xfactor=b\xfactor

    Delete b

    EndIf

  EndIf


  Next

Next

time=MilliSecs()-start

For m.mytype=Each mytype
  Print m\xfactor
Next

Print "Search took "+time+" millisecs for "+somany+" types"


While Not KeyHit(1)
Wend
