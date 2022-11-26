;(c) Agnislav Lipcan
;e-mail: avelnet[@]yandex.ru

;изначально = 1
Global game=1

; основа
Repeat 
Select game
Case 1
; начало тела игры
  Cls();<-очищаем экран
  Graphics3D 1024,768,32,1;<-инициализируем графику
  SetBuffer BackBuffer()

  ekran=LoadImage("GUI-Screen.png");<-загружаем картинку

  Repeat;<-начало главного цикла

      If KeyHit(1) game=2

      UpdateWorld
      RenderWorld

      DrawImage ekran,0,0

      Flip

   Until game=2;<-конец главного цикла

Case 2
; здесь меню
   Cls();<-очищаем экран
   Graphics 1024,768,32,1;<-инициализируем графику
   font=LoadFont("Arial Cyr",24)
   SetFont font
   FlushKeys();<-очищаем клавиатуру (всмысле нажатые клавиши)
   Text 430,300,"здесь будет меню"
   WaitKey()
   End ;<-конец, выходим
End Select
Forever 