Function DLLSystemTime$(ret$)
ret$ = Replace$(ret$,"d",Replace(LSet(MilliSecs()/1000/60/60/24,2)," ","0"))
ret$ = Replace$(ret$,"h",Replace(RSet(MilliSecs()/1000/60/60-(MilliSecs()/1000/60/60/24*24),2)," ","0"))
ret$ = Replace$(ret$,"m",Replace(RSet(MilliSecs()/1000/60-(MilliSecs()/1000/60/60*60),2)," ","0"))
ret$ = Replace$(ret$,"s",Replace(RSet(MilliSecs()/1000-(MilliSecs()/1000/60*60),2)," ","0"))
ret$ = Replace$(ret$,"m",Replace(RSet(MilliSecs()-(MilliSecs()/1000*1000),4)," ","0"))
Return ret$
End Function
