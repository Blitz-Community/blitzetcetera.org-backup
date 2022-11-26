;
;     BulletDemo.bb - Copyright ©2002 EdzUp
;     Coded by Ed Upton
;

;Use as you wish although I would like to be given credit for the use of this code.

Graphics3D 640,480,16                                        ;initialise 3d graphics
SetBuffer BackBuffer()                                        ;initialise double buffering

Type BulletType                                                  ;our bullet type
     Field BulletEntity                                        ;the bullet entity
     Field BulletTimer                                        ;timer till bullet is removed
     Field Active                                             ;if its active or not ( True/False )
End Type

Global Camera = CreateCamera()                              ;create camera

Global HitWall = False                                        ;if you hit the wall last cycle

AmbientLight 255,255,255                                   ;set ambient world light to full bright

Global Wall = CreateCube()                                   ;create a wall for out bullets to hit
EntityColor Wall, 255, 0, 0                                   ;colour it red
MoveEntity Wall, 0, 0, 20                                   ;move it infront of the camera
EntityType Wall, 1                                             ;set the collision type to 1 (for use with collisions)
EntityPickMode Wall, 2                                        ;turn on pick mode so this entity is pickable

Collisions 2, 1, 2, 1                                        ;setup Bullet to wall collisions

CreateBullets( 500 )                                        ;create 500 bullet entities

PointEntity Camera, Wall
MoveMouse 320, 240

While Not KeyDown(1)                                        ;main loop
     HitWall = False                                             ;reset HitWall variable

     ;mouse camera controls
     TurnEntity Camera, MouseYSpeed(), -MouseXSpeed(), 0
     MoveMouse 320,240                                        ;needed to stop the mouse from stopping the camera when it hits the edge of the screen

     UpdateBullets( 2.0 )                                   ;update all bullets

     If MouseDown( 1 )=1 Then FireBullet()               ;when mouse pressed fire a bullet

     UpdateWorld                                                  ;update 3d world
     RenderWorld                                                  ;render 3d world to buffer
     
     If HitWall=True Then Text 320, 50, "You hit the wall", 1, 1
     
     Flip                                                       ;flip buffer to front screen
Wend
End

Function CreateBullets( BulletCount )
     ;This function creates a certain number of bullets ( BulletCount ) for use in your game
     Local BC                                                  ;Local variable to make sure no resources are used
     
     For BC =1 To BulletCount                              ;count through to BulletCount
          Bullet.BulletType = New BulletType               ;create new bullettype
          Bullet\BulletEntity = CreateSphere()          ;create a bullet entity (we'll use a sphere for now)
          Bullet\Active = False                              ;make sure bullet is not active
          EntityType Bullet\BulletEntity, 2               ;setup bullet collision type
          EntityRadius Bullet\BulletEntity, .5          ;set the collision radius
          EntityAlpha Bullet\BulletEntity, .5               ;alpha bullets so you can see whats going on
          
          HideEntity Bullet\BulletEntity                    ;hide bullet to make sure game doesnt slow down
          
          ;NOTE we dont need to modify BulletTimer as this will be changed when a bullet is activated
     Next
End Function

Function FireBullet()
     ;this function is used to shoot bullets from the camera
     For Bullet.BulletType = Each BulletType               ;run through our list of bullets
          If Bullet<>Null                                        ;purely for error checking but very good practice
               If Bullet\Active = False                    ;if we find a bullet that isnt active we can use it
               
                    Bullet\Active = True                    ;Activate bullet
                    Bullet\BulletTimer = 50                    ;set timer to 50
                    
                    ;position bullet's entity where player camera is
                    PositionEntity Bullet\BulletEntity, EntityX#( Camera ), EntityY#( Camera ), EntityZ#( Camera )
                    RotateEntity Bullet\BulletEntity, EntityPitch#( Camera ), EntityYaw#( Camera ), EntityRoll#( Camera )
                    ShowEntity Bullet\BulletEntity
                    
                    Goto FBjump                                   ;exit the check
               EndIf
          EndIf
     Next
     .FBJump
End Function

Function UpdateBullets( Speed# )
     ;this function is here to update the bullets every frame
     For Bullet.BulletType = Each BulletType               ;run through list
          If Bullet<>Null                                        ;error checking
               If Bullet\Active=True                         ;only check active bullets
                    MoveEntity Bullet\BulletEntity, 0, 0, Speed#          ;move bullet forward
                    
                    ;we use entitypick first to see if the bullet will hit something
                    ;if we move it next cycle, the Speed# value is used to make sure
                    ;the bullet doesnt pass through anything when moving the bullet,
                    ;like thin fences etc. EntityPickMode needs to be used for this
                    ;to work.
                    Pick = EntityPick( Bullet\BulletEntity, Speed# )
                    If Pick<>0
                         ;put damage code here
                         Bullet\Active = False               ;deactivate bullet
                         HideEntity Bullet\BulletEntity     ;hide entity
                         
                         If Pick = Wall Then HitWall = True
                    EndIf
                    
                    If EntityCollided( Bullet\BulletEntity, 1 )
                         ;put damage code here
                         Bullet\Active = False               ;deactivate bullet
                         HideEntity Bullet\BulletEntity     ;hide entity
                         
                         HitWall = True
                    EndIf
               
                    Bullet\BulletTimer = Bullet\BulletTimer -1
                    If Bullet\BulletTimer<0                    ;see if bullet has timed out
                         Bullet\Active = False               ;deactivate Bullet
                         HideEntity Bullet\BulletEntity     ;hide entity
                    EndIf
               EndIf
          EndIf
     Next
End Function