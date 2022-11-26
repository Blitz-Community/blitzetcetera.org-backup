Graphics 800, 600:SetBuffer BackBuffer() 
 
fn=LoadFont("system", 1, 0, 0, 0):SetFont(fn) 
 
box.TDialog=CreateDialog(100, 100):showdialog(box) 
AddString(box, "Ну как?! Хорошо?") 
AddButton(box, "Хорошо", 2) 
AddButton(box, "Плохо", 1) 
AddButton(box, "Выход", -2) 
 
box2.TDialog=CreateDialog(150, 150):SetBackColor(box2, 200, 100, 100) 
AddString(box2, "Вы наверное ошиблись.") 
AddButton(box2, "Да, я ошибся...", -11) 
 
While 1 
Cls 
c=DrawDialogs() 
If c=2 addString(box, "Спасибо!") 
If c=1 ShowDialog(box2) 
If c=-2 End 
Flip 
Wend 
;==================================== 
;============ ОСНОВА =============== 
;==================================== 
Global count_of_dialogBox_=0 
Global Enable_press_dialogBox_=1 
;Тип - строка 
Type TString 
Field content$ 
Field ID 
End Type 
;Тип - кнопка 
Type TButton 
Field content$ 
Field number 
Field x 
Field y 
Field dx 
Field dy 
Field ID 
End Type 
;Тип - диалоговое окно 
Type TDialog 
Field x, y 
Field visible 
Field StrCount 
Field BtnCount 
Field ID 
Field maxWidth1, maxWidth2 
Field r2, g2, b2 
Field  r1, g1, b1 
End Type 
 
Function CreateDialog.TDialog(x=0, y=0) 
a.TDialog = New TDialog 
a\BtnCount=0 
a\StrCount=0 
count_of_dialogBox_=count_of_dialogBox_+1 
a\ID=count_of_dialogBox_ 
a\maxWidth1=0 
a\maxWidth2=0 
a\r1=200:a\g1=200:a\b1=200 
a\x=x:a\y=y 
a\visible=0 
Return a 
End Function 
 
Function SetBackColor(box.TDialog, red, green, blue) 
box\r1=red 
box\g1=green 
box\b1=blue 
End Function 
 
Function SetTextColor(box.TDialog, red, green, blue) 
box\r2=red 
box\g2=green 
box\b2=blue 
End Function 
 
Function AddString(box.TDialog, s$) 
a.TString = New TString 
a\content$ = s$ 
a\ID=box\ID 
box\StrCount = box\StrCount+1 
If box\maxWidth1<StringWidth(s$) box\maxWidth1=StringWidth(s$)  
End Function 
 
Function AddButton(box.TDialog, b$, num) 
a.TButton = New TButton 
a\content$ = b$ 
a\number = num 
a\ID=box\ID 
box\BtnCount = box\BtnCount+1 
If box\maxWidth2<StringWidth(b$) box\maxWidth2=StringWidth(b$)  
End Function 
 
Function DrawDialogs() 
Local press=0 
Local ScreenW=GraphicsWidth() Shr 1 
Local ScreenH=GraphicsHeight() Shr 1 
Local mx=MouseX() 
Local my=MouseY() 
Local w1, h1, w2, h2 
For box.TDialog=Each TDialog 
If Not box\visible Exit 
w1=box\MaxWidth1 
w2=box\MaxWidth2 
h1=(box\StrCount)*StringHeight("W") 
h2=3*(box\BtnCount)*StringHeight("W") Shr 1 
;Высота окна 
If h2>h1 h1=h2 
;Вывод фона 
Color box\r1, box\g1, box\b1:Rect box\x, box\y, w1+w2+30, h1+20, True 
Color box\r2, box\g2, box\b2:Rect box\x+2, box\y+2, w1+w2+26, h1+16, False 
;Вывод строк 
y=box\y+10 
For ss.TString = Each TString 
 If box\ID=ss\ID 
    Text box\x+10, y, ss\content$, 0, 0 
  y=y+StringHeight("W") 
 End If 
Next 
;Вывод кнопок 
y=box\y+10:x=box\x+w1+15 
For bb.TButton = Each TButton 
 If box\ID=bb\ID  
 Color box\r2, box\g2, box\b2 
 If (mx>x) And (mx<x+w2+10) And (my>y) And (my<y+StringHeight("W")) 
  Rect  x, y, w2+10, StringHeight("W"), 0 
  If MouseDown(1) And (Enable_press_dialogBox_=1)  
   press=bb\number 
   If press<0 box\visible=0 
  End If 
 Else 
  Rect  x, y, w2+10, StringHeight("W"), 1 
  Color box\r1, box\g1, box\b1 
 End If 
   Text x+5+w2 Shr 1, y, bb\content$, 1, 0 
 y=y+3*StringHeight("W") Shr 1 
 End If 
Next 
Next 
If MouseDown(1) Enable_press_dialogBox_=0 Else Enable_press_dialogBox_=1 
Return press 
End Function 
 
Function ClearStrings(box.TDialog) 
For a.TString = Each TString 
If a\ID = box\ID Delete a 
Next 
box\StrCount=0 
box\maxWidth1=0 
End Function 
 
Function ClearButtons(box.TDialog) 
For b.TButton = Each TButton 
If b\ID = box\ID Delete b 
Next 
box\BtnCount=0 
box\maxWidth2=0 
End Function 
 
Function ShowDialog(box.TDialog) 
box\visible=1 
End Function 
 
Function HideDialog(box.TDialog) 
box\visible=0 
End Function 
 
Function MoveDialog(box.TDialog, dx, dy) 
box\x=dx+box\x:box\y=dy+box\y 
End Function 
 
Function PositionDialog(box.TDialog, NewX, NewY) 
box\x=NewX:box\y=NewY 
End Function