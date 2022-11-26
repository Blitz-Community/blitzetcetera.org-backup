;3d bullets with collisions
;by Mark Rosten
Graphics3D 640,480
 
;-- create type for bullet
Type bullet
     Field entity
     Field distance_to_travel#
End Type
 
;-- create type for block
Type block
     Field entity
End Type
 
;-- define collision constants. one for each type of collision object
Const BLOCK_COLLISION = 1
Const BULLET_COLLISION = 2
 
;-- create player cube
Global player = CreateCube()
 
;-- create camera and attach behind player
Global camera = CreateCamera(player)
PositionEntity camera, 0, 10, -20 ;comment this line out for inside the player view
 
;-- create base bullet entity
Global bullet_entity = CreateSphere(6) ;use your sprite here
EntityColor bullet_entity, 255, 0, 0
EntityType bullet_entity, BULLET_COLLISION ;set collision type
HideEntity bullet_entity
 
;-- declare block counter global field
Global block_counter = 0
 
;-- create base block
Global block_entity = CreateCube()
ScaleEntity block_entity, 3, 3, 3
EntityColor block_entity, 255,255,0
EntityType block_entity, BLOCK_COLLISION ;set collision type
HideEntity block_entity
 
;-- setup collision checks
;(setup sphere_to_polygon collision between blocks and bullets)
Collisions BULLET_COLLISION, BLOCK_COLLISION, 2, 1
 
;-- create initial blocks
For b = 1 To 20
     CreateBlock()
Next
 
;-- create simple texture on the fly
texture = CreateTexture(128,128)
SetBuffer TextureBuffer(texture)
Color 64,64,196
Rect 0,0,32,32
Rect 64,0,32,32
Rect 32,32,32,32
Rect 96,32,32,32
SetBuffer BackBuffer()
 
;-- create platform and texture it
;   (For point of reference when moving player around)
platform = CreateCube()
ScaleEntity platform, 512, 1, 512
EntityTexture platform, texture
PositionEntity platform, 0, -2, 0
 
 
;------------------------ main loop
While Not KeyHit(1)
 
     ;-- move bullets
     MoveBullets()
 
     ;-- movement checks (use mouse movement here instead)
     If KeyDown(200) ;up arrow
          MoveEntity player, 0, 0, .5
     ElseIf KeyDown(208) ;down arrow
          MoveEntity player, 0, 0, -.5
     EndIf
     If KeyDown(203) ;left arrow
          TurnEntity player, 0, .5, 0
     ElseIf KeyDown(205)
          TurnEntity player, 0, -.5, 0
     EndIf
      
     ;-- fire new bullet
     If KeyHit(57) ;change to MouseHit(1) for mouse button 1
          shoot.bullet = New bullet
          shoot\entity = CopyEntity( bullet_entity, player ) ;<parent> parameter here is IMPORTANT
          EntityParent shoot\entity, 0 ;remove from parent so it can move on its own
          shoot\distance_to_travel = 150
     EndIf
      
     ;-- update world and render to screen
     UpdateWorld
     RenderWorld
     Flip
Wend
End
 
 
;============================== F U N C T I O N S
 
 
;------------------------ move bullets
Function MoveBullets()
 
     ;-- go through all active bullets...
     For shoot.bullet = Each bullet
 
          ;move bullet forward
          MoveEntity shoot\entity, 0, 0, 1
 
          ;check if bullet collided with block
          ;(colent will equal entity handle of entity collided with)
          colent = EntityCollided( shoot\entity, BLOCK_COLLISION )
 
          ;collided with block?
          If colent
               ;determine name of entity collided with
               collide_name$ = EntityName( colent )
 
               ;search through blocks for this one (using name), then remove the block
               For delblock.block = Each block
                    ;is this the block?
                    If EntityName( delblock\entity ) = collide_name$
                         ;remove block from world and type collection
                         FreeEntity delblock\entity
                         Delete delblock
                         ;exit collision checking loop
                         Exit
                    EndIf
               Next
 
               ;remove bullet on collision by setting it's distance to travel to zero
               shoot\distance_to_travel = 0
 
               ;spawn new block (slightly further away than default)
               CreateBlock( 50 )
 
          Else
               ;no collision:
                ;reduce counter of distance to travel until finished movement
               shoot\distance_to_travel = shoot\distance_to_travel - 1
 
          EndIf
 
          ;remove bullet entity? (finished it's movement (or collided with block))
          If shoot\distance_to_travel <= 0
               ;remove entity and delete bullet type instance
               FreeEntity shoot\entity
               Delete shoot
          EndIf
     Next
 
End Function
 
 
;------------------------ create a block
Function CreateBlock( min_distance = 0 ) ;(optional parameter min_distance)
 
     ;create new block entity at the player's position
     newblock.block = New block
     newblock\entity = CopyEntity( block_entity, player ) ;position at player's position to start
     EntityParent newblock\entity, 0 ;remove parent
 
     ;move block entity away from the player
     TurnEntity newblock\entity, 0, Rnd(1,40)-20, 0 ;rotate -20 to +20 degrees from player's attitude
     MoveEntity newblock\entity, 0, 0, min_distance + Rnd(50,200) ;move forward random amount
 
     ;increment number of blocks counter
     block_counter = block_counter + 1
 
     ;give this entity a unique name so it can be searched for in the collision check loop
     NameEntity newblock\entity, Str$(block_counter)
 
End Function