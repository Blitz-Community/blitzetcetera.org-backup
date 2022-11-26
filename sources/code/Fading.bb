;,,,,
    ;   Contents
;''''
;
;   <1>    Overview
;   <2.1>  Fade system functions
;   <2.2>  Fade system usage example
;   <3>    Supplemental functions
;   <4.1>  Demo overview
;   <4.2>  Demo 1: Simple fading
;   <4.3>  Demo 2: Single buffered fading
;   <4.4>  Demo 3: Double buffered fading
;   <4.5>  Demo 4: Manual fading
;   <4.6>  Demo 5: Setting brightness
;   <4.7>  Demo 6: Colorizing
;   <5>    Fade system code
;
;,,,,
    ;   <1>  Overview
;''''
;
;   This independent gamma system encapsulates the Blitz functions
;   SetGamma and UpdateGamma enabling you to:
;
;     * automatically fade the screen in/out,
;     * accurately control gamma manually:
;       * lightness; making the screen relatively brighter
;         which is also refered to as 'Gamma correction' in games,
;       * darkness; making the screen relatively darker
;         basically controlling the fullbright level or 'fullbrightness'.
;
;   This is currently (Jan 2004) the fastest way of doing fullscreen fading
;   using the 2D commandset in Blitz. The only limitation is that Blitz'
;   gamma functions only work in fullscreen.
;
;   TheChance <TheChange@yahoo.com>
;
;,,,,
    ;   <2.1>  Fade system functions
;''''
;
;   Fade_Init                           ; Mandatory initialization procedure.
;   Fade_Exit                           ; Optional clean up procedure.
;
;   Fade_Set    ( Mode , Duration )     ; Start fade (1=In, 0=Out).
;   Fade_In     ( Duration )            ; Start fade in.
;   Fade_Out    ( Duration )            ; Start fade out.
;
;   Notes:  Duration defines total duration of fade in milliseconds.
;           Which is 5,000 by default (5 seconds).
;           Specifying a duration of 0 will cause an instant fade.
;
;   Fade_Done%  ()                      ; Returns True when fade is done.
;   Fade_Update                         ; Updates the fade.
;
;   Notes:  The smoothness of the Fade depends on the DefaultFadeDelay value
;           which is set to 50ms by default. Decreasing this value will make
;           the Fade smoother but put more strain on your system. Increasing
;           this value will make the Fade jerk but relieve strain on Blitz.
;
;   Examples with parameters:
;
;       Fade_Set ( 1 , 1000 )  ; Fade in screen in 1 second,
;                              ; which is the equivalent of Fade_In ( 1000 ).
;
;       Fade_Set ( 0 , 2500 )  ; Fade screen out in 2.5 seconds,
;                              ; which is the equivalent of Fade_Out ( 2500 ).
;
;,,,,
    ;   <2.2>  Fade system usage example
;''''
;
;   Graphics 640 , 480 , 0 , 1  ; <- Has to be fullscreen.
;
;   Initialize ( game elements )
;
;   Fade_Init  ; Mandatory.
;
;   SetBuffer BackBuffer ()
;   Repeat
;
;       ; Logic
;
;       Update ( game elements )
;
;       If ( game requires fade out ) Then Fade_Out duration
;       If ( game requires fade in  ) Then Fade_In  duration
;       If ( game requires any fade ) Then Fade_Set mode , duration
;
;       Fade_Update
;
;       ; Visual
;
;       Display ( game elements )
;
;       Flip
;
;   Until ( game finished )
;
;   Fade_Exit  ; Optional.
;   End
;
;,,,,
    ;   <3>  Supplemental functions
