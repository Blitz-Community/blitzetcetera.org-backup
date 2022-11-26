; This game tech demo is a Destruct clone.
; Destruct is the minigame hidden inside another game called Tyrian.
; This game was created to see how hard (or easy) it was to recreate Destruct.
; Turn-based variations: Target2, Scorched Earth, Worms.
; Real-time variations: Liero, Soldat.

     Const Graphics_SizeX = 1024
     Const Graphics_SizeY = 768
     Const Graphics_MidX = Graphics_SizeX / 2
     Const Graphics_MidY = Graphics_SizeY / 2
     
     Graphics Graphics_SizeX , Graphics_SizeY
     
     ;********************************************************************************;
     ;**
     ;**     Font
     ;**
     ;********************************************************************************;
     
     Global Font = LoadFont ( "Tahoma" , 16 , True )
     SetFont Font
     
     Color 34 , 164 , 120
     Print
     Print "  Tank (Destruct)"
     Print "  Version 06 January 2003"
     Print "  Created by TheChange"
     Print
     Print "  Loading..."
     
     ;********************************************************************************;
     ;**
     ;**     Random
     ;**
     ;********************************************************************************;
     
     Const Random_Entries = 1000
     
     Dim Random_Table# ( 0 )
     Function Random_Create ( Seed )
          SeedRnd Seed
          Dim Random_Table ( Random_Entries )
          For Entry = 0 To Random_Entries
               Random_Table ( Entry ) = Rnd ( -1 , 1 )
          Next
     End Function
     
     Function Random# ()
          Random_Entry = Random_Entry + 1
          If Random_Entry > Random_Entries
               Random_Entry = 0
          End If
          Return Random_Table ( Random_Entry )
     End Function
     
     
     Global Random_Entry = 0
     
     Random_Create MilliSecs ()
     
     ;********************************************************************************;
     ;**
     ;**     Tank
     ;**
     ;********************************************************************************;
     
     Const Image_Tank_SizeX = 64
     Const Image_Tank_SizeY = 32
     Const Image_Tank_HandleX = Image_Tank_SizeX / 2 - 1
     Const Image_Tank_HandleY = Image_Tank_SizeY * 2 / 3 - 1
     
     Const Tank_Turret_Size = 50
     Const Tank_Turret_TurnSpeed# = 0.5
     
     Const Tank_Min_Velocity = 2
     Const Tank_Max_Velocity = 15
     Const Tank_Velocity_Increment# = 0.1
     Const Tank_Velocity_Min_DrawDistance = 60
     Const Tank_Velocity_Max_DrawDistance = 100
     
     Const Tank_DamageLevel = 100
     
     Type Tank
          Field Controller
          Field PosX
          Field PosY
          Field Angle#     ; Turret (Projectile)
          Field Velocity#     ; Turret (Projectile)
          Field FireTime
          Field Damage
     End Type
     
     Function Tank_Create ( Controller , PosX , PosY )
          Tank.Tank = New Tank
          Tank\Controller = Controller
          Tank\PosX = PosX
          Tank\PosY = PosY
          Tank\Angle = 0     ; Up
          Tank\Velocity = ( Tank_Min_Velocity + Tank_Max_Velocity ) / 2
          Tank\Damage = Tank_DamageLevel
     End Function
     
     Function Tank_Destroy ( Tank.Tank )
          Delete Tank
     End Function
     
     Function Tank_Control ()
          For Tank.Tank = Each Tank
               Select Tank\Controller
                    ;= Keyboard
                    Case 0
                         ;= Turret Angle
                         If KeyDown ( KeyCursorLeft )
                              If ( Tank\Angle >= 270 + Tank_Turret_TurnSpeed ) Or ( Tank\Angle <= 90 + Tank_Turret_TurnSpeed )
                                   Tank\Angle = Tank\Angle - Tank_Turret_TurnSpeed
                                   If Tank\Angle < 0
                                        Tank\Angle = Tank\Angle + 360
                                   End If
                              End If
                         End If
                         If KeyDown ( KeyCursorRight )
                              If Tank\Angle <= 90 - Tank_Turret_TurnSpeed Or Tank\Angle >= 270 - Tank_Turret_TurnSpeed
                                   Tank\Angle = Tank\Angle + Tank_Turret_TurnSpeed
                                   If Tank\Angle >= 360
                                        Tank\Angle = Tank\Angle - 360
                                   End If
                              End If
                         End If
                         ;= Turret (Projectile) Velocity
                         If KeyDown ( KeyCursorUp )
                              If Tank\Velocity <= Tank_Max_Velocity - Tank_Velocity_Increment
                                   Tank\Velocity = Tank\Velocity + Tank_Velocity_Increment
                              End If
                         End If
                         If KeyDown ( KeyCursorDown )
                              If Tank\Velocity >= Tank_Min_Velocity + Tank_Velocity_Increment
                                   Tank\Velocity = Tank\Velocity - Tank_Velocity_Increment
                              End If
                         End If
                         ;= Projectile
                         If KeyDown ( KeyRightCtrl)
                              Tank_FireProjectile Tank
                         End If
               End Select
          Next
          FlushKeys ()
     End Function
     
     Function Tank_FireProjectile ( Tank.Tank , Model = 0 )
          If MilliSecs () >= Tank\FireTime + Projectile_ReloadTime
               ;= Fire projectile from end of turret barrel
               TurretX = GetVectorX ( Tank_Turret_Size , Tank\Angle )
               TurretY = GetVectorY ( Tank_Turret_Size , Tank\Angle )
               Projectile_Create Tank\PosX + TurretX , Tank\PosY + TurretY , Tank\Angle , Tank\Velocity , Model
               Tank\FireTime = MilliSecs ()
          End If
     End Function
     
     Function Tank_Gravity ()
          For Tank.Tank = Each Tank
               For Gravity_Count = 1 To 10
                    If Tank\PosY < Graphics_SizeY - ( Image_Tank_SizeY - Image_Tank_HandleY )
                         If Not ImagesCollide ( Image_Tank , Tank\PosX , Tank\PosY + 1 , 0 , Image_Scenery , 0 , 0 , 0 )
                              Tank\PosY = Tank\PosY + 1
                         End If
                    End If
               Next
          Next
     End Function
     
     Function Tank_Damage ( Tank.Tank , Damage )
          Tank\Damage = Tank\Damage - Damage
          If Tank\Damage <= 0
               ; Explosion_Create Tank\PosX , Tank\PosY , <Type> (random)
               Tank_Destroy Tank
          End If
     End Function
     
     Function Tank_Display ()
          For Tank.Tank = Each Tank
               Tank_DisplayTurret Tank\PosX , Tank\PosY , Tank\Angle , Tank\Velocity
               DrawImage Image_Tank , Tank\PosX , Tank\PosY
          Next
     End Function
     
     Function Tank_DisplayTurret ( PosX , PosY , Angle , Velocity# )
          ;== Turret
               ;= Calculate turret tail position
               TurretX = GetVectorX ( Tank_Turret_Size , Angle )
               TurretY = GetVectorY ( Tank_Turret_Size , Angle )
               ;= Draw turret
               Color 0 , 112 , 0
               Line PosX , PosY , PosX + TurretX , PosY + TurretY
          ;== Velocity indicator
               ;= Convert velocity units to draw distance units
               Velocity_Range# = Tank_Max_Velocity - Tank_Min_Velocity
               DrawDistance_Range# = Tank_Velocity_Max_DrawDistance - Tank_Velocity_Min_DrawDistance
               Velocity = Velocity - Tank_Min_Velocity     ; Overloaded
               DrawDistance# = Velocity * DrawDistance_Range / Velocity_Range + Tank_Velocity_Min_DrawDistance
               ;= Calculate indicator position
               IndicatorX = GetVectorX ( DrawDistance , Angle )
               IndicatorY = GetVectorY ( DrawDistance , Angle )
               ;= Draw indicator
               Color 225 , 216 , 175
               Plot PosX + IndicatorX , PosY + IndicatorY
     End Function
     
     Function Tank_HUD ()
          For Tank.Tank = Each Tank
               Select Tank\Controller
                    Case 0
                         Damage_SizeX = 100
                         Damage_SizeY = 20
                         Damage_PosX = Tank\Damage * Damage_SizeX / Tank_DamageLevel
                         Color 0 , 96 , 0
                         Rect 0 , 0 , Damage_SizeX + 4 , Damage_SizeY + 4 , False
                         Color 0 , 128 , 0
                         Rect 2 , 2 , Damage_PosX , Damage_SizeY , True
                         Color 0 , 255 , 0
                         Text Damage_SizeX / 2 , Damage_SizeY / 2 , Tank\Damage , True , True
               End Select
          Next
     End Function
     
     
     Global Image_Tank = CreateImage ( Image_Tank_SizeX , Image_Tank_SizeY )
     SetBuffer ImageBuffer ( Image_Tank )
     Color 0 , 64 , 0
     Oval 0 , 0 , Image_Tank_SizeX , Image_Tank_SizeY * 2 , True
     Color 0 , 96 , 0
     Oval 2 , 2 , Image_Tank_SizeX - 4 , Image_Tank_SizeY * 2 - 4 , True
     HandleImage Image_Tank , Image_Tank_HandleX , Image_Tank_HandleY
     
     Tank_Create 0 , Graphics_MidX , 0
     
     ;********************************************************************************;
     ;**
     ;**     Projectile
     ;**
     ;********************************************************************************;
     
     Const Image_Projectile_SizeX = 8
     Const Image_Projectile_SizeY = 8
     Const Image_Projectile_MidX = Image_Projectile_SizeX / 2
     Const Image_Projectile_MidY = Image_Projectile_SizeY / 2
     
     Const Projectile_Name$ = "Tracer"
     Const Projectile_ReloadTime = 100     ; Ms
     Const Projectile_Gravity# = 0.1
     
     Type Projectile
          Field PosX#
          Field PosY#
          Field Angle#
          Field Velocity#
          Field Model
     End Type
     
     Function Projectile_Create ( PosX , PosY , Angle , Velocity# , Model = 0 )
          Proj.Projectile = New Projectile
          Proj\PosX = PosX
          Proj\PosY = PosY
          Proj\Angle = Angle
          Proj\Velocity = Velocity
          Proj\Model = Model
     End Function
     
     Function Projectile_Destroy ( Proj.Projectile )
          Delete Proj
     End Function
     
     Function Projectile_Update ()
          For Proj.Projectile = Each Projectile
               Select Proj\Model
                    Case 0
                         If ImagesCollide ( Image_Projectile , Proj\PosX , Proj\PosY , 0 , Image_Scenery , 0 , 0 , 0 )
                              ;= Destruct
                              SetBuffer ImageBuffer ( Image_Scenery )
                              Color 0 , 0 , 0
                              Oval Proj\PosX - Image_Projectile_MidX - 1 , Proj\PosY - Image_Projectile_MidY - 1 , Image_Projectile_SizeX + 2 , Image_Projectile_SizeY + 2 , True
                              SetBuffer BackBuffer ()
                              Projectile_Destroy Proj
                         Else
                              ;= Calculate projectile displacement vector
                              DisplacementX# = GetVectorX ( Proj\Velocity , Proj\Angle )
                              DisplacementY# = GetVectorY ( Proj\Velocity , Proj\Angle ) + Projectile_Gravity
                              ;= Update position
                              Proj\PosX = Proj\PosX + DisplacementX
                              Proj\PosY = Proj\PosY + DisplacementY
                              ;= Update velocity/angle
                              Proj\Velocity = GetLength ( DisplacementX , DisplacementY )
                              Proj\Angle = GetAngle ( DisplacementX , DisplacementY )
                              If Proj\PosX < -Image_Projectile_SizeX Or Proj\PosX >= Graphics_SizeX + Image_Projectile_SizeX
                                   ;= Offscreen (Left or Right)
                                   Projectile_Destroy Proj
                              ElseIf Proj\PosY >= Graphics_SizeY - Image_Projectile_MidY - 1
                                   ;= Bounce on floor
                                   Proj\PosY = Graphics_SizeY - Image_Projectile_MidY - 1
                                   BounceDisplacementY# = -DisplacementY / 1.25     ; 0.8 times the inversed Y speed
                                   If DisplacementX > -0.01 And DisplacementX < 0.01
                                        BounceDisplacementX# = DisplacementX + Random ()     ; Random bouncing
                                   Else
                                        BounceDisplacementX# = DisplacementX
                                   End If
                                   Proj\Velocity = GetLength ( BounceDisplacementX , BounceDisplacementY )
                                   Proj\Angle = GetAngle ( BounceDisplacementX , BounceDisplacementY )
                              End If
                         End If
               End Select
          Next
     End Function
     
     Function Projectile_Collision ()
          For Proj.Projectile = Each Projectile
               ;= With Tanks
               For Tank.Tank = Each Tank
                    If ImagesCollide ( Image_Tank , Tank\PosX , Tank\PosY , 0 , Image_Projectile , Proj\PosX , Proj\PosY , 0 )
                         Select Proj\Model
                              Case 0
                                   Tank_Damage Tank , 1
                         End Select
                         Projectile_Destroy Proj
                         Exit
                    End If
               Next
          Next
     End Function
     
     Function Projectile_Display ()
          For Proj.Projectile = Each Projectile
               Select Proj\Model
                    Case 0
                         DrawImage Image_Projectile , Proj\PosX , Proj\PosY
               End Select
          Next
     End Function
     
     
     Global Image_Projectile = CreateImage ( Image_Projectile_SizeX , Image_Projectile_SizeY )
     SetBuffer ImageBuffer ( Image_Projectile )
     Color 200 , 100 , 60
     Oval 0 , 0 , Image_Projectile_SizeX , Image_Projectile_SizeY , True
     Color 226 , 148 , 78
     Oval 1 , 1 , Image_Projectile_SizeX - 2 , Image_Projectile_SizeY - 2 , True
     MidHandle Image_Projectile
     
     ;********************************************************************************;
     ;**
     ;**     Scenery
     ;**
     ;********************************************************************************;
     
     Function Scenery_Display ()
          DrawImage Image_Scenery , 0 , 0
     End Function
     
     
     Global Image_Scenery = CreateImage ( Graphics_SizeX , Graphics_SizeY )
     SetBuffer ImageBuffer ( Image_Scenery )
     Color 182 , 177 , 116
     Rect 0 , Graphics_MidY * 5 / 3 , Graphics_SizeX , Graphics_MidY , True
     
     ; Scenery_Generate
     
     ;********************************************************************************;
     ;**
     ;**     Main
     ;**
     ;********************************************************************************;
     
     SetBuffer BackBuffer ()
     Repeat
          Tank_Control
          Tank_Gravity
          Projectile_Update
          Projectile_Collision
          
          Scenery_Display
          Projectile_Display
          Tank_Display
          Tank_HUD
          Flip
          Cls
     Until KeyHit ( 1 )
     
End

  ;-- Vector.BB

  Function GetVectorX# ( Distance# , Angle# )  ; Get horizontal size of vector using distance and angle
    Return Sin( Angle ) * Distance
  End Function

  Function GetVectorY# ( Distance# , Angle# )  ; Get vertical size of vector using distance and angle
    Return Sin( Angle - 90 ) * Distance
  End Function

  Function GetLength# ( X# , Y# )  ; Get true length of a vector
    Return Sqr ( X * X + Y * Y )
  End Function

  Function GetAngle# ( X# , Y# )  ; Get true angle of a vector
    Return -ATan2 ( X , Y ) + 180
  End Function

  ;-- Keyboard.BB

  ;-( Shiftstate )-----------------------------------------;

  Const KeyLeftShift%  =  42
  Const KeyRightShift% =  54
  Const KeyLeftAlt%    =  56
  Const KeyRightAlt%   = 184
  Const KeyLeftCtrl%   =  29
  Const KeyRightCtrl%  = 157
  Const KeyCapsLock%   =  58
  Const KeyNumLock%    =  69
  Const KeyScrollLock% =  70

  Const KeyLeftAlternate%  =  56
  Const KeyRightAlternate% = 184
  Const KeyLeftControl%    =  29
  Const KeyRightControl%   = 157

  ;-( Control keys )---------------------------------------;

  Const KeySpace%        =  57
  Const KeyEnter%        =  28
  Const KeyTab%          =  15
  Const KeyBackSpace%    =  14
  Const KeyInsert%       = 210
  Const KeyDelete%       = 211
  Const KeyHome%         = 199
  Const KeyEnd%          = 207
  Const KeyPageUp%       = 201
  Const KeyPageDown%     = 209
  Const KeyEscape%       =   1
  Const KeyPause%        = 197  ; Numlock
  Const KeyLeftWindows%  = 219
  Const KeyRightWindows% = 220
  Const KeyApps%         = 221
  Const KeyPower%        = 222
  Const KeySleep%        = 223
  Const KeyWake%         = 227

  ;-( Cursors )--------------------------------------------;

  Const KeyCursorUp%    = 200
  Const KeyCursorDown%  = 208
  Const KeyCursorLeft%  = 203
  Const KeyCursorRight% = 205
