Type vrazhina ; ��������� �� ������������ ������  
Field gad.ass  ; ������������ ������  
End Type   
  
Type ass   ; �������� ������������� �������  
Field holl%  
Field nuts$  
Field faak$  
Field X%  
Field y%  
End Type  
  
;/-------------------------------------------�������� �����������������-------------------------------------------  
  
Graphics 640,480,16,2  
  
Const UPS=60  
font = LoadFont("verbana",16)  
SetFont font  
  
vrag.vrazhina = New vrazhina  ; ������������� �������  
For i=0 To Rand(1,1000);500000)    ; � ������� ���� ����� ���� ����� �����   
       vrag\gad = New ass  
       vrag\gad\holl = i  
       vrag\gad\nuts = "����"  
       vrag\gad\faak = "��� - �� �������"  
       vrag\gad\x = Rand(0,680)  
       vrag\gad\y = Rand(0,480)  
Next  
  
.mine_loop  
period=1000/UPS  
time=MilliSecs()-period  
  
Repeat  
 Repeat  
  elapsed=MilliSecs()-time  
 Until elapsed   
 ticks=elapsed/period  
 tween#=Float(elapsed Mod period)/Float(period)  
  
 num = 0  
 For vrag\gad.ass=Each ass ; ������ ������ �������  
    
  Color Rand(0,255),Rand(0,255),Rand(0,255)      ;������ ��� �����������  
  Oval vrag\gad\x, vrag\gad\y, Rand(0,20), Rand(0,20) ;������ ��� �����������  
  Text vrag\gad\x,vrag\gad\y+18,vrag\gad\nuts,True,True  
  If vrag\gad\holl > 0  
   vrag\gad\holl = vrag\gad\holl-1  
  Else  
   Text vrag\gad\x,vrag\gad\y-18,vrag\gad\faak,True  
   Delete vrag\gad ; ����������� ����� ������� ������������ ��������  
  EndIf     ; ������������� �������! ����� ��� ����� ���  
  num = num-1   ; ������->�����  
 Next  
 If num = 0 Text 320,240,"������ - ��� ������ :>((",True,True   
   
 For k=1 To ticks  
  time=time+period   
  If KeyHit(1) End   
 Next  
   
 Flip  
 Cls  
Forever  