;''''
;
;   Gamma_Set   ( Intensity# , [ Red# ] , [ Green# ] , [ Blue# ] )
;
;       Easy, fast, flexible, accurate and instant gamma control.
;
;       Intensity#
;           Gamma level [0..255]
;               0 = darkest    (black)
;               1 = fullbright (normal)
;             255 = brightest  (white)
;
;       Red#, Green#, Blue#
;           Color level [0..1]
;               0 = black
;               1 = fullbright
;
;   Examples:
;
;       Gamma_Set ( 1 )                 ; Set normal brightness (fullbright).
;       Gamma_Set ( 0.5 )               ; Set half brightness.
;       Gamma_Set ( 0 )                 ; Black screen :P
;       Gamma_Set ( 255 )               ; White screen :P

;       Gamma_Set ( 1 , 0 , 0 , 1 )     ; Colorize the screen blue.
;       Gamma_Set ( 1 , 1 , 0.5 , 0 )   ; Colorize the screen orange.
;       Gamma_Set ( 128 , 0 , 1 , 0 )   ; Superbright green screen.
;
;,,,,
    ;   <4.1>  Demo overview
;''''
;
;   Included are some demos to display all aspects of the system.
;
;   Demo 1. Shows most direct usage of the fade system,
;           without buffering or screen redraws,
;           using the Fade_Set command.
;
;   Demo 2. Displays the same as the first demo,
;           but with singularly buffered screen redraws.
;
;   Demo 3. Makes use of double buffering and screen redraws,
;           using the fade system through the Fade_In and Fade_Out commands.
;
;   Demo 4. Displays how you can fade the screen manually using Gamma_Set ().
;   Demo 5. Sets lightness and darkness levels using the Gamma_Set command.
;   Demo 6. Shows how to colorize the screen using Gamma_Set ().
;
;   Each demo is labelled (.Demo1, .Demo2, etc)
;   so you can try each one out right here, right now, using a Goto :)

  Goto Demo1

;,,,,
    ;   <4.2>  Demo 1: Simple fading
