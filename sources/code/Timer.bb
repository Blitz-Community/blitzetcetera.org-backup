; ================================================================== 
; Project Title: ���������� ��� ������� � ����������� 
; Author:       Frank (�������� ���������) 
; Email:   Aculinin@mail.ru 
; Version:       1.0 
; Date:     15.09.05 
; Notes:      � ������� ���� ���������� �� ������� 
;        ����� ����������� ������ � ���������� 
;     
; ================================================================== 
 
 
Type Sec       ; ��� ��� ����������� 
 Field h,m,s,ms     ; ����, ������, �������, ������������ 
 Field ml      ; ��������������� ���������� (��� "������� ����") 
 Field enabled      ; ������� �� ���������� 
End Type 
 
Type Tmr       ; ��� ��� ������� 
 Field h,m,s,ms     ; ����, ������, �������, ������������ 
 Field ml      ; ��������������� ���������� (��� "������� ����") 
 Field enabled     ; ������� �� ������ 
 Field complete     ; �������� �� ������ 
End Type 

; ---- ������

Graphics 800,600,32,2 
SetBuffer BackBuffer() 
 
fnt=LoadFont("Arial Cyr",50,1) 
 
s.sec=CreateSec() 
t.tmr=CreateTmr(0,0,10) 
 
Repeat 
 Cls 
 UpdateSec(s) 
 UpdateTmr(t) 
 
 SetFont fnt:Color 0,255,0 
 Text 10,10,"����������:" 
 Text 10,70,"������:" 
 RenderSec(s,300,10,1,0,0,fnt,255,0,0) 
 RenderTmr(t,300,70,0,0,0,fnt,255,0,0) 
 
 If TmrComplete(t) Then 
  If Sin(MilliSecs()/5)>0 Then Color 0,0,255:Text 400,300,"������ ��������!",1,1 
 End If  
 
 Flip  
Until KeyDown(1)
 
; --- ��� ������ � ������������ --- 
Function CreateSec.sec(en=True)    ; ������� ����� ���������� 
 sec.sec=New sec 
 sec\enabled=en 
 sec\ml=MilliSecs() 
 Return sec 
End Function 
 
Function UpdateSec(e.sec)     ; ��������� ���������� 
 If Not e\enabled Then Return 
 If MilliSecs()>e\ml Then    ; ���� ����� ����������, �� 
  e\ms=e\ms+MilliSecs()-e\ml   ; ������ ������������ 
  If e\ms>999 Then e\s=e\s+(e\ms/1000):e\ms=e\ms Mod 1000  ; ���� ������������>999 �� ������ ������� 
  If e\s>59   Then e\m=e\m+(e\s/60):e\s=e\s Mod 60   ; � �.�. 
  If e\m>59   Then e\h=e\h+(e\m/60):e\m=e\m Mod 60   ; �������� Mod ������������ ��� 
  If e\h>23   Then e\h=e\h Mod 24        ; ����� ������� � ������������ ���������� 
  e\ml=MilliSecs()      ; �������� ������� ��������� 
 End If  
End Function 
 
Function RenderSec(e.sec,X,Y,ml=0,cx=0,cy=0,font=0,r=-1,g=-1,b=-1)  ; ������ ���������� 
 If font<>0 Then SetFont font     ; ���� ������ �����, �� ��� ��������� 
 If r>-1 And g>-1 And b>-1 Then Color r,g,b  ; ���� ������ ����, �� ��� ��������� 
 
 If ml Then           ; ���� ������������ ���� ����������, �� 
  Text X,Y,e\h+":"+e\m+":"+e\s+"."+e\ms,cx,cy ; ������ ��� ���, 
 Else           ; ����� 
  Text X,Y,e\h+":"+e\m+":"+e\s,cx,cy   ; ��� ���. cx � cy - ������������ �� 
 End If           ; ��� X � Y �������������� 
End Function 
 
Function ClearSec(e.sec)     ; �������� ���������� ����������� 
 e\h=0:e\m=0:e\s=0:e\ms=0 
End Function  
 
Function SecEnabled(e.sec,en)    ; ��������/��������� ���������� 
 If en=True And e\enabled=False Then e\ml=MilliSecs() ; ����� ��� ���������, ����� ������������� 
 e\enabled=en           ; � �������� �����. 
End Function 
 
; --- ��� ������ � �������� --- 
Function CreateTmr.tmr(h,m,s=0,en=True)  ; �������� ������� 
 t.tmr=New tmr 
 t\h=h:t\m=m:t\s=S 
 t\enabled=en 
 t\ml=MilliSecs() 
 Return t 
End Function 
 
Function UpdateTmr(e.tmr)     ; ��������� ������ 
 If Not e\enabled Then Return 
 If MilliSecs()>e\ml Then 
  e\ms=e\ms-(MilliSecs()-e\ml)  ; ���������� �����������, ������ �������� 
  If e\ms<0 Then e\s=e\s-((1000-e\ms)/1000):e\ms=(999 Mod (1000-e\ms)) 
  If e\s<0  Then e\m=e\m-((60-e\s)/60):e\s=(59 Mod (60-e\s)) 
  If e\m<0  Then e\h=e\h-((60-e\m)/60):e\m=(59 Mod (60-e\m)) 
  ; �������� �� ������ � ������� �� �����, �� ��� ������������ ���� ���� ���������, 
  ; ��� ������ �������� ���� ������ 
  If e\h<0 Then      ; ���� ������ "����� �� ����" 
   SetTmr(e,0,0,0,0)    ; ������������� ������ �� ���� 
   TmrEnabled(e,False)    ; ��������� ������ 
   e\complete=True     ; ��������� ������ 
  End If  
  e\ml=MilliSecs()     ; �������� ������� ��������� 
 End If  
End Function 
 
Function RenderTmr(e.tmr,X,Y,ml=0,cx=0,cy=0,font=0,r=-1,g=-1,b=-1)  ; ������ ������ 
 If font<>0 Then SetFont font     ; ���� ������ �����, �� ��� ��������� 
 If r>-1 And g>-1 And b>-1 Then Color r,g,b  ; ���� ������ ����, �� ��� ��������� 
 
 If ml Then           ; ���� ������������ ���� ����������, �� 
  Text X,Y,e\h+":"+e\m+":"+e\s+"."+e\ms,cx,cy ; ������ ��� ���, 
 Else           ; ����� 
  Text X,Y,e\h+":"+e\m+":"+e\s,cx,cy   ; ��� ���. cx � cy - ������������ �� 
 End If           ; ��� X � Y �������������� 
End Function 
 
Function TmrComplete(e.tmr)     ; ���� ������ ��������, �� True ����� False 
 Return e\complete 
End Function  
 
Function TmrEnabled(e.tmr,en)    ; ��������/��������� ������ 
 If en=True And e\enabled=False Then e\ml=MilliSecs() ; ����� ��� ���������, ����� ������������� 
 e\enabled=en  
End Function 
 
Function SetTmr(e.tmr,h,m,s,ms=0)   ; �������������� ������ 
 e\h=h:e\m=m:e\s=s:e\ms=ms 
End Function  