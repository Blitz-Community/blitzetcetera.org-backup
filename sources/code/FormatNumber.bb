SeedRnd MilliSecs()
For c=1 To 20
x#=Rnd(-100000000,100000000)/Rnd(1000)
Print "Formatted: "+format$(x,6,4)+ " Unformatted: "+x#
Next
waitkey()


Function format$(v#,d,p=0) ; format a number
Local m$, e$, r$,  n,  x#=Abs(v)
If p>0 Then m$=Int((x-Floor(x))*10^p) : e$="."+Right$("000000"+m$,p)
If p=0 Or Len(m$)>p Then n=Int(x) Else n=Floor(x)
If v<0 Then r$="-"+n Else r$=n 
If Len(r$)>d Then r$=String$("*",d): If p>0 Then e$="."+String$("*",p)
Return RSet$(r$,d)+e$
End Function