;''''
;
;   Displays:
;     * how to fade in/out using Fade_Set,
;     * you can use the system without having to redrawing the screen,
;       changes are instant! :)
;
.Demo1

    ; Draw box with color gradient; horizontally: colors, vertically: intensity (black->normal)
    Function gradbox_black(x1,y1,x2,y2,r1,g1,b1,r2,g2,b2)
        incr#=(r2-r1)/(x2-x1)  ; scale pixel range to color range
        incg#=(g2-g1)/(x2-x1)
        incb#=(b2-b1)/(x2-x1)

        For x=x1 To x2
            For y=y1 To y2
                intensity = (y-y1)*255/(y2-y1)  ; scale pixel range to color brightness
                r = intensity * r1 / 255  ; scale intensity to color range
                g = intensity * g1 / 255
                b = intensity * b1 / 255
                Color r,g,b
                Plot x , y
            Next
            r1=r1+incr
            g1=g1+incg
            b1=b1+incb
        Next
    End Function

    ; Draw box with color gradient; horizontally: colors, vertically: intensity (normal->white)
    Function gradbox_white(x1,y1,x2,y2,r1,g1,b1,r2,g2,b2)
        incr#=(r2-r1)/(x2-x1)  ; scale pixel range to color range
        incg#=(g2-g1)/(x2-x1)
        incb#=(b2-b1)/(x2-x1)

        For x=x1 To x2
            For y=y1 To y2
                intensity = (y2-y)*255/(y2-y1)  ; scale inversed pixel range to color brightness
                r = 255-intensity * (255-r1) / 255  ; scale inversed intensity to inversed color
                g = 255-intensity * (255-g1) / 255
                b = 255-intensity * (255-b1) / 255
                Color r,g,b
                Plot x , y
            Next
            r1=r1+incr
            g1=g1+incg
            b1=b1+incb
        Next
    End Function

    AppTitle "Simple fading"
    SeedRnd MilliSecs ()

    Graphics 640 , 480 , 0 , 1  ; <- Has to be fullscreen.
    SetFont LoadFont ( "Arial" , 11 , True )  ; Instantly load and set font.

    Fade_Init  ; Initialize fade system.
    Screen_IsVisible = True  ; Using a flag to keep track of fade.

    SetBuffer FrontBuffer ()  ; Note: Works with double buffering too.

    ; Draw some stuff on the screen, some nice stuff :)
    gradbox_black   0,  0, 50, 50 , 255,  0,  0 , 255,255,  0
    gradbox_black  50,  0,100, 50 , 255,255,  0 ,   0,255,  0
    gradbox_black 100,  0,150, 50 ,   0,255,  0 ,   0,255,255
    gradbox_black 150,  0,200, 50 ,   0,255,255 ,   0,  0,255
    gradbox_black 200,  0,250, 50 ,   0,  0,255 , 255,  0,255
    gradbox_black 250,  0,300, 50 , 255,  0,255 , 255,  0,  0

    ; This is actually very much like PSP's color table.
    gradbox_white   0, 50, 50,100 , 255,  0,  0 , 255,255,  0
    gradbox_white  50, 50,100,100 , 255,255,  0 ,   0,255,  0
    gradbox_white 100, 50,150,100 ,   0,255,  0 ,   0,255,255
    gradbox_white 150, 50,200,100 ,   0,255,255 ,   0,  0,255
    gradbox_white 200, 50,250,100 ,   0,  0,255 , 255,  0,255
    gradbox_white 250, 50,300,100 , 255,  0,255 , 255,  0,  0

    ; Finishing with a message.
    Color 255 , 255 , 255
    Text 0 ,  200 , "Press <space> to toggle fade in/out"

    ; The screen will not be redrawn.
    Repeat

        If KeyHit ( 57 )
            ; Toggle visibility.
            Screen_IsVisible = Not Screen_IsVisible
            ; And fade towards the new setting.
            Fade_Set Screen_IsVisible , 1000  ; Fade in 1 second.
        End If

        ; Updating the fade constantly for a smooth fade.
        Fade_Update

    ; Press ESC to quit.
    Until KeyHit ( 1 )
End

;,,,,
    ;   <4.3>  Demo 2: Single buffered fading
;''''
;
;   Displays:
;     * how to fade in/out using Fade_Set,
;     * fading while redrawing the screen using single buffering.
;
.Demo2

    AppTitle "Single buffered fading"
    SeedRnd MilliSecs ()

    Screen_SizeX = 640
    Screen_SizeY = 480

    Graphics Screen_SizeX , Screen_SizeY , 0 , 1
    SetFont LoadFont ( "Arial" , 11 , True )

    Fade_Init
    Screen_IsVisible = True

    SetBuffer FrontBuffer ()  ; Note: Works with double buffering too.
    Repeat

        ;-- Logic.

        If KeyHit ( 57 )
            Screen_IsVisible = Not Screen_IsVisible
            Fade_Set Screen_IsVisible
        End If

        Fade_Update

        ;-- Visual.

        ; Add random square to the screen.
        Color Rand ( 0 , 255 ) , Rand ( 0 , 255 ) , Rand ( 0 , 255 )
        Rect Rand ( -Screen_SizeX , Screen_SizeX*2-1 ) , Rand ( -Screen_SizeY , Screen_SizeY*2-1 ) , Rand ( Screen_SizeX ) , Rand ( Screen_SizeY ) , True

        ; Info and status messages.
        Color 0 , 0 , 0
        Rect 0 , 0 , Screen_SizeX/2 , 10 , True
        Color 255 , 255 , 255
        Text 0 ,  0 , "Press <space> to toggle fade in/out - Fade done: " + Mid ( "FalseTrue" , Fade_Done () * 5 + 1 , 5 )

        ; Using single buffering.
        VWait  ; Not flipping, just waiting

    Until KeyHit ( 1 )
End

;,,,,
    ;   <4.4>  Demo 3: Double buffered fading
