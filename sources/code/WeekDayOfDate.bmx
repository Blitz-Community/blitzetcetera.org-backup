'------------------------------- DOD (Day of date)
Function dod$(day,month,year) ' (DD,MM,YYYY)
Local d,a,m,y,tp$

a=14-month
a=a/12
y=year-a
m=month+(12*a)-2

d=(day+y+(y/4)-(y/100)+(y/400)+((31*m)/12)) Mod 7

Select d
 Case 0 tp$="Sunday "
 Case 1 tp$="Monday "
 Case 2 tp$="Tuesday"
 Case 3 tp$="Wednesday "
 Case 4 tp$="Thursday"
 Case 5 tp$="Friday "
 Case 6 tp$="Saturday " 
End Select

Return tp$			' Return day
End Function
