Graphics3D 640,480,16,2  
SetBuffer BackBuffer()  
Const COL_MAIN=1,COL_obj=2  
Collisions COL_obj,COL_MAIN,2,3  
Collisions COL_obj,COL_obj,2,3  
  
Global grav#=-0.4,Fupr#=0,deltalenght#=0  
  
plane=CreatePlane()  
EntityType plane,COL_MAIN  
PositionEntity plane,0,-8,0  
grid=CreateTexture( 64,64,0 )  
ScaleTexture grid,25,25  
SetBuffer TextureBuffer( grid )  
Color 255,255,255:Rect 0,0,32,32  
Color 128,128,128:Rect 32,0,32,32  
Color 128,128,128:Rect 0,32,32,32  
Color 255,255,255:Rect 32,32,32,32  
Color 0,0,255  
SetBuffer BackBuffer()  
EntityTexture plane,grid  
  
obj=CreateCube()  
EntityType obj,COL_obj  
PositionEntity obj,0,4,0  
obj2=CreateSphere()  
EntityType obj2,COL_obj  
EntityRadius obj2,2  
PositionEntity obj2,0,-4,0  
  
cam=CreateCamera()  
PositionEntity cam,0,1,-15  
  
leng#=6  
upr#=.6  
upr2#=.03  
  
While Not KeyHit(1)  
PointEntity cam,obj  
  
If KeyDown(30)  
yvel#=1  
Else  
yvel#=0  
End If  
  
  
If KeyDown(31)  
yvel2#=.5  
Else If KeyDown(45)  
yvel2#=-.5  
Else  
yvel2#=0  
End If  
  
If KeyDown(2)  
If upr#<.99  
upr#=upr#+.005  
EndIf  
Else If KeyDown(3)  
If upr#>.01  
upr#=upr#-.005  
EndIf  
Else  
upr#=upr#  
End If  
  
If KeyDown(4)  
If upr2#<.99  
upr2#=upr2#+.001  
EndIf  
Else If KeyDown(5)  
If upr2#>.005  
upr2#=upr2#-.001  
EndIf  
Else  
upr2#=upr2#  
End If  
  
If KeyDown(6)  
If leng#<8  
leng#=leng#+.01  
EndIf  
Else If KeyDown(7)  
If leng#>0.1  
leng#=leng#-.01  
EndIf  
Else  
leng#=leng#  
End If  
  
dempfer(obj,obj2,leng#,upr#,upr2#,yvel#,yvel2#)  
  
RenderWorld  
UpdateWorld  
Text 10,10,"жесткость подвески="+upr#  
Text 10,40,"скорость затухания колебаний="+upr2#  
Text 10,80,"длина амортизатора="+leng#  
Flip  
Wend  
End  
  
Function dempfer(ent1,ent2,lenght#,damp#,damp2#,spd1#,spd2#)  
  
deltalenght#=EntityDistance(ent1,ent2)  
  
If deltalenght#>1  
Fupr#=tovar#(Fupr#,tovar#(temp#,deltalenght#-lenght#,damp#),damp2#)  
End If  
  
TranslateEntity ent2,0,Fupr+spd1#+grav#,0  
TranslateEntity ent1,0,-Fupr+spd2#+grav#,0  
  
End Function  
  
Function ToVar#(v1#, v2#, dt#)   
 If v1# <> v2#   
  vvv# = v2# - v1#   
  rt# = v1# + (v2# - v1#) * dt#   
  If Sgn(vvv) <> Sgn(v2# - rt#) Then rt# = v2#   
 Else   
  rt# = v2#   
 EndIf   
 Return rt#   
End Function