;''''
;
;   Displays:
;     * how to fade in/out using Fade_In and Fade_Out,
;     * fading while redrawing the screen using double buffering.
;
.Demo3

    ; Uses an object collection of thingies to put on the screen.
    Type Thingy
        Field PosX#
        Field PosY#
        Field SpeedX#
        Field SpeedY#
        Field Color  ; 0..16777215 combined RGB value.
        Field SizeX
        Field SizeY
        Field Shape  ; 0 for Oval, 1 for Square.
    End Type

    AppTitle "Double buffered fading"
    SeedRnd MilliSecs ()

    Screen_SizeX = 640
    Screen_SizeY = 480

    Graphics Screen_SizeX , Screen_SizeY , 0 , 1

    Fade_Init
    Screen_IsVisible = True

    ; Make some thingies to be moved around the screen.
    For Count = 1 To 100
        Pointer.Thingy = New Thingy
        Pointer\PosX = Rand ( 0 , Screen_SizeX-1 )
        Pointer\PosY = Rand ( 0 , Screen_SizeY-1 )
        Pointer\SpeedX = Rnd ( -1 , 1 )
        Pointer\SpeedY = Rnd ( -1 , 1 )
        Pointer\Color = Rand ( 0 , 16777215 )
        Pointer\SizeX = Rand ( 1 , Screen_SizeX/10 )
        Pointer\SizeY = Rand ( 1 , Screen_SizeY/10 )
        Pointer\Shape = Rand ( 0 , 1 )
    Next

    SetBuffer BackBuffer ()  ; Using double buffering.
    Repeat

        ;-- Logic.

        ; Press space to fade in/out.
        If KeyHit ( 57 )
            If Screen_IsVisible
                Fade_Out 2000
            Else
                Fade_In 2000
            End If
            Screen_IsVisible = Not Screen_IsVisible
        End If

        ; Move thingies.
        For Pointer = Each Thingy
            Pointer\PosX = Pointer\PosX + Pointer\SpeedX
            Pointer\PosY = Pointer\PosY + Pointer\SpeedY
            If Pointer\PosX < -Pointer\SizeX Then Pointer\PosX = Pointer\PosX + Screen_SizeX + Pointer\SizeX
            If Pointer\PosY < -Pointer\SizeY Then Pointer\PosY = Pointer\PosY + Screen_SizeY + Pointer\SizeY
            If Pointer\PosX >=  Screen_SizeX Then Pointer\PosX = Pointer\PosX - Screen_SizeX - Pointer\SizeX
            If Pointer\PosY >=  Screen_SizeY Then Pointer\PosY = Pointer\PosY - Screen_SizeY - Pointer\SizeY
        Next

        ; Measure Frames Per Second.
        If MilliSecs () - FrameTime >= 1000
            FrameTime = MilliSecs ()
            FPS = Frames
            Frames = 0
        End If
        Frames = Frames + 1

        Fade_Update

        ;-- Visual

        ; Displaying the thingies.
        For Pointer = Each Thingy
            ; Fill shape with specified color.
            Color 0 , 0 , Pointer\Color
            If Pointer\Shape
                Rect Pointer\PosX , Pointer\PosY , Pointer\SizeX , Pointer\SizeY
            Else
                Oval Pointer\PosX , Pointer\PosY , Pointer\SizeX , Pointer\SizeY
            End If
            ; Outline shape with different (inverted) color.
            Color 0 , 0 , -Pointer\Color
            If Pointer\Shape
                Rect Pointer\PosX , Pointer\PosY , Pointer\SizeX , Pointer\SizeY , False
            Else
                Oval Pointer\PosX , Pointer\PosY , Pointer\SizeX , Pointer\SizeY , False
            End If
        Next

        Color 255 , 255 , 255
        Text 0 ,  0 , "Press <space> to toggle fade in/out"
        Text 0 , 10 , "Fade done: " + Mid ( "FalseTrue" , Fade_Done () * 5 + 1 , 5 )
        Text 0 , 20 , "FPS: " + FPS

        ; Double buffering so flip the screen.
        ; But not synchronizing with the monitor to see the actual FPS speed hit.
        Flip False
        Cls

    Until KeyHit ( 1 )
End

;,,,,
    ;   <4.5>  Demo 4: Manual fading
;''''
;
;   Displays how you can manually set the darkness of the screen
;   feeding the result of a calculation using a percentage
;   to the Gamma_Set function.
;
.Demo4

    AppTitle "Manual fading"
    SeedRnd MilliSecs ()

    Graphics 640 , 480 ,   0 , 1
    ClsColor 255 , 255 , 255  ; Fullbright background (kadang!).

    Value = 50  ; Percentage of 'fullbrightness', where 100% is normal brightness.

    SetBuffer BackBuffer ()
    Repeat

        ; Use left/right to adjust fullbright percentage.
        If KeyDown ( 203 ) Then Value = Value - 1
        If KeyDown ( 205 ) Then Value = Value + 1
        ; Limit percentage to (0..100).
        If Value <    0 Then Value =   0
        If Value >= 100 Then Value = 100

        ; Display percentage info.
        Color 255 , 0 , 0
        Rect 0 , 0 , 100 , 40
        Color   0 , 0 , 0
        Rect 0 , 0 , 100 , 40 , False
        Line Value , 0 , Value , 39
        Text 50 , 20 , Value + "%" , True , True

        ; Adjust fullbright gamma, in range of (0..1).
        Gamma# = Float Value / 100
        Gamma_Set Gamma

        Flip
        Cls

    Until KeyHit ( 1 )
