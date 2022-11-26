;Creates a directory in curent dir with the name of curent date.
a$=CurrentDate$()
d1$=Mid$(a$,1,2)
d2$=Mid$(a$,4,3)
d3$=Mid$(a$,8,4)

dir$=d3$+"-"+d2$+"-"+d1$
ExecFile("cmd /C "+Chr$(32)+"MKDIR "+dir$+Chr$(32))
