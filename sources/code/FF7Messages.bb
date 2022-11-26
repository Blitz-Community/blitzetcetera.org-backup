;x coordinates : y coordinates : mess$="Yuour mesg here!" : delay time ;between letters : red : green : blue : push(it can be true or a time ;in msec, it means the time that the message appear on the screen, or ;if "true" you have to push a button to close the message)
;leave the function draw_rect as it is!

Graphics 800,600
messaggio 50,50,"YEEEEEEEEEEHAAAAAAAAAA!",200,128,192,255,True

Function messaggio(x,y,mess$,time,r,g,b,push)
lenght=Len(mesS$)
pixel_width=StringWidth(mess$)
altezza=(lenght/30)+1
draw_rect(x,y,mess$)

Select altezza
     Case 1
     For z=1 To lenght+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_1$=row_1$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Flip
          Delay time
     Next
     
     Case 2
     For z=1 To (lenght-lenght/2)+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_1$=row_1$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Flip
          Delay time
     Next
     For z=(lenght-lenght/2)+2 To lenght+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_2$=row_2$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Text x+2,y+12,row_2$
          Flip
          Delay time
     Next
     
     Case 3
     For z=1 To (lenght-2*lenght/3)+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_1$=row_1$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Flip
          Delay time
     Next
     For z=(lenght-2*lenght/3)+2 To (lenght-lenght/3)+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_2$=row_2$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Text x+2,y+12,row_2$
          Flip
          Delay time
     Next
     For z=(lenght-lenght/3)+2 To lenght+1
          draw_rect(x,y,mess$)
          Color r,g,b
          row_3$=row_3$+Mid$(mess$,z,1)
          Text x+2,y+2,row_1$
          Text x+2,y+12,row_2$
          Text x+2,y+22,row_3$
          Flip
          Delay time
     Next



End Select
FlushKeys
If push=True 
     WaitKey
Else
     Delay push
EndIf

End Function

Function draw_rect(x,y,mess$)
lenght=Len(mesS$)
pixel_width=StringWidth(mess$)
altezza=(lenght/30)+1
     If lenght<30
          Color 200,200,200
          Rect x-1,y-1,(pixel_width+10)+2,(altezza*10)+8
          Color 38,3,187
          Rect x,y,pixel_width+10,(altezza*10)+6,True
     Else
          Color 200,200,200
          Rect x-1,y-1,((pixel_width+10)/altezza)+16,(altezza*10)+16
          Color 38,3,187
          Rect x,y,((pixel_width+10)/altezza)+14,(altezza*10)+14,True
     EndIf
end function