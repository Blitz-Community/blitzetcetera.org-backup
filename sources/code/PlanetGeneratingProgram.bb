;planet generating program in 3d, using squarep

;these values can be changed to change world characteristics

Global debug = 1 ;show debug info
Global colorscheme = 1 ;specify colour scheme 0..5, default = 0
Global altColors = 0 ;Switch to alternative colour scheme

Global doshade = 0 ;Use 'bumpmap' shading
Global shade_angle# = 0 ;Angle of 'light' in bumpmap shading, eg. 150.0

Global Width = 512 ;Width in pixels, default = 256
Global Height = 256 ;Height in pixels, default = 128

Global rseed# = 0.2 ;Seed as number between 0.0..1.0, eg. 0.3
Global M# = 0.19 ;Initial altitude, default = 0.3
Global dd1# = 0.15 ;Weight for altitude difference, default = 0.4
Global dd2# = 0.03 ;Weight for distance, default = 0.03
Global POW# = 0.2 ;Power for distance function, default = 0.47

Global scale# = 1.0 ;Magnification, default = 1.0
Global longi# = 0.0 ;Longitude of centre in degrees, default = 0.0
Global lat# = 0.0 ;Latitude of centre in degrees, default = 0.0
Global vgrid# = 0.0 ;Vertical gridsize in degrees, 0.0 = no grid
Global hgrid# = 0.0 ;Horisontal gridsize in degrees, 0.0 = no grid

Global latic = 0 ;Colour depends on latitude, default = only altitude
Global lighter = 0 ;Use lighter colours (original scheme only)

;globals used in calculations
Global BLACK=0
Global WHITE=1
Global BLUE0=2
Global BLUE1, LAND0, LAND1, LAND2, LAND4;
Global GREEN1, BROWN0, GREY0;
Global BACK = WHITE ;BLACK

Global shade;
Global Depth ;depth of subdivisions
Global r1#,r2#,r3#,r4# ;seeds
Global DEG2RAD#=0.0174532918661 ;Pi/180
Global Rad2Deg#=(180/Float(Pi))

;arrays used in calculations
Dim col(256,128)
Dim shades(256,128)
Dim rtable(256)
Dim gtable(256)
Dim btable(256)
Dim colors(9,3)

;set colors array from data
Select colorscheme
 Case 1: Restore atlas_colors
 Case 2: Restore mars_colors
 Case 3: Restore grey_colors
 Case 4: Restore moon_colors
 Case 5: Restore cyan_colors
 Default: Restore colors
End Select
For i=0 To 8 ;copy the 9 base colors
 For j=0 To 2: Read colors(i,j): Next
Next

Dim col(Width,Height) ;map colors
If (doshade) Dim shades(Width,Height) ;map shading

If (debug)
 Print "> -s "+rseed+" -i "+M+" -v "+dd1+" -V "+dd2+" -P "+POW
 Print "> -w "+Width+" -h "+Height+" -B "+doshade
EndIf

;init calculations
If (longi>180) longi = longi - 360;
longi = longi*DEG2RAD;
lat = lat*DEG2RAD;
Depth = 3*(Int(log_2(scale*Height)))+6;
r1 = rseed;
r1 = rand2(r1,r1);
r2 = rand2(r1,r1);
r3 = rand2(r1,r2);
r4 = rand2(r2,r3);

setcolours() ;set rgb arrays

If (debug) Print "+----+----+----+----+----+"

squarep() ;Square projection (equidistant latitudes)

If (doshade) smoothshades() ;bumpmap shading

;draw map
image=CreateImage(Width,Height)
LockBuffer ImageBuffer(image)
For y=0 To ImageHeight(image)-1
 For x=0 To ImageWidth(image)-1
  r=rtable(col(x,y))
  g=gtable(col(x,y))
  b=btable(col(x,y))
  If doshade ;bumpmap
   s = (shades(x,y)/3)+85 ;smooth bumpmap
   r = s*rtable(col(x,y))/150;
   If (r>255) r=255;
   g = s*gtable(col(x,y))/150;
   If (g>255) g=255;
   b = s*btable(col(x,y))/150;
   If (b>255) b=255;
  EndIf
  rgb=(r Shl 16)+(g Shl 8)+b
  WritePixelFast x,y,rgb,ImageBuffer(image)
 Next
