Graphics3D 800,600,0,2

SetBuffer BackBuffer()

font=LoadFont("Times New Roman",20,0,1)
SetFont font

Label$="Hello World"

While Not KeyHit(1)
Cls
Color 255,255,255
Text 20,20,"Click The Mouse Button On A Screen..."
Text 20,60,"Label = "+Label
If MouseHit(1) Then Label=my_input(Label$,MouseX(),MouseY(),150,40)
Flip
Wend
End

Function my_input$(s$,x#,y#,dx#,dy#)
cur_pos%=Len(s)
view_text$=""
While Not KeyHit(28)
Cls
Color 200,200,200
Rect x,y,dx,dy,1
Color 0,0,0
Rect x+2,y+2,dx-4,dy-4,0
Viewport x+2,y+2,dx-4,dy-4
view_text=Left(s,cur_pos)
view_text=view_text+"|"
view_text=view_text+Right(s,Len(s)-cur_pos)
Text x+dx/2,y+dy/2,view_text,1,1
Flip
k=WaitKey()
Select True
Case (k>31)And(k<127)
temp$=s
s$=Left(temp$,cur_pos)+Chr(k)+Right(temp$,Len(temp$)-cur_pos)
cur_pos=cur_pos+1

Case k=8
If cur_pos<>0 Then
temp$=s$
s= Left(temp$,cur_pos-1)+Right(temp$,Len(temp$)-cur_pos)
cur_pos=cur_pos-1
EndIf

Case k=4
If cur_pos<>Len(s) Then
temp$=s
s= Left(temp$,cur_pos)+Right(temp$,Len(temp$)-cur_pos-1)
EndIf

Case k=31
cur_pos=cur_pos-1
If cur_pos<0 Then cur_pos=0

Case k=30
cur_pos=cur_pos+1
If cur_pos>Len(s) Then cur_pos=Len(s)

Case k=1
cur_pos=0

Case k=2
cur_pos=Len(s)

End Select
Wend
Viewport 0,0,GraphicsWidth(),GraphicsHeight()
Return s$
End Function