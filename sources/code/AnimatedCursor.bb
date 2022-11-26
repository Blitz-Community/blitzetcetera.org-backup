Graphics 640, 480:HidePointer() 
;----Создание рисунка ------------------ 
img=CreateImage(192, 32):SetBuffer ImageBuffer(img) 
For k=0 To 5 
Rect 32*k, 0, 32*(k+1), k*5 
Next 
SaveImage(img, "buf.bmp"):FreeImage img 
;----Создание курсора ------------------ 
cur.TCursor=CreateCursor() 
LoadFrames(cur, "buf.bmp", 32, 32, 6) 
maskCursor(cur, 0, 0, 0) 
CursorDelay(cur, 100) 
 
f=LoadFont("system", 1, 0, 0, 0):SetFont(f) 
 
SetBuffer BackBuffer() 
 
While Not KeyHit(1) 
Cls 
 
Text 100, 100, "Нажми 1 для активизации режима 'PingPong'" 
Text 100, 150, "Нажми 2 для дезактивизации режима 'PingPong'" 
DrawCursor(cur) 
If KeyHit(2) CursorPingPong(cur, True) 
If KeyHit(3) CursorPingPong(cur, False) 
 
Flip 
Wend 
 
freeCursor(cur) 
;========================================== 
;================ ОСНОВА ================= 
;========================================== 
Type TCursor 
Field PingPong 
Field time, timer 
Field Img 
Field w, h 
Field curr, count, dd 
End Type 
 
Function CreateCursor.TCursor() 
a.TCursor = New TCursor 
a\PingPong=0:a\time=10:a\curr=0:a\count=0:a\dd=0 
Return a 
End Function 
 
Function CursorPingPong(cur.TCursor, PingPong) 
cur\PingPong=pingpong 
cur\dd=1:cur\curr=0 
End Function 
 
Function LoadFrames(cur.TCursor, FileName$, width, height, count) 
cur\img=LoadAnimImage(FileName$, width, height, 0, count) 
cur\curr=0:cur\count=count 
cur\w=width Shr 1:cur\h=height Shr 1 
End Function 
 
Function MaskCursor(cur.TCursor, red, green, blue) 
MaskImage cur\img, red, green, blue 
End Function 
 
Function CursorDelay(cur.TCursor, time) 
If time<10 time=10 
cur\time=time 
End Function 
 
Function DrawCursor(cur.TCursor) 
DrawImage cur\img, MouseX()-cur\w, MouseY()-cur\h, cur\curr 
If MilliSecs()  >= cur\timer 
cur\timer=MilliSecs()+cur\time 
If Not cur\PingPong   
 cur\curr=(cur\curr+1) Mod cur\count   
Else  
 cur\curr=cur\curr+cur\dd 
 If cur\curr=cur\count-1 cur\dd=-1 
 If cur\curr=0 cur\dd=1 
End If 
End If 
End Function 
 
Function FreeCursor(cur.TCursor) 
FreeImage cur\img 
Delete cur 
End Function