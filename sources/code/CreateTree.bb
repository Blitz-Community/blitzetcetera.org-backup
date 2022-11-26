; BB3D Tree, V1.1 by Martin A. Parrott (parrottm@hotmail.com)
; Oct. 28, 2001 - 1. Added ability to texture the tree components
;                 2. Added another leaf type, type 6
;
; This is a piece of code to create a 3D tree with a few predefined
; leaf types. Feel free to add your own leaf routines to the select/case area
; This code is based on code written in Lscript for Lightwave 3D by Newtek
; which was in turn based on an earlier BML script for Lightwave 3D
; and that was based on code ported from a POVRay macro, whew!
; Note: This code is not warranted in any way so use at your own risk
;       And test it For usability in your own program
;
; This code is free to use, but if you modify it, please send the
; changes to the above email address so I can continue to release
; updates so others can benefit.
;
; This routine can make many different types of trees, bushes, etc.
; Just change the variables! There are a lot to allow flexibility, so go for it!
;
; Parameters are:
; Wiggle_Flag 1="on", 2="off". Specifies whether to 'wiggle' the branches as they are rotated.
; Branches_On_End_Flag 1="on", 2="off". specifies growth on just the ends or evenly spaced along branch.

; LEAF TYPE is: 0=Realistic,  1=Sphere Blobs
;               2=Strange,    3=Torus
;               4=Triangles,  5=Ultra-Triangles
;               6=Texture mapped (Maps a graphics on a flattened cube, supply your own graphic)
;              -1=Default, just a sphere!

; Leaf_Mesh sets the maximum # of trianges for type 4/5 leaves

; Number_Of_Large_Branches NUMBER OF Large/Medium/Small BRANCHES is in relation to each 'parent' branch.
; Number_Of_Medium_Branches
; Number_Of_Small_Branches

; Large_Branch_Minimum_Angle# Large/Medium/Small BRANCH MIN/MAX TILT ANGLE sets limits of random branch tilt.
; Medium_Branch_Minimum_Angle#
; Small_Branch_Minimum_Angle#
; Large_Branch_Maximum_Angle#
; Medium_Branch_Maximum_Angle#
; Small_Branch_Maximum_Angle#

; Tree_Trunk_Size# sets the height of the tree trunk.

; Large_Branch_Size_Min# Large/Medium/Small BRANCH MIN/MAX SIZE sets length limits of the branches.
; Medium_Branch_Size_Min#
; Small_Branch_Size_Min#
; Large_Branch_Size_Max#
; Medium_Branch_Size_Max#
; Small_Branch_Size_Max#

; Trunk_seg This sets the number of faces in each trunk and branch cylinder
; LBranch_seg
; MBranch_seg
; SBranch_seg

; Trunk_Big_Dia# These set the bottom diameter of the trunk/branches
; Trunk_Small_Dia# These set the top diameter of the trunk/branches
; LBranch_Big_Dia#
; LBranch_Small_Dia#
; MBranch_Big_Dia#
; MBranch_Small_Dia#
; SBranch_Big_Dia#
; SBranch_Small_Dia#

; Texture section
; Uncomment the following and replace the image files with your own if you want
; textures on your trunk, branches and/or leaves
; Note: If you don't want these to be global variables, move them inside the
;       function below
;Global Trunk_texture$="trunk.bmp"
;Global LargeBranch_texture$="largebranch.bmp"
;Global MediumBranch_texture$="medbranch.bmp"
;Global SmallBranch_texture$="smallbranch.bmp"
;Global Leaf_texture$="leaf.bmp"

Graphics3D 800,600
PositionEntity CreateCamera(),0,8,-15
RotateEntity CreateLight(),45,0,45
createtree
RenderWorld
WaitKey