Next
UnlockBuffer ImageBuffer(image)
SetBuffer BackBuffer()

SaveImage(image,"planet.bmp") ;save the result

;init 3d world
Graphics3D 640,480,0,2
SetBuffer BackBuffer()

camera=CreateCamera()
PositionEntity camera,0,0,-2
light=CreateLight()
RotateEntity light,45,45,0

ball=CreateSphere(20)
texture=LoadTexture("planet.bmp") ;load map
EntityTexture ball,texture

;main loop
While Not KeyHit(1) Or KeyHit(57) ;esc or space keys
 RenderWorld

 TurnEntity ball,0,0.1,0.07

 If KeyHit(17) wf=Not wf : WireFrame wf ;W key

 Flip
Wend
End

;data labels

.colors ;colorscheme 0
Data 0,0,255 ;Dark blue depths
Data 0,128,255 ;Light blue shores
Data 0,255,0 ;Light green lowlands
Data 64,192,16 ;Dark green highlands
Data 64,192,16 ;Dark green Mountains
Data 128,128,32 ;Brown stoney peaks
Data 255,255,255 ;White - peaks
Data 0,0,0 ;Black - Space
Data 0,0,0 ;Black - Lines

.atlas_colors ;colorscheme 1
Data 0,0,192 ;Dark blue depths
Data 0,128,255 ;Light blue shores
Data 0,96,0 ;Dark green Lowlands
Data 0,224,0 ;Light green Highlands
Data 128,176,0 ;Brown mountainsides
Data 128,128,128 ;Grey stoney peaks
Data 255,255,255 ;White - peaks
Data 0,0,0 ;Black - Space
Data 0,0,0 ;Black - Lines

.mars_colors ;colorscheme 2
Data 55,28,75 ;0
Data 136,92,80 ;1
Data 148,100,82 ;2
Data 232,164,90 ;3
Data 248,180,92 ;4
Data 165,128,108 ;5
Data 255,255,255 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.grey_colors ;colorscheme 3
Data 96,96,96 ;0
Data 152,152,152 ;2
Data 144,144,144 ;1
Data 168,168,168 ;3
Data 160,160,160 ;4
Data 144,144,144 ;5
Data 128,128,128 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.moon_colors ;colorscheme 4
Data 112,112,98 ;0
Data 168,168,147 ;2
Data 160,160,140 ;1
Data 184,184,161 ;3
Data 176,176,154 ;4
Data 160,160,140 ;5
Data 144,144,126 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.cyan_colors ;colorscheme 5
Data 74,112,150 ;0
Data 126,168,210 ;2
Data 120,160,200 ;1
Data 138,184,230 ;3
Data 132,176,220 ;4
Data 120,160,200 ;5
Data 108,144,180 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

;functions

Function min(x,y)
 If x<y Then Return x Else Return y
End Function

Function max(x,y)
 If x<y Then Return y Else Return x
End Function

