;Digital Watch Code
;Chompster Productions 2002 http://chompster0.tripod.com
;Use it to convert timer measured into digital time for use in games
;Requires a global called digitimer$
Global digitimer$,time,extime#
Graphics 640,480,16
SetBuffer BackBuffer()

;Demo start

Repeat
Delay 500
extime = extime + .5
time = Int(extime)
digitimer extime
Text 100, 100, digitimer$
Flip
Cls
Until KeyHit(1)

;Demo End

;Actual function START (Copy from here down in your program)

Function digitimer(blocks)
For x = 1 To blocks
secs = secs + 1
If secs = 60 Then
secs = 0
mins = mins + 1
End If
If mins = 60 Then
hours = hours + 1
mins = 0
End If
If hours = 24 Then
secs = 0
mins = 0
hours = 0
End If
Next
If secs < 10 Then sec$ = "0"+secs Else sec$ = secs
If mins < 10 Then mi$ = "0"+mins Else mi$ = mins
If hours < 10 Then hou$ = "0"+hours Else hou$ = hours
digitimer$ = hou$+":"+mi$+":"+sec$
End Function

;Function End