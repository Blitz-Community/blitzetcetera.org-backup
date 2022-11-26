;================================================================== 
;Project Title:     Menu Libray 
;Author:       Frank (�������� ���������) 
;Email:   Aculinin@mail.ru   
;Version:      1.0 
;Date:    02.09.2005  
;Notes:   ��������� ��� ���������� � ������ ������� 
;    �� ����� ������� ��������� ��������� ���� 
;================================================================== 
 
 
Type typMenu     ; ��� ���� 
 Field X,Y     ; ���������� ���������� ���� 
 Field Font[3]    ; �����:��������, ����������, �������, ����������� 
 Field R[3],G[3],B[3]  ; ����: ��������, ����������, �������, ����������� 
 Field Show     ; ���������� �� ��� ���� �� ������ 
 Field Enable     ; �������� �� ��� ���� 
End Type 
 
Type typMenuItem    ; ��� ��� ��������� ���� 
 Field Menu.typMenu   ; � ������ ���� ����������� 
 Field X#,Y#     ; ���������� �� ������ (��������� �� ��������� � ����) 
 Field txt$     ; ����� 
 Field State     ; ��������� �������� =0 - �������� ���������, =1 - �� ���� ������� ���������, =2 - ����� 
 Field Show     ; ���������� �� ���� ������� �� ������ 
 Field Enable     ; �������� �� ���� ������� 
End Type 

; ---- ������

Graphics 800,600,32,2 
SetBuffer BackBuffer() 
 
font=LoadFont("Arial Cyr",40,1) 
ff=LoadFont("Times New Roman",80,1) 
 
img=CreateImage(255,255) 
SetBuffer ImageBuffer(img) 
For n=1 To 100 
  Plot Rnd(255),Rnd(255) 
Next 
SetBuffer BackBuffer() 
 
; ������� ���� 
mnuMain.typMenu=CreateMenu(400,200,font,255,255,255,0,0,0,0,100,255,255,255,100) 
mniNewGame.typMenuItem=CreateMenuItem(mnuMain,0,0,"����� ����") 
mniOptions.typMenuItem=CreateMenuItem(mnuMain,0,0,"�����") 
mniExit.typMenuItem=CreateMenuItem(mnuMain,0,0,"�����") 
SetMenuAlign(mnuMain,2) 
SetMenuInterval(mnuMain) 
 
; ����� ���� 
mnuNewGame.typMenu=CreateMenu(400,200,font,255,255,255,0,0,0,0,100,255,255,255,100) 
mniEasy.typMenuItem=CreateMenuItem(mnuNewGame,0,0,"�����") 
mniNormal.typMenuItem=CreateMenuItem(mnuNewGame,0,0,"������") 
mniHard.typMenuItem=CreateMenuItem(mnuNewGame,0,0,"������") 
mniBack1.typMenuItem=CreateMenuItem(mnuNewGame,0,0,"�����") 
SetMenuAlign(mnuNewGame,2) 
SetMenuInterval(mnuNewGame) 
SetMenuShow(mnuNewGame,False) 
 
; ����� 
mnuOptions.typMenu=CreateMenu(400,200,font,255,255,255,0,0,0,0,100,255,255,255,100) 
mniVideo.typMenuItem=CreateMenuItem(mnuOptions,0,0,"�����") 
mniAudio.typMenuItem=CreateMenuItem(mnuOptions,0,0,"�����") 
mniControl.typMenuItem=CreateMenuItem(mnuOptions,0,0,"����������") 
mniBack2.typMenuItem=CreateMenuItem(mnuOptions,0,0,"�����") 
SetMenuAlign(mnuOptions,2) 
SetMenuInterval(mnuOptions) 
SetMenuShow(mnuOptions,False) 
 
; ����� 
mnuVideo.typMenu=CreateMenu(400,200,font,255,255,255,0,0,0,0,100,255,255,255,100) 
mniRes1.typMenuItem=CreateMenuItem(mnuVideo,0,0,"320�200") 
mniRes2.typMenuItem=CreateMenuItem(mnuVideo,0,0,"640�480") 
mniRes3.typMenuItem=CreateMenuItem(mnuVideo,0,0,"800�600") 
mniRes4.typMenuItem=CreateMenuItem(mnuVideo,0,0,"1024x768") 
mniBack3.typMenuItem=CreateMenuItem(mnuVideo,0,0,"�����") 
SetMenuAlign(mnuVideo,2) 
SetMenuInterval(mnuVideo) 
SetMenuShow(mnuVideo,False) 
 
