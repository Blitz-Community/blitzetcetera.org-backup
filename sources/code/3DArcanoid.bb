
Graphics3D 640,480,16,2 
SeedRnd( MilliSecs()) 
 
Global campiv = CreatePivot() 
Global camera = CreateCamera(campiv) 
PositionEntity camera,0,0,-22 
PointEntity camera,campiv 
 
AmbientLight 255,255,255 
 
Global T_BALL = 1 
Global T_BRICK = 2 
Global T_PRIZ = 3 
Global T_WALL = 4 
Global T_PLAYER = 5 
 
Collisions T_BALL,T_BALL,1,3 
Collisions T_BALL,T_BRICK,2,1 
Collisions T_BALL,T_PLAYER,2,1 
 
Type ball 
 Field x#,y#,z# 
 Field vx#,vy#,vz# 
 Field entity 
End Type 
 
Type brick 
 Field entity 
 Field typ 
 Field dati%[5] 
 Field datf#[5] 
 Field dd 
End Type 
 
Global brickmesh = CreateCube() 
ScaleMesh brickmesh,1,.6,.3 
HideEntity brickmesh 
 
Global ballmesh = CreateSphere() 
ScaleMesh ballmesh,.3,.3,.3 
HideEntity ballmesh 
 
Global player = CreateCube() 
ScaleMesh player,4,0.3,0.3 
PositionEntity player,0,-13,0 
EntityType player,T_PLAYER 
 
