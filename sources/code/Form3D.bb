; Form3D.bb
; Form3D - a pixel perfect 3d gui
; Free Public Domain

; Note:
; This is a free public opensource Blitz3D project. You can derive your
; own works form it or even help add gui functionality to this. It's 100% free!

; this global helps with mouse z order
Global int_gCURRENTZORDERPOS=0


; Form type constants describing what the basic form's purpose is
; To be used with the formtype field of Type formInfo
Const FT_TEXT=0 ; Text
Const FT_NAG=1  ; non-animated graphic
Const FT_NHPB=2 ; non-highlighted push button
Const FT_HPB=3  ; highlighted push button style
Const FT_NHT=4  ; non-highlighted (true/false) toggle
Const FT_HT=5 ; highlighted (true/false) toggle
Const FT_INPUT=6; Input
Const FT_PANEL=7; Panel

Type formInfo
  Field formtype ; Form Type values - use FT_ constants
  Field thisFont.fontInfo ; stores what font this 3d text form uses
  Field zorder  ; store z order position
  Field active  ; !!! not implemented yet
  Field visible ; !!! not implemented yet
  Field alpha#  ; 3d form's vertex alpha# component
  Field red   ; " red component
  Field green   ; " green component 
  Field blue    ; " blue component
  Field alphamask ; true/false - sets see through pixels(color 0,0,0)
          ; to alpha 0 on textures when setting new texture image
          ; during runtime(aka: F3D_SetState). When set to FALSE there is no 
          ; speed hit when setting a new texture, but if new texture is different
          ; in size from previous texture (for example smaller in size)
          ; then you will most likely see left over graphics from previous
          ; texture. If you keep the textures the same size and same graphical
          ; shape (like buttons for example), then you will not notice any
          ; left over graphics when texture switching.
          ; If you must have it clean looking for textures that are not the same
          ; size and same graphical shape then set this to TRUE. In this case, the
          ; bigger the texture, the bigger the speed hit when animating textures
          ; for gadgets like buttons as an example.
          ; [---> USE the F3D_SetAlphaMask to set alphamask <---]
  Field mdooba      ; 0=no mousedown, 1=mouse down out of box area, 2=mouse down inside of box area
  Field x#        ; World X# cordinates
  Field y#        ; World Y# cordinates
  Field screenx     ; Screen X position
  Field screeny       ; Screen Y position
  Field width       ; width of visible graphic
  Field height      ; height of visible graphic
  Field txt$        ; text to be displayed (ie: F3D_CreateText, F3D_UpdateText, F3D_Input )
  Field parent.formInfo ; Type Handle to Parent 3D form this 3d form will attach to 
  Field numchildren     ; This form must be a parent with >0 children 3d forms
  Field childbank     ; bank stores children 3d form handles
  Field childpos      ; from parent zordor position
  Field hsize       ; internal use
  Field vsize       ; internal use
  Field tsize       ; internal use
  Field state       ; gives state values according to mouse over and clicks
  Field texture1      ; textures used for differen 3d form states
  Field texture2      ;
  Field texture3      ;
  Field texture4      ;
  Field shadow      ; used for F3D_Mouse
  Field mesh        ; mesh to create 3d form with
  Field numsurfs      ;
  Field surfbank      ;
  Field numtex      ;
  Field texbank     ;
End Type

Global mousepointerHandle
Global mousepointer2Handle
Global int_gOldMouseX=0
Global int_gOldMouseY=0

Global FORMZ#=10.0 ; Distance from camera the create plane entity will be moved to

Type zOrder
  Field this3DForm.formInfo
End Type
Global zList.zOrder

Type zParentOrder
  Field thisParent.formInfo
End Type
Global zPList.zParentOrder

Type fontInfo
  Field maxcol
  Field maxrow
  Field chrw
  Field chrh
  Field texsize
  Field font
  Field fonttexture
  Field fontname$
  Field fontsize
End Type
Global defaultfont.fontInfo

; characters wanted for use with text
Global gcharlist$
gcharlist$="0123456789!"+Chr$(34)+"#$%&'()*+,-./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@[]|\<>?=:;"

; var help to save unique font texture names
Global ftnum
ftnum=1

; F3D_Setup
; thisCamera - main camera entity to set invisible plane on
Function F3D_Setup(thisCamera)
  ; make invisible plane to help place Form3d gadgets/text correctly
  terr=CreatePlane(1,thisCamera)
  EntityPickMode terr,2
  PositionEntity terr,0,0,FORMZ#
  RotateEntity terr,-90,0,0
  EntityColor terr,0,0,0
  EntityAlpha terr,0
End Function

; F3D_CreateFont
; fontname$ - font to use. Will create a bmp of characters using this font.
; fontsize - size of font
; chrw,chrh - characters spaced within the texture, example: 16x16
; texsize - size of texture, for example 256 = 256x256
; RETURN:
; Font Handle to a fontInfo type
Function F3D_CreateFont(fontname$,fontsize,chrw,chrh,texsize)

  thisFont.fontInfo = New fontInfo


  thisfnt=LoadFont(fontname$,fontsize)
  SetFont thisfnt
  
  thisFont\font=thisfnt
  thisFont\fontname$=fontname$
  thisFont\fontsize=fontsize
  
  ; create font texture
  fontimage=CreateImage(texsize,texsize)
  fontbuf=ImageBuffer(fontimage)
  xpos=0
  ypos=0
  cpos=1
  SetBuffer fontbuf
  ClsColor 0,0,0
  Cls
  Color 255,255,255
  slen=Len(gcharlist$)
  While cpos<slen+1
    Text xpos,ypos,Mid$(gcharlist$,cpos,1)
    cpos=cpos+1
    xpos=xpos+chrw
    If xpos=>texsize
      xpos=0
      ypos=ypos+chrh
    EndIf
  Wend
  SetBuffer BackBuffer()
  
  SaveImage(fontimage,"fonttexture"+ftnum+".bmp")
  thisFont\fonttexture = LoadTexture("fonttexture"+ftnum+".bmp",1+2+16+32+256)
  ftnum=ftnum+1

  thisFont\chrw=chrw
  thisFont\chrh=chrh
  thisFont\texsize=texsize
  
  thisFont\maxcol=texsize/chrw
  thisFont\maxrow=texsize/chrh  
  
  If defaultfont=Null Then defaultfont=thisFont
  
  Return Handle(thisFont)
End Function

