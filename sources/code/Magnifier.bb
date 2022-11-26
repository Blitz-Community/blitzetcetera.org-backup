Graphics 800, 600 
SetBuffer BackBuffer() 
HidePointer() 
Const width=180, height=120, begin_x=300, begin_y=100 
;Create images 
Global img1=CreateImage(width, height);изображение, которое можно увеличивать 
Global img2=CreateImage(width, height);изображение img1 увеличенное в 2 раза(увеличение можно изменить) 
Global result=CreateImage(40, 40);Результат преобразований 
;Global form=CreateImage(40, 40);А это можно убрать(ето для показа формы лупы) 
Dim matrix(ImageWidth(result), ImageHeight(result));матрица для хранения суммы red+green+blue цветов пикселей курсора 
                                                                        ;(если сумма меньше 4, то в ето место вставлять пиксел из img2) 
ClsColor 200, 200, 200 
Cls 
;создание тестового поля : (begin_x, begin_y) - координаты верхнего левого угла поля 
                                    ;width- ширина поля, height - высота 
Color 0, 0, 0 
SetFont(LoadFont("arial", 0, 0, 0)) 
s$="Blitz3D rulezz!!!" 
For k=0 To 3 
      s="  "+s 
      Text 300, 100+k*16, s 
Next 
Text 300, 180, "Made by Funny Compilers" 
GrabImage img1, begin_x, begin_y;сохранение тестового поля в img1 
GrabImage img2, begin_x, begin_y;сохранение тестового поля в img2 
ScaleImage img2, 2, 2;увеличение img2 в 2 раза 
;создание курсора, если (red+green+blue)<4, то на этом месте будет увеличенное изображене 
      Color 1, 0, 0 
      Oval 0, 0, 25, 30 
      Oval 20, 10, 20, 15 
;сохранение суммы цветов каждого пикселя курсора 
For k=0 To ImageWidth(result) 
      For j=0 To ImageHeight(result) 
            GetColor k, j 
            matrix(k, j)=ColorRed()+ColorGreen()+ColorBlue() 
      Next 
Next 
;CopyRect 0, 0, ImageWidth(form), ImageHeight(form), 0, 0, BackBuffer(), ImageBuffer(form);Это тоже можно убрать 
While Not KeyHit(1) 
      Cls 
      DrawImage img1, begin_x, begin_y;прорисовка тестового поля 
      Color 0, 0, 0 
;      Text 0, 0, "Form of magnifying glass : " 
;      DrawImage form, 200, 0;Это тоже можно убрать 
      drawcursor();прорисовка лупы 
      Flip() 
Wend 
End 
 
Function DrawCursor() 
      x=MouseX():y=MouseY() 
      If x<begin_x+ImageWidth(result)/2 x=begin_x+ImageWidth(result)/2;ограничение перемещения курсора в пределах тестового поля 
      If x>begin_x-ImageWidth(result)/2+width x=begin_x-ImageWidth(result)/2+width; --:-- 
      If y<begin_y+ImageHeight(result)/2 y=begin_y+ImageHeight(result)/2; --:-- 
      If y>begin_y-ImageHeight(result)/2+height  y=begin_y-ImageHeight(result)/2+height; --:-- 
      MoveMouse x, y 
      x=x-begin_x:y=y-begin_y;Все действия относительно верхнего угла тестового поля 
      ;полное копирование увеличения в result 
      CopyRect x*2-ImageWidth(result)/2, y*2-ImageHeight(result)/2, ImageWidth(result), ImageHeight(result), 0, 0, ImageBuffer(img2), ImageBuffer(result) 
      x=x-ImageWidth(result)/2:y=y-ImageHeight(result)/2 
      ;цикл - если сумма цветов пикселей курсора < 4, вывод пикселя из result на экран, иначе-ничего 
      LockBuffer(ImageBuffer(result)) 
      LockBuffer(BackBuffer()) 
      For k=x+1 To x+ImageWidth(result)-1 
            For j=y+1 To y+ImageHeight(result)-1 
                  If matrix((k-x), (j-y))<=3 CopyPixelFast k-x, j-y, ImageBuffer(result), k+begin_x, j+begin_y 
            Next 
      Next 
      UnlockBuffer(ImageBuffer(result)) 
      UnlockBuffer(BackBuffer()) 
End Function