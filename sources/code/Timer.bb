; ================================================================== 
; Project Title: Библиотека для таймера и секундомера 
; Author:       Frank (Акулинин Алекснадр) 
; Email:   Aculinin@mail.ru 
; Version:       1.0 
; Date:     15.09.05 
; Notes:      С помощью этой библиотеки вы сможете 
;        легко реализовать таймер и секундомер 
;     
; ================================================================== 
 
 
Type Sec       ; Тип для секундомера 
 Field h,m,s,ms     ; Часы, минуты, секунды, миллисекунды 
 Field ml      ; Вспомогательная переменная (для "ровного хода") 
 Field enabled      ; Запущен ли секундомер 
End Type 
 
Type Tmr       ; Тип для таймера 
 Field h,m,s,ms     ; Часы, минуты, секунды, миллисекунды 
 Field ml      ; Вспомогательная переменная (для "ровного хода") 
 Field enabled     ; Запущен ли таймер 
 Field complete     ; Завершен ли таймер 
End Type 

; ---- Пример

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
 Text 10,10,"Секундомер:" 
 Text 10,70,"Таймер:" 
 RenderSec(s,300,10,1,0,0,fnt,255,0,0) 
 RenderTmr(t,300,70,0,0,0,fnt,255,0,0) 
 
 If TmrComplete(t) Then 
  If Sin(MilliSecs()/5)>0 Then Color 0,0,255:Text 400,300,"Таймер завершен!",1,1 
 End If  
 
 Flip  
Until KeyDown(1)
 
; --- Для работы с секундомером --- 
Function CreateSec.sec(en=True)    ; Создаем новый секундомер 
 sec.sec=New sec 
 sec\enabled=en 
 sec\ml=MilliSecs() 
 Return sec 
End Function 
 
Function UpdateSec(e.sec)     ; Обновляем секундомер 
 If Not e\enabled Then Return 
 If MilliSecs()>e\ml Then    ; Если время поменялось, то 
  e\ms=e\ms+MilliSecs()-e\ml   ; меняем миллисекунды 
  If e\ms>999 Then e\s=e\s+(e\ms/1000):e\ms=e\ms Mod 1000  ; если миллисекунды>999 то меняем секунды 
  If e\s>59   Then e\m=e\m+(e\s/60):e\s=e\s Mod 60   ; и т.д. 
  If e\m>59   Then e\h=e\h+(e\m/60):e\m=e\m Mod 60   ; оператор Mod используется для 
  If e\h>23   Then e\h=e\h Mod 24        ; более точного и независимого вычисления 
  e\ml=MilliSecs()      ; Запомним текущее состояние 
 End If  
End Function 
 
Function RenderSec(e.sec,X,Y,ml=0,cx=0,cy=0,font=0,r=-1,g=-1,b=-1)  ; Рисуем секундомер 
 If font<>0 Then SetFont font     ; Если указан шрифт, то его установим 
 If r>-1 And g>-1 And b>-1 Then Color r,g,b  ; Если указан цвет, то его установим 
 
 If ml Then           ; Если миллисекунды надо отоброжать, то 
  Text X,Y,e\h+":"+e\m+":"+e\s+"."+e\ms,cx,cy ; рисуем вот так, 
 Else           ; иначе 
  Text X,Y,e\h+":"+e\m+":"+e\s,cx,cy   ; вот так. cx и cy - выравнивание по 
 End If           ; оси X и Y соответственно 
End Function 
 
Function ClearSec(e.sec)     ; Очистить показатель секундомера 
 e\h=0:e\m=0:e\s=0:e\ms=0 
End Function  
 
Function SecEnabled(e.sec,en)    ; Включить/выключить секундомер 
 If en=True And e\enabled=False Then e\ml=MilliSecs() ; Чтобы при включении, время отсчитывалось 
 e\enabled=en           ; с текущего места. 
End Function 
 
; --- Для работы с таймером --- 
Function CreateTmr.tmr(h,m,s=0,en=True)  ; Создание таймера 
 t.tmr=New tmr 
 t\h=h:t\m=m:t\s=S 
 t\enabled=en 
 t\ml=MilliSecs() 
 Return t 
End Function 
 
Function UpdateTmr(e.tmr)     ; Обновляем таймер 
 If Not e\enabled Then Return 
 If MilliSecs()>e\ml Then 
  e\ms=e\ms-(MilliSecs()-e\ml)  ; Аналогично секундомеру, только наоборот 
  If e\ms<0 Then e\s=e\s-((1000-e\ms)/1000):e\ms=(999 Mod (1000-e\ms)) 
  If e\s<0  Then e\m=e\m-((60-e\s)/60):e\s=(59 Mod (60-e\s)) 
  If e\m<0  Then e\h=e\h-((60-e\m)/60):e\m=(59 Mod (60-e\m)) 
  ; Контроль за часами у таймера не нужен, но при отрицательно часе лего отследить, 
  ; что таймер закончил свою работу 
  If e\h<0 Then      ; Если таймер "дошёл до нуля" 
   SetTmr(e,0,0,0,0)    ; Устанавливаем таймер на ноль 
   TmrEnabled(e,False)    ; Выключаем таймер 
   e\complete=True     ; Завершаем таймер 
  End If  
  e\ml=MilliSecs()     ; Запомним текущее состояние 
 End If  
End Function 
 
Function RenderTmr(e.tmr,X,Y,ml=0,cx=0,cy=0,font=0,r=-1,g=-1,b=-1)  ; Рисуем таймер 
 If font<>0 Then SetFont font     ; Если указан шрифт, то его установим 
 If r>-1 And g>-1 And b>-1 Then Color r,g,b  ; Если указан цвет, то его установим 
 
 If ml Then           ; Если миллисекунды надо отоброжать, то 
  Text X,Y,e\h+":"+e\m+":"+e\s+"."+e\ms,cx,cy ; рисуем вот так, 
 Else           ; иначе 
  Text X,Y,e\h+":"+e\m+":"+e\s,cx,cy   ; вот так. cx и cy - выравнивание по 
 End If           ; оси X и Y соответственно 
End Function 
 
Function TmrComplete(e.tmr)     ; Если таймер выполнен, то True иначе False 
 Return e\complete 
End Function  
 
Function TmrEnabled(e.tmr,en)    ; Включить/выключить таймер 
 If en=True And e\enabled=False Then e\ml=MilliSecs() ; Чтобы при включении, время отсчитывалось 
 e\enabled=en  
End Function 
 
Function SetTmr(e.tmr,h,m,s,ms=0)   ; Переустановить таймер 
 e\h=h:e\m=m:e\s=s:e\ms=ms 
End Function  