End

;,,,,
    ;   <4.6>  Demo 5: Setting brightness
;''''
;
;   Displays how to set both darkness and lightness (gamma correction/'fullbrightness')
;   of the screen using a gamma value directly passed to the Gamma_Set function.
;
.Demo5

    AppTitle "Setting brightness"
    SeedRnd MilliSecs ()

    Screen_SizeX = 640
    Screen_SizeY = 480

    Graphics Screen_SizeX , Screen_SizeY , 0 , 1
    SetFont LoadFont ( "Arial" , 25 , True )

    ; Create a background of grey vertical bars.
    Background = CreateImage ( 640 , 480 )
    SetBuffer ImageBuffer ( Background )
    For Color = 0 To 255
        Color Color , Color , Color
        Offset = Color * Screen_SizeX / 255  ; Scale color range to horizontal screen dimension.
        Rect Offset , 0 , 640-Offset , 480 , True
    Next

    Gamma# = 1

    SetBuffer BackBuffer ()
    Repeat

        ; Draw the grey bars.
        DrawBlock Background , 0 , 0

        ; Use Left/Right to exponentially increase/decrease gamma level.
        If KeyDown ( 203 ) Then Gamma = Gamma / 1.1
        If KeyDown ( 205 ) Then Gamma = Gamma * 1.1
        ; Keep gamma in manageable ranges.
        If Gamma >  255 Then Gamma = 255
        If Gamma <=   0 Then Gamma =   0.0001

        ; Display current gamma level.
        Color 0 , 0 , 0
        Text Screen_SizeX/2 , Screen_SizeY/2 , "Gamma: " + Gamma , True , True

        ; Set current gamma level.
        Gamma_Set Gamma

        Flip
        Cls

    Until KeyHit ( 1 )
End

;,,,,
    ;   <4.7>  Demo 6: Colorizing
;''''
;
;   Displays how to colorize the screen using Gamma_Set.
;
.Demo6

    AppTitle "Colorizing"
    SeedRnd MilliSecs ()

    Screen_SizeX = 640
    Screen_SizeY = 480

    ChangeColor_Delay = 1000  ; Change colors each second.

    Graphics Screen_SizeX , Screen_SizeY , 0 , 1
    Fade_Init

    SetBuffer FrontBuffer ()
    Repeat

        ;-- Logic.

        ; Change random gamma color at a regular interval.
        If MilliSecs () - ChangeColor_Time >= ChangeColor_Delay
            ChangeColor_Time = MilliSecs ()

            ; Brightness 1 (fullbright) with random colors (red, green, blue).
            Gamma_Set 1 , Rnd ( 0 , 1 ) , Rnd ( 0 , 1 ) , Rnd ( 0 , 1 )
        End If

        ;-- Visual.

        ; Draw random boxes.
        Color Rand ( 0 , 255 ) , Rand ( 0 , 255 ) , Rand ( 0 , 255 )
        Rect Rand ( -Screen_SizeX , Screen_SizeX*2-1 ) , Rand ( -Screen_SizeY , Screen_SizeY*2-1 ) , Rand ( Screen_SizeX/10 ) , Rand ( Screen_SizeY/10 ) , True

    Until KeyHit ( 1 )
End

;,,,,
    ;   <5>  Fade system
