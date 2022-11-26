;-------------------------------------------------------------------
;createRotationFrames() - V1.0, Oct 2002
;
;     Author:          Ghost Dancer, Aurora-Soft
;     Website:     www.aurora-soft.co.uk
;     Contact:     ghost@aurora-soft.co.uk
;
;Create anim image of rotation frames of a given image.
;Use in place of LoadImage & LoadAnimImage.
;
;Parameters:
; sourceName$     path & filename of source image
; rStep               amount of degrees to rotate each frame
; rFrames          number of frames to create per image
;
; tForm               use anti-aliasing True / False (optional, default=True)
;
; iFrames          count for LoadAnimImage (optional, default=0)
; iWidth          cellwidth for LoadAnimImage (optional)
; iHeight          cellheight for LoadAnimImage (optional)
; iFirst          first for LoadAnimImage (optional, default=0)
;
;The last 4 parameters are used if the source image has multiple frames
;(i.e. if iFrames = 0 then it uses LoadImage, if iFrames > 0 then it uses LoadAnimImage)
;If a single source frame is specified,  cellwidth & cellheight are taken from image.
Function createRotationFrames(sourceName$, rStep, rFrames, tForm=True, iFrames=0, iWidth=0, iHeight=0, iFirst=0)
     TFormFilter tForm

     If iFrames Then     
          destFrames = rFrames * iFrames
          sourceImg = LoadAnimImage(sourceName, iWidth, iHeight, iFirst, iFrames)
     Else
          iFrames = 1
          destFrames = rFrames
          sourceImg = LoadImage(sourceName)
          iWidth = ImageWidth(sourceImg)
          iHeight = ImageHeight(sourceImg)
     End If
     
     iWidthHalf = iWidth / 2
     iHeightHalf = iHeight / 2
          
     destImg = CreateImage(iWidth, iHeight, destFrames)

     For f = 0 To rFrames - 1
          tmpImg = CopyImage(sourceImg)
          MidHandle tmpImg                                   ;used so status of AutoMidHandle is not changed!
          RotateImage tmpImg, rStep * f
          For i = 0 To iFrames - 1
               SetBuffer ImageBuffer(destImg, f + (i * rFrames))
               DrawBlock tmpImg, iWidthHalf, iHeightHalf, i
          Next
          FreeImage tmpImg
     Next
     
     FreeImage sourceImg
     Return destImg
End Function