; ����� 
mnuAudio.typMenu=CreateMenu(400,200,font,255,255,255,0,0,0,0,100,255,255,255,100) 
mniOnSound.typMenuItem=CreateMenuItem(mnuAudio,0,0,"�������� ����") 
mniOffSound.typMenuItem=CreateMenuItem(mnuAudio,0,0,"��������� ����") 
mniBack4.typMenuItem=CreateMenuItem(mnuAudio,0,0,"�����") 
SetMenuAlign(mnuAudio,2) 
SetMenuInterval(mnuAudio) 
SetMenuShow(mnuAudio,False) 
 
 
Repeat 
 Cls 
 If MenuClick(mniNewGame) Then SetMenuShow(mnuMain,0):SetMenuShow(mnuNewGame,1) 
 If MenuClick(mniBack1)   Then SetMenuShow(mnuMain,1):SetMenuShow(mnuNewGame,0) 
 If MenuClick(mniOptions) Then SetMenuShow(mnuOptions,1):SetMenuShow(mnuMain,0) 
 If MenuClick(mniBack2)   Then SetMenuShow(mnuMain,1):SetMenuShow(mnuOptions,0) 
 If MenuClick(mniVideo)   Then SetMenuShow(mnuOptions,0):SetMenuShow(mnuVideo,1) 
 If MenuClick(mniBack3)   Then SetMenuShow(mnuOptions,1):SetMenuShow(mnuVideo,0) 
 If MenuClick(mniAudio)   Then SetMenuShow(mnuOptions,0):SetMenuShow(mnuAudio,1) 
 If MenuClick(mniBack4)   Then SetMenuShow(mnuOptions,1):SetMenuShow(mnuAudio,0) 
 If MenuClick(mniExit)    Then End 
 
 TileBlock img,0,qq:qq=qq-1 
 SetFont ff:Color 0,255,0:Text 400,100,"Super Game",1,1 
 
 RunMenu() 
 Flip  
Until KeyDown(1)
 
; ---- �������� ������� ���������� ���� ---- 
Function CreateMenu.typMenu(X,Y,font0,R0,G0,B0,font1=0,font2=0,font3=0,R1=-1,G1=- 1,B1=-1,R2=-1,G2=-1,B2=-1,R3=-1,G3=-1,B3=-1,Enable=True,show=True) 
 M.typMenu=New typMenu 
 m\x=x 
 m\y=y 
 M\font[0]=Font0   ; ������ 
 M\font[1]=Font1 
 M\font[2]=Font2 
 M\font[3]=Font3 
 
 If font1=0 Then m\font[1]=m\font[0]  ; ���� �������������� ������ �� �������, �� 
 If font2=0 Then m\font[2]=m\font[0]  ; ��� ����� �� ��� � �������� 
 If font3=0 Then m\font[3]=m\font[0] 
  
 m\r[0]=r0:m\g[0]=g0:m\b[0]=b0  ; �������� ���� 
 m\r[1]=r1:m\g[1]=g1:m\b[1]=b1  ; ���� ��� ��������� 
 m\r[2]=r2:m\g[2]=g2:m\b[2]=b2  ; ���� ��� ������� 
 m\r[3]=r3:m\g[3]=g3:m\b[3]=b3  ; ���� ��� ����������� �������� 
 
 If r1=-1 Then m\r[1]=r0:m\g[1]=g0:m\b[1]=b0  ; ���� ����� �� �������, �� 
 If r2=-1 Then m\r[2]=r0:m\g[2]=g0:m\b[2]=b0  ; ��� ����� �� ��� � �������� 
 If r3=-1 Then m\r[3]=128:m\g[3]=128:m\b[3]=128 ; � ����������� �������� ����� ���� 
 
 m\enable=enable   ; �������� �� ���� 
 m\show=show    ; ������ �� ���� 
Return M  
End Function 
 
Function UpdateMenu(e.typMenu)    ; ��������� ���� 
 If e\enable Then 
  For i.typMenuItem=Each typMenuItem 
   If i\menu=e Then UpdateMenuItem(i)  ; ���� ������� ���� ����������� ����� ���� 
  Next 
 End If 
End Function 
 
Function RenderMenu(e.typMenu)    ; ������ ���� 
 If e\show Then  
  For i.typMenuItem=Each typMenuItem 
   If i\menu=e Then RenderMenuItem(i) 
  Next 
 End If  
End Function  
 
Function UpdateEachMenu()     ; ��������� ��� ���� 
 For m.typMenu=Each typMenu 
  UpdateMenu(m) 
 Next  
End Function 
 
Function RenderEachMenu()     ; ������ ��� ���� 
 For m.typMenu=Each typMenu 
  RenderMenu(m) 
 Next  
End Function 
 
Function RunMenu()       ; ��������� � ������ �� ���� 
 UpdateEachMenu() 
 RenderEachMenu() 
End Function  
 
Function SetMenuAlign(e.typMenu,align=1) ; ������������� ������������ ���� (������������ ���� ����� ���������� ��������� ����) 
 SetFont e\font[0]      ; ����� ��� ����������� ������� ������ 
 
 For i.typMenuItem=Each typMenuItem 
  If i\menu=e Then 
   If align=1 Then i\x=0       ; ������������ �� ������� ���� 
   If align=2 Then i\x=-StringWidth(i\txt)/2  ; ������������ �� ������ 
   If align=3 Then i\x=-StringWidth(i\txt)   ; ������������ �� ������ ���� 
  End If  
 Next 