Function CreateTree(Wiggle_Flag=2,Branches_On_End_Flag=2,Leaf_Type=-1,Leaf_Mesh=100,Number_Of_Large_Branches=5,Number_Of_Medium_Branches=3,Number_Of_Small_Branches=7,Large_Branch_Minimum_Angle#=20,Medium_Branch_Minimum_Angle#=20,Small_Branch_Minimum_Angle#=20,Large_Branch_Maximum_Angle#=40,Medium_Branch_Maximum_Angle#=40,Small_Branch_Maximum_Angle#=40,Tree_Trunk_Size#=9,Large_Branch_Size_Min#=2,Medium_Branch_Size_Min#=2,Small_Branch_Size_Min#=2,Large_Branch_Size_Max#=4,Medium_Branch_Size_Max#=3,Small_Branch_Size_Max#=4,Trunk_seg=15,LBranch_seg=4,MBranch_seg=4,SBranch_seg=4,Trunk_Big_Dia#=1,Trunk_Small_Dia#=.8,LBranch_Big_Dia#=.6,LBranch_Small_Dia#=.4,MBranch_Big_Dia#=.3,MBranch_Small_Dia#=.2,SBranch_Big_Dia#=.1,SBranch_Small_Dia#=.05)

  ; System calculated variables. Takes from defaults above or passed function parameters

  L_Bmin#=Large_Branch_Minimum_Angle#
  L_Bmax#=Large_Branch_Maximum_Angle# - Large_Branch_Minimum_Angle#

  M_Bmin#=Medium_Branch_Minimum_Angle#
  M_Bmax#=Medium_Branch_Maximum_Angle# - Medium_Branch_Minimum_Angle#

  S_Bmin#=Small_Branch_Minimum_Angle#
  S_Bmax#=Small_Branch_Maximum_Angle# - Small_Branch_Minimum_Angle#

  Large_Branch_Size_Range#=Large_Branch_Size_Max# - Large_Branch_Size_Min#

  Medium_Branch_Size_Range#=Medium_Branch_Size_Max# - Medium_Branch_Size_Min#

  Small_Branch_Size_Range#=Small_Branch_Size_Max# - Small_Branch_Size_Min#

  If Trunk_texture$<>"" ; Get our Trunk texture if defined
    trunktex=LoadTexture(Trunk_texture$)
  Else
    trunktex=0
  EndIf

  If LargeBranch_texture$<>"" ; Get our Large branch texture if defined
    lbranchtex=LoadTexture(LargeBranch_texture$)
  Else
    lbranchtex=0
  EndIf

  If MediumBranch_texture$<>"" ; Get our Medium branch texture if defined
    mbranchtex=LoadTexture(MediumBranch_texture$)
  Else
    mbranchtex=0
  EndIf

  If SmallBranch_texture$<>"" ; Get our Small branch texture if defined
    sbranchtex=LoadTexture(SmallBranch_texture$)
  Else
    sbranchtex=0
  EndIf

  If Leaf_texture$<>"" ; Get our Leaf texture if defined
    leaftex=LoadTexture(Leaf_texture$,54)
  Else
    leaftex=0
  EndIf

  ; Make the Tree Trunk
  ; Note: the Make_Branch function puts a sphere on the end of the branch to 'smooth'
  ; up the construction. If you want to save polys, change all calls of Make_Branch
  ; below to Make_Branch_Nosphere

  Trunk=Make_Branch(Tree_Trunk_Size#,Trunk_Big_Dia#,Trunk_Small_Dia#,Trunk_seg,trunktex)

  A=0
  While A < Number_Of_Large_Branches
  
    ; Make one large branch.
  
    This_Large_Branch_Size#=( Rnd(0,1) * Large_Branch_Size_Range# ) + Large_Branch_Size_Min#
    LBranch=Make_Branch(This_Large_Branch_Size#,LBranch_Big_Dia#,LBranch_Small_Dia#,LBranch_seg,lbranchtex)

    If LargeBranch_texture$<>"" ; If the texture is defined, apply it
      EntityTexture LBranch,lbranchtex
    EndIf

    ; Loop To put medium branches on that large branch.
  
    B=0
    While B < Number_Of_Medium_Branches
  
      This_Medium_Branch_Size#=( Rnd(0,1) * Medium_Branch_Size_Range# ) + Medium_Branch_Size_Min#
    
      MBranch=Make_Branch(This_Medium_Branch_Size#,MBranch_Big_Dia#,MBranch_Small_Dia#,MBranch_seg,mbranchtex)
      EntityParent MBranch,LBranch

      If MediumBranch_texture$<>"" ; If the texture is defined, apply it
        EntityTexture MBranch,mbranchtex
      EndIf

      ; Loop To put small branches on that medium branch.
    
      C=0
      While C < Number_Of_Small_Branches
    
        This_Small_Branch_Size#=( Rnd(0,1) * Small_Branch_Size_Range# ) + Small_Branch_Size_Min#
      
        SBranch=Make_Branch_Nosphere(This_Small_Branch_Size#,SBranch_Big_Dia#,SBranch_Small_Dia#,SBranch_seg,sbranchtex)
        EntityParent SBranch,MBranch
      
        If SmallBranch_texture$<>"" ; If the texture is defined, apply it
          EntityTexture SBranch,sbranchtex
        EndIf

        ; The leaf at the End of the small branch.
    
        Temp_leaf=Make_Leaf(Leaf_Type,Leaf_Mesh,leaftex)
        PositionEntity Temp_leaf,0,This_Small_Branch_Size#,0

        ; First, spin the vertical branch To a random angle.
        ; The branch doesn't really change - this actually
        ; just spins the leaf around!
      
        SpinAngle=(Rnd(0,1)*360)
        EntityParent Temp_leaf,SBranch
        TurnEntity SBranch,0,spinangle,0,True
      
        If Leaf_texture$<>"" ; If the texture is defined, apply it
          EntityTexture Temp_leaf,leaftex
        EndIf

        ; Now, tilt it over a little.
        BranchAngle=(Rnd(0,1)*S_Bmax)+S_Bmin
        TurnEntity SBranch,0,0,BranchAngle,True

        ; Rotate it into place, with a little random wiggle.
        Wiggle=(Rnd(0,1)*20) - 10
        If Wiggle_Flag=2
          Wiggle=0
        EndIf
                
        C2=( 360 / Number_Of_Small_Branches ) * C
        C2=C2 + Wiggle
        TurnEntity SBranch,0,C2,0,True
      
        ; Move it up To the top of the Medium_Branch.
        ; If flag is "off", Then move To the Next spot on branch.
      
        TEMP_HEIGHT#=This_Medium_Branch_Size#;
    
        If ( Branches_On_End_Flag=2 )
           TEMP_HEIGHT#=This_Medium_Branch_Size# - ((This_Medium_Branch_Size# / Number_Of_Small_Branches) * C)
        EndIf
      
        PositionEntity SBranch,0,TEMP_HEIGHT#,0

        C=C + 1
      Wend

      BranchAngle=(Rnd(0,1)*M_Bmax)+M_Bmin
      TurnEntity MBranch,0,0,BranchAngle,True

      Wiggle=(Rnd(0,1)*20) - 10
      If ( Wiggle_Flag=2 )
        Wiggle=0
      EndIf
      B2=( 360 / Number_Of_Medium_Branches ) * B
      B2=B2 + Wiggle
      TurnEntity MBranch,0,B2,0,True
    
      ; Move the Medium_Branch up To the top of the Large_Branch.
      ; If flag is "off", Then move To the Next spot on branch.
              
      TEMP_HEIGHT#=This_Large_Branch_Size#
      If ( Branches_On_End_Flag=2 )
        TEMP_HEIGHT#=This_Large_Branch_Size# - ((This_Large_Branch_Size# / Number_Of_Medium_Branches)*B)
      EndIf
    
      PositionEntity MBranch,0,TEMP_HEIGHT#,0
      B=B + 1
    Wend

    BranchAngle=(Rnd(0,1)*L_Bmax)+L_Bmin
    TurnEntity LBranch,0,0,BranchAngle,True

    Wiggle=(Rnd(0,1)*20) - 10
    If Wiggle_Flag=2
      Wiggle=0
    EndIf

    A2=( 360 / Number_Of_Large_Branches ) * A
    A2=A2 + Wiggle
    TurnEntity LBranch,0,A2,0,True
    
    ; Move the Large_Branch up To the top of the Tree_Trunk.
    ; If flag is "off", Then move To the Next spot on trunk.
    ; These are spaced differently than the other branches - they
    ; start about 3/4 of the way up the trunk.
            
    TEMP_HEIGHT#=Tree_Trunk_Size#
    If Branches_On_End_Flag=2
      TEMP_HEIGHT#=Tree_Trunk_Size# - ((Tree_Trunk_Size# / Number_Of_Large_Branches) * A/4);
    EndIf
  
    PositionEntity LBranch,0,TEMP_HEIGHT#,0
    A=A + 1

    EntityParent LBranch,Trunk

  Wend

  Return Trunk

End Function

Function Make_Leaf(Leaf_Type,Leaf_Mesh,texture)

  Select Leaf_Type

    ; Let's do the quasi-REALISTIC leaf.
    Case 0

      piv0=CreatePivot()
      piv1=CreatePivot(piv0)

      leaf1=CreateSphere(8,piv1)
      EntityColor leaf1,0,150,0
      PositionEntity leaf1,0,-2.5,-5
      TurnEntity leaf1,-30,0,0,True
      ScaleEntity leaf1,2.4,.6,6

      piv2=CopyEntity(piv1,piv0)
      TurnEntity piv2,0,120,0

      piv3=CopyEntity(piv2,piv0)
      TurnEntity piv2,0,120,0

      ScaleEntity piv0,.1,.1,.1 ; Change this scale to size the overall cluster
      Return piv0

    ; Now create the actual Sphere Blob leaf.
    Case 1

      ball=CreateSphere(8)
      ScaleEntity ball,2,2,2 ; Change this to size the 'blobs' up and down
      EntityAlpha ball,.1
      EntityColor ball,0,150,0

      leafmesh=CreateMesh(ball)
      leafsurf=CreateSurface(leafmesh)

      A=1

      While A<=Leaf_Mesh

        ; Calculate random location For First point.
        X1#=( Rnd(0,1) * 2 ) - 1
        Y1#=( Rnd(0,1) * 2 ) - 1
        Z1#=( Rnd(0,1) * 2 ) - 1

        pnt0=AddVertex(leafsurf,x1#,y1#,z1#)
        pnt1=AddVertex(leafsurf,0,0,0)
        pnt2=AddVertex(leafsurf,0,0,.1)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        pnt1=AddVertex(leafsurf,0,0,0)
        pnt2=AddVertex(leafsurf,.1,0,0)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        A=A + 1
      
      Wend
      EntityColor leafmesh,0,150,0

      Return ball

    ; Now create the actual STRANGE leaf. Change this To anything!
    Case 2

      box=CreateCube()
      EntityColor box,0,150,0
      ScaleEntity box,2,2,2
      sph=CreateSphere(8,box)
      EntityColor sph,150,0,0
      ScaleEntity sph,1.2,1.2,1.2
      ScaleEntity box,.3,.3,.3
      Return box

    ; Now create the actual TORUS leaf.
    Case 3

      torus=createtorus(5,12,0,0,0,0,.25,1)
      EntityColor torus,150,0,0
      torcyl1=CreateCylinder(3,False,torus)
      EntityColor torcyl1,0,150,0
      ScaleEntity torcyl1,.1,1,.1
      TurnEntity torcyl1,90,0,0
      torcyl2=CreateCylinder(3,False,torus)
      EntityColor torcyl2,0,150,0
      ScaleEntity torcyl2,.1,1,.1
      TurnEntity torcyl2,0,0,90
      Return torus

    ; Create the Tri leaf Object - with lots of little triangles!!!
    Case 4

      leafmesh=CreateMesh()
      leafsurf=CreateSurface(leafmesh)
      A=1

      While A<=Leaf_Mesh
        ; Calculate random location For First point.
        X1#=( Rnd(0,1) * 2 ) - 1
        Y1#=( Rnd(0,1) * 2 ) - 1
        Z1#=( Rnd(0,1) * 2 ) - 1

        ;Move a little way from *First* point.
        X2#=X1# + ( Rnd(0,1) * 0.6 ) - 0.3
        Y2#=Y1# + ( Rnd(0,1) * 0.6 ) - 0.3
        Z2#=Z1# + ( Rnd(0,1) * 0.6 ) - 0.3

        ; Move a little way from *First* point.
        X3#=X1# + ( Rnd(0,1) * 0.6 ) - 0.3
        Y3#=Y1# + ( Rnd(0,1) * 0.6 ) - 0.3
        Z3#=Z1# + ( Rnd(0,1) * 0.6 ) - 0.3

        pnt0=AddVertex(leafsurf,X1#,Y1#,Z1#)
        pnt1=AddVertex(leafsurf,X2#,Y2#,Z2#)
        pnt2=AddVertex(leafsurf,X3#,Y3#,Z3#)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        A=A + 1
      
      Wend
      EntityColor leafmesh,0,150,0
      Return leafmesh

    ; Create the ULTRA-Tri leaf Object - with lots of little triangles,
    ; And Each "leaf" has a connector "branch" back To <0,0,0>.
    Case 5

      leafmesh=CreateMesh(parent)
      leafsurf=CreateSurface(leafmesh)

      A=1

      While A<=Leaf_Mesh

        ; Calculate random location For First point.
        X1=( Rnd(0,1) * 2 ) - 1
        Y1=( Rnd(0,1) * 2 ) - 1
        Z1=( Rnd(0,1) * 2 ) - 1

        ; Move a little way from *First* point.
        X2=X1 + ( Rnd(0,1) * 0.6 ) - 0.3
        Y2=Y1 + ( Rnd(0,1) * 0.6 ) - 0.3
        Z2=Z1 + ( Rnd(0,1) * 0.6 ) - 0.3

        ; Move a little way from *First* point.
        X3=X1 + ( Rnd(0,1) * 0.6 ) - 0.3
        Y3=Y1 + ( Rnd(0,1) * 0.6 ) - 0.3
        Z3=Z1 + ( Rnd(0,1) * 0.6 ) - 0.3

        pnt0=AddVertex(leafsurf,x1#,y1#,z1#)
        pnt1=AddVertex(leafsurf,x2#,y2#,z2#)
        pnt2=AddVertex(leafsurf,x3#,y3#,z3#)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        pnt1=AddVertex(leafsurf,0,0,0)
        pnt2=AddVertex(leafsurf,0,0,.1)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        pnt1=AddVertex(leafsurf,0,0,0)
        pnt2=AddVertex(leafsurf,.1,0,0)

        AddTriangle(leafsurf,pnt0,pnt1,pnt2)

        A=A + 1
      
      Wend
      EntityColor leafmesh,0,150,0
      Return leafmesh

    ; Map a user specified graphic on a flattened cube
    Case 6

      cube=CreateCube() ; Alternate to 'fast' leaf below. We map an image of a leaf on a flattened cube
      ScaleEntity cube,1,1,.001 ; Flatten our cube
      tex=LoadTexture("leaf.bmp",54) ; Change the image name to one of your own
      EntityTexture cube,tex
      Return cube

    ; Use FAST leaf as the Default.
    Default

      ball=CreateSphere(4)
      ScaleEntity ball,.3,.3,.3
      EntityColor ball,0,150,0
      Return ball

  End Select

End Function

Function Make_Branch(branch_size#,Big_Dia#,Small_Dia#,branch_seg,texture) ; Now create some branches.

  branch=createcylindertaper(branch_seg,0,0,0,0,0,Big_Dia#,Small_Dia#,branch_size#)
  ball=CreateSphere(branch_seg/2,branch)
  ScaleEntity ball,Small_Dia#,Small_Dia#,Small_Dia#
  PositionEntity ball,0,branch_size,0

  If texture>0 ; If we have a texture, put it on
    EntityTexture branch,texture
    EntityTexture ball,texture
  Else ; If we don't have a texture, color it
    EntityColor branch,90,45,0 ; Give us a dark brown color, change to whatever color your want!
    EntityColor ball,90,45,0 ; Give us a dark brown color, change to whatever color your want!
  EndIf

  Return branch

End Function

Function Make_Branch_Nosphere(branch_size#,Big_Dia#,Small_Dia#,SBranch_seg,texture)

  branch=createcylindertaper(SBranch_seg,0,0,0,0,0,Big_Dia#,Small_Dia#,branch_size#)

  If texture>0 ; If we have a texture, put it on
    EntityTexture branch,texture
  Else ; If we don't have a texture, color it
    EntityColor branch,90,45,0 ; Give us a dark brown color, change to whatever color your want!
  EndIf

  Return branch

End Function

Type f_torus
  Field x#
  Field y#
  Field z#
  Field v#
End Type

Function CreateTorus(seg=8,outerseg=16,parent=0,xloc#=0,yloc#=0,zloc#=0,rad1#=1,rad2#=3)
  ; seg# defines the number of vertices in the torus cross-section
  ; outerseg# defines the number of 'chambers' our torus will have about its circumference
  ; parent is the parent enitity handle
  ; xloc# is the final x axis location for the torus
  ; yloc# is the final y axis location for the torus
  ; zloc# is the final z axis location for the torus
  ; rad1# is the radius of the cross-section
  ; rad2# is the radius of the circumference

  If seg<3 Then seg=8 ; make sure the number of segments is set to something sane
  If seg>64 Then seg=64 ; change this if you need more segments
  If outerseg<3 Then outerseg=8 ; make sure the number of segments is set to something sane
  If outerseg>120 Then outerseg=120 ; change this if you need more segments

  torusmesh=CreateMesh(parent)
  torussurf=CreateSurface(torusmesh)
  
  angle#=0 ; Set our initial starting angle
  inc#=Float 360 / Float seg ; Setup increment for setting up vertices around our torus cross-section

  ; Do vertices
  For doverts = 0 To seg
    angle#=inc#*doverts
    verts.f_torus = New f_torus
    verts\x#=rad1#*Cos(angle#)
    verts\y#=rad1#*Sin(angle#)
    verts\z#=0
    verts\v#=angle#/360
    AddVertex (torussurf,verts\x#+rad2#,verts\y#,verts\z#,0,verts\v#)
  Next

  outside_inc#=Float 360 / Float outerseg ; Setup increment for setting up # of seg#ments that make up our torus sweep
  rotinc#=outside_inc#

  For rotseg= 1 To outerseg ; Rotate our initial verts around the Y axis
    For verts.f_torus = Each f_torus
      rx#=Cos(rotinc#)*(verts\x#+rad2#)+Sin(rotinc#)*verts\z#
      rz#=-Sin(rotinc#)*(verts\x#+rad2#)+Cos(rotinc#)*verts\z#
      u#=rotinc#/360
      AddVertex (torussurf,rx#,verts\y#,rz#,u#,verts\v#)
    Next
    rotinc#=Float(outside_inc#*(rotseg+1))
  Next

  ; Do sides of torus
  seginc=0
  For rottri = 1 To outerseg
    For vert= 0 To seg-1
      AddTriangle torussurf,vert+seginc,vert+seg+1+seginc,vert+seg+2+seginc
      AddTriangle torussurf,vert+seginc,vert+seg+2+seginc,vert+seginc+1
    Next
    seginc=(seg+1)*rottri
  Next

;  FlipMesh torusmesh ; Uncomment this is you want to put the camera inside the torus

  UpdateNormals torusmesh ; fix our normals

  MoveEntity torusmesh,xloc#,yloc#,zloc# ; Put the torus in place per passed X,Y,Z parameters

  For verts.f_torus = Each f_torus ; Clean up our data
    Delete verts
  Next

  Return torusmesh

End Function

Function CreateCylinderTaper(seg=8,parent=0,solid=True,xloc#=0,yloc#=0,zloc#=0,rad1#=1,rad2#=.75,height#=2)
  ; seg defines the number of segments/vertices in the cylinder cross-section
  ; parent is the parent enitity handle
  ; solid defines whether the cylinder ends are capped or open, true-they are closed, false-they are open
  ; xloc# is the final x axis location for the cylinder
  ; yloc# is the final y axis location for the cylinder
  ; zloc# is the final z axis location for the cylinder
  ; rad1# is the radius of the bottom of the cylinder
  ; rad2# is the radius of the top of the cylinder
  ; height# is the total height of the cylinder

  If seg<2 Then seg=8 ; make sure the number of segments is set to something sane
  If seg>32 Then seg=32

  cylmesh=CreateMesh()
  cylsurf=CreateSurface(cylmesh)
  
  ;Create center vertex of bottom disc
  AddVertex cylsurf,0,0,0,1,1

  angle#=0 ; Set our initial starting angle
  inc#=Float 360 / Float seg ; Setup increment for setting up vertices around our cylinder ends

  ; Do bottom end vertices
  While angle# < 360.01
    x#=rad1#*Cos(angle#)
    z#=rad1#*Sin(angle#)
    u#=angle#/360
    AddVertex (cylsurf,x#,0,z#,u#,1)
    angle#=angle#+inc#
  Wend

  ; If solid is set, then cap end. Do triangles
  If solid>0
    For vert=1 To seg
      AddTriangle cylsurf,0,vert,vert+1
    Next
  EndIf

  ;Create center vertex of top disc
  AddVertex cylsurf,0,height#,0,0,0

  angle#=0 ; reset angle

  ; Do top end
  While angle# < 360.01
    x#=rad2#*Cos(angle#)
    z#=rad2#*Sin(angle#)
    u#=angle#/360
    AddVertex (cylsurf,x#,height#,z#,u#,0)
    angle#=angle#+inc#
  Wend

  ; If solid is set, then cap end. Do triangles
  If solid>0
    For vert=seg+3 To (seg*2)+3
      AddTriangle cylsurf,seg+2,vert+1,vert
    Next
  EndIf

  ; Do sides of cylinder
  For vert=1 To seg
    AddTriangle cylsurf,vert,vert+seg+2,vert+seg+3
    AddTriangle cylsurf,vert,vert+seg+3,vert+1
  Next

  UpdateNormals cylmesh ; fix our normals
  MoveEntity cylmesh,xloc#,yloc#,zloc# ; Put the cylinder in place

  If parent > 0 ; Assign our cylinder to a parent if one is passed to us
    EntityParent cylmesh,parent
  EndIf

  Return cylmesh

End Function