Function setcolours()

 Local i;
 Local crow;
 Local nocols = 256

 If (debug)
  For crow = 0 To 8
   Write "c"+crow+"="+colors(crow,0)+","+colors(crow,1)+","+colors(crow,2)+" ";
   If Not (crow+1) Mod 3 Print " "
  Next
 EndIf

 If (altColors)

  If (nocols < 8) nocols = 8;
    
  LAND0 = max(nocols / 4, BLUE0 + 1);
  BLUE1 = LAND0 - 1;
  GREY0 = nocols - (nocols / 8);
  GREEN1 = min(LAND0 + (nocols / 2), GREY0 - 2);
  BROWN0 = (GREEN1 + GREY0) / 2;
  LAND1 = nocols - 1;

  rtable(BLACK) = colors(7,0);
  gtable(BLACK) = colors(7,0);
  btable(BLACK) = colors(7,0);
  rtable(WHITE) = colors(6,0);
  gtable(WHITE) = colors(6,1);
  btable(WHITE) = colors(6,2);
  rtable(BLUE0) = colors(0,0);
  gtable(BLUE0) = colors(0,1);
  btable(BLUE0) = colors(0,2);

  For i=BLUE0+1 To BLUE1;
   rtable(i) = (colors(0,0)*(BLUE1-i)+colors(1,0)*(i-BLUE0))/(BLUE1-BLUE0);
   gtable(i) = (colors(0,1)*(BLUE1-i)+colors(1,1)*(i-BLUE0))/(BLUE1-BLUE0);
   btable(i) = (colors(0,2)*(BLUE1-i)+colors(1,2)*(i-BLUE0))/(BLUE1-BLUE0);
  Next
  For i=LAND0 To GREEN1-1
   rtable(i) = (colors(2,0)*(GREEN1-i)+colors(3,0)*(i-LAND0))/(GREEN1-LAND0);
   gtable(i) = (colors(2,1)*(GREEN1-i)+colors(3,1)*(i-LAND0))/(GREEN1-LAND0);
   btable(i) = (colors(2,2)*(GREEN1-i)+colors(3,2)*(i-LAND0))/(GREEN1-LAND0);
  Next
  For i=GREEN1 To BROWN0-1
   rtable(i) = (colors(3,0)*(BROWN0-i)+colors(4,0)*(i-GREEN1))/(BROWN0-GREEN1);
   gtable(i) = (colors(3,1)*(BROWN0-i)+colors(4,1)*(i-GREEN1))/(BROWN0-GREEN1);
   btable(i) = (colors(3,2)*(BROWN0-i)+colors(4,2)*(i-GREEN1))/(BROWN0-GREEN1);
  Next
  For i=BROWN0 To GREY0-1
   rtable(i) = (colors(4,0)*(GREY0-i)+colors(5,0)*(i-BROWN0))/(GREY0-BROWN0);
   gtable(i) = (colors(4,1)*(GREY0-i)+colors(5,1)*(i-BROWN0))/(GREY0-BROWN0);
   btable(i) = (colors(4,2)*(GREY0-i)+colors(5,2)*(i-BROWN0))/(GREY0-BROWN0);
  Next
  For i=GREY0 To nocols-1
   rtable(i) = (colors(5,0)*(nocols-i)+(colors(6,0)+1)*(i-GREY0))/(nocols-GREY0);
   gtable(i) = (colors(5,1)*(nocols-i)+(colors(6,1)+1)*(i-GREY0))/(nocols-GREY0);
   btable(i) = (colors(5,2)*(nocols-i)+(colors(6,2)+1)*(i-GREY0))/(nocols-GREY0);
  Next

 Else

  rtable(BLACK) = 0;
  gtable(BLACK) = 0;
  btable(BLACK) = 0;
  rtable(WHITE) = 255;
  gtable(WHITE) = 255;
  btable(WHITE) = 255;

  Local r, c;
  Local x#;

  While (lighter>0)
   For r = 0 To 7-1
    For c = 0 To 3-1
     x = Sqr(Float(colors(r,c))/256.0);
     colors(r,c) = Int(240.0*x+16);
    Next
   Next
   lighter=lighter-1
  Wend

  BLUE1 = (nocols-4)/2+BLUE0;

  If (BLUE1=BLUE0)
   rtable(BLUE0) = colors(0,0);
   gtable(BLUE0) = colors(0,1);
   btable(BLUE0) = colors(0,2);
  Else
   For i=BLUE0 To BLUE1;
    rtable(i) = (colors(0,0)*(BLUE1-i)+colors(1,0)*(i-BLUE0))/(BLUE1-BLUE0);
    gtable(i) = (colors(0,1)*(BLUE1-i)+colors(1,1)*(i-BLUE0))/(BLUE1-BLUE0);
    btable(i) = (colors(0,2)*(BLUE1-i)+colors(1,2)*(i-BLUE0))/(BLUE1-BLUE0);
   Next
  EndIf
  LAND0 = BLUE1+1: LAND2 = nocols-2: LAND1 = (LAND0+LAND2+1)/2;
  For i=LAND0 To LAND1-1
   rtable(i) = (colors(2,0)*(LAND1-i)+colors(3,0)*(i-LAND0))/(LAND1-LAND0);
   gtable(i) = (colors(2,1)*(LAND1-i)+colors(3,1)*(i-LAND0))/(LAND1-LAND0);
   btable(i) = (colors(2,2)*(LAND1-i)+colors(3,2)*(i-LAND0))/(LAND1-LAND0);
  Next

  If (LAND1=LAND2)
   rtable(LAND1) = colors(4,0);
   gtable(LAND1) = colors(4,1);
   btable(LAND1) = colors(4,2);
  Else
   For i=LAND1 To LAND2;
    rtable(i) = (colors(4,0)*(LAND2-i)+colors(5,0)*(i-LAND1))/(LAND2-LAND1);
    gtable(i) = (colors(4,1)*(LAND2-i)+colors(5,1)*(i-LAND1))/(LAND2-LAND1);
    btable(i) = (colors(4,2)*(LAND2-i)+colors(5,2)*(i-LAND1))/(LAND2-LAND1);
   Next
  EndIf
  LAND4 = nocols-1;
  rtable(LAND4) = colors(6,0);
  gtable(LAND4) = colors(6,1);
  btable(LAND4) = colors(6,2);

 EndIf

