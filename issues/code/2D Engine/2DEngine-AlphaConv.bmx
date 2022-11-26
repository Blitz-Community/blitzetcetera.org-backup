images = LoadPixmapPNG("2DEngine-Images.png")
xsize = PixmapWidth(images)
ysize = PixmapHeight(images)
images_alpha = LoadPixmapPNG("2DEngine-ImagesAlpha.png")
new_images = CreatePixmap(xsize, ysize, PF_RGBA8888)
For y = 0 Until ysize
	For x = 0 Until xsize
		WritePixel new_images, x, y, (ReadPixel(images, x, y) & $FFFFFF) | ((ReadPixel(images_alpha, x, y) & $FF) Shl 24)
	Next
Next
SavePixmapPNG new_images, "2DEngine-NewImages.png", 9
DebugLog "Done!"