;''''

; Constants.

    Const GammaFade_DefaultDuration  = 5000  ; Ms.
    Const GammaFade_DefaultFadeDelay = 50    ; Ms.

; Structures.

    Type GammaFade_Base
        Field Done        ; Fading runs if True.
        Field Started     ; When fade was started.
        Field Duration    ; Time for entire fade to take.
        Field Direction   ; Fade direction (In = 1, Out = 0).
        Field LastFade    ; Time of previous fade update.
        Field FadeDelay   ; Time between each fade update.
    End Type

; Root structure.

    Global GammaFade_.GammaFade_Base

; Methods.

    ; Reset fade system.
    Function Fade_Init ()
        If GammaFade_ = Null Then GammaFade_ = New GammaFade_Base
        GammaFade_\Done = True
        GammaFade_\Duration = GammaFade_DefaultDuration
        GammaFade_\FadeDelay = GammaFade_DefaultFadeDelay
    End Function

    ; Clean up remains.
    Function Fade_Exit ()
        Delete GammaFade_
    End Function

    ; Initiate fade.
    Function Fade_Set ( Fade_Mode , Fade_Duration = GammaFade_DefaultDuration )
        Local Level

        ; Instant fade.
        If Fade_Duration = 0
            ; Warp to final destination.
            If Fade_Mode
                For Level = 0 To 255
                    SetGamma Level , Level , Level , Level , Level , Level
                Next
            Else
                For Level = 0 To 255
                    SetGamma Level , Level , Level , 0 , 0 , 0
                Next
            End If
            UpdateGamma
            ; Fade done.
            GammaFade_\Done = True
        ; Gradual fade.
        Else
            ; Remember gradual fade direction (in/out).
            GammaFade_\Direction = Fade_Mode
            ; Remember duration of fade.
            GammaFade_\Duration = Fade_Duration
            ; Remember current time (start time).
            GammaFade_\Started = MilliSecs ()
            ; Run fade.
            GammaFade_\Done = False
        End If
    End Function

    ; Initiate fade in.
    Function Fade_In ( Duration = GammaFade_DefaultDuration )
        Fade_Set True , Duration
    End Function

    ; Initiate fade out.
    Function Fade_Out ( Duration = GammaFade_DefaultDuration )
        Fade_Set False , Duration
    End Function

    ; Check if fade is done.
    Function Fade_Done% ()
        Return GammaFade_\Done
    End Function

    ; Update the fade system.
    Function Fade_Update ()
        Local Time_Elapsed
        Local Gamma_Level
        Local Target_Level
        Local MilliSecs

        ; Still fading.
        If Not GammaFade_\Done
            ; Check the time.
            MilliSecs = MilliSecs ()
            ; Time to update the fade.
            If MilliSecs - GammaFade_\LastFade >= GammaFade_\FadeDelay
                ; Remember the time.
                GammaFade_\LastFade = MilliSecs
                ; Time since start.
                Time_Elapsed = MilliSecs - GammaFade_\Started
                ; Fade's duration has elapsed.
                If Time_Elapsed >= GammaFade_\Duration
                    ; Finish gamma table and clean up.
                    Fade_Set GammaFade_\Direction , 0
                ; Still fading.
                Else
                    ; Set gamma levels depending on fade direction.
                    If GammaFade_\Direction
                        For Gamma_Level = 0 To 255
                            Target_Level = Time_Elapsed * Gamma_Level / GammaFade_\Duration
                            SetGamma Gamma_Level , Gamma_Level , Gamma_Level , Target_Level , Target_Level , Target_Level
                        Next
                    Else
                        For Gamma_Level = 0 To 255
                            Target_Level = Gamma_Level - Time_Elapsed * Gamma_Level / GammaFade_\Duration
                            SetGamma Gamma_Level , Gamma_Level , Gamma_Level , Target_Level , Target_Level , Target_Level
                        Next
                    End If
                    UpdateGamma
                End If
            End If
        End If
    End Function

    ; (Independent) Gamma control.
    Function Gamma_Set ( Brightness# = 1 , Red# = 1 , Green# = 1 , Blue# = 1 )
        Local Gamma_Level
        Local Target_Level

        For Gamma_Level = 0 To 255
            ; Use accurate exponential ranges (0..1, 1..255).
            If Brightness > 1
                Target_Level = (Gamma_Level+1) * (Brightness/(Gamma_Level+1)+1) - 1
            Else
                Target_Level = Gamma_Level * Brightness
            End If
            ; Ensure compatibility with BlitzPlus.
            If Target_Level <   0 Then Target_Level =   0
            If Target_Level > 255 Then Target_Level = 255
            ; Apply changes.
            SetGamma Gamma_Level , Gamma_Level , Gamma_Level , Target_Level * Red , Target_Level * Green , Target_Level * Blue
        Next
        UpdateGamma
    End Function

;-->