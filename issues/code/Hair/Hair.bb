Graphics3D 640,480,32,2
SetBuffer BackBuffer()

cam=CreateCamera()
MoveEntity cam,0,0,-7

Type cub
Field ent%
End Type 

cachestvo#=300

;scale
x#=4
y#=x
z#=x
mm#=0

;smeshenie
t#=1

;bufers
xz#=0
yz#=0

t=t/cachestvo# 

For f#=0 To cachestvo
c.cub=New cub
c\ent=CreateCube()
EntityFX c\ent,1
EntityColor c\ent,218,194,103

ScaleEntity c\ent,mm+f/(cachestvo/x),mm+f/(cachestvo/y),mm+f/(cachestvo/z)
t1=LoadTexture("Hair.png",2)
ScaleTexture t1,0.35,0.35

;ето надо для придания небольшой ломаности волосам
xz=xz+t+Rnd(-0.006,0.006)
yz=yz+t+Rnd(-0.006,0.006)
EntityAlpha c\ent,1-f/cachestvo

PositionTexture t1,xz,yz

EntityTexture c\ent,t1
Next 

While Not KeyHit(1)

For c.cub=Each cub
TurnEntity c\ent,0,1,0
Next

UpdateWorld
RenderWorld

Flip
Wend
End