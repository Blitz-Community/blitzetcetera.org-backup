Include "..\bones.bb"

PakOutputDir ""
PakInit "Data.Pak", $82F0273E, "TMP", $323B99AF
PakBulkOverWrite = True

Graphics3D 640,480
camera = CreateCamera()
light = CreateLight()
PositionEntity light,-1,-1,-1
CameraZoom camera, 2.5
PositionEntity camera,0,0,-15

	
Robot = LoadMesh(pak("robot.B3D"))
		
Pakclean()
		
While Not KeyDown(1)

TurnEntity robot,1,1,1
RenderWorld()
Text 10,10,"One B3D Model - Textures Unpacked Automatically"
Text 10,30,"Press ESC to Exit"
Flip
Wend

EndGraphics
End