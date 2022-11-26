Type vrazhina ; óêàçàòåëü íà äèíàìè÷åñêèé ìàññèâ  
Field gad.ass  ; äèíàìè÷åñêèé ìàññèâ  
End Type   
  
Type ass   ; îïèñàíèå äèíàìè÷åñêîãî ìàññèâà  
Field holl%  
Field nuts$  
Field faak$  
Field X%  
Field y%  
End Type  
  
;/-------------------------------------------ïðîâåðêà ðàáîòîñïîñîáíîñòè-------------------------------------------  
  
Graphics 640,480,16,2  
  
Const UPS=60  
font = LoadFont("verbana",16)  
SetFont font  
  
vrag.vrazhina = New vrazhina  ; èíèöèàëèçàöèÿ ìàññèâà  
For i=0 To Rand(1,1000);500000)    ; ê ïðèìåðó òóòà ìîæåò áûòü ËÞÁÎÅ ÷èñëî   
       vrag\gad = New ass  
       vrag\gad\holl = i  
       vrag\gad\nuts = "áÿêà"  
       vrag\gad\faak = "ðàç - íå ëåãîëàñ"  
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
 For vrag\gad.ass=Each ass ; ÷òåíèå äàííûõ ìàññèâà  
    
  Color Rand(0,255),Rand(0,255),Rand(0,255)      ;ïðîñòî äëÿ íàãëÿäíîñòè  
  Oval vrag\gad\x, vrag\gad\y, Rand(0,20), Rand(0,20) ;ïðîñòî äëÿ íàãëÿäíîñòè  
  Text vrag\gad\x,vrag\gad\y+18,vrag\gad\nuts,True,True  
  If vrag\gad\holl > 0  
   vrag\gad\holl = vrag\gad\holl-1  
  Else  
   Text vrag\gad\x,vrag\gad\y-18,vrag\gad\faak,True  
   Delete vrag\gad ; ÎÁßÇÀÒÅËÜÍÎ ÍÓÆÍÎ ÓÄÀËßÒÜ ÎÒÐÀÁÎÒÀÍÍÛÅ ÝËÅÌÅÍÒÛ  
  EndIf     ; ÄÈÍÀÌÈ×ÅÑÊÎÃÎ ÌÀÑÑÈÂÀ! ÈÍÀ×Å ÈËÈ ÂÈÑËÎ ÈËÈ  
  num = num-1   ; ÒÎÐÌÎÇ->ÂÈÑËÎ  
 Next  
 If num = 0 Text 320,240,"ÊÎÐÎ×Å - ÂÑÅ ÓÌÅÐËÈ :>((",True,True   
   
 For k=1 To ticks  
  time=time+period   
  If KeyHit(1) End   
 Next  
   
 Flip  
 Cls  
Forever  