End Function

Function smoothshades()

 Local i,j;

 For i=0 To Width-3 ;i<Width-2
  For j=0 To Height-3;
   shades(i,j) = (4*shades(i,j)+2*shades(i,j+1)+2*shades(i+1,j)+shades(i+1,j+2)+4)/9;
  Next
 Next

End Function

Function squarep() ;-pq

 Local y#,scale1#,theta1#,cos2#,theta0#;
 Local k,i,j;

 k = Int(lat*Width*scale/Pi);

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write "q"
  y = (2.0*(j-k)-Height)/Width/scale*Pi;
  If (Abs(y)>0.5*Pi) ;(Abs(y)>=0.5*Pi)
   For i = 0 To Width-1
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Next
  Else
   cos2 = Cos(y*Rad2Deg) ;Cos(y)
   If (cos2>0.0)
    scale1 = scale*Width/Height/cos2/Pi;
    Depth = 3*(Int(log_2(scale1*Height)))+3;
    For i = 0 To Width-1
     theta1 = longi-0.5*Pi+Pi*(2.0*i-Width)/Width/scale;
     col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,Sin(y*Rad2Deg),-Sin(theta1*Rad2Deg)*cos2);
     ;col(i,j) = planet0(Cos(theta1)*cos2,Sin(y),-Sin(theta1)*cos2);
     If (doshade) shades(i,j) = shade;
    Next
   EndIf
  EndIf
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
  theta0=theta0-hgrid
  theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = DEG2RAD*theta1;
    j = Height/2+Int(0.5*y*Width*scale/Pi)+k;
    If (j>=0 And j<Height)
     For i = 0 To Width-1: col(i,j) = BLACK: Next;
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
    i = Int(0.5*Width*(1.0+scale*(DEG2RAD*theta1-longi)/Pi));
    If (i>=0 And i<Width)
     For j = max(0,Height/2-Int(0.25*Pi*Width*scale/Pi)+k) To min(Height,Height/2+Int(0.25*Pi*Width*scale/Pi)+k)-1
      col(i,j) = BLACK;
     Next
    EndIf
   Wend
  Wend
 EndIf

End Function

