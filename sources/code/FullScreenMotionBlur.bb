;====== Full Screen Motion Blur =================================================================
;====== PROGRAMMED BY JEREMY ALESSI =============================================================
;====== Requires Rob's Sprite Functions =========================================================

;====== SPRITE FUNCTIONS IF YOU NEED THEM =======================================================
;====== IF NOT JUST COMMENT THEM OUT ============================================================
;fov is the same as your CameraZoom.
Function Sprite2D(sprite,x#,y#,fov#)
     PositionEntity sprite,2*(x#-320),-2*(y#-240),fov#*640
End Function

;scale sprite in screen pixels relative to a 640x480 res when used with Sprite2D

Function ScaleSprite2(sprite,x,y)
     ScaleEntity sprite,x,y,1
End Function
;please pass camera to this function Or 0 for a billboard Type with mesh.

Function CreateSprite2(parent)
     If parent<>0
          m=CreateMesh(parent)
     Else
          m=CreateMesh()
     EndIf
     s=CreateSurface(m)
     AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
     AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
     AddTriangle s,0,1,2:AddTriangle s,0,2,3
     ScaleEntity m,100,100,1
     Return m
End Function
;================================================================================================

;====== GLOBAL VARIABLES ========================================================================
Global blur_2
Global blur_3
Global blur_4

Global blur_timer_2#=MilliSecs()
Global blur_timer_3#=MilliSecs()
Global blur_timer_4#=MilliSecs()
;================================================================================================

;====== CALL THIS TO SET UP BLUR CAMERA, CHILDREN, AND TEXTURES =================================
;====== CALL IT AFTER SETTING UP THE MAIN CAMERA AND THEN PASS THE CAMERA =======================
;====== BLUR_SEVERITY BECOMES WORSE WITH HIGHER NUMERICAL ALPHA LEVELS ==========================
;====== SET THE SEVERITY AND THE FALLOFF VALUES SO THAT ALL 3 BLUR HUDS TOGETHER ARE STILL ======
;====== TRANSPARENT =============================================================================
Function SetupBlurCamera(blur_camera,blur_severity#=.5,falloff_one#=.3,falloff_two#=.4,field_of_view#=1)
     
     blur_2=CreateTexture(Float(GraphicsWidth()),Float(GraphicsHeight()),256)
     
     width_ratio#=TextureWidth(blur_2)/Float(GraphicsWidth())
     height_ratio#=TextureHeight(blur_2)/Float(GraphicsHeight())
     
     ScaleTexture(blur_2,width_ratio#,height_ratio#)
     blur_hud_2=CreateSprite2(blur_camera)
     NameEntity(blur_hud_2,"blur_hud_2")
     EntityOrder(blur_hud_2,-2)
     EntityFX(blur_hud_2,1+8)
     ScaleSprite2(blur_hud_2,640,480)
     Sprite2D(blur_hud_2,320,240,field_of_view#)
     EntityAlpha(blur_hud_2,blur_severity#)
     
     blur_3=CreateTexture(Float(GraphicsWidth()),Float(GraphicsHeight()),256)
     
     ScaleTexture(blur_3,width_ratio#,height_ratio#)
     blur_hud_3=CreateSprite2(blur_camera)
     NameEntity(blur_hud_3,"blur_hud_3")
     EntityOrder(blur_hud_3,-2)
     EntityFX(blur_hud_3,1+8)
     ScaleSprite2(blur_hud_3,640,480)
     Sprite2D(blur_hud_3,320,240,field_of_view#)
     EntityAlpha(blur_hud_3,blur_severity#-falloff_one#)
     
     blur_4=CreateTexture(Float(GraphicsWidth()),Float(GraphicsHeight()),256)
     
     ScaleTexture(blur_4,width_ratio#,height_ratio#)
     blur_hud_4=CreateSprite2(blur_camera)
     NameEntity(blur_hud_4,"blur_hud_4")
     EntityOrder(blur_hud_4,-2)
     EntityFX(blur_hud_4,1+8)
     ScaleSprite2(blur_hud_4,640,480)
     Sprite2D(blur_hud_4,320,240,field_of_view#)
     EntityAlpha(blur_hud_4,blur_severity#-falloff_two#)
     
     HideEntity(blur_hud_2)
     HideEntity(blur_hud_3)
     HideEntity(blur_hud_4)
End Function
;================================================================================================

;====== BLUR FUNCTION, CALL BEFORE RENDERWORLD ==================================================
;====== PASS THE CAMERA TO IT, HOW MUCH DURATION BETWEEN LATENT IMAGES OR JUST HOW MUCH BLUR ====
;====== AND WHETHER YOU WANT TO COPY FROM THE FRONT OR BACK BUFFER 1=FRONT 2=BACK ===============
;====== BE SURE TO CALL ShowBlur() BEFORE USING THIS OR THE BLUR WILL NOT APPEAR ================
;====== YOU CAN ALSO USE HideBlur() SO THE EFFECT CAN BE CONTROLLED FOR A CERTAIN MODE ==========
Function BlurScreen(blur_camera,blur_magnitude=30,back_or_front_buffer=1)
     If blur_timer_2#+blur_magnitude<MilliSecs()
          Select back_or_front_buffer
               Case 1
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,FrontBuffer(),TextureBuffer(blur_2))
               
               Case 2
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,BackBuffer(),TextureBuffer(blur_2))     
                         
          End Select
          EntityTexture(FindChild(blur_camera,"blur_hud_2"),blur_2)
          blur_timer_2#=MilliSecs()
     EndIf
     
     If blur_timer_3#+2*blur_magnitude<MilliSecs()
          Select back_or_front_buffer
               Case 1
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,FrontBuffer(),TextureBuffer(blur_3))
               Case 2
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,BackBuffer(),TextureBuffer(blur_3))
          End Select
          EntityTexture(FindChild(blur_camera,"blur_hud_3"),blur_3)
          blur_timer_3#=MilliSecs()
     EndIf
     
     If blur_timer_4#+3*blur_magnitude<MilliSecs()
          Select back_or_front_buffer
               Case 1
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,FrontBuffer(),TextureBuffer(blur_4))
               Case 2
                    CopyRect(0,0,Float(GraphicsWidth()),Float(GraphicsHeight()),0,0,BackBuffer(),TextureBuffer(blur_4))
          End Select
          EntityTexture(FindChild(blur_camera,"blur_hud_4"),blur_4)
          blur_timer_4#=MilliSecs()
     EndIf
End Function
;================================================================================================

;====== HIDE THE BLUR EFFECTS ===================================================================
Function HideBlur(blur_camera)
     HideEntity(FindChild(blur_camera,"blur_hud_2"))
     HideEntity(FindChild(blur_camera,"blur_hud_3"))
     HideEntity(FindChild(blur_camera,"blur_hud_4"))
End Function
;================================================================================================

;====== SHOW THE BLUR EFFECTS ===================================================================
Function ShowBlur(blur_camera)     
     ShowEntity(FindChild(blur_camera,"blur_hud_2"))
     ShowEntity(FindChild(blur_camera,"blur_hud_3"))
     ShowEntity(FindChild(blur_camera,"blur_hud_4"))
End Function
;================================================================================================