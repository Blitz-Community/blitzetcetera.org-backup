;
; Sim City type roads
;
Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim map(20,15)

While KeyDown(1) = False
     Cls
     drawtile 0,0          +5     ,480-32
     drawtile 1,32          +10     ,480-32
     drawtile 2,64          +15     ,480-32
     drawtile 3,96          +20     ,480-32
     drawtile 4,128          +25     ,480-32
     drawtile 5,156          +30     ,480-32
     drawtile 6,188          +35     ,480-32
     drawtile 7,188+32     +40     ,480-32
     drawtile 8,188+64     +45     ,480-32
     drawtile 9,188+96     +50     ,480-32
     drawtile 10,188+128     +50     ,480-32
     ;
     ;drawtile 10,50,50
     ;
     If MouseHit(1) = True Then
          map(MouseX()/32,MouseY()/32) = 1
          makestructure(MouseX()/32,MouseY()/32)
     End If
     ;
     drawmap
     Text 0,0,"Press the mouse to draw the roads"
     Flip
Wend
End

Function makestructure(x,y)
     ; Make the road connect to the sides.
     ; we need to fix the connecting roads to so we check each side
     For y1=y-1 To y+1
     For x1=x-1 To x+1
          If RectsOverlap(x1,y1,1,1,0,0,20,15) = True Then
          setroad(x1,y1)
          End If
     Next:Next
End Function

Function setroad(x,y)
     ; Here we check if the sides have roads and place a flag for us to later assign
     ; a tile to.
     If map(x,y) = 0 Then Return
     If map(x-1,y) > 0 Then r$ = r$ + "1" Else r$ = r$ + "0"
     If map(x+1,y) > 0 Then r$ = r$ + "1" Else r$ = r$ + "0"
     If map(x,y-1) > 0 Then r$ = r$ + "1" Else r$ = r$ + "0"
     If map(x,y+1) > 0 Then r$ = r$ + "1" Else r$ = r$ + "0"
     ;
     Select r$
     Case "0000" : t = 1
     Case "1000" : t = 3
     Case "0100" : t = 3
     Case "0010" : t = 2
     Case "0001" : t = 2
     Case "1100" : t = 3
     Case "0011" : t = 2
     Case "1010" : t = 8
     Case "1001" : t = 10
     Case "0110" : t = 9
     Case "0101" : t = 11
     Case "1110" : t = 6
     Case "1101" : t = 7
     Case "1011" : t = 4
     Case "0111" : t = 5
     Case "1111" : t = 1
     End Select
     map(x,y) = t
End Function

Function drawmap()
     For y=0 To 15
          For x=0 To 20
               If map(x,y) > 0 Then drawtile(map(x,y)-1,x*32,y*32)
          Next
     Next
End Function

Function drawtile(num,x,y)
     ;
     ; This function draws the road segments. You can replace
     ; these with your custom graphics .
     ;
     Select num
          Case 0 : corner 0,x,y : corner 1,x,y : corner 2,x,y : corner 3,x,y
          Case 1
               Line 8+x,0+y,8+x,32+y
               Line 32-8+x,0+y,32-8+x,32+y
          Case 2
               Line 0+x,8+y,32+x,8+y
               Line 0+x,32-8+y,32+x,32-8+y
          Case 3
               corner 0,x,y : corner 2,x,y
               Line 32-8+x,0+y,32-8+x,32+y
          Case 4
               corner 1,x,y : corner 3,x,y
               Line 8+x,0+y,8+x,32+y
          Case 5
               corner 0,x,y : corner 1,x,y
               Line 0+x,32-8+y,32+x,32-8+y
          Case 6
               corner 2,x,y : corner 3,x,y
               Line 0+x,8+y,32+x,8+y
          Case 7
               corner 0,x,y
               Line 0+x,32-8+y,32-8+x,32-8+y
               Line 32-8+x,y,32-8+x,32-8+y               
          Case 8
               corner 1,x,y
               Line 8+x,y,8+x,32-8+y
               Line 8+x,32-8+y,32+x,32-8+y
          Case 9
               corner 2,x,y
               Line 32-8+x,8+y,32-8+x,32+y
               Line 0+x,8+y,32-8+x,8+y
          Case 10
               corner 3,x,y
               Line 8+x,8+y,32+x,8+y
               Line 8+x,8+y,8+x,32+y
     End Select
End Function

Function corner(num,x,y)
     ;
     ; This function draws the corners of the roads
     ;
     Select num
          Case 0
          Line 0+x,8+y,8+x,8+y
          Line 8+x,0+y,8+x,8+y
          Case 1
          Line 32-8+x,8+y,32+x,8+y
          Line 32-8+x,0+y,32-8+x,8+y
          Case 2
          Line 0+x,32-8+y,8+x,32-8+y
          Line 8+x,32+y,8+x,32-8+y
          Case 3
          Line 32+x,32-8+y,32-8+x,32-8+y
          Line 32+x-8,32+y,32-8+x,32-8+y
     End Select
End Function