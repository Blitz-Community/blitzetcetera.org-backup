Function CenterMesh (entity)
	FitMesh entity, -(MeshWidth (entity) / 2), -(MeshHeight (entity) / 2), -(MeshDepth (entity) / 2), MeshWidth (entity), MeshHeight (entity), MeshDepth (entity)
End Function

; Example usage (don't try to run this!)...

planeModel = LoadMesh ("mak_running.3ds")
CenterMesh (planeModel)
PositionEntity planeModel, 50, 100, 50
