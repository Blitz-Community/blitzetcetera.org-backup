Function TxtCol(col$)

Select col$

Case "white"
RED=225
GREEN=225
BLUE=225

Case "brilliant"
RED=255
GREEN=255
BLUE=255

Case "black"
RED=0
GREEN=0
BLUE=0

Case "silver"
RED=180
GREEN=180
BLUE=195

Case "red"
RED=225
GREEN=0
BLUE=0

Case "emerald"
RED=0
GREEN=225
BLUE=0

Case "blue"
RED=0
GREEN=0
BLUE=225

Case "yellow"
RED=225
GREEN=225
BLUE=0

Case "magenta"
RED=225
GREEN=0
BLUE=225

Case "orange"
RED=195
GREEN=170
BLUE=0

Case "brown"
RED=80
GREEN=95
BLUE=0

Case "cyan"
RED=0
GREEN=225
BLUE=225

Case "crimson"
RED=80
GREEN=0
BLUE=0

Case "navy"
RED=0
GREEN=0
BLUE=80

Case "green"
RED=0
GREEN=80
BLUE=0

Case "light grey"
RED=150
GREEN=150
BLUE=150

Case "dark grey"
RED=80
GREEN=80
BLUE=80

Default
RED=255
GREEN=255
BLUE=255

End Select

Color RED,GREEN,BLUE

End Function

;Just add more colours as necessary (to improve speed, try to keep most popular colours at the top of the 'Select' list.

Const color_red=$FF0000
Const color_green=$00FF00
Const color_blue=$0000FF
Const color_yellow=$FFFF00

Function Colour(rgb,g=-1,b=-1)
  If g=-1
    Color 0,0,rgb
  Else
    Color rgb,g,b 
  EndIf
End Function

Colour color_red
Text 10,10,"Red"

Colour color_green
Text 10,30,"Green"

Colour color_blue
Text 10,50,"Blue"

Colour color_yellow
Text 10,70,"Yellow"

Colour 255,255,255
Text 10,90,"White"

MouseWait 