Function planet0(x#,y#,z#)
 ;x#,y#,z#

 Local alt#;
 Local colour;

 alt = planet1(x,y,z);

 If (altColors)

  Local snow# = 0.125;
  Local tree# = snow * 0.5;
  Local bare# = (tree + snow) / 2.0;
    
  If (latic)
   snow = snow - (0.13 * (y*y*y*y*y*y));
   bare = bare - (0.12 * (y*y*y*y*y*y));
   tree = tree - (0.11 * (y*y*y*y*y*y));
  EndIf

  If (alt > 0) ;Land
   If (alt > snow) ;Snow: White
    colour = WHITE;
   ElseIf (alt > bare) ;Snow: Grey - White
    colour = GREY0+Int((1+LAND1-GREY0) * (alt-bare)/(snow-bare));
    If (colour > LAND1) colour = LAND1;
   ElseIf (alt > tree) ;Bare: Brown - Grey
    colour = GREEN1+Int((1+GREY0-GREEN1) * (alt-tree)/(bare-tree));
    If (colour > GREY0) colour = GREY0;
   Else ;Green: Green - Brown
    colour = LAND0+Int((1+GREEN1-LAND0) * (alt)/(tree));
    If (colour > GREEN1) colour = GREEN1;
   EndIf
  Else ;Sea
   alt = alt/2;
   If (alt > snow) ;Snow: White
    colour = WHITE;
   ElseIf (alt > bare)
    colour = GREY0+Int((1+LAND1-GREY0) * (alt-bare)/(snow-bare));
    If (colour > LAND1) colour = LAND1;
   Else
    colour = BLUE1+Int((BLUE1-BLUE0+1)*(25*alt));
    If (colour<BLUE0) colour = BLUE0;
   EndIf
  EndIf

 Else ;calculate colour

  If (alt <=0.0) ;if below sea level then
   If (latic And y*y+alt >= 0.98)
    colour = LAND4 ;white if close to poles
   Else
    colour = BLUE1+Int((BLUE1-BLUE0+1)*(10*alt)) ;blue scale otherwise
    If (colour<BLUE0) colour = BLUE0;
   EndIf
  Else
   If (latic) alt = alt + 0.10204*y*y ;altitude adjusted with latitude
   If (alt >= 0.1) ;if high then
    colour = LAND4;
   Else ;else green to brown scale
    colour = LAND0+Int((LAND2-LAND0+1)*(10*alt));
    If (colour>LAND2) colour = LAND2;
   EndIf
  EndIf

 EndIf

 Return (colour);

End Function

Function planet#(a#,b#,c#,d#,as#,bs#,cs#,ds#,ax#,ay#,az#,bx#,by#,bz#,cx#,cy#,cz#,dx#,dy#,dz#,x#,y#,z#,level)
 ;a#,b#,c#,d# = altitudes of the 4 vertices
 ;as#,bs#,cs#,ds# = seeds of the 4 vertices
 ;ax#,ay#,az#,bx#,by#,bz#,cx#,cy#,cz#,dx#,dy#,dz# = vertex coordinates
 ;x#,y#,z# = goal point
 ;level = levels to go

 Local ssa#,ssb#,ssc#,ssd#,ssas#,ssbs#,sscs#,ssds#,ssax#,ssay#,ssaz#
 Local ssbx#,ssby#,ssbz#,sscx#,sscy#,sscz#,ssdx#,ssdy#,ssdz#;

 Local abx#,aby#,abz#,acx#,acy#,acz#,adx#,ady#,adz#;
 Local bcx#,bcy#,bcz#,bdx#,bdy#,bdz#,cdx#,cdy#,cdz#;
 Local lab#,lac#,lad#,lbc#,lbd#,lcd#;
 Local ex#,ey#,ez#,e#,es#,es1#,es2#,es3#;
 Local eax#,eay#,eaz#,epx#,epy#,epz#;
 Local ecx#,ecy#,ecz#,edx#,edy#,edz#;
 Local x1#,y1#,z1#,x2#,y2#,z2#,l1#,tmp#;

 If (level>0)

  If (level=11)
   ssa=a: ssb=b: ssc=c: ssd=d: ssas=as: ssbs=bs: sscs=cs: ssds=ds;
   ssax=ax: ssay=ay: ssaz=az: ssbx=bx: ssby=by: ssbz=bz;
   sscx=cx: sscy=cy: sscz=cz: ssdx=dx: ssdy=dy: ssdz=dz;
  EndIf

  abx = ax-bx: aby = ay-by: abz = az-bz;
  acx = ax-cx: acy = ay-cy: acz = az-cz;
  lab = abx*abx+aby*aby+abz*abz;
  lac = acx*acx+acy*acy+acz*acz;

  If (lab<lac)
   Return (planet(a,c,b,d,as,cs,bs,ds,ax,ay,az,cx,cy,cz,bx,by,bz,dx,dy,dz,x,y,z,level));
  Else
   adx = ax-dx: ady = ay-dy: adz = az-dz;
   lad = adx*adx+ady*ady+adz*adz;
   If (lab<lad)
    Return (planet(a,d,b,c,as,ds,bs,cs,ax,ay,az,dx,dy,dz,bx,by,bz,cx,cy,cz,x,y,z,level));
   Else
    bcx = bx-cx: bcy = by-cy: bcz = bz-cz;
    lbc = bcx*bcx+bcy*bcy+bcz*bcz;
    If (lab<lbc)
     Return (planet(b,c,a,d,bs,cs,as,ds,bx,by,bz,cx,cy,cz,ax,ay,az,dx,dy,dz,x,y,z,level));
    Else
     bdx = bx-dx: bdy = by-dy: bdz = bz-dz;
     lbd = bdx*bdx+bdy*bdy+bdz*bdz;
     If (lab<lbd)
      Return (planet(b,d,a,c,bs,ds,as,cs,bx,by,bz,dx,dy,dz,ax,ay,az,cx,cy,cz,x,y,z,level));
     Else
      cdx = cx-dx: cdy = cy-dy: cdz = cz-dz;
      lcd = cdx*cdx+cdy*cdy+cdz*cdz;
      If (lab<lcd)
       Return (planet(c,d,a,b,cs,ds,as,bs,cx,cy,cz,dx,dy,dz,ax,ay,az,bx,by,bz,x,y,z,level));
      Else
       es = rand2(as,bs);
       es1 = rand2(es,es);
       es2 = 0.5+0.1*rand2(es1,es1);
       es3 = 1.0-es2;
       If (ax=bx) ;very unlikely to ever happen
        ex = 0.5*ax+0.5*bx: ey = 0.5*ay+0.5*by: ez = 0.5*az+0.5*bz;
       ElseIf (ax<bx)
        ex = es2*ax+es3*bx: ey = es2*ay+es3*by: ez = es2*az+es3*bz;
       Else
        ex = es3*ax+es2*bx: ey = es3*ay+es2*by: ez = es3*az+es2*bz;
       EndIf
       If (lab>1.0) lab = lab^0.75 ;pow(lab,0.75);
       e = 0.5*(a+b)+es*dd1*Abs(a-b)+es1*dd2*(lab^POW) ;pow(lab,POW);
       eax = ax-ex: eay = ay-ey: eaz = az-ez;
       epx =  x-ex: epy =  y-ey: epz =  z-ez;
       ecx = cx-ex: ecy = cy-ey: ecz = cz-ez;
       edx = dx-ex: edy = dy-ey: edz = dz-ez;
       If ((eax*ecy*edz+eay*ecz*edx+eaz*ecx*edy-eaz*ecy*edx-eay*ecx*edz-eax*ecz*edy)*(epx*ecy*edz+epy*ecz*edx+epz*ecx*edy-epz*ecy*edx-epy*ecx*edz-epx*ecz*edy)>0.0)
        Return (planet(c,d,a,e,cs,ds,as,es,cx,cy,cz,dx,dy,dz,ax,ay,az,ex,ey,ez,x,y,z,level-1));
       Else
        Return (planet(c,d,b,e,cs,ds,bs,es,cx,cy,cz,dx,dy,dz,bx,by,bz,ex,ey,ez,x,y,z,level-1));
       EndIf
      EndIf
     EndIf
    EndIf
   EndIf 
  EndIf

 Else

  If (doshade)
   x1 = 0.25*(ax+bx+cx+dx);
   x1 = a*(x1-ax)+b*(x1-bx)+c*(x1-cx)+d*(x1-dx);
   y1 = 0.25*(ay+by+cy+dy);
   y1 = a*(y1-ay)+b*(y1-by)+c*(y1-cy)+d*(y1-dy);
   z1 = 0.25*(az+bz+cz+dz);
   z1 = a*(z1-az)+b*(z1-bz)+c*(z1-cz)+d*(z1-dz);
   l1 = Sqr(x1*x1+y1*y1+z1*z1);
   If (l1=0.0) l1 = 1.0;
   tmp = Sqr(1.0-y*y);
   If (tmp<0.0001) tmp = 0.0001;
   x2 = x*x1+y*y1+z*z1;
   y2 = -x*y/tmp*x1+tmp*y1-z*y/tmp*z1;
   z2 = -z/tmp*x1+x/tmp*z1;
   shade = Int((-Sin(Pi*shade_angle/180.0)*y2-Cos(Pi*shade_angle/180.0)*z2)/l1*48.0+128.0);
   If (shade<10) shade = 10;
   If (shade>255) shade = 255;
  EndIf
  Return ((a+b+c+d)/4);

 EndIf

End Function

Function planet1#(x#,y#,z#)
 ;x#,y#,z#

 Local abx#,aby#,abz#,acx#,acy#,acz#,adx#,ady#,adz#,apx#,apy#,apz#;
 Local bax#,bay#,baz#,bcx#,bcy#,bcz#,bdx#,bdy#,bdz#,bpx#,bpy#,bpz#;

 abx = ssbx-ssax: aby = ssby-ssay: abz = ssbz-ssaz;
 acx = sscx-ssax: acy = sscy-ssay: acz = sscz-ssaz;
 adx = ssdx-ssax: ady = ssdy-ssay: adz = ssdz-ssaz;
 apx = x-ssax: apy = y-ssay: apz = z-ssaz;

 If ((adx*aby*acz+ady*abz*acx+adz*abx*acy-adz*aby*acx-ady*abx*acz-adx*abz*acy)*(apx*aby*acz+apy*abz*acx+apz*abx*acy-apz*aby*acx-apy*abx*acz-apx*abz*acy)>0.0);
  ;p is on same side of abc as d
  If ((acx*aby*adz+acy*abz*adx+acz*abx*ady-acz*aby*adx-acy*abx*adz-acx*abz*ady)*(apx*aby*adz+apy*abz*adx+apz*abx*ady-apz*aby*adx-apy*abx*adz-apx*abz*ady)>0.0);
   ;p is on same side of abd as c
   If ((abx*ady*acz+aby*adz*acx+abz*adx*acy-abz*ady*acx-aby*adx*acz-abx*adz*acy)*(apx*ady*acz+apy*adz*acx+apz*adx*acy-apz*ady*acx-apy*adx*acz-apx*adz*acy)>0.0);
    ;p is on same side of acd as b
    bax = -abx: bay = -aby: baz = -abz;
    bcx = sscx-ssbx: bcy = sscy-ssby: bcz = sscz-ssbz;
    bdx = ssdx-ssbx: bdy = ssdy-ssby: bdz = ssdz-ssbz;
    bpx = x-ssbx: bpy = y-ssby: bpz = z-ssbz;
    If ((bax*bcy*bdz+bay*bcz*bdx+baz*bcx*bdy-baz*bcy*bdx-bay*bcx*bdz-bax*bcz*bdy)*(bpx*bcy*bdz+bpy*bcz*bdx+bpz*bcx*bdy-bpz*bcy*bdx-bpy*bcx*bdz-bpx*bcz*bdy)>0.0);
     ;p is on same side of bcd as a
     ;Hence, p is inside tetrahedron
     Return (planet(ssa,ssb,ssc,ssd,ssas,ssbs,sscs,ssds,ssax,ssay,ssaz,ssbx,ssby,ssbz,sscx,sscy,sscz,ssdx,ssdy,ssdz,x,y,z,11));
    EndIf
   EndIf
  EndIf
 EndIf

 ;otherwise
 Return (planet(M,M,M,M,r1,r2,r3,r4,0.0,0.0,3.01,0.0,Sqr(8.0)+0.01*r1*r1,-1.02+0.01*r2*r3,-Sqr(6.0)-0.01*r3*r3,-Sqr(2.0)-0.01*r4*r4,-1.02+0.01*r1*r2,Sqr(6.0)-0.01*r2*r2,-Sqr(2.0)-0.01*r3*r3,-1.02+0.01*r1*r3,x,y,z,Depth));
 ;M,M,M,M = initial altitude is M on all corners of tetrahedron
 ;r1,r2,r3,r4 = same seed set is used in every call
 ;coordinates of vertices
 ;x,y,z = coordinates of point we want colour of
 ;Depth = subdivision depth

End Function

Function rand2#(p#,q#)
 ;p#,q#
 ;random number generator taking two seeds
 ;rand2(p,q) = rand2(q,p) is important

 Local r#;
 r = (p+3.14159265)*(q+3.14159265);
 Return (2.0*(r-Int(r))-1.0);

End Function

Function log_2#(x#)
 Return (Log(x)/Log(2.0))
End Function