Function createbrick.brick( xx#, yy#, zz#, typ%=0) 
 Local b.brick = New brick 
 b\entity = CopyEntity(brickmesh)  
 PositionEntity b\entity,xx,yy,zz 
 b\typ = typ 
 Select typ 
  Case 0 
   EntityAlpha b\entity,.5 
  Case 1 
   EntityAlpha b\entity,.7 
   b\datf[0] = .7 
   b\dati[0] = 7 
 End Select  
 EntityType b\entity,T_BRICK 
 NameEntity b\entity,Handle(b) 
 ResetEntity b\entity  
 Return b 
End Function 
 
Function createball.ball( xx#,yy#,zz#) 
 Local b.ball = New ball 
 b\x# = xx# 
 b\y# = yy# 
 b\z# = zz# 
 b\vx# = Rnd#(.1,.5) 
 b\vy# = Rnd#(.1,.5) 
 b\entity = CopyEntity(ballmesh) 
 EntityRadius b\entity,.3 
 NameEntity b\entity,Handle(b) 
 PositionEntity b\entity, b\x#,b\y#,b\z# 
 EntityType b\entity, T_BALL 
 ResetEntity b\entity 
 Return b 
End Function 
 
Function updateballs() 
 Local b.ball 
 Local bb.brick 
 Local i% 
 Local ent%,surf%,tr% 
 Local dot#,nx#,ny#,ln#,ex#,ey# 
  
 For  b = Each ball     
  b\x# = EntityX#(b\entity) 
  b\y# = EntityY#(b\entity) 
 
  For  i=1 To CountCollisions(b\entity) 
   nx# = CollisionNX(b\entity, i) 
   ny# = CollisionNY(b\entity, i) 
   dot# = b\vx# * nx# + b\vy# * ny# 
   ex# = nx# * dot# 
   ey# = ny# * dot# 
       
   b\vx# = b\vx# - ex# * 2.0 + Rnd(-.02, .02) 
   b\vy# = b\vy# - ey# * 2.0 + Rnd(-.02, .02) 
    
   ln# = Sqr(b\vx * b\vx + b\vy * b\vy) 
   If ln# <> 0 
    ln# = .5 / ln 
    b\vx = b\vx * ln 
    b\vy = b\vy * ln 
   EndIf 
 
   EntityColor b\entity, Rand(200,255),Rand(200,255),Rand(200,255) 
   part_createemiter( CollisionX(b\entity,i),CollisionY(b\entity,i),CollisionZ(b\entity,i),5) 
 
   ent = CollisionEntity(b\entity,i) 
   bb = Object.brick(EntityName(ent)) 
   If bb <> Null 
    kickbrick( bb ) 
   End If 
    
  Next 
 
  ;ResetEntity b\entity 
   
  b\x# = b\x# + b\vx# 
  b\y# = b\y# + b\vy# 
 
  If b\x > 17 Or b\x < -17 Then 
   If b\x > 17 Then b\x = 17 
   If b\x < -17 Then b\x = -17 
   b\vx = b\vx*-1 
  End If 
  If b\y > 15 Then 
   If b\y > 15 Then b\y = 15         
   b\vy = b\vy*-1 
  End If 
 
  If b\y < -15 Then 
   FreeEntity b\entity 
   Delete b 
  Else   
   PositionEntity b\entity,b\x,b\y,b\z 
  End If 
 Next 
End Function 
 
Function updatebricks() 
 For b.brick = Each brick 
  b\dd = b\dd + 1 
  RotateEntity b\entity,0,0,Sin(b\dd)*5 
 Next 
End Function 
 
Function kickbrick( b.brick) 
 
 Select b\Typ 
  Case 0 
   FreeEntity b\entity 
   Delete b 
  Case 1 
   b\dati[0] = b\dati[0]-1 
   If b\dati[0] = 0 
    FreeEntity b\entity 
    Delete b    
   Else 
    b\datf[0] = b\datf[0]-.10 
    EntityAlpha b\entity,b\datf[0] 
   End If 
 
 End Select 
End Function 
 
Global FPS=30 
Global period=1000/FPS 
Global time=MilliSecs()-period 
Global wire,elapsed,ticks,tween# 
Global e_FPS,curFPS,Lasttime  
 
For x=-5 To 5 
For y=2 To 9 
If y=1 Or 10 Then createbrick(x*3,y*1.5,0,1) : Else : createbrick(x*3,y*1.5,0,0) 
Next 
Next
 
part_init() 
 
While Not KeyHit(1) 
 Repeat: elapsed=MilliSecs()-time:Until elapsed  
 ticks=elapsed/period  
 tween#=Float(elapsed Mod period)/Float(period)  
 For k=1 To ticks 
  time=time+period 
  If k=ticks Then CaptureWorld 
  ;UPDATE 
  If KeyHit(57) 
   createball(0,-10,0) 
  End If 
  updateinput() 
  part_update() 
  updateballs() 
  updatebricks() 
  ;UPDATE   
  UpdateWorld 
 Next  
 RenderWorld tween 
 e_FPS() 
 Text 0,10,"FPS:"+e_FPS 
 Flip  
Wend 
 
Function updateinput() 
 MoveEntity player,MouseXSpeed()*0.2,0,0 
 If EntityX(player) > 14 Then PositionEntity(player,14,EntityY(player),EntityZ(player)) 
 If EntityX(player) < -14 Then PositionEntity(player,-14,EntityY(player),EntityZ(player)) 
End Function

;/////////////////////////////////////////////////////////////////////// ////////// 
 
Type part 
 Field obj 
 Field vx#,vy#,vz# 
 Field e.emiter 
 Field life 
End Type 
 
Type emiter 
 Field obj 
 Field running 
 Field Todelete 
 Field life 
 Field particles 
End Type 
 
Global particlesprite 
Global particle_texture 
 
Function part_init() 
 Local x%,y% 
 Local col% = argb(0,0,0,0) 
 
 particlesprite = CreateSprite() 
 ScaleSprite particlesprite,.2,.2 
 particle_texture = CreateTexture(32,32,1+2+8) 
  
 LockBuffer( TextureBuffer(particle_texture)) 
 For x = 0 To TextureWidth(particle_texture)-1 
  For y= 0 To TextureHeight(particle_texture)-1 
   WritePixelFast x%,y%,col,TextureBuffer(particle_texture) 
  Next 
 Next 
 UnlockBuffer( TextureBuffer(particle_texture)) 
  
 SetBuffer TextureBuffer(particle_texture) 
 Color 200,200,200 
 Oval TextureWidth(particle_texture)/2-12,TextureHeight(particle_texture)/2-12 ,25,25,1 
 SetBuffer BackBuffer() 
 
 EntityTexture particlesprite,particle_texture 
 HideEntity particlesprite 
End Function 
 
 
Function part_createparticle.part( e.emiter ) 
 Local p.part = New part 
 p\obj = CopyEntity(particlesprite,e\obj);CreateSprite( e\obj ) 
 EntityColor p\obj,252,249,124 
 p\e = e 
 p\vx = Rnd(-.05,.18) 
 p\vy = Rnd(-.05,.18) 
 p\life = 15 
 e\particles = e\particles + 1 
End Function 
 
Function part_createemiter%( x#,y#,z#, life = 100, parent=0) 
 Local e.emiter = New emiter 
 e\obj = CreatePivot( parent ) 
 e\running = False 
 e\life = life 
 e\Todelete = False 
 If ( parent = 0 ) 
  PositionEntity e\obj,x#,y#,z# 
 End If 
 Return Handle(e)  
End Function 
 
Function part_updateparticles() 
 Local p.part 
 For p = Each part 
  MoveEntity p\obj,p\vx,p\vy,0  
  p\life = p\life - 1 
  If p\life = 0 
   p\e\particles = p\e\particles - 1 
   FreeEntity p\obj 
   Delete p 
  End If 
 Next 
End Function 
 
Function part_updateemiters() 
 Local e.emiter 
 For e = Each emiter 
  If e\Todelete 
   If e\particles = 0 
    FreeEntity e\obj 
    Delete e 
   End If 
  Else 
   e\life = e\life - 1 
   If e\life = 0 
    e\Todelete = True 
   End If 
 
   part_createparticle( e ) 
  End If 
 Next 
End Function 
 
Function part_update() 
 part_updateemiters() 
 part_updateparticles() 
End Function 
;/////////////////////////////////////////////////////////////////////// ////////// 
 
;/////////////////////////////////////////////////////////////////////// /// 
Function argb%(a%,r%=0,g%=0,b%=0) 
 Return (a Shl 24) Or (r Shl 16) Or (g Shl 8) Or b  
End Function  
 
Function e_FPS() 
 mmss=MilliSecs()  
 If mmss-lasttime>1000  
  e_FPS = curFPS : lasttime=mmss : curFPS=0  
 Else  
  curFPS=curFPS+1  
 End If  
End Function 
;/////////////////////////////////////////////////////////////////////// ///