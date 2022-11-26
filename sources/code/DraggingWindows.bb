;----------------------------------------------------------------------------------------------------------------------;
;
;   Dragging Windows - Multiple
;
;----------------------------------------------------------------------------------------------------------------------;
; Multiple (overlapping) rectangles to be moved by dragging them using the mouse.

    Graphics 640 , 480

    ;= Window specification
    Type Window
        Field PosX
        Field PosY
        Field SizeX
        Field SizeY
        Field Dragging
    End Type

    ;= Create 10 random windows
    For Count = 1 To 10
        Window.Window = New Window
        Window\PosX = Rand ( 0 , 500 )
        Window\PosY = Rand ( 0 , 400 )
        Window\SizeX = Rand ( 0 , 200 )
        Window\SizeY = Rand ( 0 , 100 )
        Window\Dragging = False
    Next

    ;= Any windows being dragged
    Dragging = False

    ;= Main loop
    SetBuffer BackBuffer ()
    Repeat

        ;= Draw mouse
        Plot MouseX () , MouseY ()

        ;= Draw windows
        For Window.Window = Each Window
            Rect Window\PosX , Window\PosY , Window\SizeX , Window\SizeY , False
        Next

        ;= Loop through all windows
        For Window.Window = Each Window

            ;= Mousebutton pressed and holding
            If MouseDown ( 1 )

                ;= No windows being dragged
                If Not Dragging

                    ;= Mouse overlaps any window
                    If RectsOverlap ( MouseX () , MouseY () , 1 , 1 , Window\PosX , Window\PosY , Window\SizeX , Window\SizeY )

                        ;= Window becomes dragged
                        Window\Dragging = True
                        ;= There's a window being dragged
                        Dragging = True

                    End If

                ;= Any windows being dragged
                Else

                    ;= Current window (from the collection) being dragged
                    If Window\Dragging

                        ;= Make window chase mouse position
                        Window\PosX = MouseX () - Window\SizeX / 2  ; Center window
                        Window\PosY = MouseY () - Window\SizeY / 2

                    End If

                End If  ; Dragging

            ;= Mousebutton released or not holding
            Else

                ;= Current window (from the collection) no longer being dragged
                Window\Dragging = False
                ;= There is no window being dragged
                Dragging = False

            End If  ; MouseDown

        Next  ; Window

        ;= Output to screen
        Flip
        Cls

    ;= Escape quits
    Until KeyHit ( 1 )
    End

;-->