; F3D_SetFont
; thisFontHandle - Font Handle to set before calling F3D_CreateText
Function F3D_SetFont(thisFontHandle)
  Local thisFont.fontInfo
  thisFont.fontInfo = Object.fontInfo( thisFontHandle )
  defaultfont=thisFont
  SetFont defaultfont\font
End Function

; F3D_CreateText
; txt$ - Text to display
; formXp - screen X position
; formYp - screen Y position
; width - width of form
; height - height of form
; parent - a parent handle that this object will attach itself to
; minpow - minimum surface size to split up form by. example: use 5 for: 2^5=32
; maxpow - maximum surface size to split up form by. example: use 8 for: 2^8=256
; RETURN:
; Text Handle to a formInfo type
Function F3D_CreateText(txt$,formXp,formYp,width,height=0,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo, thisMesh, tsw, tsh, numoftextures
  
  this3DForm.formInfo = New formInfo
  this3DForm\formtype=FT_TEXT
  this3DForm\thisFont=defaultfont
  this3DForm\alphamask=False
  this3DForm\shadow=False
  this3DForm\parent=Null
  this3DForm\childpos=0
  this3DForm\numchildren=0
  this3DForm\childbank=CreateBank(0)
    
    
  thisMesh=CreateMesh(camera)
  this3DForm\mesh=thisMesh
  this3DForm\txt$=txt$

  tsw=width
  tsh=height
  If height=0
    SetFont this3DForm\thisFont\font
    tsh=FontHeight()
  EndIf
  
  this3DForm\screenx=formXp
  this3DForm\screeny=formYp
  this3DForm\width=tsw
  this3DForm\height=tsh
  
  sizepow=minpow
  qsize=2^sizepow
  splitv=False  
  While tsh>qsize
    sizepow=sizepow+1
    qsize=2^sizepow
  Wend
  If qsize>2^maxpow
    ; split vertical and horizontal
    qsize=2^maxpow
    splitv=True
  Else
    ; split horizontal only
    splitv=False
  EndIf
  
  hqsize=0
  While (hqsize*qsize)<tsw
    hqsize=hqsize+1
  Wend 

  vqsize=0
  If splitv=True
    While (vqsize*qsize)<tsh
      vqsize=vqsize+1
    Wend 
  Else
    vqsize=1  
  EndIf
  

  this3DForm\hsize=hqsize
  this3DForm\vsize=vqsize
  this3DForm\tsize=qsize
  
  CameraPick(camera,formXp,formYp)
  x1#=PickedX#()
  y1#=PickedY#()
  CameraPick(camera,formXp+(hqsize*qsize),formYp+(vqsize*qsize))
  x2#=PickedX#()
  y2#=PickedY#()


  this3DForm\x#=x1#
  this3DForm\y#=y1#
  
  tw#=Abs(x2#-x1#)
  th#=Abs(y2#-y1#)
  txs#=tw#/Float(hqsize*qsize)
  tys#=th#/Float(vqsize*qsize)
  
  min#=0.5/Float(qsize)
  max#=(Float(qsize)+0.5)/Float(qsize)

  fontbuf=TextureBuffer(this3DForm\thisFont\fonttexture)
  SetFont this3DForm\thisFont\font
  
  numoftextures=0
  this3DForm\texbank=CreateBank(0)

  numofsurfaces=0
  this3DForm\surfbank=CreateBank(0)

  thisBrush = CreateBrush()
  For j=0 To vqsize-1
  For i=0 To hqsize-1
    Surf=CreateSurface(thisMesh)
    numofsurfaces=numofsurfaces+1
    this3DForm\numsurfs=numofsurfaces
    ResizeBank this3DForm\surfbank,numofsurfaces*4
    PokeInt this3DForm\surfbank,numofsurfaces*4-4,Surf
    
  
      ; Create and store texture for possible later updates
      ti=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(ti)
      numoftextures=numoftextures+1
      this3DForm\numtex=numoftextures
      ResizeBank this3DForm\texbank,numoftextures*4
      PokeInt this3DForm\texbank,numoftextures*4-4,ti

      int_ManualAlpha(tibuf,qsize)

      placetext=0-(i*qsize)
      For z=1 To Len(txt$)
        location=Instr(gcharlist$,Mid$(txt$,z,1),1)
        If location>0 Or Mid$(txt$,z,1)=Chr$(32)
          thisrow=(location/this3DForm\thisFont\maxrow) ;Y
          thiscol=location Mod this3DForm\thisFont\maxcol ;X
          
          ; *** brain fart lazy hotfix hack ***
          ; stops it from it placing last column characters from fonttexture in
          ; front, negative area on the next row.
          ; Rem out to see it not print characters to get a better idea.
          If thiscol=0 
            thiscol=this3DForm\thisFont\maxcol
            thisrow=thisrow-1
          EndIf
          
          CopyRect (thiscol*this3DForm\thisFont\chrw)-this3DForm\thisFont\chrw,thisrow*this3DForm\thisFont\chrh,this3DForm\thisFont\chrw,this3DForm\thisFont\chrh,placetext,0-(j*qsize),fontbuf,tibuf
          placetext=placetext+StringWidth(Mid$(txt$,z,1))
        EndIf
      Next

    tv0=AddVertex(Surf,x1#+(txs#*(i*qsize))     ,y1#-(tys#*(j*qsize)),FORMZ#,min#,min#)
    tv1=AddVertex(Surf,x1#+(txs#*(((i+1)*qsize))) ,y1#-(tys#*(j*qsize)),FORMZ#,max#,min#)
    tv2=AddVertex(Surf,x1#+(txs#*(((i+1)*qsize))) ,y1#-(tys#*(((j+1)*qsize))),FORMZ#,max#,max#)
    tv3=AddVertex(Surf,x1#+(txs#*(i*qsize))     ,y1#-(tys#*(((j+1)*qsize))),FORMZ#,min#,max#)
    tri0=AddTriangle(Surf,tv0,tv1,tv2)
    tri1=AddTriangle(Surf,tv0,tv2,tv3)


    VertexColor Surf,tv0,255,255,255
    VertexColor Surf,tv1,255,255,255
    VertexColor Surf,tv2,255,255,255
    VertexColor Surf,tv3,255,255,255
  
    BrushTexture thisBrush, ti
    BrushFX thisBrush, 3
    PaintSurface Surf,thisBrush

  Next
  Next
  FreeBrush thisBrush
  int_Add2zList(Handle(this3DForm),parent)
  
  Return Handle(this3DForm)

End Function


; F3D_UpdateText
; this3DFormHandle - Text handle to be updated
; txt$ - text to be updated
Function F3D_UpdateText(this3DFormHandle,txt$)
  Local this3DForm.formInfo
  Local thisSurf, thisTexture, brushnum
  brushnum=0

  this3DForm.formInfo = Object.formInfo(this3DFormHandle)

  this3DForm\txt$=txt$

  fontbuf=TextureBuffer(this3DForm\thisFont\fonttexture)
  SetFont this3DForm\thisFont\font
  
  thisBrush = CreateBrush()
  For j=0 To this3DForm\vsize-1
  For i=0 To this3DForm\hsize-1
    brushnum=brushnum+4
    thisSurf=PeekInt(this3DForm\surfbank,brushnum-4)
    
    
      thisTexture=PeekInt(this3DForm\texbank,brushnum-4)
      tibuf=TextureBuffer(thisTexture)
    
      placetext=0-(i*this3DForm\tsize)
      For z=1 To Len(txt$)
        location=Instr(gcharlist$,Mid$(txt$,z,1),1)
        
        If location>0 Or Mid$(txt$,z,1)=Chr$(32)
          thisrow=(location/this3DForm\thisFont\maxrow) ;Y
          thiscol=location Mod this3DForm\thisFont\maxcol ;X
          
          If thiscol=0 
            thiscol=this3DForm\thisFont\maxcol
            thisrow=thisrow-1
          EndIf
          
          CopyRect (thiscol*this3DForm\thisFont\chrw)-this3DForm\thisFont\chrw,thisrow*this3DForm\thisFont\chrh,this3DForm\thisFont\chrw,this3DForm\thisFont\chrh,placetext,0-(j*this3DForm\tsize),fontbuf,tibuf
          placetext=placetext+StringWidth(Mid$(txt$,z,1))
        EndIf
      Next
  
    BrushTexture thisBrush, thisTexture
    BrushFX thisBrush, 3
    PaintSurface thisSurf,thisBrush

  Next
  Next
  FreeBrush thisBrush
  Return
End Function

; F3D_VColor
; this3DFormHandle - 3D Form Handle ( can be a text handle )
; red - red component value of 3d form's vertices
; green - red component value of 3d form's vertices
; blue - red component value of 3d form's vertices
; alpha# - alpha component value of 3d form's vertices
Function F3D_VColor(this3DFormHandle,red,green,blue,alpha#=1.0)
  Local thisSurf,numsurf,thisVertex,numverts
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
    
  this3DForm\alpha#=alpha#
  this3DForm\red=red
  this3DForm\green=green
  this3DForm\blue=blue
  numsurf=CountSurfaces(this3DForm\mesh)
  For i=1 To numsurf
    thisSurf=GetSurface(this3DForm\mesh,i)
    numverts=CountVertices(thisSurf)
    For j=0 To numverts-1
      VertexColor thisSurf,j,red,green,blue,alpha#
    Next 
  Next
End Function

; F3D_CreateForm
; formXp - screen X position
; formYp - screen Y position
; formtype - type of button styles
; tex1 - default texture
; tex2 - 2nd
; tex3 - 3rd 
; tex4 - 4th 
; parent - a parent handle that this object will attach itself to
; minpow - minimum surface size to split up form by. example: use 5 for: 2^5=32
; maxpow - maximum surface size to split up form by. example: use 8 for: 2^8=256
; RETURN:
; 3D Form handle to a formInfo type
Function F3D_CreateForm(formXp,formYp,formtype,tex1,tex2=0,tex3=0,tex4=0,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo, thisMesh, tsw, tsh, numoftextures
  Local thisParent.formInfo
  
  this3DForm.formInfo = New formInfo
  this3DForm\formtype=formtype
  this3DForm\alphamask=False
  this3DForm\shadow=False
  this3DForm\parent=Null
  this3DForm\childpos=0
  this3DForm\numchildren=0
  this3DForm\childbank=CreateBank(0)
  
  thisMesh=CreateMesh(camera)
  this3DForm\mesh=thisMesh
  this3DForm\txt$=""
  
  tsw=TextureWidth(tex1)
  tsh=TextureHeight(tex1)
  If tex2<>0
    tsw2=TextureWidth(tex2)
    tsh2=TextureHeight(tex2) 
    If tsw<tsw2 Then tsw=tsw2
    If tsh<tsh2 Then tsh=tsh2
  EndIf
  If tex3<>0
    tsw3=TextureWidth(tex3)
    tsh3=TextureHeight(tex3) 
    If tsw<tsw3 Then tsw=tsw3
    If tsh<tsh3 Then tsh=tsh3
  EndIf
  If tex4<>0
    tsw4=TextureWidth(tex4)
    tsh4=TextureHeight(tex4) 
    If tsw<tsw4 Then tsw=tsw4
    If tsh<tsh4 Then tsh=tsh4
  EndIf
  
  this3DForm\screenx=formXp
  this3DForm\screeny=formYp 
  this3DForm\width=tsw
  this3DForm\height=tsh
  
  sizepow=minpow
  qsize=2^sizepow
  splitv=False  
  While tsh>qsize
    sizepow=sizepow+1
    qsize=2^sizepow
  Wend
  If qsize>2^maxpow
    ; split vertical and horizontal
    qsize=2^maxpow
    splitv=True
  Else
    ; split horizontal only
    splitv=False
  EndIf
  
  hqsize=0
  While (hqsize*qsize)<tsw
    hqsize=hqsize+1
  Wend 

  vqsize=0
  If splitv=True
    While (vqsize*qsize)<tsh
      vqsize=vqsize+1
    Wend 
  Else
    vqsize=1  
  EndIf
  

  this3DForm\hsize=hqsize
  this3DForm\vsize=vqsize
  this3DForm\tsize=qsize
  
  CameraPick(camera,formXp,formYp)
  x1#=PickedX#()
  y1#=PickedY#()
  CameraPick(camera,formXp+(hqsize*qsize),formYp+(vqsize*qsize))
  x2#=PickedX#()
  y2#=PickedY#()


  this3DForm\x#=x1#
  this3DForm\y#=y1#
  
  tw#=Abs(x2#-x1#)
  th#=Abs(y2#-y1#)
  txs#=tw#/Float(hqsize*qsize)
  tys#=th#/Float(vqsize*qsize)
  
  min#=0.5/Float(qsize)
  max#=(Float(qsize)+0.5)/Float(qsize)

  
  texbuf=TextureBuffer(tex1)
  this3DForm\state=0
  this3DForm\texture1=tex1
  this3DForm\texture2=tex2
  this3DForm\texture3=tex3
  this3DForm\texture4=tex4

  numoftextures=0
  this3DForm\texbank=CreateBank(0)

  numofsurfaces=0
  this3DForm\surfbank=CreateBank(0)
  

  thisBrush = CreateBrush()
  For j=0 To vqsize-1
  For i=0 To hqsize-1
    Surf=CreateSurface(thisMesh)
    numofsurfaces=numofsurfaces+1
    this3DForm\numsurfs=numofsurfaces
    ResizeBank this3DForm\surfbank,numofsurfaces*4
    PokeInt this3DForm\surfbank,numofsurfaces*4-4,Surf
    
  
      ; Create and store texture for possible later updates
      ti=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(ti)
      numoftextures=numoftextures+1
      this3DForm\numtex=numoftextures
      ResizeBank this3DForm\texbank,numoftextures*4
      PokeInt this3DForm\texbank,numoftextures*4-4,ti

      CopyRect 0,0,tsw,tsh,0-(i*qsize),0-(j*qsize),texbuf,tibuf
      
      int_ManualAlpha(tibuf,qsize)
        

    tv0=AddVertex(Surf,x1#+(txs#*(i*qsize))     ,y1#-(tys#*(j*qsize)),FORMZ#,min#,min#)
    tv1=AddVertex(Surf,x1#+(txs#*(((i+1)*qsize))) ,y1#-(tys#*(j*qsize)),FORMZ#,max#,min#)
    tv2=AddVertex(Surf,x1#+(txs#*(((i+1)*qsize))) ,y1#-(tys#*(((j+1)*qsize))),FORMZ#,max#,max#)
    tv3=AddVertex(Surf,x1#+(txs#*(i*qsize))     ,y1#-(tys#*(((j+1)*qsize))),FORMZ#,min#,max#)
    tri0=AddTriangle(Surf,tv0,tv1,tv2)
    tri1=AddTriangle(Surf,tv0,tv2,tv3)


    VertexColor Surf,tv0,255,255,255
    VertexColor Surf,tv1,255,255,255
    VertexColor Surf,tv2,255,255,255
    VertexColor Surf,tv3,255,255,255
  
    BrushTexture thisBrush, ti
    BrushFX thisBrush, 3
    PaintSurface Surf,thisBrush

  Next
  Next
  FreeBrush thisBrush
  int_Add2zList(Handle(this3DForm),parent)
  
  Return Handle(this3DForm)
End Function

Function F3D_SetAlphaMask(this3DFormHandle,alphamask)
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
  this3DForm\alphamask=alphamask
  Return
End Function


; F3D_SetState
; this3DFormHandle - 3d form handle to be updated
; state - set to 0, 1, 2 or 3 for desired state
Function F3D_SetState(this3DFormHandle, state)
  Local thisSurf, thisTexture, brushnum
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle) 
  brushnum=0

  this3DForm\state=state
  
  If this3DForm\state=0 
    If this3DForm\texture1>0
      texbuf=TextureBuffer(this3DForm\texture1)
    Else
      Return
    EndIf     
  EndIf
  If this3DForm\state=1 
    If this3DForm\texture2>0
      texbuf=TextureBuffer(this3DForm\texture2)
    Else
      Return
    EndIf     
  EndIf
  If this3DForm\state=2 
    If this3DForm\texture3>0
      texbuf=TextureBuffer(this3DForm\texture3)
    Else
      Return
    EndIf     
  EndIf
  If this3DForm\state=3
    If this3DForm\texture4>0
      texbuf=TextureBuffer(this3DForm\texture4)
    Else
      Return
    EndIf     
  EndIf 


  thisBrush = CreateBrush()
  For j=0 To this3DForm\vsize-1
  For i=0 To this3DForm\hsize-1
  
      brushnum=brushnum+4
      thisSurf=PeekInt(this3DForm\surfbank,brushnum-4)

      thisTexture=PeekInt(this3DForm\texbank,brushnum-4)
      tibuf=TextureBuffer(thisTexture)
    
      If this3DForm\alphamask=True
        SetBuffer tibuf
          ClsColor 0,0,0
          Cls 
        SetBuffer BackBuffer()

        int_ManualAlpha(tibuf,this3DForm\tsize)
      EndIf
      CopyRect 0,0,this3DForm\width,this3DForm\height,0-(i*this3DForm\tsize),0-(j*this3DForm\tsize),texbuf,tibuf
  
      BrushTexture thisBrush, thisTexture
      BrushFX thisBrush, 3
      PaintSurface thisSurf,thisBrush

  Next
  Next
  FreeBrush thisBrush
  Return
End Function

; F3D_GetState
; this3DFormHandle - 3d form handle
Function F3D_GetState(this3DFormHandle)
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
  Return this3DForm\state
End Function


; F3D_Position
; this3DFormHandle - 3d form handle
; formXp - screen X position to be updated
; formYp - screen Y position to be updated
Function F3D_Position(this3DFormHandle,formXp,formYp)
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
  
  CameraPick(camera,formXp,formYp)
  x1#=PickedX#()
  y1#=PickedY#()

  MoveEntity this3DForm\mesh,x1#-this3DForm\x#,y1#-this3DForm\y#,0.0
  
  this3DForm\screenx=formXp
  this3DForm\screeny=formYp 
  this3DForm\x#=x1#
  this3DForm\y#=y1#
  
End Function

; F3D_FocusParent 
; thisParentHandle - Valid parent 3d form handle
Function F3D_FocusParent(thisParentHandle)
  Local thisChild.formInfo, thisChildHandle 
  Local thisParent.formInfo
  thisParent.formInfo = Object.formInfo(thisParentHandle) 

    ;delete parent from zList to be put into zPList
    For zPList.zParentOrder = Each zParentOrder
      If zPList\thisParent = thisParent
        Insert zPList After Last zParentOrder
        Insert zPList\thisParent After Last formInfo
        ; set children zorder
        For childlist=1 To zPList\thisParent\numchildren
          thisChildHandle=PeekInt(zPList\thisParent\childbank,childlist*4-4)
          thisChild.formInfo = Object.formInfo(thisChildHandle)
          Insert thisChild After Last formInfo
        Next      
        Exit
      EndIf 
    Next

    int_CleanZOrder()
    int_UpdateMouseZOrder()
End Function

;F3D_UpdateGui
; // Update gui states
Function F3D_UpdateGui()
  Local this3DForm.formInfo
  Local legalParent.formInfo
  Local mousepointer.formInfo
  Local mousepointer2.formInfo
  mousepointer.formInfo = Object.formInfo(mousepointerHandle) 
  mousepointer2.formInfo = Object.formInfo(mousepointer2Handle) 
  ; update mouse
  If MouseX()<>int_gOldMouseX Or MouseY()<>int_gOldMouseY
    If mousepointer<>Null
      F3D_Position(Handle(mousepointer),MouseX(),MouseY()-2)
      mousepointer\zorder=int_gCURRENTZORDERPOS-2
      If mousepointer2\shadow=True 
        F3D_Position(Handle(mousepointer2),MouseX()+2,MouseY())
        mousepointer2\zorder=int_gCURRENTZORDERPOS-1
      EndIf
      int_UpdateMouseZOrder()     
    EndIf
    int_gOldMouseX=MouseX()
    int_gOldMouseY=MouseY()
  EndIf

  gui_ops=False
  isParentInFront=False
  isChild=False
  legalParent=Null
  this3DForm.formInfo = Last formInfo
  While (this3DForm.formInfo<>Null)
  
    ; check if parent group selected to be switched to front
    If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
      
        If this3DForm\parent<>Null 
          If legalParent=Null 
            legalParent=this3DForm\parent
            isChild=True
          EndIf
        EndIf
        If legalParent=Null
          If this3DForm\numchildren>0 And isChild=False
            legalParent=this3DForm
          EndIf
        EndIf
        If (legalParent=this3DForm\parent And isChild=True) Or (legalParent=this3DForm And isChild=False)
          gui_ops=True
          isParentInFront=True
          If MouseDown(1)=True 
            F3D_FocusParent(Handle(legalParent))
          EndIf
        Else
          gui_ops=False
        EndIf   
    EndIf
    If isParentInFront=False And this3DForm\numchildren=0 And legalParent=Null
      gui_ops=True
    EndIf
                
  If gui_ops=True ;legalParent=this3DForm\parent Or legalParent=this3DForm
    ; Panel
    If this3DForm\formtype=FT_PANEL
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If MouseDown(1)=True And this3DForm\state=0 And (this3DForm\mdooba=0 Or this3DForm\mdooba=2)
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area
        Else
          If MouseDown(1)=False
            If this3DForm\state=1
              F3D_SetState(Handle(this3DForm),0)
            EndIf
            If this3DForm\mdooba=1 ; mousedown was outside of box area
              this3DForm\mdooba=0 ; no mousedown
            EndIf
          EndIf
        EndIf
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
        If this3DForm\state>0 ; mouse outside of box = default texture
          F3D_SetState(Handle(this3DForm),0)
        EndIf
      EndIf
    EndIf 
  
    ; input 
    If this3DForm\formtype=FT_INPUT
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If MouseDown(1)=True And this3DForm\state=0 And (this3DForm\mdooba=0 Or this3DForm\mdooba=2)
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area
        Else
          If MouseDown(1)=False
            If this3DForm\state=1
              F3D_SetState(Handle(this3DForm),0)
            EndIf
            If this3DForm\mdooba=1 ; mousedown was outside of box area
              this3DForm\mdooba=0 ; no mousedown
            EndIf
          EndIf
        EndIf
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
        If this3DForm\state>0 ; mouse outside of box = default texture
          F3D_SetState(Handle(this3DForm),0)
        EndIf
      EndIf
    EndIf 
  
    ; non-highlighted push button style
    If this3DForm\formtype=FT_NHPB
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If MouseDown(1)=True And this3DForm\state=0 And (this3DForm\mdooba=0 Or this3DForm\mdooba=2)
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area
        Else
          If MouseDown(1)=False
            If this3DForm\state=1
              F3D_SetState(Handle(this3DForm),0)
            EndIf
            If this3DForm\mdooba=1 ; mousedown was outside of box area
              this3DForm\mdooba=0 ; no mousedown
            EndIf
          EndIf
        EndIf
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
        If this3DForm\state>0 ; mouse outside of box = default texture
          F3D_SetState(Handle(this3DForm),0)
        EndIf
      EndIf
    EndIf 
    ; highlighted push button style
    If this3DForm\formtype=FT_HPB
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If this3DForm\state=0
            F3D_SetState(Handle(this3DForm),2)
        EndIf
        If MouseDown(1)=True And this3DForm\state=2 And (this3DForm\mdooba=0 Or this3DForm\mdooba=2)
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area          
        Else
          If MouseDown(1)=False
            If this3DForm\state=1
              F3D_SetState(Handle(this3DForm),2)
            EndIf
            If this3DForm\mdooba=1 ; mousedown was outside of box area
              this3DForm\mdooba=0 ; no mousedown
            EndIf
          EndIf
        EndIf
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
        If this3DForm\state>0 ; mouse outside of box = default texture
          F3D_SetState(Handle(this3DForm),0)
        EndIf
      EndIf
    EndIf
    ; non-highlighted toggle true/false style
    If this3DForm\formtype=FT_NHT
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If MouseDown(1)=True And this3DForm\state=0 And this3DForm\mdooba=0
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area
        EndIf
        If MouseDown(1)=True And this3DForm\state=1 And this3DForm\mdooba=0
          F3D_SetState(Handle(this3DForm),0)
          this3DForm\mdooba=2 ; mouse inside of box area
        EndIf
        If MouseDown(1)=False
          If this3DForm\mdooba>0 ; mousedown was outside of box area
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        EndIf
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
      EndIf
    EndIf
    ; highlighted toggle true/false style
    If this3DForm\formtype=FT_HT
      If (MouseX()>this3DForm\screenx) And (MouseX()<this3DForm\screenx+this3DForm\width) And (MouseY()>this3DForm\screeny) And (MouseY()<this3DForm\screeny+this3DForm\height)
        If MouseDown(1)=True And (this3DForm\state=0 Or this3DForm\state=2) And this3DForm\mdooba=0
          F3D_SetState(Handle(this3DForm),1)
          this3DForm\mdooba=2 ; mouse inside of box area
        EndIf
        If MouseDown(1)=True And (this3DForm\state=1 Or this3DForm\state=3) And this3DForm\mdooba=0
          F3D_SetState(Handle(this3DForm),0)
          this3DForm\mdooba=2 ; mouse inside of box area
        EndIf
        If MouseDown(1)=False
          If this3DForm\mdooba>0 ; mousedown was outside of box area
            this3DForm\mdooba=0 ; no mousedown inside of box area
          EndIf
        EndIf
        If this3DForm\state=0
          F3D_SetState(Handle(this3DForm),2)
        EndIf         
        If this3DForm\state=1
          F3D_SetState(Handle(this3DForm),3)
        EndIf         
      Else
        If this3DForm\mdooba<2 ; mouse outside of box
          If MouseDown(1)=True
            this3DForm\mdooba=1 ; mousedown outside of box area
          Else
            this3DForm\mdooba=0 ; no mousedown
          EndIf
        Else
          If MouseDown(1)=False
            this3DForm\mdooba=0 ; mousedown was inside of box area
          EndIf
        EndIf
        If this3DForm\state=2
          F3D_SetState(Handle(this3DForm),0)
        EndIf
        If this3DForm\state=3
          F3D_SetState(Handle(this3DForm),1)
        EndIf
      EndIf
    EndIf
  EndIf 
    this3DForm = Before this3DForm        
  Wend
  
Return
End Function

;###################################################################
;###################################################################
; Custom Functions

; F3D_Mouse
Function F3D_Mouse(formXp,formYp,mousetex,mouseshadow=False,minpow=5,maxpow=8)
  Local mousepointer2.formInfo 

  If mouseshadow=True
    mousepointer2Handle=F3D_CreateForm(0,0,FT_NAG,mousetex)
    mousepointer2.formInfo = Object.formInfo(mousepointer2Handle)     
    mousepointer2\shadow=True
    F3D_VColor(Handle(mousepointer2),0,0,0,.5)
  EndIf
  mousepointerHandle=F3D_CreateForm(0,0,FT_NAG,mousetex)
End Function

; F3D_Panel
Function F3D_Panel(formXp,formYp,width,height,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo,qsize
  

  hcr=255
  hcg=255
  hcb=255
  mcr=196
  mcg=196
  mcb=196
  scr=64
  scg=64
  scb=64

  If width>height
    qsize=width
  Else
    qsize=height
  EndIf

  temp=CreateImage(width,height)
  tempbuf=ImageBuffer(temp)
  SetBuffer tempbuf
  ClsColor 0,0,0
  SetBuffer tempbuf
  Cls   

  Color mcr,mcg,mcb
  Rect 0, 0,  width-1, height-1, True
  
  Color hcr,hcg,hcb
  Line 0,0,0,height-1
  Line 0,0,width-1,0
  
  Color scr,scg,scb
  Line 0,height-1,width-1,height-1
  Line width-1,0,width-1,height-1
  SetBuffer BackBuffer()
      ; 
      tex1=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex1)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
      
  FreeImage temp
  
  this3DFormHandle=F3D_CreateForm(formXp,formYp,FT_PANEL,tex1,0,0,0,parent,minpow,maxpow)
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
  
  this3DForm\width=width
  this3DForm\height=height
    
  Return Handle(this3DForm)
End Function

Function F3D_Button(formXp,formYp,width,height,highlight=False,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo,qsize

  hcr=255
  hcg=255
  hcb=255
  mcr=196
  mcg=196
  mcb=196
  scr=64
  scg=64
  scb=64

  If width>height
    qsize=width
  Else
    qsize=height
  EndIf
  

  temp=CreateImage(width,height)
  tempbuf=ImageBuffer(temp)
  SetBuffer tempbuf
  ClsColor 0,0,0
  SetBuffer tempbuf
  Cls   

  Color mcr,mcg,mcb
  Rect 0, 0,  width-1, height-1, True
  
  Color hcr,hcg,hcb
  Line 0,0,0,height-1
  Line 0,0,width-1,0
  
  Color scr,scg,scb
  Line 0,height-1,width-1,height-1
  Line width-1,0,width-1,height-1
  SetBuffer BackBuffer()
      ; 
      tex1=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex1)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
  
  FreeImage temp


  ;texture 2
  temp=CreateImage(width,height)
  tempbuf=ImageBuffer(temp)
  SetBuffer tempbuf
  ClsColor 0,0,0
  SetBuffer tempbuf
  Cls   

  Color mcr,mcg,mcb
  Rect 0, 0,  width-1, height-1, True

  Color scr,scg,scb
  Line 0,0,0,height-1
  Line 0,0,width-1,0
  
  Color hcr,hcg,hcb
  Line 0,height-1,width-1,height-1
  Line width-1,0,width-1,height-1
  SetBuffer BackBuffer()
      ; 
      tex2=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex2)
      
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
    
  FreeImage temp

  If highlight=False
    this3DFormHandle=F3D_CreateForm(formXp,formYp,FT_NHPB,tex1,tex2,0,0,parent,minpow,maxpow)
    this3DForm.formInfo = Object.formInfo(this3DFormHandle)   
  Else 
    ;texture 3
    temp=CreateImage(width,height)
    tempbuf=ImageBuffer(temp)
    SetBuffer tempbuf
    ClsColor 0,0,0
    SetBuffer tempbuf
    Cls   

    Color mcr+32,mcg+32,mcb+32
    Rect 0, 0,  width-1, height-1, True

    Color hcr,hcg,hcb
    Line 0,0,0,height-1
    Line 0,0,width-1,0
  
    Color scr,scg,scb
    Line 0,height-1,width-1,height-1
    Line width-1,0,width-1,height-1
    SetBuffer BackBuffer()
      ; 
      tex3=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex3)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
    
    FreeImage temp  
  
    this3DFormHandle=F3D_CreateForm(formXp,formYp,FT_HPB,tex1,tex2,tex3,0,parent,minpow,maxpow)
    this3DForm.formInfo = Object.formInfo(this3DFormHandle)   
  EndIf 
  
  this3DForm\width=width
  this3DForm\height=height
    
  Return Handle(this3DForm)
End Function

Function F3D_Toggle(formXp,formYp,width,height,highlight=False,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo,qsize

  hcr=255
  hcg=255
  hcb=255
  mcr=196
  mcg=196
  mcb=196
  scr=64
  scg=64
  scb=64

  If width>height
    qsize=width
  Else
    qsize=height
  EndIf
  

  temp=CreateImage(width,height)
  tempbuf=ImageBuffer(temp)
  SetBuffer tempbuf
  ClsColor 0,0,0
  SetBuffer tempbuf
  Cls   

  Color mcr,mcg,mcb
  Rect 0, 0,  width-1, height-1, True
  
  Color hcr,hcg,hcb
  Line 0,0,0,height-1
  Line 0,0,width-1,0
  
  Color scr,scg,scb
  Line 0,height-1,width-1,height-1
  Line width-1,0,width-1,height-1
  SetBuffer BackBuffer()
      ; 
      tex1=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex1)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
  
  FreeImage temp


  ;texture 2
  temp=CreateImage(width,height)
  tempbuf=ImageBuffer(temp)
  SetBuffer tempbuf
  ClsColor 0,0,0
  SetBuffer tempbuf
  Cls   

  Color mcr,mcg,mcb
  Rect 0, 0,  width-1, height-1, True

  Color scr,scg,scb
  Line 0,0,0,height-1
  Line 0,0,width-1,0
  
  Color hcr,hcg,hcb
  Line 0,height-1,width-1,height-1
  Line width-1,0,width-1,height-1
  SetBuffer BackBuffer()
      ; 
      tex2=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex2)
      
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
    
  FreeImage temp

  If highlight=False
    this3DFormHandle=F3D_CreateForm(formXp,formYp,FT_NHT,tex1,tex2,0,0,parent,minpow,maxpow)
    this3DForm.formInfo = Object.formInfo(this3DFormHandle)   
  Else 
    ;texture 3
    temp=CreateImage(width,height)
    tempbuf=ImageBuffer(temp)
    SetBuffer tempbuf
    ClsColor 0,0,0
    SetBuffer tempbuf
    Cls   

    Color mcr+32,mcg+32,mcb+32
    Rect 0, 0,  width-1, height-1, True

    Color hcr,hcg,hcb
    Line 0,0,0,height-1
    Line 0,0,width-1,0
  
    Color scr,scg,scb
    Line 0,height-1,width-1,height-1
    Line width-1,0,width-1,height-1
    SetBuffer BackBuffer()
      ; 
      tex3=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex3)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
    
    FreeImage temp  

    ;texture 4
    temp=CreateImage(width,height)
    tempbuf=ImageBuffer(temp)
    SetBuffer tempbuf
    ClsColor 0,0,0
    SetBuffer tempbuf
    Cls   

    Color mcr+32,mcg+32,mcb+32
    Rect 0, 0,  width-1, height-1, True

    Color scr,scg,scb
    Line 0,0,0,height-1
    Line 0,0,width-1,0
  
    Color hcr,hcg,hcb
    Line 0,height-1,width-1,height-1
    Line width-1,0,width-1,height-1
    SetBuffer BackBuffer()
      ; 
      tex4=CreateTexture(qsize,qsize,1+2+16+32+256)
      tibuf=TextureBuffer(tex4)
    
      int_WritePixelWithAlpha(tempbuf,height,width,tibuf,qsize)
    
    FreeImage temp  
    
    this3DFormHandle=F3D_CreateForm(formXp,formYp,FT_HT,tex1,tex2,tex3,tex4,parent,minpow,maxpow)
    this3DForm.formInfo = Object.formInfo(this3DFormHandle)   
  EndIf 
  
  this3DForm\width=width
  this3DForm\height=height
    
  Return Handle(this3DForm)
End Function


Function F3D_Input(txt$,formXp,formYp,width,thisFontHandle,textrect=False,parent=0,minpow=5,maxpow=8)
  Local this3DForm.formInfo,qsize
  Local thisFont.fontInfo
  thisFont.fontInfo = Object.fontInfo( thisFontHandle )
  
  this3DFormHandle=F3D_CreateText(txt$,formXp,formYp,width)
  this3DForm.formInfo = Object.formInfo(this3DFormHandle)
  
  this3DForm\formtype=FT_INPUT  
    
  Return Handle(this3DForm)
End Function

;###################################################################
;###################################################################
; Set fps vars

Function int_ManualAlpha(texbuffer,texsize)
      ; manually alpha out texture
      LockBuffer texbuffer
        For jj=0 To texsize-1
          For ii=0 To texsize-1
            val=ReadPixelFast(ii,jj,texbuffer)

            If Str(val And $00FFFFFF)="0"
              WritePixelFast ii,jj,$00FFFFFF,texbuffer
            EndIf
          Next
        Next
      UnlockBuffer texbuffer
End Function

Function int_WritePixelWithAlpha(tempbuffer,height,width,texbuffer,texsize)
      LockBuffer tempbuffer
      LockBuffer texbuffer
        For jj=0 To texsize-1
          For ii=0 To texsize-1
              If jj<height And ii<width
                val=ReadPixelFast(ii,jj,tempbuffer)
              Else
                val=0
              EndIf
              
              If Str(val And $00FFFFFF)="0"
                WritePixelFast ii,jj,$00FFFFFF,texbuffer
              Else
                WritePixelFast ii,jj,val,texbuffer;$00FFFFFF,tibuf
              EndIf
              
          Next
        Next
      UnlockBuffer texbuffer
      UnlockBuffer tempbuffer
End Function

Function int_Add2zList(this3DFormHandle,thisParentHandle=0)
  Local thisParent.formInfo
  Local this3DForm.formInfo 
  this3DForm.formInfo = Object.formInfo(this3DFormHandle) 

  If(thisParentHandle=0)
    ; ## Set non parent ##
    zList.zOrder = New zOrder
    zList\this3DForm = this3DForm
    int_CleanZOrder()   
  Else
    ; ### Set Parent/Child Info ###
    ; Set parent info
    thisParent.formInfo = Object.formInfo(thisParentHandle) 
    this3DForm\parent=thisParent
    
    ; ### NEW PARENT ###
    ; if new parent has 0 children, must be still in zList...
    ; take it out of there and put it in the zPList...
    If thisParent\numchildren=0
      ;delete parent from zList to be put into zPList
      For zList.zOrder = Each zOrder
        If zList\this3DForm = thisParent
          Delete zList
          Exit
        EndIf 
      Next
    
      ; Place New Parent into zPList
      zPList.zParentOrder = New zParentOrder
      zPList\thisParent = thisParent
    EndIf

    ; ### NEW CHILD ###
    
    thisParent\numchildren=thisParent\numchildren+1
    this3DForm\childpos=thisParent\numchildren
    ResizeBank thisParent\childbank,thisParent\numchildren*4
    PokeInt thisParent\childbank,thisParent\numchildren*4-4,Handle(this3DForm)

    int_CleanZOrder()
  EndIf
End Function

Function int_CleanZOrder()
  Local thisChild.formInfo, thisChildHandle   
  Local zListPos
  zListPos=-2
  ; set zList position for zPList starting position
  For zList.zOrder = Each zOrder
    zListPos=zListPos-1
    zList\this3DForm\zorder=zListPos
    EntityOrder zList\this3DForm\mesh,zList\this3DForm\zorder
  Next    
  
  For zPList.zParentOrder = Each zParentOrder 
    zListPos=zListPos-1 
    zPList\thisParent\zorder=zListPos
    EntityOrder zPList\thisParent\mesh,zPList\thisParent\zorder
    zListPos=zListPos-zPList\thisParent\numchildren
    ; set children zorder
    For childlist=1 To zPList\thisParent\numchildren
      thisChildHandle=PeekInt(zPList\thisParent\childbank,childlist*4-4)
      thisChild.formInfo = Object.formInfo(thisChildHandle)
      thisChild\zorder=zPList\thisParent\zorder-thisChild\childpos
      EntityOrder thisChild\mesh,thisChild\zorder
    Next
  Next
  
  int_gCURRENTZORDERPOS=zListPos  
End Function

Function int_UpdateMouseZOrder()
  ; validate mouse hack
  Local mousepointer.formInfo
  Local mousepointer2.formInfo
  mousepointer.formInfo = Object.formInfo(mousepointerHandle) 
  mousepointer2.formInfo = Object.formInfo(mousepointer2Handle)   
      mousepointer\zorder=int_gCURRENTZORDERPOS-2
      EntityOrder mousepointer\mesh,mousepointer\zorder
      If mousepointer2\shadow=True 
        mousepointer2\zorder=int_gCURRENTZORDERPOS-1
        EntityOrder mousepointer2\mesh,mousepointer2\zorder
      EndIf
End Function


; F3D_Example1.bb


;###################################################################
;###################################################################
; Setup

Graphics3D 1024,768
SetBuffer BackBuffer()
light=CreateLight()
Global camera
camera=CreateCamera()
CameraRange camera,1,10000
PositionEntity camera,0,0,0
CameraClsColor camera,0,96,128

; Need to setup Form3D to be able to place 3d forms corretly
F3D_Setup(camera)

; Set fps vars
Global newtime#=MilliSecs()
Global oldtime#=newtime#
Global AdjTime#=(MilliSecs()/1000.0)-newtime#
Global tick=0
Global myfps=0

;; Font Handles
Global myfont1
Global myfont2

; First Create A Font to use with displaying text with
; ex: "courier new", font size of 16, 16x16 spaced in a 256x256 texture
myfont1=F3D_CreateFont("courier new",16,16,16,256)
myfont2=F3D_CreateFont("blitz",24,32,32,512)

; Create static text
Global textmesh
Global textmesh2
txt$="Form3D - A Pixel Perfect 3D GUI Library"
F3D_SetFont(myfont2)
textmesh=F3D_CreateText(txt$,52,12,StringWidth(txt$))
textmesh2=F3D_CreateText(txt$,50,10,StringWidth(txt$))
F3D_VColor(textmesh,0,0,0)
F3D_VColor(textmesh2,0,255,0)
F3D_SetFont(myfont1)

; Create FPS text
Global tmfps
txt$="FPS="
tmfps=F3D_CreateText(txt$,0,0,128)
F3D_VColor(tmfps,255,255,0)

; Create 3D Mouse to use 
Global mousetex
mousetex=CreateTexture(16,16,1+2+16+32+256)
SetBuffer TextureBuffer(mousetex)
Line 0,0,7,15
Line 0,0,15,7
Line 7,15,15,7
SetBuffer BackBuffer()
F3D_Mouse(0,0,mousetex,True)

; Create Push Button
Global autobutton1, abstate1
autobutton1=F3D_Button(250,50,128,32,True)
abstate1=F3D_CreateText("##",225,50,StringWidth("##"))
F3D_VColor(abstate1,0,255,255)

; Create Toggle
Global autotoggle1, atstate1
autotoggle1=F3D_Toggle(450,50,32,32,True)
atstate1=F3D_CreateText("##",425,50,StringWidth("##"))
F3D_VColor(atstate1,0,255,255)

; Create Input (!!! Does not work - Not fully Implemented yet!!! )
Global autoInput1, aInputstate1
autoInput1=F3D_Input("Input Test",550,50,240,myfont1,True)
aInputstate1=F3D_CreateText("##",525,50,StringWidth("##"))
F3D_VColor(aInputstate1,0,255,255)

; Create a Parent panel with a child text
Global autopanel1, apstate1, static1
autopanel1=F3D_Panel(250,200,200,200)
apstate1=F3D_CreateText("##",225,200,StringWidth("##"))
F3D_VColor(apstate1,0,255,255)
static1=F3D_CreateText("Testing Parent 1",260,210,StringWidth("Testing Parent 1"),0,autopanel1)
F3D_VColor(static1,32,32,32)

; Create a Parent panel with a child text
Global autopanel2, apstate2, static2
autopanel2=F3D_Panel(350,300,200,200)
apstate2=F3D_CreateText("##",225,300,StringWidth("##"))
F3D_VColor(autopanel2,200,200,255)
F3D_VColor(apstate2,200,200,255)
static2=F3D_CreateText("Testing Parent 2",360,310,StringWidth("Testing Parent 2"),0,autopanel2)
F3D_VColor(static2,32,32,32)

;###################################################################
;###################################################################
; MAIN loop

While Not KeyHit(1)

  UpdateWorld
  RenderWorld
  F3D_UpdateGui()
  
  ; gui interaction
  F3D_UpdateText(abstate1,F3D_GetState(autobutton1))
  F3D_UpdateText(atstate1,F3D_GetState(autotoggle1))
  F3D_UpdateText(aInputstate1,F3D_GetState(autoInput1))
    
  F3D_UpdateText(apstate1,F3D_GetState(autopanel1))
  F3D_UpdateText(apstate2,F3D_GetState(autopanel2))

  fps()
  F3D_UpdateText(tmfps,"FPS="+myfps)

  Flip False
Wend
End 

;###################################################################
;###################################################################
; Functions

Function fps()
  AdjTime# = (MilliSecs()-newtime#)/1000.0
  newtime# = MilliSecs()
  If (newtime#-oldtime#)>1000
    oldtime=newtime
    myfps=tick
    tick=0
  EndIf
  tick=tick+1
End Function
