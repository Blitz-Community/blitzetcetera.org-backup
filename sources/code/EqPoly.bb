;Small demo, press + and -
Graphics3D 640, 480, 16, 1
SetBuffer BackBuffer( )

camera = CreateCamera( )
PositionEntity camera, 0, 15, 0

Local Sides = 3
poly = Create_Polygon( sides, 10, 1 )
PointEntity camera, poly

While Not KeyHit( 1 )
     
     If KeyHit( 12 )
          sides = sides - 1
          If poly
               FreeEntity poly
          EndIf
          poly = Create_Polygon( sides, 10, 1 )
     EndIf
     
     If KeyHit( 13 )
          sides = sides + 1
          If poly
               FreeEntity poly
          EndIf
          poly = Create_Polygon( sides, 10, 1 )
     EndIf
     
     UpdateWorld
     RenderWorld
     Flip
     Cls
     
Wend



;Create Polygon Function
;Written by Michael Reitzenstein (huntersd@iprimus.com.au)
;Creates an equilateral polygon of 'radius' distance, with segment width width.

Function Create_Polygon( Sides, Distance, Width )
     
     Local Base_Mesh = CreateMesh( )
     EntityFX Base_Mesh, 16
     Local Base_Surface = CreateSurface( Base_Mesh )
     
     Local Creation_Pivot = CreatePivot( )
     
     For Count = 1 To Sides
          
          PositionEntity Creation_Pivot, 0, 0, 0
          RotateEntity Creation_Pivot, 0, ( 360.0 / Float( Sides ) ) * Count, 0
          
          TurnEntity Creation_Pivot, 0, -( 360 / Float#( Sides ) ) / 2.0, 0
          MoveEntity Creation_Pivot, 0, 0, Distance
          
          V1 = AddVertex( Base_Surface, EntityX( Creation_Pivot ), EntityY( Creation_Pivot ), EntityZ( Creation_Pivot ) )
          
          MoveEntity Creation_Pivot, 0, 0, Width
          
          V2 = AddVertex( Base_Surface, EntityX( Creation_Pivot ), EntityY( Creation_Pivot ), EntityZ( Creation_Pivot ) )
          
          PositionEntity Creation_Pivot, 0, 0, 0
          RotateEntity Creation_Pivot, 0, ( 360.0 / Float( Sides ) ) * Count, 0
          
          TurnEntity Creation_Pivot, 0, ( 360.0 / Float( Sides ) ) / 2.0, 0
          MoveEntity Creation_Pivot, 0, 0, Distance
          
          V3 = AddVertex( Base_Surface, EntityX( Creation_Pivot ), EntityY( Creation_Pivot ), EntityZ( Creation_Pivot ) )
          
          MoveEntity Creation_Pivot, 0, 0, Width
          
          V4 = AddVertex( Base_Surface, EntityX( Creation_Pivot ), EntityY( Creation_Pivot ), EntityZ( Creation_Pivot ) )
          
          AddTriangle Base_Surface, V4, V3, V2
          AddTriangle Base_Surface, V1, V2, V3
          
     Next
     
     Return Base_Mesh
     
End Function
