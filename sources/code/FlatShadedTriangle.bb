Function TriangleFlatShaded(Tx1 , Ty1 , Tx2 , Ty2 , Tx3 , Ty3 , Col)

     ;LIGHTNING FAST Triangle Drawer to be used on a Locked Buffer, uses WritePixelFast

     ;CONSTAND VARIABLES REQUIRED :
     
     ;     MINX = Minimum X Coordinate to Draw to.
     ;     MINY = Minimum Y Coordinate to Draw to.
     ;     MAXX = Maximum X Coordinate to Draw to.
     ;     MAXY = Maximum Y Coordinate to Draw to.
     ;  These 4 CONSTS act as a ViewPort for this function.
     
     ;  Col is the Colour in ARGB value the Triangle is.
     
     ;e.g.  TriangleFlatShaded(200 , 1 , 302 , 155 , 305 , 4 , 16777215) would
     ;draw a White Triangle at these coordinates...the coordinates do NOT need to be
     ;in a certain order, this function will sort them

     ;Variables in use as locals
     ;Tx1 , Ty1 , Tx2 , Ty2 , Tx3 , Ty3 , Col
     ;FirstTimeCalled , EdgeAmount , Top , RightPos , LeftPos , NewRightPos , NewLeftPos
     ;y , Temp , LEdgeHeight , REdgeHeight , StepY , Lx# , Ly# , LStepX# , RStepX# , Height
     ;PixL , PixR , Pixcnt
     
     ;NOTE  This Function can be used to draw Triangles for a 3D mesh, the function
     ;does NOT draw the last pixel on each scanline, and it does not draw the last scanline
     ;because of this.  This is to prevent over-drawing of triangles sharing points, whereby
     ;the same pixel would have been drawn to repeatedly.
     ;If you want to use this Function for a precise Triangle on its own or whatever, just
     ;deduct 1 from Tx1 , Tx2 and Tx3, and Add 1 to Ty1, Ty2 and Ty3, best to do this to the
     ;parameters during Function Call Time.

     If FirstTimeCalled = 0

          Local Tri[5]
          FirstTimeCalled = 1
     
     End If
     
     Tri[0] = Tx1 : Tri[1] = Ty1
     Tri[2] = Tx2 : Tri[3] = Ty2
     Tri[4] = Tx3 : Tri[5] = Ty3

       EdgeAmount = 3
  
     Top = 1

     If Tri[3] < Tri[Top] Then Top = 3
     If Tri[5] < Tri[Top] Then Top = 5

     RightPos = Top
     LeftPos = RightPos

     While EdgeAmount > 0
     
          If LEdgeHeight <= 0                                             ;(RE)Initialse LeftSegment Data
          
               NewLeftPos = LeftPos + 2
               If NewLeftPos > 5 Then NewLeftPos = 1
                                   
               LEdgeHeight = Tri[NewLeftPos] - Tri[LeftPos]
               
               If LEdgeHeight > 0
               
                    Temp = LeftPos - 1
               
                    LStepX# = Float (Tri[NewLeftPos - 1] - Tri[Temp]) / LEdgeHeight
                    
                    If Tri[LeftPos] < MINY                    ;CLIP to the MINY if it needs it
                    
                         y = MINY
                         StepY = MINY - Tri[LeftPos]
                         Lx# = Tri[Temp] + StepY * LStepX#
                         LEdgeHeight = LEdgeHeight - StepY
                         
                    Else
                    
                         y = Tri[LeftPos]
                         Lx# = Tri[Temp]
                         
                    End If
                              
               End If

               LeftPos = NewLeftPos
               
               EdgeAmount = EdgeAmount - 1
                    
          End If
          
          If REdgeHeight <= 0                         ;(RE)Initialise RightSegment Data
          
               NewRightPos = RightPos - 2
               If NewRightPos < 0 Then NewRightPos = 5
               
               REdgeHeight = Tri[NewRightPos] - Tri[RightPos]
               
               If REdgeHeight > 0
               
                    Temp = RightPos - 1
                    
                    RStepX# = Float (Tri[NewRightPos - 1] - Tri[Temp]) / REdgeHeight

                    If Tri[RightPos] < MINY                    ;Clip to the MINY if it needs it
                         
                         y = MINY
                         StepY = MINY - Tri[RightPos]
                         Rx# = Tri[Temp] + StepY * RStepX#
                         REdgeHeight = REdgeHeight - StepY
                                                  
                    Else
                    
                         y = Tri[RightPos]
                         Rx# = Tri[Temp]
                         
                    End If
                         
               End If
               
               RightPos = NewRightPos
               EdgeAmount = EdgeAmount - 1
                    
          End If
          
          If LEdgeHeight < REdgeHeight
          
               Height = LEdgeHeight - 1
               
               If y + Height > MAXY
               
                    Height = MAXY - y
                    EdgeAmount = -1
                    
               End If
               
          Else
          
               Height = REdgeHeight - 1
               
               If y + Height > MAXY
               
                    Height = MAXY - y
                    EdgeAmount = -1
                    
               End If
                    
          End If
          
          For cntr = y To y + Height          ;RASTER SCANLINE
          
               If Lx# < Rx#
               
                    PixL = Floor(Lx#)
                    PixR = Floor(Rx#) - 1
                    
               Else
               
                    PixL = Floor(Rx#)
                    PixR = Floor(Lx#) - 1
                    
               End If

               If PixL < MINX
                    
                    PixL = MINX
                         
               Else
                    
                    If PixL > MAXX Then PixL = MAXX + 1
                         
               End If
                    
               If PixR < MINX
                    
                    PixR = MINX - 1
                         
               Else
                    
                    If PixR > MAXX Then PixR = MAXX
                         
               End If
                         
               For Pixcnt = PixL To PixR
               
                    WritePixelFast Pixcnt , cntr , Col
                                   
               Next
                    
               Lx# = Lx# + LStepX#
               Rx# = Rx# + RStepX#
          
               LEdgeHeight = LEdgeHeight - 1
               REdgeHeight = REdgeHeight - 1
               
          Next                                             ;END RASTER SCANLINE
          
     Wend

End Function
