;инициализация графики
Graphics3D 800,600,32
SetBuffer BackBuffer()

Global user
Global camera

Const G#=0.001

Const USERT=1;for user
Const TERRT=2;for terrain
Const SHOTT=3;for shot
Const BOTT=4;for bot
Const BORDERT=5;for box

Global jump_bool
Global user_time
Global user_vy#

Global pricel=LoadImage("FPS-Crosshair.png")
Global shot_sprite=LoadSprite("FPS-Shot.png")

Type shot
  Field entity
  Field dist#
  Field time
End Type

Type bot
  Field entity
  Field time
  Field Survivability
End Type

;создание игрока
Function create_user(x#=0,y#=10,z#=0)

  MidHandle pricel
  MaskImage pricel,255,255,255
  
  user=CreateSphere()
  k#=3
  ScaleEntity user,k#,k#,k#
  EntityRadius user,k#
  camera=CreateCamera(user)
  CameraRange camera,0.1,10000
  PositionEntity user,x#,y#,z#
  EntityType user,USERT

End Function

;обновление игрока
Function update_user()
  ;=
  If user_time=0 user_time=MilliSecs()
  new_time=MilliSecs()
  delta_t=new_time-user_time
  user_time=new_time
  ;=
  V#=0.02*delta_t
  u#=70;предельный угол
  TurnEntity camera,MouseYSpeed(),0,0
  TurnEntity user,0,-MouseXSpeed(),0
  If KeyDown(30)=1 Then MoveEntity user,-V#,0,0
  If KeyDown(32)=1 Then MoveEntity user,V#,0,0
  If KeyDown(17)=1 Then MoveEntity user,0,0,+V# 
  If KeyDown(31)=1 Then MoveEntity user,0,0,-V#
  MoveMouse GraphicsWidth()*0.5,GraphicsHeight()*0.5
  If Abs(EntityPitch#(camera))>u# RotateEntity camera,u#*Sgn(EntityPitch#(camera)),0,0
  
  If MouseHit(1) create_shot(EntityX(user),EntityY(user),EntityZ(user),EntityPitch(camera),EntityYaw(user),0)
  If MouseDown(2) create_shot(EntityX(user),EntityY(user),EntityZ(user),EntityPitch(camera),EntityYaw(user),0)
  

  ;физика
  
  
  pick_ent=LinePick(EntityX(user,1),EntityY(user,1),EntityZ(user,1),0,-3.6,0)
  
  TranslateEntity user,0,user_vy#*delta_t,user_vy#*0.01,True
  
  If pick_ent
    jump_bool=False
    user_vy=0
    TranslateEntity user,0,-1,0,True
  Else
    jump_bool=True
    user_vy#=user_vy#-G#*delta_t;v1=v2-a*t
  EndIf

  ;;;
  If KeyHit(57) And jump_bool=False Then user_vy#=1
End Function

Function create_Shot(x#,y#,z#,pitch#,yaw#,roll#)
  s.shot=New shot
  s\entity=CopyEntity(shot_sprite)
  EntityType s\entity,SHOTT
  PositionEntity s\entity,x#,y#,z#,1
  RotateEntity s\entity,pitch#,yaw#,roll#,1
  Return True
End Function

Function update_shot()
  v#=0.1
  max_dist#=1000
  For a.shot=Each shot
    ;=
    If a\time=0 a\time=MilliSecs()
    new_time=MilliSecs()
    delta_t=new_time-a\time
    a\time=new_time
    vs#=v*delta_t
    ;=
    MoveEntity a\entity,0,0,vs#
    a\dist#=a\dist#+vs#
    bot_h=EntityCollided(a\entity,BOTT)
    If a\dist#>max_dist#
      FreeEntity a\entity
      Delete a
    ElseIf EntityCollided(a\entity,TERRT)<>0
      FreeEntity a\entity
      Delete a
    ElseIf bot_h<>0
      FreeEntity a\entity
      Delete a
      bhandle=EntityName(bot_h)
      bc.bot=Object.bot(bhandle)
      bc\Survivability=bc\Survivability-16
      If bc\Survivability<=0
        Delete bc
        FreeEntity bot_h
        create_bot(EntityX(user)+Rnd(-100,100),2,EntityZ(user)+Rnd(-100,100))
      EndIf
    EndIf
  Next
End Function

Function create_bot(x#,y#,z#)
  b.bot=New bot
  b\Survivability=100
  b\entity=CreateSphere()
  EntityType b\entity,BOTT
  PositionEntity b\entity,x#,y#,z#
  NameEntity b\entity,Handle(b)
  Return True
End Function

Function update_bot()
  v#=0.01
  For a.bot=Each bot
    ;=
    If a\time=0 a\time=MilliSecs()
    new_time=MilliSecs()
    delta_t=new_time-a\time
    a\time=new_time
    ;=
    PointEntity a\entity,user
    MoveEntity a\entity,0,0,v#*delta_t
    ;физика
    TranslateEntity user,0,-G#*delta_t,0
  Next
End Function

;создание игрового мира
Function create_world()
  light=CreateLight()
  RotateEntity light,90,0,0
  HideEntity shot_sprite
  terrain=CreatePlane()
  tertex=LoadTexture("FPS-Terrain.jpg")
  ScaleTexture tertex,10,10
  EntityTexture terrain,tertex
  FreeTexture tertex
  EntityType terrain,TERRT
  EntityPickMode terrain,2
  create_user()
  
  sky=MakeSkyBox("FPS-Sky")
  ScaleEntity sky,150,150,150
  border=MakeBox()
  ScaleEntity border,140,140,140
  EntityAlpha border,0
  EntityType border,BORDERT
  
  c_bot=Input("input Amount")
  For i=1 To c_bot
    create_bot(Rnd(-100,100),2,Rnd(-100,100))
  Next
  
  Collisions USERT,BORDERT,2,1
  
  Collisions USERT,TERRT,2,3
  Collisions SHOTT,TERRT,2,1
  
  Collisions BOTT,TERRT,2,3
  Collisions BOTT,USERT,2,3
  Collisions BOTT,BOTT,2,3
  
  Collisions USERT,BOTT,2,3
  
  Collisions SHOTT,BOTT,2,1
End Function

Function MakeSkyBox( file$ )

  m=CreateMesh()
  ;front face
  b=LoadBrush( file$+"FR.jpg",49 )
  s=CreateSurface( m,b )
  AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
  AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  FreeBrush b
  ;right face
  b=LoadBrush( file$+"LF.jpg",49 )
  s=CreateSurface( m,b )
  AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
  AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  FreeBrush b
  ;back face
  b=LoadBrush( file$+"BK.jpg",49 )
  s=CreateSurface( m,b )
  AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
  AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  FreeBrush b
  ;left face
  b=LoadBrush( file$+"RT.jpg",49 )
  s=CreateSurface( m,b )
  AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
  AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  FreeBrush b
  ;top face
  b=LoadBrush( file$+"UP.jpg",49 )
  s=CreateSurface( m,b )
  AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
  AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  FreeBrush b

  ScaleMesh m,100,100,100
  FlipMesh m
  EntityFX m,9
  EntityOrder m,10
  Return m
  
End Function

Function MakeBox()

  m=CreateMesh()
  ;front face
  s=CreateSurface( m )
  AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
  AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  ;right face
  s=CreateSurface( m )
  AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
  AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  ;back face
  s=CreateSurface( m )
  AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
  AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  ;left face
  s=CreateSurface( m )
  AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
  AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3
  ;top face
  s=CreateSurface( m)
  AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
  AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
  AddTriangle s,0,1,2:AddTriangle s,0,2,3

  ScaleMesh m,100,100,100
  FlipMesh m
  EntityFX m,9
  EntityOrder m,10
  Return m
  
End Function

;функции закончились  
;--------------------------------
create_world()

;MAIN LOOP
While Not KeyHit(1)=1
  update_user()
  update_shot()
  update_bot()
  UpdateWorld()
  RenderWorld()
  DrawImage pricel,MouseX(),MouseY()
  Text 10,10,"jump_bool="+jump_bool
  Flip
Wend
;----------------------------
End