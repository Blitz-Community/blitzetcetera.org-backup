;(c) Agnislav Lipcan
;e-mail: avelnet@yandex.ru


Graphics3D 1024,768,32,1;<-инициализируем графику
SetBuffer BackBuffer()

;изначально = 2
Global game=2
Global font=LoadFont("courier",20)
SetFont font
Dim Buttons(3)
Global kn1=LoadAnimImage("GUI-Button1.png",64,70,0,3)
Global kn2=LoadAnimImage("GUI-Button2.png",100,40,0,3)
Global kursor=LoadImage("GUI-Cursor.png")
; основа
Repeat 
Select game
Case 1
; начало тела игры
      Cls();<-очищаем экран

     ekran=LoadImage("GUI-Screen2.png");<-загружаем картинку

    camera=CreateCamera(); создаем камеру
    PositionEntity camera,0,10,0; размещаем камеру
    CameraClsColor camera,50,0,180; цвет фона

    plane= CreatePlane()
    
    pltex=LoadTexture("GUI-Grass.png")
    ScaleTexture pltex,10,10
    EntityTexture plane,pltex
        FreeTexture pltex

    lit=CreateLight()
    

  Repeat;<-начало главного цикла

      If KeyHit(1) Or Buttons(1)=1 Then game=2 Buttons(1)=0

      TurnEntity camera,0,-1,0; поворачиваем камеру

      UpdateWorld()
      RenderWorld()

      DrawImage ekran,0,0
      RefreshButton(kn1,952,12,"",1)
      DrawImage kursor,MouseX(),MouseY()

      Flip
      
   Until game=2;<-конец главного цикла
      FreeEntity camera
      FreeEntity plane 
      FreeEntity lit 
      FreeImage ekran

Case 2
; здесь меню
   Cls();<-очищаем экран

    camera=CreateCamera(); создаем камеру
    PositionEntity camera,0,10,0; размещаем камеру
        CameraFogMode camera,1
        CameraFogRange camera,.1,50

    plane= CreatePlane()
    
    pltex=LoadTexture("GUI-Grass.png")
    ScaleTexture pltex,10,10
    EntityTexture plane,pltex
       FreeTexture pltex

    lit=CreateLight()
   
       

   Repeat 
   
           If exitprog=True End ;<-конец, выходим

      If KeyHit(1) Or Buttons(2)=1 Then exitprog=True Buttons(2)=0
      If  Buttons(0)=1 Then game=1 Buttons(0)=0

      TurnEntity camera,0,-1,0; поворачиваем камеру

      UpdateWorld()
      RenderWorld()

           RefreshButton(kn2,462,300,"старт",0)
      RefreshButton(kn2,462,450,"выход",2)
           Color 230,230,230
      Text 430,150,"здесь будет меню"
      DrawImage kursor,MouseX(),MouseY()

      Flip 
   Until game=1
      FreeEntity camera
      FreeEntity plane 
      FreeEntity lit 


End Select
Until  exitprog=True

Function RefreshButton(ImBtn,x,y,txt$,n)
mx=MouseX() : my=MouseY();координаты мыши 
MHit=MouseDown(1);проверяем нажатие левой мыши 
If RectsOverlap(x+2,y+2,ImageWidth(ImBtn)-4,ImageHeight(ImBtn)-4,mx,my,1,1) Then; наведение
     f=1
     y2=0
     If MHit=True Then ; нажатие мыши
           f=2 
           y2=2 ; сдвигаем текст кнопки ниже на 2 пикселя
           Buttons(n)=1 ; записываем в массив, что кнопка нажата
     EndIf 
Else ; иначе кнопка неактивна
     f=0
     y2=0
EndIf 
DrawImage ImBtn,x,y,f ; рисуем кнопку в нужном положении
Color 0,0,0 ; цвет текста
Text x + 0.5 * (ImageWidth(ImBtn) + 4 - Len(txt$) * FontHeight() * 0.5), y + 0.5 * (ImageHeight(ImBtn) - 4 - FontHeight()) + y2, txt$
End Function 