;Вывод форматированного текста с использованием тэгов 
;ОПИСАНИЕ : 
;Тэги заключаются в квадратных скобках('[', ']') 
;ТЭГИ : 
;  'b' - жирный 
;  'i' - курсив 
;  'u' - подчеркнутый 
;  Для отмены режимов необходимые теги писать ПОСЛЕ '/' 
;  Примеры : 
;   [bi] - жирный курсив 
;   [/biu] - нормальный текст(отменяет все режимы после '/') 
;   [bu/bi] - устанавливает и тут же отменяет жирность, устанавливает подчеркнутость, отменяет курсив 
;ЦВЕТ : 
;  формат : [xxx] = rgb 
;  вместо x вставлять 1 или 0 
;  Примеры : 
;   [111] = белый(red=255, grenn=255, blue=255) 
;   [100] = красный(red=255, grenn=0, blue=0) 
;   [000] = черный(red=0, grenn=0, blue=0) 
;ФУНКЦИИ : 
;  InitFonts(Font$, height) - инициализация шрифтов 
;  FText(x, y, string$) - вывести строку string$ в координатах (x, y) 
;ДАННЫЕ : 
;  fonts(1, 1, 1)-массив шрифтов 
Dim fonts(1, 1, 1) 
;---------------------------------------------- 
Function InitFonts(Font$="system", h=20) 
 For kk=0 To 1 
  For jj=0 To 1 
   For ii=0 To 1 
    fonts(kk, jj, ii)=LoadFont(font$, h, kk, jj, ii) 
   Next 
  Next 
 Next 
 SetFont(fonts(0, 0, 0)) 
End Function 
;---------------------------------------------- 
Function FText(x, y, S$) 
 italic=0:bold=0:underline=0 
 stag$="":btag=0:jk=1 
 SetFont(fonts(0, 0, 0)) 
 While jk<=Len(s) 
  char$=Mid$(s, jk, 1) 
  If char="[";Open tag 
   stag="[":jk=jk+1 
   While jk<=Len(s$) 
    char$=Mid$(s, jk, 1) 
    If (char="b") Or (char="u") Or (char="i") Or (char="/") Or (char="1") Or (char="0");settings 
     stag=stag+char 
    ElseIf (char="]");close tag 
     z=1:rgb=0:cc=1 
     For jj=2 To Len(stag) 
      ch$=Mid$(stag, jj, 1) 
      If Ch="b" bold=z 
      If Ch="i" italic=z 
      If Ch="u" underline=z 
      If Ch="/" z=0 
      If ch="0" cc=cc*2 
      If ch="1" rgb=rgb+cc:cc=cc*2 
     Next 
     SetFont(fonts(bold, italic, underline)) 
     If cc=8 Color 255*(rgb And 1), 255*(rgb And 2), 255*(rgb And 4) 
     stag="" 
     char="" 
     Exit 
    Else;incorrect char 
     jk=jk-1 
     char="" 
     Exit 
    End If 
    jk=jk+1 
   Wend;search tag 
  End If;char="[" 
  char=stag+char 
  stag="" 
  Text x, y, char 
  x=x+StringWidth(char) 
  jk=jk+1 
 Wend 
End Function 
;========= Тестовая прога(можно удалить) ========== 
Graphics 800, 600 
InitFonts("arial", 30);инициализация шрифтов(всегда вызывать перед использованием FText(...)) 
FText(100, 100, "[b100]It[/b111] is a [iu]simple[/u] example.") 
FText(100, 150, "[001bu]Blitz3D[111/ub] rulezzzz[010]z[111]zz!!!") 
FText(100, 200, "[biu110]Bold & italic & underline, yellow color.") 
WaitKey() 
End