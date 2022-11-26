AppTitle "Blitz2D Dirt Drawing Program"
Global ScreenW = 800
Global ScreenH = 600
Global dirt3 = $00FF00
Global dirt2 = $00CC00
Global dirt1 = $00AA00

Dim Landscape(ScreenW,1)
Dim DirtArray(ScreenW,1)

Type Dirt
     Field X
     Field Y
     Field Colour
End Type

Graphics ScreenW,ScreenH
SetBuffer BackBuffer()

CreateDirt(75,100)
SetDirt(1)

While Not KeyHit(1)

Cls

DrawDirt()

Flip

Wend

; End of Game

; ------------------ FUNCTIONS ----------------------
;
; Thanks To Andreas Blixt For the basic functions that make up CreateDirt() And SetDirt()
;
Function CreateDirt(Horizontal,Vertical)
     SeedRnd MilliSecs()
     For Loop = 1 To ScreenW
          Landscape(Loop,0) = Landscape(Loop - 1,0) + Rand(0,Horizontal) + 10
          Landscape(Loop,1) = Landscape(Loop - 1,1) + Rand(-Vertical / 2,Vertical / 2)
          If Landscape(Loop,1) < -200 Landscape(Loop,1) = -190
          If Landscape(Loop,1) > 200 Landscape(Loop,1) = 190
     Next
End Function

Function SetDirt(XOffset)
     LandscapeX = 0
     While LandscapeX <= ScreenW
          XOffset = XOffset + 1
          YOffset = (Landscape(XOffset,1) + Landscape(XOffset + 1,1)) / 2
          Length =  Landscape(XOffset,0) - Landscape(XOffset - 1,0)
          Height =  (Landscape(XOffset,1) - Landscape(XOffset + 1,1)) / 2
          
               For Progress = 0 To Length - 1
                    Angle# =  (180.0 / Length) * Progress
                    LandscapeX = LandscapeX + 1
                    LandscapeY = (Cos(Angle) * Height + YOffset) + 200
                    If LandscapeY < 100 LandscapeY=100
                    If LandscapeY > 450 LandscapeY=450
                    DirtArray(LandscapeX,0) = LandscapeY
               Next
     Wend

     For X = 1 To ScreenW
          For Y = DirtArray(X,0) To (DirtArray(X,0) + 20)
               ground.dirt = New dirt
               ground\X = X
               ground\Y = Y
               ground\Colour = dirt3
          Next
          For Y = (DirtArray(X,0) + 21) To (DirtArray(X,0) + 40)
               ground.dirt = New dirt
               ground\X = X
               ground\Y = Y
               ground\Colour = dirt2
          Next
          For Y = (DirtArray(X,0) + 41) To 600
               ground.dirt = New dirt
               ground\X = X
               ground\Y = Y
               ground\Colour = dirt1
          Next
     Next
End Function

Function DrawDirt()
     For ground.dirt = Each dirt
          WritePixel ground\X,ground\Y,ground\Colour
     Next
End Function

Function Cleanup()
     Delete Each dirt
     Cls
End Function