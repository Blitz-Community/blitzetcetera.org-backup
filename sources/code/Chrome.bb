Graphics3D 800,600,24,2 
Global camera=CreateCamera() 
      grid_tex=CreateTexture( 16,16,8,1 ) 
    ScaleTexture grid_tex,10,10 
    SetBuffer TextureBuffer( grid_tex ) 
    ClsColor 255,255,255:Cls:ClsColor 0,0,0 
    Color 192,192,192:Rect 0,0,8,8:Rect 8,8,8,8 
    SetBuffer BackBuffer() 
 
plane=CreatePlane() 
EntityTexture plane,grid_tex 
 
 
 
pivot=CreatePivot() 
PositionEntity pivot,0,2,0 
 
t_cube=CreateCube(  ) 
 
ScaleEntity t_cube,1,50,1 
EntityShininess t_cube,.2 
For t=0 To 359 Step 36 
      cube=CopyEntity( t_cube,pivot ) 
      EntityColor cube,128,128,192 
       
      TurnEntity cube,0,t,0 
      MoveEntity cube,0,0,30 
Next 
FreeEntity t_cube 
 
 
Global qq=CreateSphere(20) 
EntityColor qq,255,128,64 
PositionEntity qq,0,5,0 
PositionEntity qq,0,6,0 
ScaleEntity qq,5,5,5 
 
 
 
;////////////////////////////////////////////////////////////////////Hro m///////////////////////////////////// 
;/////////////////////////////////////////////////////////////////////// ////////////////////////////////////////// 
Type THrom 
 Field Tex 
 Field Plancam 
 Field Camt 
 Field bl 
End Type 
;x, y, z - координаты если меш из функции 
;c - камера 
;m - имя меша  
Function CreateHrom(x=0,y=0,z=0,c,m$="sphere") 
 
ne.THrom=New THrom 
    ne\Camt=c 
      ne\Tex=CreateTexture( 128,128,64 ) 
      ne\Plancam=CreateCamera() 
    TurnEntity ne\Plancam,0,0,0 
    PositionEntity ne\Plancam,0,20,0 
    CameraViewport ne\Plancam,0,0,128,128 
      CameraRange ne\Plancam,0.1,200 
    CameraClsColor ne\Plancam,0,0,0 
      If m$="cube" Then mesh=CreateCube() 
      If m$="sphere" Then         mesh=CreateSphere(24) 
      If m$<>"cube" And m$<>"sphere" Then 
          mesh=m$ 
             
          x=EntityX(m$) 
            y=EntityY(m$) 
            z=EntityZ(m$) 
    End If       
      EntityTexture mesh,ne\Tex,0,2 
      EntityShininess mesh,1 
    PositionEntity mesh, x, y, z 
       
    End Function 
Function UpdateHrom() 
For ne.THrom=Each THrom 
    HideEntity ne\Camt 
      ShowEntity ne\Plancam 
      PointEntity ne\Plancam,ne\Camt 
 
Next 
End Function 
Function RenderHrom() 
For ne.THrom=Each THrom 
      CopyRect 0,0,128,128,0,0,0,TextureBuffer( ne\Tex ) 
      ShowEntity ne\Camt 
      HideEntity ne\Plancam 
       
       
Next       
End Function 
 
 
 
 
;/////////////////////////////////////////////////////////////////////// ////////////////////////////////////////// 
 
 
 
 
CreateHrom(0,7,0,Camera,qq);++++++++++++++++ 
 
 
 
light=CreateLight() 
TurnEntity light,45,45,0 
 
 
 
 
 
d#=-20 
 
While Not KeyHit(1) 
 
      If KeyDown(17) d=d+1 
      If KeyDown(31) d=d-1 
      If KeyDown(30) TurnEntity camera,0,-3,0 
      If KeyDown(32) TurnEntity camera,0,+3,0 
       
      PositionEntity camera,0,7,0 
      MoveEntity camera,0,0,d 
       
      ;TurnEntity cube,.1,.2,.3 
      TurnEntity pivot,0,0.1,0 
       
      UpdateWorld 
      UpdateHrom();++++++++++++++++ 
       
       
      RenderWorld 
      RenderHrom();++++++++++++++++ 
     
      RenderWorld 
       
      Flip 
Wend