End Function  
 
Function SetMenuInterval(e.typMenu,in=-1) ; ������������� ��������� ����� ���������� ���� (������������ ���� ����� ���������� ��������� ����) 
Local K          ; ����� ��� "����������" ��������� 
 
 SetFont e\font[0]      ; ����� ��� ����������� ������� ������ 
 For i.typMenuItem=Each typMenuItem 
  If i\menu=e Then 
   If in=-1 Then i\y=StringHeight(i\txt)*k Else i\y=in*k   ; ���� ��������� ��������� �� �����, �� ���������� ��������������� 
   k=k+1 
  End If 
 Next  
End Function  
 
Function SetMenuEnable(e.typMenu,enable) ; ��������� ����������� ���� 
 e\enable=enable  
End Function  
 
Function SetMenuShow(e.typMenu,show)  ; ��������� ��������� ���� 
 e\show=show 
End Function  
 
Function DeleteMenu(e.typMenu)    ; �������� ���� � ���� ��� ��������� 
 For i.typMenuItem=Each typMenuItem 
  If i\menu=e Then Delete I 
 Next 
 Delete e  
End Function  
 
Function GetMenuEnable(e.typMenu)   ; �������� ��������� ����������� ���� 
 Return e\enable 
End Function  
 
Function GetMenuShow(e.typMenu)    ; �������� ��������� ��������� ���� 
 Return e\show 
End Function  
 
 
; ---- �������� ������� ���������� ���������� ���� ---- 
Function CreateMenuItem.typMenuItem(m.typMenu,X,Y,txt$,enable=True,show=True) 
 i.typMenuItem=New typMenuItem 
 i\menu=m 
 i\x=x 
 i\y=y 
 i\txt=txt 
 i\state=0 
 i\enable=enable 
 i\show=show 
Return i  
End Function 
 
Function UpdateMenuItem(e.typMenuItem)  ; ���������� ��������� ���� 
 SetFont e\menu\font[0]      ; ����� ��� ����������� ������� ������ 
            ; ���������� ������ ��������� 
 e\state=0         ; ������� ������ (���� ���� ��� �������) 
 If RectsOverlap(MouseX(),MouseY(),1,1,e\x+e\menu\x,e\y+e\menu\y,StringWidth (e\txt),StringHeight(e\txt)) Then 
  e\state=1 
  If MouseDown(1) And e\enable And e\show And e\menu\enable And e\menu\show And e\show Then e\state=2 
 End If 
End Function 
 
Function RenderMenuItem(e.typMenuItem)  ; ��������� ��������� ���� 
 Select e\state      ; ������������� ��������� ������ ������, � ����������� 
  Case 0:       ; �� ���������   
   SetFont e\menu\font[0] 
   Color e\menu\r[0],e\menu\g[0],e\menu\b[0] 
  Case 1: 
   SetFont e\menu\font[1] 
   Color e\menu\r[1],e\menu\g[1],e\menu\b[1] 
  Case 2: 
   SetFont e\menu\font[2] 
   Color e\menu\r[2],e\menu\g[2],e\menu\b[2] 
  Default: 
 End Select   
 
 If (Not e\enable) Or (Not e\menu\enable) Then ; ���� ������� ���������� ��� ���������� ��� ����, �� ��� �� ����� � ����� 
  SetFont e\menu\font[3] 
  Color e\menu\r[3],e\menu\g[3],e\menu\b[3] 
 End If  
  
 If e\show Then Text e\x+e\menu\x,e\y+e\menu\y,e\txt   ; ���������� ������ �� ������ 
End Function 
 
Function SetMenuItemText(e.typMenuItem,txt$) ; ��������� ������ ������ ��� �������� ���� 
 e\txt=txt 
End Function 
 
Function SetMenuItemEnable(e.typMenuItem,enable) ; ��������� ����������� ��� �������� ���� 
 e\enable=enable 
End Function  
 
Function SetMenuItemShow(e.typMenuItem,show)  ; ��������� ��������� ��� �������� ���� 
 e\show=show 
End Function  
 
Function GetMenuItemEnable(e.typMenuItem)   ; �������� ��������� ����������� �������� 
 Return e\enable 
End Function  
 
Function GetMenuItemShow(e.typMenuItem)    ; �������� ��������� ��������� �������� 
 Return show 
End Function  
 
Function GetMenuItemState(e.typMenuItem)   ; �������� ��������� �������� ���� 
 Return e\state 
End Function  
 
Function MenuClick(e.typMenuItem)     ; ������� �� ������� 
 FlushMouse() 
 If MouseDown(1) Then Return 
 If (Not e\show) Or (Not e\enable) Or (Not e\menu\show) Or (Not e\menu\enable) Then Return  
 
 st=GetMenuItemState(e) 
 If st=2 Then Return True Else Return False  
End Function 
 
Function DeleteMenuItem(e.typMenuItem)    ; ������� ������� ���� 
 Delete e 
End Function  