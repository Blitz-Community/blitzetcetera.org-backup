; Тьюториал: Как создать скринсейвер.
; Автор: Grisu
; Перевод: MANIAK_dobrii
; Версия: 1.00


; Это код готового скринсейвера.
;
; 1. Просто вставь свой код в функции "Configure()" и "Start()".
;
; 2. Скомпилируй прогу и поменяй расширение с ".exe" на ".scr".
;
; 3. Положи свою скрину(& файлы если есть) в корневую папку Windows(C:\Windows\ например)


; Известные проблемы со скринами, написанными в blitz-е:
;
; - Невозможно показать препросмотр скрины в окне препросмотра скринсейвера Windows.
;
; - При запуске скрины, сначала появится маленькое черненькое окно.


Const SCREENX=800 ; 2 константы для установки разрешения
Const SCREENY=600 

; ВАЖНО
; Нужно чтобы запустить твою скрину под Windows!!!.
; Она меняет процессную дирикторию на ту, где лежит скринсейвер.
; Комментирую эту строку во время тестов, т.к. "appdir" Blitz-а это "\bin".
; иначе твоя скрина просто вылетит!!! :)

;ChangeDir SystemProperty$("appdir")

; Смотрим, какие параметры нам дала Windows 

If CommandLine$() <> "" Then ; Если параметр есть то: 
  If Upper(Left$(CommandLine$(),2)) = "/C" Then Configure() ; надо включать окно установок
  If Upper(Left$(CommandLine$(),2)) = "/S" Then Start() ; или надо включать сам скринсейвер? 
EndIf ; <> ""

End ; Конец проги, bye bye(прим. автора:)(прим. пер.))


Function Configure()

; Вставляй сюда код окна установок.
; Если нужно, то можно врубить внешнюю прогу. Например написанную на VB, Delphi или другом.

 ; Просто чтобы показать, что окно установок включилось!

 AppTitle "Screensaver Options" ; Меняем заголовок окна
 Graphics 640,480,16,2 ; ставим GFX режим
 SetBuffer BackBuffer()

 Repeat 
  Text 10,10, "Тьюториал Grisu по созданию скринсейверов" 
  Text 10,20, "Перевел MANIAK_dobrii"
  Text 10,30, "-----------------------------------------"
  Text 10,50, "Это просто пример простейшего скринсейвера." 
  Text 10,70, "Так что тут нечего конфигурировать!" 
  Text 10,450,"Нажми кнопку или пошируди мышой... :)" 
  Flip
  Delay 1 ; не будем использовать все 100% CPU!
 Until GetKey() Or WaitMouse() 

End Function ; конец Configure()


Function Start()

; Вставляй сюда код самого скринсейвера.

 AppTitle "Screensaver Tutorial1" ; The Name of your Screensaver
 Graphics SCREENX,SCREENY,16,1 ; set GFX Mode
 SetBuffer BackBuffer() 

 FlushKeys() ; чистим буфер клавы
 FlushMouse() ; чистим буфер мыши 
 MoveMouse 0,0 ; бвигаем мышу в 0,0 для последующей проверки

 Textout$="Тьюториал Grisu по созданию скринсейверов" ; Этот текст будет написан

 Repeat ; Начало главного цикла 

  Color Rand(255),Rand(255),Rand(255) 
  Rect 0,0,SCREENX,SCREENY,1

  Color 0,0,0 
  For y=0 To SCREENY Step 2 
   Rect 0,y,SCREENX,1 
  Next 

  Rect 50,50,SCREENX-100,SCREENY-100,1 ; Рисуем черный квадрат в центре

  Color 255,255,255 ; пишем текст
  Text (SCREENX-StringWidth(Textout$))/2,SCREENY/2, Textout$ 

  Flip ; flip-аем буфферы
  Delay 1 ; не будем использовать все 100% CPU!
 Until GetMouse() <> 0 Or MouseX() <> 0 Or MouseY() <> 0 Or GetKey() <> 0 ; проверяем, дергает ли пользователь судорожно мышкой, или бьет по клаве :) 

End Function ; конец Start() 