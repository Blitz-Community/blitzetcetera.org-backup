If KeyHit(88) skreenshot()  
  
;=======================  
Function skreenshot()  
  
aa%=1  
fold$=CurrentDir$()+"\screens"  
dir=ReadDir(fold$)  
Repeat  
p$="screen"+aa+".bmp"  
File$=NextFile$(dir)  
If File$="" Then Exit  
If FileType(fold$+"\"+File$)=1  
  If p$=File$ aa=aa+1  
EndIf  
Forever  
CloseDir dir  
pp$="screens\"+"screen"+aa+".bmp"  
  
SaveBuffer (BackBuffer(),pp$)  
End Function  
;======================